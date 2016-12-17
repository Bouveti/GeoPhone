package com.example.bouveti.geophone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by Bouveti on 16/12/2016.
 */

public class ForgottenActivity extends AppCompatActivity
            implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Récupération du layout de l'activité
        setContentView(R.layout.activity_forgotten);
        //Mise en place de la barre d'action
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Mise en place du sideMenu
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

        if (id == R.id.lang_setting) {
            String lang = getResources().getConfiguration().locale.toString();

            if(lang.equals("en_US"))
            {
                Toast.makeText(ForgottenActivity.this ,
                        "Changement de langue pour le Français", Toast.LENGTH_SHORT)
                        .show();
                item.setIcon(R.drawable.fr_fr);
                setLocale(Locale.FRANCE);
            } else if (lang.equals("fr_FR"))
            {
                Toast.makeText(ForgottenActivity.this,
                        "Change of language for English", Toast.LENGTH_SHORT)
                        .show();
                    item.setIcon(R.drawable.en_us);
                setLocale(Locale.US);
            } else {
                Toast.makeText(ForgottenActivity.this,
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home ) {
            //Lancement de l'activité d'accueil
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            overridePendingTransition(0,0);
            finish();
        }
        else if (id == R.id.nav_recent) {
            //Lancement de l'activité "Récent"
            startActivity(new Intent(getApplicationContext(),RecentActivity.class));
            overridePendingTransition(0,0);
            finish();

            //Lancement de l'activité "Mot de passe"
        } else if (id == R.id.nav_mot_de_passe) {
            startActivity(new Intent(getApplicationContext(), com.example.bouveti.geophone.PasswordActivity.class));
            overridePendingTransition(0,0);
            finish();

            //Lancement de l'activité "Paramètres"
        } else if (id == R.id.nav_parametre) {
            startActivity(new Intent(getApplicationContext(),ParametreActivity.class));
            overridePendingTransition(0,0);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Méthode de vérification de la réponse à la question et affichage du mot de passe
    public void showPassword(View v){

        //Récupération des éléments graphiques
        EditText answerSub = (EditText) findViewById(R.id.enter_secrete_answer);
        TextView showPassword = (TextView) findViewById(R.id.reveal_password);
        String answer = answerSub.getText().toString();

        //Récupération de la bonne réponse
        SharedPreferences config = getSharedPreferences("credentials",0);

        //Si la réponse entrée correspond à la réponse stockée en mémoire
        if(config.getString("answer",null).equals(answer)){
            //Affichage du mot de passe
            showPassword.setText(showPassword.getText().toString()+config.getString("password",null));
            answerSub.setText("");
        }else{
            //Sinon, message d'erreur
            Toast.makeText(ForgottenActivity.this,
                    "Mauvaise réponse", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    //Rafraichissement de l'activité au changement de langue
    public void setLocale(Locale lang)
    {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = lang;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, com.example.bouveti.geophone.ForgottenActivity.class);
        startActivity(refresh);
        overridePendingTransition(0,0);
        finish();
    }
}
