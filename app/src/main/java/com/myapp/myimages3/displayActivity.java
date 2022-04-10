package com.myapp.myimages3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class displayActivity extends AppCompatActivity {
    ImageView displayImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        displayImage = findViewById(R.id.displayImage);
        Intent intent1 = getIntent();

        String urlPath = intent1.getStringExtra("url");

        Picasso.with(this)
                .load(urlPath)
                .placeholder(R.drawable.holder)
                .fit()
                .centerCrop()
                .into(displayImage);


    }
}