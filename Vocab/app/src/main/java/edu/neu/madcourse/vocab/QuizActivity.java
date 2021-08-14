package edu.neu.madcourse.vocab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class QuizActivity extends AppCompatActivity {

    private Button tButton;
    private Button fButton;
    private Button mNextButton;
    private TextView questionTextView;
    private int mCurrentIndex = 0;
    private int score=0;
    FirebaseAuth m_auth;
    FirebaseUser user;

    private Questions[] mQuestionBank = new Questions[]{
            new Questions(R.string.question1, true),
            new Questions(R.string.question2, true),
            new Questions(R.string.question3, false),
            new Questions(R.string.question4, true),
            new Questions(R.string.question5, true),
            new Questions(R.string.question6, true),
            new Questions(R.string.question7, false),
            new Questions(R.string.question8, true)
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionTextView = (TextView) findViewById(R.id.questions);
        tButton = (Button) findViewById(R.id.trueButton);
        tButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });
        fButton = (Button) findViewById(R.id.falseButton);
        fButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });
        nextQuestion();
    }

    private void updatescore() {
        FirebaseFirestore m_firestore = FirebaseFirestore.getInstance();
        m_auth = FirebaseAuth.getInstance();
        user = m_auth.getCurrentUser();
        m_firestore.collection("users")
                .document(user.getUid())
                .update("Score", score);

    }

    private void nextQuestion(){
        int question = mQuestionBank[mCurrentIndex].getQuestionId();
        questionTextView.setText(question);
        tButton.setClickable(true);
        fButton.setClickable(true);
    }
    private void checkAnswer(boolean userPress){
        boolean answerTrue = mQuestionBank[mCurrentIndex].isAnswer();
        if (userPress == answerTrue){
            score = score+1;
            Toast.makeText(QuizActivity.this, "Correct Answer", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(QuizActivity.this, "Incorrect Answer", Toast.LENGTH_SHORT).show();
        }

        if (mCurrentIndex == 7) {
            updatescore();
            Toast.makeText(QuizActivity.this, "Quiz Completed!!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(QuizActivity.this,LevelVocab.class);
            intent.putExtra("score",score);
            startActivity( intent );
            finish();
        } else {
            mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
            System.out.println(mCurrentIndex);
            nextQuestion();
        }

    }


}