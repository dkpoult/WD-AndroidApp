package com.example.witsdaily;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ua.naiksoftware.stomp.dto.StompMessage;

public class ChatActivity extends AppCompatActivity {
    ChatAccessor newChatAccesor;
    boolean connected;
    LinearLayout mainLayout;
    final LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
    ViewGroup.LayoutParams.WRAP_CONTENT);
    String userToken,personNumber,courseCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        userToken = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber",null);
        Intent i = getIntent();
        courseCode = i.getStringExtra("courseCode");
        newChatAccesor = new ChatAccessor(personNumber,userToken,courseCode) {
            @Override
            void onMessage(StompMessage topicMessage) {
                addSingleMessage(topicMessage.getPayload(),getCurrentTime(),false);
            }
        };
        connected  = newChatAccesor.establishConnection();
        mainLayout = (LinearLayout)findViewById(R.id.chatLayout);
        params.topMargin = 10;

        addPreviousMessages();
    }

    private void addPreviousMessages(){

        JSONArray messages = newChatAccesor.getPreviousMessages();
        for (int i =0;i<messages.length();i++){
            try {
                addSingleMessage(messages.getString(i),"12:10",false);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    private void addSingleMessage(String message, String timeStamp,boolean outgoing) {
        View newMessage = getLayoutInflater().inflate(R.layout.chat_message, null);
        TextView messageContents = newMessage.findViewById(R.id.tvMessageContents);
        TextView time = newMessage.findViewById(R.id.tvTime);
        if (outgoing)
            newMessage.setBackgroundResource(R.drawable.outgoing_message_bubble);
        else
            newMessage.setBackgroundResource(R.drawable.incoming_message_bubble);
        messageContents.setText(message);
        time.setText(timeStamp);
        newMessage.setLayoutParams(params);
        mainLayout.addView(newMessage);
    }
    public void clickSendMessage(View v){
        String message;
        EditText edtMessage = (EditText)findViewById(R.id.edtMessage);
        if (edtMessage.getText().toString().equals("")){
            return;
        }
        message = edtMessage.getText().toString();
        addSingleMessage(message,getCurrentTime(),true);
        if (connected){
            newChatAccesor.sendMessage(message);
        }
        edtMessage.setText("");
    }
    public String getCurrentTime() {
        String currentTime = Calendar.getInstance().getTime().toString();
        return currentTime;
    }
    @Override
    public void onBackPressed() {
        newChatAccesor.onDestroy();
        this.finish();
        return;

    }
}
