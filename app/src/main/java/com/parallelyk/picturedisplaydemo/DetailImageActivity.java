package com.parallelyk.picturedisplaydemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nostra13.universalimageloader.core.ImageLoader;

public class DetailImageActivity extends AppCompatActivity {

    private String mImageUrl;
    private MyImageView myImageView;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_image);
        init();
    }
    private void init(){
        myImageView = (MyImageView) findViewById(R.id.myImageView);
        mImageUrl = (String) getIntent().getExtras().get("url");
         bitmap = ImageLoader.getInstance().loadImageSync(mImageUrl);
         //bitmap = BitmapFactory.decodeFile(mImageUrl);
        myImageView.setBitmap(bitmap);
    }


}
