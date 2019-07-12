package smarthome.android_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.Serializable;
import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {
    private HashMap<Integer, String> roomMap;
    Object entity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        roomMap = (HashMap<Integer, String>)intent.getSerializableExtra("ROOM_MAP");
        entity = intent.getSerializableExtra("ENTITY");
        if(entity instanceof SmartHomeApiClient.Lamp) {
            loadLampSettings((SmartHomeApiClient.Lamp)entity);
            setTitle(R.string.title_lamp_settings);
        } else if(entity instanceof SmartHomeApiClient.RTV) {
            loadRTVSettings((SmartHomeApiClient.RTV)entity);
            setTitle(R.string.title_rtv_settings);
        } else if(entity instanceof SmartHomeApiClient.Door) {
            loadDoorSettings((SmartHomeApiClient.Door)entity);
            setTitle(R.string.title_door_settings);
        }  else if(entity instanceof SmartHomeApiClient.Room) {
            loadRoomSettings((SmartHomeApiClient.Room) entity);
            setTitle(R.string.title_room_settings);
        }

        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_save) {
            Serializable e = null;
            if(entity instanceof SmartHomeApiClient.Lamp) {
                e = getLampSettings();
            } else if(entity instanceof SmartHomeApiClient.Door) {
                e = getDoorSettings();
            } else if(entity instanceof SmartHomeApiClient.RTV) {
                e = getRTVSettings();
            } else if(entity instanceof SmartHomeApiClient.Room) {
                e = getRoomSettings();
            }

            if(e == null) {
                Toast.makeText(this, R.string.msg_invalid_data, Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent();
                intent.putExtra("ENTITY", e);
                setResult(RESULT_OK, intent);
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void loadLampSettings(SmartHomeApiClient.Lamp lamp) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        sharedPreferences.edit()
                .putString("pref_lamp_room", lamp.room.toString())
                .putString("pref_lamp_name", lamp.name)
                .putBoolean("pref_lamp_dimmable", lamp.dimmable)
                .putBoolean("pref_lamp_favourite", lamp.favourite)
                .apply();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_settings, LampSettingsFragment.newInstance(roomMap))
                .commit();
    }

    protected SmartHomeApiClient.Lamp getLampSettings() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SmartHomeApiClient.Lamp lamp = (SmartHomeApiClient.Lamp)entity;
        lamp.name = sharedPreferences.getString("pref_lamp_name", "");
        if(lamp.name.isEmpty())
            return null;
        try {
            lamp.room = Integer.valueOf(sharedPreferences.getString("pref_lamp_room", ""));
        } catch(NumberFormatException e) {
            return null;
        }
        lamp.favourite = sharedPreferences.getBoolean("pref_lamp_favourite", lamp.favourite);
        lamp.dimmable = sharedPreferences.getBoolean("pref_lamp_dimmable", lamp.dimmable);
        // TODO: modify physical device id
        return lamp;
    }

    protected void loadRTVSettings(SmartHomeApiClient.RTV rtv) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        sharedPreferences.edit()
                .putString("pref_rtv_room", rtv.room.toString())
                .putString("pref_rtv_name", rtv.name)
                .putBoolean("pref_rtv_favourite", rtv.favourite)
                .apply();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_settings, RTVSettingsFragment.newInstance(roomMap))
                .commit();
    }

    protected SmartHomeApiClient.RTV getRTVSettings() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SmartHomeApiClient.RTV rtv = (SmartHomeApiClient.RTV)entity;
        rtv.name = sharedPreferences.getString("pref_rtv_name", "");
        if(rtv.name.isEmpty())
            return null;
        try {
            rtv.room = Integer.valueOf(sharedPreferences.getString("pref_rtv_room", ""));
        } catch(NumberFormatException e) {
            return null;
        }
        rtv.favourite = sharedPreferences.getBoolean("pref_rtv_favourite", rtv.favourite);
        // TODO: modify physical device id
        return rtv;
    }

    protected void loadDoorSettings(SmartHomeApiClient.Door door) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        sharedPreferences.edit()
                .putString("pref_door_room1", door.room1.toString())
                .putString("pref_door_room2", door.room2.toString())
                .putBoolean("pref_lamp_favourite", door.favourite)
                .apply();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_settings, DoorSettingsFragment.newInstance(roomMap))
                .commit();
    }

    protected SmartHomeApiClient.Door getDoorSettings() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SmartHomeApiClient.Door door = (SmartHomeApiClient.Door)entity;
        try {
            door.room1 = Integer.valueOf(sharedPreferences.getString("pref_door_room1", ""));
            door.room2 = Integer.valueOf(sharedPreferences.getString("pref_door_room2", ""));
        } catch(NumberFormatException e) {
            return null;
        }
        door.favourite = sharedPreferences.getBoolean("pref_door_favourite", door.favourite);
        // TODO: modify physical device id
        return door;
    }

    protected void loadRoomSettings(SmartHomeApiClient.Room room) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        sharedPreferences.edit()
                .putString("pref_room_name", room.name)
                .putBoolean("pref_room_favourite", room.favourite)
                .apply();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_settings, RoomSettingsFragment.newInstance())
                .commit();
    }

    protected SmartHomeApiClient.Room getRoomSettings() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SmartHomeApiClient.Room room = (SmartHomeApiClient.Room)entity;
        room.name = sharedPreferences.getString("pref_room_name", "");
        if(room.name.isEmpty())
            return null;
        room.favourite = sharedPreferences.getBoolean("pref_room_favourite", room.favourite);
        // TODO: modify physical device id
        return room;
    }
}
