__author__ = 'Nicole'

from urllib import request
import simplejson

baseUrl = "http://pokeapi.co/api/v1/"
rootdir = 'C:/Users/Nicole/PycharmProjects/mchacksPokedex'

#classUrl = 'pokemon/'
#total = 719
#for i in range(1, total):
#    thisUrl = classUrl+str(i)
#    url = baseUrl+thisUrl
#    response = request.urlopen(url)
#    data = simplejson.loads(response.read())
#    print(data)
#    outfile = open(thisUrl, 'w')
#    simplejson.dump(data, outfile)
#    outfile.close()

#classUrl = 'ability/'
#total = 249
#for i in range(1, total):
#    thisUrl = classUrl+str(i)
#    url = baseUrl+thisUrl
#    response = request.urlopen(url)
#    data = simplejson.loads(response.read())
#    print(data)
#    outfile = open(thisUrl, 'w')
#    simplejson.dump(data, outfile)
#    outfile.close()

#classUrl = 'description/'
#total = 6611
#for i in range(4463, total):
#    thisUrl = classUrl+str(i)
#    url = baseUrl+thisUrl
#    response = request.urlopen(url)
#    data = simplejson.loads(response.read())
#    print(data)
#    outfile = open(thisUrl, 'w')
#   simplejson.dump(data, outfile)
#   outfile.close()

#classUrl = 'egg/'
#total = 16
#for i in range(1, total):
#    thisUrl = classUrl+str(i)
#    url = baseUrl+thisUrl
#    response = request.urlopen(url)
#    data = simplejson.loads(response.read())
#    print(data)
#    outfile = open(thisUrl, 'w')
#    simplejson.dump(data, outfile)
#    outfile.close()

#classUrl = 'move/'
#total = 626
#for i in range(1, total):
#    thisUrl = classUrl+str(i)
#    url = baseUrl+thisUrl
#    response = request.urlopen(url)
#    data = simplejson.loads(response.read())
#    print(data)
#    outfile = open(thisUrl, 'w')
#    simplejson.dump(data, outfile)
#    outfile.close()

#classUrl = 'sprite/'
#total = 720
#for i in range(21, total):
#    thisUrl = classUrl+str(i)
#    url = baseUrl+thisUrl
#    response = request.urlopen(url)
#    data = simplejson.loads(response.read())
#   print(data)
#    outfile = open(thisUrl, 'w')
#    simplejson.dump(data, outfile)
#    outfile.close()

#classUrl = 'type/'
#total = 19
#for i in range(1, total):
#    thisUrl = classUrl+str(i)
#    url = baseUrl+thisUrl
#    response = request.urlopen(url)
#    data = simplejson.loads(response.read())
#    print(data)
#    outfile = open(thisUrl, 'w')
#    simplejson.dump(data, outfile)
#    outfile.close()

classUrl = 'game/'
total = 26
for i in range(1, total):
    thisUrl = classUrl+str(i)
    url = baseUrl+thisUrl
    response = request.urlopen(url)
    data = simplejson.loads(response.read())
    print(data)
    outfile = open(thisUrl, 'w')
    simplejson.dump(data, outfile)
    outfile.close()