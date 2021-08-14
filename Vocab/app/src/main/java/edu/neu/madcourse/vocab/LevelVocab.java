package edu.neu.madcourse.vocab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class LevelVocab extends AppCompatActivity {

    Button beginner, intermediate, advance, expert;
    FirebaseFirestore firebaseFirestore;
    HashMap<String, String> wordPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_vocab);

        beginner = findViewById(R.id.beginner);
        intermediate = findViewById(R.id.intermediate);
        advance = findViewById(R.id.advance);
        expert = findViewById(R.id.expert);

        firebaseFirestore = FirebaseFirestore.getInstance();
        wordPresent = new HashMap<>();

        Bundle bundle = getIntent().getExtras();
        String scoreStr = bundle.getString("score");

        int score = Integer.parseInt(scoreStr);
        if (score >= 0 && score <=2) {
            beginner.setEnabled(true);
            intermediate.setEnabled(false);
            advance.setEnabled(false);
            expert.setEnabled(false);
        }

        else if (score > 2 && score <=4) {
            beginner.setEnabled(true);
            intermediate.setEnabled(true);
            advance.setEnabled(false);
            expert.setEnabled(false);
        }

        else if (score > 4 && score <=6) {
            beginner.setEnabled(true);
            intermediate.setEnabled(true);
            advance.setEnabled(true);
            expert.setEnabled(false);
        }

        else {
            beginner.setEnabled(true);
            intermediate.setEnabled(true);
            advance.setEnabled(true);
            expert.setEnabled(true);
        }

        beginner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LevelVocab.this, LearnVocab.class);
                intent.putExtra("Level", "Beginner");
                startActivity(intent);

            }
        });

        intermediate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LevelVocab.this, LearnVocab.class);
                intent.putExtra("Level", "Intermediate");
                startActivity(intent);

            }
        });

        advance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LevelVocab.this, LearnVocab.class);
                intent.putExtra("Level", "Advance");
                startActivity(intent);
            }
        });

        expert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LevelVocab.this, LearnVocab.class);
                intent.putExtra("Level", "Expert");
                startActivity(intent);
            }
        });

    }
}