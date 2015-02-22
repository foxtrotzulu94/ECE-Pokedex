__author__ = 'Nicole'

from sqlite3 import dbapi2 as sqlite
import simplejson

conn = sqlite.connect('pokedex.db')

cursor = conn.cursor()
cursor.execute("DROP TABLE IF EXISTS ability")
cursor.execute('create table ability (id integer PRIMARY KEY , created varchar, description varchar, modified varchar, name varchar)')

for i in range(1, 249):
    file = open('ability/'+str(i), "r")
    d = simplejson.load(file)

    cursor.execute('insert into ability values (?,?,?,?,?)', (d['id'], d['created'], d['description'], d['modified'], d['name']))

    conn.commit()