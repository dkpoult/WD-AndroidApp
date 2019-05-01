package com.example.witsdaily;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class SurveyDialog extends AppCompatActivity {
    String courseCode,userToken,personNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_dialog);
        Intent i = getIntent();
        courseCode = i.getStringExtra("courseCode");
        userToken = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        TextView tvCourseCode = (TextView)findViewById(R.id.tvCourseCode);
        tvCourseCode.setText(courseCode);

    }

    private void getSurvey(){
        StorageAccessor dataAccessor = new StorageAccessor(this,personNumber,userToken) {
            @Override
            void getData(JSONObject data) {
                try {
                    if (data.getString("responseCode").equals("successful")){
                        JSONObject survey = data.getJSONObject("survey");
                        if (!survey.getBoolean("active")){
                            Toast.makeText(SurveyDialog.this,"Survey concluded",
                                    Toast.LENGTH_SHORT).show();
                        }
                        /*{title: string, list: options, responseType: string, active: boolean}.
                        ResponseTypes can be MC (Multiple Choice),
                         TEXT (Free text responses) or NUMERIC (Numeric only responses).*/

                        switch (survey.getString("responseType")){
                            case "MC": multipleChoice(survey);break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        dataAccessor.getSurvey(courseCode);
    }
    private void multipleChoice(JSONObject survey){
        try {
            TextView tvTitle = (TextView)findViewById(R.id.tvSurveyTitle);
            String title = survey.getString("title");
            tvTitle.setText(title);
            JSONArray options  = survey.getJSONArray("options");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
