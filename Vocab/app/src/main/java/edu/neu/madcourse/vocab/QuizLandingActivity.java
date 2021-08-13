package edu.neu.madcourse.vocab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class QuizLandingActivity extends AppCompatActivity {

    Button quizButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_landing);
        quizButton = findViewById( R.id.takeQuiz );
    }



    public void onTakeQuiz(View view)
    {
        //Intent intent = getIntent();
        //String str = intent.getStringExtra("user_score");
        Intent intent1 = new Intent(this,QuizActivity.class);
        startActivity( intent1 );
        //intent1.putExtra("user_score",str);
        //startActivity(intent1);
        quizButton.setClickable(false);
    }

}