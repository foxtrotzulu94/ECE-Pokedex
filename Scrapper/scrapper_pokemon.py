__author__ = 'as'

import bs4;
import sqlite3;
import json;
#import pykemon


### RANDOM CRAP
    #pokemonHeight = type(pokemons["alts"][0]["height"])
    #len(genArray)
    #int(pokemonHeight)
    #print(height) #checks the data type
###


# used to place what the current generation is, (currently gen 6)
currentGeneration = 6

with open("pokemon.json") as data_file:
    data = json.load(data_file)

conn = sqlite3.connect('..//database/pokedex.sqlite3')
c = conn.cursor();

c.execute("delete from " + "pokemon_unique_info")
# c.execute("ALTER TABLE pokemon AUTOINCREMENT = 1")
c.execute("delete from " + " sqlite_sequence where name = 'pokemon'")

c.execute("delete from " + "pokemon_suffix")
c.execute("delete from" + " sqlite_sequence where name = 'pokemon_suffix'")
conn.commit()
counter = 1


for pokemons in data["pokemon"]:
    # Length of alts, to determine whether pokemon has mega evolutions

    # Name of the pokemon
    name = pokemons["name"]




    # Generation that pokemon was first introduced,
    # temp goes through json file to find how many generations the pokemon is in, obviously it is in the latest
    # generation, so [( 1 + currentGeneration) - temp] determines the generation the pokemon was first introduced
    temp = len(pokemons["genfamily"])
    generation = 1 + currentGeneration - temp

    # Generation first appeared is stored in pokemon_common_info table
    # Need to get national ID of the pokemon currently being populated

    # c.execute("UPDATE pokemon_common_info set genFirstAppeared=? WHERE ")

    #length = len(pokemons["alts"])

    # i is used as a counter but not a counter, which goes through the form (1,2,3) numbering the dictionary while
    # pokemon form simply writes the stats of each form
    for i, pokemonForm in enumerate(pokemons["alts"]):


        # Height of the pokemon, in meters
        height = pokemons["alts"][i]["height"]

        # Weight of the pokemon, in kilograms
        weight = pokemons["alts"][i]["weight"]

        # Attack of the pokemon
        attack = pokemons["alts"][i]["atk"]

        # Defence of the pokemon
        defence = pokemons["alts"][i]["def"]

        # Health points (HP) of the pokemon
        healthPoints = pokemons["alts"][i]["hp"]

        # Special Attack of the pokemon
        spAttack = pokemons["alts"][i]["spa"]

        # Special Defence of the pokemon
        spDefence = pokemons["alts"][i]["spd"]

        # Speed of the pokemon
        speed = pokemons["alts"][i]["spe"]



        #Pushes entries into database
        c.execute("INSERT INTO pokemon VALUES (?,?,?,?,?,?,?,?,?,?)", (None, name,
                                                                                   height, weight, attack,
                                                                                   defence, healthPoints, spAttack,
                                                                                   spDefence, speed))

        conn.commit()

        #++
        pokemonID = c.lastrowid
        suffix = pokemons["alts"][i]["suffix"]

        if suffix is not "":
            print(suffix)
            c.execute("INSERT INTO pokemon_suffix VALUES (?,?)", (pokemonID, suffix))
            conn.commit()

    # c.execute("INSERT INTO pokemon ( name, description, height, weight, attack, defence, hp, spattack, spdefence, speed, genFirstAppeared, hatchTime, catchRate, genderRatioMale) VALUES ( name, description, height, weight, attack, defence, healthPoints, spAttack, spDefence, speed, generation, hatchTime, catchRate, genderRatioMale)")


 #   counter+=1


conn.commit()
conn.close()