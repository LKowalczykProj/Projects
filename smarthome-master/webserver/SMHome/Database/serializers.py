from .models import *
from rest_framework import serializers


class HouseSerializer(serializers.ModelSerializer):

    class Meta:
        model = House
        fields = ('__all__')


class RoomSerializer(serializers.ModelSerializer):

    class Meta:
        model = Room
        fields = ('__all__')


class LampSerializer(serializers.ModelSerializer):

    class Meta:
        model = Lamp
        fields = ('__all__')


class RTVSerializer(serializers.ModelSerializer):

    class Meta:
        model = RTV
        fields = ('__all__')


class DoorSerializer(serializers.ModelSerializer):

    class Meta:
        model = Door
        fields = ('__all__')

class MapSerializer(serializers.ModelSerializer):

    class Meta:
        model = Map
        fields = ('__all__')

class UserSerializer(serializers.ModelSerializer):

    class Meta:
        model = User
        fields = ('id', 'username', 'password', 'email', 'first_name', 'last_name')
        write_only_fields = ('password',)
        read_only_fields = ('id',)


    def create(self, validated_data):
        user = User.objects.create(
            username=validated_data['username'],
            email=validated_data['email'],
        )

        user.set_password(validated_data['password'])
        user.save()

        return user