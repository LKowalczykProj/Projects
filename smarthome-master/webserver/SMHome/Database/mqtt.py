import paho.mqtt.client as mqtt
import time

client=None
dev_list=[]
ROOM=None
RTV=None
LAMP=None
DOOR=None
DEVICE=None

def subscribe():
    global client
    print("Subscribed")
    li=[]
    li.append(("dev/+/value/+",0))
    li.append(("dev/+/value/type",0))
    client.subscribe(li)

def on_connect(client,userdata,flags,rc):
    print("Connected")
    subscribe()

def on_message(client,userdata,message):
    global LAMP
    global DOOR
    global ROOM
    global RTV
    global DEVICE
    global dev_list
    m=message.topic.split("/")
    dev=m[1]
    param=m[3]
    #print("message recieved "+str(dev) + " - "+str(param)+"!")
    if(param=="type" and dev!=""):
        dev_list.append(dev)
    else:
        if(param=="brightness"):
            q=LAMP.objects.filter(device=dev)
            for i in q:
                i.intensity=int(message.payload)
                i.save()
        if(param=="power"):
            q=LAMP.objects.filter(device=dev)
            for i in q:
                i.state=int(message.payload)
                i.save()
            q=RTV.objects.filter(device=dev)
            for i in q:
                i.state = int(message.payload)
                i.save()
        if(param=="locked"):
            q = DOOR.objects.filter(device=dev)
            for i in q:
                i.state = int(message.payload)
                i.save()
        if(param=="volume"):
            q = RTV.objects.filter(device=dev)
            for i in q:
                i.volume = int(message.payload)
                i.save()
        if(param=="temperature"):
            q = ROOM.objects.filter(device=dev)
            for i in q:
                i.temperature = float(message.payload)
                i.save()
        if(param=="humidity"):
            q = ROOM.objects.filter(device=dev)
            for i in q:
                i.humidity = float(message.payload)
                i.save()
        if(param=="detected"):
            q = ROOM.objects.filter(device=dev)
            for i in q:
                i.people = int(message.payload)
                i.save()




def connect2(room,lamp,rtv,door,device):
    global client
    global ROOM
    ROOM=room
    global LAMP
    LAMP=lamp
    global RTV
    RTV=rtv
    global DOOR
    DOOR=door
    global DEVICE
    DEVICE=device
    broker_adress="51.38.131.73"
    client=mqtt.Client()
    client.username_pw_set("django","vo1queLahc")
    client.on_connect=on_connect
    client.on_message=on_message
    client.connect(broker_adress)
    client.loop_start()

def use(argument,name,id):
    global client
    client.publish("dev/"+str(id)+"/write/"+name,argument)
    print("USE USED")

def load_devices():
    global client
    global dev_list
    client.publish("command","list")
    time.sleep(2)
    #print(dev_list)
    for i in dev_list:
        #print(i)
        q=DEVICE.objects.filter(name=i).count()
        if(q==0):
            d =DEVICE(name=i)
            d.save()

