__author__ = 'Nicole'

from sqlite3 import dbapi2 as sqlite
import simplejson

conn = sqlite.connect('pokedex.db')

cursor = conn.cursor()

cursor.execute('create table pokemon(pkdx_id integer PRIMARY KEY , ability1 integer, ability2 integer, ability3 integer, attack integer, catch_rate integer, created varchar, defense integer, description varchar, egg_cycles integer, egg_group1 integer, egg_group2 integer, ev_yield varchar, evolution_level integer, evolution_detail varchar, evolution_method varchar, evolution integer, exp integer, growth_rate varchar, happiness integer, height varchar, hp integer, male_female_ratio varchar, modified varchar, name varchar, national_id integer, sp_atk integer, sp_def integer, species varchar, speed integer, sprite integer,type1 integer, type2 integer, weight varchar)')

for i in range(1, 719):
    file = open('pokemon/'+str(i), "r")
    d = simplejson.load(file)

    if len(d['abilities']) > 1:
        abilities2 = d['abilities'][1]['resource_uri'].split('/')[4]
    else:
        abilities2 = -1

    if len(d['abilities']) > 2:
        abilities3 = d['abilities'][2]['resource_uri'].split('/')[4]
    else:
        abilities3 = -1

    if len(d['egg_groups']) > 1:
        eggGroup1 = d['egg_groups'][1]['resource_uri'].split('/')[4]
    else:
        eggGroup1 = -1

    if len(d['types']) > 1:
        type1 = d['types'][1]['resource_uri'].split('/')[4]
    else:
        type1 = -1

    if len(d['evolutions']) > 0:
        try:
            level = d['evolutions'][0]['level']
        except KeyError:
            level = -1
        try:
            detail = d['evolutions'][0]['detail']
        except KeyError:
            detail = ""
        method = d['evolutions'][0]['method']
        evolvedMonster = d['evolutions'][0]['resource_uri'].split('/')[4]
    else:
        level = -1
        detail = ""
        method = ""
        evolvedMonster = 0

    cursor.execute('insert into pokemon values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)', (d['pkdx_id'], d['abilities'][0]['resource_uri'].split('/')[4], abilities2, abilities3, d['attack'], d['catch_rate'], d['created'], d['defense'], d['descriptions'][0]['resource_uri'].split('/')[4], d['egg_cycles'], d['egg_groups'][0]['resource_uri'].split('/')[4], eggGroup1, d['ev_yield'], level, detail, method, evolvedMonster, d['exp'], d['growth_rate'], d['happiness'], d['height'], d['hp'], d['male_female_ratio'], d['modified'], d['name'], d['national_id'], d['sp_atk'], d['sp_def'], d['species'], d['speed'], d['sprites'][0]['resource_uri'].split('/')[4], d['types'][0]['resource_uri'].split('/')[4], type1, d['weight']))

    conn.commit()