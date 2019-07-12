from .mqtt import *
from .views import getAuto
house_args=[]
room_args=[]
rtv_args=[]
lamp_args=[]
door_args=[]



def init_house(sender,**kwargs):
    global house_args
    house_args.clear()
    #print("Initialize house "+str(kwargs.get('instance').pk))
    house_args.append(kwargs.get('instance').owner)


def init_room(sender, **kwargs):
    global room_args
    room_args.clear()
    #print("Room Initialized "+str(kwargs.get('instance').pk))
    room_args.append(kwargs.get('instance').name)
    room_args.append(kwargs.get('instance').temperature)
    room_args.append(kwargs.get('instance').humidity)
    room_args.append(kwargs.get('instance').people)
    room_args.append(kwargs.get('instance').house)
    room_args.append(kwargs.get('instance').favourite)
    room_args.append(kwargs.get('instance').device)


def init_rtv(sender, **kwargs):
    global rtv_args
    #print("RTV Initialized "+str(kwargs.get('instance').pk))
    rtv_args.clear()
    rtv_args.append(kwargs.get('instance').name)
    rtv_args.append(kwargs.get('instance').state)
    rtv_args.append(kwargs.get('instance').volume)
    rtv_args.append(kwargs.get('instance').room)
    rtv_args.append(kwargs.get('instance').device)
    rtv_args.append(kwargs.get('instance').favourite)


def init_lamp(sender, **kwargs):
    global lamp_args
    #print("Lamp Initialized "+str(kwargs.get('instance').pk))
    lamp_args.clear()
    lamp_args.append(kwargs.get('instance').name)
    lamp_args.append(kwargs.get('instance').state)
    lamp_args.append(kwargs.get('instance').intensity)
    lamp_args.append(kwargs.get('instance').room)
    lamp_args.append(kwargs.get('instance').device)
    lamp_args.append(kwargs.get('instance').favourite)
    lamp_args.append(kwargs.get('instance').dimmable)

def init_door(sender, **kwargs):
    global door_args
 #   print("Door Initialized "+str(kwargs.get('instance').pk))
    door_args.clear()
    door_args.append(kwargs.get('instance').state)
    door_args.append(kwargs.get('instance').room1)
    door_args.append(kwargs.get('instance').room2)
    door_args.append(kwargs.get('instance').device)
    door_args.append(kwargs.get('instance').favourite)

def house_save(sender,**kwargs):
    global house_args
    house_args2=[]
    print("Save house "+str(kwargs.get('instance').pk))
    house_args2.append(kwargs.get('instance').owner)
    if(house_args==house_args2):
        print("No change")
    else:
        print(str(house_args[0])+" -> "+str(house_args2[0]))

def room_save(sender,**kwargs):
    global room_args
    room_args2=[]
    print("Save room "+str(kwargs.get('instance').pk))
    room_args2.append(kwargs.get('instance').name)
    room_args2.append(kwargs.get('instance').temperature)
    room_args2.append(kwargs.get('instance').humidity)
    room_args2.append(kwargs.get('instance').people)
    room_args2.append(kwargs.get('instance').house)
    room_args2.append(kwargs.get('instance').favourite)
    room_args2.append(kwargs.get('instance').device)
    if (room_args[0] != room_args2[0]):
        pass
    if (room_args[1] != room_args2[1]):
        print(str(room_args[1]) + " -> " + str(room_args2[1]))
        use(float(room_args2[1]), "temperature", room_args2[6])
    if (room_args[2] != room_args2[2]):
        print(str(room_args[2]) + " -> " + str(room_args2[2]))
    if (room_args[3] != room_args2[3]):
        print(str(room_args[3]) + " -> " + str(room_args2[3]))
        auto = getAuto()
        try:
            a =auto.index(kwargs.get('instance').pk)
            q=Lamp.objects.filter(room__pk=kwargs.get('instance').pk)
            for i in q:
                i.state=room_args2[3]
                i.save()
        except ValueError as e:
            print("Error:", e)

    if (room_args[4] != room_args2[4]):
        print(str(room_args[4]) + " -> " + str(room_args2[4]))
    if (room_args[5] != room_args2[5]):
        print(str(room_args[5]) + " -> " + str(room_args2[5]))

def lamp_save(sender,**kwargs):
    global lamp_args
    lamp_args2=[]
    print("Save Lamp "+str(kwargs.get('instance').pk))
    lamp_args2.append(kwargs.get('instance').name)
    lamp_args2.append(kwargs.get('instance').state)
    lamp_args2.append(kwargs.get('instance').intensity)
    lamp_args2.append(kwargs.get('instance').room)
    lamp_args2.append(kwargs.get('instance').device)
    lamp_args2.append(kwargs.get('instance').favourite)
    lamp_args2.append(kwargs.get('instance').dimmable)

    if (kwargs.get('created')):
        use(int(lamp_args2[1]), "power", lamp_args2[4])
        use(int(lamp_args2[2]), "brightness",lamp_args2[4])
    else:
        if (lamp_args[0] != lamp_args2[0]):
            print(str(lamp_args[0]) + " -> " + str(lamp_args2[0]))
        #if (lamp_args[1] != lamp_args2[1]):
        use(int(lamp_args2[1]),"power",lamp_args2[4])
        #print(f"{lamp_args2[0]} state changed")
        if (lamp_args[2] != lamp_args2[2]):
            use(int(lamp_args2[2]), "brightness", lamp_args2[4])
            print("intensity changed")
        if (lamp_args[3] != lamp_args2[3]):
            print(str(lamp_args[3]) + " -> " + str(lamp_args2[3]))
        if (lamp_args[4] != lamp_args2[4]):
            print(str(lamp_args[4]) + "->" + str(lamp_args2[4]))
        if (lamp_args[5] != lamp_args2[5]):
            print(str(lamp_args[5]) + "->" + str(lamp_args2[5]))
        if (lamp_args[6] != lamp_args2[6]):
            print(str(lamp_args[6]) + "->" + str(lamp_args2[6]))


def door_save(sender,**kwargs):
    global door_args
    door_args2=[]
    print("Save DOOR "+str(kwargs.get('instance').pk))
    door_args2.append(kwargs.get('instance').state)
    door_args2.append(kwargs.get('instance').room1)
    door_args2.append(kwargs.get('instance').room2)
    door_args2.append(kwargs.get('instance').device)
    door_args2.append(kwargs.get('instance').favourite)
    if (kwargs.get('created')):
        use(int(door_args2[0]), "locked", door_args2[3])
    else:
        #if (door_args[0] != door_args2[0]):
        use(int(door_args2[0]), "locked", door_args2[3])
        #print("state changed")
        if (door_args[1] != door_args2[1]):
            print(str(door_args[1]) + " -> " + str(door_args2[1]))
        if (door_args[2] != door_args2[2]):
            print(str(door_args[2]) + " -> " + str(door_args2[2]))
        if (door_args[3] != door_args2[3]):
            print(str(door_args[3]) + " -> " + str(door_args2[3]))
        if (door_args[4] != door_args2[4]):
            print(str(door_args[4]) + " -> " + str(door_args2[4]))

def rtv_save(sender,**kwargs):
    global rtv_args
    rtv_args2=[]
    print("Save RTV "+str(kwargs.get('instance').pk))
    rtv_args2.append(kwargs.get('instance').name)
    rtv_args2.append(kwargs.get('instance').state)
    rtv_args2.append(kwargs.get('instance').volume)
    rtv_args2.append(kwargs.get('instance').room)
    rtv_args2.append(kwargs.get('instance').device)
    rtv_args2.append(kwargs.get('instance').device)

    if (kwargs.get('created')):
        use(int(rtv_args2[1]), "power", rtv_args2[4])
        use(int(rtv_args2[2]), "volume", rtv_args2[4])
    else:
        if (rtv_args[0] != rtv_args2[0]):
            print(str(rtv_args[0]) + " -> " + str(rtv_args2[0]))
        #if (rtv_args[1] != rtv_args2[1]):
        use(int(rtv_args2[1]), "power", rtv_args2[4])
        #print("state changed")
        if (rtv_args[2] != rtv_args2[2]):
            use(int(rtv_args2[2]), "volume", rtv_args2[4])
            print("state changed")
        if (rtv_args[3] != rtv_args2[3]):
            print(str(rtv_args[3]) + " -> " + str(rtv_args2[3]))
        if (rtv_args[4] != rtv_args2[4]):
           print(str(rtv_args[4]) + "->" + str(rtv_args2[4]))

from .models import Lamp