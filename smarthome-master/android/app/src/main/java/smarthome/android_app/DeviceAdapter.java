package smarthome.android_app;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.List;
import java.util.Map;

import static android.widget.Toast.*;

public class DeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<SmartHomeApiClient.Device> deviceList = null;
    private Map<Integer, String> roomMap = null;

    public interface Listener {
        void switchStateOnClick(View v, int position);
        void sliderLampIntensityOnChanged(SeekBar seekBar, int position);
        void sliderRTVVolumeOnChanged(SeekBar seekBar, int position);
        void buttonSettingsOnClick(int position);
        void buttonFavouritesOnClick(View v, int position);
    }

    private Listener deviceListener;

    class LampHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        public LampHolder(CardView v) {
            super(v);
            cardView = v;
            Switch switchState = cardView.findViewById(R.id.switch_lamp_state);
            switchState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deviceListener.switchStateOnClick(v, getAdapterPosition());
                }
            });
            SeekBar sliderIntensity = cardView.findViewById(R.id.slider_intensity);
            sliderIntensity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    deviceListener.sliderLampIntensityOnChanged(seekBar, getAdapterPosition());
                }
            });


            ImageView buttonSettings = cardView.findViewById(R.id.button_lamp_settings);
            buttonSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deviceListener.buttonSettingsOnClick(getAdapterPosition());
                }
            });
            ToggleButton buttonFavourites = cardView.findViewById(R.id.button_lamp_favourites);
            buttonFavourites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deviceListener.buttonFavouritesOnClick(v, getAdapterPosition());
                }
            });
        }

        public void setData(SmartHomeApiClient.Lamp lamp) {
            TextView textName = cardView.findViewById(R.id.text_lamp_name);
            TextView textRoom = cardView.findViewById(R.id.text_lamp_room);
            TextView textIntensity = cardView.findViewById(R.id.label_intensity);
            SeekBar sliderIntensity = cardView.findViewById(R.id.slider_intensity);
            Switch switchState = cardView.findViewById(R.id.switch_lamp_state);
            ToggleButton buttonFavourites = cardView.findViewById(R.id.button_lamp_favourites);

            if(!lamp.dimmable) {
                textIntensity.setVisibility(View.GONE);
                sliderIntensity.setVisibility(View.GONE);
            } else {
                textIntensity.setVisibility(View.VISIBLE);
                sliderIntensity.setVisibility(View.VISIBLE);
                sliderIntensity.setProgress(lamp.intensity);
            }
            textName.setText(lamp.name);
            if(roomMap != null) {
                textRoom.setText(roomMap.get(lamp.room));
            }
            switchState.setChecked(lamp.state);
            buttonFavourites.setChecked(lamp.favourite);
        }

    }
    class RTVHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public RTVHolder(CardView v) {
            super(v);
            cardView = v;
            Switch switchState = cardView.findViewById(R.id.switch_rtv_state);
            switchState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deviceListener.switchStateOnClick(v, getAdapterPosition());
                }
            });

            SeekBar sliderVolume = cardView.findViewById(R.id.slider_volume);
            sliderVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    deviceListener.sliderRTVVolumeOnChanged(seekBar, getAdapterPosition());
                }
            });
            ImageView buttonSettings = cardView.findViewById(R.id.button_rtv_settings);
            buttonSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deviceListener.buttonSettingsOnClick(getAdapterPosition());
                }
            });
            ToggleButton buttonFavourites = cardView.findViewById(R.id.button_rtv_favourites);
            buttonFavourites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deviceListener.buttonFavouritesOnClick(v, getAdapterPosition());
                }
            });
        }

        public void setData(SmartHomeApiClient.RTV rtv) {
            TextView textName = cardView.findViewById(R.id.text_rtv_name);
            TextView textRoom = cardView.findViewById(R.id.text_rtv_room);
            SeekBar sliderVolume = cardView.findViewById(R.id.slider_volume);
            Switch switchState = cardView.findViewById(R.id.switch_rtv_state);
            ToggleButton buttonFavourites = cardView.findViewById(R.id.button_rtv_favourites);
            textName.setText(rtv.name);
            if(roomMap != null) {
                textRoom.setText(roomMap.get(rtv.room));
            }
            switchState.setChecked(rtv.state);
            sliderVolume.setProgress(rtv.volume);
            buttonFavourites.setChecked(rtv.favourite);
        }

    }

    class DoorHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public DoorHolder(CardView v) {
            super(v);
            cardView = v;
            Switch switchState = cardView.findViewById(R.id.switch_door_state);
            switchState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deviceListener.switchStateOnClick(v, getAdapterPosition());
                }
            });
            ImageView buttonSettings = cardView.findViewById(R.id.button_door_settings);
            buttonSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deviceListener.buttonSettingsOnClick(getAdapterPosition());
                }
            });
            ToggleButton buttonFavourites = cardView.findViewById(R.id.button_door_favourites);
            buttonFavourites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deviceListener.buttonFavouritesOnClick(v, getAdapterPosition());
                }
            });
        }

        public void setData(SmartHomeApiClient.Door door) {
            TextView textName = cardView.findViewById(R.id.text_door_name);
            Switch switchState = cardView.findViewById(R.id.switch_door_state);
            ToggleButton buttonFavourites = cardView.findViewById(R.id.button_door_favourites);
            if(roomMap != null) {
                textName.setText(String.format("%s - %s", roomMap.get(door.room1),
                        roomMap.get(door.room2)));
            }
            switchState.setChecked(door.state);
            buttonFavourites.setChecked(door.favourite);
        }

    }

    public DeviceAdapter(Listener deviceListener) {
        this.deviceListener = deviceListener;
    }

    public Map<Integer, String> getRoomMap() {
        return roomMap;
    }

    public void setRoomMap(Map<Integer, String> roomMap) {
        this.roomMap = roomMap;
    }

    @Override
    public int getItemViewType(int position) {
        SmartHomeApiClient.Device device = deviceList.get(position);
        if(device instanceof SmartHomeApiClient.Lamp)
            return 0;

        if(device instanceof SmartHomeApiClient.RTV)
            return 1;

        if(device instanceof SmartHomeApiClient.Door)
            return 2;

        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        CardView v;
        switch(type) {
            case 0:
            default:
                v = (CardView)LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_lamp,
                        viewGroup, false);
                return new LampHolder(v);
            case 1:
                v = (CardView)LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_rtv,
                        viewGroup, false);
                return new RTVHolder(v);
            case 2:
                v = (CardView)LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_door,
                        viewGroup, false);
                return new DoorHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        SmartHomeApiClient.Device device = deviceList.get(i);
        switch (viewHolder.getItemViewType()) {
            case 0:
                ((LampHolder)viewHolder).setData((SmartHomeApiClient.Lamp)device);
                break;
            case 1:
                ((RTVHolder)viewHolder).setData((SmartHomeApiClient.RTV)device);
                break;
            case 2:
                ((DoorHolder)viewHolder).setData((SmartHomeApiClient.Door)device);

        }
    }

    @Override
    public int getItemCount() {
        return deviceList == null ? 0 : deviceList.size();
    }

    public void updateDevices(List<SmartHomeApiClient.Device> list) {
        deviceList = list;
        notifyDataSetChanged();
    }
    public void updateDevice(SmartHomeApiClient.Device device, int position) {
        deviceList.set(position, device);
        notifyItemChanged(position);
    }

    public SmartHomeApiClient.Device getDevice(int position) {
        return deviceList.get(position);
    }
}
