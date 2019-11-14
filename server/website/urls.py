from django.urls import path
from django.urls import re_path
from . import views
from django.conf import settings

urlpatterns = [
	path('login/', views.login, name = 'login'),
    path('add_jobs/', views.add_jobs, name = 'add_jobs'),
    path('add_jobs_detail/', views.add_jobs_detail, name = 'add_jobs_detail'),
    path('get_jobs/', views.get_jobs, name = 'get_jobs'),
    path('get_jobs_detail/', views.get_jobs_detail, name = 'get_jobs_detail'),
    path('get_quizzes/', views.get_quizzes, name = 'get_quizzes'),
    path('get_quizzes_detail/', views.get_quizzes_detail, name = 'get_quizzes_detail'),

    path('get_filters/', views.get_filters, name = 'get_filters'),
    path('get_filters_detail/', views.get_filters_detail, name = 'get_filters_detail'),


    # path('add_admit_cards/', views.add_admit_cards, name = 'add_admit_cards'),
    # path('add_results/', views.add_results, name = 'add_results'),
    # path('add_answer_keys/', views.add_answer_keys, name = 'add_answer_keys'),
]