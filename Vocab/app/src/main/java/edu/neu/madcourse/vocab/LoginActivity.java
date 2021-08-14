package edu.neu.madcourse.vocab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.messaging.FirebaseMessaging;


public class LoginActivity extends AppCompatActivity {

    EditText m_emailId;
    EditText m_password;
    Button m_loginButton;
    FirebaseAuth m_Auth;
    String user_score;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        m_emailId = findViewById( R.id.login_email_editText );
        m_password = findViewById( R.id.login_password_editText );
        m_loginButton = findViewById( R.id.login_complete_button );
        m_loginButton.setOnClickListener( this::onLoginButtonClick );
        m_Auth = FirebaseAuth.getInstance();

        CollectionReference users;
        db = FirebaseFirestore.getInstance();
        users = db.collection("users");

        FirebaseUser user = m_Auth.getCurrentUser();
        if (user != null) {
            helper(user, users);
        }

        ConstraintLayout loginLayout = findViewById( R.id.login_layout );
        loginLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // hide keyboard
                try{
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    return false;
                }catch (Exception e){
                    return false;
                }

            }
        });
    }

    public void onLoginButtonClick(View view)
    {
        //On Login button click, verify the details entered with the database.
        //If Login successful, go to the home page
        //Else, show error

        String email_id = m_emailId.getText().toString();
        String password = m_password.getText().toString();

        if(TextUtils.isEmpty( email_id ))
        {
            m_emailId.setError( "Email is required" );
            return;
        }
        if(TextUtils.isEmpty( password ))
        {
            m_password.setError( "Password is required" );
            return;
        }

        m_Auth.signInWithEmailAndPassword( email_id,password ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Log.d( "Login","signInWithEmailAndPassword:success");
                    FirebaseUser user = m_Auth.getCurrentUser();

                    if(email_id.equals( "admin@vocab.com" ))
                    {
                        Intent intent = new Intent(LoginActivity.this,AdminActivity.class);
                        startActivity( intent );
                        finish();
                    }

                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener( new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (task.isSuccessful()){
                                String refreshToken = task.getResult();
                                Log.d("REFRESH TOKEN:", "Retrieved refresh token: " + refreshToken);
                                db.collection( "users" ).document(user.getUid()).update("device_token", refreshToken);

                            }else{
                                Log.d("REFRESH TOKEN:", "Error in retrieving refresh token");
                            }
                        }
                    });

                    CollectionReference users;
                    db = FirebaseFirestore.getInstance();
                    //String user_score;
                    users = db.collection("users");
                    //assert user != null;

                    helper(user, users);

                }
                else {
                    Log.w("Login", "signInWithEmailAndPassword:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        } );

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
                            Intent intent = new Intent(LoginActivity.this, QuizLandingActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(LoginActivity.this, LevelVocab.class);
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