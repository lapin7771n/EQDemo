package com.example.eqdemoandroid;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

//    private ArrayList<Song> songList;
//    private ListView songLV;

    private Switch switcher;
    private Equalizer equalizer;
    private BassBoost bassBoost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            throw new RuntimeException("Permission not granted");
        } else {
            Log.i(TAG, "Permission granted!");
        }

        switcher = findViewById(R.id.switcher);
        equalizer = new Equalizer(Integer.MAX_VALUE, 0);
        final short numberOfBands = equalizer.getNumberOfBands();
        Log.i(TAG, "Supported number of bands: " + numberOfBands);
        setUpEqualizerPreset();

        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                equalizer.setEnabled(isChecked);
            }
        });
    }

    private void setUpEqualizerPreset() {
        Equalizer.Settings settings = new Equalizer.Settings();
        settings.bandLevels = new short[equalizer.getNumberOfBands()];
        settings.numBands = equalizer.getNumberOfBands();

        for (int i = 0; i < equalizer.getNumberOfBands(); i++) {
            settings.bandLevels[i] = (short) 4;
        }

        try {
            equalizer.setProperties(settings);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }
    }
}
