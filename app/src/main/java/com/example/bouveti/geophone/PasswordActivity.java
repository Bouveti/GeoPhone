package com.example.bouveti.geophone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class PasswordActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        getMenuInflater().inflate(R.menu.main, menu);

        String lang = getResources().getConfiguration().locale.toString();
        MenuItem lang_item = menu.findItem(R.id.lang_setting);

        if(lang.equals("en_US"))
        {
            lang_item.setIcon(R.drawable.en_us);
        } else if ( lang.equals("fr_FR"))
        {
            lang_item.setIcon(R.drawable.fr_fr);
        } else {
            lang_item.setIcon(R.drawable.fr_fr);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.lang_setting) {
            String lang = getResources().getConfiguration().locale.toString();

            if(lang.equals("en_US"))
            {
                Toast.makeText(PasswordActivity.this,
                        "Changement de langue pour le Français", Toast.LENGTH_SHORT)
                        .show();
                item.setIcon(R.drawable.fr_fr);
                setLocale(Locale.FRANCE);
            } else if (lang.equals("fr_FR"))
            {
                Toast.makeText(PasswordActivity.this,
                        "Change of language for English", Toast.LENGTH_SHORT)
                        .show();
                item.setIcon(R.drawable.en_us);
                setLocale(Locale.US);
            } else {
                Toast.makeText(PasswordActivity.this,
                        "Changement de langue pour le Français", Toast.LENGTH_SHORT)
                        .show();
                item.setIcon(R.drawable.fr_fr);
                setLocale(Locale.FRANCE);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            overridePendingTransition(0, 0);
            finish();
        } else if (id == R.id.nav_recent) {
            startActivity(new Intent(getApplicationContext(), RecentActivity.class));
            overridePendingTransition(0, 0);
            finish();

        } else if (id == R.id.nav_mot_de_passe) {
            startActivity(new Intent(getApplicationContext(), PasswordActivity.class));
            overridePendingTransition(0, 0);
            finish();

        } else if (id == R.id.nav_parametre) {
            startActivity(new Intent(getApplicationContext(), ParametreActivity.class));
            overridePendingTransition(0, 0);
            finish();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void changePassword(View v){

        EditText oldPassSub = (EditText) findViewById(R.id.enterPassword);
        EditText newPassSub =  (EditText) findViewById(R.id.enterNewPassword);

        String password = oldPassSub.getText().toString();
        String newPassword = newPassSub.getText().toString();

        SharedPreferences config = getSharedPreferences("credentials",0);
        SharedPreferences.Editor editor = config.edit();

        if(password.equals(config.getString("password",null))){
            editor.putString("password", newPassword);
            editor.apply();
            Toast.makeText(PasswordActivity.this,
                    "Mot de passe changé", Toast.LENGTH_SHORT)
                    .show();
            oldPassSub.setText("");
            newPassSub.setText("");

        }else {
            Toast.makeText(PasswordActivity.this,
                    "Mauvais mot de passe", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void toForgotten(View v)
    {
        Intent intent = new Intent(this , ForgottenActivity.class);
        startActivity(intent);
    }

    public void setLocale(Locale lang)
    {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = lang;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, PasswordActivity.class);
        startActivity(refresh);
        overridePendingTransition(0,0);
        finish();
    }
}
