from Database.viewsets import *
from rest_framework import routers
from rest_framework.authentication import BasicAuthentication
from rest_framework.permissions import IsAuthenticated


router = routers.DefaultRouter()
router.register('House',HouseViewSet, basename='House')
router.register('Room',RoomViewSet,basename='Room')
router.register('Lamp',LampViewSet,basename='Lamp')
router.register('RTV',RTVViewSet,basename='RTV')
router.register('Door',DoorViewSet,basename='Door')
router.register('Map',MapViewSet,basename='Map')
router.register('User',UserViewSet,basename='User')
router.register('FavRoom',FavouriteRoomViewSet,basename="FavRoom")
router.register('FavRTV',FavouriteRTVViewSet,basename="FavRTV")
router.register('FavLamp',FavouriteLampViewSet,basename="FavLamp")
router.register('FavDoor',FavouriteDoorViewSet,basename="FavDoor")

