package com.example.bouveti.geophone;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.SmsManager;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;

public class RechercheRapprocheeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String wifiName;
    int intensityWifi;
    String phoneNummber;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Récupération du layout de l'activité
        setContentView(R.layout.activity_recherche_rapprochee);
        //Mise en place de la barre d'action
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Mise en place du sideMenu
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Récupération des paramètres de l'activité précédente
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                wifiName = getString(R.string.erreur_wifi);
                intensityWifi = 0;
            } else {
                wifiName = extras.getString("ssid");
                intensityWifi = extras.getInt("level");
                phoneNummber = extras.getString("number");
                password = extras.getString("password");

                this.ring();
            }
        } else {
            wifiName = (String) savedInstanceState.getSerializable("ssid");
            intensityWifi = (int) savedInstanceState.getSerializable("number");
        }

        TextView tvWifiName = (TextView) findViewById(R.id.tvWifiName);
        tvWifiName.setText(wifiName);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarWifi);
        progressBar.setMax(5);

        // Remplir la progressbar plus la puissance du signal du wifi est forte
        progressBar.setProgress(intensityWifi);

    }

    //Appel lors de l'utilisation du bouton retour
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

        //Initialisation de la langue utilisée
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

    //Méthode de changement de langue
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
                setLocale(Locale.FRANCE);
                Toast.makeText(RechercheRapprocheeActivity.this,
                        getString(R.string.change_lang), Toast.LENGTH_SHORT)
                        .show();
                item.setIcon(R.drawable.fr_fr);
            } else if (lang.equals("fr_FR"))
            {
                setLocale(Locale.US);
                Toast.makeText(RechercheRapprocheeActivity.this,
                        getString(R.string.change_lang), Toast.LENGTH_SHORT)
                        .show();
                item.setIcon(R.drawable.en_us);
            } else {
                setLocale(Locale.FRANCE);
                Toast.makeText(RechercheRapprocheeActivity.this,
                        getString(R.string.change_lang), Toast.LENGTH_SHORT)
                        .show();
                item.setIcon(R.drawable.fr_fr);
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

        if (id == R.id.nav_home ) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            overridePendingTransition(0,0);
            finish();
        }else if (id == R.id.nav_mot_de_passe) {
            startActivity(new Intent(getApplicationContext(),PasswordActivity.class));
            overridePendingTransition(0,0);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Rafraichissement de l'activité au changement de langue
    public void setLocale(Locale lang)
    {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = lang;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, RechercheRapprocheeActivity.class);
        startActivity(refresh);
        overridePendingTransition(0,0);
        finish();
    }

    public void ring(){

        String message = "GEOPHONE//RING//PASSWORD:"+this.password;

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(this.phoneNummber, null, message, null, null);

    }

}

