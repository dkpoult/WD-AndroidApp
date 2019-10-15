package com.example.witsdaily;

import android.content.ContentValues;
import android.content.Context;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.witsdaily.PhoneDatabaseContract.*;

public abstract class StorageAccessor { // singleton class

    private DatabaseAccessor databaseAccessor;
    private String personNumber;
    private String userToken;
    private Context appContext;

    public StorageAccessor(Context context, String pPersonNumber, String pUserToken) {
        personNumber = pPersonNumber;
        userToken = pUserToken;
        appContext = context;
        databaseAccessor = new DatabaseAccessor(context);
    }

    public abstract void getData(JSONObject data);


    public void login(String password) {
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber, userToken) {
            @Override
            public void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.loginRequest(password);
    }

    public void updateServerFCMToken(String fcmToken) {
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber, userToken) {
            @Override
            public void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.updateServerFCMToken(fcmToken);
    }

    public void registerUser(String personIDNumber, String personPassword) {
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber, userToken) {
            @Override
            public void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.registerUser(personIDNumber, personPassword);
    }

    public void getEnrolledCourses() {
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber, userToken) {
            @Override
            public void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.getEnrolledCourses();
    }

    public void getUnenrolledCourses() {
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber, userToken) {
            @Override
            public void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.getUnenrolledCourses();
    }

    public boolean userLinked(String courseID) {
        return databaseAccessor.userLinked(courseID, personNumber);

    }

    public String courseCodeToID(String courseCode) {
        return databaseAccessor.courseCodeToID(courseCode);
    }

    public JSONArray getLocalCourses() {
        return databaseAccessor.getLocalCourses(personNumber);
    }

    //
    public JSONArray getUCourses() {

        String sql = "Select * From " + TableCourse.TABLE_NAME
                + " where " + TableCourse.COLUMN_NAME_CODE + " NOT IN (Select " +
                TableCourse.COLUMN_NAME_CODE
                + " From "
                + TableCourse.TABLE_NAME
                + " Join " + TablePersonCourse.TABLE_NAME + " on " + TableCourse.COLUMN_NAME_ID + " = "
                + TablePersonCourse.COLUMN_NAME_COURSEID + " where " + TablePersonCourse.COLUMN_NAME_PERSONNUMBER
                + " = \"" + personNumber + "\")";
        JSONArray values = null;
        try {
            values = databaseAccessor.selectRecords(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    public void linkUserToCourse(String courseID) {
        ContentValues params = new ContentValues();
        params.put(TablePersonCourse.COLUMN_NAME_PERSONNUMBER, personNumber);
        params.put(TablePersonCourse.COLUMN_NAME_COURSEID, courseID);
        databaseAccessor.insertValues(params, TablePersonCourse.TABLE_NAME);
    }

    public void enrollUser(String password, String courseCode) {

        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber, userToken) {
            @Override
            public void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.enrollUser(password, courseCode);
    }

    public boolean containsCourseCode(String courseCode) {
        return databaseAccessor.containsCourseCode(courseCode);
    }

    public void firebaseAuthenticate() {
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber, userToken) {
            @Override
            public void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.firebaseAutenticate();
    }

    public void getSurvey(String courseCode) {
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber, userToken) {
            @Override
            public void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.getSurvey(courseCode);
    }

    public void makeSurvey(String courseCode, String title, JSONArray options, String surveyType) {
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber, userToken) {
            @Override
            public void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.makeSurvey(courseCode, title, options, surveyType);
    }

    public void closeSurvey(String courseCode) {
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber, userToken) {
            @Override
            public void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.closeSurvey(courseCode);
    }

    public void getSurveyResults(String courseCode) {
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber, userToken) {
            @Override
            public void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.getSurveyResults(courseCode);
    }

    public void sendAnswer(String answer, String courseCode, String surveyType) {
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber, userToken) {
            @Override
            public void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.sendAnswer(answer, courseCode, surveyType);
    }

    public void getPosts(String forumCode) {
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber, userToken) {
            @Override
            public void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.getPosts(forumCode);
    }

    public void getPost(String postCode) {
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber, userToken) {
            @Override
            public void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.getPost(postCode);
    }

    public void makePost(String forumCode, String title, String body) {
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber, userToken) {
            @Override
            public void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.makePost(forumCode, title, body);
    }

    public void makeComment(String postCode, String body) {
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber, userToken) {
            @Override
            public void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.makeComment(postCode, body);
    }

    public void makeVote(String postCode, String vote) {
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber, userToken) {
            @Override
            public void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.makeVote(postCode, vote);
    }

    public void updateSettings(String language, int notifications, String pPersonNumber) {
        ContentValues params = new ContentValues();
        params.put(TableSettings.COLUMN_NAME_LANGUAGE, language);
        params.put(TableSettings.COLUMN_NAME_NOTIFICATIONS, notifications);
        params.put(TableSettings.COLUMN_NAME_PERSONNUMBER, pPersonNumber);
        databaseAccessor.updateRecords(TableSettings.TABLE_NAME, params, pPersonNumber);
    }

    public void getEvents() {
        NetworkAccessor NA = new NetworkAccessor(appContext, personNumber, userToken) {
            @Override
            public void getResponse(JSONObject data) {
                getData(data);
            }
        };
        NA.getEvents();
    }

    public JSONArray getSettings(String pPersonnumber) {
        try {
            return databaseAccessor.selectRecords("Select * From " + TableSettings.TABLE_NAME + " where "
                    + TableSettings.COLUMN_NAME_PERSONNUMBER + " = \'" + pPersonnumber + "\'");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setAnswer(String postCode, String commentCode) {
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber, userToken) {
            @Override
            public void getResponse(JSONObject data) {
                getData(data);
            }
        };
        networkAccessor.setAnswer(postCode, commentCode);
    }

    public void getChatTypeMessages(String chatroomCode, String socketType) {
        NetworkAccessor networkAccessor = new NetworkAccessor(appContext, personNumber, userToken) {
            @Override
            public void getResponse(JSONObject data) {

                try {
                    if (!data.getString("responseCode").equals("successful")) {
                        return;
                    }
                    JSONArray messages = data.getJSONArray("messages");
                    for (int i = 0; i < messages.length(); i++) {
                        if (!((JSONObject) messages.get(i)).getString("messageType").equals(socketType)) {
                            messages.remove(i);
                            i -= 1;

                        }
                    }
                    JSONObject newData = new JSONObject();
                    newData.put("messages", messages);
                    getData(newData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
        networkAccessor.getChatTypeMessages(chatroomCode);
    }
}

