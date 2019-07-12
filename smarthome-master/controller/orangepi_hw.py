import os
import sys

onewire_dir = '/sys/bus/w1/devices/'
onewire_family = '28'

def temp_sensor_list():
    entries = os.scandir(onewire_dir)
    return [e.name for e in entries if e.name[:2] == onewire_family]


def temp_sensor_read(sensor):
    path = os.path.join(onewire_dir, sensor, 'w1_slave')
    try:
        with open(path) as file:
            sensor_data = file.read()
    except OSError:
        print('Cannot read sensor file', file=sys.stderr)
        return None

    temp_pos = sensor_data.find('t=') + 2
    try:
        temp_raw = int(sensor_data[temp_pos:])
    except ValueError:
        print('Sensor data is invalid', file=stderr)
        return None

    return temp_raw / 1000


if __name__ == '__main__':
    for sensor in temp_sensor_list():
        print(f'{sensor}: {temp_sensor_read(sensor)}')


    
