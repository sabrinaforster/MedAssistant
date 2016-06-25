package at.htl.medassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import at.htl.medassistant.entity.Treatment;
import at.htl.medassistant.model.MedicineDatabaseHelper;

public class NotificationScreen extends AppCompatActivity {
    private FloatingActionButton fabLater;
    private FloatingActionButton fabTaken;

    private TextView currentTime;
    private TextView treatmentInfo;

    private MedicineDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent intent = new Intent(getApplicationContext(), LocalService.class);

        fabLater = (FloatingActionButton) findViewById(R.id.fabLater);
        fabTaken = (FloatingActionButton) findViewById(R.id.fabTaken);

        currentTime = (TextView) findViewById(R.id.currentTime);
        treatmentInfo = (TextView) findViewById(R.id.treatmentInfo);

        Bundle extras = getIntent().getExtras();
        int index = extras.getInt(LocalService.TREATMENT_INDEX);
        db = MedicineDatabaseHelper.getInstance(this);
        Treatment treatment = db.getTreatments().get(index);
        treatmentInfo.setText(treatment.toString());

        setCurrentTimeInGui();
        startTime(this);

        fabLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.setFlags(R.id.fabLater);
                getBaseContext().startService(intent);
                finish();
            }
        });

        fabTaken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.setFlags(R.id.fabTaken);
                getBaseContext().startService(intent);
                finish();
            }
        });
    }

    public void startTime(final Activity activity){
        int remindertime = 1000;
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setCurrentTimeInGui();
                    }
                });
            }
        }, 0, remindertime);
    }

    private void setCurrentTimeInGui(){
        Calendar nowCalendar = Calendar.getInstance();
        Time nowTime = new Time(nowCalendar.getTimeInMillis());

        Treatment treatment = new Treatment();
        treatment.setTimeOfTaking(nowTime);

        try {
            currentTime.setText(treatment.getTimeOfTakingToString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
