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
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import ua.naiksoftware.stomp.dto.StompMessage;

public class LiveQuestions extends AppCompatActivity {
    String userToken,personNumber,courseCode;
    SocketAccessor newQuestionAccessor,voteAccessor,deleteAccessor;
    int votedQuestion;
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
        newQuestionAccessor = new SocketAccessor(personNumber,userToken,courseCode,"LIVE_QUESTION",true) {
            @Override
            void onMessage(StompMessage topicMessage) {
                try{
                    JSONObject question = new JSONObject(topicMessage.getPayload());

                    String content = question.getString("content");
                    int score = question.getInt("score");
                    int voted = question.getInt("voted");
                    long id = question.getLong("id");
                    addSingleQuestion(content,score,voted,id);
                }catch (Exception e){

                }

            }
        };
        connected  = newQuestionAccessor.establishConnection();
        voteAccessor = new SocketAccessor(personNumber,userToken,courseCode,"LIVE_QUESTION_VOTE",true) {
            @Override
            void onMessage(StompMessage topicMessage) {
                try{

                    String[] values = topicMessage.getPayload().split("");
                    JSONObject question = new JSONObject();
                    question.put("id",Long.valueOf(values[0]));
                    question.put("score",Long.valueOf(values[1]));
                    receiveVoteChange(question);
                }catch (Exception e){

                }

            }
        };
        voteAccessor.establishConnection();
        deleteAccessor = new SocketAccessor(personNumber,userToken,courseCode,"DELETE",false) {
            @Override
            void onMessage(StompMessage topicMessage) {

            }
        };
        deleteAccessor.establishConnection();

        addPreviousQuestions();

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
        if (voted==1)
            votedQuestion = (int)id;
        newQuestion.setId((int)id);
        newQuestion.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) { // when deleting
                deleteAccessor.sendMessage(String.valueOf(id));
                view.setVisibility(View.GONE);

                return false;
            }
        });
        newQuestion.setTag(score);
        TextView questionContents = newQuestion.findViewById(R.id.tvQuestion);
        TextView totalScore = newQuestion.findViewById(R.id.tvScore);
        totalScore.setText(String.valueOf(score));
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
            newQuestionAccessor.sendMessage(question);
        }
        edtQuestion.setText("");
        messageScroll.requestFocus();
        messageScroll.fullScroll(View.FOCUS_DOWN);
    }

    public void clickQuestionVote(View v){
        View parent = (View)v.getParent().getParent().getParent();
        TextView totalScore = parent.findViewById(R.id.tvScore);
        LinearLayout mainLayout = findViewById(R.id.questionLayout);
        int sum = (Integer)parent.getTag();
        if (parent.getId()!=votedQuestion) {
            totalScore.setText("(" + (sum + 1) + ")");
            View previousAnswer = mainLayout.findViewById(votedQuestion);
            TextView previousText = previousAnswer.findViewById(R.id.tvScore);
            int oldSum = (Integer)previousAnswer.getTag();
            previousText.setText("(" + (oldSum -1) + ")");
            votedQuestion = parent.getId();
            // <id> <vote>
            String sendVote = String.valueOf(votedQuestion)+" 1";
            if (connected){
                voteAccessor.sendMessage(sendVote);
            }
        }
    }
    public void receiveVoteChange(JSONObject voteInfo){
        try{
            int id = (int)voteInfo.getLong("id");
            LinearLayout mainLayout = findViewById(R.id.questionLayout);
            View currentQuestion = mainLayout.findViewById(id);
            int score =  voteInfo.getInt("score");
            TextView previousVote = currentQuestion.findViewById(R.id.tvScore);
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
                    ViewGroup mainLayout = findViewById(R.id.questionLayout);
                    System.out.println("");
                    for (int i =0;i<messages.length();i++) {
                        try{
                            JSONObject currentMessage = messages.getJSONObject(i);
                            int currentId = (int)currentMessage.getLong("content");
                            View messageObject = mainLayout.findViewById(currentId);
                            mainLayout.removeView(messageObject);}
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
