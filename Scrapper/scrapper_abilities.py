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


# TODO Change the counter so that autoincrement works properly using the none keyword, look at pokemon scrapper file
with open("pokemon.json") as data_file:
    data = json.load(data_file)

conn = sqlite3.connect('..//database/pokedex.sqlite3')
c = conn.cursor();

c.execute("delete from " + "abilities")
conn.commit()
counter = 1

for ability in data["abilities"]:
    abilityName = ability["name"]
    abilityDescription = ability["description"]
    c.execute("INSERT INTO abilities VALUES (?,?,?)",(counter, abilityName, abilityDescription))
    conn.commit()
    counter+=1


conn.commit()
conn.close()