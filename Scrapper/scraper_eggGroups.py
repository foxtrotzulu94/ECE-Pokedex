__author__ = 'Thinesh'

# Run this after the pokemon_unique_info table is complete, and the pokemon_suffix table is compelete
# pokemon_common_info table also needs to be complete
# IN other words, run after scrapper_pokemon.py AND scraper_SuffixFixer.py AND scrapper_description.py

# Script uses Bulbapedia's Breeding group information: i.e: http://bulbapedia.bulbagarden.net/wiki/List_of_Pok%C3%A9mon_by_Egg_Group
# Both eggGroups and pokemon_eggGroups tables are filled by this script

# Changes from the bulbapedia organization, is that if a pokemon is in a group and has no gender,
# Then instead of being associated with its actual group, it is moved to the Ditto Group since that's all it can breed
# with successfully



from bs4 import BeautifulSoup
import sqlite3
import urllib.request
import urllib.error
from urllib.request import urlretrieve
import os
import re
from Scrapper import function_pokemonID


# Connect to database
conn = sqlite3.connect('..//database/pokedex.sqlite3')
c = conn.cursor()

c.execute("delete from " + "eggGroups")
c.execute("delete from " + " sqlite_sequence where name = 'eggGroups'")

c.execute("delete from " + "pokemon_eggGroups")
c.execute("delete from " + " sqlite_sequence where name = 'pokemon_eggGroups'")

pokemonByEggGroupURL = "http://bulbapedia.bulbagarden.net/wiki/Egg_cycles"
eggGroupURL = "http://bulbapedia.bulbagarden.net/wiki/Egg_Group"


eggGroupPage = urllib.request.urlopen(eggGroupURL)
pokemonByEggGroupPage = urllib.request.urlopen(pokemonByEggGroupURL)

inputDataGroups = eggGroupPage.read()
soupGroup = BeautifulSoup(inputDataGroups, "html.parser")

inputDataPokemon = pokemonByEggGroupPage.read()

# Cache this page as it might be removed in future, if it does not exist
if not os.path.isfile('pokemonbyegggroups.txt'):
        urlretrieve(pokemonByEggGroupURL, 'pokemonbyegggroups.txt')

soupPokemon = BeautifulSoup(inputDataPokemon, "html.parser")

# Get all egg groups first
eggGroupList = soupGroup.find(attrs={"class": "mw-headline", "id": "Egg_Groups"}).findNext('ol')
groupslist = list()

# Split at the ":" colon since after is description
# the index of the group in the array corresponds to the id number of the group
while ":" in eggGroupList.findNext('li').get_text():
    groupNameandDesc  = eggGroupList.findNext('li').get_text()
    # Get rid of trailing space and the word Group
    groupName = groupNameandDesc.split(':')[0][1:-6]
    groupslist.append(groupName)
    print(groupName)
    eggGroupList = eggGroupList.findNext('li')

for index, group in enumerate(groupslist):
    c.execute("insert INTO eggGroups VALUES (?,?) ", (index, group))
conn.commit()

pokemonByEggGroup = soupPokemon.find('table', attrs={"style": "margin: auto; border: 3px solid #44685E; background: #68A090;"})

# The table is organized in 5 td cells for each pokemon
# First corresponds to nationalID
# Second to image of pokemon
# Third to pokemon name
# 4th to First egg group name
# fifth to second egg group
# 6th egg cycles
# 7th steps gen 2
# 8th steps gen 3
# 9th steps gen 4
# 10th steps gen 5



TOTAL_POKEMON = 721
for x in range(0, TOTAL_POKEMON):

    # text needs to be stripped of new line chars
    # PokemonName has a trailing space in the front needs to be removed
    pokemonName = pokemonByEggGroup.findNext('td').findNext('td').findNext('td').get_text().rstrip()[1:]
    pokemonNatID = int(pokemonByEggGroup.findNext('td').get_text().rstrip())

    # First and second egg group has a trailing space in the front needs to be removed
    firstEggGroup = pokemonByEggGroup.findNext('td').findNext('td').findNext('td').findNext('td').get_text().rstrip()[1:]
    secondEggGroup = pokemonByEggGroup.findNext('td').findNext('td').findNext('td').findNext('td').findNext('td').get_text().rstrip()[1:]

    gen5eggsteps = pokemonByEggGroup.findNext('td').findNext('td').findNext('td').findNext('td').findNext('td').findNext('td').findNext('td').findNext('td').findNext('td').findNext('td').get_text().rstrip()[1:]
    pokemonByEggGroup = pokemonByEggGroup.findNext('td').findNext('td').findNext('td').findNext('td').findNext('td').findNext('td').findNext('td').findNext('td').findNext('td').findNext('td')

    # Before storing in DB, use the nationalID to find if the pokemon is genderless AND not undiscovered
    #  if it is ignore all egg groups and add to ditto group

    # pos 4 correspends to gender ratio, check if NaN
    genderRatio = function_pokemonID.returnCommonInfoFromNationalID(pokemonNatID, c)[0][4]

    if genderRatio == 'NaN' and firstEggGroup != "Undiscovered":
        groupID = groupslist.index("Ditto")
        c.execute("INSERT INTO pokemon_eggGroups VALUES (?,?)", (pokemonNatID, groupID))
        print(str(pokemonNatID)+"-"+pokemonName+"- Ditto")
    else:
        groupID = groupslist.index(firstEggGroup)
        c.execute("INSERT INTO pokemon_eggGroups VALUES (?,?)", (pokemonNatID, groupID))
        print(str(pokemonNatID)+"-"+pokemonName+"-"+firstEggGroup)
        if secondEggGroup != "—":
            groupID = groupslist.index(secondEggGroup)
            c.execute("INSERT INTO pokemon_eggGroups VALUES (?,?)", (pokemonNatID, groupID))
            print(str(pokemonNatID)+"-"+pokemonName+"-"+secondEggGroup)
    conn.commit()

    c.execute("UPDATE pokemon_common_info set hatchTime=? WHERE pokemonNationalID = (?)", (gen5eggsteps, pokemonNatID))
    conn.commit()
    print(gen5eggsteps)

conn.close()
