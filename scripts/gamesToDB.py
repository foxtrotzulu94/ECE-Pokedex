__author__ = 'Nicole'

from sqlite3 import dbapi2 as sqlite
import simplejson

conn = sqlite.connect('pokedex.db')

cursor = conn.cursor()
cursor.execute("DROP TABLE IF EXISTS game")
cursor.execute('create table game (id integer PRIMARY KEY , created varchar, modified varchar, name varchar, release_year integer, generation integer)')

for i in range(1, 26):
    file = open('game/'+str(i), "r")
    d = simplejson.load(file)

    cursor.execute('insert into game values (?,?,?,?,?,?)', (d['id'], d['created'], d['modified'], d['name'], d['release_year'], d['generation']))
    conn.commit()