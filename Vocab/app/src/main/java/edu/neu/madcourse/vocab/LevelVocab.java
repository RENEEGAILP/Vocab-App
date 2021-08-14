package edu.neu.madcourse.vocab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class LevelVocab extends AppCompatActivity {

    Button beginner, advance, expert;
    FirebaseFirestore firebaseFirestore;
    HashMap<String, String> wordPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_vocab);

        beginner = findViewById(R.id.beginner);
        advance = findViewById(R.id.advance);
        expert = findViewById(R.id.expert);

        firebaseFirestore = FirebaseFirestore.getInstance();
        wordPresent = new HashMap<>();

        beginner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LevelVocab.this, LearnVocab.class);
                intent.putExtra("Level", "Beginner");
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