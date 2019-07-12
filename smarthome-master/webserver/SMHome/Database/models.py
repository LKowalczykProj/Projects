from django.db import models
from django.contrib.auth.models import User
from django.db.models.signals import post_init,post_save
from django.core.validators import MaxValueValidator,MinValueValidator
from .mqtt import load_devices,connect2

# Create your models here.

class House(models.Model):
    owner = models.ForeignKey(User,on_delete=models.CASCADE)
    auto = models.BooleanField(default=False)

    def __str__(self):
        return "SmartHome "+str(self.pk)

class Device(models.Model):
    name=models.CharField(max_length=50)

    def __str__(self):
        return self.name

class Room(models.Model):
    name = models.CharField(max_length=50)
    temperature = models.DecimalField(decimal_places=1,max_digits=3,null=True)
    humidity = models.DecimalField(decimal_places=1,max_digits=3,null=True)
    people = models.BooleanField(default=False)
    house = models.ForeignKey(House, on_delete=models.CASCADE,related_name='rooms')
    device = models.CharField(max_length=50, null=True)
    favourite = models.BooleanField(default=False)

    def __str__(self):
        return str(self.name)+" "+str(self.pk)

class Lamp(models.Model):
    name = models.CharField(max_length=50)
    state = models.BooleanField(default=False)
    intensity = models.IntegerField(validators=[MinValueValidator(0),MaxValueValidator(100)])
    room = models.ForeignKey(Room, on_delete=models.CASCADE,related_name='lamps')
    device = models.CharField(max_length=50, null=True)
    dimmable = models.BooleanField(default=True)
    favourite = models.BooleanField(default=False)

    def __str__(self):
        return str(self.name)+" "+str(self.pk)


class RTV(models.Model):
    name = models.CharField(max_length=50)
    state = models.BooleanField()
    volume = models.IntegerField(validators=[MinValueValidator(0),MaxValueValidator(100)])
    room = models.ForeignKey(Room, on_delete=models.CASCADE,related_name='RTVs')
    device = models.CharField(max_length=50, null=True)
    favourite = models.BooleanField(default=False)

    def __str__(self):
        return str(self.name)+" "+str(self.pk)


class Door(models.Model):
    state = models.BooleanField()
    room1 = models.ForeignKey(Room, null=True, related_name='doors1', on_delete=models.CASCADE)
    room2 = models.ForeignKey(Room, null=True, related_name='doors2', on_delete=models.CASCADE)
    device = models.CharField(max_length=50, null=True)
    favourite = models.BooleanField(default=False)

    def __str__(self):
        return "Drzwi"+str(self.pk)

class Map(models.Model):
    name = models.CharField(max_length=50)
    type = models.CharField(max_length=1)
    posX = models.IntegerField()
    posY = models.IntegerField()
    house = models.ForeignKey(House,null=False,related_name='Map',on_delete=models.CASCADE)

connect2(Room,Lamp,RTV,Door,Device)

load_devices()

from .signals import *

post_init.connect(init_house,sender=House)
post_save.connect(house_save,sender=House)

post_init.connect(init_room,sender=Room)
post_save.connect(room_save,sender=Room)

post_init.connect(init_lamp,sender=Lamp)
post_save.connect(lamp_save,sender=Lamp)

post_init.connect(init_rtv,sender=RTV)
post_save.connect(rtv_save,sender=RTV)

post_init.connect(init_door,sender=Door)
post_save.connect(door_save,sender=Door)

#Pamietaj o zmianie autoryzacji!!!