package com.example.eqdemoandroid;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.IBinder;
import android.util.Log;

import java.util.Arrays;

import androidx.core.app.NotificationCompat;

import static com.example.eqdemoandroid.EQApplication.CHANNEL_ID;

public class EQService extends Service {

    public static final String EQUALIZER_KEY = "equalizer";
    public static final String BASS_KEY = "bass";
    public static final String IS_EQ_ON = "eqON";

    private static final String TAG = "EQService";

    private Equalizer equalizer;
    private BassBoost bassBoost;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        short[] equalizerBandLevels = intent.getShortArrayExtra(EQUALIZER_KEY);
        int bassStrength = intent.getIntExtra(BASS_KEY, 0);

        Intent notificationIntent = new Intent(EQService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(EQService.this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(EQService.this, CHANNEL_ID)
                .setContentText("EQNotif")
                .setContentText(Arrays.toString(equalizerBandLevels))
                .setSmallIcon(R.drawable.ic_hearing)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        equalizer = new Equalizer(Integer.MAX_VALUE, 0);
        for (int i = 0; i < equalizerBandLevels.length; i++) {
            equalizer.setBandLevel((short) i, equalizerBandLevels[i]);
        }
        equalizer.setEnabled(true);

        bassBoost = new BassBoost(Integer.MAX_VALUE, 0);
        bassBoost.setStrength((short) bassStrength);
        bassBoost.setEnabled(true);

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
