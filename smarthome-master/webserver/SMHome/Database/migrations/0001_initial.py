# Generated by Django 2.1.5 on 2019-03-05 20:32

from django.conf import settings
from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
    ]

    operations = [
        migrations.CreateModel(
            name='Device',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=50)),
            ],
        ),
        migrations.CreateModel(
            name='Door',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('state', models.BooleanField()),
                ('device', models.ForeignKey(on_delete=django.db.models.deletion.PROTECT, related_name='door_name', to='Database.Device')),
            ],
        ),
        migrations.CreateModel(
            name='House',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('owner', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to=settings.AUTH_USER_MODEL)),
            ],
        ),
        migrations.CreateModel(
            name='Lamp',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=50)),
                ('state', models.BooleanField()),
                ('intensity', models.DecimalField(decimal_places=1, max_digits=3)),
                ('device', models.ForeignKey(on_delete=django.db.models.deletion.PROTECT, related_name='lamp_name', to='Database.Device')),
            ],
        ),
        migrations.CreateModel(
            name='Room',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=50)),
                ('temperature', models.DecimalField(decimal_places=1, max_digits=3)),
                ('humidity', models.DecimalField(decimal_places=1, max_digits=3)),
                ('people', models.IntegerField()),
                ('device', models.ForeignKey(on_delete=django.db.models.deletion.PROTECT, related_name='sensors', to='Database.Device')),
                ('house', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='rooms', to='Database.House')),
            ],
        ),
        migrations.CreateModel(
            name='RTV',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=50)),
                ('state', models.BooleanField()),
                ('volume', models.IntegerField()),
                ('device', models.ForeignKey(on_delete=django.db.models.deletion.PROTECT, related_name='rtv_name', to='Database.Device')),
                ('room', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='RTVs', to='Database.Room')),
            ],
        ),
        migrations.AddField(
            model_name='lamp',
            name='room',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='lamps', to='Database.Room'),
        ),
        migrations.AddField(
            model_name='door',
            name='room1',
            field=models.ForeignKey(null=True, on_delete=django.db.models.deletion.CASCADE, related_name='doors1', to='Database.Room'),
        ),
        migrations.AddField(
            model_name='door',
            name='room2',
            field=models.ForeignKey(null=True, on_delete=django.db.models.deletion.CASCADE, related_name='doors2', to='Database.Room'),
        ),
    ]
