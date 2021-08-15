package edu.neu.madcourse.vocab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import static java.lang.Thread.sleep;

public class QuizActivity extends AppCompatActivity implements SensorEventListener {

    private Button tButton;
    private Button fButton;
    private TextView questionTextView;
    private ConstraintLayout quizCardLayout;
    private int mCurrentIndex = 0;
    private int score=0;
    FirebaseAuth m_auth;
    FirebaseUser user;
    FirebaseFirestore m_firestore;
    private static boolean flip = false;

    private final int RED_COLOR = Color.parseColor("#FF7043");
    private final int GREEN_COLOR = Color.parseColor("#8BC34A");
    private final int WHITE_COLOR = Color.parseColor("#FFFFFF");

    private SensorManager sensorManager;
    private Sensor sensor;



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
        m_firestore = FirebaseFirestore.getInstance();
        m_auth = FirebaseAuth.getInstance();
        user = m_auth.getCurrentUser();
        quizCardLayout = findViewById(R.id.quiz_card_layout);

        //declaring Sensor Manager and sensor type
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

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
        m_firestore.collection("users")
                .document(user.getUid())
                .update("Score", score);

    }

    private void nextQuestion(){
        int question = mQuestionBank[mCurrentIndex].getQuestionId();
        questionTextView.setText(question);
        quizCardLayout.setBackgroundColor(WHITE_COLOR);
        tButton.setClickable(true);
        fButton.setClickable(true);
    }
    private void checkAnswer(boolean userPress){
        boolean answerTrue = mQuestionBank[mCurrentIndex].isAnswer();
        if (userPress == answerTrue){
            score = score+1;
            quizCardLayout.setBackgroundColor(GREEN_COLOR);
            Toast.makeText(QuizActivity.this, "Correct Answer", Toast.LENGTH_SHORT).show();
        }else{
            quizCardLayout.setBackgroundColor(RED_COLOR);
            Toast.makeText(QuizActivity.this, "Incorrect Answer", Toast.LENGTH_SHORT).show();
        }

        if (mCurrentIndex == 7) {
            updatescore();
            //Toast.makeText(QuizActivity.this, "Quiz Completed!!", Toast.LENGTH_SHORT).show();
            checkScores();
            Intent intent = new Intent(QuizActivity.this,LevelVocab.class);
            intent.putExtra("score",score);
            startActivity( intent );
            finish();
        } else {
            mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
            final Handler handler = new Handler(Looper.getMainLooper());

            // display the next question after a 1 second delay
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nextQuestion();
                }
            }, 1000);

        }

    }

    private void checkScores() {
        if (score <= 2) {
            Toast.makeText(QuizActivity.this, "Well done! You have unlocked 1 level!!", Toast.LENGTH_SHORT).show();
        }

        if (score > 2 && score <=4) {
            m_firestore.collection("users")
                    .document(user.getUid())
                    .update("Beginner", true);
            Toast.makeText(QuizActivity.this, "Well done! You have unlocked 2 levels!!", Toast.LENGTH_SHORT).show();
        }

        else if (score > 4 && score <=6) {
            m_firestore.collection("users")
                    .document(user.getUid())
                    .update("Beginner", true);
            m_firestore.collection("users")
                    .document(user.getUid())
                    .update("Intermediate", true);
            Toast.makeText(QuizActivity.this, "Well done! You have unlocked 3 levels!!", Toast.LENGTH_SHORT).show();
        }

        else if (score > 6 && score <=8) {
            m_firestore.collection("users")
                    .document(user.getUid())
                    .update("Beginner", true);
            m_firestore.collection("users")
                    .document(user.getUid())
                    .update("Intermediate", true);
            m_firestore.collection("users")
                    .document(user.getUid())
                    .update("Advanced", true);
            Toast.makeText(QuizActivity.this, "Well done! You have unlocked 4 levels!!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        if (Math.abs(x) > Math.abs(y)) {
            if (x < -0.1) {
                if (flip){
                    checkAnswer(false);
                    flip = false;
                }
            }
            if (x > 0.1) {
                if(flip){
                    checkAnswer(true);
                    flip = false;
                }
            }
        }
//        else {
//            if (y < 0) {
//                flip = false;
//            }
//            if (y > 0) {
//                flip = false;
//            }
//        }
        if (x > (-1) && x < (1)
                //&& y > (-1) && y < (1)
        ) {
            flip = true;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregister Sensor listener
        sensorManager.unregisterListener(this);
    }

}