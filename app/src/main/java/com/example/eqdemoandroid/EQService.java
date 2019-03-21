package com.example.eqdemoandroid;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Arrays;

import static com.example.eqdemoandroid.EQApplication.CHANNEL_ID;

public class EQService extends Service {

    public static final String EQUALIZER_KEY = "equalizer";
    public static final String BASS_KEY = "bass";
    public static final String IS_EQ_ON = "eqON";

    private static final String TAG = "EQService";

    private static Equalizer equalizer;
    private static BassBoost bassBoost;


    public EQService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        short[] equalizerBandLevels = intent.getShortArrayExtra(EQUALIZER_KEY);
        Log.d(TAG, "Retrieved  equalizer settings - " + Arrays.toString(equalizerBandLevels));

        Intent notificationIntent = new Intent(EQService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(EQService.this, 0, notificationIntent, 0);
        notificationIntent.putExtra(IS_EQ_ON, true);

        Notification notification = new NotificationCompat.Builder(EQService.this, CHANNEL_ID)
                .setContentText("EQNotif")
                .setContentText(Arrays.toString(equalizerBandLevels))
                .setSmallIcon(R.drawable.ic_hearing)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        equalizer = new Equalizer(Integer.MAX_VALUE, 0);
        Equalizer.Settings equalizerSettings = new Equalizer.Settings();

        equalizerSettings.bandLevels = equalizerBandLevels;
        equalizerSettings.numBands = equalizer.getNumberOfBands();

        bassBoost = new BassBoost(Integer.MAX_VALUE, 0);
        bassBoost.setStrength((short) 1000);
        bassBoost.setEnabled(true);
//        equalizerSettings.curPreset = 2;

        equalizer.setEnabled(true);

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "EQService is stopped");
        equalizer.setEnabled(false);
        bassBoost.setEnabled(false);
    }
}
