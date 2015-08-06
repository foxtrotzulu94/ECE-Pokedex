__author__ = 'Thinesh'

import bs4;
import sqlite3;
import json;
import urllib.request

conn = sqlite3.connect('..//database/pokedex.sqlite3')
c = conn.cursor();

//No

bulbapediaPokedexList = urllib.request.urlopen("http://www.serebii.net/pokedex-xy/")
