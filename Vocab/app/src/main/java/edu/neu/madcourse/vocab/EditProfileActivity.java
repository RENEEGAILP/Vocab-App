package edu.neu.madcourse.vocab;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class EditProfileActivity extends NavigationDrawer {

    private TextView name;
    private ImageView profilePic;
    private ProgressBar progressBar;

    private AlertDialog alertDialog;
    private View imageUploadView;
    private ImageView cameraImage;
    private ImageView galleryImage;

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int GET_FROM_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_edit_profile, null, false);
        drawerLayout.addView(contentView, 0);

        progressBar = findViewById( R.id.edit_account_progressBar );
        progressBar.setVisibility( View.INVISIBLE );

        name = findViewById( R.id.name_edit_account_editText );

        profilePic = findViewById( R.id.profile_edit_account_imageView );
        profilePic.setOnClickListener( this::onProfileImageClick );

        Button saveChanges = findViewById( R.id.edit_account_complete_button );
        saveChanges.setOnClickListener( this::onSaveChangesButtonClick );
        createItemInputDialog();
        initializeUserDetails();
    }

    void initializeUserDetails() {
        CollectionReference users = firestore.collection( "users" );
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        DocumentReference docRef = users.document(currentUser.getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot userRef = task.getResult();
                    if(userRef.exists()){
                        Map<String, Object> user_map = userRef.getData();

                        name.setText( user_map.get(getString(R.string.users_db_name)).toString() );

                        Bitmap bitmap = Users.getDecodedBitmapFromString(
                                user_map.get( getString(R.string.users_db_profile) ).toString() );
                        profilePic.setImageBitmap( bitmap );
                    }
                }else{
                    Log.d("Update Profile", "Error getting document: ", task.getException());
                }
            }
        });
    }

    void onSaveChangesButtonClick(View view)
    {
        progressBar.setVisibility( View.VISIBLE );
        String fullName =  name.getText().toString();
        BitmapDrawable drawable = (BitmapDrawable) profilePic.getDrawable();
        Bitmap profilePicBitmap = drawable.getBitmap();

        Users.updateUserInfo( fullName,profilePicBitmap );
        setHeaderText(fullName);

        progressBar.setVisibility( View.INVISIBLE );
        finish();

    }

    public void onProfileImageClick(View view) {
        alertDialog.show();
    }

    private void createItemInputDialog() {
        AlertDialog.Builder itemDetailsBuilder = new AlertDialog.Builder(this);
        imageUploadView = getLayoutInflater().inflate(R.layout.image_upload, null);
        itemDetailsBuilder.setView(imageUploadView);
        alertDialog = itemDetailsBuilder.create();

        cameraImage = imageUploadView.findViewById(R.id.camera_imageView);
        galleryImage = imageUploadView.findViewById(R.id.gallery_imageView);

        cameraImage.setOnClickListener(v -> takePictureFromCamera());
        galleryImage.setOnClickListener(v -> uploadImage());
    }

    private void takePictureFromCamera(){
        if (hasCamera()){
            Intent takePictureIntent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
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
        alertDialog.hide();
    }

    private void uploadImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent,GET_FROM_GALLERY);
        alertDialog.hide();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Image taken from camera
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            profilePic.setImageBitmap(imageBitmap);
        }

        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                profilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                //Snackbar.make(mRecyclerView, "Could not upload image. Please try to add image!",
                //        Snackbar.LENGTH_LONG).show();
                //TODO : Error handling
                e.printStackTrace();
            }
        }
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