package com.example.perfectenglish.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.perfectenglish.R;
import com.example.perfectenglish.dialog.DownloadDialog;
import com.example.perfectenglish.model.Word;
import com.example.perfectenglish.repository.GsonHelper;
import com.example.perfectenglish.util.IntentData;

import java.util.ArrayList;
import java.util.List;

public class WordEditorActivity extends AppCompatActivity {
    private Mode mode;
    private String original, translation;
    private int position = -1;
    private EditText editWordOriginal, editWordTranslation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_editor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(mode==null) {
            mode = (Mode) getIntent().getExtras().getSerializable(IntentData.KEY_MODE);
            if(mode==Mode.Create) {
                original = "";
                translation = "";
            }
            if(mode==Mode.Edit) {
                original = getIntent().getExtras().getString(IntentData.KEY_ORIGINAL);
                translation = getIntent().getExtras().getString(IntentData.KEY_TRANSLATION);
                position = getIntent().getExtras().getInt(IntentData.KEY_POSITION);
            }
        }

        if(mode==Mode.Create) {
            setTitle(R.string.create_word);
        }
        if(mode==Mode.Edit) {
            setTitle(R.string.edit_word);
        }

        editWordOriginal = findViewById(R.id.edit_word_original);
        editWordTranslation = findViewById(R.id.edit_word_translation);
        editWordOriginal.setText(original);
        editWordTranslation.setText(translation);

        TextView textApply = findViewById(R.id.text_apply);
        textApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                original = editWordOriginal.getText().toString();
                translation = editWordTranslation.getText().toString();

                if(isInputCorrect(original, translation)) {
                    applyWordChanges(original, translation);
                }
                else Toast.makeText(WordEditorActivity.this, R.string.incorrect_input, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void applyWordChanges(String original, String translation) {
        DownloadDialog dialog = new DownloadDialog();
        dialog.show(getSupportFragmentManager(), "download");
        GsonHelper.importWords(WordEditorActivity.this, WordEditorActivity.this, new GsonHelper.ImportedWordsListener() {
            @Override
            public void onImported(@Nullable String[] originals, @Nullable String[] translations) {
                if(mode==Mode.Create) {
                    String[] newOriginals = null;
                    String[] newTranslations = null;
                    if(originals!=null && translations!=null) {
                        newOriginals = new String[originals.length+1];
                        newTranslations = new String[translations.length+1];
                        for(int i = 0; i<originals.length;i++) {
                            newOriginals[i] = originals[i];
                            newTranslations[i] = translations[i];
                        }
                        newOriginals[newOriginals.length-1] = original;
                        newTranslations[newTranslations.length-1] = translation;
                    }
                    else {
                        newOriginals = new String[1];
                        newTranslations = new String[1];
                        newOriginals[0] = original;
                        newTranslations[0] = translation;
                    }
                    GsonHelper.exportWords(WordEditorActivity.this, WordEditorActivity.this, newOriginals, newTranslations, new GsonHelper.ExportedWordsListener() {
                        @Override
                        public void onExported() {
                            dialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), TestSettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        }
                    });
                }
                if(mode==Mode.Edit) {
                    originals[position] = original;
                    translations[position] = translation;
                    GsonHelper.exportWords(WordEditorActivity.this, WordEditorActivity.this, originals, translations, new GsonHelper.ExportedWordsListener() {
                        @Override
                        public void onExported() {
                            dialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), TestSettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        }
                    });
                }
            }
        });
    }

    private boolean isInputCorrect(String original, String translation) {
        String originalAlphabet = "abcdefghijkmnlopqrstuvwxyzABCDEFGHIJKMNLOPQRSTUVWXYZ1234567890 ";
        if(original.length()>0 && translation.length()>0) {
            for(char ch : original.toCharArray()) {
                if(!originalAlphabet.contains(Character.toString(ch))) {
                    return false;
                }
            }
            return true;
        }
        else return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, TestSettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        original = editWordOriginal.getText().toString();
        translation = editWordTranslation.getText().toString();
        super.onSaveInstanceState(outState);
    }

    public enum Mode {
        Create, Edit
    }
}