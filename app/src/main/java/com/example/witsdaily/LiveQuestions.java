package com.example.witsdaily;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Random;

import ua.naiksoftware.stomp.dto.StompMessage;

public class LiveQuestions extends AppCompatActivity {
    String userToken,personNumber,courseCode;
    SocketAccessor newQuestionAccessor;
    boolean connected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_questions);
        userToken = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber",null);
        Intent i = getIntent();
        courseCode = i.getStringExtra("courseCode");

        String audience = i.getStringExtra("audience");
        courseCode += ":"+audience;
        newQuestionAccessor = new SocketAccessor(personNumber,userToken,courseCode,"LIVE_QUESTION") {
            @Override
            void onMessage(StompMessage topicMessage) {
                try{

                    JSONObject question = new JSONObject(topicMessage.getPayload());
                    String messageType = question.getString("messageType");
                    if (messageType.equals("LIVE_QUESTION")) {

                        String content = question.getString("content");
                        int score = question.getInt("score");
                        int voted = question.getInt("voted");
                        long id = question.getLong("id");
                        addSingleQuestion(content, score, voted, id);
                    }
                    else if (messageType.equals("LIVE_QUESTION_VOTE")){
                        String[] values = (new JSONObject(topicMessage.getPayload())).getString("content").split(" ");
                        JSONObject questionInfo = new JSONObject();
                        questionInfo.put("id",Long.valueOf(values[0]));
                        questionInfo.put("score",Long.valueOf(values[1]));
                        receiveVote(questionInfo);
                    }
                    else if (messageType.equals("DELETE")){
                        String id = question.getString("content");
                        deleteMessage(Integer.valueOf(id));
                    }
                    // else might be chat and so on
                }catch (Exception e){

                }

            }
        };
        connected  = newQuestionAccessor.establishConnection();

        addPreviousQuestions();

    }

    public void deleteMessage(int id){
        LinearLayout mainLayout = findViewById(R.id.questionLayout);
        try {
            View messageObject = mainLayout.findViewById(id);
            mainLayout.removeView(messageObject);
        }catch(Exception e){
            System.out.println("probably deleted on user side already");
        }
    }

    public void addPreviousQuestions(){
        StorageAccessor dataAccessor = new StorageAccessor(this,personNumber,userToken) {
            @Override
            public void getData(JSONObject data) {
                try {
                    JSONArray messages = data.getJSONArray("messages"); // content score votes
                    for (int i =0;i<messages.length();i++) {

                        int j = messages.length()-i-1;

                        JSONObject question = messages.getJSONObject(j);
                        String content = question.getString("content");
                        int score = question.getInt("score");
                        int voted = question.getInt("voted");
                        long id = question.getLong("id");
                        addSingleQuestion(content,score,voted,id);

                    }
                    deleteBadMessages();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
        dataAccessor.getChatTypeMessages(courseCode,"LIVE_QUESTION");
    }
    public void addSingleQuestion(String content, int score, int voted,long id){
        LinearLayout mainLayout = (LinearLayout)findViewById(R.id.questionLayout);

        View newQuestion = getLayoutInflater().inflate(R.layout.live_question, null);
        CheckBox checkBox = (CheckBox)newQuestion.findViewById(R.id.btnUpvote);
        if (voted==1){

            checkBox.setChecked(true);
        }
        else{
            checkBox.setChecked(false);
        }
        newQuestion.setId((int)id);
        newQuestion.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) { // when deleting
                newQuestionAccessor.setStreamType(false);
                newQuestionAccessor.setMessageType("DELETE");
                newQuestionAccessor.sendMessage(String.valueOf(id));
                newQuestionAccessor.setStreamType(true);
                view.setVisibility(View.GONE);

                return false;
            }
        });
        newQuestion.setTag(score);
        TextView questionContents = newQuestion.findViewById(R.id.tvQuestion);
        TextView totalScore = newQuestion.findViewById(R.id.tvScore);
       // totalScore.setText("("+score+")");
       // int a = new Random().nextInt();
        totalScore.setText("("+voted+")");
        questionContents.setText(content);

        mainLayout.addView(newQuestion);
    }
    public void clickSendQuestion(View v){
        String question;
        ScrollView messageScroll = (ScrollView)findViewById(R.id.messageScroll);

        EditText edtQuestion = (EditText)findViewById(R.id.edtQuestion);
        if (edtQuestion.getText().toString().equals("")){
            return;
        }
        question = edtQuestion.getText().toString();
        if (connected){
            newQuestionAccessor.setMessageType("LIVE_QUESTION");
            newQuestionAccessor.sendMessage(question);
        }
        edtQuestion.setText("");
        messageScroll.requestFocus();
        messageScroll.fullScroll(View.FOCUS_DOWN);
    }

    public void clickQuestionVote(View v){
            newQuestionAccessor.setMessageType("LIVE_QUESTION_VOTE");
            View parent = (View)v.getParent().getParent().getParent();
            int id = parent.getId();
            String sendVote = String.valueOf(id);
            if (((CheckBox)v).isChecked()){
                sendVote += " 0";

            }else{
                sendVote += " 1";
            }

            if (connected){
                newQuestionAccessor.sendMessage(sendVote);
            }

    }
    public void receiveVote(JSONObject voteInfo){
        try{
            int id = (int)voteInfo.getLong("id");
            LinearLayout mainLayout = findViewById(R.id.questionLayout);
            View currentQuestion = mainLayout.findViewById(id);
            int score =  voteInfo.getInt("score");
            TextView previousVote = currentQuestion.findViewById(R.id.tvScore);
            currentQuestion.setTag(score);

            previousVote.setText("("+score+")");
        }catch (Exception e){

        }

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

}
