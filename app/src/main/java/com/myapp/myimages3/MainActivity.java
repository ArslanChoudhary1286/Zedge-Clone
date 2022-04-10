package com.myapp.myimages3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.midi.MidiDeviceService;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;

    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private FirebaseStorage mStorage;
    private ValueEventListener mDBListener, nDBListener;
    private DatabaseReference mDatabaseRef, nDatabaseRef;
    public static List<Upload> mUploads;
    public static List<fvrtModel> fvrtList;
    public static int pos = 0;

    ImageView profileImage;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    public static String ppUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        profileImage = findViewById(R.id.profileImage);


        mRecyclerView = findViewById(R.id.recycler_view);
        mProgressCircle = findViewById(R.id.progress_circle);
        mRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 3);
        mRecyclerView.setLayoutManager(layoutManager);

        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        nDatabaseRef = FirebaseDatabase.getInstance().getReference("favoriteImages");

        sharedPreferences = getSharedPreferences("pp",MODE_PRIVATE);
        editor = sharedPreferences.edit();


        mUploads = new ArrayList<>();
        fvrtList = new ArrayList<>();

        mAdapter = new ImageAdapter(MainActivity.this, mUploads);

//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                lastPosition = layoutManager.findFirstVisibleItemPosition();
//
//            }
//        });

        mRecyclerView.setAdapter(mAdapter);

        getFavoriteData();
        getData();

        mAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                pos = position;

                Intent intent1  = new Intent(MainActivity.this,Dashboard.class);
                startActivity(intent1);
            }

            @Override
            public void onWhatEverClick(int position) {
                editor.putString("pp1", mUploads.get(position).getmImageUrl());
                editor.apply();


            }

            @Override
            public void onDeleteClick(int position) {

                Upload selectedItem = mUploads.get(position);
                fvrtModel selectedItem2 = fvrtList.get(position);

                final String selectedKey = selectedItem.getKey();
                final String selectedKey2 = selectedItem2.getKey();

                StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getmImageUrl());
                imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mDatabaseRef.child(selectedKey).removeValue();
                        nDatabaseRef.child(selectedKey2).removeValue();
                        Toast.makeText(MainActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }


    public void AddImage(View view){
       redirecActivity(this,addImage.class);
    }
    public void clickMenu(View view){

        imageSet();
        openDrawer(drawerLayout);
    }

    public void imageSet() {
        ppUrl = sharedPreferences.getString("pp1","1");
        // i am setting default image nothing from firebase
        Picasso.with(MainActivity.this)
                .load(R.drawable.logo)
                .placeholder(R.drawable.logo)
                .fit()
                .centerCrop()
                .into(profileImage);
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }
    public void clickLogo(View view){
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void clickHome(View view){
        recreate();
    }
    public void clickDashBoard(View view){
        redirecActivity(this,Dashboard.class);
    }
    public void clickFavorite(View view){
        redirecActivity(this,FavoriteActivity.class);
    }
    public void clickAboutUs(View view){
        redirecActivity(this,AboutUs.class);
    }
    public void clickLogOut(View view) {
        logOut(this);
    }

    public static void logOut(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure you want to exit");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finishAffinity();
                System.exit(0);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static  void redirecActivity(Activity activity, Class aClass) {

        Intent intent = new Intent(activity,aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);

    }

    private void getData(){
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    upload.setKey(postSnapshot.getKey());
                    mUploads.add(upload);
                }
                mAdapter.notifyDataSetChanged();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void getFavoriteData() {
        nDBListener = nDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fvrtList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    fvrtModel fvrtmodel = postSnapshot.getValue(fvrtModel.class);
                    fvrtmodel.setKey(postSnapshot.getKey());
                    fvrtList.add(fvrtmodel);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {

                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                        Intent intent1 = new Intent();
                        intent1.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",getPackageName(),null);
                        intent1.setData(uri);
                        startActivity(intent1);
                        finish();
                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
}