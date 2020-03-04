import requests 


url = 'http://localhost:9123/test?parameter=asdasd'

requests = requests.get(url)

print (requests.content)