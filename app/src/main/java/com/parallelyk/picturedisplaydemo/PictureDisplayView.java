package com.parallelyk.picturedisplaydemo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by YK on 2016/6/2.
 */
public class PictureDisplayView extends ScrollView{

    private ImageLoader imageLoader;

    public PictureDisplayView(Context context) {
        super(context);
    }
    public PictureDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public PictureDisplayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(){
         imageLoader = ImageLoader.getInstance();

    }


//    class LoadPicTask extends AsyncTask<String,Integer,Bitmap>{
//
//        private String mPicUrl;
//
//        @Override
//        protected Bitmap doInBackground(String... params) {
//            mPicUrl = params[0];
//            Bitmap picBitmap = imageLoader.loadImageSync(mPicUrl);
//            return picBitmap;
//        }
//    }
}
