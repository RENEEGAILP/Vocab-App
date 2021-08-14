package edu.neu.madcourse.vocab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LearnVocab extends AppCompatActivity {

    TextView word, definition;
    FirebaseFirestore firebaseFirestore;
    HashMap<String, String> wordPresent;
    Button next, previous;
    int currWord = 0;
    FirebaseAuth m_Auth;
    String user_score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_vocab);

        word = findViewById(R.id.word);
        definition = findViewById(R.id.definition);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        firebaseFirestore = FirebaseFirestore.getInstance();
        wordPresent = new HashMap<>();
        m_Auth = FirebaseAuth.getInstance();
        FirebaseUser user = m_Auth.getCurrentUser();

        Bundle bundle = getIntent().getExtras();
        String level = bundle.getString("Level");

        if (level.equals("Beginner")) {
            practiceWords(1);
        }
        else if (level.equals("Advance")) {
            practiceWords(2);
        }
        else {
            practiceWords(3);
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currWord == wordPresent.size()-1) {
                    Toast.makeText(LearnVocab.this, level + " Completed!!", Toast.LENGTH_SHORT).show();
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
        firebaseFirestore.collection("words").get()
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