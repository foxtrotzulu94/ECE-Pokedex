__author__ = 'Thinesh'

from bs4 import BeautifulSoup;
import sqlite3;
import json;
import urllib.request
import re


# This file scrapes descriptions, as well as hatchTime catchRate and gender ratios
# TODO: Change file name to represent it better

conn = sqlite3.connect('..//database/pokedex.sqlite3')
c = conn.cursor();

#c.execute("delete from " + "pokemon_description")

#c.execute("delete from " + " sqlite_sequence where name = 'pokemon_description'")

c.execute("delete from " + "pokemon_hatchTime")

c.execute("delete from " + " sqlite_sequence where name = 'pokemon_hatchTime'")

c.execute("delete from " + "pokemon_genderRatioMale")

c.execute("delete from " + " sqlite_sequence where name = 'pokemon_genderRatioMale'")

c.execute("delete from " + "pokemon_catchRate")

c.execute("delete from " + " sqlite_sequence where name = 'pokemon_catchRate'")

bulbapediaPokedexList = urllib.request.urlopen("http://bulbapedia.bulbagarden.net/wiki/List_of_Pok%C3%A9mon_by_National_Pok%C3%A9dex_number")

inputData = bulbapediaPokedexList.read()
soup = BeautifulSoup(inputData, "html.parser")

# We look for this particular style (there is 6, one for each gen, this table contains the pokemonx2, but also types, we just want links
stylegen = list()

#Gen 1 style
stylegen.append("border-radius: 10px; -moz-border-radius: 10px; -webkit-border-radius: 10px; -khtml-border-radius: 10px; -icab-" \
        "border-radius: 10px; -o-border-radius: 10px;; border: 2px solid #FF1111; background: #FF1111;")

#Gen 2 style
stylegen.append("border-radius: 10px; -moz-border-radius: 10px; -webkit-border-radius: 10px; -khtml-border-radius: 10px; -icab-"
                "border-radius: 10px; -o-border-radius: 10px;; border: 2px solid #DAA520; background: #DAA520;")

#Gen 3 style
stylegen.append("border-radius: 10px; -moz-border-radius: 10px; -webkit-border-radius: 10px; -khtml-border-radius: 10px; -icab-border-radius: 10px; -o-border-radius: 10px;; border: 2px solid #A00000; background: #A00000;")

#Gen 4
stylegen.append("border-radius: 10px; -moz-border-radius: 10px; -webkit-border-radius: 10px; -khtml-border-radius: 10px; -icab-border-radius: 10px; -o-border-radius: 10px;; border: 2px solid #AAAAFF; background: #AAAAFF;")

#Gen 5
stylegen.append("border-radius: 10px; -moz-border-radius: 10px; -webkit-border-radius: 10px; -khtml-border-radius: 10px; -icab-border-radius: 10px; -o-border-radius: 10px;; border: 2px solid #444444; background: #444444;")

#Gen 6
stylegen.append("border-radius: 10px; -moz-border-radius: 10px; -webkit-border-radius: 10px; -khtml-border-radius: 10px; -icab-border-radius: 10px; -o-border-radius: 10px;; border: 2px solid #025DA6; background: #025DA6;")



# empty list to contain all the generation tables
generationpokemontables = list()

for style in stylegen:
    generationpokemontables.append(soup.findAll('table', attrs={"style": style}))


# final list which will contain all links to pokemon
pokemonlinklist = list()


for generation in generationpokemontables:
    for everylink in generation[0].findAll("a"):
            #print(everypokemon)
            # everylink contains each pokemon twice, as well as their types, so filter for types
            # plus the first link is to the generation
            if "(type)" not in str(everylink) and "List_of" not in str(everylink):
                href = everylink.get('href')
                # add it to your pokemonlinklist, if and only if the previous entry is not same
                # but if list is empty, go ahead and add without check
                if len(pokemonlinklist) == 0:
                    pokemonlinklist.append(href)
                    print(href)

                elif pokemonlinklist[-1] != href:
                    pokemonlinklist.append(href)
                    print(href)

# Now we can start scraping the descriptions page by page

mainsiteURL = "http://bulbapedia.bulbagarden.net"

# Also need to be able to remove the random links sometimes found in descriptions, using regex
# This TAG.RE can now remove everything contained within "< >"
# There's also a weird "' ," that surrounds these links so need 2 regex to remove, maybe can be combined not sure
# And just for fun, sometimes it doesn't have these, so must run through all 3, in this order.
TAG_RE1 = re.compile(r"', <[^>]+>")
TAG_RE2 = re.compile(r"<[^>]+>, '")
TAG_RE3 = re.compile(r"<[^>]+>")

# Reg to extract what's between parenthesis

TAG_PARENTHESIS = re.compile(r"\([^\)]+\)")

testpokemonlinklist = ['/wiki/Slowpoke_(Pok%C3%A9mon)',
'/wiki/Slowbro_(Pok%C3%A9mon)',
'/wiki/Magnemite_(Pok%C3%A9mon)',
'/wiki/Magneton_(Pok%C3%A9mon)',
'/wiki/Farfetch%27d_(Pok%C3%A9mon)']

for index, pokemonURL in enumerate(testpokemonlinklist):

    bulbapediaPokemonPage = urllib.request.urlopen(mainsiteURL+pokemonURL)
    inputData = bulbapediaPokemonPage.read()
    soup = BeautifulSoup(inputData, "html.parser")

    ## TODO: Original code seemed to break for gen 6 pokedex entries, so reduced the number of attrs to search for
    pokdexentry = soup.findAll(attrs={"style" :"vertical-align: middle; border: 1px solid #9DC1B7; padding-left:3px;"})


    #Regex formatted pokedexentry
    pokdexentry = TAG_RE1.sub("", str(pokdexentry[0].contents))
    pokdexentry = TAG_RE2.sub("", pokdexentry)
    pokdexentry = TAG_RE3.sub("", pokdexentry)

    #TODO: Maybe we'll want to remove the [] and quotes? will be easy enough to do by taking pokedexentry[2:-2] if new line char needs to go as well [3:-4]
    #TODO: Currently getting the description from the first gen it appeared in, can change pokedexentry[0] to len() of it all
    print(pokdexentry[3:-4])

    pokdexentry = pokdexentry[3:-4]

    # Can also obtain catchRate, hatchTime and genderRatios here

    ##CATCH RATES
    ## The % rate when the pokemon is at full health with a normal pokeball, seems like a great stat
    ## it's also easier to scrape
    catchrate = soup.findAll(attrs={"title": "When an ordinary Poké Ball is thrown at full health"})


    print(catchrate)
    catchrate = TAG_RE3.sub("", str(catchrate[0].contents))
    print(catchrate[2:-3])





    # # The current index+1 will correspond to the national ID number
    #
    # c.execute("INSERT INTO pokemon_description VALUES (?,?)", ((index+1), pokdexentry))
    # conn.commit()



