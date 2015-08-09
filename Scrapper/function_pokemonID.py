__author__ = 'as'

import bs4
import sqlite3
import json
import pykemon
#
# with open("pokemon.json") as data_file:
#     data = json.load(data_file)
#
# conn = sqlite3.connect('..//database/pokedex.sqlite3')
# c = conn.cursor();

temp = pykemon.get(pokemon='ivysaur')

# GET http://pokeapi.co/api/v1/ability/1/