package com.example.perfectenglish.repository;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.perfectenglish.activity.TestSettingsActivity;
import com.example.perfectenglish.model.Word;
import com.example.perfectenglish.model.WordRoot;
import com.example.perfectenglish.util.ExportWorker;
import com.example.perfectenglish.util.ImportWorker;
import com.google.gson.Gson;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GsonHelper {
    private static String FILE_NAME = "words";

    public static void importWords(Context context, LifecycleOwner owner, ImportedWordsListener listener) {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(ImportWorker.class)
                .build();
        WorkManager.getInstance(context).getWorkInfoByIdLiveData(workRequest.getId())
                .observe(owner, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo.getState()== WorkInfo.State.SUCCEEDED) {
                            String[] originals = workInfo.getOutputData().getStringArray(ImportWorker.KEY_ORIGINALS);
                            String[] translations = workInfo.getOutputData().getStringArray(ImportWorker.KEY_TRANSLATIONS);
                            listener.onImported(originals, translations);
                        }
                    }
                });
        WorkManager.getInstance(context).enqueue(workRequest);
    }

    public static void importWordsAsyncTask(Context context, ImportedWordsListener listener) {
        class WordsTask extends AsyncTask<Context, Void, String[][]> {

            @Override
            protected String[][] doInBackground(Context... contexts) {
                return GsonHelper.importFromJson(contexts[0]);
            }

            @Override
            protected void onPostExecute(String[][] strings) {
                if(strings==null) {
                    listener.onImported(null, null);
                }
                else listener.onImported(strings[0], strings[1]);
            }
        }

        WordsTask task = new WordsTask();
        task.execute(context);
    }

    @Nullable
    public static String[][] importFromJson(Context context) {
        try {
            FileInputStream fileInputStream = context.openFileInput(FILE_NAME);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            Gson gson = new Gson();
            WordRoot wordRoot = gson.fromJson(inputStreamReader, WordRoot.class);
            if(wordRoot.words==null || wordRoot.words.length==0) return null;
            else {
                String[] originals = new String[wordRoot.words.length];
                String[] translations = new String[wordRoot.words.length];

                for(int i = 0; i<wordRoot.words.length; i++) {
                    originals[i] = wordRoot.words[i].getOriginal();
                    translations[i] = wordRoot.words[i].getTranslation();
                }
                String[][] result = new String[2][];
                result[0] = originals;
                result[1] = translations;
                return result;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void exportWords(Context context, LifecycleOwner owner, String[] originals, String[] translations, ExportedWordsListener listener) {
        Data inputData = new Data.Builder()
                .putStringArray(ExportWorker.KEY_ORIGINALS, originals)
                .putStringArray(ExportWorker.KEY_TRANSLATIONS, translations)
                .build();
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(ExportWorker.class)
                .setInputData(inputData)
                .build();
        WorkManager.getInstance(context).getWorkInfoByIdLiveData(workRequest.getId())
                .observe(owner, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo.getState()== WorkInfo.State.SUCCEEDED) {
                            listener.onExported();
                        }
                    }
                });
        WorkManager.getInstance(context).enqueue(workRequest);
    }

    public static void exportWords(Context context, LifecycleOwner owner, List<Word> words, ExportedWordsListener listener) {
        String[] originals = new String[words.size()];
        String[] translations = new String[words.size()];
        for(int i = 0; i<words.size(); i++) {
            originals[i] = words.get(i).getOriginal();
            translations[i] = words.get(i).getTranslation();
        }
        exportWords(context, owner, originals, translations, listener);
    }

    public static void exportToJSON(Context context, String[] originals, String[] translations) {
        Gson gson = new Gson();
        Word[] wordsArray = new Word[originals.length];
        for(int i = 0; i<originals.length;i++) {
            wordsArray[i] = new Word(originals[i], translations[i]);
        }
        WordRoot wordRoot = new WordRoot(wordsArray);
        String strJson = gson.toJson(wordRoot);

        try {
            FileOutputStream fileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fileOutputStream.write(strJson.getBytes());
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface ImportedWordsListener {
        void onImported(@Nullable String[] originals, @Nullable String[] translations);
    }

    public interface ExportedWordsListener {
        void onExported();
    }
}
