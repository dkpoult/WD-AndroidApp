package com.example.witsdaily;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UnregisteredCourses extends AppCompatActivity {
String user_token,personNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unregistered_courses);
        user_token = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("userToken", null);
        personNumber = getSharedPreferences("com.wd", Context.MODE_PRIVATE).getString("personNumber", null);
        addAvailableCourses();
    }

    private void addAvailableCourses(){
        PhoneDatabaseHelper dbHelper = new PhoneDatabaseHelper(getApplicationContext());
        View currentLayout = (LinearLayout)findViewById(R.id.llUnregister);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "Select Distinct * from "+ PhoneDatabaseContract.TableCourse.TABLE_NAME
                +" JOIN "+ PhoneDatabaseContract.TablePersonCourse.TABLE_NAME +" ON " + PhoneDatabaseContract.TableCourse.COLUMN_NAME_ID
                + " = " + PhoneDatabaseContract.TablePersonCourse.COLUMN_NAME_COURSEID
                + " WHERE " + PhoneDatabaseContract.TablePersonCourse.COLUMN_NAME_PERSONNUMBER
                + " != \""+ personNumber+"\"";
        Cursor cursor = db.rawQuery(sql,null); //have to do a better table name here

        while(cursor.moveToNext()) {
            String courseID = cursor.getString(
                    cursor.getColumnIndexOrThrow(PhoneDatabaseContract.TablePersonCourse.COLUMN_NAME_COURSEID));
            View courseBrief = getLayoutInflater().inflate(R.layout.briefcoursedisplay, null);
            courseBrief.setTag(courseID);
            ImageView stripe = (ImageView) courseBrief.findViewById(R.id.imgColor);
            TextView name = (TextView)(courseBrief.findViewById(R.id.tvName));
            TextView description = (TextView)(courseBrief.findViewById(R.id.tvDescription));
            name.setText(cursor.getString(cursor.getColumnIndexOrThrow(PhoneDatabaseContract.TableCourse.COLUMN_NAME_NAME)));
            description.setText(cursor.getString(cursor.getColumnIndexOrThrow(PhoneDatabaseContract.TableCourse.COLUMN_NAME_DESCRIPTION)));
            stripe.setColorFilter(getApplicationContext().getResources().getColor(R.color.colorAccent));
            ((LinearLayout) currentLayout).addView(courseBrief);
        }
        cursor.close();

    }
    public void courseClicked(View v){ // means they're already enrolled
        // go to that course
        Intent i = new Intent(UnregisteredCourses.this, EnrollDialog.class);
        i.putExtra("courseCode",v.getTag().toString()); // course code ?
        startActivity(i);
    }

}
