package com.example.bouveti.geophone;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by Bouveti on 17/12/2016.
 */

public class SmsReader extends BroadcastReceiver{

    private String phoneNumber;
    private String password;

    private GPSTracker tracker;
    private double latitude;
    private double longitude;
    private int wifi;
    private String ssid;

    @Override
    public void onReceive(Context context, Intent intent) {

        //Initialisation de la ressource GPS
        tracker = new GPSTracker(context);

        //Récupération de l'événement de réception d'un SMS
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {

            Bundle bundle = intent.getExtras();

            //Récupération du SMS
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");

                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }

                if (messages.length > -1) {

                    //Récupération du contenu du SMS
                    final String messageBody = messages[0].getMessageBody();
                    phoneNumber = messages[0].getDisplayOriginatingAddress();

                    //Si le SMS contiens la string ""GEOPHONE//"
                    if (messageBody.contains("GEOPHONE//")) {

                        //Récupération du mot de passe enregistré
                        SharedPreferences config = context.getSharedPreferences("credentials", 0);
                        password = config.getString("password", null);

                        //Appel de la méthode de traitement du SMS réçu
                        this.receiveMessage(context, messageBody);
                    }
                }
            }
        }
    }

    //Méthode de traitement du SMS réçu
    public void receiveMessage(Context context, String messageBody){

        //Si le SMS est une requête de position
        if (messageBody.contains("REQUEST//")) {

            //Notification de la tentative de localisation
            this.notifPush(context);
            //Traitement de la requête
            this.sendResponse(context, messageBody);

            //Si le SMS est une réponse "Mauvais mot de passe"
        }else if(messageBody.contains("WRONG_PASSWORD")){
            //Blocage de la localisation
            this.denyLocation(context);
        }else if(messageBody.contains("LOCATION")){

            //Sinon, parsing des coordonnées dans le SMS
            Double longitudeReceived = Double.parseDouble(messageBody.substring(messageBody.lastIndexOf("=")+1,messageBody.length()-5));
            Double latitudeReceived = Double.parseDouble(messageBody.substring(44,messageBody.lastIndexOf("/")));
            String password = messageBody.substring(messageBody.length()-4);

            //Redirection vers la navigation
            this.toMap(context, latitudeReceived, longitudeReceived ,password);
        }else if(messageBody.contains("WIFI")){

            String ssid = messageBody.substring(43,messageBody.lastIndexOf("/"));
            int level = Integer.parseInt(messageBody.substring(messageBody.lastIndexOf("=")+1));

            this.toRechercheRapprochee(context, ssid, level);
        }
    }

    //Méthode de traitement d'une requête de localisation
    public void sendResponse(Context context, String messageBody){

        int responseType = -1;
        String response = "GEOPHONE//";

        if (messageBody.contains("GEOPHONE//LOCATIONREQUEST//")) {
            //Récupération des coordonnées GPS de l'appareil
            this.latitude = this.tracker.getLatitude();
            this.longitude = this.tracker.getLongitude();

            //Initialisation du message de réponse
            response += "LOCATIONRESPONSE//";
            responseType = 1;

        }else if (messageBody.contains("GEOPHONE//WIFIINFOREQUEST//")){

            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            int numberOfLevels = 5;
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            this.wifi = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
            this.ssid = wifiInfo.getSSID();

            response += "WIFIINFORESPONSE//";
            responseType = 0;
        }

        //Parsing du mot de passe reçu
        String passwordReceived = messageBody.substring(messageBody.length() - password.length());

        Log.d("Password:",passwordReceived);

        //Si le mot de passe reçu correspond à celui enregistré
        if(passwordReceived.equals(password)&&responseType == 1){
            //Ajout des coordonnées au message de réponse
            response += "LOCATION:GEOLAT=" + this.latitude + "/GEOLONG=" + this.longitude+"&"+password;

        }else if(passwordReceived.equals(password)&&responseType == 0){
            //Ajout de la force du signal Wifi
            response += "WIFIINFO:SSID="+ this.ssid +"/LEVEL=" + this.wifi;

        }else{
            //Sinon, ajout de la mention "Mauvais mot de passe"
            response += "WRONG_PASSWORD";
        }

        //Envois de la réponse par SMS
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, response, null, null);

    }

    //Méthode d'annulation de la localisation
    public void denyLocation(Context context){

        //Redirection vers l'activité de recherche avec un paramètre permttant l'affichage d'un message d'erreur
        Intent intent = new Intent(context.getApplicationContext(), RechercheActivity.class);
        intent.putExtra("failed", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    //Méthode de redirection vers l'activité de locatlisation avec les coordonnées reçues en paramètres et le numéro envoyeur
    public void toMap(Context context, Double latitude, Double longitude, String password){
        Intent intent = new Intent(context.getApplicationContext(), MapActivity.class);

        //Récupération des paramètres
        intent.putExtra("lat", latitude);
        intent.putExtra("long", longitude);
        intent.putExtra("number",phoneNumber);
        intent.putExtra("password",password);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }

    public void toRechercheRapprochee(Context context, String ssid, int level){
        Intent intent = new Intent(context.getApplicationContext(), RechercheRapprocherActivity.class);

        //Récupération des paramètres
        intent.putExtra("ssid", ssid);
        intent.putExtra("level", level);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);

    }

    //Méthode de notifiaction
    protected void notifPush(Context context){
        //PowerManager pour allumer l'ecran le temps d'afficher la notification PUSH
        //Il utilise la permission suivante dans le manifest : <uses-permission android:name="android.permission.WAKE_LOCK"></uses-permission>
        PowerManager.WakeLock screenOn = ((PowerManager)context.getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "example");
        screenOn.acquire();

        //Creation de la notification PUSH
        NotificationManager notif=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify=new Notification.Builder
                (context).setContentTitle("GeoPhone").setContentText("Quelqu'un vous a geolocalise : "+phoneNumber).
                setContentTitle("GeoPhone").setSmallIcon(R.drawable.ic_gps_fixed_white_24dp).build();

        //Activation de la notification
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notif.notify(0, notify);

        //On release le fait d'avoir allume l'ecran
        screenOn.release();
    }
}