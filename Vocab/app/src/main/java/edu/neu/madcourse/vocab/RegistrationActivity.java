package edu.neu.madcourse.vocab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;

public class RegistrationActivity extends AppCompatActivity {

    private EditText m_name, m_emailID,m_password;
    private ImageView m_imageView;
    private Button m_registerButton;
    private ProgressBar m_progressBar;

    // image dialog
    private View m_imageUploadView;
    private ImageView m_cameraImage;
    private ImageView m_galleryImage;
    private AlertDialog m_alertDialog;

    private FirebaseAuth mAuth;
    private FirebaseFirestore m_firestore;

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int GET_FROM_GALLERY = 2;

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

        createItemInputDialog();
    }

    public void onProfileImageClick(View view) {
        m_alertDialog.show();
    }

    private void createItemInputDialog() {
        AlertDialog.Builder itemDetailsBuilder = new AlertDialog.Builder(this);
        m_imageUploadView = getLayoutInflater().inflate(R.layout.image_upload, null);
        itemDetailsBuilder.setView(m_imageUploadView);
        m_alertDialog = itemDetailsBuilder.create();

        m_cameraImage = m_imageUploadView.findViewById(R.id.camera_imageView);
        m_galleryImage = m_imageUploadView.findViewById(R.id.gallery_imageView);

        m_cameraImage.setOnClickListener(v -> takePictureFromCamera());
        m_galleryImage.setOnClickListener(v -> uploadImage());
    }

    private void takePictureFromCamera(){
        if (hasCamera()){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } catch (ActivityNotFoundException e) {
                // display error state to the user
                Snackbar.make(findViewById( R.id.layout ), "Could not capture image. Please try to upload!",
                        Snackbar.LENGTH_LONG).show();
            }
        }
        else{
             Snackbar.make(findViewById( R.id.layout ), "No Camera found. Please try to upload!",
                    Snackbar.LENGTH_LONG).show();
        }
        m_alertDialog.hide();
    }

    private void uploadImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent,GET_FROM_GALLERY);
        m_alertDialog.hide();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Image taken from camera
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            m_imageView.setImageBitmap(imageBitmap);
        }

        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                m_imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                //Snackbar.make(mRecyclerView, "Could not upload image. Please try to add image!",
                //        Snackbar.LENGTH_LONG).show();
                //TODO : Error handling
                e.printStackTrace();
            }
        }
    }



    public void onRegisterButtonClick(View view) {
        String email_id = m_emailID.getText().toString();
        String password = m_password.getText().toString();
        String name = m_name.getText().toString();
        BitmapDrawable drawable = (BitmapDrawable) m_imageView.getDrawable();
        Bitmap profile_image = drawable.getBitmap();

        if (TextUtils.isEmpty(name))
        {
            m_name.setError("Full name is required");
            return;
        }
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
        if (password.length() < 6)
        {
            m_password.setError("Password must be at least 6 characters");
            return;
        }

        m_progressBar.setVisibility( View.VISIBLE );

        mAuth.createUserWithEmailAndPassword( email_id, password )
                .addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Log.d( "Register","createUserWithEmail:success");
                            Users user = new Users( name,email_id,profile_image );
                            user.createUser();
                            m_progressBar.setVisibility( View.INVISIBLE );
                            Snackbar.make(view, "Successfully created user! Please login.", Snackbar.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            m_progressBar.setVisibility( View.INVISIBLE );
                            Log.w("Register", "createUserWithEmail:failure", task.getException());
                            Snackbar.make(view, "Error in registration: " + task.getException().getMessage(),
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }
                } );
    }

    private boolean hasCamera(){
        PackageManager pm = this.getPackageManager();
        boolean hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
        if(hasCamera){
            return true;
        }
        else {
            Log.w("Camera", "No camera found!");
            return false;
        }
    }
}