import urllib.request
from bs4 import BeautifulSoup
import datetime
# import os
# import django
# from website import models

# os.environ.setdefault("DJANGO_SETTINGS_MODULE", "server.settings")
# django.setup()

website = "sarkariresult"
jobLink = 'https://www.sarkariresult.com/latestjob.php'
admitCardLink = 'https://www.sarkariresult.com/admitcard.php'
resultLink = 'https://www.sarkariresult.com/result.php'
answerKeyLink = 'https://www.sarkariresult.com/answerkey.php'

def fetchHtml(url):
	headers = {}
	headers['User-Agent'] = "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:48.0) Gecko/20100101 Firefox/48.0"
	req = urllib.request.Request(url, headers = headers)
	fp = urllib.request.urlopen(req)
	myBytes = fp.read()
	myStr = myBytes.decode("utf8")
	fp.close()
	return myStr

def jobFinder():
	# models.JobResult.objects.filter(website=website).delete()

	htmlDoc = fetchHtml(jobLink)
	soup = BeautifulSoup(htmlDoc, 'html5lib')
	postDiv = soup.findAll('div', {'id': 'post'})
	try:	
		rows = postDiv[0].findAll('ul')
		for row in rows:
			postDt 	= ""
			rctBrd 	= ""
			postNm	= ""
			qual	= ""
			advtNo 	= ""
			lastDt	= ""
			detail	= ""

			try:
				postDt 	= str(datetime.datetime.now().strftime("%d-%m-%Y"))
				rctBrd 	= ""
				postNm 	= row.li.findAll('a')[1].text.strip()
				qual 	= ""
				advtNo 	= ""
				lastDt 	= row.li.text.replace(postNm,"") + ": "
				lastDt 	= lastDt.split(":")[1].strip()
				detail 	= row.li.findAll('a')[1]['href'].strip()

				try:
					# models.JobResult.objects.create(website=website,postDt=postDt,rctBrd=rctBrd,postNm=postNm,qual=qual,advtNo=advtNo,lastDt=lastDt,detail=detail)
					print("Job Result Entry Added")
				except:
					print("Unable to add entry")
			except:
				print("Missing entry in this table")
	except:
		print("Post not found")


# jobFinder()


def admitCardFinder():
	# models.AdmitCardResult.objects.filter(website=website).delete()
	
	htmlDoc = fetchHtml(admitCardLink)
	soup = BeautifulSoup(htmlDoc, 'html5lib')
	postDiv = soup.findAll('div', {'id': 'post'})
	try:	
		rows = postDiv[0].findAll('ul')
		for row in rows:
			relDt	= ""
			rctBrd	= ""
			examNm	= ""
			detail 	= ""

			try:
				relDt	= str(datetime.datetime.now().strftime("%d-%m-%Y"))
				rctBrd	= ""
				examNm	= row.li.findAll('a')[1].text.strip()
				detail 	= row.li.findAll('a')[1]['href'].strip()

				try:
					# models.AdmitCardResult.objects.create(website=website,relDt=relDt,rctBrd=rctBrd,examNm=examNm,detail=detail)
					print("Admit Card Result Entry Added")
				except:
					print("Unable to add entry")
			except:
				print("Missing entry in this table")
	except:
		print("Post not found")

# admitCardFinder()

def resultFinder():
	# models.ResultResult.objects.filter(website=website).delete()
	
	htmlDoc = fetchHtml(resultLink)
	soup = BeautifulSoup(htmlDoc, 'html5lib')
	postDiv = soup.findAll('div', {'id': 'post'})
	try:	
		rows = postDiv[0].findAll('ul')
		for row in rows:
			resDt	= ""
			examNm	= ""
			detail 	= ""

			try:
				resDt	= str(datetime.datetime.now().strftime("%d-%m-%Y"))
				examNm	= row.li.findAll('a')[1].text.strip()
				detail 	= row.li.findAll('a')[1]['href'].strip()

				try:
					# models.ResultResult.objects.create(website=website,resDt=resDt,examNm=examNm,deatil=detail)
					print("Result Result Entry Added")
				except:
					print("Unable to add entry")
			except:
				print("Missing entry in this table")
	except:
		print("Post not found")

# examResultFinder()


def answerKeyFinder():
	# models.AnswerKeyResult.objects.filter(website=website).delete()
	
	htmlDoc = fetchHtml(answerKeyLink)
	soup = BeautifulSoup(htmlDoc, 'html5lib')
	postDiv = soup.findAll('div', {'id': 'post'})
	try:	
		rows = postDiv[0].findAll('ul')
		for row in rows:
			postDt	= ""
			rctBrd	= ""
			examNm	= ""
			detail 	= ""

			try:
				postDt	= str(datetime.datetime.now().strftime("%d-%m-%Y"))
				rctBrd 	= ""
				examNm	= row.li.findAll('a')[1].text.strip()
				detail 	= row.li.findAll('a')[1]['href'].strip()

				try:
					# models.AnswerKeyResult.objects.create(website=website,postDt=postDt,rctBrd=rctBrd,examNm=examNm,deatil=detail)
					print("Answer Key Result Entry Added")
				except:
					print("Unable to add entry")
			except:
				print("Missing entry in this table")
	except:
		print("Post not found")


# answerKeyFinder()

def jobDetailParser(jobDetailLink):
	htmlDoc = fetchHtml(jobDetailLink)
	soup = BeautifulSoup(htmlDoc, 'html5lib')
	postContent = soup.find('body').div.div.findAll('table',recursive=False)
	# print(postContent[0].prettify())
	try:
		headings = postContent[0].tbody.tr.findAll('td',recursive=False)[0].div.h2.table.tbody.findAll('tr')
		table = postContent[0].tbody.tr.findAll('td',recursive=False)[0].div.find('table',recursive=False)

		# Creating a Job Model Entry
		topTags 	= ["Name of Post","Post Date","Post Update Date","Short Information"]
		tableTags 	= ["Header","Application Fee","Important Dates","Age Limit","Qualifications","Vacancy Details","Important Links"]
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
		try:
			for heading in headings:
				if heading.findAll('td')[0].text.split(":")[0].strip() == topTags[0]:
					name 	= heading.findAll('td')[1].text.strip()
				elif heading.findAll('td')[0].text.split(":")[0].strip() == topTags[1]:
					date 	= heading.findAll('td')[1].text.strip()
				elif heading.findAll('td')[0].text.split(":")[0].strip() == topTags[2]:
					ltstUp = heading.findAll('td')[1].text.strip()
				elif heading.findAll('td')[0].text.split(":")[0].strip() == topTags[3]:
					brief = heading.findAll('td')[1].text.strip()
		except:
			print("Error creating 'top' for Job Model Entry")

		try:

			print(table.text)
			entries = jobTableParser(table,tableTags)
			# head 	= entries[0]
			# appFee 	= entries[1]
			# impDt 	= entries[2]
			# ageLt 	= entries[3]
			# qual 	= entries[4]
			# vacc 	= entries[5]
			# impLks  = entries[6]
		except:
			print("Error creating 'table' for Job Model Entry")
	except:
		print("Post not found")

def jobTableParser(table, tableTags):
	entries = ["","","","","","",""]
	try:
		tableText = list(filter(None,table.text.split('\n')))
		tableText.insert(0,tableTags[0])
		section = ""
		for line in tableText:
			# print(line)
			if line.strip() in tableTags:
				section = line.strip()
			else:
				entries[tableTags.index(section)] = entries[tableTags.index(section)] + line + "\n" 
	except:
		print("Error in jobTableParser")
	return entries

# jobDetailParser('https://www.sarkariresult.com/bank/new-india-assurance-niacl-ao.php')

def admitCardDetailParser(admitCardDetailLink):
	htmlDoc = fetchHtml(jobDetailLink)
	soup = BeautifulSoup(htmlDoc, 'html5lib')
	postContent = soup.find('body').div.div.findAll('table',recursive=False)
	# print(postContent[0].prettify())
	try:
		headings = postContent[0].tbody.tr.findAll('td',recursive=False)[0].div.h2.table.tbody.findAll('tr')
		table = postContent[0].tbody.tr.findAll('td',recursive=False)[0].div.find('table',recursive=False)

		# Creating a Job Model Entry
		topTags 	= ["Name of Post","Post Date","Post Update Date","Short Information"]
		tableTags 	= ["Header","Application Fee","Important Dates","Age Limit","Qualifications","Vacancy Details","Important Links"]
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
		try:
			for heading in headings:
				if heading.findAll('td')[0].text.split(":")[0].strip() == topTags[0]:
					name 	= heading.findAll('td')[1].text.strip()
				elif heading.findAll('td')[0].text.split(":")[0].strip() == topTags[1]:
					date 	= heading.findAll('td')[1].text.strip()
				elif heading.findAll('td')[0].text.split(":")[0].strip() == topTags[2]:
					ltstUp = heading.findAll('td')[1].text.strip()
				elif heading.findAll('td')[0].text.split(":")[0].strip() == topTags[3]:
					brief = heading.findAll('td')[1].text.strip()
		except:
			print("Error creating 'top' for Job Model Entry")

		try:

			print(table.text)
			entries = jobTableParser(table,tableTags)
			# head 	= entries[0]
			# appFee 	= entries[1]
			# impDt 	= entries[2]
			# ageLt 	= entries[3]
			# qual 	= entries[4]
			# vacc 	= entries[5]
			# impLks  = entries[6]
		except:
			print("Error creating 'table' for Job Model Entry")
	except:
		print("Post not found")

def admitCardTableParser(table, tableTags):
	entries = ["","","","","","",""]
	try:
		tableText = list(filter(None,table.text.split('\n')))
		tableText.insert(0,tableTags[0])
		section = ""
		for line in tableText:
			# print(line)
			if line.strip() in tableTags:
				section = line.strip()
			else:
				entries[tableTags.index(section)] = entries[tableTags.index(section)] + line + "\n" 
	except:
		print("Error in admitCardTableParser")
	return entries

# admitCardDetailParser('http://www.freejobalert.com/drdo-recruitment/17386/')


def resultDetailParser(resultDetailLink):
	htmlDoc = fetchHtml(resultDetailLink)
	soup = BeautifulSoup(htmlDoc, 'html5lib')
	postDiv = soup.findAll('div', {'class': 'PostContent'})
	try:
		children = postDiv[0].find_all(['p','table'],recursive=False)

		# Removing the first child if it is a table (of hyperlinks)
		if children[0].name == 'table':
			children = children[1:]
		try:	
			# Creating a Result Model Entry
			topTags 	= ["Name of the Post","Post Date","Latest Update","Total Vacancy","Brief Information"]
			tableTags 	= ["Header","Application Fee","Important Dates","Age Limit","Qualifications","Vacancy Details","Important Links"]
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
			for child in children: 
				if (child.name == 'p' and ('——————————————' in child.text)) or (children[len(children) - 1] == child):
					# Result Model Entry complete
					print("Result Entry Added") 
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
				elif child.name == 'p':
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
						print("Error creating 'top' for Result Model Entry")

				elif child.name == 'table':
					entries = resultTableParser(child,tableTags)
					head 	= entries[0]
					appFee 	= entries[1]
					impDt 	= entries[2]
					ageLt 	= entries[3]
					qual 	= entries[4]
					vacc 	= entries[5]
					impLks  = entries[6]
		except:
			print("Error creating Result Entries")
	except:
		print("PostContent not found")

def resultTableParser(table, tableTags):
	entries = ["","","","","","",""]
	try:
		tableText = list(filter(None,table.text.split('\n')))
		tableText.insert(0,tableTags[0])
		section = ""
		for line in tableText:
			# print(line)
			if line.strip() in tableTags:
				section = line.strip()
			else:
				entries[tableTags.index(section)] = entries[tableTags.index(section)] + line + "\n" 
	except:
		print("Error in resultTableParser")
	return entries

# resultDetailParser('http://www.freejobalert.com/upsc-nda-na/18623/')


def answerKeyDetailParser(answerKeyDetailLink):
	htmlDoc = fetchHtml(jobDetailLink)
	soup = BeautifulSoup(htmlDoc, 'html5lib')
	postContent = soup.find('body').div.div.findAll('table',recursive=False)
	# print(postContent[0].prettify())
	try:
		headings = postContent[0].tbody.tr.findAll('td',recursive=False)[0].div.h2.table.tbody.findAll('tr')
		table = postContent[0].tbody.tr.findAll('td',recursive=False)[0].div.find('table',recursive=False)

		# Creating a Job Model Entry
		topTags 	= ["Name of Post","Post Date","Post Update Date","Short Information"]
		tableTags 	= ["Header","Application Fee","Important Dates","Age Limit","Qualifications","Vacancy Details","Important Links"]
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
		try:
			for heading in headings:
				if heading.findAll('td')[0].text.split(":")[0].strip() == topTags[0]:
					name 	= heading.findAll('td')[1].text.strip()
				elif heading.findAll('td')[0].text.split(":")[0].strip() == topTags[1]:
					date 	= heading.findAll('td')[1].text.strip()
				elif heading.findAll('td')[0].text.split(":")[0].strip() == topTags[2]:
					ltstUp = heading.findAll('td')[1].text.strip()
				elif heading.findAll('td')[0].text.split(":")[0].strip() == topTags[3]:
					brief = heading.findAll('td')[1].text.strip()
		except:
			print("Error creating 'top' for Job Model Entry")

		try:

			print(table.text)
			entries = jobTableParser(table,tableTags)
			# head 	= entries[0]
			# appFee 	= entries[1]
			# impDt 	= entries[2]
			# ageLt 	= entries[3]
			# qual 	= entries[4]
			# vacc 	= entries[5]
			# impLks  = entries[6]
		except:
			print("Error creating 'table' for Job Model Entry")
	except:
		print("Post not found")

def answerKeyTableParser(table, tableTags):
	entries = ["","","","","","",""]
	try:
		tableText = list(filter(None,table.text.split('\n')))
		tableText.insert(0,tableTags[0])
		section = ""
		for line in tableText:
			# print(line)
			if line.strip() in tableTags:
				section = line.strip()
			else:
				entries[tableTags.index(section)] = entries[tableTags.index(section)] + line + "\n" 
	except:
		print("Error in answerKeyTableParser")
	return entries

# resultDetailParser('http://www.freejobalert.com/upsc-nda-na/18623/')