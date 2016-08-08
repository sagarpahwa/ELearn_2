package com.ithub.mda.elearn;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class YourElearn extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView userpic;
    private NavigationView navigationView;
    private View headerView;
    private TextView email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_elearn);

        //database code
        SQLiteDatabase SQLreaddb = new ELearnLocalDB(this).getReadableDatabase();
        Cursor cr = SQLreaddb.query("elearnuserdetails",new String[]{"name","email","profilepic","status"},"",null,"","","name");
        cr.moveToNext();

        //initialize
        userpic = (ImageView) findViewById(R.id.elearn_user_image_view);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.removeHeaderView(navigationView.getHeaderView(0));
        navigationView.setNavigationItemSelectedListener(this);
        headerView = LayoutInflater.from(this).inflate(R.layout.nav_header_your_elearn,null);
        //set image name and idd
        loadImageFromStorage(cr.getString(2));
        TextView name = (TextView)headerView.findViewById(R.id.elearnusername);
        email = (TextView)headerView.findViewById(R.id.elearnuseremail);
        navigationView.addHeaderView(headerView);
        name.setText(cr.getString(0));
        email.setText(cr.getString(1));
        Toast.makeText(this, cr.getString(0) + "," + cr.getString(1) + "" + cr.getInt(3), Toast.LENGTH_SHORT).show();

        //auto genereated by IDE for toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //auto genereated by IDE for fab
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //auto genereated by IDE for navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }
    //my code goes here
    private void loadImageFromStorage(String path) {
        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            userpic.setImageBitmap(b);
            ImageView tryy = (ImageView)headerView.findViewById(R.id.userPicImageView);
            tryy.setImageBitmap(b);
        } catch (FileNotFoundException e) { e.printStackTrace();
            Toast.makeText(this, "error pic:"+e, Toast.LENGTH_SHORT).show();}
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.your_elearn, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            SQLiteDatabase sQLwritedb = new ELearnLocalDB(this).getWritableDatabase();
            sQLwritedb.execSQL("delete from elearnuserdetails");
            //sQLwritedb.delete("elearnuserdetails","status=0",null);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
