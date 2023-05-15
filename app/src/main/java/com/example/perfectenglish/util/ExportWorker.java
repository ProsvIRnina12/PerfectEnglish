package com.example.perfectenglish.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.perfectenglish.repository.GsonHelper;

public class ExportWorker extends Worker {
    public static final String KEY_ORIGINALS = "originals";
    public static final String KEY_TRANSLATIONS = "translations";
    private Context context;

    public ExportWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        String[] originals = getInputData().getStringArray(KEY_ORIGINALS);
        String[] translations = getInputData().getStringArray(KEY_TRANSLATIONS);
        GsonHelper.exportToJSON(context, originals, translations);
        return Result.success();
    }
}
