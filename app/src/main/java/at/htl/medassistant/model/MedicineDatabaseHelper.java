package at.htl.medassistant.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLDataException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import at.htl.medassistant.entity.MedType;
import at.htl.medassistant.entity.Medicine;
import at.htl.medassistant.entity.Treatment;
import at.htl.medassistant.entity.User;

/**
 *
 * Die Klasse MedicineDatabseHelper ist als Singleton zu implementieren.
 * Dabei hält dieses Singleton-Objekt genau eine Instanz vom Typ SQLiteDatabase
 *         static SQLiteDatabase mDb;
 * Dieses Database-Objekt wird für Schreibzugriffe verwendet --> getWritableDatabase()
 * Für Lesezugriffe wird jedesmal eine Nur-Lese-Datenbankobjekt erzeugt --> getReadableDatabase()
 *
 * Die Daen werden im Format dd.MM.yyyy n der DB gespeichert
 *
 * @see <a href="https://cmanios.wordpress.com/2012/05/17/extend-sqliteopenhelper-as-a-singleton-class-in-android/">cmanios: Extend SQLiteOpenHelper as a singleton class in Android</a>
 */
public class MedicineDatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = MedicineDatabaseHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "medicine.sqlite";
    static SQLiteDatabase mDb;

    private static MedicineDatabaseHelper instance;

    private MedicineDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static MedicineDatabaseHelper getInstance(Context context){
        if (instance == null) {
            instance = new MedicineDatabaseHelper(context);
        }
        return instance;
    }

    /**
     * Wenn mDb null ist oder geschlossen ist dieser Instanzvariablen eine neue
     * schreibfähige DB zugewiesen, ansonsten wird nur die Instanzvarianle zurückgegeben
     *        ... super.getWritableDatabase();
     *
     * @return database
     */
    @Override
    public SQLiteDatabase getWritableDatabase() {
        if ((mDb == null)||(!mDb.isOpen())) {
            mDb = super.getWritableDatabase();
        }
        return mDb;
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }


    @Override
    public synchronized void close() {
        super.close();
        if (mDb != null) {
            mDb.close();
            mDb = null;
        }
    }

    //------------------------- Ende Singleton Core Methods -------------------------

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(LOG_TAG, Contract.SQL_CREATE_MEDICINE_TABLE);
        db.execSQL(Contract.SQL_CREATE_MEDICINE_TABLE);

        Log.d(LOG_TAG, Contract.SQL_CREATE_TREATMENT_TABLE);
        db.execSQL(Contract.SQL_CREATE_TREATMENT_TABLE);

        Log.d(LOG_TAG, Contract.SQL_CREATE_USER_TABLE);
        db.execSQL(Contract.SQL_CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * stores user in db   --> Verwendung von insertOrThrow(...)
     *
     * @param user
     * @return generated id
     */
    public long insertUser(User user) {

        if (findCurrentUserId() < 0) {
            user.setCurrentUser(true);
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.UserEntry.COLUMN_FIRST_NAME, user.getFirstName());
        contentValues.put(Contract.UserEntry.COLUMN_LAST_NAME, user.getLastName());

        if (user.isCurrentUser()) {
            contentValues.put(Contract.UserEntry.COLUMN_CURRENT_USER, "1");
        } else {
            contentValues.put(Contract.UserEntry.COLUMN_CURRENT_USER, "0");
        }

        return db.insertOrThrow(Contract.UserEntry.TABLE_NAME, null, contentValues);
    }


    /**
     *
     * stores medicine in db   --> Verwendung von insertOrThrow(...)
     *
     * @param medicine
     * @return
     */
    public long insertMedicine(Medicine medicine) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.MedicineEntry.COLUMN_NAME, medicine.getName());
        contentValues.put(Contract.MedicineEntry.COLUMN_ACTIVE_SUBSTANCE, medicine.getActiveSubstance());
        contentValues.put(Contract.MedicineEntry.COLUMN_MED_TYPE, medicine.getMedType().toString());
        contentValues.put(Contract.MedicineEntry.COLUMN_PERIODICITY, medicine.getPeriodicityInDays());

        return db.insertOrThrow(Contract.MedicineEntry.TABLE_NAME, null, contentValues);
    }

    /**
     *
     * Hier wird zunächst überprüft, ob User und Medicine vorhanden sind, wenn nicht werden
     * entsprechende Exceptions geworfen (siehe InstrumentationTest)
     *    --> Verwendung von insertOrThrow(...)
     *
     * @param treatment
     * @return generierte id des erstellten Datensatzes
     * @throws SQLDataException
     */
    public long insertTreatment(Treatment treatment) throws SQLDataException {
        Medicine medicine = findMedicineByName(treatment.getMedicine().getName());
        User user = findUserByName(treatment.getUser().getFirstName(), treatment.getUser().getLastName());

        if (user == null) {
            throw new SQLDataException(String.format("User '%s' not found",
                    treatment.getUser().toString()));
        }
        if (medicine == null) {
            throw new SQLDataException(String.format("Medicine '%s' not found", treatment.getMedicine().getName()));
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.TreatmentEntry.COLUMN_START_DATE, treatment.getStartDateToString());
        contentValues.put(Contract.TreatmentEntry.COLUMN_END_DATE, treatment.getEndDateToString());
        contentValues.put(Contract.TreatmentEntry.COLUMN_TIME_OF_TAKING, treatment.getTimeOfTakingToString());
        contentValues.put(Contract.TreatmentEntry.COLUMN_MEDICINE_ID, medicine.getId());
        contentValues.put(Contract.TreatmentEntry.COLUMN_USER_ID, user.getId()); //medicineid
        contentValues.put(Contract.TreatmentEntry.COLUMN_NOTE, treatment.getNote());

        return this.getWritableDatabase().insertOrThrow(Contract.TreatmentEntry.TABLE_NAME, null, contentValues);

    }

    public User findUserById(long id) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            Cursor cursor = db.query(
                    Contract.UserEntry.TABLE_NAME,
                    null,
                    Contract.UserEntry._ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                cursor.moveToFirst();
            } else {
                return null;
            }
            String firstname = cursor.getString(cursor.getColumnIndex(Contract.UserEntry.COLUMN_FIRST_NAME));
            String lastname = cursor.getString(cursor.getColumnIndex(Contract.UserEntry.COLUMN_LAST_NAME));
            String currentUser = cursor.getString(cursor.getColumnIndex(Contract.UserEntry.COLUMN_CURRENT_USER));

            User user = new User(id, firstname, lastname);
            if (currentUser.equals("1")) {
                user.setCurrentUser(true);
            }
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Verwenden Sie db.query() sowie das AutoClosable-Interface
     *
     * @param firstName
     * @param lastName
     * @return
     */
    public User findUserByName(String firstName, String lastName) {
        String text = Contract.UserEntry.COLUMN_FIRST_NAME + " = ?";
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            Cursor cursor = db.query(Contract.UserEntry.TABLE_NAME,
                    null,
                    Contract.UserEntry.COLUMN_FIRST_NAME + " = ? and "+Contract.UserEntry.COLUMN_LAST_NAME + " = ?",
                    new String[]{firstName, lastName},
                    null,
                    null,
                    null);
            if (cursor != null) {
                cursor.moveToFirst();
            } else {
                return null;
            }

            String firstname = cursor.getString(cursor.getColumnIndex(Contract.UserEntry.COLUMN_FIRST_NAME));
            String lastname = cursor.getString(cursor.getColumnIndex(Contract.UserEntry.COLUMN_LAST_NAME));
            int id = cursor.getInt(cursor.getColumnIndex(Contract.UserEntry._ID));
            String currentUser = cursor.getString(cursor.getColumnIndex(Contract.UserEntry.COLUMN_CURRENT_USER));

            User user = new User(id, firstname, lastname);
            if (currentUser != null && currentUser.equals("1")) {
                user.setCurrentUser(true);
            }

            return user;
        }catch (Exception e){
            return null;
        }

    }

    public Medicine findMedicineById(long id) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            Cursor cursor = db.query(
                    Contract.MedicineEntry.TABLE_NAME,
                    null,
                    "_id = ?",
                    new String[]{String.valueOf(id)},
                    null,
                    null,
                    null);
            if (cursor != null) {
                cursor.moveToFirst();
            } else {
                return null;
            }
            Medicine medicine = new Medicine();
            medicine.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            medicine.setActiveSubstance(cursor.getString(cursor.getColumnIndex(Contract.MedicineEntry.COLUMN_ACTIVE_SUBSTANCE)));
            medicine.setMedType(MedType.valueOf(cursor.getString(cursor.getColumnIndex(Contract.MedicineEntry.COLUMN_MED_TYPE))));
            medicine.setPeriodicityInDays(cursor.getInt(cursor.getColumnIndex(Contract.MedicineEntry.COLUMN_PERIODICITY)));
            medicine.setName(cursor.getString(cursor.getColumnIndex(Contract.MedicineEntry.COLUMN_NAME)));
            return medicine;

        } catch (Exception e) {
            return null;
        }
    }

    public Medicine findMedicineByName(String name) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            Cursor cursor = db.query(
                    Contract.MedicineEntry.TABLE_NAME,
                    null,
                    Contract.MedicineEntry.COLUMN_NAME+" = ?",
                    new String[]{String.valueOf(name)},
                    null,
                    null,
                    null);
            if (cursor != null) {
                cursor.moveToFirst();
            } else {
                return null;
            }
            Medicine medicine = new Medicine();
            medicine.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            medicine.setActiveSubstance(cursor.getString(cursor.getColumnIndex(Contract.MedicineEntry.COLUMN_ACTIVE_SUBSTANCE)));
            medicine.setMedType(MedType.valueOf(cursor.getString(cursor.getColumnIndex(Contract.MedicineEntry.COLUMN_MED_TYPE))));
            medicine.setPeriodicityInDays(cursor.getInt(cursor.getColumnIndex(Contract.MedicineEntry.COLUMN_PERIODICITY)));
            medicine.setName(cursor.getString(cursor.getColumnIndex(Contract.MedicineEntry.COLUMN_NAME)));
            return medicine;

        } catch (Exception e) {
            return null;
        }
    }

    //11.06.2016 Forster
    public List<Treatment> getTreatments(){
        long currentUserId = findCurrentUserId();
        try (SQLiteDatabase db = this.getReadableDatabase()) {

            Cursor cursor = db.query(
                    Contract.TreatmentEntry.TABLE_NAME,
                    null,
                    Contract.TreatmentEntry.COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(currentUserId)},
                    null,
                    null,
                    null);
            if (cursor != null) {
                cursor.moveToFirst();
            } else {
                return null;
            }
            List<Treatment> treatments = new ArrayList<>();
            while (cursor.isAfterLast() == false) {
                int medicieId = cursor.getInt(cursor.getColumnIndex(Contract.TreatmentEntry.COLUMN_MEDICINE_ID));
                Medicine medicine = findMedicineById(medicieId);

                int userId = cursor.getInt(cursor.getColumnIndex(Contract.TreatmentEntry.COLUMN_USER_ID));
                User user = findUserById(userId);

                String startDate = cursor.getString(cursor.getColumnIndex(Contract.TreatmentEntry.COLUMN_START_DATE));
                String endDate = cursor.getString(cursor.getColumnIndex(Contract.TreatmentEntry.COLUMN_END_DATE));
                String timeOfTaking = cursor.getString(cursor.getColumnIndex(Contract.TreatmentEntry.COLUMN_TIME_OF_TAKING));
                String note = cursor.getString(cursor.getColumnIndex(Contract.TreatmentEntry.COLUMN_NOTE));

                Treatment treatment = new Treatment(user, medicine, startDate, endDate, timeOfTaking, note);

                treatments.add(treatment);

                cursor.moveToNext();
            }
            return treatments;
        } catch (Exception e) {
            return null;
        }
    }

    public List<User> getUsers() {
        try (SQLiteDatabase db = this.getReadableDatabase()) {

            Cursor cursor = db.query(
                    Contract.UserEntry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
            if (cursor != null) {
                cursor.moveToFirst();
            } else {
                return null;
            }
            List<User> users = new ArrayList<>();
            while (cursor.isAfterLast() == false) {
                String firstname = cursor.getString(cursor.getColumnIndex(Contract.UserEntry.COLUMN_FIRST_NAME));
                String lastname = cursor.getString(cursor.getColumnIndex(Contract.UserEntry.COLUMN_LAST_NAME));
                long id = cursor.getInt(cursor.getColumnIndex(Contract.UserEntry._ID));
                String currentUser = cursor.getString(cursor.getColumnIndex(Contract.UserEntry.COLUMN_CURRENT_USER));

                User user = new User(id, firstname, lastname);
                if (currentUser == null || currentUser.equals("1")) {
                    user.setCurrentUser(true);
                }
                users.add(user);
                cursor.moveToNext();
            }
            return users;
        }
    }

    //14.06.2016 Forster
    public Treatment getNextTreatment() {
        try {
            Calendar c = Calendar.getInstance();
            List<Treatment> treatmentList = getTreatments();
            if(treatmentList.size() > 0)
            {
                treatmentList = getListToday(treatmentList, c);

                Time nowTime = new Time(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));

                boolean checkTimeIsBefore = false;
                boolean checkNextNotificationTomorrow = false;
                long treatmentId = -1;

                if(treatmentList.size() > 0){
                    Collections.sort(treatmentList);
                    if(treatmentList.size() == 1)
                    {
                        return treatmentList.get(0);
                    }
                    for(Treatment t : treatmentList){

                        if(t.getTimeOfTaking().before(nowTime)){
                            checkTimeIsBefore = true;
                            treatmentId++;
                        }
                        else
                        {
                            treatmentId++;
                            checkTimeIsBefore = false;
                            checkNextNotificationTomorrow = true;
                            break;
                        }
                    }
                    if(checkNextNotificationTomorrow == false){ //dann ist die nächste Erinnerung morgen
                        treatmentId = 0;
                    }
                    return treatmentList.get((int)treatmentId);
                }
            }
        }catch (Exception e){
            return null;
        }
        return null;
    }

    private List<Treatment> getListToday(List<Treatment> treatmentList, Calendar c) {
        Date nowDate = new Date(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        nowDate.setYear(116);
        List<Treatment> newTreatmentList = new ArrayList<>();
        for(Treatment t: treatmentList){
            if(t.getStartDate().before(nowDate) && nowDate.before(t.getEndDate()) ||
                    sameDate(t.getStartDate(),nowDate) ||
                    sameDate(t.getEndDate(), nowDate)){

                Date now = new Date();
                Date treatmentDate = new Date();
                treatmentDate.setHours(t.getTimeOfTaking().getHours());
                treatmentDate.setMinutes(t.getTimeOfTaking().getMinutes());
                treatmentDate.setSeconds(t.getTimeOfTaking().getSeconds());

                if (treatmentDate.after(now)) {
                    newTreatmentList.add(t);
                }
            }
        }
        return newTreatmentList;
    }

    private boolean sameDate(Date startDateInDate, Date nowDate) {
        if(startDateInDate.getDate() == nowDate.getDate() && startDateInDate.getMonth() == nowDate.getMonth()) // && startDateInDate.getYear() == nowDate.getYear()
            return true;
        return false;
    }

    //11.06.2016 Forster
    public boolean deleteTreatmentById(int position) {
        Treatment treatment = getTreatments().get(position);

        try (SQLiteDatabase db = this.getWritableDatabase()){
            db.delete(Contract.TreatmentEntry.TABLE_NAME,
                    Contract.TreatmentEntry.COLUMN_MEDICINE_ID + " = ?" + " and " +
                            Contract.TreatmentEntry.COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(treatment.getMedicine().getId()),
                            String.valueOf(treatment.getUser().getId())});
        } catch (Exception e) {
            return false;
        }

        //Contract.TreatmentEntry.COLUMN_MEDICINE_ID + " = " + tratment.getMedicine().getId() + " and " + Contract.TreatmentEntry.COLUMN_USER_ID + " = " + tratment.getUser().getId()
        return true;
    }

    public boolean deleteTreatmentByUserMedicine(long userId, String medicineName ){
         Medicine medicine = findMedicineByName(medicineName);

        try (SQLiteDatabase db = this.getWritableDatabase()){
            db.delete(Contract.TreatmentEntry.TABLE_NAME,
                    Contract.TreatmentEntry.COLUMN_MEDICINE_ID + " = ?" + " and " +
                            Contract.TreatmentEntry.COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(medicine.getId()),
                            String.valueOf(userId)});
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public void updateTreatment(Treatment treatment){
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(Contract.TreatmentEntry.COLUMN_END_DATE, treatment.getEndDateToString());
            cv.put(Contract.TreatmentEntry.COLUMN_MEDICINE_ID, treatment.getMedicine().getId());
            cv.put(Contract.TreatmentEntry.COLUMN_START_DATE, treatment.getStartDateToString());
            cv.put(Contract.TreatmentEntry.COLUMN_TIME_OF_TAKING, treatment.getTimeOfTakingToString());
            cv.put(Contract.TreatmentEntry.COLUMN_USER_ID, treatment.getUser().getId());
            cv.put(Contract.TreatmentEntry.COLUMN_NOTE, treatment.getNote());

            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.MedicineEntry.COLUMN_NAME, treatment.getMedicine().getName());
            contentValues.put(Contract.MedicineEntry.COLUMN_ACTIVE_SUBSTANCE, treatment.getMedicine().getActiveSubstance());
            contentValues.put(Contract.MedicineEntry.COLUMN_MED_TYPE, treatment.getMedicine().getMedType().toString());
            contentValues.put(Contract.MedicineEntry.COLUMN_PERIODICITY, treatment.getMedicine().getPeriodicityInDays());

            synchronized (db) {
                db.update(Contract.MedicineEntry.TABLE_NAME,
                        contentValues,
                        Contract.MedicineEntry._ID + " = ?",
                        new String[]{treatment.getMedicine().getId() + ""});

                db.update(Contract.TreatmentEntry.TABLE_NAME,
                        cv,
                        Contract.TreatmentEntry.COLUMN_MEDICINE_ID + " = ? and " +
                                Contract.TreatmentEntry.COLUMN_USER_ID + " = ?",
                        new String[]{treatment.getMedicine().getId() + "", treatment.getUser().getId() + ""});
            }
        }
    }

    public long findCurrentUserId()
    {
        try (SQLiteDatabase db = this.getReadableDatabase()) {

            Cursor cursor = db.query(
                    Contract.UserEntry.TABLE_NAME,
                    null,
                    Contract.UserEntry.COLUMN_CURRENT_USER + " = ?",
                    new String[]{"1"},
                    null,
                    null,
                    null);

            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex(Contract.UserEntry._ID));
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void changeCurrentUserToOtherUser(long changeUserId) {
        long currentUserId = findCurrentUserId();

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(Contract.UserEntry.COLUMN_CURRENT_USER, "0");

            db.update(Contract.UserEntry.TABLE_NAME,
                    cv,
                    Contract.UserEntry._ID + " = ?",
                    new String[]{currentUserId + ""});
        }

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(Contract.UserEntry.COLUMN_CURRENT_USER, "1");

            db.update(Contract.UserEntry.TABLE_NAME,
                    cv,
                    Contract.UserEntry._ID + " = ?",
                    new String[]{changeUserId + ""});
        }
    }
}
