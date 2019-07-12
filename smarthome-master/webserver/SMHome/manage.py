#!/usr/bin/env python
import os
import sys
from Database.mqtt import *

if __name__ == '__main__':
    os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'SMHome.settings')

    # from Database.models import *
    #
    # connect2(Room,Lamp,RTV)
    # print(Room.objects.all())

    try:
        from django.core.management import execute_from_command_line
    except ImportError as exc:
        raise ImportError(
            "Couldn't import Django. Are you sure it's installed and "
            "available on your PYTHONPATH environment variable? Did you "
            "forget to activate a virtual environment?"
        ) from exc
    execute_from_command_line(sys.argv)

