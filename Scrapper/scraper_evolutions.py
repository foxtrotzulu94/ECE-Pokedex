__author__ = 'Thinesh'

# Run this after the pokemon_unique_info table is complete, and the pokemon_suffix table is compelete
# IN other words, run after scrapper_pokemon.py AND scraper_SuffixFixer.py

# This file uses both the info contained in the JSON file with evolutions
# and information from serebii to fill out evo conditions
# In the JSON file mega's aren't considered evos, however alternate forms are
# This behaviour should be the opposite of what we want.
# First Step:
# We can run through the json file, making the appropriate connections
# We ignore alternative forms
# If the suffix in the json file contains mega, we look which nationalID it is mapped to, and link it as an evo
# to the original form
# Second step:
# Go through serebii and fill out evolution conditions.
# Because of the way serebii is organized, a 3rd level evolution wll contain information on its first levels
# So logic will have to be implemented based on the url containing nationalID


from bs4 import BeautifulSoup
import sqlite3
import json
import urllib.request
import urllib.error
import re
from Scrapper import function_pokemonID


# Open the json file
with open("pokemon.json") as data_file:
    data = json.load(data_file)

# Connect to database
conn = sqlite3.connect('..//database/pokedex.sqlite3')
c = conn.cursor()

c.execute("delete from " + "pokemon_evolutions")
c.execute("delete from " + " sqlite_sequence where name = 'pokemon_evolutions'")

uniqueID = 0

serebiiURLstart = "http://serebii.net/pokedex-xy/"
serebiiURLend = ".shtml"

for pokemons in data["pokemon"]:

    # Strictly to keep track of the correct uniqueID
    for pokemonForm in pokemons["alts"]:
        uniqueID += 1

        suffix = str.lower(pokemonForm["suffix"])

        # if this form is not a suffix defined by json file
        # simply look at regular evo
        if "" == suffix:
            evolutionlist = pokemons["evos"]

            # Need to make sure these are not alternatives
            for evocount, evo in enumerate(evolutionlist):
                method = ""
                evolutionIDlist = function_pokemonID.returnPokemonUniqueIDFromName(evo, c)

                # If evolutionIDlist is an empty set, then since the name doesn't correspond to what the database has, must
                # be an alt form and we can ignore
                # If it returns something, then the first position in the list is the pokemon
                # first position there again is the id
                if len(evolutionIDlist) > 0:

                    # Use this to obtain evolution information from serebii
                    # Returns list, should be only 1 though, position 1 of position 0 corresponds to nationalID
                    nationalID = function_pokemonID.returnPokemonNationalIDFromUniqueID(uniqueID, c)[0][1]
                    # Format to 3 digit form
                    nationalID = "%03d" % nationalID

                    # Create URL and access
                    serebiiURL = serebiiURLstart+nationalID+serebiiURLend
                    serebiiPokedex = urllib.request.urlopen(serebiiURL)

                    inputData = serebiiPokedex.read()
                    soup = BeautifulSoup(inputData, "html.parser")

                    # Find main table that contains evolution information
                    maintable = soup.find( attrs={"class": "evochain"})

                    # Construct the image url,
                    pokemonimageURL = "/xy/pokemon/"+nationalID+".png"

                    # Use imageURL to find the appropriate box, next to it contains evo info

                    # If singular chain, then it's simple
                    if len(evolutionlist) == 1:
                        evoinfo = maintable.find(src = pokemonimageURL).findNext('td').contents[0]


                        # Sometimes, when a stone or item is used, we need to go one element deeper
                        if 'href' not in evoinfo.attrs:
                            method = evoinfo.get('title')
                        else:
                            evoinfo = evoinfo.next_element
                            method = evoinfo.get('title')

                    # If branch evolution i.e: gloom, wurmple  who are organized horizontally
                    # need to change how it's found
                    elif len(evolutionlist) == 2:
                        # Logic is to work backwards, find the image of the evo and go back
                        uniqueIDtoSearch = function_pokemonID.returnPokemonUniqueIDFromName(evo, c)[0][0]
                        natIDtoSearch = function_pokemonID.returnPokemonNationalIDFromUniqueID(uniqueIDtoSearch, c)[0][1]
                        natIDtoSearch = "%03d" % natIDtoSearch

                        # Construct the href url,
                        pokemonhrefURL = "/pokedex-xy/"+natIDtoSearch+".shtml"
                        evoinfo = maintable.find(href=pokemonhrefURL)
                        evoinfo = evoinfo.find_previous('td').find_previous('td').contents[0]

                        # Sometimes, when a stone or item is used, we need to go one element deeper
                        if 'href' not in evoinfo.attrs:
                            method = evoinfo.get('title')
                        else:
                            evoinfo = evoinfo.next_element
                            method = evoinfo.get('title')

                    # So far the case for eevee and tyrogue, with vertically stacked evos on page
                    # In this case the evolution method is directly above the pokemon
                    # So far only the case of one pokemon with >2 branches, won't work with more complicated
                    elif len(evolutionlist) > 2:

                        # Still neeed to find out what we are looking for:
                        uniqueIDtoSearch = function_pokemonID.returnPokemonUniqueIDFromName(evo, c)[0][0]
                        natIDtoSearch = function_pokemonID.returnPokemonNationalIDFromUniqueID(uniqueIDtoSearch, c)[0][1]
                        natIDtoSearch = "%03d" % natIDtoSearch

                        # create an array of all the images
                        allimgs = maintable.find_all('img')

                        # array of all evo methods
                        evomethods = list()
                        # array of all pokemon by natID
                        natidarray = list()
                        for img in allimgs:
                            if 'title' in img.attrs:
                                evomethods.append(img.get('title'))
                            else:
                                natidarray.append(img.get('src')[-7:-4])

                        # Get the index - 1 (Since the first position is taken by the first evolution)
                        indexpos = natidarray.index(natIDtoSearch)
                        method = evomethods[indexpos-1]




            # If title text matches "Level " including space, for the last 6 charactders
                    #  construct string with level number
                    if "Level " == method[-6:]:
                        # obtain what level, which is always characters -6 to -4
                        level = evoinfo.get('src')[-6:-4]
                        method = method + level

                    # Special case where pokemon needs to be traded for a specific pokemon
                    if "Trade with " == method:
                        # obtain which pokemon, which is always characters -7 to -4 nationalID
                        specialpokemonNatID = evoinfo.get('src')[-7:-4]
                        uniqueIDtoSearch = function_pokemonID.returnPokemonUniqueIDFromNationalID(int(specialpokemonNatID), c)[0][0]
                        # use that to find the name
                        specialname = function_pokemonID.returnPokemonNamefromUniqueID(uniqueIDtoSearch, c)[0][1]
                        method = method + specialname







                    print(pokemons["name"]+"-->"+evo+" ("+method+")")
                    c.execute("INSERT into pokemon_evolutions values (?,?,?)", (uniqueID, evolutionIDlist[0][0], method))
                    conn.commit()





        # If the altform is a mega or primal, then need to link to its regular form
        elif "mega" in suffix or "primal" in suffix:
            uniqueIDToLinkTo = function_pokemonID.returnPokemonUniqueIDFromName(pokemons["name"], c)[0][0]
            print(pokemons["name"]+"-->"+pokemons["name"]+"-"+suffix)
            c.execute("INSERT into pokemon_evolutions values (?,?,?)", (uniqueIDToLinkTo, uniqueID, "MegaStone/Primal"))
            conn.commit()




