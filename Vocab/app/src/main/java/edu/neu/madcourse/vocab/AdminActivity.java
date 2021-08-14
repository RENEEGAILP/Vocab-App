package edu.neu.madcourse.vocab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AdminActivity extends AppCompatActivity {

    Button notifyUsersButton;
    EditText notificationMessageEditText;
    Button addWordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_admin );

        addWordButton = findViewById( R.id.addword_button );
        addWordButton.setOnClickListener( this::onAddButtonClick );

        notifyUsersButton = findViewById(R.id.notify_users_custom_messageButton);
        notifyUsersButton.setOnClickListener(this::onNotifyUserButtonClick);

        notificationMessageEditText = findViewById(R.id.notification_messageEdittext);

    }

    public void onAddButtonClick(View view)
    {
        Intent intent = new Intent(this,AddWordActivity.class);
        startActivity( intent );
    }

    public void onNotifyUserButtonClick(View view){
        String notificationMessage = notificationMessageEditText.getText().toString().trim();
        if (TextUtils.isEmpty(notificationMessage))
        {
            notificationMessageEditText.setError("Notification message cannot be empty.");
            return;
        }

        NotificationService.sendNotificationToAllUsers(notificationMessage);
    }
}