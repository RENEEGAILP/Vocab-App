package edu.neu.madcourse.vocab;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;


import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Users {

    private String m_name;
    private String m_emailId;
    private Bitmap m_profileImage;
    private long m_score;
    private boolean m_beginner;
    private boolean m_intermediate;
    private boolean m_expert;
    private boolean m_advanced;
    private String s_beginner;
    private String s_intermediate;
    private String s_expert;
    private String s_advanced;

    static private FirebaseFirestore m_firestore = FirebaseFirestore.getInstance();
    static private FirebaseAuth m_auth = FirebaseAuth.getInstance();


    public Users()
    {
        m_name = null;
        m_emailId = null;
        m_profileImage = null;
        m_score = -1;
        m_beginner = false;
        m_advanced = false;
        m_intermediate = false;
        m_expert = false;
        s_beginner = "";
        s_advanced = "";
        s_intermediate = "";
        s_expert = "";


    }

    public Users(String name, String emailId,Bitmap profileImage) {
        m_name = name;
        m_emailId = emailId;
        m_profileImage = profileImage;
        m_score = -1;
        m_beginner = false;
        m_advanced = false;
        m_intermediate = false;
        m_expert = false;
        s_beginner = "B0";
        s_advanced = "A0";
        s_intermediate = "I0";
        s_expert = "E0";
        
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
        userObj.put( "Score", m_score );
        userObj.put( "Beginner", m_beginner );
        userObj.put( "Intermediate", m_intermediate );
        userObj.put( "Advanced", m_advanced );
        userObj.put( "Expert", m_expert );
        userObj.put( "BeginnerStatus", s_beginner );
        userObj.put( "IntermediateStatus", s_intermediate );
        userObj.put( "AdvancedStatus", s_advanced );
        userObj.put( "ExpertStatus", s_expert );

        documentReference.set( userObj ).addOnSuccessListener( new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Log.d( "Create User","user Profile Created for " + user.getUid());
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()){
                            String refreshToken = task.getResult();
                            Log.d("REFRESH TOKEN:", "Retrieved refresh token: " + refreshToken);
                            m_firestore.collection( "users" ).document(user.getUid()).update("device_token", refreshToken);

                        }else{
                            Log.d("REFRESH TOKEN:", "Error in retrieving refresh token");
                        }
                    }
                });
            }
        } )
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Create User", "Failed");
            }
        });
    }

    static String getEncodedStringOfBitmap(Bitmap imageBitmap) {
        // First compress this image and then encode it
        int newHeight = (int) ( imageBitmap.getHeight() * (512.0 / imageBitmap.getWidth()) );
        Bitmap scaledImageBitmap = Bitmap.createScaledBitmap(imageBitmap, 512, newHeight, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scaledImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    static Bitmap getDecodedBitmapFromString(String imageEncoded) {
        byte[] decodedByteArray = Base64.decode(imageEncoded, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    static void updateUserInfo(String name, Bitmap profilePic)
    {
        FirebaseUser user = m_auth.getCurrentUser();
        DocumentReference documentReference= m_firestore.collection( "users" ).document(user.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot countRef = task.getResult();
                    if(countRef.exists()){
                        documentReference.update("Name",name );
                        documentReference.update( "ProfilePic", getEncodedStringOfBitmap(profilePic));

                    }
                }else{
                    Log.d("User Update : ", "Could not generate group code");
                }
            }
        });

    }


}
