package edu.neu.madcourse.vocab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LearnVocab extends NavigationDrawer {

    TextView word, definition;
    HashMap<String, String> wordPresent;
    Button next, previous;
    int currWord = 0;
    FirebaseAuth m_Auth;
    FirebaseUser user;
    FirebaseFirestore m_firestore;
    String flag;
    String level;
    String userLevel;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_learn_vocab, null, false);
        drawerLayout.addView(contentView, 0);

        word = findViewById(R.id.word);
        definition = findViewById(R.id.definition);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        wordPresent = new HashMap<>();

        m_firestore = FirebaseFirestore.getInstance();
        m_Auth = FirebaseAuth.getInstance();
        user = m_Auth.getCurrentUser();

        if(user == null) {
            finish();
        }

        Bundle bundle = getIntent().getExtras();
        level = bundle.getString("Level");

        CollectionReference users;
        db = FirebaseFirestore.getInstance();
        users = db.collection("users");

        helper(user, users);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currWord == wordPresent.size()-1) {
                    Toast.makeText(LearnVocab.this, level + " Completed!!", Toast.LENGTH_SHORT).show();
                    updateLevel(level);
                    flag =  ""+level.charAt(0)+currWord;
                    finish();
                } else {
                    currWord = (currWord + 1) % wordPresent.size();
                    showWord();
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currWord == 0) {
                    Toast.makeText(LearnVocab.this, "No previous Question", Toast.LENGTH_SHORT).show();
                } else {
                    currWord = (currWord - 1) % wordPresent.size();
                    showWord();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        flag =  ""+level.charAt(0)+currWord;
        m_firestore.collection("users")
                .document(user.getUid())
                .update(level+"Status", flag);
    }

    private void helper(FirebaseUser user, CollectionReference users) {
        DocumentReference docRef = users.document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot userRef = task.getResult();
                    if(userRef.exists()) {
                        userLevel = String.valueOf(userRef.get(level+"Status"));
                        currWord = Integer.parseInt(userLevel.substring(1));
                        checkLevel();
                    }
                }
            }
        });
    }

    private void checkLevel() {
        if (level.equals("Beginner")) {
            practiceWords(1);
        }
        else if (level.equals("Intermediate")) {
            practiceWords(2);
        }
        else if (level.equals("Advanced")) {
            practiceWords(3);
        }
        else {
            practiceWords(4);
        }
    }

    private void updateLevel(String level) {
        m_firestore.collection("users")
                .document(user.getUid())
                .update(level, true);
    }

    private void showWord(){
        String wordVal = "";
        int i = 0;
        for (Map.Entry<String,String> entry : wordPresent.entrySet()) {
            if (i==currWord) {
                wordVal = entry.getKey();
                break;
            }
            i++;
        }
        word.setText(wordVal);
        definition.setText(wordPresent.get(wordVal));
    }

    private void practiceWords(int levelNum) {
        m_firestore.collection("words").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            assert querySnapshot != null;
                            if (!querySnapshot.isEmpty()) {
                                List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                                for (DocumentSnapshot d : documents) {
                                    if (Integer.parseInt((String) Objects.requireNonNull(d.get("Level"))) == levelNum) {
                                        wordPresent.put(d.getId(), (String) d.get("Definition"));
                                    }
                                }
                                showWord();
                            }
                        }
                    }
                });
    }
}