package edu.neu.madcourse.vocab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class NavigationDrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private AlertDialog logoutConfirmDialog;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_navigation_drawer );

        drawerLayout = findViewById( R.id.navigation_drawer );
        toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        navigationView = findViewById( R.id.navigationView );
        navigationView.setNavigationItemSelectedListener( this );

        actionBarDrawerToggle = new ActionBarDrawerToggle( this, drawerLayout, toolbar, R.string.open, R.string.close );

        drawerLayout.addDrawerListener( actionBarDrawerToggle );
        actionBarDrawerToggle.setDrawerIndicatorEnabled( true );
        actionBarDrawerToggle.syncState();

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        setHeaderText();

        createLogoutConfirmDialog();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer( GravityCompat.START );
        switch (item.getItemId()) {
            case R.id.profile_menuitem:
                Intent profileIntent = new Intent( this, ProfileActivity.class );
                startActivity( profileIntent );
                return true;
            case R.id.history_menuitem:
                return true;
            case R.id.logout_menuitem:
                logoutConfirmDialog.show();
                return true;
        }
        return true;
    }

    protected void setHeaderText() {
        CollectionReference users = firestore.collection( "users" );
        DocumentReference docRef = users.document( firebaseAuth.getCurrentUser().getUid() );

        docRef.get().addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot userRef = task.getResult();
                    if (userRef.exists()) {
                        Map<String, Object> user_map = userRef.getData();

                        String name = user_map.get( getString( R.string.users_db_name ) ).toString();
                        NavigationView navigationView = findViewById( R.id.navigationView );
                        View headerView = navigationView.getHeaderView( 0 );
                        TextView headerName = (TextView) headerView.findViewById( R.id.drawerHeader_textView );
                        headerName.setText( name );

                    }
                } else {
                    Log.d( "Navigation Drawer", "Error getting document: ", task.getException() );
                }
            }
        } );

    }

    protected void setHeaderText(String headerText){
        NavigationView navigationView = findViewById( R.id.navigationView );
        View headerView = navigationView.getHeaderView( 0 );
        TextView headerName = (TextView) headerView.findViewById( R.id.drawerHeader_textView );
        headerName.setText( headerText );
    }

    private void createLogoutConfirmDialog() {
        AlertDialog.Builder logoutConfirmBuilder = new AlertDialog.Builder(this);
        View logoutConfirmView = getLayoutInflater().inflate(R.layout.logout_confirm_layout, null);
        logoutConfirmBuilder.setView(logoutConfirmView);
        logoutConfirmDialog = logoutConfirmBuilder.create();

        // get views
        Button logoutYesButton = logoutConfirmView.findViewById(R.id.logout_confirm_yes_button);
        Button logoutNoButton = logoutConfirmView.findViewById(R.id.logout_confirm_no_button);

        logoutYesButton.setOnClickListener(v -> logoutConfirmButtonYesOnClick());
        logoutNoButton.setOnClickListener(v -> logoutConfirmButtonNoOnClick());
    }

    private void logoutConfirmButtonYesOnClick()
    {
        firebaseAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void logoutConfirmButtonNoOnClick()
    {
        logoutConfirmDialog.hide();
    }

}