package edu.neu.madcourse.vocab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        Button loginButton = findViewById( R.id.login_button );
        Button registerButton = findViewById( R.id.register_button );

        loginButton.setOnClickListener( this::onLoginClick );
        registerButton.setOnClickListener( this::onRegisterClick );
    }

    public void onLoginClick(View view)
    {
        //Launch Login Activity
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity( intent );
    }

    public void onRegisterClick(View view)
    {
        //Launch registration activity for new user
        Intent intent = new Intent(this,RegistrationActivity.class);
        startActivity( intent );
    }

}