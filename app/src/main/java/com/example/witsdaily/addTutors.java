package com.example.witsdaily;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class addTutors extends AppCompatActivity {
    String courseCodeString;
    String forumCode;
    String user_token, personNumber;
    NetworkAccessor NA;
    HashMap<String, Long> permTypes = new HashMap<>();
    String[] list = {"Course", "Forum", "Both"};
    String[] list2 = {"Lecturer", "Student", "Tutor"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tutors);
        Intent i = getIntent();
        courseCodeString = i.getStringExtra("courseCode");
        forumCode= i.getStringExtra("forumCode");
        System.out.println(courseCodeString);
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        NA = new NetworkAccessor(this, personNumber, user_token) {
            @Override
            void getResponse(JSONObject data) {
                System.out.println(data.toString());
                try {
                    String s = data.getString("responseCode");
                    if (s.equals("successful")) {
                        JSONArray r = data.getJSONArray("permissions");
                        for (int i = 0; i < r.length(); i++) {
                            JSONObject temp = r.getJSONObject(i);
                            permTypes.put(temp.getString("identifier"), temp.getLong("value"));
                        }
                    } else {
                        s = data.getString("responseCode");
                        switch (s) {
                            case "failed_unknown":
                                s = "Failed to get permissions";
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                                break;
                            case "failed_invalid_params":
                                s = "Failed to get permissions: " + data.getString("responseCode");
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                                break;
                            case "failed_missing_params":
                                s = "Failed to get permissions: " + data.getString("responseCode");
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                                break;
                            case "failed_missing_perms":
                                s = "Failed to get permissions: " + data.getString("responseCode");
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                        }

                    }

                    doSetup();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        NA.getPermissionCodes();

    }

    public void doSetup() {
        LinearLayout mainLayout = findViewById(R.id.inflatable);
        View LLayout = getLayoutInflater().inflate(R.layout.permission_setter, mainLayout, false);
        Spinner lItems = LLayout.findViewById(R.id.permissionArea);
        Spinner lItems1 = LLayout.findViewById(R.id.permissionType);
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (addTutors.this, android.R.layout.select_dialog_item, list);
//        lItems.setThreshold(1); //will start working from first character
        lItems.setAdapter(adapter);
        adapter = new ArrayAdapter<>
                (addTutors.this, android.R.layout.select_dialog_item, list2);
        lItems1.setAdapter(adapter);
        mainLayout.addView(LLayout);
    }

    public void changePerm(View v) {
        LinearLayout mainLayout = findViewById(R.id.inflatable);
        View LLayout = mainLayout.getChildAt(0);
        EditText et = LLayout.findViewById(R.id.targetPersonNumber);
        Spinner type = LLayout.findViewById(R.id.permissionType);
        Spinner area = LLayout.findViewById(R.id.permissionArea);
        String targetPersonNumber = et.getText().toString();
        String pType = type.getSelectedItem().toString();
        String pArea = area.getSelectedItem().toString();
        NA = new NetworkAccessor(this, personNumber, user_token) {
            @Override
            void getResponse(JSONObject data) {
                try {
                    String s = data.getString("responseCode");
                    System.out.println(s);
                    switch (s) {
                        case "successful":
                            s = "Successful: ";
                            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                            break;

                        case "failed_unknown":
                            s = "Failed to change permissions";
                            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                            break;
                        case "failed_invalid_params":
                            s = "Failed to change permissions: " + data.getString("responseCode");
                            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                            break;
                        case "failed_missing_params":
                            s = "Failed to change permissions: " + data.getString("responseCode");
                            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                            break;
                        case "failed_missing_perms":
                            s = "Failed to change permissions: " + data.getString("responseCode");
                            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        String context = "";
        long perm = 0;
        if (targetPersonNumber.isEmpty()) {
            et.setText((R.string.error_pNumb_not_set_addTutors));
            et.setTextColor(Color.RED);
        }
        switch (pType) {
            case "Lecturer":
                perm = 128 | 64 | 32 | 16 | 8 | 4 | 2 | 1;
                break;
            case "Tutor":
                perm = 128 | 64 |  1;
                break;
            case "Student":
                perm =  64 | 1;
                break;

        }
        System.out.println(perm);
        JSONObject p = new JSONObject();
        switch (pArea) {
            case "Forum":
                context += "f"+ forumCode;
                try {
                    p.put("targetPersonNumber", targetPersonNumber);
                    p.put("contextCode", context);
                    p.put("permissions", perm);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                NA.serPermissions(p);
                break;
            case "Course":
                context += "c"+ courseCodeString;
                try {
                    p.put("targetPersonNumber", targetPersonNumber);
                    p.put("contextCode", context);
                    p.put("permissions", perm);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                NA.serPermissions(p);
                break;
            case "Both":
                context = "c" + courseCodeString;
                try {
                    p.put("targetPersonNumber", targetPersonNumber);
                    p.put("contextCode", context);
                    p.put("permissions", perm);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                NA.serPermissions(p);
                context = "";
                context += "f"+ forumCode;
                try {
                    p.remove("contextCode");
                    p.put("contextCode", context);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                NA.serPermissions(p);
                break;

        }
    }
}
