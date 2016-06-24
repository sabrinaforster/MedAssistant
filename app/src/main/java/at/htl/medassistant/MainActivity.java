package at.htl.medassistant;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.sql.Time;
import java.text.ParseException;
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
import at.htl.medassistant.model.MedicineDatabaseHelper;
import at.htl.medassistant.model.TreatmentAdapter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MedicineDetailsActivity.class.getName();

    private List<Treatment> treatmentList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TreatmentAdapter adapter;
    private MedicineDatabaseHelper db;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    private LocalService boundService;
    boolean isBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MedicineDetailsActivity.class));
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        adapter = new TreatmentAdapter(treatmentList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

        try{
            prepareTreatmentData();
        }catch (Exception e){
            Log.d(TAG, "MainActivity, onCreate: Fehler beim Einfügen der Daten");
            e.printStackTrace();
        }

        recyclerView.addOnItemTouchListener(new RecycleTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        doBindService();
    }

    private void prepareTreatmentData()  throws Exception {

        db = MedicineDatabaseHelper.getInstance(this);

        User dagobert = new User("Dagobert", "Duck");
        User forster = new User("Sabrina", "Forster");

        try {
            //if (db.findUserByName(dagobert.getFirstName(), dagobert.getLastName()) == null) {
                dagobert.setCurrentUser(true);
                db.insertUser(dagobert);
            //}
            //if (db.findUserByName(forster.getFirstName(), forster.getLastName()) == null) {
                db.insertUser(forster);
            //}
        } catch (Exception e) {
            Log.d(TAG, "MainActivity, prepareTreatmentData: User wurde schon in Db gespeichert");
            e.printStackTrace();
        }

        List<Treatment> treatments = db.getTreatments();

        if(treatments != null && treatments.size() != 0){
            treatmentList.clear();
            treatmentList.addAll(db.getTreatments());
            adapter.notifyDataSetChanged();

        }else
        {
            Medicine aspirin = new Medicine("Aspirin", "Acetylsalicylsäure", MedType.PHARMACEUTICAL, 7);
            aspirin.setId(200L);

            try {
                db.insertMedicine(aspirin);
            } catch (Exception e) {
                Log.d(TAG, "prepareTreatmentData: Medicine wurde schon in die Db gespeichert");
            }

            Medicine fsme = new Medicine("FSME-IMMUN Inject", "FSME-Virus-Antigen", MedType.VACCINE, 365 * 5);
            try {
                db.insertMedicine(fsme);
            } catch (Exception e) {
                Log.d(TAG, "MainActivity, prepareTreatmentData: Medicine wurde schon in die Db gespeichert");
                e.printStackTrace();
            }

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
                Log.d(TAG, "MainActivity, prepareTreatmentData: Parsing Fehler");
                e.printStackTrace();
            }

            //treatmentList.add(treatment);
            try{
                db.insertTreatment(treatment);
            }catch (Exception e){
                Log.d(TAG, "MainActivity, prepareTreatmentData: Treatment wurde schon in die Db gespeichert");
                e.printStackTrace();
            }

            try {
                treatment = new Treatment(
                        dagobert,
                        fsme,
                        sdf.parse("05.05.2016"),
                        sdf.parse("10.06.2016"),
                        Time.valueOf("05:00:00")
                );
            } catch (ParseException e) {
                Log.d(TAG, "MainActivity: Parsing Fehler");
                e.printStackTrace();
            }

            //treatmentList.add(treatment);
            try {
                db.insertTreatment(treatment);
            } catch (Exception e) {
                Log.d(TAG, "MainActivity, prepareTreatmentData: Treatment wurde schon in die Db gespeichert");
                e.printStackTrace();
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        treatmentList.clear();
        if (db.getTreatments() != null) {
            treatmentList.addAll(db.getTreatments());
            adapter.notifyDataSetChanged();

        }

        try {
            boundService.destroyAndCreateNewNotification();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
        try {
            boundService.onDestroy();
            boundService.onCreate();
            boundService.showNotification();
        } catch (Exception e) {
            Log.d(TAG, "MainActivity, onResume: Fehle beim Zerstoeren der Notification");
            e.printStackTrace();
        }*/
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.notifyDataSetChanged();
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            boundService = ((LocalService.LocalBinder) service).getService();
            //boundService.showNotification();

            boundService.newNotification();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            boundService = null;
        }
    };

    private  void doBindService(){
        bindService(new Intent(MainActivity.this, LocalService.class), connection, Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    private void doUnbindService(){
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();

    }
}
