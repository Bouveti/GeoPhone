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
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
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

    @Override
    public void onReceive(Context context, Intent intent) {

        tracker = new GPSTracker(context);

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {

            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");

                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }

                if (messages.length > -1) {

                    final String messageBody = messages[0].getMessageBody();
                    phoneNumber = messages[0].getDisplayOriginatingAddress();

                    if (messageBody.contains("GEOPHONE//")) {

                        SharedPreferences config = context.getSharedPreferences("credentials", 0);
                        password = config.getString("password", null);

                        this.receiveMessage(context, messageBody);
                    }
                }
            }
        }
    }


    public void receiveMessage(Context context, String messageBody){

        if (messageBody.contains("GEOPHONE//LOCATIONREQUEST//")) {

            //this.notifPush(context);
            this.sendResponse(context, messageBody);

        }else if(messageBody.contains("WRONG_PASSWORD")){
            this.denyLocation();
        }else{

            String longitudeReceived = messageBody.substring(messageBody.lastIndexOf("="));
            String latitudeReceived = messageBody.substring(15,messageBody.lastIndexOf("/"));

            this.toMap(context, latitudeReceived, longitudeReceived);
        }
    }

    public void sendResponse(Context context, String messageBody){

        this.latitude = this.tracker.getLatitude();
        this.longitude = this.tracker.getLongitude();

        String response = "GEOPHONE//LOCATIONRESPONSE//";
        String passwordReceived = messageBody.substring(messageBody.length() - password.length());

        Log.d("Password:",passwordReceived);

        if(passwordReceived.equals(password)){
            response += "LOCATION:GEOLAT=" + this.latitude + "/GEOLONG=" + this.longitude;
        }else{
            response += "WRONG_PASSWORD";
        }

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, response, null, null);

    }

    public void denyLocation(){

    }

    public void toMap(Context context, String latitude, String longitude){
        Intent intent = new Intent(context.getApplicationContext(), MapActivity.class);
        intent.putExtra("lat", latitude);
        intent.putExtra("long", longitude);
        intent.putExtra("number",phoneNumber);
        context.startActivity(intent);
    }

    /*protected void notifPush(Context context){
        //PowerManager pour allumer l'ecran le temps d'afficher la notification PUSH
        //Il utilise la permission suivante dans le manifest : <uses-permission android:name="android.permission.WAKE_LOCK"></uses-permission>
        PowerManager.WakeLock screenOn = ((PowerManager)context.getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "example");
        screenOn.acquire();

        //Creation de la notification PUSH
        NotificationManager notif=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify=new Notification.Builder
                (context).setContentTitle("GeoPhone").setContentText("Quelqu'un vous a géolocalisé : "+phoneNumber).
                setContentTitle("GeoPhone").setSmallIcon(R.drawable.ic_menu_share).build();

        //Activation de la notification
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notif.notify(0, notify);

        //On release le fait d'avoir allume l'ecran
        screenOn.release();
    }*/

}
