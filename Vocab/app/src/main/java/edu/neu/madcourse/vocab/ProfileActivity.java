package edu.neu.madcourse.vocab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ProfileActivity extends NavigationDrawer {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    TextView name,emailId, joinedDate,score;
    ImageView profileImage;

    TextView beginnerLevel,advancedLevel,intermediateLevel,expertLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_profile, null, false);
        drawerLayout.addView(contentView, 0);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Button editProfileButton = findViewById( R.id.edit_account_button );
        editProfileButton.setOnClickListener( this::onEditButtonClick );

        initializeViewItems();
        //initializeUser();

    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeUser();
    }

    void initializeViewItems()
    {
        name = findViewById( R.id.name_account_textView );
        emailId = findViewById( R.id.emailid_account_textView );
        joinedDate = findViewById( R.id.joiningdate_textView );
        score = findViewById( R.id.score_value_textView );

        profileImage = findViewById( R.id.profile_account_imageView );

        beginnerLevel = findViewById( R.id.beginner_textView );
        advancedLevel = findViewById( R.id.advanced_textView );
        intermediateLevel = findViewById( R.id.intermediate_textView );
        expertLevel = findViewById( R.id.expert_textView );

    }

    void initializeUser()
    {
        CollectionReference users = firestore.collection( "users" );
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        DocumentReference docRef = users.document(currentUser.getUid());

        joinedDate.setText( "Joined " + new SimpleDateFormat("MMMM YYYY").
                format(new Date(currentUser.getMetadata().getCreationTimestamp())) );


        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot userRef = task.getResult();
                    if(userRef.exists()){
                        Map<String, Object> user_map = userRef.getData();

                        name.setText( user_map.get(getString(R.string.users_db_name)).toString() );
                        emailId.setText( user_map.get( getString(R.string.users_db_email) ).toString() );
                        String scoreVal = user_map.get( getString(R.string.users_db_score) ).toString();
                        if(scoreVal.equals( "-1" )) {
                            score.setText( "0" );
                        }
                        else {
                            score.setText( scoreVal );
                        }


                        Bitmap bitmap = Users.getDecodedBitmapFromString(
                                user_map.get( getString(R.string.users_db_profile) ).toString() );
                        profileImage.setImageBitmap( bitmap );

                        boolean level = (boolean)user_map.get( getString(R.string.users_db_beginner) );
                        if(level == true )
                        {
                            beginnerLevel.setAlpha( 1.0f );
                        }
                        level = (boolean)user_map.get( getString(R.string.users_db_intermediate) );
                        if(level ==true)
                        {
                            intermediateLevel.setAlpha( 1.0f );
                        }
                        level = (boolean)user_map.get( getString(R.string.users_db_advanced) );
                        if(level ==true)
                        {
                            advancedLevel.setAlpha( 1.0f );
                        }
                        level = (boolean)user_map.get( getString(R.string.users_db_expert) );
                        if(level ==true)
                        {
                            expertLevel.setAlpha( 1.0f );
                        }

                    }
                }else{
                    Log.d("View Profile", "Error getting document: ", task.getException());
                }
            }
        });
    }

    void onEditButtonClick(View view)
    {
        Intent editProfileIntent = new Intent(this,EditProfileActivity.class);
        startActivity( editProfileIntent );
    }
}

