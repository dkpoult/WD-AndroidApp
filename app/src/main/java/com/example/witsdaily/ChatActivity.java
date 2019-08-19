package com.example.witsdaily;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ua.naiksoftware.stomp.dto.StompMessage;

public class ChatActivity extends AppCompatActivity {
    SocketAccessor newChatAccesor;
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
        //courseCode now has a specific type, ie tutor or normal
        String audience = i.getStringExtra("audience");
        courseCode += ":"+audience;
        newChatAccesor = new SocketAccessor(personNumber,userToken,courseCode,"CHAT") {
            @Override
            void onMessage(StompMessage topicMessage) {
               try{

                   JSONObject jsonMessage = new JSONObject(topicMessage.getPayload());
                   String messageType = jsonMessage.getString("messageType");
                   if (messageType.equals("CHAT")) {
                       String userType = jsonMessage.getString("tag");
                       String currentPersonNumber = jsonMessage.getString("personNumber");
                       int id = (int) jsonMessage.getLong("id");
                       addSingleMessage(jsonMessage.getString("content"), getCurrentTime(), userType, currentPersonNumber, id);
                   }
                   else if (messageType.equals("DELETE")){
                       int id = (int) jsonMessage.getLong("content");
                       deleteMessage(id);
                   }
               }catch (Exception e){

               }

            }
        };
        connected  = newChatAccesor.establishConnection();
        mainLayout = (LinearLayout)findViewById(R.id.chatLayout);
        params.topMargin = 10;

        addPreviousMessages();
    }

    private void addPreviousMessages(){
        StorageAccessor dataAccessor = new StorageAccessor(this,personNumber,userToken) {
            @Override
            public void getData(JSONObject data) {
                try {
                        JSONArray messages = data.getJSONArray("messages");
                        for (int i =0;i<messages.length();i++) {
                            int j = messages.length()-i-1;
                            JSONObject message = messages.getJSONObject(j);
                            String userType = message.getString("tag");
                            String currentPersonNumber = message.getString("personNumber");
                            int id = (int)message.getLong("id");
                            if (message.getString("personNumber").equals(personNumber)) {
                                addSingleMessage(message.getString("content"), message.getString("time"), "normal",currentPersonNumber,id);//fake normal
                            }else{
                                addSingleMessage(message.getString("content"), message.getString("time"),userType,currentPersonNumber,id);
                            }

                        }
                        deleteBadMessages();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


            }
        };
       dataAccessor.getChatTypeMessages(courseCode,"CHAT");

    }
    private void addSingleMessage(String message, String timeStamp,String userType,String pPersonNumber, int id) {
        View newMessage = getLayoutInflater().inflate(R.layout.chat_message, null);
        newMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                newChatAccesor.setStreamType(false);
                newChatAccesor.setMessageType("DELETE");
                newChatAccesor.sendMessage(String.valueOf(id));
                newChatAccesor.setStreamType(true);
                view.setVisibility(View.GONE);

                return false;
            }
        });
        TextView messageContents = newMessage.findViewById(R.id.tvMessageContents);
        TextView time = newMessage.findViewById(R.id.tvTime);
        TextView tvPersonNumber = newMessage.findViewById(R.id.tvPersonNumber);
        if (personNumber.equals(pPersonNumber))
            newMessage.setBackgroundResource(R.drawable.outgoing_message_bubble);
        else
            newMessage.setBackgroundResource(R.drawable.incoming_message_bubble);
        messageContents.setText(message);
        tvPersonNumber.setText(pPersonNumber);
        time.setText(timeStamp);
        newMessage.setLayoutParams(params);
        newMessage.setId(id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            switch (userType){
                //  case "tutor": newQuestion.setBackgroundColor(Color.rgb(181,218,240));break;//tutor
                // case "lecturer":newQuestion.setBackgroundColor(Color.rgb(245,172,172));break;//lecturer
                case "tutor": newMessage.setBackgroundTintList(this.getResources().getColorStateList(R.color.tutor));break;//tutor
                case "lecturer":newMessage.setBackgroundTintList(this.getResources().getColorStateList(R.color.lecturer));break;//lecturer
                case "normal":newMessage.setBackgroundTintList(this.getResources().getColorStateList(R.color.outgoing));break;//normal
            }

        }else{
            switch (userType){
                //  case "tutor": newQuestion.setBackgroundColor(Color.rgb(181,218,240));break;//tutor
                // case "lecturer":newQuestion.setBackgroundColor(Color.rgb(245,172,172));break;//lecturer
                case "tutor": newMessage.setBackgroundColor(Color.rgb(181,218,240));break;//tutor
                case "lecturer":newMessage.setBackgroundColor(Color.rgb(245,172,172));break;//lecturer
                case "normal":newMessage.setBackgroundColor(Color.rgb(220,248,198));break;//normal
            }

        }

        mainLayout.addView(newMessage);
    }
    public void clickSendMessage(View v){
        String message;
        ScrollView messageScroll = (ScrollView)findViewById(R.id.messageScroll);

        EditText edtMessage = (EditText)findViewById(R.id.edtMessage);
        if (edtMessage.getText().toString().equals("")){
            return;
        }
        message = edtMessage.getText().toString();
     //   addSingleMessage(message,getCurrentTime(),true,"normal",personNumber);
        if (connected){
            newChatAccesor.setMessageType("CHAT");
            newChatAccesor.sendMessage(message);
        }
        edtMessage.setText("");
        messageScroll.requestFocus();
        messageScroll.fullScroll(View.FOCUS_DOWN);
    }
    public String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(cal.getTime()) ;
    }
    @Override
    public void onBackPressed() {
        newChatAccesor.onDestroy();
        this.finish();
        return;

    }
    public void deleteBadMessages(){
        StorageAccessor dataAccessor = new StorageAccessor(this,personNumber,userToken) {
            @Override
            public void getData(JSONObject data) {
                try {
                    JSONArray messages = data.getJSONArray("messages"); // content score votes

                    System.out.println("");
                    for (int i =0;i<messages.length();i++) {
                        try {
                            JSONObject currentMessage = messages.getJSONObject(i);
                            int currentId = (int) currentMessage.getLong("content");
                            deleteMessage(currentId);
                        }
                        catch(Exception e){

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
        dataAccessor.getChatTypeMessages(courseCode,"DELETE");
    }
    public void deleteMessage(int id){
        LinearLayout mainLayout = findViewById(R.id.chatLayout);
        try {
            View messageObject = mainLayout.findViewById(id);
            mainLayout.removeView(messageObject);
        }catch(Exception e){
            System.out.println("probably deleted on user side already");
        }
    }
}
