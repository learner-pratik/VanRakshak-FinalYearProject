from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.authtoken.models import Token
from rest_framework.permissions import IsAuthenticated 

from django.shortcuts import render,redirect
from django.contrib.auth import authenticate, login,logout
from django.contrib.auth.models import User
from django.contrib.sessions.models import Session
from django.utils import timezone
from .models import *
from django.http import HttpResponse,JsonResponse
import json
import xlsxwriter

############################SERVER########################################
def logout_server(request):
    logout(request)
    return redirect('login_server')

def login_server(request):
    if request.user.is_authenticated:
        return redirect('info')
    return render(request, "login.html", {})

def info(request):
    if not request.user.is_authenticated:
        username=request.POST['username']
        password=request.POST['password']
        print(username,password)
        user = authenticate(request, username=username, password=password)
        if user is None:
            return redirect('login_server')
        login(request, user)
    print(request.user.username)
    f=Forest_employee.objects.get(user=request.user)
    request.session['data']={'area':f.area,'empid':f.empid}
    request.session['flag']=f.role
    print(f.role,"this is role")
    if f.role=="admin":
        return redirect('admin')
    else:
        return redirect('forest_employee')

def admin(request):
    animals=[]
    sdict={}
    slat={}
    slon={}
    x=Animal.objects.all()
    for y in x:
        animals.append(y.animal_info)
    animals=list(set(animals))
    for atype in animals:
        sdict[atype]=[]
        slat[atype]=[]
        slon[atype]=[]
        a=Animal.objects.filter(animal_info=atype)
        for i in a:
            sdict[atype].append(i.animal_id)
            slat[atype].append(i.latitude[-1])
            slon[atype].append(i.longitude[-1])
    print(sdict,slat,slon)
    return render(request,"admin.html",{"animals":animals,"sdict":sdict,"slat":slat,"slon":slon})
    # return render(request,"admin.html",{}) 

def forest_employee(request):
    data=request.session.get('data')
    print(data)
    animals=[]
    sdict={}
    slat={}
    slon={}
    x=Animal.objects.all()
    for y in x:
        animals.append(y.animal_info)
    animals=list(set(animals))
    for atype in animals:
        sdict[atype]=[]
        slat[atype]=[]
        slon[atype]=[]
        a=Animal.objects.filter(animal_info=atype)
        for i in a:
            sdict[atype].append(i.animal_id)
            slat[atype].append(i.latitude[-1])
            slon[atype].append(i.longitude[-1])
    print(sdict,slat,slon)
    return render(request,"forest_employee.html",{"animals":animals,"sdict":sdict,"slat":slat,"slon":slon})
    # return render(request,"forest_employee.html",{}) 

def addforest_employee(request):
    if request.method == 'POST':
        username=request.POST['username']
        password=request.POST['password']
        print(username,password)
        count=Forest_employee.objects.count()
        temp='id_'+(str(count+1))
        create = User.objects.create_user(username,username,password)
        user=Forest_employee.objects.create(user=create,empid=temp,area=request.POST['area'],name=request.POST['name'],role=request.POST['role'],latitude=[0.0],longitude=[0.0])
        return render(request,"done.html",{})  
    return render(request,"register.html",{})

def task(request):
    role=request.session.get('flag')
    info=request.session.get('data')
    area=info['area']
    if role == 'division_incharge':
        w=Division_range.objects.filter(division_id=area)
        data=Division_tasks.objects.filter(task_from=area)
    elif role == 'range_incharge':
        lb=Range_beat.objects.filter(range_id=area)
        w=[]
        d1=Range_tasks.objects.filter(task_to=area)## tasks from division to range and not assigned to beat
        d2=Range_tasks.objects.filter(task_from=area) ##tasks assigned to beat from range
        data=[]
        for i in d1:
            data.append(i)
        for i in d2:
            data.append(i)
        
        ##w=workers range officer will assign to this workers
        for i in lb:
            beat_id=i.beat_id
            w.append(Forest_employee.objects.get(area=beat_id))

    elif role == 'beat_incharge':
        # area=info['area']
        # data=Range_tasks.objects.filter(task_to=area)
        # w=[]
        lb=Beat_employee.objects.filter(beat_id=area)
        w=[]
        d1=Beat_tasks.objects.filter(task_to=area)
        d2=Beat_tasks.objects.filter(task_from=area)
        data=[]
        for i in d1:
            data.append(i)
        for i in d2:
            data.append(i)
        
        ##w=workers range officer will assign to this workers
        for i in lb:
            empid=i.empid
            w.append(Forest_employee.objects.get(empid=empid))

    flag=role
    print(data,flag,w)
    return render(request,"task.html",{'data':data,'flag':flag,"workers":w})

def addtask(request):
    for key, value in request.session.items():
        print('{} => {}'.format(key, value))
    if request.method == 'POST':
        print(request.POST)
        ## Creating new task
        t=Division_tasks()
        count=Division_tasks.objects.count()
        t.task_id='id_'+(str(count+1))
        t.task_from=request.session.get('data')['area']
        t.task_info=request.POST.get("task_info")
        t.task_to=request.POST.get("task_to")
        t.status='assigned'
        t.deadline=request.POST.get("deadline")
        print(t)
        t.save()

        ##Assigning task to range incharge
        t=Range_tasks()
        # count=Tasks.objects.count()
        t.task_id='id_'+(str(count+1))
        t.task_from=request.session.get('data')['area']
        t.task_info=request.POST.get("task_info")
        t.task_to=request.POST.get("task_to")
        t.status='incomplete'
        t.deadline=request.POST.get("deadline")
        print(t)
        t.save()
        # form = addtaskform(request.POST or None)
        # if form.is_valid():
        #     form.save()  
        return render(request,"done.html",{})
    
    data=Division_range.objects.filter(division_id=request.session.get('data')['area'])
    range=[]
    for i in data:
        range.append(i.range_id)
    return render(request,"addtask.html",{'range':range})

def assigntask(request):
    if request.method == 'POST':
        print(request.POST)
        x=request.POST['task_to'].split('_')
        ###RANGE OFFICER ASSIGNING TASK TO BEAT OFFICER
        # if x[0]!='r' and x[0]!='d':
        if x[0]=='b':
            print('RANGE OFFICER ASSIGNING TASK TO BEAT OFFICER')
            task=Range_tasks.objects.get(task_id=request.POST['task_id'])
            Range_tasks.objects.filter(task_id=request.POST["task_id"]).update(status="assigned")
            Range_tasks.objects.filter(task_id=request.POST["task_id"]).update(task_to=request.POST['task_to'])
            Range_tasks.objects.filter(task_id=request.POST["task_id"]).update(task_from=task.task_to)
            print(task)
            ta=Beat_tasks()
            ta.task_id=task.task_id
            ta.task_from=task.task_to
            ta.task_info=task.task_info
            ta.task_to=request.POST['task_to']
            ta.status='incomplete'
            ta.deadline=task.deadline.strftime('%Y-%m-%d')
            ta.save()
            print(ta)

            # t=Range_tasks()
            # t.task_id=task.task_id
            # t.task_from=task.task_to
            # t.task_info=task.task_info
            # t.task_to=request.POST['task_to']
            # t.status='assigned'
            # t.deadline=task.deadline.strftime('%Y-%m-%d')
            # t.save()
            # t.save()
            
        ##Beat incharge assign task to Forest Employee    
        else:
            print('Beat incharge assign task to Forest Employee')
            task=Beat_tasks.objects.get(task_id=request.POST['task_id'])
            Beat_tasks.objects.filter(task_id=request.POST["task_id"]).update(status="assigned")
            Beat_tasks.objects.filter(task_id=request.POST["task_id"]).update(task_to=request.POST['task_to'])
            Beat_tasks.objects.filter(task_id=request.POST["task_id"]).update(task_from=task.task_to)
            print(task)
            ta=Tasks()
            ta.task_id=task.task_id
            ta.task_from=task.task_to
            ta.task_info=task.task_info
            ta.task_to=request.POST['task_to']
            ta.status='incomplete'
            ta.deadline=task.deadline.strftime('%Y-%m-%d')
            ta.save()
            print(ta)

            # t=Range_tasks()
            # t.task_id=task.task_id
            # t.task_from=task.task_to
            # t.task_info=task.task_info
            # t.task_to=request.POST['task_to']
            # t.status='assigned'
            # t.deadline=task.deadline.strftime('%Y-%m-%d')
            # t.save()
        # elif x[0]=='r':
        #     task=Range_tasks.objects.get(task_id=request.POST['task_id'])
        #     Range_tasks.objects.filter(task_id=request.POST["task_id"]).update(status="assigned")
        #     t=Tasks()
        #     t.task_id=task.task_id
        #     t.task_from=task.task_to
        #     t.task_info=task.task_info
        #     t.task_to=request.POST['task_to']
        #     t.status='assigned'
        #     t.deadline=task.deadline.strftime('%Y-%m-%d')
        #     t.save()
    return HttpResponse(status=200)

def addanimal(request):
    if request.method == 'POST':
        # print((request.POST['animal_id']))
        a=Animal()
        count=Animal.objects.count()
        # print(count)
        a.animal_id='id_'+(str(count+1))
        a.animal_name=request.POST['animal_name']
        a.animal_info=request.POST['animal_info']
        a.latitude=[0]
        a.longitude=[0]
        a.save()
        return render(request,"done.html",{})  
    return render(request,"addanimal.html",{})

def addcamera(request):
    if request.method == 'POST': 
        c=Camera()
        count=Camera.objects.count()
        c.camera_id='id_'+(str(count+1))
        c.latitude=request.POST['latitude']
        c.longitude=request.POST['longitude']
        c.status=request.POST['status']
        c.save()
        return render(request,"done.html",{})
    return render(request,"addcamera.html",{})

def location(request):
    print(request.POST['animals'])
    animals=request.POST['animals'].split(",")
    sdict={}
    slat={}
    slon={}
    for atype in animals:
        sdict[atype]=[]
        slat[atype]=[]
        slon[atype]=[]
        a=Animal.objects.filter(animal_info=atype)
        for i in a:
            sdict[atype].append(i.animal_id)
            slat[atype].append(i.latitude[-1])
            slon[atype].append(i.longitude[-1])
    print(sdict,slat,slon)
    return JsonResponse({"sdict":sdict,"slat":slat,"slon":slon})

def task_description(request):
    tid=request.POST['task_id']
    print(tid)
    ts=Task_Description.objects.get(task_id=tid)
    data={ "task_id":ts.task_id,
            "image":"img",
            "description":ts.description}
    # print(bytes(ts.image))
    
    data['image']=bytes(ts.image).decode("utf-8") 
    
    return HttpResponse(status=200,content=str(json.dumps(data)))

def test(request):
    return render(request,"done.html",{})

def alertmap(request):
    return render(request,"alertmap.html",{})

def stats(request):
    return render(request,"dashboard.html",{})

def track(request):
    for key, value in request.session.items():
        print('{} => {}'.format(key, value))
    if request.session['flag']=='division_incharge':
        div=request.session['data']['area']
        ran=Division_range.objects.filter(division_id=div)
        beat=[]
        arr=[]
        data={}
        for i in ran:
            b=Range_beat.objects.filter(range_id=i.range_id)
            for j in b:
                beat.append(j.beat_id)
                arr.append(str(i.range_id)+"->"+str(j.beat_id))
                print(j.beat_id)
                f=Beat_employee.objects.get(beat_id=j.beat_id)
                print(f.empid)
                emp=Forest_employee.objects.get(empid=f.empid)
                print(emp.empid)
                if j.beat_id in data:
                    for k in range(len(emp.latitude)):
                        temp=[]
                        temp.append(emp.longitude[k])
                        temp.append(emp.latitude[k])
                        data[j.beat_id].append(temp)
                else:
                    data[j.beat_id]=[]
                    for k in range(len(emp.latitude)):
                        temp=[]
                        temp.append(emp.longitude[k])
                        temp.append(emp.latitude[k])
                        data[j.beat_id].append(temp)
                
    print(data)
    return render(request,"track.html",{'data':data,'arr':arr})

def allotrange(request):
    div_ran={}
    d=Division_range.objects.all()
    for i in d:
        div_ran[i.division_id]=i.range_id
    ran_beat={}
    r=Range_beat.objects.all()
    for i in r:
        ran_beat[i.range_id]=i.beat_id
    print(div_ran,ran_beat)
    return render(request,'allotrange.html',{'div_ran':div_ran,'ran_beat':ran_beat})

def allotbeat(request):
    ran_beat={}
    d=Range_beat.objects.all()
    for i in d:
        ran_beat[i.range_id]=i.range_id
    beat={}
    r=Range_beat.objects.all()
    for i in r:
        beat[i.range_id]=i.beat_id
    print(ran_beat,beat)
    return render(request,'allotbeat.html',{'ran_beat':ran_beat,'beat':beat})

def reportlist(request):
    main_dict={}
    # rep=Report.objects.all()
    data=request.session.get('data')
    print(data)
    work_id=data['area'].split("_")
    work_id=work_id[0]
    print(work_id)
    
    if work_id=='admin':
        div={}
        ldivision=Division_range.objects.all()
        #list of all divisions
        for x in ldivision:
            div[x.division_id]={}
            ran={}
            #code for particular division
            lrange=Division_range.objects.filter(division_id=x.division_id)
            #list of ranges in a particular division
            for i in lrange:
                range_id=i.range_id
                #code for particular range
                lbeat=Range_beat.objects.filter(range_id=i.range_id)
                #list of beats in a particular range
                ran[i.range_id]={}
                be={}
                for j in lbeat:
                    beat_id=j.beat_id
                    #code for particular beat
                    employee=Beat_employee.objects.filter(beat_id=beat_id)
                    #list of employees in a particular beat
                    be[beat_id]={}
                    e={}
                    for k in employee:
                        emp=Forest_employee.objects.get(empid=k.empid)
                        reports=Report.objects.filter(empid=(emp.empid+'-'+emp.name))
                        e[k.empid]=[]
                        for l in reports:
                            rep_dic={}
                            rep_dic['rid']=l.rid
                            rep_dic['timestamp']=l.timestamp
                            rep_dic['empid']=l.empid
                            rep_dic['description']=l.description
                            rep_dic['image']=bytes(l.image).decode("utf-8") 
                            rep_dic['latitude']=l.latitude
                            rep_dic['longitude']=l.longitude
                            e[k.empid].append(rep_dic)
                            # if k.empid in tem_dict[range_id]:
                            #     tem_dict[range_id][k.empid].append(rep_dic)
                            # else:
                            #     tem_dict[range_id][k.empid]=[]
                            #     tem_dict[range_id][k.empid].append(rep_dic)
                    be[j.beat_id]=e
                ran[i.range_id]=be
            div[x.division_id]=ran
        main_dict=div
        
    
    elif work_id=='d':
        ran={}
        #code for particular division
        lrange=Division_range.objects.filter(division_id=data['area'])
        #list of ranges in a particular division
        for i in lrange:
            range_id=i.range_id
            #code for particular range
            lbeat=Range_beat.objects.filter(range_id=i.range_id)
            #list of beats in a particular range
            ran[i.range_id]={}
            be={}
            for j in lbeat:
                beat_id=j.beat_id
                #code for particular beat
                employee=Beat_employee.objects.filter(beat_id=beat_id)
                #list of employees in a particular beat
                be[beat_id]={}
                e={}
                for k in employee:
                    emp=Forest_employee.objects.get(empid=k.empid)
                    reports=Report.objects.filter(empid=(emp.empid+'-'+emp.name))
                    e[k.empid]=[]
                    for k in reports:
                        rep_dic={}
                        rep_dic['rid']=l.rid
                        rep_dic['timestamp']=l.timestamp
                        rep_dic['empid']=k.empid
                        rep_dic['description']=k.description
                        rep_dic['image']=bytes(k.image).decode("utf-8") 
                        rep_dic['latitude']=k.latitude
                        rep_dic['longitude']=k.longitude
                        e[k.empid].append(rep_dic)
                        # if k.empid in tem_dict[range_id]:
                        #     tem_dict[range_id][k.empid].append(rep_dic)
                        # else:
                        #     tem_dict[range_id][k.empid]=[]
                        #     tem_dict[range_id][k.empid].append(rep_dic)
                be[j.beat_id]=e
            ran[i.range_id]=be
        main_dict=ran
    
    
    elif work_id=='r':
        range_id=data['area']
        lbeat=Range_beat.objects.filter(range_id=i.range_id)
        # list of beats in a particular range
        be={}
        for j in lbeat:
            beat_id=j.beat_id
            #code for particular beat
            employee=Beat_employee.objects.filter(beat_id=beat_id)
            #list of employees in a particular beat
            be[beat_id]={}
            e={}
            for k in employee:
                emp=Forest_employee.objects.get(empid=k.empid)
                reports=Report.objects.filter(empid=(emp.empid+'-'+emp.name))
                e[k.empid]=[]
                for k in reports:
                    rep_dic={}
                    rep_dic['rid']=l.rid
                    rep_dic['timestamp']=l.timestamp
                    rep_dic['empid']=k.empid
                    rep_dic['description']=k.description
                    rep_dic['image']=bytes(k.image).decode("utf-8") 
                    rep_dic['latitude']=k.latitude
                    rep_dic['longitude']=k.longitude
                    e[k.empid].append(rep_dic)
                    # if k.empid in tem_dict[range_id]:
                    #     tem_dict[range_id][k.empid].append(rep_dic)
                    # else:
                    #     tem_dict[range_id][k.empid]=[]
                    #     tem_dict[range_id][k.empid].append(rep_dic)
            be[j.beat_id]=e
        main_dict=be
    
    
    elif work_id=='b':
        beat_id=data['area']
        employee=Beat_employee.objects.filter(beat_id=beat_id)
        #list of employees in a particular beat
        e={}
        for k in employee:
            emp=Forest_employee.objects.get(empid=k.empid)
            reports=Report.objects.filter(empid=(emp.empid+'-'+emp.name))
            e[k.empid]=[]
            for k in reports:
                rep_dic={}
                rep_dic['rid']=l.rid
                rep_dic['timestamp']=l.timestamp
                rep_dic['empid']=k.empid
                rep_dic['description']=k.description
                rep_dic['image']=bytes(k.image).decode("utf-8") 
                rep_dic['latitude']=k.latitude
                rep_dic['longitude']=k.longitude
                e[k.empid].append(rep_dic)
        main_dict=e
    
            #sort the array
    print(main_dict,work_id)
    return render(request,"reportlist.html",{"main_dict":main_dict,"flag":work_id})

def localreportlist(request):
    r=Local_report.objects.all()
    main_dict=[]
    for lr in r:
        rep_dic={}
        rep_dic['phone']=lr.phone_no
        rep_dic['description']=lr.description
        rep_dic['image']=bytes(lr.image).decode("utf-8") 
        rep_dic['latitude']=lr.latitude
        rep_dic['longitude']=lr.longitude
        main_dict.append(rep_dic)
    return render(request,"localreportlist.html",{"main_dict":main_dict})

def getexcelanimal(request):
    workbook = xlsxwriter.Workbook('main/static/data.xlsx')
    worksheet = workbook.add_worksheet()  
    row = 0
    column = 0
    a=Animal.objects.all()
    aid=[]
    atype=[]
    lat=[]
    lon=[]
    for i in a:
        item=i.animal_id
        worksheet.write(row, column, item)
        column+=1
        item=i.animal_info
        worksheet.write(row, column, item)
        column+=1
        for j in range(len(i.latitude)):
            item=i.latitude[j]
            worksheet.write(row, column, item)
            column+=1
            item=i.longitude[j]
            worksheet.write(row, column, item)
            column-=1
            row+=1
        column-=2
    # content = ["ankit", "rahul", "priya", "harshita", 
    #                     "sumit", "neeraj", "shivam"] 

    # for item in content : 
    #     worksheet.write(row, column, item) 
    #     row += 1
        
    workbook.close() 
    
    path = 'main/static/data.xlsx' # this should live elsewhere, definitely
    with open(path, "rb") as excel:
        data = excel.read()

    response = HttpResponse(data,content_type='application/vnd.openxmlformats-officedocument.spreadsheetml.sheet')
    response['Content-Disposition'] = 'attachment; filename=data.xlsx'
    return response

#############################API##########################################

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

class Report_api(APIView):
    def post(self,request):
        r=Report()
        r.rid=request.data['rid']
        r.timestamp=request.data['timestamp']
        r.empid=request.data['empid']
        res = bytes(request.data['image'], 'utf-8')
        r.image=res
        r.description=request.data['description']
        r.latitude=request.data['latitude']
        r.longitude=request.data['longitude']
        r.save()
        return Response("Uploaded")

class Local_report_api(APIView):
    def post(self,request):
        r=Local_report()
        r.empid=request.data['empid']
        res = bytes(request.data['image'], 'utf-8')
        r.image=res
        r.description=request.data['description']
        r.latitude=request.data['latitude']
        r.longitude=request.data['longitude']
        r.save()

class Check(APIView):
    def post(self,request):
        print("checking email id",request.data['email'])
        email=Email.objects.filter(email=request.data['email'])
        if email:
            print("1")
            return Response("1")
        return Response("0")