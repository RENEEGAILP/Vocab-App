package edu.neu.madcourse.vocab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

public class NavigationDrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    protected DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;

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

        actionBarDrawerToggle = new ActionBarDrawerToggle( this,drawerLayout,toolbar, R.string.open, R.string.close );

        drawerLayout.addDrawerListener( actionBarDrawerToggle );
        actionBarDrawerToggle.setDrawerIndicatorEnabled( true );
        actionBarDrawerToggle.syncState();

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        setHeaderText();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull  MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.profile_menuitem:
                return true;
            case R.id.history_menuitem:
                return true;
        }
        return true;
    }

    protected void setHeaderText()
    {
        CollectionReference users = firestore.collection( "users" );
        DocumentReference docRef = users.document(firebaseAuth.getCurrentUser().getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot userRef = task.getResult();
                    if(userRef.exists()){
                        Map<String, Object> user_map = userRef.getData();

                        String name =  user_map.get("Name").toString();
                        NavigationView navigationView = findViewById(R.id.navigationView);
                        View headerView = navigationView.getHeaderView(0);
                        TextView headerName = (TextView) headerView.findViewById(R.id.drawerHeader_textView);
                        headerName.setText( name );

                    }
                }else{
                    Log.d("Navigation Drawer", "Error getting document: ", task.getException());
                }
            }
        });

    }
}