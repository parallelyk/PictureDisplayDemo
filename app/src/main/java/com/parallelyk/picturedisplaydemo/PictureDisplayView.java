package com.parallelyk.picturedisplaydemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by YK on 2016/6/2.
 */
public class PictureDisplayView extends ScrollView{

    private LinearLayout firstLayout,secondLayout;
    private Context mContext;


    private boolean loadOnce;
    private int mColumnWidth;

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

                imageLoader.loadImage(mPicUrl, mOptions, new SimpleImageLoadingListener());

            }
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

        }
    }
}
