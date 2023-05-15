package com.example.perfectenglish.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.perfectenglish.R;
import com.example.perfectenglish.util.IntentData;

import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {
    private int[] footers = {R.drawable.main_footer1, R.drawable.main_footer2, R.drawable.main_footer3, R.drawable.main_footer4, R.drawable.main_footer5};
    private int randomFooter = footers[ThreadLocalRandom.current().nextInt(0, footers.length)];
    private ImageView imageFooter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageFooter = findViewById(R.id.image_main_footer);

        if(savedInstanceState!=null) randomFooter = savedInstanceState.getInt(IntentData.KEY_RANDOM_FOOTER);

        imageFooter.setImageResource(randomFooter);

        LinearLayout linearTest = findViewById(R.id.linear_test);

        linearTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), TestSettingsActivity.class));
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(IntentData.KEY_RANDOM_FOOTER, randomFooter);
        super.onSaveInstanceState(outState);
    }
}