package com.myapp.myimages3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.myapp.myimages3.MainActivity.fvrtList;
import static com.myapp.myimages3.MainActivity.redirecActivity;

public class Dashboard extends AppCompatActivity {
    DrawerLayout drawerLayout;
    private Handler sliderHandler = new Handler();
    private ViewPager2 viewPager2;
    private ValueEventListener mDBListener, nDBListener;
    private DatabaseReference mDatabaseRef, nDatabaseRef;
    private FirebaseStorage mStorage;
    ImageView favrtImage, backImage, downloadImageView;
    int timer = 500;

    int newPosition;
    int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        drawerLayout = findViewById(R.id.drawer_layout);
        favrtImage = findViewById(R.id.fvrtImage);
        backImage = findViewById(R.id.backImage);
        downloadImageView = findViewById(R.id.downloadImageView);
        viewPager2 = findViewById(R.id.viewPagerImageSlider);

        pos = MainActivity.pos;

        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        nDatabaseRef = FirebaseDatabase.getInstance().getReference("favoriteImages");

        viewPager2.setAdapter(new SliderAdapter(MainActivity.mUploads,viewPager2,this,pos));

        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(1);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1-Math.abs(position);
                page.setScaleY(0.8f + r * 0.15f);
            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                newPosition = position;

                Picasso.with(Dashboard.this)
                        .load(MainActivity.mUploads.get(position+pos).getmImageUrl())
                        .fit()
                        .centerCrop()
                        .into(backImage);

                favoriteCheck();

                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 8000); // slide duration 3 seconds
            }
        });

        downloadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Dashboard.this, "downloading start", Toast.LENGTH_SHORT).show();
                String urlPath = MainActivity.mUploads.get(newPosition+pos).getmImageUrl();
                downloadImage(urlPath,"ali");
            }
        });

        favrtImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LikeUnlike();

            }
        });

    }

    public void AddImage(View view){
        redirecActivity(this,addImage.class);
    }

    public void clickMenu(View view){
        MainActivity.openDrawer(drawerLayout);
    }

    public void clickLogo(View view){
        MainActivity.closeDrawer(drawerLayout);
    }
    public void clickHome(View view){
        redirecActivity(this,MainActivity.class);
    }
    public void clickDashBoard(View view){
        recreate();
    }
    public void clickFavorite(View view){
        redirecActivity(this,FavoriteActivity.class);
    }
    public void clickAboutUs (View view){
        redirecActivity(this,AboutUs.class);
    }
    public void clickLogOut(View view) {
        MainActivity.logOut(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.closeDrawer(drawerLayout);
        sliderHandler.removeCallbacks(sliderRunnable);
        MainActivity.pos = 0;
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 8000);
    }

    private void LikeUnlike(){
        if (newPosition+pos < MainActivity.fvrtList.size()){
            int checkUnlike = Integer.parseInt(MainActivity.fvrtList.get(newPosition+pos).getFm());

            if (checkUnlike == 0) {
                favrtImage.setImageResource(R.drawable.favrtfill);

                fvrtModel selectedItem = MainActivity.fvrtList.get(pos + newPosition);
                final String selectedKey = selectedItem.getKey();

                HashMap<String,Object> hashMap = new HashMap();
                hashMap.put("fm","1");

                nDatabaseRef.child(selectedKey).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Dashboard.this, "Added to Favorite", Toast.LENGTH_LONG).show();
                    }
                });
            }
            else if (checkUnlike == 1){
                favrtImage.setImageResource(R.drawable.fvrt);

                fvrtModel selectedItem = MainActivity.fvrtList.get(pos + newPosition);
                final String selectedKey = selectedItem.getKey();

                HashMap<String,Object> hashMap = new HashMap();
                hashMap.put("fm","0");

                nDatabaseRef.child(selectedKey).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Toast.makeText(Dashboard.this, "Removed from Favorite", Toast.LENGTH_LONG).show();
                    }
                });

            }
            else {
                Toast.makeText(Dashboard.this, "nothing", Toast.LENGTH_LONG).show();
            }

        }
        else{
            Toast.makeText(this, "you can not L/U on repeated item", Toast.LENGTH_SHORT).show();
        }

    }

    private void favoriteCheck(){
        new CountDownTimer(timer,timer){
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                int currentPosition = newPosition+pos;
                if (currentPosition < MainActivity.fvrtList.size()){

                    int fvrtCheck = Integer.parseInt(MainActivity.fvrtList.get(currentPosition).getFm());
                    if (fvrtCheck == 1){
                        favrtImage.setImageResource(R.drawable.favrtfill);
                    }
                    else {
                        favrtImage.setImageResource(R.drawable.fvrt);
                    }
                }
                timer = 0;

            }
        }.start();
    }


    private void downloadImage(String urlPath, String pathName) {
        Uri uri = Uri.parse(urlPath);
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request =new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);
        request.setTitle("Image is downloading");
        request.setDescription("Android data is download from download manger");

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"/images/"+"/"+pathName+".jpg");
        request.setMimeType("*/*");
        downloadManager.enqueue(request);
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };




}