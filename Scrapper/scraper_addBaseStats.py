__author__ = 'Thinesh'
# Run this after the pokemon_unique_info table is complete, and the pokemon_suffix table is partially compelete
# IN other words, run after scrapper_pokemon.py

# Gets total base stats and sums them up to add them up to add

import sqlite3;

conn = sqlite3.connect('..//database/pokedex.sqlite3')
c = conn.cursor()

# Fetch all pokemon
# For each pokemon, the 0 pos contains the uniqueID, the 1 position the  name
pokemon_unique_list = c.execute("select * from pokemon_unique_info").fetchall()

for uniqueID, pokemon in enumerate(pokemon_unique_list):

    totalbasestats = pokemon[4]+pokemon[5]+pokemon[6]+pokemon[7]+pokemon[8]+pokemon[9]

    c.execute("UPDATE pokemon_unique_info set basestat=? where pokemonUniqueID=?", (totalbasestats, uniqueID+1))

conn.commit()
conn.close()