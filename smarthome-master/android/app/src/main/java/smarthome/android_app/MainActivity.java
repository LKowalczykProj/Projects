package smarthome.android_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawer;
    SmartHomeApiClient apiClient;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
        drawer = findViewById(R.id.home_drawer);
        ActionBarDrawerToggle toggleButton = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer.addDrawerListener(toggleButton);

        sharedPreferences = getSharedPreferences(getString(R.string.key_user_data), Context.MODE_PRIVATE);
        // load saved token from SharedPreferences
        String savedToken = sharedPreferences.getString(getString(R.string.key_token), null);
        if(savedToken == null) {
            // something went wrong
            // remove saved token and go back to login screen
            sharedPreferences.edit().remove(getString(R.string.key_token)).apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        apiClient = new SmartHomeApiClient(getString(R.string.url_server), savedToken);

        changeFragment(HomeFragment.class);
        setTitle(R.string.label_home_screen);

        NavigationView navView = findViewById(R.id.home_nav_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.nav_logout) {
                    // remove token from shared preferences
                    sharedPreferences.edit().remove(getString(R.string.key_token)).apply();
                    // go back to login activity
                    Toast.makeText(MainActivity.this, getString(R.string.msg_logged_out),
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else
                    selectDrawerItem(menuItem);
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    void selectDrawerItem(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nav_home:
                changeFragment(HomeFragment.class);
                break;
            case R.id.nav_rooms:
                changeFragment(RoomsFragment.class);
                break;
            case R.id.nav_devices:
                changeFragment(DevicesFragment.class);
                break;
//            case R.id.nav_settings:
//                changeFragment(HomeSettingsFragment.class);
//                break;
            default:
                changeFragment(HomeFragment.class);

        }

        setTitle(item.getTitle());
        drawer.closeDrawers();
    }

    void changeFragment(Class fragmentClass) {
        Fragment fragment = null;
        try {
            fragment = (Fragment)fragmentClass.newInstance();
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frame_content, fragment).commit();
    }

    public SmartHomeApiClient getApiClient() {
        return apiClient;
    }
}
