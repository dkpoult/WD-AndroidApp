package com.example.witsdaily.Survey;

import android.content.Context;
import android.content.Intent;

import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.witsdaily.NetworkAccessor;
import com.example.witsdaily.R;
import com.example.witsdaily.StorageAccessor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class SurveyAnswer extends AppCompatActivity {
    String courseCode,userToken,personNumber,surveyType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_answer);
        Intent i = getIntent();
        courseCode = i.getStringExtra("courseCode");
        userToken = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        TextView tvCourseCode = (TextView)findViewById(R.id.tvCourseCode);
        tvCourseCode.setText(courseCode);
        getSurvey();
    }

    private void getSurvey(){
        StorageAccessor dataAccessor = new StorageAccessor(this,personNumber,userToken) {
            @Override
            public void getData(JSONObject data) {
                try {
                    if (data.getString("responseCode").equals("successful")){
                        JSONObject survey = data.getJSONObject("survey");
                        if (!survey.getBoolean("active")){
                            Toast.makeText(SurveyAnswer.this,"Survey concluded",
                                    Toast.LENGTH_SHORT).show();
                        }

                        TextView tvTitle = (TextView)findViewById(R.id.tvSurveyTitle);
                        String title = survey.getString("title");
                        tvTitle.setText(title);

                        surveyType = survey.getString("responseType");
                        switch (surveyType){

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
        dataAccessor.getSurvey(courseCode);
    }
    private void multipleChoice(JSONObject survey){

        RadioGroup rgOptions = findViewById(R.id.rgSurveyOptions);
        rgOptions.setVisibility(View.VISIBLE);
        try {
            JSONArray options = survey.getJSONArray("options");
            for (int i =0;i<options.length();i++){
                RadioButton newOption = new RadioButton(SurveyAnswer.this);
                newOption.setText(options.getString(i));
                rgOptions.addView(newOption);
            }
            ((RadioButton)rgOptions.getChildAt(0)).setChecked(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void textType(JSONObject survey){
        EditText edtTextValue = findViewById(R.id.edtTextValue);
        edtTextValue.setVisibility(View.VISIBLE);
    }
    private void numericalType(JSONObject survey){
        EditText edtNumerical = findViewById(R.id.edtNumerical);
        edtNumerical.setVisibility(View.VISIBLE);

    }

    public void clickSubmit(View v){
        StorageAccessor dataAccessor = new StorageAccessor(this,personNumber,userToken) {
            @Override
            public void getData(JSONObject data) {
                try {
                    Toast.makeText(SurveyAnswer.this,data.getString("responseCode"),
                            Toast.LENGTH_SHORT).show();
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        String answer = "";
        EditText edtNumerical = findViewById(R.id.edtNumerical);
        EditText edtTextValue = findViewById(R.id.edtTextValue);
        switch (surveyType){
            case "MC": answer = String.valueOf(getMCAnswer());break;
            case "TEXT" : answer = edtTextValue.getText().toString();break;
            case "NUMERIC" : answer = edtNumerical.getText().toString(); break;
        }
        if (answer.equals("")){
            Toast.makeText(SurveyAnswer.this,"Please enter an answer",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        dataAccessor.sendAnswer(answer,courseCode,surveyType);

    }
    private int getMCAnswer(){
        RadioGroup rgOptions = findViewById(R.id.rgSurveyOptions);
        RadioButton selected = rgOptions.findViewById(rgOptions.getCheckedRadioButtonId());
        return rgOptions.indexOfChild(selected);

    }
}
