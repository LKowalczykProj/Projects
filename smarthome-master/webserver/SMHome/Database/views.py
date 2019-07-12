from rest_framework.authentication import SessionAuthentication, BasicAuthentication
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from django.http import HttpResponse
from rest_framework.views import APIView
from rest_framework import status
from django.shortcuts import render
from .models import *
from .serializers import UserSerializer
import time
from decimal import Decimal

auto_mode=[]

def getAuto():
    global auto_mode
    return auto_mode

# class ExampleView(APIView):
#     authentication_classes = (SessionAuthentication,BasicAuthentication)
#     permission_classes = (IsAuthenticated,)
#
#     def get(self,request, format=None):
#         content={
#             'user': unicode(request.user),
#             'auth': unicode(request.auth),
#         }
#         return Response(content)


class Registration(APIView):
    authentication_classes = []
    permission_classes = []

    def post(self, request, format='json'):
        serializer = UserSerializer(data=request.data)
        if serializer.is_valid():
            user = serializer.save()
            if user:
                House.objects.create(owner=user)
                return Response(serializer.data, status.HTTP_201_CREATED)
            else:
                return Response("Cannot create user", status.HTTP_400_BAD_REQUEST)
        else:
            return Response("Invalid data", status.HTTP_400_BAD_REQUEST)

def Token(request):
    print("Token")

def Lockdown(request,id):
    print(id)
    q=Door.objects.filter(room1__house__pk=id)
    #q=Door.objects.all()
    for i in q:
        print(i,i.state)
        i.state=False
        i.save()
    print("*")
    return HttpResponse("Worked fine")

def Shutdown(request,id):
    print(id)
    q=RTV.objects.filter(room__house__pk=id)
    #q=Door.objects.all()
    for i in q:
        print(i,i.state)
        i.state=False
        i.save()
    print("*")
    return HttpResponse("Worked fine")

def Lightout(request,id):
    print(id)
    q=Lamp.objects.filter(room__house__pk=id)
    #q=Door.objects.all()
    for i in q:
        print(i,i.state)
        i.state=False
        i.save()
    print("*")
    return HttpResponse("Worked fine")

def Auto_mode(request,id,option):
    global auto_mode
    que = House.objects.get(pk=id)
    au=que.auto
    if(option=="on" and not au):
        q=Room.objects.filter(house__pk=id)
        for i in q:
            auto_mode.append(i.pk)
            q2=Lamp.objects.filter(room__pk=i.pk)
            for j in q2:
                j.state=i.people
                print(j.name)
                j.save()

        q3=House.objects.get(pk=id)
        q3.auto=True
        q3.save()
        return HttpResponse("Auto-mode turned on")

    if(option=="off" and au):
        q = Room.objects.filter(house__pk=id)
        for i in q:
            try:
                j = auto_mode.index(i.pk)
                auto_mode.pop(j)
            except ValueError:
                pass
        q3 = House.objects.get(pk=id)
        q3.auto = False
        q3.save()
        return HttpResponse("Auto-mode turned off")

    return HttpResponse("Wrong option")

def TempControl(request,id,temp,room):

    temp = float(temp)
    if(room !=0 ):
        q = Room.objects.filter(pk=room)
    else:
        q = Room.objects.filter(house__pk=id)
    stabilized=False
    while not stabilized:
        stabilized=True
        for i in q:
            ac_temp=round(i.temperature,1)
            if (ac_temp < temp):
                ac_temp = round(ac_temp + Decimal(0.1), 1)
                stabilized=False
            if (ac_temp > temp):
                ac_temp = round(ac_temp - Decimal(0.1), 1)
                stabilized=False
            print(ac_temp)
            i.temperature=ac_temp
            i.save()
        time.sleep(0.1)

    return HttpResponse("Working fine")

