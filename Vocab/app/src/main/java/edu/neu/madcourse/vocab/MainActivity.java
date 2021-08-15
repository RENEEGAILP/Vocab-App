package edu.neu.madcourse.vocab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    Button loginButton, registerButton;
    String user_score;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        loginButton = findViewById( R.id.login_button );
        registerButton = findViewById( R.id.register_button );

        loginButton.setOnClickListener( this::onLoginClick );
        registerButton.setOnClickListener( this::onRegisterClick );

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

    }

    public void onLoginClick(View view)
    {
        //Launch Login Activity
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity( intent );
        finish();
    }

    public void onRegisterClick(View view)
    {
        //Launch registration activity for new user
        Intent intent = new Intent(this,RegistrationActivity.class);
        startActivity( intent );
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user != null) {
            CollectionReference users;
            db = FirebaseFirestore.getInstance();
            users = db.collection("users");
            helper(user, users);

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
                        user_score = String.valueOf(userRef.get("Score"));


                        if (user_score.equals("-1")) {
                            Intent intent = new Intent(MainActivity.this, QuizLandingActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(MainActivity.this, LevelVocab.class);
                            intent.putExtra("score", user_score);
                            startActivity(intent);
                            finish();
                        }


                    }
                }
            }
        });
    }

}