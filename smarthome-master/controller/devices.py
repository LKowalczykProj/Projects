import orangepi_hw
from pyA20.gpio import gpio

gpio.init()

def value_err_msg(parameter_name):
    print(f'Wrong value for parameter {parameter_name}.')


class Lamp:
    type = 'lamp'
    read_parameters = ('type', 'power', 'brightness')
    write_parameters = ('power', 'brightness')

    def __init__(self, out_gpio=None, invert=False):
        self.out_gpio = out_gpio
        self.invert = invert
        self.__power = 0
        self.__brightness = 100
        if self.out_gpio:
            gpio.setcfg(self.out_gpio, gpio.OUTPUT)
            gpio.output(self.out_gpio, invert)

    @property
    def power(self):
        return self.__power

    @power.setter
    def power(self, power: int):
        if isinstance(power, int) and power in (0, 1):
            self.__power = power
            gpio.output(self.out_gpio, not power if self.invert else power)
        else:
            value_err_msg('power')

    @property
    def brightness(self):
        return self.__brightness

    @brightness.setter
    def brightness(self, brightness: int):
        if isinstance(brightness, int) and brightness in range(0, 100+1):
            self.__brightness = brightness
        else:
            value_err_msg('brightness')

    def __str__(self):
        return f'Lamp; power: {self.power}, brightness: {self.brightness}'


class Lock:
    type = 'lock'
    read_parameters = ('type', 'locked')
    write_parameters = ('locked',)

    def __init__(self, out_gpio=None):
        self.out_gpio = out_gpio
        self.__locked = 1

    @property
    def locked(self):
        return self.__locked

    @locked.setter
    def locked(self, locked: int):
        if isinstance(locked, int) and locked in (0, 1):
            self.__locked = locked
        else:
            value_err_msg('locked')

    def __str__(self):
        return f'Lock; locked: {self.locked}'


class TemperatureSensor:
    type = 'temperature_sensor'
    read_parameters = ('type', 'temperature')
    write_parameters = ()

    def __init__(self, onewire_serial=None):
        self.onewire_serial = onewire_serial
        self.__temperature = 0.0

    @property
    def temperature(self):
        return self.__temperature

    @temperature.setter
    def temperature(self, temperature: float):
        if isinstance(temperature, float) and -100.0 < temperature < 100.0:
            self.__temperature = temperature
        else:
            value_err_msg('temperature')

    def read_sensor(self):
        if self.onewire_serial is None:
            return None
        self.temperature = orangepi_hw.temp_sensor_read(self.onewire_serial)
        return {'temperature': self.temperature}

    def __str__(self):
        return f'TemperatureSensor; temperature: {self.temperature}'


class HumiditySensor:
    type = 'humidity_sensor'
    read_parameters = ('type', 'humidity')
    write_parameters = ()

    def __init__(self):
        self.__humidity = 0

    @property
    def humidity(self):
        return self.__humidity

    @humidity.setter
    def humidity(self, humidity: int):
        if isinstance(humidity, int) and humidity in range(0, 100+1):
            self.__humidity = humidity
        else:
            value_err_msg('humidity')

    def __str__(self):
        return f'HumiditySensor; humidity: {self.humidity}'


class MotionSensor:
    type = 'motion_sensor'
    read_parameters = ('type', 'detected')
    write_parameters = ()

    def __init__(self, in_gpio=None):
        self.__detected = 0
        self.in_gpio = in_gpio
        if self.in_gpio:
            gpio.setcfg(self.in_gpio, gpio.INPUT)
            gpio.pullup(self.in_gpio, gpio.PULLUP)

    @property
    def detected(self):
        return self.__detected

    @detected.setter
    def detected(self, detected: int):
        if isinstance(detected, int) and detected in (0, 1):
            self.__detected = detected
        else:
            value_err_msg('detected')

    def read_sensor(self):
        if self.in_gpio is None:
            return None
        result = gpio.input(self.in_gpio)
        self.detected = int(not result)
        return {'detected': int(not result)}
            
    def __str__(self):
        return f'MotionSensor; detected: {self.detected}'


class AudioSystem:
    type = 'audio_system'
    read_parameters = ('type', 'power', 'volume', 'mute')
    write_parameters = ('power', 'volume', 'mute')

    def __init__(self):
        self.__power = 0
        self.__volume = 0
        self.__mute = 0

    @property
    def power(self):
        return self.__power

    @power.setter
    def power(self, power: int):
        if isinstance(power, int) and power in (0, 1):
            self.__power = power
        else:
            pass

    @property
    def volume(self):
        return self.__volume

    @volume.setter
    def volume(self, volume: int):
        if isinstance(volume, int) and volume in range(0, 100+1):
            self.__volume = volume
        else:
            value_err_msg('volume')

    @property
    def mute(self):
        return self.__mute

    @mute.setter
    def mute(self, mute: int):
        if isinstance(mute, int) and mute in (0, 1):
            self.__mute = mute
        else:
            value_err_msg('mute')

    def __str__(self):
        return f'AudioSystem; power: {self.power}, volume: {self.volume}, mute: {self.mute}'
