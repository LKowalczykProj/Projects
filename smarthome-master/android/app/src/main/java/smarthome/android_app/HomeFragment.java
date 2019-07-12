package smarthome.android_app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    DeviceAdapter deviceAdapter;
    RoomAdapter roomAdapter;
    Type lampType, doorType, rtvType, roomType;
    RecyclerView recyclerView;
    SmartHomeApiClient apiClient;
    HashMap<Integer, String> roomMap = new HashMap<>();
    RecyclerView.LayoutManager layoutManager;
    List<SmartHomeApiClient.Device> devices;
    Timer timer;
    TimerTask timerTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timer = new Timer(false);
        apiClient = ((MainActivity)getActivity()).getApiClient();
        doorType = new TypeToken<List<SmartHomeApiClient.Door>>() {}.getType();
        rtvType = new TypeToken<List<SmartHomeApiClient.RTV>>() {}.getType();
        lampType = new TypeToken<List<SmartHomeApiClient.Lamp>>() {}.getType();
        roomType = new TypeToken<List<SmartHomeApiClient.Room>>() {}.getType();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView home_rooms = view.findViewById(R.id.home_room_recycler_view);
        RecyclerView home_devices = view.findViewById(R.id.home_device_recycler_view);
        home_rooms.setNestedScrollingEnabled(false);
        home_devices.setNestedScrollingEnabled(false);

        RecyclerView.LayoutManager rooms_layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.LayoutManager devices_layoutManager = new LinearLayoutManager(getActivity());
        home_rooms.setLayoutManager(rooms_layoutManager);
        home_devices.setLayoutManager(devices_layoutManager);

        deviceAdapter = new DeviceAdapter(deviceListener);
        roomAdapter = new RoomAdapter(roomListener);
        home_rooms.setAdapter(roomAdapter);
        home_devices.setAdapter(deviceAdapter);

        return view;
    }


    DeviceAdapter.Listener deviceListener = new DeviceAdapter.Listener() {
        @Override
        public void switchStateOnClick(View v, int position) {
            SmartHomeApiClient.Device device = deviceAdapter.getDevice(position);
            device.state = ((Switch)v).isChecked();
            device.localPosition = position;
            new HomeFragment.DevicePutTask().execute(device);
        }

        @Override
        public void sliderLampIntensityOnChanged(SeekBar seekBar, int position) {
            SmartHomeApiClient.Lamp lamp = (SmartHomeApiClient.Lamp)deviceAdapter.getDevice(position);
            lamp.intensity = seekBar.getProgress();
            lamp.localPosition = position;
            new HomeFragment.DevicePutTask().execute(lamp);
        }

        @Override
        public void sliderRTVVolumeOnChanged(SeekBar seekBar, int position) {
            SmartHomeApiClient.RTV rtv = (SmartHomeApiClient.RTV)deviceAdapter.getDevice(position);
            rtv.volume = seekBar.getProgress();
            rtv.localPosition = position;
            new HomeFragment.DevicePutTask().execute(rtv);
        }

        @Override
        public void buttonSettingsOnClick(int position) {
            SmartHomeApiClient.Device device = deviceAdapter.getDevice(position);
            device.localPosition = position;
            if(Data.isNull(device.device))
                device.device = null;
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            intent.putExtra("ROOM_MAP", roomMap);
            intent.putExtra("ENTITY", device);
            startActivityForResult(intent, 1);
        }

        @Override
        public void buttonFavouritesOnClick(View v, int position) {
            SmartHomeApiClient.Device device = deviceAdapter.getDevice(position);
            device.favourite = ((ToggleButton) v).isChecked();
            device.localPosition = position;
            new DevicePutTask().execute(device);
            if(!device.favourite) {
                new DeviceTask().execute();
            }
        }

    };


    RoomAdapter.Listener roomListener = new RoomAdapter.Listener() {
        @Override
        public void buttonSettingsOnClick(int position) {
            SmartHomeApiClient.Room room = roomAdapter.getRoom(position);
            room.localPosition = position;
            if(Data.isNull(room.device))
                room.device = null;
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            intent.putExtra("ENTITY", room);
            startActivityForResult(intent, 2);
        }

        @Override
        public void buttonFavouritesOnClick(View v, int position) {
            SmartHomeApiClient.Room room = roomAdapter.getRoom(position);
            room.favourite = ((ToggleButton) v).isChecked();
            room.localPosition = position;
            new RoomPutTask().execute(room);
            if(!room.favourite) {
                new RoomTask().execute();
            }
        }
    };


    @Override
    public void onStart() {

        timerTask = new TimerTask() {
            @Override
            public void run() {
                new RoomTask().execute();
                new DeviceTask().execute();
            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, 1000);
        super.onStart();
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
            SmartHomeApiClient.Device d = (SmartHomeApiClient.Device)data.getSerializableExtra("ENTITY");
            new HomeFragment.DevicePutTask().execute(d);
        }
        else if(requestCode == 2 && resultCode == RESULT_OK){
            SmartHomeApiClient.Room r = (SmartHomeApiClient.Room)data.getSerializableExtra("ENTITY");
            new HomeFragment.RoomPutTask().execute(r);
        }
    }

    class RoomTask extends AsyncTask<String, Void, List<SmartHomeApiClient.Room>> {
        @Override
        protected List<SmartHomeApiClient.Room> doInBackground(String... credentials) {
            Type roomType = new TypeToken<List<SmartHomeApiClient.Room>>() {}.getType();
            return (List<SmartHomeApiClient.Room>)apiClient.getList("FavRoom", roomType);
        }

        @Override
        protected void onPostExecute(List<SmartHomeApiClient.Room> result) {
            if(result != null) {
                roomAdapter.updateRooms(result);
            } else {
                Toast.makeText(getActivity(), R.string.msg_request_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    class RoomPutTask extends AsyncTask<SmartHomeApiClient.Room, Void, SmartHomeApiClient.Room> {
        @Override
        protected SmartHomeApiClient.Room doInBackground(SmartHomeApiClient.Room... room) {
            Class type = SmartHomeApiClient.Room.class;
            String typeStr = "FavRoom";

            SmartHomeApiClient.Room newRoom = (SmartHomeApiClient.Room)apiClient.putObject(
                    typeStr, type, room[0].id, room[0]);

            if(newRoom != null) {
                newRoom.localPosition = room[0].localPosition;
            }

            return newRoom;
        }

        @Override
        protected void onPostExecute(SmartHomeApiClient.Room room) {
            if(room != null) {
                roomAdapter.updateRoom(room, room.localPosition);
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
            if(device[0] instanceof SmartHomeApiClient.Lamp) {
                type = SmartHomeApiClient.Lamp.class;
                typeStr = "Lamp";
            } else if(device[0] instanceof SmartHomeApiClient.RTV) {
                type = SmartHomeApiClient.RTV.class;
                typeStr = "RTV";
            } else if(device[0] instanceof SmartHomeApiClient.Door) {
                type = SmartHomeApiClient.Door.class;
                typeStr = "Door";
            } else {
                return null;
            }

            SmartHomeApiClient.Device newDevice = (SmartHomeApiClient.Device)apiClient.putObject(
                    typeStr, type, device[0].id, device[0]);

            if(newDevice != null) {
                newDevice.localPosition = device[0].localPosition;
            }

            return newDevice;
        }

        @Override
        protected void onPostExecute(SmartHomeApiClient.Device device) {
            if(device != null) {
                deviceAdapter.updateDevice(device, device.localPosition);
            } else {
                Toast.makeText(getActivity(), R.string.msg_request_error, Toast.LENGTH_SHORT).show();
            }
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
            rooms = (List<SmartHomeApiClient.Room>)apiClient.getList("Room", roomType);
            if(rooms == null)
                return null;
            for(SmartHomeApiClient.Room room : rooms) {
                roomMap.put(room.id, room.name);
            }
            deviceAdapter.setRoomMap(roomMap);


            lamps = (List<SmartHomeApiClient.Lamp>)apiClient.getList("FavLamp", lampType);
            if(lamps == null)
                return null;
            devices.addAll(lamps);

            doors = (List<SmartHomeApiClient.Door>)apiClient.getList("FavDoor", doorType);
            if(doors == null)
                return null;
            devices.addAll(doors);

            rtvs = (List<SmartHomeApiClient.RTV>)apiClient.getList("FavRTV", rtvType);
            if(rtvs == null)
                return null;
            devices.addAll(rtvs);

            return devices;
        }

        @Override
        protected void onPostExecute(List<SmartHomeApiClient.Device> result) {
            if(result != null) {
                deviceAdapter.updateDevices(result);
            } else {
                Toast.makeText(getActivity(), R.string.msg_request_error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
