package com.example.bouveti.geophone;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by Bouveti on 16/12/2016.
 */

public class InitialisationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Récupération du layout de l'activité
        setContentView(R.layout.activity_initialisation);

        //Mise en place de la barre d'action
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    //Appel lors de l'utilisation du bouton retour
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                }).setNegativeButton("No", null).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return false;
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
                Toast.makeText(InitialisationActivity.this,
                        "Changement de langue pour le Français", Toast.LENGTH_SHORT)
                        .show();
                item.setIcon(R.drawable.fr_fr);
                setLocale(Locale.FRANCE);
            } else if (lang.equals("fr_FR"))
            {
                Toast.makeText(InitialisationActivity.this,
                        "Change of language for English", Toast.LENGTH_SHORT)
                        .show();
                item.setIcon(R.drawable.en_us);
                setLocale(Locale.US);
            } else {
                Toast.makeText(InitialisationActivity.this,
                        "Changement de langue pour le Français", Toast.LENGTH_SHORT)
                        .show();
                item.setIcon(R.drawable.fr_fr);
                setLocale(Locale.FRANCE);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Méthode de sélection des items du sideMenu
   @SuppressWarnings("StatementWithEmptyBody")
   @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        return true;
    }

    //Méthode de récupération des entrées utilisateurs
    public void submit(View v){

        EditText passwordSub, answerSub;

        //Récupération des éléments graphiques
        passwordSub = (EditText) findViewById(R.id.enterPassword);
        answerSub = (EditText) findViewById(R.id.enter_secrete_answer);

        //Récupération du mot de passe et de la réponse secrète
        String password = passwordSub.getText().toString();
        String answer = answerSub.getText().toString();

        //Enregistrement dans les SharedPreferences
        SharedPreferences config = getSharedPreferences("credentials",0);
        SharedPreferences.Editor editor = config.edit();

        editor.putString("password",password);
        editor.putString("answer",answer);

        editor.apply();

        finish();
    }


    //Rafraichissement de l'activité au changement de langue
    public void setLocale(Locale lang)
    {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = lang;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        overridePendingTransition(0,0);
        finish();
    }
}
