package com.example.eqdemoandroid;

import android.media.audiofx.Equalizer;
import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableEQ implements Parcelable {
    private short currentPreset;
    private short numBands;
    private short[] bandLevels;

    public ParcelableEQ(Equalizer equalizer) {
        currentPreset = equalizer.getCurrentPreset();
        numBands = equalizer.getNumberOfBands();
        bandLevels = equalizer.getProperties().bandLevels;
    }

    protected ParcelableEQ(Parcel in) {
        currentPreset = (short) in.readInt();
        numBands = (short) in.readInt();

        //converting int to short and writing in 'private short[] bandLevels' field
        int[] bandLevelsInts = new int[bandLevels.length];
        in.readIntArray(bandLevelsInts);
        for (int i = 0; i < bandLevelsInts.length; i++) {
            bandLevels[i] = (short) bandLevelsInts[i];
        }
    }

    public static final Creator<ParcelableEQ> CREATOR = new Creator<ParcelableEQ>() {
        @Override
        public ParcelableEQ createFromParcel(Parcel in) {
            return new ParcelableEQ(in);
        }

        @Override
        public ParcelableEQ[] newArray(int size) {
            return new ParcelableEQ[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writing to Parcelable object
     * First - current preset (short)
     * Second - number of bands (short)
     * Third - band levels (short[])
     *
     * @param dest  this object will contains our data
     * @param flags special flags for writing
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        int[] bandLevelsInts = new int[bandLevels.length];
        for (int i = 0; i < bandLevels.length; i++) {
            bandLevelsInts[i] = bandLevels[i];
        }

        dest.writeInt(currentPreset);
        dest.writeInt(numBands);
        dest.writeIntArray(bandLevelsInts);
    }
}
