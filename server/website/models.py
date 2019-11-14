from __future__ import unicode_literals
from django.db import models


class Password(models.Model):
	email = models.CharField(max_length=50, primary_key=True)
	password = models.CharField(max_length=30)

	def __str__(self):
		return str(self.email)

class User(models.Model):
	email = models.CharField(max_length = 50, primary_key=True)
	name = models.CharField(max_length = 50)
	mobile = models.CharField(max_length = 10)
	qualification = models.CharField(max_length = 50) 

	def __str__(self):
		return str(self.email)

class JobResult(models.Model):
	website = models.TextField()							# Website 

	postDt 	= models.TextField()							# Post Date
	rctBrd 	= models.TextField()							# Recruitment Board
	postNm	= models.TextField()							# Post Name
	qual	= models.TextField()							# Qualification
	advtNo 	= models.TextField()							# Advt No
	lastDt	= models.TextField() 							# Last Date
	detail	= models.TextField()							# Detail Link

	def __str__(self):
		return self.postNm

class JobDetail(models.Model):
	job = models.ForeignKey(JobResult, on_delete=models.CASCADE)

	postDt	= models.TextField()							# Post Date
	postNm	= models.TextField()							# Post Name
	ltstUp	= models.TextField()							# Latest Update
	totVacc	= models.TextField()							# Total Vacancy
	brief 	= models.TextField()							# BriefInformation
	appFee	= models.TextField()							# Application Fees
	impDt	= models.TextField()							# Important Dates
	ageLt 	= models.TextField()							# Age Limit
	qual 	= models.TextField()							# Qualification
	vaccDet	= models.TextField()							# Vaccancy Details
	impLinks= models.TextField()							# Important Links
	table 	= models.TextField()							# Complete Table

	def __str__(self):
		return self.postNm


class QuizDetail(models.Model):
	quizNm	= models.TextField()
	brief 	= models.TextField()
	totQues	= models.TextField()
	time	= models.TextField()
	postDt	= models.TextField()

	def __str__(self):
		return self.quizNm

class Question(models.Model):
	quiz 	= models.ForeignKey(QuizDetail, on_delete=models.CASCADE)

	question= models.TextField()
	optA	= models.TextField()
	optB	= models.TextField()
	optC	= models.TextField()
	optD	= models.TextField()
	answer	= models.TextField()

	def __str__(self):
		return self.quiz.quizNm+" - "+self.question

class FilterTitle(models.Model):
	title 	= models.TextField()

	def __str__(self):
		return self.title

class FilterDetail(models.Model):
	filterTitle = models.ForeignKey(FilterTitle, on_delete=models.CASCADE)
	subtitle= models.TextField()
	catogory= models.TextField(max_length=6, choices=(('job','JOB'),('quiz','QUIZ')), default='job')
	match	= models.TextField()

	def __str__(self):
		return self.filterTitle.title + " - " + self.subtitle

class Feedback(models.Model):
	email = models.TextField()
	message = models.TextField()
		
# class AdmitCardResult(models.Model):
# 	website = models.TextField()							# Website 

# 	relDt	= models.TextField()							# Admit Cards Released Date
# 	rctBrd	= models.TextField()							# Recruitment Board
# 	examNm	= models.TextField()							# Exam Name
# 	detail 	= models.TextField()							# Detail Link

# 	def __str__(self):
# 		return self.examNm

# class AdmitCardDetail(models.Model):
# 	admitCard = models.ForeignKey(AdmitCardResult, on_delete=models.CASCADE)

# 	postDt	= models.TextField()							# Post Date
# 	postNm	= models.TextField()							# Post Name
# 	ltstUp	= models.TextField()							# Latest Update
# 	totVacc	= models.TextField()							# Total Vacancy
# 	brief 	= models.TextField()							# BriefInformation
# 	appFee	= models.TextField()							# Application Fees
# 	impDt	= models.TextField()							# Important Dates
# 	ageLt 	= models.TextField()							# Age Limit
# 	qual 	= models.TextField()							# Qualification
# 	vaccDet	= models.TextField()							# Vaccancy Details
# 	impLinks= models.TextField()							# Important Links

# 	def __str__(self):
# 		return self.postNm



# class ResultResult(models.Model):
# 	website = models.TextField()							# Website 

# 	resDt	= models.TextField()							# Results Date
# 	examNm	= models.TextField()							# Exam Name
# 	detail 	= models.TextField()							# Detail Link

# 	def __str__(self):
# 		return self.examNm

# class ResultDetail(models.Model):
# 	result = models.ForeignKey(ResultResult, on_delete=models.CASCADE)

# 	postDt	= models.TextField()							# Post Date
# 	postNm	= models.TextField()							# Post Name
# 	ltstUp	= models.TextField()							# Latest Update
# 	totVacc	= models.TextField()							# Total Vacancy
# 	brief 	= models.TextField()							# BriefInformation
# 	appFee	= models.TextField()							# Application Fees
# 	impDt	= models.TextField()							# Important Dates
# 	ageLt 	= models.TextField()							# Age Limit
# 	qual 	= models.TextField()							# Qualification
# 	vaccDet	= models.TextField()							# Vaccancy Details
# 	impLinks= models.TextField()							# Important Links

# 	def __str__(self):
# 		return self.postNm	



# class AnswerKeyResult(models.Model):
# 	website = models.TextField()							# Website 

# 	postDt 	= models.TextField()							# Post Date
# 	rctBrd 	= models.TextField()							# Recruitment Board	
# 	examNm	= models.TextField()							# Exam Name	
# 	details = models.TextField()							# Detail Link

# 	def __str__(self):
# 		return self.examNm

# class AnswerKeyDetail(models.Model):
# 	answerKey = models.ForeignKey(AnswerKeyResult, on_delete=models.CASCADE)

# 	postDt	= models.TextField()							# Post Date
# 	postNm	= models.TextField()							# Post Name
# 	ltstUp	= models.TextField()							# Latest Update
# 	totVacc	= models.TextField()						# Total Vacancy
# 	brief 	= models.TextField()							# BriefInformation
# 	appFee	= models.TextField()							# Application Fees
# 	impDt	= models.TextField()							# Important Dates
# 	ageLt 	= models.TextField()							# Age Limit
# 	qual 	= models.TextField()							# Qualification
# 	vaccDet	= models.TextField()							# Vaccancy Details
# 	impLinks= models.TextField()							# Important Links

# 	def __str__(self):
# 		return self.postNm