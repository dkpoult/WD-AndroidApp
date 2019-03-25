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
        public  static final String COLUMN_NAME_SYNCED = "Synced";
    }

    /*
    * public static class TableNotifications implements BaseColumns {
    *   public static final String TABLE_NAME = "Notifications";
    *   public static final String COLUMN_NAME_MESSAGE = "Message"; // "Message is the sql column name"
    *
    *
    * }
    *
    * */

}
