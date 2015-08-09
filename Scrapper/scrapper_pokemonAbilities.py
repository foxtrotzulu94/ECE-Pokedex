__author__ = 'as'

import bs4
import json
import sqlite3

with open("pokemon.json") as data_file:
    data = json.load(data_file)

conn = sqlite3.connect('../database/pokedex.sqlite3')
c = conn.cursor()

c.execute("delete from " + "pokemon_abilities")

# this file could possibly "inherit" from "scrapper_pokemon"...
# for pokemons, i in enumerate(data["pokemon"]):
#     pokemonID = pokemons["alts"][0]

if i < len(pokemons[""])

# The database pokemon ID will be mapped with the database ability ID.
# Each pokemon can have up to 3 abilities. The pokemon.json file shows the abilities that each pokemon can have.
# What needs to be done is to match the pokemon ID to the ability ID.
# How can this be done? By parsing through the the json file each time and when the pokemon's ability is found,
# this ability is compared to the abilities table and returns the abilities ID to fill the pokemon_abilities table.
# However, there are multiple abilities per pokemon. So will an intermediary table be used???

# variables: pokemonID (int, not null) and abilityID (int, not null)

conn.commit()
conn.close()
