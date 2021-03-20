from django.db import models
from django.contrib.auth.models import User
from django.db.models.signals import post_save
from django.dispatch import receiver
from django.contrib.postgres.fields import ArrayField

class Email(models.Model):
    email= models.CharField(max_length=200)
    def __str__(self):
        return self.email

class Forest_employee(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE)
    empid = models.CharField(max_length=200)
    name= models.CharField(max_length=200, blank=True)
    area= models.CharField(max_length=200, blank=True)
    role= models.CharField(max_length=200, blank=True)
    latitude = ArrayField(models.FloatField(),default=list)
    longitude = ArrayField(models.FloatField(),default=list)
    def __str__(self):
        return self.user.username

#######COPY########
class Report(models.Model):
    rid = models.CharField(max_length=200)
    empid = models.CharField(max_length=200)
    description = models.CharField(max_length=1000)
    image = models.BinaryField()
    latitude = models.FloatField()
    longitude = models.FloatField()
    timestamp = models.CharField(max_length=200)
    def __str__(self):
        return self.empid

class Task_Description(models.Model):
    task_id = models.CharField(max_length=200)
    description = models.CharField(max_length=1000)
    image = models.BinaryField()
    def __str__(self):
        return self.task_id

class Division_range(models.Model):
    division_id = models.CharField(max_length=200)
    range_id = models.CharField(max_length=200)
    def __str__(self):
        return self.division_id

class Range_beat(models.Model):
    range_id = models.CharField(max_length=200)
    beat_id = models.CharField(max_length=200)
    def __str__(self):
        return self.range_id

class Beat_employee(models.Model):
    beat_id = models.CharField(max_length=200)
    empid = models.CharField(max_length=200)
    def __str__(self):
        return self.beat_id

class Local_report(models.Model):
    lrid = models.CharField(max_length=200)
    description = models.CharField(max_length=1000)
    phone_no = models.CharField(max_length=10)
    image = models.BinaryField()
    latitude = models.FloatField()
    longitude = models.FloatField()
    timestamp = models.CharField(max_length=200)
    def __str__(self):
        return self.phone_no

class Animal(models.Model):
    animal_id= models.CharField(max_length=200)
    animal_name= models.CharField(max_length=200)
    animal_info= models.CharField(max_length=500)
    latitude = ArrayField(models.FloatField())
    longitude = ArrayField(models.FloatField())
    def __str__(self):
        return self.animal_name

class Camera(models.Model):
    camera_id = models.CharField(max_length=200)
    latitude = models.FloatField()
    longitude = models.FloatField()
    status = models.CharField(max_length=200)
    def __str__(self):
        return self.camera_id

class Tasks(models.Model):
	task_id = models.CharField(max_length=200)
	task_info = models.CharField(max_length=200)
	deadline = models.DateField()
	task_from = models.CharField(max_length=200)
	task_to = models.CharField(max_length=200)
	status = models.CharField(max_length=200)
	def __str__(self):
		return self.task_id

class Beat_tasks(models.Model):
	task_id = models.CharField(max_length=200)
	task_info = models.CharField(max_length=200)
	deadline = models.DateField()
	task_from = models.CharField(max_length=200)
	task_to = models.CharField(max_length=200)
	status = models.CharField(max_length=200)
	def __str__(self):
		return self.task_id

class Range_tasks(models.Model):
	task_id = models.CharField(max_length=200)
	task_info = models.CharField(max_length=200)
	deadline = models.DateField()
	task_from = models.CharField(max_length=200)
	task_to = models.CharField(max_length=200)
	status = models.CharField(max_length=200)
	def __str__(self):
		return self.task_id

class Division_tasks(models.Model):
	task_id = models.CharField(max_length=200)
	task_info = models.CharField(max_length=200)
	deadline = models.DateField()
	task_from = models.CharField(max_length=200)
	task_to = models.CharField(max_length=200)
	status = models.CharField(max_length=200)
	def __str__(self):
		return self.task_id

class Logs(models.Model):
    camera_id = models.CharField(max_length=200)
    time = models.CharField(max_length=200)
    action = models.CharField(max_length=200)
    latitude = models.FloatField()
    longitude = models.FloatField()
    def __str__(self):
        return self.camera_id

class Status(models.Model):
    camera_id = models.CharField(max_length=200)
    time = models.CharField(max_length=200)
    action = models.CharField(max_length=200)
    latitude = models.FloatField()
    longitude = models.FloatField()
    def __str__(self):
        return self.camera_id

# @receiver(post_save, sender=User)
# def create_user_profile(sender, instance, created, **kwargs):
#     if created:
#         Forest_employee.objects.create(user=instance)

# @receiver(post_save, sender=User)
# def save_user_profile(sender, instance, **kwargs):
#     instance.forest_employee.save()