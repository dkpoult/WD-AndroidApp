package com.example.witsdaily.Forum;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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
                String likeValue;
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                TextView likeCount = (TextView)group.findViewById(R.id.tvLikeCount);
                String likeCountString = likeCount.getText().toString();

                likeCountString = likeCountString.substring(1,likeCountString.length()-1);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked)
                {
                    likeValue = String.valueOf(checkedRadioButton.getTag());
                    if (likeValue.equals("1")){
                        likeCountString = String.valueOf(Integer.valueOf(likeCountString)+1);
                    }
                    else
                    {
                        likeCountString = String.valueOf(Integer.valueOf(likeCountString)-1);
                    }
                    likeCountString = "("+likeCountString+")";
                }
                else{
                    likeValue = "0";
                }
                likeCount.setText(likeCountString);
                StorageAccessor dataAccessor = new StorageAccessor(applicationContext,personNumber,user_token) {
                    @Override
                    public void getData(JSONObject data) {

                    }
                };

                LinearLayout mainView = (LinearLayout)rgLikes.getParent().getParent();
                String postCode = String.valueOf((mainView.getTag()));
                dataAccessor.makeVote(postCode,likeValue);
            }
        });
    }
}
