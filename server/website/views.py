from __future__ import unicode_literals
from django.shortcuts import render
from django.core import serializers
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
from . import models
from django.db.models import Q
import json
from functools import reduce

import sys
sys.path.insert(0, '../')
import sarkari
import freeJobAlert

@csrf_exempt
def login(request):

	if request.session.get('user') != None:
		user = models.User.objects.filter(email=request.session.get('user'))
		userJson = serializers.serialize('json', user)
		response = "{ status : True, data : " + userJson + "}"
		return HttpResponse(response, content_type="application/json")

	if request.method == "POST":
		if request.POST["type"] == "login":
			email = request.POST["email"]
			password = request.POST["password"]

			if models.Password.objects.filter(email = email, password = password).count() > 0:
				# Valid Login/Password
				user = models.User.objects.get(email=email)
				request.session['user'] = user.email
				userJson = serializers.serialize('json', [user])
				response = "{ 'status' : True, 'data' : " + userJson + ", 'message' : 'Login successful'}"
				return HttpResponse(response, content_type="application/json")

			else:
				response = "{ 'status' : False, 'data' : '', 'message' : 'Incorrect email or password'}"
				return HttpResponse(response, content_type="application/json")

		elif request.POST["type"] == "registration":
			email = request.POST['email']
			name = request.POST['name']
			password = request.POST['password']
			mobile = request.POST['mobile']
			qualification = request.POST['qualification']

			if models.User.objects.filter(email = email).count() == 0:
				models.Password.objects.create(email = email, password = password)
				user = models.User.objects.create(email = email, name = name, mobile = mobile, qualification = qualification)
				request.session['user'] = user.email
				userJson = serializers.serialize('json', [user])
				response = "{ 'status' : True, 'data' : " + userJson + ", 'message' : 'Registration successful'}"
				return HttpResponse(response, content_type="application/json")

			else:
				response = "{ 'status' : False, 'data' : '', 'message' : 'Email already taken'}"
				return HttpResponse(response, content_type="application/json")

	else:
		response = "{ 'status' : False, 'data' : '', 'message' : 'Unknown request'}"
		return HttpResponse(response, content_type="application/json")		

def add_jobs(request):
	return freeJobAlert.jobFinder()

def add_jobs_detail(request):
	return freeJobAlert.jobDetailAdder()

def get_jobs(request):
	try:
		page = int(request.GET.get('page'))
		limit = int(request.GET.get('limit'))
	except:
		page = 1
		limit = 10

	try:
		qual = request.GET.get('Qualification').split(",")
		vac = request.GET.get('Vacancy').split(",")
		sec = request.GET.get('Sector').split(",")
		location = request.GET.get('Location').split(",")
	except:
		qual = [""]
		vac = [""]
		sec = [""]
		location = [""]

	querySet = []
	for obj in models.JobDetail.objects.filter():
		if (not any(obj.qual.__contains__(q) for q in qual)):
			continue
		if (not any(obj.postNm.__contains__(q) for q in sec)):
			continue
		if (not any(obj.postNm.__contains__(q) for q in location)):
			continue

		try:
			if (len(vac) == 1 or obj.totVacc == ''):
				querySet.append(obj)
				continue
			elif (not ((int(obj.totVacc) <= max(map(int,vac))) and (int(obj.totVacc) >= min(map(int,vac))))):
				continue
		except:
			print("Invalid Total Vacancy entry")

		querySet.append(obj)

	next =  True if ((page)*limit < len(querySet)) else False
	page = querySet[(page-1)*limit:min((page)*limit,len(querySet))]

	serializedPage = serializers.serialize('json', page)
	jsonArray = json.loads(serializedPage)
	for entry in jsonArray:
		entry["fields"]["job"] = serializers.serialize('json', models.JobResult.objects.filter(pk=entry["fields"]["job"]))
		del entry["fields"]["brief"]
		del entry["fields"]["appFee"] 
		del entry["fields"]["impDt"]
		del entry["fields"]["ageLt"]
		del entry["fields"]["qual"]
		del entry["fields"]["vaccDet"]
		del entry["fields"]["impLinks"]
		del entry["fields"]["table"]


	serializedPage = str(jsonArray)
	serializedPage = "{'next' :" + str(next) + ", 'data' : " + serializedPage +"}"
	
	return HttpResponse(serializedPage.encode('ascii', 'ignore').decode("unicode_escape"),content_type="application/json")

def get_jobs_detail(request):
	try:
		pk = int(request.GET.get('pk'))
	except:
		pk = 1
	querySet = models.JobDetail.objects.filter(pk=pk)
	serializedPage = "[{\"fields\" : {\"brief\" : \"" + querySet[0].brief +"\", \"table\" : \"" + querySet[0].table.replace('"',"'")+"\"}}]"
		
	return HttpResponse(serializedPage.encode('ascii', 'ignore').decode("unicode_escape"),content_type="application/json")	

def get_quizzes(request):
	try:
		page = int(request.GET.get('page'))
		limit = int(request.GET.get('limit'))
	except:
		page = 1
		limit = 10

	querySet = models.QuizDetail.objects.filter()
	next =  True if ((page)*limit < len(querySet)) else False
	page = querySet[(page-1)*limit:min((page)*limit,len(querySet))]

	serializedPage = serializers.serialize('json', page)
	serializedPage = "{'next' :" + str(next) + ", 'data' : " + serializedPage +"}"
	
	return HttpResponse(serializedPage.encode('ascii', 'ignore').decode("unicode_escape"),content_type="application/json")

def get_quizzes_detail(request):
	try:
		pk = int(request.GET.get('pk'))
	except:
		pk = 1
	
	querySet = models.Question.objects.filter(quiz=pk)
	serializedPage = serializers.serialize('json', querySet)	
	return HttpResponse(serializedPage.encode('ascii', 'ignore').decode("unicode_escape"),content_type="application/json")	


def get_filters(request):
	querySet = models.FilterTitle.objects.filter()
	serializedPage = serializers.serialize('json', querySet)	
	return HttpResponse(serializedPage.encode('ascii', 'ignore').decode("unicode_escape"),content_type="application/json")


def get_filters_detail(request):
	querySet = models.FilterDetail.objects.filter()
	serializedPage = serializers.serialize('json', querySet)
	jsonArray = json.loads(serializedPage)
	for entry in jsonArray:
		entry["fields"]["filterTitle"] = models.FilterTitle.objects.filter(pk=entry["fields"]["filterTitle"])[0].title
	serializedPage = str(jsonArray)	
	return HttpResponse(serializedPage.encode('ascii', 'ignore').decode("unicode_escape"),content_type="application/json")


# def add_admit_cards(request):


# def add_results(request):	


# def add_answer_keys(request):

