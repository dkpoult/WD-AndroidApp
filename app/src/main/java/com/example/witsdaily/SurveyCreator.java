package com.example.witsdaily;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SurveyCreator extends AppCompatActivity {
    String courseCode,userToken,personNumber;
    RadioGroup rgOptions;
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
        rgOptions = (RadioGroup)findViewById(R.id.rgSurveyType);
        rgOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                LinearLayout mcqLayout = (LinearLayout)findViewById(R.id.llMCQ);
                int id = rgOptions.getCheckedRadioButtonId();
                mcqLayout.setVisibility(View.GONE);
                switch (id){
                    case R.id.rbMC: setMultipleChoice();break; // then its mcq
                    case R.id.rbText: setTextType();break; // text
                    case R.id.rbNumeric: setNumericType();break; // numeric
                }
            }
        });

    }

    private void setMultipleChoice(){
        LinearLayout mcqLayout = (LinearLayout)findViewById(R.id.llMCQ);
        mcqLayout.setVisibility(View.VISIBLE);
    }
    private void setTextType(){
      //  LinearLayout mcqLayout = (LinearLayout)findViewById(R.id.llMCQ);
      //  mcqLayout.setVisibility(View.VISIBLE);
    }
    private void setNumericType(){
        //LinearLayout mcqLayout = (LinearLayout)findViewById(R.id.llMCQ);
       // mcqLayout.setVisibility(View.VISIBLE);
    }
    public void clickRemoveOption(View v){ // will be enabled if there are options
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
        for (int i =0;i<rgOptions.getChildCount();i++){
            options.put(((RadioButton)rgOptions.getChildAt(i)).getText().toString());
        }
        EditText edtTitle = (EditText)findViewById(R.id.edtTitle);
        if (edtTitle.getText().toString().equals("")){
            Toast.makeText(SurveyCreator.this, "Please enter survey title",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        StorageAccessor dataAccessor = new StorageAccessor(this,personNumber,userToken) {
            @Override
            void getData(JSONObject data){
                try {
                    Toast.makeText(SurveyCreator.this, data.getString("responseCode"),
                            Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        dataAccessor.makeSurvey(courseCode,edtTitle.getText().toString(),options);
    }
}
