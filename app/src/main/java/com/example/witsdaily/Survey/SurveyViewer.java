package com.example.witsdaily.Survey;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.witsdaily.R;
import com.example.witsdaily.StorageAccessor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SurveyViewer extends AppCompatActivity {
    String courseCode,userToken,personNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_viewer);
        Intent i = getIntent();
        courseCode = i.getStringExtra("courseCode");
        userToken = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        TextView tvCourseCode = (TextView)findViewById(R.id.tvCourseCode);
        tvCourseCode.setText(courseCode);
        getSurveyResults();

    }

    private void getSurveyResults(){
        StorageAccessor dataAccessor = new StorageAccessor(this,personNumber,userToken) {
            @Override
            public void getData(JSONObject data) {
                try {
                    if (data.getString("responseCode").equals("successful")){
                        JSONObject survey = data.getJSONObject("survey");
                        if (!survey.getBoolean("active")){
                            Toast.makeText(SurveyViewer.this,"Survey concluded",
                                    Toast.LENGTH_SHORT).show();
                        }

                        TextView tvTitle = (TextView)findViewById(R.id.tvSurveyTitle);
                        String title = survey.getString("title");
                        tvTitle.setText(title);

                        switch (survey.getString("responseType")){
                            case "MC": multipleChoice(survey);break;
                            case "TEXT" : textType(survey);break;
                            case "NUMERIC" : numericalType(survey); break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        dataAccessor.getSurveyResults(courseCode);
    }
    private void multipleChoice(JSONObject survey){
        try {

            JSONArray options  = survey.getJSONArray("options");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void textType(JSONObject survey){
        try {
            LinearLayout answerLayout = (LinearLayout)findViewById(R.id.llOptions);
            JSONArray options  = survey.getJSONArray("options");
            for (int i =0;i<options.length();i++){
                TextView oneOption = new TextView(SurveyViewer.this);
                oneOption.setText(options.getString(i));
                oneOption.setTextSize(15);
                answerLayout.addView(oneOption);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void numericalType(JSONObject survey){
        try {
            LinearLayout answerLayout = (LinearLayout)findViewById(R.id.llOptions);
            JSONArray options  = survey.getJSONArray("options");
            for (int i =0;i<options.length();i++){
                TextView oneOption = new TextView(SurveyViewer.this);
                oneOption.setText(String.valueOf(options.getDouble(i)));
                oneOption.setTextSize(15);
                answerLayout.addView(oneOption);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void clickOkay(View v){
        finish();
    }
}
