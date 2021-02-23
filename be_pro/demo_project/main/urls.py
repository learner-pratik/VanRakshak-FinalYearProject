from django.urls import path
from main import views

urlpatterns = [
    path('login/', views.Login_api.as_view(), name='login'),
    path('logout/', views.Logout_api.as_view(), name='logout'),
    path('register/', views.Register_api.as_view(), name='register'),
    path('check/', views.Check.as_view(), name='check'),
]