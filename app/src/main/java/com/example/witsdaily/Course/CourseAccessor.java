package com.example.witsdaily.Course;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.witsdaily.EnrollDialog;
import com.example.witsdaily.HomeScreen;
import com.example.witsdaily.PhoneDatabaseContract;
import com.example.witsdaily.PhoneDatabaseHelper;
import com.example.witsdaily.R;
import com.example.witsdaily.StorageAccessor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CourseAccessor extends Fragment{
    StorageAccessor syncAccessor;
    String userToken,personNumber;
    Context context;

    @Override
    public void onAttach(@NonNull Context context) {

        userToken = getActivity().getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getActivity().getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        context = getActivity().getApplicationContext();
        syncAccessor  = new StorageAccessor(context, personNumber,userToken){
            @Override
            public void getData(JSONObject data) {
                System.out.println("Successful sync task complete");
            }
        };
        super.onAttach(context);
    }

    private void processGetCourses(JSONObject response, boolean enrolled, View currentLayout) throws Exception{
        PhoneDatabaseHelper dbHelper = new PhoneDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (!response.getString("responseCode").equals("successful")){
            addAvailableCourses(currentLayout,enrolled);// then we dont even need to call add courses out of context, hence add is private
            return;
        }
        JSONArray coursesList = response.getJSONArray("courses");
        for (int i =0;i<coursesList.length();i++) {
            response = coursesList.getJSONObject(i);

            ContentValues values = new ContentValues();
            if (!syncAccessor.containsCourseCode(response.getString("courseCode"))){
                values.put(PhoneDatabaseContract.TableCourse.COLUMN_NAME_CODE, response.getString("courseCode"));
                values.put(PhoneDatabaseContract.TableCourse.COLUMN_NAME_DESCRIPTION, response.getString("courseDescription"));
                values.put(PhoneDatabaseContract.TableCourse.COLUMN_NAME_LECTURER, response.getString("lecturer"));
                values.put(PhoneDatabaseContract.TableCourse.COLUMN_NAME_NAME, response.getString("courseName"));
                long result = db.insertOrThrow(PhoneDatabaseContract.TableCourse.TABLE_NAME, null, values);
                if (result <= 0) {
                    return;
                }
            }
            String courseID = syncAccessor.courseCodeToID(response.getString("courseCode"));
            if (enrolled && !syncAccessor.userLinked(courseID)){
                values = new ContentValues();
                values.put(PhoneDatabaseContract.TablePersonCourse.COLUMN_NAME_PERSONNUMBER, personNumber);
                values.put(PhoneDatabaseContract.TablePersonCourse.COLUMN_NAME_COURSEID, courseID);
                long result2 = db.insertOrThrow(PhoneDatabaseContract.TablePersonCourse.TABLE_NAME, null, values);

            }
        }
        addAvailableCourses(currentLayout,enrolled);

    }


    public void getRegisteredCourses(View mainLayout){
        context = getActivity().getApplicationContext();
        StorageAccessor dataAccessor = new StorageAccessor(context, personNumber,userToken){
            @Override
            public void getData(JSONObject data) {
                try {
                    processGetCourses(data,true,mainLayout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        dataAccessor.getEnrolledCourses();
    }
    public void getUnenrolledCourses(View mainLayout){
        context = getActivity().getApplicationContext();
        StorageAccessor dataAccessor = new StorageAccessor(context, personNumber,userToken){
            @Override
            public void getData(JSONObject data) {
                try {
                    processGetCourses(data,false,mainLayout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        dataAccessor.getUnenrolledCourses();
    }
    private void addAvailableCourses(View currentLayout,boolean registered){

        ((LinearLayout) currentLayout).removeAllViews(); // clears this for when async is done
        JSONArray value;
        if (registered)
             value = syncAccessor.getLocalCourses();
        else
             value = syncAccessor.getUCourses();
        for (int i =0;i<value.length();i++){
            try {
                View courseBrief = getLayoutInflater().inflate(R.layout.briefcoursedisplay, null);
                TextView name = (TextView)(courseBrief.findViewById(R.id.tvName));
                TextView description = (TextView)(courseBrief.findViewById(R.id.tvDescription));

                String courseID = value.getJSONObject(i).getString(PhoneDatabaseContract.TableCourse.COLUMN_NAME_ID); // or whatever whatever
                String courseName = value.getJSONObject(i).getString(PhoneDatabaseContract.TableCourse.COLUMN_NAME_NAME);
                String courseDescription = value.getJSONObject(i).getString(PhoneDatabaseContract.TableCourse.COLUMN_NAME_DESCRIPTION);
                ImageView colorBar = (ImageView)(courseBrief.findViewById(R.id.imgColor));
                courseBrief.setTag(courseID);
                name.setText(courseName);
                description.setText(courseDescription);
                courseBrief.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (registered){
                            courseClickedRegistered(view);
                        }else{
                            courseClickedUnregistered(view);
                        }
                    }
                });
                if (registered)
                    colorBar.setImageDrawable(getResources().getDrawable(R.color.color_primary));
                else
                    colorBar.setImageDrawable(getResources().getDrawable(R.color.color_secondary));
                ((LinearLayout) currentLayout).addView(courseBrief);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void courseClickedRegistered(View v){ // means they're already enrolled
        // go to that course
        Intent i = new Intent(getActivity().getApplicationContext(), CourseDisplay.class);
        i.putExtra("courseID",String.valueOf(v.getTag()));
        startActivity(i);
    }

    public void courseClickedUnregistered(View v){
        // go to that course
        Intent i = new Intent(getActivity().getApplicationContext(), EnrollDialog.class);
        i.putExtra("courseCode",v.getTag().toString()); // course code ?
        startActivity(i);
    }
}
