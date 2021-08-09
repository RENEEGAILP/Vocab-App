package edu.neu.madcourse.vocab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private Button tButton;
    private Button fButton;
    private Button mNextButton;
    private TextView questionTextView;
    private int mCurrentIndex = 0;

    private Questions[] mQuestionBank = new Questions[]{
            new Questions(R.string.question1, true),
            new Questions(R.string.question2, false),
            new Questions(R.string.question3, true),
            new Questions(R.string.question4, false)
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
        mNextButton = (Button) findViewById(R.id.nextButton);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentIndex == 3) {
                    Toast.makeText(QuizActivity.this, "Quiz Completed!!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                    System.out.println(mCurrentIndex);
                    nextQuestion();
                }
            }
        });
        nextQuestion();
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
            //increase the score
            Toast.makeText(QuizActivity.this, "Correct Answer", Toast.LENGTH_SHORT).show();
            tButton.setClickable(false);
            fButton.setClickable(false);
        }else{
            Toast.makeText(QuizActivity.this, "Incorrect Answer", Toast.LENGTH_SHORT).show();
            tButton.setClickable(false);
            fButton.setClickable(false);
        }
    }


}