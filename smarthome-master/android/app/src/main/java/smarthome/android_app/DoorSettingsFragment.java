package smarthome.android_app;

import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;

import java.util.HashMap;

public class DoorSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_door, rootKey);
        ListPreference listRoom1 = (ListPreference)getPreferenceScreen()
                .findPreference("pref_door_room1");
        ListPreference listRoom2 = (ListPreference)getPreferenceScreen()
                .findPreference("pref_door_room2");

        HashMap<Integer, String> roomMap = (HashMap<Integer, String>)getArguments().getSerializable("ROOM_MAP");
        if(roomMap == null)
            return;
        CharSequence roomNames[] = new String[roomMap.size()];
        CharSequence roomIDs[] = new String[roomMap.size()];
        int i = 0;
        for(HashMap.Entry<Integer, String> entry : roomMap.entrySet()) {
            roomNames[i] = entry.getValue();
            roomIDs[i] = entry.getKey().toString();
            ++i;
        }
        listRoom1.setEntries(roomNames);
        listRoom1.setEntryValues(roomIDs);
        listRoom2.setEntries(roomNames);
        listRoom2.setEntryValues(roomIDs);
    }

    public static DoorSettingsFragment newInstance(HashMap<Integer, String> roomMap) {
        Bundle args = new Bundle();
        DoorSettingsFragment fragment = new DoorSettingsFragment();
        args.putSerializable("ROOM_MAP", roomMap);
        fragment.setArguments(args);
        return fragment;
    }


}
