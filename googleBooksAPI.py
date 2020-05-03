import json
import urllib.request

# APIKEY = "AIzaSyCnyD-WBqPTr3gAz9XkpHkB3E7jca8Toas"
URL = "https://www.googleapis.com/books/v1/volumes?q="
search = "isbn:9781506710129"
# #query = "".join([URL, search, "&key=${", APIKEY, "}"])
query = "".join([URL, search])
with urllib.request.urlopen(query) as response:
    html = response.read()

jsonDict = json.loads(html)
print(json.dumps(jsonDict))

# print("data type: {}".format(type(jsonDict)))
itemsList = jsonDict['items']
# print("items type: {}".format(type(itemsList)))
itemsDict = itemsList[0]
# print("items type: {}".format(type(itemsDict)))
itemsJSON = json.dumps(itemsDict)
# print("items type: {}".format(type(itemsJSON)))
items = json.loads(itemsJSON)
volumeInfo = items['volumeInfo']
# print("items type: {}".format(type(volumeInfo)))
print(volumeInfo)
for k in volumeInfo:
    print("{}: {}".format(k, volumeInfo[k]))