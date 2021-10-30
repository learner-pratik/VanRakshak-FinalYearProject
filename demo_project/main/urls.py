from django.urls import path
from main import views

urlpatterns = [
    ###############SERVER######################
    path('',views.login_server,name='login_server'),
    path('addforest_employee/',views.addforest_employee,name='addforest_employee'),
    path('logout_server',views.logout_server,name='logout_server'),

    ####COPY####
    # path('', views.login, name='login'),
    path('info', views.info, name='info'),
    path('admin', views.admin, name='admin'),
    # path('researcher', views.researcher, name='researcher'),
    path('forest_employee', views.forest_employee, name='forest_employee'),
    path('task', views.task, name='task'),
    path('addtask', views.addtask, name='addtask'),
    path('assigntask', views.assigntask, name='assigntask'),
    path('addanimal', views.addanimal, name='addanimal'),
    path('addcamera', views.addcamera, name='addcamera'),
    # path('addresearcher', views.addresearcher, name='addresearcher'),
    # path('addforest_employee', views.addforest_employee, name='addforest_employee'),
    # path('researcherlist', views.researcherlist, name='researcherlist'),
    path('location',views.location,name='location'),
    # path('geojson',views.geojson,name='geojson'),
    # path('editresearcher/<id>/',views.editresearcher,name='editresearcher'),
    # path('report',views.report,name='report'),
    path('stats',views.stats,name='stats'),
    path('reportlist',views.reportlist,name='reportlist'),
    path('localreportlist',views.localreportlist,name='localreportlist'),
    path('task_description',views.task_description,name='task_description'),
    path('alertmap',views.alertmap,name='alertmap'),
    path('getexcelanimal',views.getexcelanimal,name='getexcelanimal'),
    path('allotrange',views.allotrange,name='allotrange'),
    path('allotbeat',views.allotbeat,name='allotbeat'),
    path('track',views.track,name='track'),

    #############API###########################
    path('login/', views.Login_api.as_view(), name='login_api'),
    path('logout/', views.Logout_api.as_view(), name='logout_api'),
    path('register/', views.Register_api.as_view(), name='register_api'),
    path('report_api/', views.Report_api.as_view(), name='report_api'),
    path('localreport_api/', views.Local_report_api.as_view(), name='localreport_api'),
    path('register/', views.Register_api.as_view(), name='register_api'),
    path('check/', views.Check.as_view(), name='check_api'),
]