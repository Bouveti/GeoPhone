package com.example.bouveti.geophone;

import android.Manifest;
import android.content.ContentResolver;
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
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class RechercheActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else if (hasPermissions(this, PERMISSIONS)) {
            ListView list = (ListView) findViewById(R.id.list_contact);
            List<String> contacts = retrieveContacts(this.getContentResolver());

            if (contacts != null)
            {
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contacts);
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String item = adapter.getItem(position);

                        new AlertDialog.Builder(RechercheActivity.this)
                                .setTitle( getString(R.string.button_rechercher) + " " + item + " ?")
                                .setMessage( getString(R.string.message_recherche_confirm) + " " + item + " ?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(getApplicationContext(), RechercheActivity2.class));
                                        overridePendingTransition(0,0);
                                        finish();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setIcon(android.R.drawable.ic_menu_search)
                                .show();
                    }
                });
            }
        }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home ) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            overridePendingTransition(0,0);
            finish();
        }
        else if (id == R.id.nav_recent) {
            startActivity(new Intent(getApplicationContext(),RecentActivity.class));
            overridePendingTransition(0,0);
            finish();

        } else if (id == R.id.nav_mot_de_passe) {
            startActivity(new Intent(getApplicationContext(),PasswordActivity.class));
            overridePendingTransition(0,0);
            finish();

        } else if (id == R.id.nav_parametre) {
            startActivity(new Intent(getApplicationContext(),ParametreActivity.class));
            overridePendingTransition(0,0);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

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

    private List<String> retrieveContacts(ContentResolver contentResolver)
    {
        Set<String> contacts = new HashSet<>();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, new String[]
                { ContactsContract.Data.DISPLAY_NAME, ContactsContract.Data._ID, ContactsContract.Contacts.HAS_PHONE_NUMBER }, null, null, null);

        if (cursor == null)
        {
            Log.e("retrieveContacts", "Cannot retrieve the contacts");
            return null;
        }

        if (cursor.moveToFirst() == true)
        {
            do
            {
                long id = Long.parseLong(cursor.getString(cursor.getColumnIndex(ContactsContract.Data._ID)));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                int hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Data.HAS_PHONE_NUMBER));

                if (hasPhoneNumber > 0)
                {
                    contacts.add(name);
                }
            }
            while (cursor.moveToNext() == true);
        }

        if (cursor.isClosed() == false)
        {
            cursor.close();
        }

        List<String> sortedContacts = new ArrayList<>(contacts);
        Collections.sort(sortedContacts);

        return sortedContacts;
    }
}
