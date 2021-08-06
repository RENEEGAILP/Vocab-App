package edu.neu.madcourse.vocab;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Users {
    private String m_name;
    private String m_emailId;
    private Bitmap m_profileImage;

    private FirebaseFirestore m_firestore;
    private FirebaseAuth m_auth;

    public Users(String name, String emailId,Bitmap profileImage) {
        m_name = name;
        m_emailId = emailId;
        m_profileImage = profileImage;

        m_firestore = FirebaseFirestore.getInstance();
        m_auth = FirebaseAuth.getInstance();
    }

    public void createUser()
    {
        FirebaseUser user = m_auth.getCurrentUser();
        DocumentReference documentReference= m_firestore.collection( "users" ).document(user.getUid());
        Map<String,Object> userObj = new HashMap<>();
        userObj.put( "Name", m_name );
        userObj.put( "Email", m_emailId );
        userObj.put("ProfilePic", getEncodedStringOfBitmap(m_profileImage));

        documentReference.set( userObj ).addOnSuccessListener( new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d( "Create User","user Profile Created for " + user.getUid());
            }
        } )
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Create User", "Failed");
            }
        });
    }

    private String getEncodedStringOfBitmap(Bitmap imageBitmap) {
        // First compress this image and then encode it
        int newHeight = (int) ( imageBitmap.getHeight() * (512.0 / imageBitmap.getWidth()) );
        Bitmap scaledImageBitmap = Bitmap.createScaledBitmap(imageBitmap, 512, newHeight, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scaledImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

}
