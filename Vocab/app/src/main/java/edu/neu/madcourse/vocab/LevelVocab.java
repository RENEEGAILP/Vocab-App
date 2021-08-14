package edu.neu.madcourse.vocab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class LevelVocab extends NavigationDrawer {

    CardView beginner, intermediate, advance, expert;
    FirebaseFirestore firebaseFirestore;
    HashMap<String, String> wordPresent;
    String userLevel;
    FirebaseAuth m_Auth;
    FirebaseUser user;
    int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_level_vocab, null, false);
        drawerLayout.addView(contentView, 0);


        beginner = findViewById(R.id.beginner);
        intermediate = findViewById(R.id.intermediate);
        advance = findViewById(R.id.advance);
        expert = findViewById(R.id.expert);

        wordPresent = new HashMap<>();

        firebaseFirestore = FirebaseFirestore.getInstance();
        m_Auth = FirebaseAuth.getInstance();
        user = m_Auth.getCurrentUser();

        FirebaseFirestore db;
        CollectionReference users;
        db = FirebaseFirestore.getInstance();
        users = db.collection("users");

        if (user != null) {
            helper(user, users);
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
                intent.putExtra("Level", "Advanced");
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

    @Override
    protected void onStart() {
        super.onStart();

        firebaseFirestore = FirebaseFirestore.getInstance();
        m_Auth = FirebaseAuth.getInstance();
        user = m_Auth.getCurrentUser();

        FirebaseFirestore db;
        CollectionReference users;
        db = FirebaseFirestore.getInstance();
        users = db.collection("users");

        if (user != null) {
            helper(user, users);
        }

    }

    private void updateLevels() {
        if (flag == 0) {
            beginner.setEnabled(true);
            beginner.setCardBackgroundColor(Color.parseColor("#6495ed"));
            intermediate.setEnabled(false);
            intermediate.setCardBackgroundColor(Color.parseColor("#bebebe"));
            advance.setEnabled(false);
            advance.setCardBackgroundColor(Color.parseColor("#bebebe"));
            expert.setEnabled(false);
            expert.setCardBackgroundColor(Color.parseColor("#bebebe"));
        }
        if (flag == 1) {
            beginner.setEnabled(true);
            beginner.setCardBackgroundColor(Color.parseColor("#6495ed"));
            intermediate.setEnabled(true);
            intermediate.setCardBackgroundColor(Color.parseColor("#6495ed"));
            advance.setEnabled(false);
            advance.setCardBackgroundColor(Color.parseColor("#bebebe"));
            expert.setEnabled(false);
            expert.setCardBackgroundColor(Color.parseColor("#bebebe"));
        }
        else if (flag == 2) {
            beginner.setEnabled(true);
            beginner.setCardBackgroundColor(Color.parseColor("#6495ed"));
            intermediate.setEnabled(true);
            intermediate.setCardBackgroundColor(Color.parseColor("#6495ed"));
            advance.setEnabled(true);
            advance.setCardBackgroundColor(Color.parseColor("#6495ed"));
            expert.setEnabled(false);
            expert.setCardBackgroundColor(Color.parseColor("#bebebe"));
        }
        else if (flag == 3) {
            beginner.setEnabled(true);
            beginner.setCardBackgroundColor(Color.parseColor("#6495ed"));
            intermediate.setEnabled(true);
            intermediate.setCardBackgroundColor(Color.parseColor("#6495ed"));
            advance.setEnabled(true);
            advance.setCardBackgroundColor(Color.parseColor("#6495ed"));
            expert.setEnabled(true);
            expert.setCardBackgroundColor(Color.parseColor("#6495ed"));
        }
    }

    private void helper(FirebaseUser user, CollectionReference users) {

        DocumentReference docRef = users.document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot userRef = task.getResult();
                    if(userRef.exists()) {
                        userLevel = String.valueOf(userRef.get("Advanced"));
                        if (String.valueOf(userRef.get("Advanced")).equals("true")) {
                            flag = 3;
                        }
                        else if (String.valueOf(userRef.get("Intermediate")).equals("true")) {
                            flag = 2;
                        }
                        else if (String.valueOf(userRef.get("Beginner")).equals("true")) {
                            flag = 1;
                        }
                        else {
                            flag = 0;
                        }
                    }
                    updateLevels();
                }
            }
        });
    }

}