package com.example.perfectenglish.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Word implements Parcelable {
    private String original, translation;

    public static Creator<Word> CREATOR = new Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel parcel) {
            return new Word(parcel.readString(), parcel.readString());
        }

        @Override
        public Word[] newArray(int i) {
            return new Word[i];
        }
    };

    public Word(String original, String translation) {
        this.original = original;
        this.translation = translation;
    }

    public String getOriginal() {
        return original;
    }

    public String getTranslation() {
        return translation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(original);
        parcel.writeString(translation);
    }
}
