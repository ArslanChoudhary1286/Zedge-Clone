package com.myapp.myimages3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.myapp.myimages3.MainActivity.fvrtList;
import static com.myapp.myimages3.MainActivity.ppUrl;
import static com.myapp.myimages3.MainActivity.redirecActivity;

public class FavoriteActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;

    private RecyclerView mRecyclerView;
    private ImageAdapter2 mAdapter;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef, nDatabaseRef;
    private ValueEventListener mDBListener, nDBListener;
    private List<fvrtClass> fvrtlist;
    static int counter = 0;
    public static SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        drawerLayout = findViewById(R.id.drawer_layout);
        img = findViewById(R.id.profileImage);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new GridLayoutManager(FavoriteActivity.this, 3);
        mRecyclerView.setLayoutManager(layoutManager);

        fvrtlist = new ArrayList<>();

        sharedPreferences = getSharedPreferences("pp",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        for (int i = 0; i < MainActivity.fvrtList.size(); i++){
            int CL = Integer.parseInt(MainActivity.fvrtList.get(i).getFm());

            if (CL == 1){
                fvrtClass fvrtclass = new fvrtClass();

                fvrtclass.setImageUrl(MainActivity.mUploads.get(i).getmImageUrl());
                fvrtclass.setPosition(i);

                fvrtlist.add(fvrtclass);
            }
        }

        mAdapter = new ImageAdapter2(FavoriteActivity.this, fvrtlist);


        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new ImageAdapter2.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                String imgUrl = fvrtlist.get(position).getImageUrl();


                Intent intent1  = new Intent(FavoriteActivity.this,displayActivity.class);
                intent1.putExtra("url",imgUrl);
                startActivity(intent1);
            }

            @Override
            public void onWhatEverClick(int position) {

                editor.putString("pp1", fvrtlist.get(position).getImageUrl());
                editor.apply();
                Toast.makeText(FavoriteActivity.this, "Whatever click at position: " + fvrtlist.get(position).getImageUrl(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(int position) {

                fvrtModel selectedItem = MainActivity.fvrtList.get(fvrtlist.get(position).getPosition());
                final String selectedKey = selectedItem.getKey();
                Log.d("delete","the delete item is " + selectedItem);
                Log.d("delete","the delete key is " + selectedKey);

                HashMap<String,Object> hashMap = new HashMap();
                hashMap.put("fm","0");

                nDatabaseRef.child(selectedKey).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Toast.makeText(FavoriteActivity.this, "Remove from Favorite", Toast.LENGTH_LONG).show();
                        recreate();
                    }
                });

            }
        });
        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        nDatabaseRef = FirebaseDatabase.getInstance().getReference("favoriteImages");


    }
    public void AddImage(View view){
        redirecActivity(this,addImage.class);
    }
    public void clickMenu(View view){
        ppUrl = sharedPreferences.getString("pp1","1");
        Picasso.with(this)
                .load(ppUrl)
                .placeholder(R.drawable.person)
                .fit()
                .centerCrop()
                .into(img);

        MainActivity.openDrawer(drawerLayout);

    }

    public void clickLogo(View view){
        MainActivity.closeDrawer(drawerLayout);

    }
    public void clickHome(View view){
        redirecActivity(this,MainActivity.class);

    }
    public void clickDashBoard(View view){
        redirecActivity(this,Dashboard.class);
    }
    public void clickFavorite(View view){
        recreate();
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
    }


    //    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        nDatabaseRef.removeEventListener(nDBListener);
//    }

//    @Override
//    public void onResume() {
//        super.onResume();
////        mRecyclerView.scrollToPosition(mUploads.size()-1);
//    }

}