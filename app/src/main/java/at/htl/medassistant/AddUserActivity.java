package at.htl.medassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import at.htl.medassistant.entity.User;
import at.htl.medassistant.model.MedicineDatabaseHelper;
import at.htl.medassistant.model.UserAdapter;

public class AddUserActivity extends AppCompatActivity {
    MedicineDatabaseHelper db;
    EditText firstname, lastname;
    Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = MedicineDatabaseHelper.getInstance(getBaseContext());

        firstname = (EditText) findViewById(R.id.editTextUserFirstName);
        lastname = (EditText) findViewById(R.id.editTextUserLastName);

        buttonSave = (Button) findViewById(R.id.buttonSaveUser);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User(firstname.getText().toString(), lastname.getText().toString());
                if (db.getUsers() == null && db.getUsers().size() == 0) {
                    user.setCurrentUser(true);
                }
                db.insertUser(user);
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                startActivity(intent);
            }
        });
    }

}
