package smarthome.android_app;

import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;

import java.util.HashMap;

public class LampSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_lamp, rootKey);
        ListPreference listRoom = (ListPreference)getPreferenceScreen()
                .findPreference("pref_lamp_room");

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
        listRoom.setEntries(roomNames);
        listRoom.setEntryValues(roomIDs);
    }

    public static LampSettingsFragment newInstance(HashMap<Integer, String> roomMap) {
        Bundle args = new Bundle();
        LampSettingsFragment fragment = new LampSettingsFragment();
        args.putSerializable("ROOM_MAP", roomMap);
        fragment.setArguments(args);
        return fragment;
    }


}
