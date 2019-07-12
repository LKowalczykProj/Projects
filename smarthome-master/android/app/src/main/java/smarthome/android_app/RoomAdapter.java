package smarthome.android_app;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<SmartHomeApiClient.Room> roomList = null;

    public interface Listener {
        void buttonSettingsOnClick(int position);
        void buttonFavouritesOnClick(View v, int position);
    }

    private Listener roomListener;

    public RoomAdapter(Listener roomListener) {
        this.roomListener = roomListener;
    }

    class RoomHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public RoomHolder(CardView v) {
            super(v);
            cardView = v;

            ImageView buttonSettings = cardView.findViewById(R.id.button_room_settings);
            buttonSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    roomListener.buttonSettingsOnClick(getAdapterPosition());
                }
            });

            ToggleButton buttonFavourites = cardView.findViewById(R.id.button_room_favourites);
            buttonFavourites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    roomListener.buttonFavouritesOnClick(v, getAdapterPosition());
                }
            });
        }

        public void setData(SmartHomeApiClient.Room room) {
            TextView textName = cardView.findViewById(R.id.text_room_name);
            TextView textTemperature = cardView.findViewById(R.id.text_room_temperature);
            TextView textHumidity = cardView.findViewById(R.id.text_room_humidity);
            TextView textMotion = cardView.findViewById(R.id.text_room_motion);
            ToggleButton buttonFavourites = cardView.findViewById(R.id.button_room_favourites);
            textName.setText(room.name);
            textTemperature.setText(String.format("%.1f℃", room.temperature));
            textHumidity.setText(String.format("%.1f%%", room.humidity));
            textMotion.setText(room.people ? R.string.label_yes : R.string.label_no);
            buttonFavourites.setChecked(room.favourite);
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        CardView v = (CardView)LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_room,
                viewGroup, false);
        return new RoomHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((RoomHolder)viewHolder).setData(roomList.get(i));
    }

    @Override
    public int getItemCount() {
        return roomList == null ? 0 : roomList.size();
    }

    public void updateRooms(List<SmartHomeApiClient.Room> list) {
        roomList = list;
        notifyDataSetChanged();
    }
    public void updateRoom(SmartHomeApiClient.Room room, int position) {
        roomList.set(position, room);
        notifyItemChanged(position);
    }

    public SmartHomeApiClient.Room getRoom(int position) {
        return roomList.get(position);
    }
}
