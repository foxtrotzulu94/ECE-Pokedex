__author__ = 'Thinesh'
# Run this after the pokemon_unique_info table is complete, and the pokemon_suffix table is partially compelete
# IN other words, run after scrapper_pokemon.py

# This file, goes through pokemon_unique_info, and finds all instance of a pokemon with a dash(-) in its name
# The pokemon returned are cross referenced by their uniqueID to the pokemon_nationalID table
# This is done in two steps, look up the uniqueID in pokemon_nationalID table. Then look up the nationalID returned
# IF more than one is returned then there MUST be multiple forms
# Next step, verify that the uniqueIDs found to be alternate forms are not already in the pokemon_suffix table
# Last step is to rename the pokemon in the pokemon_unique_info table to a name without a dash(-) to conform with how
# megas are named
#


import sqlite3
from Scrapper import function_pokemonID


conn = sqlite3.connect('../database/pokedex.sqlite3')
c = conn.cursor()

# Fetch all pokemon
# For each pokemon, the 0 pos contains the uniqueID, the 1 position the  name
pokemon_unique_list = c.execute("select * from pokemon_unique_info").fetchall()

# Also fetch the current suffix table list
current_pokemon_suffix_list = c.execute("select * from pokemon_suffix").fetchall()
print(current_pokemon_suffix_list)

# Build list of pokemon with dashes in their name that fit the profile mentioned above
dashpokemonlist = list()

for pokemon in pokemon_unique_list:
    #Step 1 check if name has dash
    if "-" in pokemon[1]:
        #Step 2 find nationalID of the corresponding uniqueID
        # Only 1 instance will be returned, to access get the 0 position of the list,
        # Nat ID is in the 1 pos
        nationalID = function_pokemonID.returnPokemonNationalIDFromUniqueID(pokemon[0], c)[0][1]

        #Step 3, use nationalID to find how many uniqueID's are returned
        uniqueIDlist = function_pokemonID.returnPokemonUniqueIDFromNationalID(nationalID, c)

        #Step 4, if the size of list is bigger than 1, and the uniqueID is not already in the list
        # than we can safely add to the dashpokemonlist
        if len(uniqueIDlist) > 1:
            pokemonAlreadyPresent = False
            for suffix in current_pokemon_suffix_list:
                if pokemon[0] == suffix[0]:
                    pokemonAlreadyPresent = True
            if not pokemonAlreadyPresent:
                dashpokemonlist.append(pokemon)

print(dashpokemonlist)

for pokemon in dashpokemonlist:
    #Last step add the pokemon to the suffix table, rename the pokemon in unique info table

    newname = pokemon[1].split("-")[0]
    suffixtostore = pokemon[1].split("-")[1]


    c.execute("INSERT INTO pokemon_suffix VALUES (?,?)", (pokemon[0], suffixtostore))
    conn.commit()
    c.execute("UPDATE pokemon_unique_info set name=? where pokemonUniqueID=?", (newname, pokemon[0]))
    conn.commit()
    print(newname)
    print(suffixtostore)

    # We will need an exceptional behaviour for Meowstic which has -M and -F in suffix table. But We want -M to be the
    # alternative, and -F to be present in the name

