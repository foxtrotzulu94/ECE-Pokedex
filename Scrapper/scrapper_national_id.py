__author__ = 'as'

from bs4 import BeautifulSoup
import json
import sqlite3
import urllib.request
import urllib.parse
import urllib.error


# Learn how to scrape from website!

serebiiPokedex = urllib.request.urlopen("http://www.serebii.net/pokedex-xy/")
# print(serebiiPokedex.read())

inputData = serebiiPokedex.read()
soup = BeautifulSoup(inputData, "html.parser")
# form = soup.find('form', id = 'nav')
form = soup.find_all('form')

i=1
while i<=721:
    nationalID = "%03d" % (i)
    i += 1
    formline = soup.find(value="/pokedex-xy/"+nationalID+".shtml")
    print (formline)
# form = soup.find("form", attrs={"FORM NAME": "nav"})
# pret = form.prettify()
