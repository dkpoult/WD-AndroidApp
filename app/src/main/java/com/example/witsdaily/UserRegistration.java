package com.example.witsdaily;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class UserRegistration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
    }
    public void clickRegister(View v){
        EditText personID = (EditText)findViewById(R.id.edtPersonID);
        EditText personPassword = (EditText)findViewById(R.id.edtPassword);

        final String personIDValue = personID.getText().toString();
        final String passwordValue = personPassword.getText().toString();
    }

}
