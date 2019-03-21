package com.example.eqdemoandroid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

//    private ArrayList<Song> songList;
//    private ListView songLV;

    private Switch switcher;
    private Equalizer equalizer;
    private BassBoost bassBoost;

    private short[] bandLevelRange;

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

        Intent intent = getIntent();
        boolean booleanExtra = intent.getBooleanExtra(EQService.IS_EQ_ON, false);
        Log.d(TAG, "From notification - " + booleanExtra);
        switcher.setChecked(true);

        equalizer = new Equalizer(Integer.MAX_VALUE, 0);
        bassBoost = new BassBoost(Integer.MAX_VALUE, 0);

        bandLevelRange = equalizer.getBandLevelRange();

        final short numberOfBands = equalizer.getNumberOfBands();
        Log.i(TAG, "Supported number of bands: " + numberOfBands);
        //setUpEqualizerPreset();

        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startEqualizerService();
                } else {
                    stopService(new Intent(MainActivity.this, EQService.class));
                }
            }
        });
    }

    private void setUpEqualizerPreset() {
        Equalizer.Settings settings = new Equalizer.Settings();
        settings.bandLevels = new short[equalizer.getNumberOfBands()];
        settings.numBands = equalizer.getNumberOfBands();

        Log.d(TAG, "Level range - " + Arrays.toString(bandLevelRange));

        bassBoost.setStrength((short) 1000);

        for (int i = 0; i < equalizer.getNumberOfBands(); i++) {
            settings.bandLevels[i] = bandLevelRange[0];
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

    private void startEqualizerService() {
        Intent intent = new Intent(this, EQService.class);

        short numberOfBands = equalizer.getNumberOfBands();
        short[] bandsLevels = new short[numberOfBands];

        for (int i = 0; i < bandsLevels.length; i++) {
            bandsLevels[i] = bandLevelRange[1];
        }

        intent.putExtra(EQService.EQUALIZER_KEY, bandsLevels);

        ContextCompat.startForegroundService(this, intent);
    }
}