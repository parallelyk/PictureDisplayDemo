package com.parallelyk.picturedisplaydemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by YK on 2016/6/2.
 */
public class PictureDisplayView extends ScrollView implements View.OnTouchListener{

    private String TAG = "PictureDisplayView";
    private LinearLayout firstLayout,secondLayout;
    private Context mContext;


    private boolean loadOnce =false;
    private int mColumnWidth;
    private int mCurrentPage;
    private int mFirstHeight = 0,mSecondHeight = 0;


    private ImageLoader imageLoader;
    private DisplayImageOptions mOptions;

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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
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
            Bitmap picBitmap = imageLoader.getMemoryCache().get(mPicUrl);
            if(picBitmap ==null){
                //picBitmap = imageLoader.getDiskCache().get(mPicUrl);
                 picBitmap = imageLoader.loadImageSync(mPicUrl, mOptions);

            }
            Log.d(TAG,picBitmap.toString());
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
                findColumn(imageHeight).addView(imageView);
            }
        }
    }

    private LinearLayout findColumn(int imageHeight){
        if(mFirstHeight<=mSecondHeight){
            mFirstHeight += imageHeight;
            return firstLayout;
        }
        else {
            mSecondHeight += imageHeight;
            return secondLayout;
        }
    }
}
