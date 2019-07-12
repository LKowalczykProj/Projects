package smarthome.android_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    static final String logTag = "login";
    SmartHomeApiClient apiClient = null;

    SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setSupportActionBar((Toolbar)findViewById(R.id.login_toolbar));
        Button loginButton = findViewById(R.id.button_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText usernameText = findViewById(R.id.text_username);
                EditText passwordText = findViewById(R.id.text_password);
                new LoginTask().execute(usernameText.getText().toString(),
                        passwordText.getText().toString());
            }
        });

        sharedPreferences = getSharedPreferences(getString(R.string.key_user_data), Context.MODE_PRIVATE);
        // load saved token from SharedPreferences
        String savedToken = sharedPreferences.getString(getString(R.string.key_token), null);
        // skip login screen if token is not null
        if(savedToken != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        apiClient = new SmartHomeApiClient(getString(R.string.url_server));
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    class LoginTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... credentials) {
                return apiClient.login(credentials[0], credentials[1]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result) {
                Toast.makeText(LoginActivity.this,"Zalogowano", Toast.LENGTH_SHORT).show();
                Log.i(logTag, String.format("Token: %s", apiClient.getAuthToken()));
                // store token in preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.key_token), apiClient.getAuthToken());
                editor.apply();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(LoginActivity.this, getString(R.string.msg_login_error),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
