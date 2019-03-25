package com.example.eqdemoandroid;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int SEEK_BAR_DEF_VALUE = 50;
    private static final int SEEK_BAR_MAX_VALUE = 100;

    private static final int SEEK_BAR_ID_COEF = 123456;

    @BindView(R.id.switcher)
    Switch switcher;
    @BindView(R.id.seekBarsLL)
    LinearLayout seekBarsLL;
    TextView currentSettings;
    @BindView(R.id.bassBoostSwitcher)
    Switch bassBoostSwitcher;
    @BindView(R.id.bassBoostSeekBar)
    AppCompatSeekBar bassBoostSeekBar;

    private Equalizer equalizer;

    private short[] bandLevelRange;
    private SharedPreferences preferences;
    private SeekBar[] seekBars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkPermission();

        preferences = getSharedPreferences(getString(R.string.preferenceKey), MODE_PRIVATE);
        boolean isServiceStarted = preferences.getBoolean(
                getString(R.string.isServiceStarted),
                false);
        switcher.setChecked(isServiceStarted);
        switcher.setOnCheckedChangeListener(new SwitcherListener());

        equalizer = new Equalizer(Integer.MAX_VALUE, 0);
        bandLevelRange = equalizer.getBandLevelRange();
        new FrequenciesMapper(equalizer, seekBars);

        currentSettings = new TextView(this);
        seekBarsLL.addView(currentSettings);
        initSeekBars();

        bassBoostSwitcher.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startEqualizerService(equalizer.getNumberOfBands());
            } else {
                stopService(new Intent(MainActivity.this, EQService.class));
            }
        });

        bassBoostSeekBar.setOnSeekBarChangeListener(new SeekBarProgressListener());
    }

    private void initSeekBars() {
        short numberOfBands = equalizer.getNumberOfBands();
        seekBars = new SeekBar[numberOfBands];

        for (int i = 0; i < seekBars.length; i++) {
            SeekBar seekBar = new SeekBar(this);
            int seekBarCount = i + 1;
            seekBar.setId(SEEK_BAR_ID_COEF * seekBarCount);

            int seekBarProgress = preferences.getInt(
                    String.valueOf(seekBar.getId()),
                    SEEK_BAR_DEF_VALUE);

            seekBar.setProgress(seekBarProgress);
            seekBar.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            seekBar.setOnSeekBarChangeListener(new SeekBarProgressListener());

            TextView label = new TextView(this);
            String text = String.format("%dHz", equalizer.getCenterFreq((short) i) / 1000);
            label.setText(text);
            label.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            seekBarsLL.addView(label);
            seekBarsLL.addView(seekBar);
            seekBars[i] = seekBar;
        }
    }

    private void checkPermission() {
        boolean isPermissionGranted = ContextCompat
                .checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS)
                != PackageManager.PERMISSION_GRANTED;

        if (isPermissionGranted) {
            throw new PermissionNotGrantedError("Permission not granted");
        } else {
            Log.i(TAG, "Permission granted!");
        }
    }

    private void startEqualizerService(short numberOfBands) {
        Intent intent = new Intent(this, EQService.class);

        short[] bandsLevels = new short[numberOfBands];
        short minValue = bandLevelRange[0];
        short maxValue = bandLevelRange[1];

        int bandBound = Math.abs(minValue) + Math.abs(maxValue);
        int coefficient = bandBound / SEEK_BAR_MAX_VALUE;

        for (int i = 0; i < bandsLevels.length; i++) {
            int progress = seekBars[i].getProgress();
            bandsLevels[i] = (short) (minValue + progress * coefficient);
        }
        currentSettings.setText(Arrays.toString(bandsLevels));
        intent.putExtra(EQService.EQUALIZER_KEY, bandsLevels);

        if (bassBoostSwitcher.isChecked()) {
            intent.putExtra(EQService.BASS_KEY, bassBoostSeekBar.getProgress() * 10);
        }
        ContextCompat.startForegroundService(this, intent);
        Log.i(TAG, "Equalizer service started");
    }

    private class SwitcherListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                startEqualizerService(equalizer.getNumberOfBands());
            } else {
                stopService(new Intent(MainActivity.this, EQService.class));
            }
            preferences.edit()
                    .putBoolean(getString(R.string.isServiceStarted), isChecked)
                    .apply();
        }
    }

    private class SeekBarProgressListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (switcher.isChecked()) {
                startEqualizerService(equalizer.getNumberOfBands());
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //We don't have to implement this method
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.d(TAG, "Saved to preferences - [ " + seekBar.getId() + " ];");
            int progress = seekBar.getProgress();
            preferences.edit()
                    .putInt(String.valueOf(seekBar.getId()), progress)
                    .apply();
        }
    }
}