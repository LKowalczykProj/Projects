# Generated by Django 2.1.5 on 2019-05-23 20:00

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('Database', '0006_door_favourite'),
    ]

    operations = [
        migrations.CreateModel(
            name='Map',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=50)),
                ('type', models.CharField(max_length=1)),
                ('posX', models.IntegerField()),
                ('posY', models.IntegerField()),
                ('house', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='Map', to='Database.House')),
            ],
        ),
    ]
