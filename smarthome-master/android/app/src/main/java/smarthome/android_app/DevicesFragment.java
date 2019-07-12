package smarthome.android_app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.api.client.util.Data;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;

public class DevicesFragment extends Fragment {

    RecyclerView recyclerView;
    DeviceAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    Type lampType, doorType, rtvType, roomType;
    List<SmartHomeApiClient.Device> devices;
    HashMap<Integer, String> roomMap = new HashMap<>();
    SmartHomeApiClient apiClient;
    Timer timer;
    TimerTask timerTask;


    DeviceAdapter.Listener deviceListener = new DeviceAdapter.Listener() {
        @Override
        public void switchStateOnClick(View v, int position) {
            SmartHomeApiClient.Device device = adapter.getDevice(position);
            device.state = ((Switch) v).isChecked();
            device.localPosition = position;
            new DevicePutTask().execute(device);
        }

        @Override
        public void sliderLampIntensityOnChanged(SeekBar seekBar, int position) {
            SmartHomeApiClient.Lamp lamp = (SmartHomeApiClient.Lamp) adapter.getDevice(position);
            lamp.intensity = seekBar.getProgress();
            lamp.localPosition = position;
            new DevicePutTask().execute(lamp);
        }

        @Override
        public void sliderRTVVolumeOnChanged(SeekBar seekBar, int position) {
            SmartHomeApiClient.RTV rtv = (SmartHomeApiClient.RTV) adapter.getDevice(position);
            rtv.volume = seekBar.getProgress();
            rtv.localPosition = position;
            new DevicePutTask().execute(rtv);
        }

        @Override
        public void buttonSettingsOnClick(int position) {
            SmartHomeApiClient.Device device = adapter.getDevice(position);
            device.localPosition = position;
            if (Data.isNull(device.device))
                device.device = null;
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            intent.putExtra("ROOM_MAP", roomMap);
            intent.putExtra("ENTITY", device);
            startActivityForResult(intent, 1);
        }

        @Override
        public void buttonFavouritesOnClick(View v, int position) {
            SmartHomeApiClient.Device device = adapter.getDevice(position);
            device.favourite = ((ToggleButton) v).isChecked();
            device.localPosition = position;
            new DevicePutTask().execute(device);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiClient = ((MainActivity) getActivity()).getApiClient();
        timer = new Timer(false);
        doorType = new TypeToken<List<SmartHomeApiClient.Door>>() {
        }.getType();
        rtvType = new TypeToken<List<SmartHomeApiClient.RTV>>() {
        }.getType();
        lampType = new TypeToken<List<SmartHomeApiClient.Lamp>>() {
        }.getType();
        roomType = new TypeToken<List<SmartHomeApiClient.Room>>() {
        }.getType();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices, container, false);

        recyclerView = view.findViewById(R.id.device_recycler_view);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new DeviceAdapter(deviceListener);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                new DeviceTask().execute();
            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, 1000);

    }

    @Override
    public void onStop() {
        super.onStop();

        timerTask.cancel();
        timer.purge();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            SmartHomeApiClient.Device d = (SmartHomeApiClient.Device) data.getSerializableExtra("ENTITY");
            new DevicePutTask().execute(d);
        }
    }

    class DeviceTask extends AsyncTask<String, Void, List<SmartHomeApiClient.Device>> {
        @Override
        protected List<SmartHomeApiClient.Device> doInBackground(String... credentials) {

            List<SmartHomeApiClient.Lamp> lamps;
            List<SmartHomeApiClient.RTV> rtvs;
            List<SmartHomeApiClient.Door> doors;
            List<SmartHomeApiClient.Room> rooms;
            devices = new ArrayList<>();

            // get list of rooms to map their IDs to names
            rooms = (List<SmartHomeApiClient.Room>) apiClient.getList("Room", roomType);
            if (rooms == null)
                return null;
            for (SmartHomeApiClient.Room room : rooms) {
                roomMap.put(room.id, room.name);
            }
            adapter.setRoomMap(roomMap);


            lamps = (List<SmartHomeApiClient.Lamp>) apiClient.getList("Lamp", lampType);
            if (lamps == null)
                return null;
            devices.addAll(lamps);

            doors = (List<SmartHomeApiClient.Door>) apiClient.getList("Door", doorType);
            if (doors == null)
                return null;
            devices.addAll(doors);

            rtvs = (List<SmartHomeApiClient.RTV>) apiClient.getList("RTV", rtvType);
            if (rtvs == null)
                return null;
            devices.addAll(rtvs);

            return devices;
        }

        @Override
        protected void onPostExecute(List<SmartHomeApiClient.Device> result) {
            if (result != null) {
                adapter.updateDevices(result);
            } else {
                Toast.makeText(getActivity(), R.string.msg_request_error, Toast.LENGTH_SHORT).show();
            }
        }
    }


    class DevicePutTask extends AsyncTask<SmartHomeApiClient.Device, Void, SmartHomeApiClient.Device> {
        @Override
        protected SmartHomeApiClient.Device doInBackground(SmartHomeApiClient.Device... device) {
            Class type;
            String typeStr;
            if (device[0] instanceof SmartHomeApiClient.Lamp) {
                type = SmartHomeApiClient.Lamp.class;
                typeStr = "Lamp";
            } else if (device[0] instanceof SmartHomeApiClient.RTV) {
                type = SmartHomeApiClient.RTV.class;
                typeStr = "RTV";
            } else if (device[0] instanceof SmartHomeApiClient.Door) {
                type = SmartHomeApiClient.Door.class;
                typeStr = "Door";
            } else {
                return null;
            }

            SmartHomeApiClient.Device newDevice = (SmartHomeApiClient.Device) apiClient.putObject(
                    typeStr, type, device[0].id, device[0]);

            if (newDevice != null) {
                newDevice.localPosition = device[0].localPosition;
            }

            return newDevice;
        }

        @Override
        protected void onPostExecute(SmartHomeApiClient.Device device) {
            if (device != null) {
                adapter.updateDevice(device, device.localPosition);
            } else {
                Toast.makeText(getActivity(), R.string.msg_request_error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
