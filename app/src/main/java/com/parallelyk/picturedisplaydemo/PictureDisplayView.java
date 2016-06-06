package com.parallelyk.picturedisplaydemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YK on 2016/6/2.
 */
public class PictureDisplayView extends ScrollView {

    private String TAG = "PictureDisplayView";
    private final static int URL = 1,BOTTOM = 2,TOP = 3;
    private LinearLayout firstLayout,secondLayout;
    private Context mContext;


    private boolean loadOnce =false;
    private int mColumnWidth;
    private int mCurrentPage;
    private int mFirstHeight = 0,mSecondHeight = 0;


    private ImageLoader imageLoader;
    private DisplayImageOptions mOptions;

    private List<ImageView> imageViewList = new ArrayList<ImageView>();
    private List<ImageView> recycleList = new ArrayList<ImageView>();
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

    private void checkRecycle(){
        for(int i = 0;i<imageViewList.size();i++){
            ImageView imageView = imageViewList.get(i);
            int top = (int) imageView.getTag(R.string.top);
            int bottom = (int) imageView.getTag(R.string.buttom);
            if(bottom > getScrollY()){

            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && !loadOnce) {
            //scrollViewHeight = getHeight();
            //scrollLayout = getChildAt(0);
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
