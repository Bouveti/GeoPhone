package com.example.bouveti.geophone;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;

public class RingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);

        final Button button = (Button) findViewById(R.id.found);
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.meow);
        final AudioManager audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);

        // Met le volume au max
        //audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        mediaPlayer.setLooping(true);

        //SOS en vibration et MORSE
        int dot = 200;
        int dash = 500;
        int short_gap = 200;
        int medium_gap = 500;
        int long_gap = 1000;
        final long[] pattern = { 0, dot, short_gap, dot, short_gap, dot, medium_gap, dash, short_gap, dash, short_gap, dash, medium_gap, dot, short_gap, dot, short_gap, dot, long_gap };

        mediaPlayer.start();
        vibrator.vibrate(pattern, 0);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { //Clic sur le boutton trouvé
                vibrator.cancel();
                mediaPlayer.pause();
                finish();
            }
        });
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
}
