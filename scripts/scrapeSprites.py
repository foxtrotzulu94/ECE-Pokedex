__author__ = 'Nicole'

from urllib import request
from bs4 import BeautifulSoup

baseUrl = 'http://pokeapi.co/media/img/'

for i in range(1, 719):
    url = baseUrl + str(i) + '.png'
    file = 'sprites/p'+str(i)+'.png'
    output = open(file, "wb")
    download_img = request.urlopen(url)
    output.write(download_img.read())
    output.close()
