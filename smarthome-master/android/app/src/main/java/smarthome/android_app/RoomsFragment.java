package smarthome.android_app;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.api.client.util.Data;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;

public class RoomsFragment extends Fragment {

    RecyclerView recyclerView;
    RoomAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    Timer timer;
    TimerTask timerTask;


    RoomAdapter.Listener roomListener = new RoomAdapter.Listener() {
        @Override
        public void buttonSettingsOnClick(int position) {
            SmartHomeApiClient.Room room = adapter.getRoom(position);
            room.localPosition = position;
            if(Data.isNull(room.device))
                room.device = null;
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            intent.putExtra("ENTITY", room);
            startActivityForResult(intent, 1);
        }

        @Override
        public void buttonFavouritesOnClick(View v, int position) {
            SmartHomeApiClient.Room room = adapter.getRoom(position);
            room.favourite = ((ToggleButton) v).isChecked();
            room.localPosition = position;
            new RoomPutTask().execute(room);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            SmartHomeApiClient.Room r = (SmartHomeApiClient.Room)data.getSerializableExtra("ENTITY");
            new RoomPutTask().execute(r);
        }
    }

    SmartHomeApiClient apiClient;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rooms, container, false);

        recyclerView = view.findViewById(R.id.room_recycler_view);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RoomAdapter(roomListener);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timer = new Timer(false);
        apiClient = ((MainActivity)getActivity()).getApiClient();
    }

    @Override
    public void onStart() {
        super.onStart();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                new RoomTask().execute();
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

    class RoomTask extends AsyncTask<String, Void, List<SmartHomeApiClient.Room>> {
        @Override
        protected List<SmartHomeApiClient.Room> doInBackground(String... credentials) {
            Type roomType = new TypeToken<List<SmartHomeApiClient.Room>>() {}.getType();
            return (List<SmartHomeApiClient.Room>)apiClient.getList("Room", roomType);
        }

        @Override
        protected void onPostExecute(List<SmartHomeApiClient.Room> result) {
            if(result != null) {
                adapter.updateRooms(result);
            } else {
                Toast.makeText(getActivity(), R.string.msg_request_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    class RoomPutTask extends AsyncTask<SmartHomeApiClient.Room, Void, SmartHomeApiClient.Room> {
        @Override
        protected SmartHomeApiClient.Room doInBackground(SmartHomeApiClient.Room... room) {
            Class type = SmartHomeApiClient.Room.class;
            String typeStr = "Room";

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
                adapter.updateRoom(room, room.localPosition);
            } else {
                Toast.makeText(getActivity(), R.string.msg_request_error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
