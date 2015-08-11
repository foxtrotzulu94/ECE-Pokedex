__author__ = 'Thinesh'

from bs4 import BeautifulSoup;
import sqlite3;
import json;
import urllib.request
import urllib.error
import re
# Needed to convert names with accents to normal
from unidecode import unidecode


# This file scrapes descriptions, as well as hatchTime catchRate and gender ratios
# TODO: Change file name to represent it better

conn = sqlite3.connect('..//database/pokedex.sqlite3')
c = conn.cursor();

c.execute("delete from " + "pokemon_common_info")

c.execute("delete from " + " sqlite_sequence where name = 'pokemon_common_info'")

soup = BeautifulSoup()

try:
    bulbapediaPokedexList = urllib.request.urlopen("http://bulbapedia.bulbagarden.net/wiki/List_of_Pok%C3%A9mon_by_National_Pok%C3%A9dex_number")


    inputData = bulbapediaPokedexList.read()
    soup = BeautifulSoup(inputData, "html.parser")

except urllib.error:
    print("URL NOT VALID")

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
# TODO: Looks like there's another case with moves linked, needs another regex
TAG_RE1 = re.compile(r"', <[^>]+>")
TAG_RE2 = re.compile(r"<[^>]+>, '")
TAG_RE3 = re.compile(r"<[^>]+>")

#Regex to extract all numbers from string, if nothing then, Genderless

TAG_FLOAT = re.compile(r"[0-9][^%]+")

# Reg to extract what's between parenthesis

TAG_PARENTHESIS = re.compile(r"\([^\)]+\)")

# Test pokemon list contains border cases for testing
testpokemonlinklist = ['/wiki/Slowpoke_(Pok%C3%A9mon)',
                       '/wiki/Slowbro_(Pok%C3%A9mon)',
                       '/wiki/Magnemite_(Pok%C3%A9mon)',
                       '/wiki/Mew_(Pok%C3%A9mon)',
                       '/wiki/Jynx_(Pok%C3%A9mon)',
                       '/wiki/Nidoking_(Pok%C3%A9mon)',
                       '/wiki/Eevee_(Pok%C3%A9mon)',
                       '/wiki/Weepinbell_(Pok%C3%A9mon)',
                       '/wiki/_(Pok%C3%A9mon)']

for index, pokemonURL in enumerate(pokemonlinklist):

    try:
        bulbapediaPokemonPage = urllib.request.urlopen(mainsiteURL+pokemonURL)
    except urllib.error:
        print("URL NOT VALID")

    inputData = bulbapediaPokemonPage.read()
    soup = BeautifulSoup(inputData, "html.parser")


    ##DESCRIPTION

    ## Original code seemed to break for gen 6 pokedex entries, so reduced the number of attrs to search for
    pokdexentry = soup.findAll(attrs={"style": "vertical-align: middle; border: 1px solid #9DC1B7; padding-left:3px;"})


    #Regex formatted pokedexentry
    pokdexentry = TAG_RE1.sub("", str(pokdexentry[0].contents))
    pokdexentry = TAG_RE2.sub("", pokdexentry)
    pokdexentry = TAG_RE3.sub("", pokdexentry)

    #TODO: Currently getting the description from the first gen it appeared in, can change pokedexentry[0] to len() of it all

    # Removing  [ ] and \n and quotations and spaces
    print(pokdexentry[3:-4])

    pokdexentry = pokdexentry[3:-4]


    # Can also obtain catchRate, hatchTime and genderRatios here

    ##CATCH RATES
    ## The % rate when the pokemon is at full health with a normal pokeball, seems like a great stat
    ## it's also easier to scrape
    catchrate = soup.findAll(attrs={"title": "When an ordinary Poké Ball is thrown at full health"})

    # This only gets the value in %
    catchrate = TAG_RE3.sub("", str(catchrate[0].contents))
    print(catchrate[2:-3])
    #remove % and ()
    catchrate = float(catchrate[2:-3])


    ##GENDER RATIOS
    genderratiotable = soup.find(attrs={"title": "List of Pokémon by gender ratio"})

    # We obtain an array of gender ratios
    # Genderless pokemon have 1 entry
    # Gendered pokemon have 3 entries (Unknown, male, female)
    # #unknown seems useless
    # So we can conclude to only take the last entry, and since we are storing male, to take 100-female
    # The only problems occur with 100% male pokemon, so need to make doublesure it's female we take
    # If genderless, then match will return None type, so we can store the default NaN
    genderratiotable = genderratiotable.parent.next_sibling.next_sibling.findAll("span")

    malegenderratioreal = "NaN"
    genderratiostring = ""
    #Default flag to assume the pokemon has female
    hasFemale = True

    if genderratiotable is not None and len(genderratiotable) > 0:
        genderratiostring = str(genderratiotable[len(genderratiotable)-1])
        print(genderratiostring)

    genderratiostring = TAG_RE3.sub("", genderratiostring)

    #Check if word female is not contained
    if "female" not in genderratiostring:
        hasFemale = False

    genderratiostring = TAG_FLOAT.match(genderratiostring)

    if genderratiostring is not None:
        if hasFemale:
            malegenderratioreal = 100-float(genderratiostring.group(0))
        else:
            malegenderratioreal = float(genderratiostring.group(0))

    print(malegenderratioreal)



    # The current index+1 will correspond to the national ID number
    # TODO: HatchTime, and eggroups can be done here too

    hatchTime = 0

    c.execute("INSERT INTO pokemon_common_info VALUES (?,?,?,?,?)", ((index+1), pokdexentry, hatchTime, catchrate, malegenderratioreal))
    conn.commit()







