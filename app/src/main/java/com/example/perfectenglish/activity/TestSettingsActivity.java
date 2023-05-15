package com.example.perfectenglish.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.perfectenglish.R;
import com.example.perfectenglish.dialog.DownloadDialog;
import com.example.perfectenglish.model.Word;
import com.example.perfectenglish.repository.GsonHelper;
import com.example.perfectenglish.util.IntentData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TestSettingsActivity extends AppCompatActivity {
    private List<Word> words;

    private ListView listWords;
    private TextView textNoWords;

    private TextToSpeech tts;
    private boolean ttsError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.test_settings);

        DownloadDialog dialog = new DownloadDialog();
        dialog.show(getSupportFragmentManager(), "download");

        GsonHelper.importWords(this, this, new GsonHelper.ImportedWordsListener() {
            @Override
            public void onImported(String[] originals, String[] translations) {
                if(originals!=null && translations!=null) {
                    words = new ArrayList<>(originals.length);
                    for(int i = 0; i<originals.length;i++) {
                        words.add(new Word(originals[i], translations[i]));
                    }
                }
                else {
                    words = null;
                }
                dialog.dismiss();
                configureListView();
                configureButtonClickListeners();
                initTextToSpeech();
            }
        });

    }

    private void initTextToSpeech() {
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                ttsError = i==TextToSpeech.ERROR;
                if(i!=TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                }
                else Toast.makeText(TestSettingsActivity.this, R.string.unable_tts, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void configureButtonClickListeners() {
        FloatingActionButton buttonAddWord = findViewById(R.id.button_add_word);
        TextView textStartTest = findViewById(R.id.text_start_test);

        buttonAddWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startWordEditorActivityToCreateWord();
            }
        });

        textStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(words==null || words.size()==0) {
                    Toast.makeText(TestSettingsActivity.this, R.string.no_words_to_test, Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(TestSettingsActivity.this, TestActivity.class);
                    intent.putExtra(IntentData.KEY_WORDS, (ArrayList<Word>) words);
                    startActivity(intent);
                }
            }
        });
    }

    private void configureListView() {
        listWords = findViewById(R.id.list_words);
        textNoWords = findViewById(R.id.text_no_words);
        if(words==null) {
            listWords.setVisibility(View.INVISIBLE);
        }
        else {
            textNoWords.setVisibility(View.GONE);
            listWords.setAdapter(new WordAdapter(this, R.layout.item_word, words));
        }
    }

    private void startWordEditorActivityToCreateWord() {
        Intent intent = new Intent(this, WordEditorActivity.class);
        intent.putExtra(IntentData.KEY_MODE, WordEditorActivity.Mode.Create);
        startActivity(intent);
    }

    private void startWordEditorActivityToEditWord(String original, String translation, int position) {
        Intent intent = new Intent(this, WordEditorActivity.class);
        intent.putExtra(IntentData.KEY_MODE, WordEditorActivity.Mode.Edit);
        intent.putExtra(IntentData.KEY_ORIGINAL, original);
        intent.putExtra(IntentData.KEY_TRANSLATION, translation);
        intent.putExtra(IntentData.KEY_POSITION, position);
        startActivity(intent);
    }

    class WordAdapter extends ArrayAdapter<Word> {

        private int layout;
        private LayoutInflater inflater;

        public WordAdapter(@NonNull Context context, int resource, List<Word> words) {
            super(context, resource, words);
            this.layout = resource;
            this.inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = inflater.inflate(layout, parent, false);

            TextView textWordOriginal = view.findViewById(R.id.text_word_original);
            TextView textWordTranslation = view.findViewById(R.id.text_word_translation);
            ImageView imageWordDelete = view.findViewById(R.id.image_word_delete);
            ImageView imageWordEdit = view.findViewById(R.id.image_word_edit);
            ImageView imageWordSound = view.findViewById(R.id.image_word_sound);

            textWordOriginal.setText(words.get(position).getOriginal());
            textWordTranslation.setText(words.get(position).getTranslation());



            imageWordDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    words.remove(position);
                    DownloadDialog dialog = new DownloadDialog();
                    dialog.show(getSupportFragmentManager(), "download");
                    GsonHelper.exportWords(TestSettingsActivity.this, TestSettingsActivity.this, words, new GsonHelper.ExportedWordsListener() {
                        @Override
                        public void onExported() {
                            notifyDataSetChanged();
                            if(words.size()==0) {
                                words = null;
                                listWords.setVisibility(View.INVISIBLE);
                                textNoWords.setVisibility(View.VISIBLE);
                            }
                            dialog.dismiss();
                        }
                    });
                }
            });

            imageWordEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startWordEditorActivityToEditWord(words.get(position).getOriginal(), words.get(position).getTranslation(), position);
                }
            });

            imageWordSound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tts.speak(words.get(position).getOriginal(), TextToSpeech.QUEUE_FLUSH, null, words.get(position).getTranslation());
                }
            });


            return view;
        }
    }


}