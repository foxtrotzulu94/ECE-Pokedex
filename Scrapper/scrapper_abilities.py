__author__ = 'as'

import bs4;
import sqlite3;
import json;


#Connect to SQLite


#
# conn = sqlite3.connect('..//database/pokedex.sqlite3')
# c=conn.cursor()
# c.execute("INSERT INTO abilities VALUES (1,'NaN','2')")
# conn.commit()
# conn.close()

# json.load('pokemon.json')

with open("pokemon.json") as data_file:
    data = json.load(data_file)

conn = sqlite3.connect('..//database/pokedex.sqlite3')
c = conn.cursor();

c.execute("delete from " + "abilities")
conn.commit()
counter = 1

for ability in data["abilities"]:
    pokemonName = ability["name"]
    pokemonDescription = ability["description"]
    c.execute("INSERT INTO abilities VALUES (?,?,?)",(counter,pokemonName,pokemonDescription))
    conn.commit()
    counter+=1


conn.commit()
conn.close()