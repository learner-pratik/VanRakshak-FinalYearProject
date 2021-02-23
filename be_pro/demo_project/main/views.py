from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.authtoken.models import Token
from rest_framework.permissions import IsAuthenticated 
from django.contrib.auth import authenticate, login,logout
from django.contrib.auth.models import User
from django.contrib.sessions.models import Session
from django.utils import timezone
from .models import *

class Login_api(APIView):
    # permission_classes = (IsAuthenticated,) No token required 

    def post(self, request):
        print(request.data)
        username = request.data['username']
        password = request.data['password']
        
        user = authenticate(request, username=username, password=password)
        token, created  = Token.objects.get_or_create(user=user)
        if user is not None:
            login(request, user)
            content = {'message': 'Logged in ','token':token.key}
        else:
            # Return an 'invalid login' error message.
            content = {'message': 'Hello, World!'}
        return Response(content)

class Logout_api(APIView):
    def post(self,request):
        
        content = {'message': 'Logged out'}
        
        active_sessions = Session.objects.filter(expire_date__gte=timezone.now())
        user_id_list = []
        for session in active_sessions:
            data = session.get_decoded()
            user_id_list.append(data.get('_auth_user_id', None))
        print(User.objects.filter(id__in=user_id_list))
        #

        # print(request.data)
        request.user.auth_token.delete()
        logout(request)
        active_sessions = Session.objects.filter(expire_date__gte=timezone.now())
        user_id_list = []
        for session in active_sessions:
            data = session.get_decoded()
            user_id_list.append(data.get('_auth_user_id', None))
        print(User.objects.filter(id__in=user_id_list))
        return Response(content)

class Register_api(APIView):
    def post(self,request):
        username = request.data['username']
        password = request.data['password']
        create = User.objects.create_user(username,username,password)
        
        user = authenticate(request, username=username, password=password)
        token, created  = Token.objects.get_or_create(user=user)
        if user is not None:
            login(request, user)
            content = {'message': 'Registered and Logged in ','token':token.key}
        return Response(content)

class Check(APIView):
    def post(self,request):
        print("checking email id",request.data['email'])
        email=Email.objects.filter(email=request.data['email'])
        if email:
            print("1")
            return Response("1")
        return Response("0")