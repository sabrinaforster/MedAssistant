package at.htl.medassistant.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLDataException;
import java.text.SimpleDateFormat;

import at.htl.medassistant.entity.MedType;
import at.htl.medassistant.entity.Medicine;
import at.htl.medassistant.entity.Treatment;
import at.htl.medassistant.entity.User;

/**
 * @see <a href="https://cmanios.wordpress.com/2012/05/17/extend-sqliteopenhelper-as-a-singleton-class-in-android/">cmanios: Extend SQLiteOpenHelper as a singleton class in Android</a>
 */
public class MedicineDatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = MedicineDatabaseHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "medicine_db";
    private static MedicineDatabaseHelper mInstance;
    static SQLiteDatabase mDb;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    private MedicineDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized MedicineDatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MedicineDatabaseHelper(context);
        }
        return mInstance;
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        if ((mDb == null) || (!mDb.isOpen())) {
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
        db.execSQL("DROP TABLE IF EXISTS " + Contract.MedicineEntry.TABLE_NAME);
        onCreate(db);
    }

    /**
     * stores user in db
     *
     * @param user
     * @return generated id
     */
    public long insertUser(User user) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Contract.UserEntry.COLUMN_FIRST_NAME, user.getFirstName());
        values.put(Contract.UserEntry.COLUMN_LAST_NAME, user.getLastName());
        return db.insertOrThrow(Contract.UserEntry.TABLE_NAME, null, values);
    }

    public long insertMedicine(Medicine medicine) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Contract.MedicineEntry.COLUMN_NAME, medicine.getName());
        values.put(Contract.MedicineEntry.COLUMN_ACTIVE_SUBSTANCE, medicine.getActiveSubstance());
        values.put(Contract.MedicineEntry.COLUMN_MED_TYPE, medicine.getMedType().name());
        values.put(Contract.MedicineEntry.COLUMN_PERIODICITY, medicine.getPeriodicityInDays());
        return db.insertOrThrow(Contract.MedicineEntry.TABLE_NAME, null, values);
    }

    public long insertTreatment(Treatment treatment) throws SQLDataException {

        // check if User available in db
        User u;
        try {
            u = findUserById(treatment.getUser().getId());
        } catch (Exception e) {
            throw new SQLDataException("User '" + treatment.getUser().toString() + "' not found");
        }

        // check if Medicine available
        Medicine m;
        try {
            m = findMedicineById(treatment.getMedicine().getId());
        } catch (Exception e) {
            throw new SQLDataException("Medicine '" + treatment.getMedicine().getName() + "' not found");
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(Contract.TreatmentEntry.COLUMN_START_DATE, sdf.format(treatment.getStartDate()));
        v.put(Contract.TreatmentEntry.COLUMN_END_DATE, sdf.format(treatment.getEndDate()));
        v.put(Contract.TreatmentEntry.COLUMN_TIME_OF_TAKING, treatment.getTimeOfTakingToString());
        v.put(Contract.TreatmentEntry.COLUMN_USER_ID, treatment.getUser().getId());
        v.put(Contract.TreatmentEntry.COLUMN_MEDICINE_ID, treatment.getMedicine().getId());

        return db.insertOrThrow(Contract.TreatmentEntry.TABLE_NAME, null, v);
    }

    public User findUserById(long id) {
        try (SQLiteDatabase db = getReadableDatabase()) {
            Cursor cursor = db.query(
                    Contract.UserEntry.TABLE_NAME,      // Table to Query
                    null,                               // leaving "columns" null just returns all the columns.
                    Contract.UserEntry._ID + " = ?",                         // cols for "where" clause
                    new String[]{String.valueOf(id)},   // values for "where" clause
                    null,                               // columns to group by
                    null,                               // columns to filter by row groups
                    null                                // sort order
            );

            if (cursor != null) {
                cursor.moveToFirst();
            } else {
                return null;
            }

            User user = new User(cursor.getLong(0), cursor.getString(1), cursor.getString(2));
            return user;
        }
    }

    public User findUserByName(String firstName, String lastName) {
        try (SQLiteDatabase db = getReadableDatabase()) {
            Cursor cursor = db.query(
                    Contract.UserEntry.TABLE_NAME,                      // Table to Query
                    null,                                               // leaving "columns" null just returns all the columns.
                    Contract.UserEntry.COLUMN_FIRST_NAME + " = ? AND "
                            + Contract.UserEntry.COLUMN_LAST_NAME + " = ?",     // cols for "where" clause
                    new String[]{firstName, lastName},                   // values for "where" clause
                    null,                                               // columns to group by
                    null,                                               // columns to filter by row groups
                    null                                                // sort order
            );

            if (cursor != null) {
                cursor.moveToFirst();
            } else {
                return null;
            }

            User user = new User(cursor.getLong(0), cursor.getString(1), cursor.getString(2));
            return user;
        }

    }

    public Medicine findMedicineById(long id) {
        try (SQLiteDatabase db = getReadableDatabase()) {
            Cursor c = db.query(
                    Contract.MedicineEntry.TABLE_NAME,      // Table to Query
                    null,                                   // leaving "columns" null just returns all the columns.
                    Contract.MedicineEntry._ID + " = ?",    // cols for "where" clause
                    new String[]{String.valueOf(id)},       // values for "where" clause
                    null,                                   // columns to group by
                    null,                                   // columns to filter by row groups
                    null                                    // sort order
            );

            if (c != null) {
                c.moveToFirst();
            } else {
                return null;
            }

            Medicine m = new Medicine(
                    c.getLong(0),                       // id
                    c.getString(1),                     // name
                    c.getString(2),                     // activeSubstance
                    MedType.valueOf(c.getString(3)),    // medType
                    c.getInt(4));                       // perioicity
            return m;
        }

    }

    public Medicine findMedicineByName(String name) {
        try (SQLiteDatabase db = getReadableDatabase()) {
            Cursor c = db.query(
                    Contract.MedicineEntry.TABLE_NAME,              // Table to Query
                    null,                                           // leaving "columns" null just returns all the columns.
                    Contract.MedicineEntry.COLUMN_NAME + " = ?",    // cols for "where" clause
                    new String[]{name},                             // values for "where" clause
                    null,                                           // columns to group by
                    null,                                           // columns to filter by row groups
                    null                                            // sort order
            );

            if (c != null) {
                c.moveToFirst();
            } else {
                return null;
            }

            Medicine m = new Medicine(
                    c.getLong(0),                       // id
                    c.getString(1),                     // name
                    c.getString(2),                     // activeSubstance
                    MedType.valueOf(c.getString(3)),    // medType
                    c.getInt(4));                       // perioicity
            return m;
        }


    }


//    public List<Treatment> getTreatment(){
//
//        List<Treatment> treatmentList = new LinkedList<>();
//
//        //bekommt man alle Treatments
//        String sqlStatement = "SELECT " +
//                Contract.TreatmentEntry.COLUMN_MEDICINE_ID + ", " +
//                Contract.TreatmentEntry.COLUMN_START_DATE + ", " +
//                Contract.TreatmentEntry.COLUMN_END_DATE +", " +
//                Contract.TreatmentEntry.COLUMN_TIME_OF_TAKING + ","+
//                Contract.TreatmentEntry.COLUMN_USER_ID + ", " +
//                Contract.TreatmentEntry.COLUMN_DELETE_ROW + ", " +
//                Contract.TreatmentEntry._ID +
//                " FROM " + Contract.TreatmentEntry.TABLE_NAME +
//                " WHERE " + Contract.TreatmentEntry.COLUMN_DELETE_ROW + " = 0";
//
//
//        Cursor cursor = mDb.rawQuery(sqlStatement, null);
//
//        int i = 0;
//        if (null != cursor) {
//            while ((cursor.moveToNext())){
//                long medicineId = cursor.getLong(0);
//                String startdate = cursor.getString(1);
//                String enddate = cursor.getString(2);
//                String time_of_taking = cursor.getString(3);
//                int userId = cursor.getInt(4);
//                int delete_row = cursor.getInt(5);
//                long id = cursor.getLong(6);
//
//                Medicine medicine = null;
//                if(medicineId > -1 && delete_row == 0) {
//                    String sqlGetMedicine = "SELECT " +
//                            Contract.MedicineEntry.COLUMN_NAME + ", " +
//                            Contract.MedicineEntry.COLUMN_ACTIVE_SUBSTANCE + ", " +
//                            Contract.MedicineEntry.COLUMN_IS_DRUG + ", " +
//                            Contract.MedicineEntry._ID +
//                            " FROM " + Contract.MedicineEntry.TABLE_NAME +
//                            " WHERE " + Contract.TreatmentEntry._ID + " == " + medicineId;
//                    //" AND " + Contract.TreatmentEntry.COLUMN_DELETE_ROW + " == 0" ;
//                    //sqlGetMedicine = "SELECT * FROM " + Contract.MedicineEntry.TABLE_NAME +
//                    // " WHERE " + Contract.TreatmentEntry.COLUMN_ID + " == " + medicineId;
//
//                    Cursor cursorMedicine = mDb.rawQuery(sqlGetMedicine, null);
//                    while (cursorMedicine.moveToNext()){
//                        String name = cursorMedicine.getString(0);
//                        String activeSubstance = cursorMedicine.getString(1);
//                        Boolean isDrug = (cursorMedicine.getString(2).equals("1"));
//                        medicineId = cursorMedicine.getLong(3);
//                        medicine = new Medicine(medicineId,name, activeSubstance, isDrug);
//
//                    }
//                }
//
//                treatmentList.add(new Treatment(id, medicine, startdate, enddate, time_of_taking));
//            }
//            cursor.close();
//        }
//
//        return treatmentList;
//    }
//
//    public boolean setDeleteTreatment(Treatment treatment) {
//
//        //UPDATE table_name SET column1=value1,column2=value2,... WHERE some_column=some_value;
//        String sqlStatement = "UPDATE " +
//                Contract.TreatmentEntry.TABLE_NAME + " SET " +
//                Contract.TreatmentEntry.COLUMN_DELETE_ROW + " = 1 WHERE _id = "+treatment.getId();
//
//        //Cursor cursor = mDb.rawQuery(sqlStatement, null);
//        ContentValues args = new ContentValues();
//        args.put(Contract.TreatmentEntry.COLUMN_DELETE_ROW, "1");
//
//        //mDb.update(Contract.TreatmentEntry.TABLE_NAME, args, "_id=" +treatment.getId(), null);
//        mDb.execSQL(sqlStatement);
//
//        return true;
//    }
//
//    private static Context repoContext;
//
//    public static void setContext(Context context){
//        repoContext = context;
//    }
//
//    public int changeTreatment(Treatment treatment) {
//
//        //Update Treatment
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(Contract.TreatmentEntry.COLUMN_TIME_OF_TAKING, treatment.getTimeOfTakingToString());
//        contentValues.put(Contract.TreatmentEntry.COLUMN_END_DATE, treatment.getEndDateToString());
//        contentValues.put(Contract.TreatmentEntry.COLUMN_START_DATE, treatment.getStartDateToString());
//
//        mDb.update(
//                Contract.TreatmentEntry.TABLE_NAME,
//                contentValues, "_id = " + treatment.getId(),
//                null
//        );
//
//        //Update Medicine
//        ContentValues contentValues1 = new ContentValues();
//        contentValues1.put(Contract.MedicineEntry.COLUMN_NAME, treatment.getMedicine().getName());
//        contentValues1.put(Contract.MedicineEntry.COLUMN_ACTIVE_SUBSTANCE, treatment.getMedicine().getActiveSubstance());
//
//        return  mDb.update(
//                Contract.MedicineEntry.TABLE_NAME,
//                contentValues1,
//                "_id = "+treatment.getMedicine().getId(),
//                null
//        );
//    }
//
//    public Treatment getNextTreatment() {
//        try {
//            Calendar c = Calendar.getInstance();
//            List<Treatment> treatmentList = getTreatment();
//            if(treatmentList.size() > 0)
//            {
//                treatmentList = getListToday(treatmentList, c);
//
//                Time nowTime = new Time(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
//
//                boolean checkTimeIsBefore = false;
//                boolean checkNextNotificationTomorrow = false;
//                long treatmentId = -1;
//
//
//                if(treatmentList.size() > 0){
//                    Collections.sort(treatmentList);
//                    if(treatmentList.size() == 1)
//                    {
//                        return treatmentList.get(0);
//                    }
//                    for(Treatment t : treatmentList){
//
//                        if(t.getTimeOfTaking().before(nowTime)){
//                            checkTimeIsBefore = true;
//                            treatmentId++;
//                        }
//                        else
//                        {
//                            treatmentId++;
//                            checkTimeIsBefore = false;
//                            checkNextNotificationTomorrow = true;
//                            break;
//                        }
//                    }
//                    if(checkNextNotificationTomorrow == false){ //dann ist die nächste Erinnerung morgen
//                        treatmentId = 0;
//                    }
//                    return treatmentList.get((int)treatmentId);
//                }
//            }
//        }catch (Exception e){
//            return null;
//        }
//        return null;
//    }
//
//    //alle Treatments von heute
//    private List<Treatment> getListToday(List<Treatment> treatmentList, Calendar c) {
//        Date nowDate = new Date(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
//        nowDate.setYear(116);
//        //Date nowDate = new Date(2016, 4, 16);
//        List<Treatment> newTreatmentList = new ArrayList<>();
//        for(Treatment t: treatmentList){
//            if(t.getStartDateInDate().before(nowDate) && nowDate.before(t.getEndDateInDate()) || sameDate(t.getStartDateInDate(),nowDate) || sameDate(t.getEndDateInDate(), nowDate)){
//                newTreatmentList.add(t);
//            }
//        }
//        return newTreatmentList;
//    }
//
//    private boolean sameDate(Date startDateInDate, Date nowDate) {
//        if(startDateInDate.getDate() == nowDate.getDate() && startDateInDate.getMonth() == nowDate.getMonth()) // && startDateInDate.getYear() == nowDate.getYear()
//            return true;
//        return false;
//    }
}
