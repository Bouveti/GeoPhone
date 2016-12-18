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
import android.widget.Toast;
import java.util.Locale;

public class RechercheRapprocheeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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

        final Button button = (Button) findViewById(R.id.found);
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.meow);
        final AudioManager audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);

        // Met le volume au max
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        mediaPlayer.setLooping(true);

        //SOS en vibration et MORSE
        int dot = 200;
        int dash = 500;
        int short_gap = 200;
        int medium_gap = 500;
        int long_gap = 1000;
        final long[] pattern = { 0, dot, short_gap, dot, short_gap, dot, medium_gap, dash, short_gap, dash, short_gap, dash, medium_gap, dot, short_gap, dot, short_gap, dot, long_gap };

        Switch aSwitch = (Switch) findViewById(R.id.switch_vibe_sound);
        aSwitch.setChecked(false); // Fix les vibrations et la sonnerie par défault à false

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) //If sound and vibration are switched to on, start vibration and sound
                {
                    mediaPlayer.start();
                    vibrator.vibrate(pattern, 0);
                    button.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) { //Clic sur le boutton trouvé
                            vibrator.cancel();
                            mediaPlayer.pause();
                        }
                    });
                } else {
                    vibrator.cancel();
                    mediaPlayer.pause();
                }
            }
        }
        );

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarWifi);
        progressBar.setMax(5);

        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        int numberOfLevels = 5;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        // Récupérer la puissance du wifi. Plus elle est proche du max(5) plus le téléphone est pret de la borne wifi
        int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
        progressBar.setProgress(level);

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


}

