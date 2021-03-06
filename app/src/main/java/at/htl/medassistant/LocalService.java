package at.htl.medassistant;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.phenotype.Flag;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import at.htl.medassistant.entity.Treatment;
import at.htl.medassistant.model.MedicineDatabaseHelper;

public class LocalService extends Service{
    private NotificationManager notificationManager;
    private int NOTIFICATION = 1;

    private final IBinder binder = new LocalBinder();
    private MedicineDatabaseHelper db;

    public static String TREATMENT_INDEX = "treatment_index";

    public class LocalBinder extends Binder {
        LocalService getService(){return LocalService.this;}
    }

    ///// TODO: Later
    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        db = MedicineDatabaseHelper.getInstance(this);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if (intent != null && intent.getFlags() == 5) { //AlarmReceiver
            //Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();
            showNotification();
            intent.setFlags(1);
        }
        if (intent != null && intent.getFlags() == R.id.fabLater) { //NotificationScreen, Later
            Toast.makeText(this, getString(R.string.notification_later), Toast.LENGTH_SHORT).show();
            intent.setFlags(1);
            //this.destroyAndCreateNewNotification();

        }
        if (intent != null && intent.getFlags() == R.id.fabTaken) { //NotificationScreen, Taken
            Toast.makeText(this, getString(R.string.taken), Toast.LENGTH_SHORT).show();
            this.destroyAndCreateNewNotification();
            intent.setFlags(1);
        }

        super.onStart(intent, startId);
    }

    public void showNotification() {
        Treatment treatment = db.getNextTreatment();

        if (treatment != null) {
            int treatmentIndex = getIndexOfTreatment(treatment);
            Intent intent = new Intent(this, NotificationScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(TREATMENT_INDEX, treatmentIndex);
            PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calender = Calendar.getInstance();
            calender.set(Calendar.HOUR, calender.get(Calendar.HOUR));
            calender.set(Calendar.MINUTE, calender.get(Calendar.MINUTE));
            calender.set(Calendar.SECOND, 0);

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Notification notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.medassistant_small_icon)
                    .setWhen(calender.getTimeInMillis()) //System.currentTimeMillis()
                    .setContentTitle(getString(R.string.notification_title1) + treatment.getMedicine().getName() + getString(R.string.notification_title2))
                    .setContentText(getString(R.string.notification_content))
                    .setContentIntent(contentIntent)
                    .setSound(alarmSound)
                    .setVibrate(new long[]{1000, 1000})
                    .build();
            //notification.contentIntent = contentIntent;
            notificationManager.notify(NOTIFICATION, notification);
        }
    }

    private int getIndexOfTreatment(Treatment treatment) {
        List<Treatment> treatments = db.getTreatments();
        for (Treatment t : treatments) {
            if (t.equals(treatment)) {
                return treatments.indexOf(t);
            }
        }
        return -1;
    }

    public void destroyAndCreateNewNotification(){
        onDestroy();
        newNotification();
    }

    public void newNotification(){
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 1, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        Treatment treatment = db.getNextTreatment();
        if (treatment != null) {
            Toast.makeText(this, treatment.getTimeOfTakingToString(), Toast.LENGTH_LONG).show();
            Calendar calender = Calendar.getInstance();
            Date now = new Date();
            calender.set(now.getYear()+1900, now.getMonth(), now.getDate(),
                    treatment.getTimeOfTaking().getHours(), treatment.getTimeOfTaking().getMinutes(), 0);

            alarmManager.set(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), pendingIntent);
        }
    }

    @Override
    public void onDestroy() {
        notificationManager.cancelAll();

        Toast.makeText(this, "Local Notification stopped!", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
       return binder;
    }
}
