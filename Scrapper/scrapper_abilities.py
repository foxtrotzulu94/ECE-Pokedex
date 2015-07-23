__author__ = 'as'

import bs4;
import sqlite3;
import json;


#Connect to SQLite
# conn = sqlite3.connect('..//database/pokedex')
#
# c = conn.cursor();
#
# c.execute("delete from " + "abilities")
#
# c.execute("INSERT INTO abilities VALUES (1,'ditto', 'an egg')")
#
#
# conn.commit()
#
# conn.close()


# conn = sqlite3.connect('..//database/pokedex')
# c=conn.cursor()
# c.execute("INSERT INTO typeEffectiveness VALUES (1,'NaN',2)")
# conn.commit()
# conn.close()

# json.load('pokemon.json')

with open("pokemon.json") as data_file:
    data = json.load(data_file)