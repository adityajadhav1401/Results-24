from __future__ import unicode_literals
from django.contrib import admin
from .models import *

admin.site.register(JobResult)
admin.site.register(JobDetail)
admin.site.register(Password)
admin.site.register(User)
admin.site.register(QuizDetail)
admin.site.register(Question)
admin.site.register(FilterTitle)
admin.site.register(FilterDetail)
admin.site.register(Feedback)
# admin.site.register(AdmitCardResult)
# admin.site.register(ResultResult)
# admin.site.register(AnswerKeyResult)
# admin.site.register(AdmitCardDetail)
# admin.site.register(ResultDetail)
# admin.site.register(AnswerKeyDetail)

