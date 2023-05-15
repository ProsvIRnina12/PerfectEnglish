package com.example.perfectenglish.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.perfectenglish.R;
import com.example.perfectenglish.fragment.TestFragment;
import com.example.perfectenglish.model.Word;
import com.example.perfectenglish.util.IntentData;

import java.util.ArrayList;
import java.util.Locale;

public class TestActivity extends AppCompatActivity {
    private ArrayList<Word> words;
    private int wordNumber = 0;
    private ArrayList<Word> incorrectWords;
    private TestFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        setTitle(R.string.test);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        words = getIntent().getExtras().getParcelableArrayList(IntentData.KEY_WORDS);

        if(savedInstanceState!=null) {
            wordNumber = savedInstanceState.getInt(IntentData.KEY_WORD_NUMBER);
            incorrectWords = savedInstanceState.getParcelableArrayList(IntentData.KEY_INCORRECT_WORDS);
        }
        else {
            incorrectWords = new ArrayList<>(words.size());
        }

        fragment = new TestFragment(words.get(wordNumber).getTranslation());
        getSupportFragmentManager().beginTransaction().add(R.id.frame_fragment_test, fragment)
                .commit();

        TextView textContinue = findViewById(R.id.text_continue);
        textContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answer = fragment.getEditEnterOriginal().getText().toString();
                if(! answer.toLowerCase(Locale.ROOT).equals(words.get(wordNumber).getOriginal().toLowerCase(Locale.ROOT))) {
                    incorrectWords.add(words.get(wordNumber));
                }

                wordNumber++;
                if(wordNumber==words.size()) {
                    startTestResultActivity();
                }
                else {
                    fragment = new TestFragment(words.get(wordNumber).getTranslation());
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragment_test, fragment)
                            .commit();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                showExitDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(IntentData.KEY_WORD_NUMBER, wordNumber);
        outState.putParcelableArrayList(IntentData.KEY_INCORRECT_WORDS, incorrectWords);
        super.onSaveInstanceState(outState);
    }

    private void startTestResultActivity() {
        Intent intent = new Intent(this, TestResultActivity.class);
        intent.putExtra(IntentData.KEY_WORDS, words);
        intent.putExtra(IntentData.KEY_INCORRECT_WORDS, incorrectWords);
        startActivity(intent);
    }

    private void showExitDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(TestActivity.this, MainActivity.class));
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setMessage(R.string.dialog_message_exit)
                .create();
        dialog.show();
    }
}