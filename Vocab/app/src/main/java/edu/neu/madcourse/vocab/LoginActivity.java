package edu.neu.madcourse.vocab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText m_emailId;
    EditText m_password;
    Button m_loginButton;
    FirebaseAuth m_Auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        m_emailId = findViewById( R.id.login_email_editText );
        m_password = findViewById( R.id.login_password_editText );
        m_loginButton = findViewById( R.id.login_complete_button );
        m_loginButton.setOnClickListener( this::onLoginButtonClick );
        m_Auth = FirebaseAuth.getInstance();
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
                    }

                    Intent intent = new Intent(LoginActivity.this,QuizLandingActivity.class);
                    startActivity( intent );


                }
                else {
                    Log.w("Login", "signInWithEmailAndPassword:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        } );

    }
}