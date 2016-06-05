package at.htl.medassistant.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;


import java.sql.SQLDataException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;

import at.htl.medassistant.entity.MedType;
import at.htl.medassistant.entity.Medicine;
import at.htl.medassistant.entity.Treatment;
import at.htl.medassistant.entity.User;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MedicineInstrumentationTest {

    public static final String TAG = MedicineInstrumentationTest.class.getSimpleName();
    private Context appContext = InstrumentationRegistry.getTargetContext();

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    @Test
    public void t010UseAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("at.htl.medassistant", appContext.getPackageName());
    }

    @Test
    public void t020CreateDb() {

        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(Contract.UserEntry.TABLE_NAME);
        tableNameHashSet.add(Contract.MedicineEntry.TABLE_NAME);
        tableNameHashSet.add(Contract.TreatmentEntry.TABLE_NAME);

        deleteTheDatabase();

        // check, wether the table is opened
        SQLiteDatabase db = MedicineDatabaseHelper.getInstance(appContext).getWritableDatabase();
        assertThat(db.isOpen(), is(true));

        // check the metadata of sqlite, if tables are created
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertThat("db not created correctly", c.moveToFirst(), is(true));

        // verify that the tables have been created
        do {
            // die Log-Ausgaben findet man im Android Monitor
            Log.i(TAG, "t020createDb: " + c.getString(0));
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        Assert.assertThat("Die Datenbank wurde erstellt, allerdings fehlen Tabellen",
                tableNameHashSet.isEmpty(), is(true));

//        // ========================= DVD =========================
//        // now, do our tables contain the correct columns?
//        c = db.rawQuery("PRAGMA table_info(" + DvdContract.TABLE_NAME + ")", null);
//
//        assertThat("Error: This means that we were unable to query the database for table information.",
//                c.moveToFirst(), is(true));
//
//        // Build a HashSet of all of the column names we want to look for
//        final HashSet<String> dvdColumnHashSet = new HashSet<String>();
//        dvdColumnHashSet.add(DvdContract.DvdEntry._ID);
//        dvdColumnHashSet.add(DvdContract.DvdEntry.COLUMN_NAME_GENRE);
//        dvdColumnHashSet.add(DvdContract.DvdEntry.COLUMN_NAME_PRICE);
//        dvdColumnHashSet.add(DvdContract.DvdEntry.COLUMN_NAME_RATING);
//        dvdColumnHashSet.add(DvdContract.DvdEntry.COLUMN_NAME_STATUS);
//        dvdColumnHashSet.add(DvdContract.DvdEntry.COLUMN_NAME_STUDIO);
//        dvdColumnHashSet.add(DvdContract.DvdEntry.COLUMN_NAME_TITLE);
//        dvdColumnHashSet.add(DvdContract.DvdEntry.COLUMN_NAME_YEAR);
//
//        int columnNameIndex = c.getColumnIndex("name");
//        do {
//            String columnName = c.getString(columnNameIndex);
//            dvdColumnHashSet.remove(columnName);
//        } while (c.moveToNext());
//
//        // if this fails, it means that your database doesn't contain all of the required location
//        // entry columns
//        assertThat("Error: The database doesn't contain all of the required location entry columns",
//                dvdColumnHashSet, is(empty()));
//
//
//        // ========================= GENRE =========================
//        // now, do our tables contain the correct columns?
//        c = db.rawQuery("PRAGMA table_info(" + GenreContract.TABLE_NAME + ")", null);
//
//        assertThat("Error: This means that we were unable to query the database for table information.",
//                c.moveToFirst(), is(true));
//
//        // Build a HashSet of all of the column names we want to look for
//        final HashSet<String> genreColumnHashSet = new HashSet<String>();
//        genreColumnHashSet.add(GenreContract.GenreEntry._ID);
//        genreColumnHashSet.add(GenreContract.GenreEntry.COLUMN_NAME_GENRE);
//
//        columnNameIndex = c.getColumnIndex("name");
//        do {
//            String columnName = c.getString(columnNameIndex);
//            genreColumnHashSet.remove(columnName);
//        } while (c.moveToNext());
//
//        // if this fails, it means that your database doesn't contain all of the required location
//        // entry columns
//        assertThat("Error: The database doesn't contain all of the required location entry columns",
//                genreColumnHashSet, is(empty()));

        c.close();
        db.close();
    }

    @Test
    public void t030InsertUser() {

        MedicineDatabaseHelper dbHelper = MedicineDatabaseHelper.getInstance(appContext);

        User dagobert = new User("Dagobert", "Duck");

        assertThat(
                "Es konnte kein Datensatz in Tabelle USER eingetragen werden",
                dbHelper.insertUser(dagobert),
                greaterThan(0L)
        );

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.query(
                Contract.UserEntry.TABLE_NAME,// Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertThat("Es wurden keine Datensätze zurückgegeben", c.moveToFirst());

        do {
            // die Log-Ausgaben findet man im Android Monitor
            Log.wtf(TAG, "t030InsertUser: " + c.getLong(0) + " | " + c.getString(1) + " | " + c.getString(2));
        } while (c.moveToNext());

        c.close();
        dbHelper.close();

    }

    @Test
    public void t040InsertUserFailBecauseRedundantUser() {
        deleteTheDatabase();
        MedicineDatabaseHelper dbHelper = MedicineDatabaseHelper.getInstance(appContext);
        User dagobert = new User("Dagobert", "Duck");

        assertThat(
                "Es konnte kein Datensatz in Tabelle USER eingetragen werden",
                dbHelper.insertUser(dagobert),
                greaterThan(0L)
        );

        long id = 0L;

        try {
            id = dbHelper.insertUser(dagobert);
        } catch (Exception e) {
            Log.e(TAG, "t040InsertUserFailBecauseRedundantUser: " + e.getMessage());
            assertThat(e.getMessage(), is("UNIQUE constraint failed: user.first_name, user.last_name (code 2067)"));
            return;
        } finally {
            dbHelper.close();

        }

        if (id == -1) {
            fail("Es ist die Methode insertOrThrow(...) zu verwenden");
        }

        fail("Der doppelte User (gleicher Vor- und Nachname) darf nicht in die DB gespeichert werden");
    }

    @Test
    public void t050InsertMedicine() {

        MedicineDatabaseHelper dbHelper = MedicineDatabaseHelper.getInstance(appContext);
        Medicine aspirin = new Medicine("Aspirin", "Acetylsalicylsäure", MedType.PHARMACEUTICAL, 7);

        assertThat(
                "Es konnte kein Datensatz in Tabelle MEDICINE eingetragen werden",
                dbHelper.insertMedicine(aspirin),
                greaterThan(0L)
        );

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.query(
                Contract.MedicineEntry.TABLE_NAME,// Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertThat("Es wurden keine Datensätze zurückgegeben", c.moveToFirst());

        assertThat(c.getLong(0), is(1L));
        assertThat(c.getString(1), is("Aspirin"));
        assertThat(c.getString(2), is("Acetylsalicylsäure"));
        assertThat(c.getString(3), is("PHARMACEUTICAL"));
        assertThat(c.getInt(4), is(7));

        do {
            // die Log-Ausgaben findet man im Android Monitor
            Log.wtf(TAG, "t050InsertMedicine: " + c.getLong(0) + " | " + c.getString(1) + " | " + c.getString(2) + " | " + c.getString(3) + " | " + c.getInt(4));
        } while (c.moveToNext());

        c.close();
        dbHelper.close();

    }

    @Test
    public void t060findUserById() {
        deleteTheDatabase();
        MedicineDatabaseHelper dbHelper = MedicineDatabaseHelper.getInstance(appContext);

        User dagobert = new User("Dagobert", "Duck");
        Long id = dbHelper.insertUser(dagobert);
        dagobert.setId(id);

        User mickey = new User("Mickey", "Mouse");
        id = dbHelper.insertUser(mickey);
        mickey.setId(id);

        User actualUser = dbHelper.findUserById(2L);
        assertThat(actualUser, is(equalTo(mickey)));

    }

    @Test
    public void t070findUserByName() {
        deleteTheDatabase();
        MedicineDatabaseHelper dbHelper = MedicineDatabaseHelper.getInstance(appContext);

        User dagobert = new User("Dagobert", "Duck");
        long id = dbHelper.insertUser(dagobert);
        dagobert.setId(id);

        User mickey = new User("Mickey", "Mouse");
        id = dbHelper.insertUser(mickey);
        mickey.setId(id);

        User actualUser = dbHelper.findUserByName("Dagobert", "Duck");
        assertThat(actualUser, is(equalTo(dagobert)));

    }

    @Test
    public void t080findMedicineById() {
        deleteTheDatabase();
        MedicineDatabaseHelper dbHelper = MedicineDatabaseHelper.getInstance(appContext);

        Medicine aspirin = new Medicine("Aspirin", "Acetylsalicylsäure", MedType.PHARMACEUTICAL, 7);
        long id = dbHelper.insertMedicine(aspirin);
        aspirin.setId(id);

        Medicine fsme = new Medicine("FSME-IMMUN Inject", "FSME-Virus-Antigen", MedType.VACCINE, 365 * 5);
        id = dbHelper.insertMedicine(fsme);
        fsme.setId(id);

        Medicine actualMedicine = dbHelper.findMedicineById(2L);
        assertThat(actualMedicine, is(equalTo(fsme)));
        assertFalse("Es müssen 2 verschiedene Objekte mit gleichen Werten sein", actualMedicine == fsme);
    }

    @Test
    public void t090findMedicineByName() {
        deleteTheDatabase();
        MedicineDatabaseHelper dbHelper = MedicineDatabaseHelper.getInstance(appContext);

        Medicine aspirin = new Medicine("Aspirin", "Acetylsalicylsäure", MedType.PHARMACEUTICAL, 7);
        long id = dbHelper.insertMedicine(aspirin);
        aspirin.setId(id);

        Medicine fsme = new Medicine("FSME-IMMUN Inject", "FSME-Virus-Antigen", MedType.VACCINE, 365 * 5);
        id = dbHelper.insertMedicine(fsme);
        fsme.setId(id);

        Medicine actualMedicine = dbHelper.findMedicineByName("Aspirin");
        assertThat(actualMedicine, is(equalTo(aspirin)));
        assertFalse("Es müssen 2 verschiedene Objekte mit gleichen Werten sein", actualMedicine == aspirin);
    }

    @Test
    public void t100InsertTreatment() {
        deleteTheDatabase();
        MedicineDatabaseHelper dbHelper = MedicineDatabaseHelper.getInstance(appContext);

        User dagobert = new User("Dagobert", "Duck");
        long id = dbHelper.insertUser(dagobert);
        dagobert.setId(id);

        User mickey = new User("Mickey", "Mouse");
        id = dbHelper.insertUser(mickey);
        mickey.setId(id);

        Medicine aspirin = new Medicine("Aspirin", "Acetylsalicylsäure", MedType.PHARMACEUTICAL, 7);
        id = dbHelper.insertMedicine(aspirin);
        aspirin.setId(id);

        Medicine fsme = new Medicine("FSME-IMMUN Inject", "FSME-Virus-Antigen", MedType.VACCINE, 365 * 5);
        id = dbHelper.insertMedicine(fsme);
        fsme.setId(id);

        Treatment treatment = null;
        try {
            treatment = new Treatment(
                    dagobert,
                    aspirin,
                    sdf.parse("06.05.2016"),
                    sdf.parse("06.06.2016"),
                    Time.valueOf("08:00:00")
            );
        } catch (ParseException e) {
            Log.d(TAG, "t100InsertTreatment: " + e.getMessage());
            fail("t100InsertTreatment: Insert des Treatments fehlgeschlagen");
        }

        try {
            id = dbHelper.insertTreatment(treatment);
        } catch (SQLDataException e) {
            Log.e(TAG, "t100InsertTreatment: " + e.getMessage());
            fail("t100InsertTreatment: Insert des Treatments fehlgeschlagen");
        }

        try {
            treatment = new Treatment(
                    dagobert,
                    fsme,
                    sdf.parse("01.05.2016"),
                    sdf.parse("06.06.2030"),
                    Time.valueOf("11:00:00")
            );
        } catch (ParseException e) {
            Log.d(TAG, "t100InsertTreatment: " + e.getMessage());
            fail("t100InsertTreatment: Insert des Treatments fehlgeschlagen");
        }

        try {
            id = dbHelper.insertTreatment(treatment);
        } catch (SQLDataException e) {
            Log.e(TAG, "t100InsertTreatment: " + e.getMessage());
            fail("t100InsertTreatment: Insert des Treatments fehlgeschlagen");
        }

        try {
            treatment = new Treatment(
                    mickey,
                    aspirin,
                    sdf.parse("01.01.2016"),
                    sdf.parse("08.10.2016"),
                    Time.valueOf("10:00:00")
            );
        } catch (ParseException e) {
            Log.d(TAG, "t100InsertTreatment: " + e.getMessage());
            fail("t100InsertTreatment: Insert des Treatments fehlgeschlagen");
        }

        try {
            id = dbHelper.insertTreatment(treatment);
        } catch (SQLDataException e) {
            Log.e(TAG, "t100InsertTreatment: " + e.getMessage());
            fail("t100InsertTreatment: Insert des Treatments fehlgeschlagen");
        }

        try (SQLiteDatabase db = dbHelper.getReadableDatabase()) {
            int countTreatments = 0;

            Cursor c = db.query(
                    Contract.TreatmentEntry.TABLE_NAME,// Table to Query
                    null, // leaving "columns" null just returns all the columns.
                    null, // cols for "where" clause
                    null, // values for "where" clause
                    null, // columns to group by
                    null, // columns to filter by row groups
                    null  // sort order
            );

            // Move the cursor to the first valid database row and check to see if we have any rows
            assertThat("Anzahl der eingelesenen DVDs falsch", c.getCount(), is(3));
            assertThat("Es wurden keine Datensätze zurückgegeben", c.moveToFirst());

            assertThat(c.getInt(0), is(1));
            assertThat(c.getInt(1), is(1));
            assertThat(c.getString(2), is("06.05.2016"));
            assertThat(c.getString(3), is("06.06.2016"));
            assertThat(c.getString(4), is("08:00"));

            c.moveToNext();

            assertThat(c.getInt(0), is(2));
            assertThat(c.getInt(1), is(1));
            assertThat(c.getString(2), is("01.05.2016"));
            assertThat(c.getString(3), is("06.06.2030"));
            assertThat(c.getString(4), is("11:00"));

            c.moveToNext();

            assertThat(c.getInt(0), is(1));
            assertThat(c.getInt(1), is(2));
            assertThat(c.getString(2), is("01.01.2016"));
            assertThat(c.getString(3), is("08.10.2016"));
            assertThat(c.getString(4), is("10:00"));

            c.close();
        }
    }

    /**
     * Wird der User des Treatments nicht gefunden wird eine SQLDataException geworfen mit
     * folgender Fehlermeldung: zB "User 'Mouse, Mickey' not found"
     */
    @Test
    public void t110InsertTreatmentUserNotAvailabeFails() {
        deleteTheDatabase();
        MedicineDatabaseHelper dbHelper = MedicineDatabaseHelper.getInstance(appContext);

        User dagobert = new User("Dagobert", "Duck");
        long id = dbHelper.insertUser(dagobert);
        dagobert.setId(id);

        User mickey = new User("Mickey", "Mouse");
        mickey.setId(300L);

        Medicine aspirin = new Medicine("Aspirin", "Acetylsalicylsäure", MedType.PHARMACEUTICAL, 7);
        aspirin.setId(200L);

        Medicine fsme = new Medicine("FSME-IMMUN Inject", "FSME-Virus-Antigen", MedType.VACCINE, 365 * 5);
        id = dbHelper.insertMedicine(fsme);
        fsme.setId(id);

        Treatment treatment = null;
        try {
            treatment = new Treatment(
                    mickey,
                    fsme,
                    sdf.parse("06.05.2016"),
                    sdf.parse("06.06.2016"),
                    Time.valueOf("08:00:00")
            );
        } catch (ParseException e) {
            Log.d(TAG, "t110InsertTreatment: " + e.getMessage());
            fail("t100InsertTreatment: Insert des Treatments fehlgeschlagen");
        }

        try {
            id = dbHelper.insertTreatment(treatment);
        } catch (SQLDataException e) {
            Log.e(TAG, "t100InsertTreatment: " + e.getMessage());
            assertThat(e.getMessage(), is("User 'Mouse, Mickey' not found"));
            return;
        }

        fail("t110InsertTreatment: Treatment dürfte nicht gespeichert werden");

    }

    /**
     * Wird der User des Treatments nicht gefunden wird eine SQLDataException geworfen mit
     * folgender Fehlermeldung: zB "User 'Mouse, Mickey' not found"
     */
    @Test
    public void t120InsertTreatmentMedicineNotAvailabeFails() {
        deleteTheDatabase();
        MedicineDatabaseHelper dbHelper = MedicineDatabaseHelper.getInstance(appContext);

        User dagobert = new User("Dagobert", "Duck");
        long id = dbHelper.insertUser(dagobert);
        dagobert.setId(id);

        User mickey = new User("Mickey", "Mouse");
        mickey.setId(300L);

        Medicine aspirin = new Medicine("Aspirin", "Acetylsalicylsäure", MedType.PHARMACEUTICAL, 7);
        aspirin.setId(200L);

        Medicine fsme = new Medicine("FSME-IMMUN Inject", "FSME-Virus-Antigen", MedType.VACCINE, 365 * 5);
        id = dbHelper.insertMedicine(fsme);
        fsme.setId(id);

        Treatment treatment = null;
        try {
            treatment = new Treatment(
                    dagobert,
                    aspirin,
                    sdf.parse("06.05.2016"),
                    sdf.parse("06.06.2016"),
                    Time.valueOf("08:00:00")
            );
        } catch (ParseException e) {
            Log.d(TAG, "t110InsertTreatment: " + e.getMessage());
            fail("t100InsertTreatment: Insert des Treatments fehlgeschlagen");
        }

        try {
            id = dbHelper.insertTreatment(treatment);
        } catch (SQLDataException e) {
            Log.e(TAG, "t100InsertTreatment: " + e.getMessage());
            assertThat(e.getMessage(), is("Medicine 'Aspirin' not found"));
            return;
        }

        fail("t110InsertTreatment: Treatment dürfte nicht gespeichert werden");

    }


    private void deleteTheDatabase() {
        appContext.deleteDatabase(MedicineDatabaseHelper.DATABASE_NAME);
    }

}