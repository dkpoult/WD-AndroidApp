package com.example.witsdaily.Course;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.witsdaily.R;


public class UnenrolledCourses extends CourseAccessor {
    String userToken,personNumber;
    View mainLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_enrolled_courses, container, false);
        mainLayout = v.findViewById(R.id.llCourseLayout);
        getUnenrolledCourses(mainLayout);

        return v;
    }

    @Override
    public void onResume() {

        getUnenrolledCourses(mainLayout);
        super.onResume();
    }

}
