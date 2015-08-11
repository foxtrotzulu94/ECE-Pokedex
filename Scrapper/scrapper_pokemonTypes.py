__author__ = 'as'

import json
import bs4
import sqlite3


# Open the json file
with open("pokemon.json") as data_file:
    data = json.load(data_file)

# Connect to database
conn = sqlite3.connect('..//database/pokedex.sqlite3')
c = conn.cursor();


c.execute("delete from " + "pokemon_types")
c.execute("delete from " + " sqlite_sequence where name = 'pokemon_types'")


uniqueID = 0

currentGeneration = 6

for pokemons in data["pokemon"]:

    for counter, pokemonForm in enumerate(pokemons["alts"]):

        uniqueID += 1

        # Generation that pokemon was first introduced,
        # temp goes through json file to find how many generations the pokemon is in, obviously it is in the latest
        # generation, so [( 1 + currentGeneration) - temp] determines the generation the pokemon was first introduced
        temp = len(pokemons["genfamily"])
        generation = 1 + currentGeneration - temp

        # Generation first appeared is stored in pokemon_common_info table
        # Need to get national ID of the pokemon currently being populated
        # uniqueID =1
        uniqueIDtuple = (uniqueID,)

        pokemon_NationalID_List = c.execute("select * from pokemon_nationalID WHERE pokemonUniqueID=?", uniqueIDtuple)

        pokemon_NationalID = c.fetchall()[0]


        c.execute("UPDATE pokemon_common_info set genFirstAppeared=? WHERE pokemonNationalID = (?)", (generation, pokemon_NationalID[1]))

        # Types that a Pokemon has
        typesList = pokemonForm["types"]

        for types in typesList:
            typesTuple = (types,)
            pokemon_Types_List = c.execute("select * from types WHERE name=?", typesTuple)
            pokemon_Types = c.fetchone()
            c.execute("INSERT INTO pokemon_types VALUES (?,?)", (uniqueID, pokemon_Types[0]))
            print(pokemon_Types[0])


        conn.commit()

conn.commit()
conn.close()