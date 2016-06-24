package at.htl.medassistant;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import at.htl.medassistant.entity.Treatment;
import at.htl.medassistant.model.MedicineDatabaseHelper;

public class LocalService extends Service{
    private NotificationManager notificationManager;
    private int NOTIFICATION = 1;

    private final IBinder binder = new LocalBinder();

    private MedicineDatabaseHelper db;

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
        Intent intent = new Intent(this, NotificationScreen.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Treatment treatment = db.getNextTreatment();

        if (treatment != null) {
            Calendar calender = Calendar.getInstance();
            calender.set(Calendar.HOUR, calender.get(Calendar.HOUR));
            calender.set(Calendar.MINUTE, calender.get(Calendar.MINUTE));
            calender.set(Calendar.SECOND, 0);

            Notification notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.medassistant_small_icon)
                    .setTicker("Ticker")
                    .setWhen(calender.getTimeInMillis()) //System.currentTimeMillis()
                    .setContentTitle("You have to take " + treatment.getMedicine().getName() + " now.")
                    .setContentText("Choose Later or Taken")
                    .setContentIntent(contentIntent)
                    .build();

            notificationManager.notify(NOTIFICATION, notification);
        }
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
        notificationManager.cancel(NOTIFICATION);

        Toast.makeText(this, "Local Service stopped!", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
       return binder;
    }
}
