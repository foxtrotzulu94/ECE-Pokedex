__author__ = 'as'

import bs4
import json
import sqlite3


# used to place what the current generation is, (currently gen 6)
currentGeneration = 6

with open("pokemon.json") as data_file:
    data = json.load(data_file)

conn = sqlite3.connect('..//database/pokedex.sqlite3')
c = conn.cursor();

c.execute("delete from " + "moves")
c.execute("delete from " + " sqlite_sequence where name = 'moves'")


# Parameters: moveID (autoincrements, so put None), name (str), description (str), power (int), accuracy (int), pp (int),
# affects (str), genFirstAppeared (int), secondaryEffects(str), typeID (int), categoryID (int)


# The latest build, with moves dictionary in the pokemon.json file, will be needed
for moves in data["moves"]:
    name = moves["name"]

    # TODO The description of the move (such as "a very fast attack that etc) is not found in the smogon file.
    # The description that is shown is a description of the affects, such as increased defence, etc
    description = "hello"

    power = moves["power"]

    accuracy = moves["accuracy"]

    # Power points, or the number of times the move can be done
    pp = moves["pp"]

    # Look at description above for clarification
    affects = moves["description"]

    # Generation that move was first introduced,
    # temp goes through json file to find how many generations the move is in, obviously it is in the latest
    # generation, so [( 1 + currentGeneration) - temp] determines the generation the move was first introduced
    temp = len(moves["genfamily"])
    genFirstAppeared = 1 + currentGeneration - temp

     # TODO Not sure about secondary effect, like can a move burn and lower attack for example.
     # Growth increases attack and sp attack. Does that count as secondary effect?
     # I feel like we've had this conversation before...

     # TODO TypeID requires an integer, so scrapper_types file needs to be created, although there is info missing
     # such as description
     # TODO How do you connect a file like scrapper_types.py to this one so that you can parse through it to get typeID?
     # typeID = moves["scrapper_types"]

    # Determines whether move is physical, special, or other
    categoryID = moves["category"]

    todo = "TO DO"
    c.execute("INSERT INTO moves VALUES (?,?,?,?,?,?,?,?,?,?,?)", (None, name, description, power, accuracy, pp, affects,
                                                                  genFirstAppeared, todo, todo,
                                                                    # secondaryEffects, typeID,
                                                                  categoryID))
    conn.commit()


conn.commit()
conn.close()