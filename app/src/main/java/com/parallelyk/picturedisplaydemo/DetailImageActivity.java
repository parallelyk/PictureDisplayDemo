package com.parallelyk.picturedisplaydemo;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nostra13.universalimageloader.core.ImageLoader;

public class DetailImageActivity extends AppCompatActivity {

    private String mImageUrl;
    private MyImageView myImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_image);

    }
    private void init(){
        myImageView = (MyImageView) findViewById(R.id.myImageView);
        mImageUrl = (String) getIntent().getExtras().get("url");
        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(mImageUrl);
        myImageView.setBitmap(bitmap);
    }
}
