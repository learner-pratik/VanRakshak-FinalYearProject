# Generated by Django 3.1.6 on 2021-03-19 11:13

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('main', '0003_beat_tasks'),
    ]

    operations = [
        migrations.AddField(
            model_name='local_report',
            name='lrid',
            field=models.CharField(default='hello', max_length=200),
            preserve_default=False,
        ),
        migrations.AddField(
            model_name='local_report',
            name='timestamp',
            field=models.CharField(default='0.0', max_length=200),
            preserve_default=False,
        ),
        migrations.AddField(
            model_name='report',
            name='rid',
            field=models.CharField(default='ss', max_length=200),
            preserve_default=False,
        ),
        migrations.AddField(
            model_name='report',
            name='timestamp',
            field=models.CharField(default='aas', max_length=200),
            preserve_default=False,
        ),
    ]