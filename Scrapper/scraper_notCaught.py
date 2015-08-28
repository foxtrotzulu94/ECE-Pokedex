__author__ = "Nicole"

# This script should be used to fill the pokemon_caught table for the first time to indicate
# that the pokemon are not caught yet.

import sqlite3

conn = sqlite3.connect('..//database/pokedex.sqlite3')
c = conn.cursor();

c.execute("delete from " + "pokemon_caught")
c.execute("delete from " + " sqlite_sequence where name = 'pokemon_caught'")

for index in range(1, 722):
    c.execute("INSERT INTO pokemon_caught VALUES (?,?)", (index, 0))
    conn.commit()