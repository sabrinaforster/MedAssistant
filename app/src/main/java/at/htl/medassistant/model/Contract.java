package at.htl.medassistant.model;

import android.provider.BaseColumns;


public class Contract {

    /**
     * SQL-Statement zum Erstellen der Tabelle Medicine
     */
    final static String SQL_CREATE_MEDICINE_TABLE =
            "CREATE TABLE IF NOT EXISTS " +
                    MedicineEntry.TABLE_NAME + " ("+ MedicineEntry._ID
                    +" integer primary key autoincrement, " +
                    MedicineEntry.COLUMN_NAME + " text, " +
                    MedicineEntry.COLUMN_ACTIVE_SUBSTANCE + " text, " +
                    MedicineEntry.COLUMN_MED_TYPE + " text, " +
                    MedicineEntry.COLUMN_PERIODICITY + " text)";

    /**
     * SQL-Statement zum Erstellen der Tabelle Treatment
     *
     * Der Primärschlüssel setzt sich aus den Spalten medicine_id und user_id zusammen
     * Es gibt KEINE Spalte _id
     *
     */
    final static String SQL_CREATE_TREATMENT_TABLE =
            "CREATE TABLE IF NOT EXISTS "+
                TreatmentEntry.TABLE_NAME + " ("+
                TreatmentEntry.COLUMN_MEDICINE_ID + " integer, "+
                TreatmentEntry.COLUMN_USER_ID + " integer, "+
                TreatmentEntry.COLUMN_START_DATE +" text, "+
                TreatmentEntry.COLUMN_END_DATE + " text, "+
                    TreatmentEntry.COLUMN_TIME_OF_TAKING + " text, "+
                    "primary key (" + TreatmentEntry.COLUMN_MEDICINE_ID +
                    ", " + TreatmentEntry.COLUMN_USER_ID +"))";


    /**
     * SQL-Statement zum Erstellen der Tabelle User
     * <p/>
     * Die Kombination aus firstName und lastName muss eindeutig sein!
     * <p/>
     * http://stackoverflow.com/questions/2701877/sqlite-table-constraint-unique-on-multiple-columns
     * https://www.sqlite.org/lang_conflict.html
     */
    final static String SQL_CREATE_USER_TABLE =
            "CREATE TABLE if not exists " +
                    UserEntry.TABLE_NAME + " ("+
                    UserEntry._ID + " integer primary key autoincrement, " +
                    UserEntry.COLUMN_FIRST_NAME + " text, " +
                    UserEntry.COLUMN_LAST_NAME + " text, " +
                    UserEntry.COLUMN_CURRENT_USER + " text, " +
                    "UNIQUE ("+UserEntry.COLUMN_FIRST_NAME + ", "+UserEntry.COLUMN_LAST_NAME+
                    "))";


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
    }


    public static final class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "user";

        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_CURRENT_USER = "current_user";
    }

}
