package com.example.perfectenglish.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.perfectenglish.R;
import com.example.perfectenglish.model.Word;
import com.example.perfectenglish.util.IntentData;

import java.util.ArrayList;

public class TestFragment extends Fragment {
    private String word;
    private EditText editEnterOriginal;

    public TestFragment() {

    }

    public TestFragment(String word) {
        this.word = word;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        TextView textWordTranslation = view.findViewById(R.id.text_word_translation);
        textWordTranslation.setText(word);

        editEnterOriginal = view.findViewById(R.id.edit_enter_original);
        return view;
    }



    public EditText getEditEnterOriginal() {
        return editEnterOriginal;
    }
}