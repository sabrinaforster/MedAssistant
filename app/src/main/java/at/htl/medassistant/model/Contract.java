package at.htl.medassistant.model;

import android.provider.BaseColumns;


public class Contract {


    final static String SQL_CREATE_MEDICINE_TABLE =
            "CREATE TABLE " + Contract.MedicineEntry.TABLE_NAME + " ("
                    + MedicineEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + MedicineEntry.COLUMN_NAME + " TEXT NOT NULL, "
                    + MedicineEntry.COLUMN_ACTIVE_SUBSTANCE + " TEXT, "
                    + MedicineEntry.COLUMN_MED_TYPE + " STRING NOT NULL, "
                    + MedicineEntry.COLUMN_PERIODICITY + " INTEGER)";


    final static String SQL_CREATE_TREATMENT_TABLE =
            "CREATE TABLE " + Contract.TreatmentEntry.TABLE_NAME + " ("
                    + TreatmentEntry.COLUMN_MEDICINE_ID + " INTEGER NOT NULL, "
                    + TreatmentEntry.COLUMN_USER_ID + " INTEGER NOT NULL, "
                    + TreatmentEntry.COLUMN_START_DATE + " TEXT NOT NULL, "
                    + TreatmentEntry.COLUMN_END_DATE + " TEXT NOT NULL, "
                    + TreatmentEntry.COLUMN_TIME_OF_TAKING + " TEXT NOT NULL, "
                    //+ TreatmentEntry.COLUMN_DELETE_ROW + " INTEGER NOT NULL,"
                    + "FOREIGN KEY (" + TreatmentEntry.COLUMN_MEDICINE_ID + ") REFERENCES "
                    + MedicineEntry.TABLE_NAME + " ("
                    + MedicineEntry._ID + "), "
                    + "FOREIGN KEY (" + TreatmentEntry.COLUMN_USER_ID + ") REFERENCES "
                    + UserEntry.TABLE_NAME + " ("
                    + UserEntry._ID + "), "
                    + "PRIMARY KEY (" + TreatmentEntry.COLUMN_MEDICINE_ID
                    + "," + TreatmentEntry.COLUMN_USER_ID + "))";


    /**
     * http://stackoverflow.com/questions/2701877/sqlite-table-constraint-unique-on-multiple-columns
     * https://www.sqlite.org/lang_conflict.html
     */
    final static String SQL_CREATE_USER_TABLE =
            "CREATE TABLE " + Contract.UserEntry.TABLE_NAME + " ("
                    + UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + UserEntry.COLUMN_FIRST_NAME + " TEXT NOT NULL, "
                    + UserEntry.COLUMN_LAST_NAME + " TEXT NOT NULL, "
                    + "UNIQUE (" + UserEntry.COLUMN_FIRST_NAME + ", " + UserEntry.COLUMN_LAST_NAME + ") ON CONFLICT FAIL"
                    + ")";


    public static final class MedicineEntry implements BaseColumns {

        public static final String TABLE_NAME = "medicine";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_ACTIVE_SUBSTANCE = "active_substance";
        public static final String COLUMN_MED_TYPE = "med_type";
        public static final String COLUMN_PERIODICITY = "periodicity";

    }


    public static final class TreatmentEntry {

        public static final String TABLE_NAME = "treatment";

        public static final String COLUMN_MEDICINE_ID = "medicine_id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_START_DATE = "startdate";
        public static final String COLUMN_END_DATE = "enddate";
        public static final String COLUMN_TIME_OF_TAKING = "time_of_taking";
        //public static final String COLUMN_DELETE_ROW = "delete_row";
    }


    public static final class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "user";

        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "last_name";
    }

}
