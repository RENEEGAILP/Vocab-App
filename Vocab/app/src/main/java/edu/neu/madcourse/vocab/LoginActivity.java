package edu.neu.madcourse.vocab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    EditText m_userName;
    EditText m_password;
    Button m_loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        m_userName = findViewById( R.id.login_username_editText );
        m_password = findViewById( R.id.login_password_editText );
        m_loginButton = findViewById( R.id.login_button );
        m_loginButton.setOnClickListener( this::onLoginButtonClick );
    }

    public void onLoginButtonClick(View view)
    {
        //On Login button click, verify the details entered with the database.
        //If Login successful, go to the home page
        //Else, show error

        String username = m_userName.getText().toString();
        String password = m_password.getText().toString();

    }
}