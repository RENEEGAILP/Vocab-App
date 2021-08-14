package edu.neu.madcourse.vocab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    Button loginButton, registerButton;

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

        if (user != null) {
            registerButton.setVisibility(View.INVISIBLE);
            loginButton.setText("START");
        }

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


}