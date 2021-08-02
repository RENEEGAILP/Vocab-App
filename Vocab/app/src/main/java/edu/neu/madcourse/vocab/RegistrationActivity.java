package edu.neu.madcourse.vocab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    EditText m_name, m_emailID,m_password;
    ImageView m_imageView;
    Button m_registerButton;
    ProgressBar m_progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore m_firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_registration );

        m_name = findViewById( R.id.name_register_editText );
        m_emailID = findViewById( R.id.emailid_register_editText );
        m_password = findViewById( R.id.password_register_password );
        m_imageView = findViewById( R.id.profile_register_imageView );
        m_registerButton = findViewById( R.id.register_complete_button );
        m_progressBar = findViewById( R.id.progressBar );

        m_progressBar.setVisibility( View.INVISIBLE );

        m_imageView.setOnClickListener( this::onProfileImageClick );
        m_registerButton.setOnClickListener( this::onRegisterButtonClick );

        mAuth = FirebaseAuth.getInstance();
        m_firestore = FirebaseFirestore.getInstance();
    }

    public void onProfileImageClick(View view) {

    }

    public void onRegisterButtonClick(View view) {
        String email_id = m_emailID.getText().toString();
        String password = m_password.getText().toString();
        String name = m_name.getText().toString();

        if(TextUtils.isEmpty( email_id ))
        {
            m_emailID.setError( "Email is required" );
            return;
        }
        if(TextUtils.isEmpty( password ))
        {
            m_password.setError( "Password is required" );
            return;
        }
        if(TextUtils.isDigitsOnly( password ) || TextUtils.isGraphic( password ) || password.length() < 8)
        {
            //m_password.setError( "Password must contain both letters and digits. Minimum length should be 8" );
            //return;
        }

        m_progressBar.setVisibility( View.VISIBLE );
        mAuth.createUserWithEmailAndPassword( email_id, password )
                .addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d( "Register","createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            m_progressBar.setVisibility( View.INVISIBLE );

                            DocumentReference documentReference= m_firestore.collection( "users" ).document(user.getUid());
                            Map<String,Object> userObj = new HashMap<>();
                            userObj.put( "Name", name );
                            userObj.put( "Email", email_id );

                            documentReference.set( userObj ).addOnSuccessListener( new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d( "Register","user Profile Created for " + user.getUid());
                                }
                            } );
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Register", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } );
    }
}