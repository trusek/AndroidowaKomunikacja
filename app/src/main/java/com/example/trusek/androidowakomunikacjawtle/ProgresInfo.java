package com.example.trusek.androidowakomunikacjawtle;

import android.os.Parcel;
import android.os.Parcelable;

public class ProgresInfo implements Parcelable {
    int mProgres;
    Long Pobrano;

    public ProgresInfo() {
        this.mProgres = 0;
        Pobrano = (long) 0;
    }

    protected ProgresInfo(Parcel in) {
        mProgres = in.readInt();
        if (in.readByte() == 0) {
            Pobrano = null;
        } else {
            Pobrano = in.readLong();
        }
    }

    public static final Creator<ProgresInfo> CREATOR = new Creator<ProgresInfo>() {
        @Override
        public ProgresInfo createFromParcel(Parcel in) {
            return new ProgresInfo(in);
        }

        @Override
        public ProgresInfo[] newArray(int size) {
            return new ProgresInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mProgres);
        if (Pobrano == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(Pobrano);
        }
    }
}
