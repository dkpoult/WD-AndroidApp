package com.example.witsdaily.Survey;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.witsdaily.LoginActivity;
import com.example.witsdaily.R;
import com.example.witsdaily.StorageAccessor;
import com.example.witsdaily.UserRegistration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SurveyCreator extends AppCompatActivity {
    String courseCode,userToken,personNumber,surveyType;
    RadioGroup rgSurveyType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_creator);
        userToken = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        Intent i = getIntent();
        courseCode = i.getStringExtra("courseCode");
        Button btnRemoveOption = (Button)findViewById(R.id.btnRemoveSelected);
        Button sendSurvey = (Button)findViewById(R.id.btnSendSurvey);
        TextView tvCourseCode = (TextView)findViewById(R.id.tvCourseCode);
        tvCourseCode.setText(courseCode);
        btnRemoveOption.setEnabled(false);
        sendSurvey.setEnabled(false);
        surveyType = "MC";
        rgSurveyType = (RadioGroup)findViewById(R.id.rgSurveyType);
        rgSurveyType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                LinearLayout mcqLayout = (LinearLayout)findViewById(R.id.llMCQ);
                int id = rgSurveyType.getCheckedRadioButtonId();
                mcqLayout.setVisibility(View.GONE);
                switch (id){
                    case R.id.rbMC: surveyType = "MC";setMultipleChoice();break; // then its mcq
                    case R.id.rbText: surveyType = "TEXT";setTextType();break; // text
                    case R.id.rbNumeric: surveyType = "NUMERIC";setNumericType();break; // numeric
                }
            }
        });

    }

    private void setMultipleChoice(){
        LinearLayout mcqLayout = (LinearLayout)findViewById(R.id.llMCQ);
        RadioGroup rgOptions = findViewById(R.id.rgOptions);
        mcqLayout.setVisibility(View.VISIBLE);
        Button sendSurvey = (Button)findViewById(R.id.btnSendSurvey);
      
        if (rgOptions.getChildCount()==0){
            sendSurvey.setEnabled(false);
        }
        else{
            sendSurvey.setEnabled(true);
        }
    }
    private void setTextType(){
      //  LinearLayout mcqLayout = (LinearLayout)findViewById(R.id.llMCQ);
      //  mcqLayout.setVisibility(View.VISIBLE);
        Button sendSurvey = (Button)findViewById(R.id.btnSendSurvey);
        sendSurvey.setEnabled(true);
    }
    private void setNumericType(){
        //LinearLayout mcqLayout = (LinearLayout)findViewById(R.id.llMCQ);
       // mcqLayout.setVisibility(View.VISIBLE);
        Button sendSurvey = (Button)findViewById(R.id.btnSendSurvey);
        sendSurvey.setEnabled(true);
    }
    public void clickRemoveOption(View v){ // will be enabled if there are options
        RadioGroup rgOptions = findViewById(R.id.rgOptions);
        int id = rgOptions.getCheckedRadioButtonId();

        rgOptions.removeView(rgOptions.findViewById(id));
        if (rgOptions.getChildCount()<1){
            Button btnRemoveOption = (Button)findViewById(R.id.btnRemoveSelected);
            btnRemoveOption.setEnabled(false);
            Button sendSurvey = (Button)findViewById(R.id.btnSendSurvey);
            sendSurvey.setEnabled(false);
        }
        else{
            ((RadioButton)rgOptions.getChildAt(0)).setChecked(true);
        }

    }
    public void clickAddOption(View v){

        RadioGroup rgOptions = (RadioGroup)findViewById(R.id.rgOptions);
        EditText edtOptionText = (EditText)findViewById(R.id.edtOption);
        if (edtOptionText.getText().toString().equals(""))
            return;
        RadioButton newOption = new RadioButton(SurveyCreator.this);
        newOption.setText(edtOptionText.getText().toString());
        rgOptions.addView(newOption);
        edtOptionText.setText("");
        Button btnRemoveOption = (Button)findViewById(R.id.btnRemoveSelected); // these commands just enable buttons to ensure no errors
        btnRemoveOption.setEnabled(true);
        Button sendSurvey = (Button)findViewById(R.id.btnSendSurvey);
        sendSurvey.setEnabled(true);
        ((RadioButton)rgOptions.getChildAt(0)).setChecked(true);

    }
    public void clickSendSurvey(View v){
        JSONArray options = new JSONArray();
        RadioGroup rgOptions = (RadioGroup)findViewById(R.id.rgOptions);
        if (surveyType.equals("MC")){
            for (int i =0;i<rgOptions.getChildCount();i++){
                options.put(((RadioButton)rgOptions.getChildAt(i)).getText().toString());
            }
        }
        EditText edtTitle = (EditText)findViewById(R.id.edtTitle);
        if (edtTitle.getText().toString().equals("")){
            Toast.makeText(SurveyCreator.this, "Please enter survey title",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        StorageAccessor dataAccessor = new StorageAccessor(this,personNumber,userToken) {
            @Override
            public void getData(JSONObject data){
                try {
                    Toast.makeText(SurveyCreator.this, data.getString("responseCode"),
                            Toast.LENGTH_SHORT).show();
                    if (data.getString("responseCode").equals("successful")){
                        surveyInProgress();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        dataAccessor.makeSurvey(courseCode,edtTitle.getText().toString(),options,surveyType);
    }
    private void surveyInProgress(){
        TextView tvProgress = (TextView)findViewById(R.id.tvProgress);
        ProgressBar pbProgress = (ProgressBar)findViewById(R.id.pbInProgress);
        tvProgress.setVisibility(View.VISIBLE);
        pbProgress.setVisibility(View.VISIBLE);
        Button endSurvey = (Button)findViewById(R.id.btnEndSurvey);
        endSurvey.setEnabled(true);
    }
    public void clickEndSurvey(View v){

        StorageAccessor dataAccessor = new StorageAccessor(this,personNumber,userToken) {
            @Override
            public void getData(JSONObject data){
                try {
                    Toast.makeText(SurveyCreator.this, data.getString("responseCode"),
                            Toast.LENGTH_SHORT).show();
                    if (data.getString("responseCode").equals("successful")){
                        TextView tvProgress = (TextView)findViewById(R.id.tvProgress);
                        ProgressBar pbProgress = (ProgressBar)findViewById(R.id.pbInProgress);
                        tvProgress.setVisibility(View.GONE);
                        pbProgress.setVisibility(View.GONE);
                       //  v.setEnabled(false);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        dataAccessor.closeSurvey(courseCode);
        Intent i = new Intent(SurveyCreator.this, SurveyViewer.class);
        i.putExtra("courseCode",courseCode);
        startActivity(i);

    }

}
