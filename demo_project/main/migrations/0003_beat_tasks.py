# Generated by Django 3.1.6 on 2021-03-08 11:40

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('main', '0002_forest_employee_empid'),
    ]

    operations = [
        migrations.CreateModel(
            name='Beat_tasks',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('task_id', models.CharField(max_length=200)),
                ('task_info', models.CharField(max_length=200)),
                ('deadline', models.DateField()),
                ('task_from', models.CharField(max_length=200)),
                ('task_to', models.CharField(max_length=200)),
                ('status', models.CharField(max_length=200)),
            ],
        ),
    ]