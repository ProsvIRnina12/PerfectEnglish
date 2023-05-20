package com.example.perfectenglish.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

import com.example.perfectenglish.R;
import com.example.perfectenglish.repository.GsonHelper;

import java.util.Random;

public class RandomWordWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        GsonHelper.importWordsAsyncTask(context, new GsonHelper.ImportedWordsListener() {
            @Override
            public void onImported(@Nullable String[] originals, @Nullable String[] translations) {
                if(originals!=null && translations!=null && originals.length>0 && translations.length>0) {
                    Random randomObj = new Random();
                    int randomWordIndex = randomObj.nextInt(originals.length);
                    for(int appWidgetId : appWidgetIds) {
                        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_random_word);

                        remoteViews.setViewVisibility(R.id.text_label_remember_random_word, View.VISIBLE);
                        remoteViews.setViewVisibility(R.id.text_random_word_original, View.VISIBLE);
                        remoteViews.setViewVisibility(R.id.text_random_word_translation, View.VISIBLE);
                        remoteViews.setViewVisibility(R.id.text_no_words_for_widget, View.GONE);

                        remoteViews.setCharSequence(R.id.text_random_word_original, "setText", originals[randomWordIndex]);
                        remoteViews.setCharSequence(R.id.text_random_word_translation, "setText", translations[randomWordIndex]);
                        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                    }
                }
                else {
                    for(int appWidgetId : appWidgetIds) {
                        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_random_word);
                        remoteViews.setViewVisibility(R.id.text_label_remember_random_word, View.GONE);
                        remoteViews.setViewVisibility(R.id.text_random_word_original, View.GONE);
                        remoteViews.setViewVisibility(R.id.text_random_word_translation, View.GONE);
                        remoteViews.setViewVisibility(R.id.text_no_words_for_widget, View.VISIBLE);
                        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                    }
                }
            }
        });
    }
}
