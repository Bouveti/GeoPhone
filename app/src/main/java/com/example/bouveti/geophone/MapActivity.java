package com.example.bouveti.geophone;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.Locale;

public class MapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    String phoneNumber;
    String password;
    double longitude;
    double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Récupération du layout de l'activité
        setContentView(R.layout.activity_map);
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
                phoneNumber = null;
            } else {
                phoneNumber = extras.getString("number");
                password = extras.getString("password");
                this.latitude = extras.getDouble("lat");
                this.longitude = extras.getDouble("long");

            }
        } else {
            phoneNumber = (String) savedInstanceState.getSerializable("number");
        }
        SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);
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

        //Initialisation de la langue utilisée
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
                Toast.makeText(MapActivity.this,
                        getString(R.string.change_lang), Toast.LENGTH_SHORT)
                        .show();
                item.setIcon(R.drawable.fr_fr);
            } else if (lang.equals("fr_FR"))
            {
                setLocale(Locale.US);
                Toast.makeText(MapActivity.this,
                        getString(R.string.change_lang), Toast.LENGTH_SHORT)
                        .show();
                item.setIcon(R.drawable.en_us);
            } else {
                setLocale(Locale.FRANCE);
                Toast.makeText(MapActivity.this,
                        getString(R.string.change_lang), Toast.LENGTH_SHORT)
                        .show();
                item.setIcon(R.drawable.fr_fr);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Méthode de sélection des items du sideMenu
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
        Intent refresh = new Intent(this, RechercheActivity.class);
        startActivity(refresh);
        overridePendingTransition(0,0);
        finish();
    }

    //Méthode d'initialisation de la carte
    @Override
    public void onMapReady(GoogleMap googleMap) {

        MarkerOptions marker = new MarkerOptions();
        marker.position(new LatLng(this.latitude, this.longitude));
        marker.title( R.string.position_of + phoneNumber);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(this.latitude, this.longitude), 9));

        googleMap.setOnMarkerClickListener(this);
        googleMap.addMarker(marker);
    }

    //Méthode de message sur clique du marker
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (( R.string.position_of + phoneNumber).equals(marker.getTitle()))
        {
            Toast.makeText(this.getApplicationContext(), R.string.position_of + phoneNumber, Toast.LENGTH_LONG).show();
        }
        return true;
    }

    //Méthode de lancement de la navigation sur googleMap
    public void LaunchNavInMaps(View v){
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+this.latitude+", "+this.longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void requestWifiBySms(String number, String password){
        String message = "GEOPHONE//WIFIINFOREQUEST//PASSWORD:"+password;

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, message, null, null);
    }

    //Méthode de transition vers l'activité de guidage rapproché
    public void toRechercheRapprochee(View v)
    {
        requestWifiBySms(this.phoneNumber, this.password);
    }
}
