import devices
import orangepi_hw
from config import device_list, mqtt_config, general_config
import paho.mqtt.client as mqtt
import threading
import time

def list_devices():
    for (i, o) in device_list.items():
        print(f'{i}: {o}')

def send_value(client, devname, parameter, value):
    client.publish(f'dev/{devname}/value/{parameter}', value)

def set_parameter(device, devname, parameter, value):
    if not hasattr(device, parameter):
        print(f'Object does not have "{parameter}" parameter.')
    else:
        t = getattr(device, parameter)
        if isinstance(t, int):
            value = int(value)
        elif isinstance(t, float):
            value = float(value)
        setattr(device, parameter, value)
        send_value(client, devname, parameter, getattr(device, parameter))


def parse_command(command):
    tokens = command.split()
    if not tokens:
        pass
    elif tokens[0] == 'list':
        list_devices()
    elif tokens[0] == 'exit':
        exit(0)
    elif tokens[0] == 'set':
        if len(tokens) != 4:
            print("Wrong number of arguments.")
        else:
            set_parameter(device_list[tokens[1]], tokens[1], tokens[2], tokens[3])


qos = 0
topic_list = [
    ('command', qos),
    ('dev/+/write/+', qos),
    ('dev/+/command', qos)
]


def on_connect(client, userdata, flags, rc):
    print(f"Connected: {rc}.")
    client.subscribe(topic_list)


def on_message(client, userdata, message):
    topic = message.topic.split('/')
    if topic[0] == 'command':
        cmd = message.payload.decode().split()
        if cmd[0] == 'list':
            for (k, dev) in device_list.items():
                send_value(client, k, 'type', dev.type)

    elif topic[0] == 'dev':
        try:
            dev = device_list[topic[1]]
        except KeyError:
            print(f'Device {topic[1]} does not exist.')
            return

        if topic[2] == 'command':
            cmd = message.payload.decode().split()
            if cmd[0] == 'read':
                if len(cmd) == 2:
                    if cmd[1] in dev.read_parameters:
                        send_value(client, topic[1], cmd[1], getaddr(dev, cmd[1]))
                    else:
                        print(f'{topic[1]}: {cmd[1]} is not readable.')
                else:
                    # read all
                    for param in dev.read_parameters:
                        send_value(client, topic[1], param, getaddr(dev, param))

        elif topic[2] == 'write':
            if topic[3] in dev.write_parameters:
                new_value = message.payload.decode()
                set_parameter(dev, topic[1], topic[3], new_value)
                print(f'{topic[1]}: set {topic[3]} to {new_value}.')
            else:
                print(f'{topic[1]}: {topic[3]} is not writable.')


sensors = {k: dev for (k, dev) in device_list.items() if hasattr(dev, 'read_sensor')}
class SensorsThread(threading.Thread):
    def run(self):
        while(True):
            for name, dev in sensors.items():
                result = dev.read_sensor()
                if result is None:
                    continue
                for param, value in result.items():
                    send_value(client, name, param, value)
            time.sleep(general_config['interval'])

        


if __name__ == '__main__':
    client = mqtt.Client()
    client.on_connect = on_connect
    client.on_message = on_message
    print(f'Connecting to {mqtt_config["host"]}:{mqtt_config["port"]}...')
    if mqtt_config['username']:
        client.username_pw_set(mqtt_config['username'], mqtt_config['password'])
    client.connect(mqtt_config['host'], mqtt_config['port'], mqtt_config['keepalive'])

    client.loop_start()
    sensors_thread = SensorsThread()
    sensors_thread.start()


    while True:
        parse_command(input())
