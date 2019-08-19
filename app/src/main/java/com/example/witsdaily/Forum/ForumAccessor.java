package com.example.witsdaily.Forum;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.witsdaily.HomeScreen;
import com.example.witsdaily.R;
import com.example.witsdaily.StorageAccessor;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class ForumAccessor {
    String personNumber,user_token;
    Context applicationContext;
    ForumAccessor(Context pAppContext, String pPersonNumber,String pUserToken){
        applicationContext = pAppContext;
        personNumber = pPersonNumber;
        user_token = pUserToken;
    }
    public void setLikeButtons(RadioGroup rgLikes){
        rgLikes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                int likeValue = 0;
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                TextView likeCount = (TextView)group.findViewById(R.id.tvLikeCount);
                Integer totalLikes = 0;
                try {
                    totalLikes =  Integer.parseInt(String.valueOf(likeCount.getTag()));
                }catch (Exception e){
                    // this happens when likes == null
                    System.out.println("");
                }
                String currentLikes = String.valueOf(totalLikes);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked)
                {

                        likeValue = Integer.parseInt(String.valueOf(checkedRadioButton.getTag()));

                    if (likeValue==1){
                        currentLikes = String.valueOf(totalLikes+1);
                    }
                    else
                    {
                        currentLikes = String.valueOf(totalLikes-1);
                    }
                    currentLikes = "("+currentLikes+")";
                }
                else{
                   // currentLikes = "0";
                }
                likeCount.setText(currentLikes);
                StorageAccessor dataAccessor = new StorageAccessor(applicationContext,personNumber,user_token) {
                    @Override
                    public void getData(JSONObject data) {

                    }
                };

                LinearLayout mainView = (LinearLayout)rgLikes.getParent().getParent();
                String postCode = String.valueOf((mainView.getTag()));
                dataAccessor.makeVote(postCode,String.valueOf(likeValue));
            }
        });
    }
    public void clickForumMarked(String v){

    }
}
