from .models import *
from .serializers import *
from rest_framework import viewsets
from rest_framework.authentication import TokenAuthentication, BasicAuthentication
from rest_framework.permissions import IsAdminUser

class HouseViewSet(viewsets.ModelViewSet):

    serializer_class = HouseSerializer

    def get_queryset(self):
        user = self.request.user
        if(user.is_authenticated):
            if(user.is_superuser):
                query=House.objects.all()
            else:
                query=House.objects.filter(owner=user)
            print(query)
            return query
        return None


class RoomViewSet(viewsets.ModelViewSet):
    serializer_class = RoomSerializer

    def get_queryset(self):
        user = self.request.user
        if(user.is_authenticated):
            if(user.is_superuser):
                query=Room.objects.all()
            else:
                query=Room.objects.filter(house__owner=user)
            return query
        return None


class LampViewSet(viewsets.ModelViewSet):
    serializer_class = LampSerializer
    def get_queryset(self):
        user = self.request.user
        if(user.is_authenticated):
            if(user.is_superuser):
                query=Lamp.objects.all()
            else:
                query=Lamp.objects.filter(room__house__owner=user)
            return query
        return None


class RTVViewSet(viewsets.ModelViewSet):
    serializer_class = RTVSerializer
    def get_queryset(self):
        user = self.request.user
        if(user.is_authenticated):
            if(user.is_superuser):
                query=RTV.objects.all()
            else:
                query=RTV.objects.filter(room__house__owner=user)
            return query
        return None


class DoorViewSet(viewsets.ModelViewSet):
    serializer_class = DoorSerializer

    def get_queryset(self):
        user = self.request.user
        if (user.is_authenticated):
            if (user.is_superuser):
                query = Door.objects.all()
            else:
                query = Door.objects.filter(room1__house__owner=user)
            return query
        return None


class FavouriteRoomViewSet(viewsets.ModelViewSet):

    serializer_class = RoomSerializer

    def get_queryset(self):
        user=self.request.user
        if(user.is_authenticated):
            query = Room.objects.filter(house__owner=user)
            query = query.filter(favourite=True)
            return query
        return None


class FavouriteRTVViewSet(viewsets.ModelViewSet):

    serializer_class = RTVSerializer

    def get_queryset(self):
        user = self.request.user
        if(user.is_authenticated):
            query=RTV.objects.filter(room__house__owner=user)
            query=query.filter(favourite=True)
            return query
        return None

class FavouriteLampViewSet(viewsets.ModelViewSet):
    serializer_class = LampSerializer
    def get_queryset(self):
        user = self.request.user
        if(user.is_authenticated):
            query=Lamp.objects.filter(room__house__owner=user)
            query=query.filter(favourite=True)
            return query
        return None

class FavouriteDoorViewSet(viewsets.ModelViewSet):
    serializer_class = DoorSerializer
    def get_queryset(self):
        user = self.request.user
        if(user.is_authenticated):
            query=Door.objects.filter(room1__house__owner=user)
            query=query.filter(favourite=True)
            return query
        return None

class MapViewSet(viewsets.ModelViewSet):
    serializer_class = MapSerializer

    def get_queryset(self):
        user=self.request.user
        if(user.is_authenticated):
            query = Map.objects.filter(house__owner=user)
            return query
        return None


class UserViewSet(viewsets.ModelViewSet):
    serializer_class = UserSerializer


    def get_queryset(self):
        user = self.request.user
        query = User.objects.filter(id=user.id)
        return query

