package edu.neu.madcourse.vocab;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;


public class QuizLandingActivity extends NavigationDrawer {

    Button quizButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_quiz_landing, null, false);
        drawerLayout.addView(contentView, 0);

        quizButton = findViewById( R.id.takeQuiz );
    }



    public void onTakeQuiz(View view)
    {
        Intent intent = new Intent(this,QuizActivity.class);
        startActivity( intent );
        quizButton.setClickable(false);
    }

}