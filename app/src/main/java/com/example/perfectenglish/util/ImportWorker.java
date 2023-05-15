package com.example.perfectenglish.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.perfectenglish.repository.GsonHelper;

public class ImportWorker  extends Worker {
    public static final String KEY_ORIGINALS = "originals";
    public static final String KEY_TRANSLATIONS = "translations";
    private Context context;

    public ImportWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        String[][] words = GsonHelper.importFromJson(context);
        if(words!=null) {
            Data output = new Data.Builder()
                    .putStringArray(KEY_ORIGINALS, words[0])
                    .putStringArray(KEY_TRANSLATIONS, words[1])
                    .build();
            return Result.success(output);
        }
        else return Result.success();
    }
}
