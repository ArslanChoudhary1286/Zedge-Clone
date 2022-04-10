package com.myapp.myimages3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;

import static com.myapp.myimages3.MainActivity.redirecActivity;

public class AboutUs extends AppCompatActivity {
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        drawerLayout = findViewById(R.id.drawer_layout);
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
            redirecActivity(this,Dashboard.class);
        }
    public void clickFavorite(View view){
        redirecActivity(this,FavoriteActivity.class);
    }
        public void clickAboutUs (View view){

            recreate();

        }
    public void clickLogOut(View view) {
        MainActivity.logOut(this);
    }

        @Override
        protected void onPause() {
            super.onPause();
            MainActivity.closeDrawer(drawerLayout);
        }
}