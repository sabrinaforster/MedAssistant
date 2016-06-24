package at.htl.medassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class NotificationScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent intent = new Intent(getApplicationContext(), LocalService.class);

        FloatingActionButton fabLater = (FloatingActionButton) findViewById(R.id.fabLater);
        FloatingActionButton fabTaken = (FloatingActionButton) findViewById(R.id.fabTaken);

        fabLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.setFlags(R.id.fabLater);
                getBaseContext().startService(intent);
            }
        });

        fabTaken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.setFlags(R.id.fabTaken);
                getBaseContext().startService(intent);
            }
        });

    }

}
