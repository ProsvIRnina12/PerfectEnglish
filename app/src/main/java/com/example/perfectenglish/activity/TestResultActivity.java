package com.example.perfectenglish.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.perfectenglish.R;
import com.example.perfectenglish.model.Word;
import com.example.perfectenglish.util.IntentData;

import java.util.ArrayList;
import java.util.List;

public class TestResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.test_results);

        ArrayList<Word> words = getIntent().getExtras().getParcelableArrayList(IntentData.KEY_WORDS);
        ArrayList<Word> incorrectWords = getIntent().getExtras().getParcelableArrayList(IntentData.KEY_INCORRECT_WORDS);

        TextView textAmountCorrectWords = findViewById(R.id.text_amount_correct_words);
        ListView listIncorrectWords = findViewById(R.id.list_incorrect_words);

        textAmountCorrectWords.setText(String.format(getString(R.string.amount_correct_words), words.size()-incorrectWords.size(), words.size()));

        if (incorrectWords.size() > 0) {
            TextView textAllWordsCorrect = findViewById(R.id.text_label_all_words_correct);
            textAllWordsCorrect.setVisibility(View.GONE);

            listIncorrectWords.setAdapter(new IncorrectWordAdapter(this, R.layout.item_incorrect_word, incorrectWords));
        }
        else listIncorrectWords.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    class IncorrectWordAdapter extends ArrayAdapter<Word> {
        private LayoutInflater inflater;
        int layout;
        List<Word> incorrectWords;

        public IncorrectWordAdapter(@NonNull Context context, int resource, @NonNull List<Word> objects) {
            super(context, resource, objects);
            this.layout = resource;
            this.incorrectWords = objects;
            this.inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = inflater.inflate(layout, parent, false);

            TextView textWordOriginal = view.findViewById(R.id.text_word_original);
            TextView textWordTranslation = view.findViewById(R.id.text_word_translation);

            textWordOriginal.setText(incorrectWords.get(position).getOriginal());
            textWordTranslation.setText(incorrectWords.get(position).getTranslation());

            return view;
        }
    }
}