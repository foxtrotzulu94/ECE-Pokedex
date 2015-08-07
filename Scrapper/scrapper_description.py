__author__ = 'Thinesh'

from bs4 import BeautifulSoup;
import sqlite3;
import json;
import urllib.request

conn = sqlite3.connect('..//database/pokedex.sqlite3')
c = conn.cursor();

c.execute("delete from " + "pokemon_description")

c.execute("delete from " + " sqlite_sequence where name = 'pokemon_description'")

bulbapediaPokedexList = urllib.request.urlopen("http://bulbapedia.bulbagarden.net/wiki/List_of_Pok%C3%A9mon_by_National_Pok%C3%A9dex_number")

inputData = bulbapediaPokedexList.read()
soup = BeautifulSoup(inputData, "html.parser")


# We look for this particular style, this table contains the pokemon
style = "border-radius: 10px; -moz-border-radius: 10px; -webkit-border-radius: 10px; -khtml-border-radius: 10px; -icab-" \
        "border-radius: 10px; -o-border-radius: 10px;; border: 2px solid #FF1111; background: #FF1111;"
generationpokemontables = soup.findAll(attrs={"style": style})
soup.findAll(a)
print(generationpokemontables)
