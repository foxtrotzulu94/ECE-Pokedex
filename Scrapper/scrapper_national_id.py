__author__ = 'as'

from bs4 import BeautifulSoup
import json
import sqlite3
import urllib.request
import urllib.parse
import urllib.error
# Needed to convert names with accents to normal
from unidecode import unidecode


# Learn how to scrape from website!

conn = sqlite3.connect('..//database/pokedex.sqlite3')
c = conn.cursor();

c.execute("delete from " + "pokemon_nationalID")

c.execute("delete from " + " sqlite_sequence where name = 'pokemon_nationalID'")

conn.commit()

# Before we go on, we extract the completed pokemon table ordered by pokemonID, store in list,
# the index plus 1 corresponds to the nationalID

# this is a tuple, we want a simple list
pokemonlist = c.execute("select name from pokemon ORDER BY pokemonID")
simplepokemonlist = []

# convert to simple list
for row in pokemonlist:
    simplepokemonlist.append(row[0])




# Constant needs to be updated whenever serebii adds more
CURRENT_NUMBER_OF_POKEMON_IN_SEREBII = 721

# Current list of exception pokemon (who have names that are subsets of others"

EXCEPTION_POKEMON = ["Pidgeot", "Paras", "Porygon", "Kabuto", "Mew", "Klink"]

serebiiPokedex = urllib.request.urlopen("http://www.serebii.net/pokedex-xy/")
# print(serebiiPokedex.read())

inputData = serebiiPokedex.read()
soup = BeautifulSoup(inputData, "html.parser")

# form now contains all instances of form, and it's children. But not needed, we can use the find(value=)
# to find the first instance of all the "/pokedex-xy/"+nationalID+".shtml" tags
# form = soup.find_all('form')


# cycle through all the numbers from 1 to 721, constructing the string for the nationalID HTML
i = 1

while i <= CURRENT_NUMBER_OF_POKEMON_IN_SEREBII:
    # formats 1 to 001 etc
    nationalID = "%03d" % (i)

    formline = soup.find(value="/pokedex-xy/"+nationalID+".shtml").get_text()
    # make sure it gets rid of accents
    formline = unidecode(formline)
    i += 1

    # Split the formline into its number and name (default separator is space)
    # Exception, watch out for pokemon with spaces in name. So need to combine everything after number

    pokemonIDCouple = formline.split()
    pokemonIDCouple[1:] = [' '.join(pokemonIDCouple[1:])]

    # pokemonIDCouple[0] has the number
    # pokemonIDCouple[1] has the name

    print(pokemonIDCouple)


    # find all instances of the name in simplepokemonlist in the pokemon table, returns indices of this pokemon that
    # shares nationalID
    # Exceptions: Klink,  Mew, Porygon, Kabuto, Paras and Pidgeot which see others as well, this was put in a list at the begining
    indices = [i for i, x in enumerate(simplepokemonlist) if pokemonIDCouple[1] in x]

    # check for mentioned exceptions
    if len(indices) > 1:
        if pokemonIDCouple[1] in EXCEPTION_POKEMON:
            # check for exact match only
            indices = [i for i, x in enumerate(simplepokemonlist) if pokemonIDCouple[1] == x]

    print(indices)
    # go through the indices which correspond to a pokemon id, and map to nationalID found in pokemonIDCouple[0]
    # nationalID = pokemonIDCouple[0]
    # pokemonID = incrementedpokeID
    for pokemonID in indices:
        #need to increment
        incrementedpokeID = pokemonID + 1
        c.execute("INSERT INTO pokemon_nationalID VALUES (?,?)", (incrementedpokeID, pokemonIDCouple[0]))

conn.commit()





