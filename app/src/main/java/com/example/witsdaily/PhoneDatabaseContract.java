package com.example.witsdaily;

import android.provider.BaseColumns;

public final class PhoneDatabaseContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private PhoneDatabaseContract() {}

    /* Inner class that defines the table contents */
    public static class TableCourse implements BaseColumns {
        public static final String TABLE_NAME = "Course";
        public static final String COLUMN_NAME_ID = "ID";
        public static final String COLUMN_NAME_CODE = "Code";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_DESCRIPTION = "Description";
        public static final String COLUMN_NAME_LECTURER = "Lecturer";
        public static final String COLUMN_NAME_SYNCED = "Synced";
    }

    public static class TablePerson implements BaseColumns {
        public static final String TABLE_NAME = "Person";
        public static final String COLUMN_NAME_NUMBER = "Number";
        public static final String COLUMN_NAME_NAME = "Name"; // idk what else the person wants
    }

    public static class TablePersonCourse implements BaseColumns{
        public static final String TABLE_NAME = "PersonCourse";
        public static final String COLUMN_NAME_PERSONNUMBER = "PersonNumber";
        public static final String COLUMN_NAME_COURSEID = "CourseID";
    }
    public static class TableVoted implements BaseColumns{
        public static final String TABLE_NAME = "VOTED";
        public static final String COLUMN_NAME_POSTID = "";
        public static final String COLUMN_NAME_DOVSID = "";
        public static final String COLUMN_NAME_COURSEID = "";
        public static final String COLUMN_NAME_TITLE = "";
    }
    public static class TableSettings implements BaseColumns{
        public static final String TABLE_NAME = "SETTINGS";
        public static final String COLUMN_NAME_LANGUAGE = "Language";
        public static final String COLUMN_NAME_NOTIFICATIONS = "Notifications";
        public static final String COLUMN_NAME_PERSONNUMBER = "PersonNumber";

    }
    /*
    * public static class TableNotifications implements BaseColumns {
    *   public static final String TABLE_NAME = "Notifications";
    *   public static final String COLUMN_NAME_MESSAGE = "Message"; // "Message is the sql column name"
    * note that the values are always String
    *
    * }
    *
    * */

}
