__author__ = 'Nicole'

from sqlite3 import dbapi2 as sqlite
import simplejson

conn = sqlite.connect('pokedex.db')

cursor = conn.cursor()
cursor.execute("DROP TABLE IF EXISTS type")
cursor.execute('create table type (id integer PRIMARY KEY , created varchar, ineffective1 integer, ineffective2 integer, ineffective3 integer, modified varchar, name varchar, no_effect1 integer, super_effective1 integer, super_effective2 integer, super_effective3 integer, weakness1 integer, weakness2 integer, weakness3 integer)')

for i in range(1, 19):
    file = open('type/'+str(i), "r")
    d = simplejson.load(file)

    if len(d['ineffective']) > 0:
        ineffective1 = d['ineffective'][0]['resource_uri'].split('/')[4]
    else:
        ineffective1 = -1

    if len(d['ineffective']) > 1:
        ineffective2 = d['ineffective'][1]['resource_uri'].split('/')[4]
    else:
        ineffective2 = -1

    if len(d['ineffective']) > 2:
        ineffective3 = d['ineffective'][2]['resource_uri'].split('/')[4]
    else:
        ineffective3 = -1

    if len(d['no_effect']) > 0:
        no_effect1 = d['no_effect'][0]['resource_uri'].split('/')[4]
    else:
        no_effect1 = -1

    if len(d['super_effective']) > 0:
        super_effective1 = d['super_effective'][0]['resource_uri'].split('/')[4]
    else:
        super_effective1 = -1

    if len(d['super_effective']) > 1:
        super_effective2 = d['super_effective'][1]['resource_uri'].split('/')[4]
    else:
        super_effective2 = -1

    if len(d['super_effective']) > 2:
        super_effective3 = d['super_effective'][2]['resource_uri'].split('/')[4]
    else:
        super_effective3 = -1

    if len(d['weakness']) > 0:
        weakness1 = d['weakness'][0]['resource_uri'].split('/')[4]
    else:
        weakness1 = -1

    if len(d['weakness']) > 1:
        weakness2 = d['weakness'][1]['resource_uri'].split('/')[4]
    else:
        weakness2 = -1

    if len(d['weakness']) > 2:
        weakness3 = d['weakness'][2]['resource_uri'].split('/')[4]
    else:
        weakness3 = -1

    cursor.execute('insert into type values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)', (d['id'], d['created'], ineffective1, ineffective2, ineffective3, d['modified'], d['name'], no_effect1, super_effective1, super_effective2, super_effective3, weakness1, weakness2, weakness3))
    conn.commit()