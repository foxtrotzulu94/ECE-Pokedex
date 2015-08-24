__author__ = 'as'

import sqlite3

## Function that either takes an exact pokemon Name, or pokemonNationalID and returns the pokemonUniqueID
## In the case of megas, of various forms that share the same name, or ID all will be returned

## To use from Scrapper import function_pokemonID

# Connect to database by the second argument, pass the cursor that's connected to our database



# example of use:
# >>> function_pokemonID.returnPokemonUniqueIDFromNationalID(9, c)
# [(675, 9), (676, 9)]
# position 0 would be the first non-mega instance
# Position 0 of each tuple within is the uniqueID
def returnPokemonUniqueIDFromNationalID(pokemonNationalID, databasecursor):

    nationalPokemonIDtuple = (pokemonNationalID,)
    pokemon_NationalID_List = databasecursor.execute("select * from pokemon_nationalID WHERE pokemonNationalID=?", nationalPokemonIDtuple)
    return databasecursor.fetchall()

# Example of use:
# >>> function_pokemonID.returnPokemonUniqueIDFromName("Alakazam", c)
# [(2, 'Alakazam', 1.5, 48.0, 50, 45, 55, 135, 95, 120), (3, 'Alakazam', 1.2, 48.0, 50, 65, 55, 175, 95, 150)]
# Position 0 is the first non-mega form
# Position 0 within this is the uniqueID
def returnPokemonUniqueIDFromName(pokemonName, databasecursor):

    pokemonNametuple = (pokemonName,)
    pokemon_NationalID_List = databasecursor.execute("select * from pokemon_unique_info WHERE name=?", pokemonNametuple)
    return databasecursor.fetchall()

def returnPokemonNamefromUniqueID(uniqueID, databasecursor):

    pokemonIDtuple = (uniqueID,)
    pokemon_NationalID_List = databasecursor.execute("select * from pokemon_unique_info WHERE pokemonUniqueID=?", pokemonIDtuple)
    return databasecursor.fetchall()

# example of use:
# >>> function_pokemonID.returnPokemonNationalIDFromUniqueID(675, c)
# [(675, 9)]
# Position 0 of each tuple within is the uniqueID
# Pos 1 is the nationalID
def returnPokemonNationalIDFromUniqueID(uniqueID, databasecursor):

    pokemonIDtuple = (uniqueID,)
    pokemon_NationalID_List = databasecursor.execute("select * from pokemon_nationalID WHERE pokemonUniqueID=?", pokemonIDtuple)
    return databasecursor.fetchall()