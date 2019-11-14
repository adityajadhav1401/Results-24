import urllib.request
from bs4 import BeautifulSoup
import os
import django
from website import models
from django.http import HttpResponse
from socket import timeout

os.environ.setdefault("DJANGO_SETTINGS_MODULE", "server.settings")
django.setup()

website = "freejobalert"
jobLink = 'http://www.freejobalert.com/latest-notifications/'
admitCardLink = 'http://www.freejobalert.com/admit-card-call-letter/'
resultLink = 'http://www.freejobalert.com/exam-results/'
answerKeyLink = 'http://www.freejobalert.com/answer-key/'

def fetchHtml(url):
	fp = urllib.request.urlopen(url,timeout=5)
	myBytes = fp.read()
	myStr = myBytes.decode("utf8")
	fp.close()
	return myStr



def jobFinder():
	entriesDeleted = models.JobResult.objects.filter(website=website).delete()
	entriesAdded = 0;

	try:
		htmlDoc = fetchHtml(jobLink)
		soup = BeautifulSoup(htmlDoc, 'html.parser')
		postDiv = soup.findAll('div', {'class': 'post'})
		tables = postDiv[0].findAll('table')
		try:
			tables = tables[1:]
			
			for table in tables:
				tbody = table.findAll('tbody')
				rows = tbody[0].findAll('tr')
				try:
					for row in rows:
						entries = row.findAll('td')
						postDt 	= ""
						rctBrd 	= ""
						postNm	= ""
						qual	= ""
						advtNo 	= ""
						lastDt	= ""
						detail	= ""
						
						try:
							postDt 	= entries[0].string.strip()
							rctBrd 	= entries[1].string.strip()
							postNm 	= entries[2].string.strip()
							qual 	= entries[3].string.strip()
							advtNo 	= entries[4].string.strip()
							lastDt 	= entries[5].string.strip()
							detail 	= entries[6].a['href']

							try:
								models.JobResult.objects.create(website=website,postDt=postDt,rctBrd=rctBrd,postNm=postNm,qual=qual,advtNo=advtNo,lastDt=lastDt,detail=detail)
								print("Job Result Entry Added")
								entriesAdded = entriesAdded + 1
							except:
								print("Unable to add entry")
						except:
							print("Missing entry in this table")
				except:
					print("No rows in this table")
		except:
			print("No tables in post")
	except:
		print("Post not found")

	response = "Entries added : " + str(entriesAdded) + "<br>" + "Entries Deleted : " + str(entriesDeleted)
	return HttpResponse(response)



def jobDetailAdder():
	entriesAdded = 0;
	for model in models.JobResult.objects.filter(website=website):
		entriesAdded = entriesAdded + jobDetailParser(model)

	response = "Entries added : " + str(entriesAdded)
	return HttpResponse(response)	




def jobDetailParser(model):
	jobDetailLink = model.detail
	jobsAdded = 0

	try:
		htmlDoc = fetchHtml(jobDetailLink)
		soup = BeautifulSoup(htmlDoc, 'html5lib')
		postDiv = soup.findAll('div', {'class': 'PostContent'})
		children = postDiv[0].find_all(['p','table'],recursive=False)

		# Removing the first child if it is a table (of hyperlinks)
		if children[0].name == 'table':
			children = children[1:]
		try:	
			# Creating a Job Model Entry
			topTags 	= ["Name of the Post","Post Date","Latest Update","Total Vacancy","Brief Information"]
			tableTags 	= ["Header","Application Fee","Important Dates","Age Limit","Qualification","Vacancy Details","Important Links"]
			name 	= ""	# Post Name
			date 	= ""	# Post Date
			ltstUp	= "" 	# Latest Update
			totVacc	= "" 	# Total Vacancy
			brief 	= "" 	# Brief Information
			head 	= "" 	# Header : 0
			appFee 	= "" 	# Application Fees : 1
			impDt	= "" 	# Important Dates : 2
			ageLt 	= "" 	# Age Limit : 3
			qual	= "" 	# Qualifications : 4
			vacc	= "" 	# Vaccancy Details : 5
			impLks	= ""	# Important Links : 6
			table 	= ""
			for child in children: 
				if child.name == 'p':
					try:
						if child.text.split(":")[0].strip() == topTags[0]:
							name 	= child.text.split(":")[1].strip()
						elif child.text.split(":")[0].strip() == topTags[1]:
							date 	= child.text.split(":")[1].strip()
						elif child.text.split(":")[0].strip() == topTags[2]:
							ltstUp 	= child.text.split(":")[1].strip()
						elif child.text.split(":")[0].strip() == topTags[3]:
							totVacc = child.text.split(":")[1].strip()
						elif child.text.split(":")[0].strip() == topTags[4]:
							brief = child.text.split(":")[1].strip()
					except:
						print("Error creating 'top' for Job Model Entry")

				elif child.name == 'table':
					entries = jobTableParser(child,tableTags)
					head 	= entries[0]
					appFee 	= entries[1]
					impDt 	= entries[2]
					ageLt 	= entries[3]
					qual 	= entries[4]
					vacc 	= entries[5]
					impLks  = entries[6] 
					table 	= str(child)

					try:
						table = table.replace("http://www.freejobalert.com","#")
						table = table.replace("WWW.FREEJOBALERT.COM","RESULT 24")
					except:
						print("Error replacing base Website Info")

					# Job Model Entry complete
					print("Job Detail Added") 
					jobsAdded = jobsAdded + 1
					models.JobDetail.objects.create(job=model,postDt=date,postNm=name,ltstUp=ltstUp,totVacc=totVacc,brief=brief,appFee=appFee,impDt=impDt,
						ageLt=ageLt,qual=qual,vaccDet=vacc,impLinks=impLks,table=table)

					name 	= ""	
					date 	= ""	
					ltstUp	= "" 	
					totVacc	= "" 	
					brief 	= "" 	
					head 	= "" 	
					appFee 	= "" 	
					impDt	= "" 	
					ageLt 	= "" 	
					qual	= "" 	
					vacc	= "" 	
					impLks	= ""
					table 	= ""
		except:
			print("Error creating Job Detail Entries")
	except:
		print("PostContent not found")

	return jobsAdded



def jobTableParser(table, tableTags):
	entries = ["","","","","","",""]
	try:
		tableText = list(filter(None,table.text.split('\n')))
		tableText.insert(0,tableTags[0])
		section = ""
		# print(tableText)
		for line in tableText:
			for tag in tableTags:
				if tag in line.strip():
					section = tag
					break;

			entries[tableTags.index(section)] = entries[tableTags.index(section)] + line + "\n" 
	except:
		print("Error in jobTableParser")
	return entries































# def admitCardFinder():
# 	htmlDoc = fetchHtml(admitCardLink)
# 	soup = BeautifulSoup(htmlDoc, 'html.parser')
# 	postDiv = soup.findAll('div', {'class': 'PostContent'})
# 	try:
# 		tables = postDiv[0].findAll('table')
# 		try:
# 			tables = tables[2:]
			
# 			for table in tables:
# 				tbody = table.findAll('tbody')
# 				rows = tbody[0].findAll('tr')[1:]
# 				try:
# 					for row in rows:
# 						entries = row.findAll('td')
# 						try:
# 							for entry in entries:
# 								if entry.a:
# 									print(entry.a['href'], end='\t')
# 								else:
# 									print(entry.string.strip(), end='\t')
# 							print('\n')

# 						except:
# 							print("No entry in this table")
# 				except:
# 					print("No rows in this table")
# 		except:
# 			print("No tables in post")
# 	except:
# 		print("Post not found")

# # admitCardFinder()

# def resultFinder():
# 	htmlDoc = fetchHtml(resultLink)
# 	soup = BeautifulSoup(htmlDoc, 'html.parser')
# 	postDiv = soup.findAll('div', {'class': 'PostContent'})
# 	try:
# 		tables = postDiv[0].findAll('table')
# 		try:
# 			tables = tables[2:]
			
# 			for table in tables:
# 				tbody = table.findAll('tbody')
# 				rows = tbody[0].findAll('tr')[1:]
# 				try:
# 					for row in rows:
# 						entries = row.findAll('td')
# 						try:
# 							for entry in entries:
# 								if entry.a:
# 									print(entry.a['href'], end='\t')
# 								else:
# 									print(entry.string.strip(), end='\t')
# 							print('\n')

# 						except:
# 							print("No entry in this table")
# 				except:
# 					print("No rows in this table")
# 		except:
# 			print("No tables in post")
# 	except:
# 		print("Post not found")


# # resultFinder()


# def answerKeyFinder():
# 	htmlDoc = fetchHtml(answerKeyLink)
# 	soup = BeautifulSoup(htmlDoc, 'html.parser')
# 	postDiv = soup.findAll('div', {'class': 'post'})
# 	try:
# 		tables = postDiv[0].findAll('table')
# 		try:
# 			tables = tables[2:]
			
# 			for table in tables:
# 				tbody = table.findAll('tbody')
# 				rows = tbody[0].findAll('tr')[1:]
# 				try:
# 					for row in rows:
# 						entries = row.findAll('td')
# 						try:
# 							for entry in entries:
# 								if entry.a:
# 									print(entry.a['href'], end='\t')
# 								else:
# 									print(entry.string.strip(), end='\t')
# 							print('\n')

# 						except:
# 							print("No entry in this table")
# 				except:
# 					print("No rows in this table")
# 		except:
# 			print("No tables in post")
# 	except:
# 		print("Post not found")


# # answerKeyFinder()

# def admitCardDetailParser(admitCardDetailLink):
# 	htmlDoc = fetchHtml(admitCardDetailLink)
# 	soup = BeautifulSoup(htmlDoc, 'html5lib')
# 	postDiv = soup.findAll('div', {'class': 'PostContent'})
# 	try:
# 		children = postDiv[0].find_all(['p','table'],recursive=False)

# 		# Removing the first child if it is a table (of hyperlinks)
# 		if children[0].name == 'table':
# 			children = children[1:]
# 		try:	
# 			# Creating a Admit Card Model Entry
# 			topTags 	= ["Name of the Post","Post Date","Latest Update","Total Vacancy","Brief Information"]
# 			tableTags 	= ["Header","Application Fee","Important Dates","Age Limit","Qualifications","Vacancy Details","Important Links"]
# 			name 	= ""	# Post Name
# 			date 	= ""	# Post Date
# 			ltstUp	= "" 	# Latest Update
# 			totVacc	= "" 	# Total Vacancy
# 			brief 	= "" 	# Brief Information
# 			head 	= "" 	# Header : 0
# 			appFee 	= "" 	# Application Fees : 1
# 			impDt	= "" 	# Important Dates : 2
# 			ageLt 	= "" 	# Age Limit : 3
# 			qual	= "" 	# Qualifications : 4
# 			vacc	= "" 	# Vaccancy Details : 5
# 			impLks	= ""	# Important Links : 6
# 			for child in children: 
# 				if (child.name == 'p' and ('——————————————' in child.text)) or (children[len(children) - 1] == child):
# 					# Admit Card Model Entry complete
# 					print("Admit Card Entry Added") 
# 					name 	= ""	
# 					date 	= ""	
# 					ltstUp	= "" 	
# 					totVacc	= "" 	
# 					brief 	= "" 	
# 					head 	= "" 	
# 					appFee 	= "" 	
# 					impDt	= "" 	
# 					ageLt 	= "" 	
# 					qual	= "" 	
# 					vacc	= "" 	
# 					impLks	= ""	
# 				elif child.name == 'p':
# 					try:
# 						if child.text.split(":")[0].strip() == topTags[0]:
# 							name 	= child.text.split(":")[1].strip()
# 						elif child.text.split(":")[0].strip() == topTags[1]:
# 							date 	= child.text.split(":")[1].strip()
# 						elif child.text.split(":")[0].strip() == topTags[2]:
# 							ltstUp 	= child.text.split(":")[1].strip()
# 						elif child.text.split(":")[0].strip() == topTags[3]:
# 							totVacc = child.text.split(":")[1].strip()
# 						elif child.text.split(":")[0].strip() == topTags[4]:
# 							brief = child.text.split(":")[1].strip()
# 					except:
# 						print("Error creating 'top' for Admit Card Model Entry")

# 				elif child.name == 'table':
# 					entries = admitCardTableParser(child,tableTags)
# 					head 	= entries[0]
# 					appFee 	= entries[1]
# 					impDt 	= entries[2]
# 					ageLt 	= entries[3]
# 					qual 	= entries[4]
# 					vacc 	= entries[5]
# 					impLks  = entries[6]
# 		except:
# 			print("Error creating Admit Card Entries")
# 	except:
# 		print("PostContent not found")

# def admitCardTableParser(table, tableTags):
# 	entries = ["","","","","","",""]
# 	try:
# 		tableText = list(filter(None,table.text.split('\n')))
# 		tableText.insert(0,tableTags[0])
# 		section = ""
# 		for line in tableText:
# 			# print(line)
# 			if line.strip() in tableTags:
# 				section = line.strip()
# 			else:
# 				entries[tableTags.index(section)] = entries[tableTags.index(section)] + line + "\n" 
# 	except:
# 		print("Error in admitCardTableParser")
# 	return entries

# # admitCardDetailParser('http://www.freejobalert.com/drdo-recruitment/17386/')


# def resultDetailParser(resultDetailLink):
# 	htmlDoc = fetchHtml(resultDetailLink)
# 	soup = BeautifulSoup(htmlDoc, 'html5lib')
# 	postDiv = soup.findAll('div', {'class': 'PostContent'})
# 	try:
# 		children = postDiv[0].find_all(['p','table'],recursive=False)

# 		# Removing the first child if it is a table (of hyperlinks)
# 		if children[0].name == 'table':
# 			children = children[1:]
# 		try:	
# 			# Creating a Result Model Entry
# 			topTags 	= ["Name of the Post","Post Date","Latest Update","Total Vacancy","Brief Information"]
# 			tableTags 	= ["Header","Application Fee","Important Dates","Age Limit","Qualifications","Vacancy Details","Important Links"]
# 			name 	= ""	# Post Name
# 			date 	= ""	# Post Date
# 			ltstUp	= "" 	# Latest Update
# 			totVacc	= "" 	# Total Vacancy
# 			brief 	= "" 	# Brief Information
# 			head 	= "" 	# Header : 0
# 			appFee 	= "" 	# Application Fees : 1
# 			impDt	= "" 	# Important Dates : 2
# 			ageLt 	= "" 	# Age Limit : 3
# 			qual	= "" 	# Qualifications : 4
# 			vacc	= "" 	# Vaccancy Details : 5
# 			impLks	= ""	# Important Links : 6
# 			for child in children: 
# 				if (child.name == 'p' and ('——————————————' in child.text)) or (children[len(children) - 1] == child):
# 					# Result Model Entry complete
# 					print("Result Entry Added") 
# 					name 	= ""	
# 					date 	= ""	
# 					ltstUp	= "" 	
# 					totVacc	= "" 	
# 					brief 	= "" 	
# 					head 	= "" 	
# 					appFee 	= "" 	
# 					impDt	= "" 	
# 					ageLt 	= "" 	
# 					qual	= "" 	
# 					vacc	= "" 	
# 					impLks	= ""	
# 				elif child.name == 'p':
# 					try:
# 						if child.text.split(":")[0].strip() == topTags[0]:
# 							name 	= child.text.split(":")[1].strip()
# 						elif child.text.split(":")[0].strip() == topTags[1]:
# 							date 	= child.text.split(":")[1].strip()
# 						elif child.text.split(":")[0].strip() == topTags[2]:
# 							ltstUp 	= child.text.split(":")[1].strip()
# 						elif child.text.split(":")[0].strip() == topTags[3]:
# 							totVacc = child.text.split(":")[1].strip()
# 						elif child.text.split(":")[0].strip() == topTags[4]:
# 							brief = child.text.split(":")[1].strip()
# 					except:
# 						print("Error creating 'top' for Result Model Entry")

# 				elif child.name == 'table':
# 					entries = resultTableParser(child,tableTags)
# 					head 	= entries[0]
# 					appFee 	= entries[1]
# 					impDt 	= entries[2]
# 					ageLt 	= entries[3]
# 					qual 	= entries[4]
# 					vacc 	= entries[5]
# 					impLks  = entries[6]
# 		except:
# 			print("Error creating Result Entries")
# 	except:
# 		print("PostContent not found")

# def resultTableParser(table, tableTags):
# 	entries = ["","","","","","",""]
# 	try:
# 		tableText = list(filter(None,table.text.split('\n')))
# 		tableText.insert(0,tableTags[0])
# 		section = ""
# 		for line in tableText:
# 			# print(line)
# 			if line.strip() in tableTags:
# 				section = line.strip()
# 			else:
# 				entries[tableTags.index(section)] = entries[tableTags.index(section)] + line + "\n" 
# 	except:
# 		print("Error in resultTableParser")
# 	return entries

# # resultDetailParser('http://www.freejobalert.com/upsc-nda-na/18623/')


# def answerKeyDetailParser(answerKeyDetailLink):
# 	htmlDoc = fetchHtml(answerKeyDetailLink)
# 	soup = BeautifulSoup(htmlDoc, 'html5lib')
# 	postDiv = soup.findAll('div', {'class': 'PostContent'})
# 	try:
# 		children = postDiv[0].find_all(['p','table'],recursive=False)

# 		# Removing the first child if it is a table (of hyperlinks)
# 		if children[0].name == 'table':
# 			children = children[1:]
# 		try:	
# 			# Creating a Answer Key Model Entry
# 			topTags 	= ["Name of the Post","Post Date","Latest Update","Total Vacancy","Brief Information"]
# 			tableTags 	= ["Header","Application Fee","Important Dates","Age Limit","Qualifications","Vacancy Details","Important Links"]
# 			name 	= ""	# Post Name
# 			date 	= ""	# Post Date
# 			ltstUp	= "" 	# Latest Update
# 			totVacc	= "" 	# Total Vacancy
# 			brief 	= "" 	# Brief Information
# 			head 	= "" 	# Header : 0
# 			appFee 	= "" 	# Application Fees : 1
# 			impDt	= "" 	# Important Dates : 2
# 			ageLt 	= "" 	# Age Limit : 3
# 			qual	= "" 	# Qualifications : 4
# 			vacc	= "" 	# Vaccancy Details : 5
# 			impLks	= ""	# Important Links : 6
# 			for child in children: 
# 				if (child.name == 'p' and ('——————————————' in child.text)) or (children[len(children) - 1] == child):
# 					# Answer Key Entry complete
# 					print("Answer Key Entry Added") 
# 					name 	= ""	
# 					date 	= ""	
# 					ltstUp	= "" 	
# 					totVacc	= "" 	
# 					brief 	= "" 	
# 					head 	= "" 	
# 					appFee 	= "" 	
# 					impDt	= "" 	
# 					ageLt 	= "" 	
# 					qual	= "" 	
# 					vacc	= "" 	
# 					impLks	= ""	
# 				elif child.name == 'p':
# 					try:
# 						if child.text.split(":")[0].strip() == topTags[0]:
# 							name 	= child.text.split(":")[1].strip()
# 						elif child.text.split(":")[0].strip() == topTags[1]:
# 							date 	= child.text.split(":")[1].strip()
# 						elif child.text.split(":")[0].strip() == topTags[2]:
# 							ltstUp 	= child.text.split(":")[1].strip()
# 						elif child.text.split(":")[0].strip() == topTags[3]:
# 							totVacc = child.text.split(":")[1].strip()
# 						elif child.text.split(":")[0].strip() == topTags[4]:
# 							brief = child.text.split(":")[1].strip()
# 					except:
# 						print("Error creating 'top' for Answer Key Model Entry")

# 				elif child.name == 'table':
# 					entries = answerKeyTableParser(child,tableTags)
# 					head 	= entries[0]
# 					appFee 	= entries[1]
# 					impDt 	= entries[2]
# 					ageLt 	= entries[3]
# 					qual 	= entries[4]
# 					vacc 	= entries[5]
# 					impLks  = entries[6]
# 		except:
# 			print("Error creating Answer Key Entries")
# 	except:
# 		print("PostContent not found")

# def answerKeyTableParser(table, tableTags):
# 	entries = ["","","","","","",""]
# 	try:
# 		tableText = list(filter(None,table.text.split('\n')))
# 		tableText.insert(0,tableTags[0])
# 		section = ""
# 		for line in tableText:
# 			# print(line)
# 			if line.strip() in tableTags:
# 				section = line.strip()
# 			else:
# 				entries[tableTags.index(section)] = entries[tableTags.index(section)] + line + "\n" 
# 	except:
# 		print("Error in answerKeyTableParser")
# 	return entries

# # resultDetailParser('http://www.freejobalert.com/upsc-nda-na/18623/')