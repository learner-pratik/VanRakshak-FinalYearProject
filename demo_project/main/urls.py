from django.urls import path
from main import views

urlpatterns = [
    ###############SERVER######################
    path('',views.login(),name='login')

    #############API###########################
    path('login/', views.Login_api.as_view(), name='login_api'),
    path('logout/', views.Logout_api.as_view(), name='logout_api'),
    path('register/', views.Register_api.as_view(), name='register_api'),
    path('check/', views.Check.as_view(), name='check_api'),
]