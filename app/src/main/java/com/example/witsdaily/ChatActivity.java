package com.example.witsdaily;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ua.naiksoftware.stomp.dto.StompMessage;

public class ChatActivity extends AppCompatActivity {
    ChatAccessor newChatAccesor;
    boolean connected;
    LinearLayout mainLayout;
    LinearLayout qScroll;
    final LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
    String userToken, personNumber, courseCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        userToken = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        Intent i = getIntent();
        courseCode = i.getStringExtra("courseCode");
        newChatAccesor = new ChatAccessor(personNumber, userToken, courseCode) {
            @Override
            void onMessage(StompMessage topicMessage) {
                try {
                    JSONObject jsonMessage = new JSONObject(topicMessage.getPayload());
                    if (!jsonMessage.getString("personNumber").equals(personNumber))
                        addSingleMessage(jsonMessage.getString("content"), getCurrentTime(), false, true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
        connected = newChatAccesor.establishConnection();
        mainLayout = findViewById(R.id.chatLayout);
        qScroll = findViewById(R.id.qChat);
        qScroll.setVisibility(View.GONE);
        params.topMargin = 10;

        addPreviousMessages();
    }

    private void addPreviousMessages() {

        JSONArray messages = newChatAccesor.getPreviousMessages();
        for (int i = 0; i < messages.length(); i++) {
            try {
                addSingleMessage(messages.getString(i), "12:10", false, true);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void addSingleMessage(String message, String timeStamp, boolean outgoing, boolean isChat) {
        View newMessage;
        TextView messageContents;

        if (isChat) {
            newMessage = getLayoutInflater().inflate(R.layout.chat_message, null);
            newMessage.setOnLongClickListener(view -> {
                view.setVisibility(View.GONE);

                return false;
            });
            messageContents = newMessage.findViewById(R.id.tvMessageContents);
            TextView time = newMessage.findViewById(R.id.tvTime);
            if (outgoing)
                newMessage.setBackgroundResource(R.drawable.outgoing_message_bubble);
            else
                newMessage.setBackgroundResource(R.drawable.incoming_message_bubble);
            messageContents.setText(message);
            time.setText(timeStamp);
            newMessage.setLayoutParams(params);
            mainLayout.addView(newMessage);
        }else{
            newMessage = getLayoutInflater().inflate(R.layout.chatposts, null);
            newMessage.setOnLongClickListener(view -> {
                view.setVisibility(View.GONE);

                return false;
            });
            messageContents = newMessage.findViewById(R.id.qBox);
            messageContents.setText(message);
            qScroll.addView(newMessage);
        }
    }

    public void clickSendMessage(View v) {
        EditText edtMessage = findViewById(R.id.edtMessage);
        String message = edtMessage.getText().toString();
        if (message.equals("")) {
            return;
        }

        if(mainLayout.getVisibility() == View.GONE){
            addSingleMessage(message, getCurrentTime(), true, false);

        }else{
            addSingleMessage(message, getCurrentTime(), true, true);
        }
        if (connected) {
            if(mainLayout.getVisibility() == View.GONE) {
                newChatAccesor.sendMessage(message, "LIVE_QUESTION");
            }else{
                newChatAccesor.sendMessage(message, "CHAT");
            }
        }
        edtMessage.setText("");
    }

    public String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(cal.getTime());
    }

    @Override
    public void onBackPressed() {
        newChatAccesor.onDestroy();
        this.finish();

    }

    public void clickSwitch(View v) {
//        Switch wChat = findViewById(R.id.switchChat);
        qScroll.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.GONE);
    }

}
