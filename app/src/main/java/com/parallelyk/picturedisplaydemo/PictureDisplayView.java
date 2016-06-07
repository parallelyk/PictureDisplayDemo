package com.parallelyk.picturedisplaydemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by YK on 2016/6/2.
 */
public class PictureDisplayView extends ScrollView  implements View.OnTouchListener{

    private static final String TAG = "PictureDisplayView";

    private LinearLayout firstLayout,secondLayout;
    private Context mContext;


    private boolean loadOnce =false;
    private int mColumnWidth;
    private int mCurrentPage;
    private int mFirstHeight = 0,mSecondHeight = 0;
    private static int mScrollViewHeight;
    private static int lastScrollY;
    private static Set<LoadPicTask> taskCollection;
    private static View scrollLayout;
    private ImageLoader imageLoader;
    private DisplayImageOptions mOptions;

    private List<ImageView> imageViewList = new ArrayList<ImageView>();
    private List<ImageView> recycleList = new ArrayList<ImageView>();

     @SuppressLint("HandlerLeak")
     static Handler mHandler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            PictureDisplayView myScrollView = (PictureDisplayView) msg.obj;
            int scrollY = myScrollView.getScrollY();
            Log.d(TAG,scrollY+"++"+lastScrollY);
            if (scrollY == lastScrollY) {
                Log.d(TAG,"======");

                if (mScrollViewHeight + scrollY >= scrollLayout.getHeight()
                        && taskCollection.isEmpty() ) {
                    Log.d(TAG,"loadmore");
                    myScrollView.loadMoreImages();
                }
                myScrollView.checkRecycle();
            } else {
                lastScrollY = scrollY;
                Message message = Message.obtain();
                message.obj = myScrollView;
                // 5毫秒后再次对滚动位置进行判断
                mHandler.sendMessageDelayed(message, 5);
            }
        }
    };
    public PictureDisplayView(Context context) {

        super(context);
        init(context);
    }
    public PictureDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public PictureDisplayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        ImageLoaderConfiguration configuration=ImageLoaderConfiguration.createDefault(mContext);
        ImageLoader.getInstance().init(configuration);
        imageLoader = ImageLoader.getInstance();
        taskCollection = new HashSet<LoadPicTask>();
        setOnTouchListener(this);
        mOptions=new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        checkRecycle();

    }

    /**
     * 重复利用imageview
     */
    private void checkRecycle(){
        for(int i = 0;i<imageViewList.size();i++){
            ImageView imageView = imageViewList.get(i);
            int top = (int) imageView.getTag(R.string.top);
            int bottom = (int) imageView.getTag(R.string.buttom);
            if(bottom < getScrollY() || top >getScrollY()+mScrollViewHeight){//移出屏幕了
                imageView.setImageResource(R.mipmap.ic_launcher);
            }
            else{
                String url = (String) imageView.getTag(R.string.url);
                Bitmap bitmap = imageLoader.getMemoryCache().get(url);
                if(bitmap != null){
                    imageView.setImageBitmap(bitmap);
                }
                else {
                    LoadPicTask task = new LoadPicTask(imageView);
                    task.execute(url);
                    taskCollection.add(task);
                }


            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && !loadOnce) {
            mScrollViewHeight = getHeight();
            scrollLayout = getChildAt(0);
            firstLayout = (LinearLayout) findViewById(R.id.first_column);
            secondLayout = (LinearLayout) findViewById(R.id.second_column);
            //thirdColumn = (LinearLayout) findViewById(R.id.third_column);
            mColumnWidth = firstLayout.getWidth();
            loadOnce = true;
            loadMoreImages();
        }
    }

    private void loadMoreImages(){

        int start = mCurrentPage*10;
        int end = mCurrentPage*10+10;
        if(start<ImageUrl.imageUrls.length){
            if(end>ImageUrl.imageUrls.length){
                end = ImageUrl.imageUrls.length;
            }
            for(int i = start;i<end;i++){
                LoadPicTask task = new LoadPicTask();
                task.execute(ImageUrl.imageUrls[i]);

            }
            mCurrentPage++;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Message message = new Message();
            message.obj = this;
            mHandler.sendMessageDelayed(message, 5);
            Log.d(TAG, "send");
        }
        return false;
    }


    class LoadPicTask extends AsyncTask<String,Integer,Bitmap> {

        private String mPicUrl;
        private ImageView imageView;


        LoadPicTask(){

        }
        LoadPicTask(ImageView imageView){
            this.imageView = imageView;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            mPicUrl = params[0];
            Bitmap picBitmap = imageLoader.loadImageSync(mPicUrl, mOptions);//imageLoader.getMemoryCache().get(mPicUrl);
            if(picBitmap ==null){
                //picBitmap = imageLoader.getDiskCache().get(mPicUrl);
                 //picBitmap = imageLoader.loadImageSync(mPicUrl, mOptions);

            }
            //Log.d(TAG,picBitmap.toString());
            Log.d(TAG,"loading");
            return picBitmap;
        }

        /**
         * 如果加载成功 则添加addview到列上
         * @param bitmap
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null){
                double ratio = bitmap.getWidth() / (mColumnWidth * 1.0);
                int scaledHeight = (int) (bitmap.getHeight() / ratio);
                addImage(bitmap, mColumnWidth, scaledHeight);
            }
            taskCollection.remove(this);
        }

        private void addImage(Bitmap bitmap, int imageWidth, int imageHeight){
            if(imageView!=null){
                imageView.setImageBitmap(bitmap);
            }
            else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        imageWidth, imageHeight);
                imageView = new ImageView(getContext());
                imageView.setLayoutParams(params);
                imageView.setImageBitmap(bitmap);

                imageView.setPadding(5, 5, 5, 5);
                imageView.setTag(R.string.url,mPicUrl);
                findColumn(imageView, imageHeight).addView(imageView);
                imageViewList.add(imageView);
            }
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,"hello",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private LinearLayout findColumn(ImageView imageView,int imageHeight){
        if(mFirstHeight<=mSecondHeight){
            imageView.setTag(R.string.top,mFirstHeight);
            mFirstHeight += imageHeight;
            imageView.setTag(R.string.buttom,mFirstHeight);
            return firstLayout;
        }
        else {
            imageView.setTag(R.string.top,mSecondHeight);
            mSecondHeight += imageHeight;
            imageView.setTag(R.string.buttom,mSecondHeight);
            return secondLayout;
        }
    }
}
