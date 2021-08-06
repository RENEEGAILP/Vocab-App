package edu.neu.madcourse.vocab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddWordActivity extends AppCompatActivity {

    EditText m_word;
    EditText m_definition;
    Spinner m_level;
    FirebaseFirestore m_firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_word );

        Button addButton = findViewById( R.id.add_word_complete_button );
        addButton.setOnClickListener( this::onAddButtonClick );

        m_word = findViewById( R.id.word_editText );
        m_definition = findViewById( R.id.definition_editText );
        m_level = findViewById(R.id.spinner);

        m_firestore = FirebaseFirestore.getInstance();

        setupSpinner();


    }

    private void setupSpinner()
    {
        String[] arraySpinner = new String[] {"1", "2", "3", "4"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_level.setAdapter(adapter);

    }
    public void onAddButtonClick(View view)
    {
        String word = m_word.getText().toString();
        String definition = m_definition.getText().toString();
        String level = m_level.getSelectedItem().toString();

        createWordEntry( word ,definition ,level );

    }

    private void createWordEntry(String word, String definition, String level)
    {
        DocumentReference documentReference= m_firestore.collection( "words" ).document(word);
        Map<String,Object> wordObj = new HashMap<>();
        wordObj.put( "Definition", definition );
        wordObj.put( "Level", level );

        documentReference.set( wordObj ).addOnSuccessListener( new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d( "Add word","Word added :  " + word);
            }
        } )
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Add word", "Failed");
                    }
                });
    }

}