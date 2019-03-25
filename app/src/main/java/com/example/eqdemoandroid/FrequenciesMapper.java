package com.example.eqdemoandroid;

import android.media.audiofx.Equalizer;
import android.util.Log;
import android.widget.SeekBar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

public class FrequenciesMapper {
    private static final String TAG = "FrequenciesMapper";

    private Map<Integer, Integer> equalizerMap = new HashMap<>();

    private Equalizer equalizer;
    private SeekBar[] seekBars;

    public FrequenciesMapper(Equalizer equalizer, SeekBar[] seekBars) {
        this.equalizer = equalizer;
        this.seekBars = seekBars;

        fillEqualizerMap(equalizer, seekBars);
    }

    private void fillEqualizerMap(Equalizer equalizer, SeekBar[] seekBars) {
        short numberOfBands = equalizer.getNumberOfBands();

        for (int i = 0; i < numberOfBands; i++) {
            int[] bandFreqRange = equalizer.getBandFreqRange((short) i);
            int centerFreq = equalizer.getCenterFreq((short) i);
            Log.d(TAG, "getBandFreqRange: " + Arrays.toString(bandFreqRange)
                    + ", center frequency - " + centerFreq);
        }
    }

    public Integer get(@Nullable Integer frequency) {
        return equalizerMap.get(frequency);
    }
}
