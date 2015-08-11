__author__ = 'as'

import bs4
import json
import sqlite3


# scrapper populates the following tables: moves, category, types
#
# used to place what the current generation is, (currently gen 6)
currentGeneration = 6


with open("pokemon.json") as data_file:
    data = json.load(data_file)

conn = sqlite3.connect('..//database/pokedex.sqlite3')
c = conn.cursor();


c.execute("delete from " + "moves")
c.execute("delete from " + "category")
c.execute("delete from " + "types")
c.execute("delete from " + "types_effectiveness")
c.execute("delete from " + " sqlite_sequence where name = 'moves'")
c.execute("delete from " + " sqlite_sequence where name = 'category'")
c.execute("delete from " + " sqlite_sequence where name = 'types'")
c.execute("delete from " + " sqlite_sequence where name = 'types_effectiveness'")


# Used for listing the form of the move, whether physical (fighting) or special (fire), or Non-Damage (status boost)
categoryList = ["Physical", "Special", "Non-Damaging"]
for category in categoryList:
    c.execute("INSERT INTO category VALUES (?,?)", (None, category))
conn.commit()


# Goes through json type dictionary to first find name, compare type to name, then append it to list
typeListInfo = data["types"]
typeList = []
for type in typeListInfo:
    # if 'name'in type:
    typeList.append(type['name'])



for type in typeList:
    c.execute("INSERT INTO types VALUES (?,?)", (None, type))
conn.commit()


for type in typeListInfo:
    fromTypeID = typeList.index(type['name'])+1

    for type_effectivnes in type['atk_effectives']:
        toTypeId = typeList.index(type_effectivnes[0])+1
        effectiveLevel = type_effectivnes[1]
        c.execute("INSERT INTO types_effectiveness VALUES (?,?,?)", (fromTypeID, effectiveLevel, toTypeId))
conn.commit()




# Parameters: moveID (autoincrements, so put None), name (str), description (str), power (int), accuracy (int), pp (int),
# affects (str), genFirstAppeared (int), secondaryEffects(str), typeID (int), categoryID (int)


# The latest build, with moves dictionary in the pokemon.json file, will be needed
for moves in data["moves"]:
    name = moves["name"]

    # The description shows what the move does, such as increased defence, etc
    description = moves["description"]

    power = moves["power"]

    accuracy = moves["accuracy"]

    # Power points, or the number of times the move can be done
    pp = moves["pp"]

    # TODO
    # Look at description above for clarification
    affects = "hello"

    # Generation that move was first introduced,
    # temp goes through json file to find how many generations the move is in, obviously it is in the latest
    # generation, so [( 1 + currentGeneration) - temp] determines the generation the move was first introduced
    temp = len(moves["genfamily"])
    genFirstAppeared = 1 + currentGeneration - temp

     # TODO Affects column in table needs to be added

    typeString = moves["type"]
    typeID = typeList.index(typeString) + 1

    # Determines whether move is physical, special, or other
    categoryString = moves["category"]
    categoryID = categoryList.index(categoryString) + 1




    todo = "TO DO"
    c.execute("INSERT INTO moves VALUES (?,?,?,?,?,?,?,?,?,?)", (None, name, description, power, accuracy, pp, affects,
                                                                  genFirstAppeared, typeID,
                                                                  categoryID))
    conn.commit()


conn.commit()
conn.close()