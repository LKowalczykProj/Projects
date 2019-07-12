package smarthome.android_app;

import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;

import java.util.HashMap;

public class RoomSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_room, rootKey);
    }

    public static RoomSettingsFragment newInstance() {
        return new RoomSettingsFragment();
    }


}
