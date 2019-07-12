from devices import *

mqtt_config = {
    "host": "51.38.131.73",
    "port": 1883,
    "keepalive": 30,
    "username": "main_controller",
    "password": "Cae9wei7mo",
    "use_tls": False
}

general_config = {
    "interval": 5
}

device_list = {
    "sw_salon": Lamp(8, True),
    "sw_pokoj1": Lamp(20),
    "sw_kuchnia": Lamp(9),
    "temp_salon": MotionSensor(68),
    "temp_pokoj_lidera": TemperatureSensor("28-00000a0a06e2"),
    "temp_zewn": TemperatureSensor("28-0114643826ff")
}
