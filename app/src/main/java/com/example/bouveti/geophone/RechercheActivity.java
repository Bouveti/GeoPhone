package com.example.bouveti.geophone;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Locale;

public class RechercheActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public ListView lv;
    public Cursor cursor1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Récupération du layout de l'activité
        setContentView(R.layout.activity_recherche);
        //Mise en place de la barre d'action
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Vérification de la présence de paramètre
        Bundle extra = getIntent().getExtras();
        //Si un paramètre "Failed existe"
        if(extra != null)if(extra.getBoolean("failed")){
            //Message d'erreur
            Toast.makeText(RechercheActivity.this,
                    getString(R.string.mauvais_mot_de_passe), Toast.LENGTH_SHORT)
                    .show();
            finish();
        }

        //Mise en place du sideMenu
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,  R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Demande des permissions requises
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        , Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        if (hasPermissions(this, PERMISSIONS)) {

            String[] projection = { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER , ContactsContract.CommonDataKinds.Phone._ID};
            cursor1 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            startManagingCursor(cursor1);

            String[] from = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone._ID};
            int[] to = { android.R.id.text1, android.R.id.text2 };

            final SimpleCursorAdapter listCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor1, from, to);


            lv = (ListView)findViewById(android.R.id.list);
            lv.setAdapter(listCursorAdapter);

            lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    search(true, parent, view, position, id);
                        }
            });
        }
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
                Toast.makeText(RechercheActivity.this,
                        getString(R.string.change_lang), Toast.LENGTH_SHORT)
                        .show();
                item.setIcon(R.drawable.fr_fr);
            } else if (lang.equals("fr_FR"))
            {
                setLocale(Locale.US);
                Toast.makeText(RechercheActivity.this,
                        getString(R.string.change_lang), Toast.LENGTH_SHORT)
                        .show();
                item.setIcon(R.drawable.en_us);
            } else {
                setLocale(Locale.FRANCE);
                Toast.makeText(RechercheActivity.this,
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
        } else if (id == R.id.nav_mot_de_passe) {
            startActivity(new Intent(getApplicationContext(),PasswordActivity.class));
            overridePendingTransition(0,0);
            finish();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Méthode de recherche pour un numéro inconny
    public void rechercheSansContact(View v){
        search(false, null, v ,0 ,0);
    }

    //Méthode de recherche d'un numéro
    public void search(boolean isKnown, AdapterView<?> parent,View view, int position, long id  ){

        //Récupération des éléments graphiques
        EditText recherche = (EditText) findViewById(R.id.recherche_sans_contact);

        final String name ;
        final String number;

        //Si le numéro est un contact
        if(isKnown){
            //Récupération des informations du contact
            Adapter adapter = parent.getAdapter();
            SimpleCursorAdapter simpleCursorAdapter = (SimpleCursorAdapter)adapter;
            Cursor cursor = (Cursor)simpleCursorAdapter.getItem(position);

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

        }else{

            //Sinon, récupération du numéro entré
            name = getString(R.string.ce_num);
            number = recherche.getText().toString();
        }

        //Mise en place d'un popup demandant confirmation et mot de passe
        final EditText input = new EditText(RechercheActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        new AlertDialog.Builder(RechercheActivity.this)
                .setTitle(getString(R.string.button_rechercher) + " " + name + " ?")
                .setMessage(getString(R.string.message_recherche_confirm) + " " + name + " (" + number + " )"+ " ?\n\n"+getString(R.string.mot_de_passe))
                .setView(input)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String password = input.getText().toString();

                        requestPositionBySMS(password, number);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_menu_search)
                .show();

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

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    //Méthode d'envois de la requête de position
    public void requestPositionBySMS(String password, String number){

        String message = "GEOPHONE//LOCATIONREQUEST//PASSWORD:"+password;

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, message, null, null);

        Toast.makeText(RechercheActivity.this,
                getString(R.string.message_envoye), Toast.LENGTH_SHORT)
                .show();

    }
}