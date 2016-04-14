__author__ = 'as'

from bs4 import BeautifulSoup
import json
import sqlite3
import urllib.request
import urllib.parse
import urllib.error
import urllib
import os
from urllib.request import urlretrieve


mapsURL = "http://archives.bulbagarden.net/w/index.php?title=Special:Search&limit=1000&offset=0&profile=images&search=*Map.png"
bulbapediaMapsPage = urllib.request.urlopen(mapsURL)
inputMaps = bulbapediaMapsPage.read()
soup = BeautifulSoup(inputMaps, "html.parser")

# Cache this page as it might be removed in future, if it does not exist
if not os.path.isfile('maps_websitelist.txt'):
        urlretrieve(mapsURL, 'maps_websitelist.txt')

# Search for class with all the maps hrefs
mapsList = soup.find(attrs={"class": "mw-search-results"})
unfilteredList = []
#print(mapsList)

# Append all the hrefs to unfilteredList, the list which is searched through for images of each region
for link in mapsList.find_all('a'):
    mapLink = link.get('href')
    # print(mapLink)
    unfilteredList.append(mapLink)

regionList = ["Kanto", "Johto", "Hoenn", "Sinnoh", "Unova", "Kalos"]

# Create dictionary of each region
regionDictionary = {"Kanto":[], "Johto":[], "Hoenn":[], "Sinnoh":[], "Unova":[], "Kalos":[]}

# Search through images for those that are in a region, then append to dictionary
for image in unfilteredList:
    for name in regionList:
        if name in image:
            regionDictionary[name].append(image)

for region in regionList:
    try:
        os.mkdir(region,)
    except:
        print("Directory "+region+" already exists!")

    for image in regionDictionary[region]:
        bulbapediaLink = "http://bulbapedia.bulbagarden.net"
        rawURL = bulbapediaLink + image
        # regionDictionary[region][image] = regionDictionary[region][rawURL]
        imageWebsite = urllib.request.urlopen(rawURL)
        imageText = imageWebsite.read()
        soup = BeautifulSoup(imageText, "html.parser")
        imageSoup = soup.find(attrs={"class": "fullImageLink"})
        mapImage = imageSoup.a.img.get("src")
        imageName = mapImage.split('/')[-1]
        urllib.request.urlretrieve(mapImage, region + "\\" + imageName)





# print(regionListFolder)




# while "href" in mapsList:
#     mapName =
########################################
