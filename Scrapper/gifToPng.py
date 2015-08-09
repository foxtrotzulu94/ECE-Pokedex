__author__ = 'as'

# from __future__ import print_function
from PIL import Image
from PIL import ImageFileIO
import os
import fnmatch

#
# im = Image.open('Fighter-Front.gif')
# transparency = im.info['transparency']
# im.save('test1.png', transparency=transparency)
#
# im.seek(im.tell()+1)
# transparency = im.info['transparency']
# im.save('test2.png', transparency=transparency)

# Currently 719 pokemon gifs in assets, along with mega and alternate forms
NUMBER_OF_POKEMON = 719

#
# im = Image.open('001.gif')
# im.show()
# print( im.format, im.size, im.mode)

# def extractFrames(inGif, outFolder):
rawGIF = Image.open('001.gif')
    # rawGIF.seek() redundant
#
# rawGIF.save('testy.png','PNG',)

rawGIF_array = []

for file in os.listdir('.'):
    if fnmatch.fnmatch(file, '*.gif'):
        # print(file)
        rawGIF_array.append(file)
# print(rawGIF_array)

for gif in rawGIF_array:
    RAWGIF = Image.open(gif)
    savename = gif.split('.')
    RAWGIF.save(savename[0]+'.png', 'PNG', )
    print(RAWGIF)
    # print
    # Steps:
    # 1) Open image
    # 2) Freeze at first frame
    # 3) Store frozen frame
    # 4) Save image in directory
#     search for gifs, store into list, convert each element in list
# FNMATCH



# from __future__ import print_function
# import os, sys
# from PIL import Image
#
# for infile in sys.argv[1:]:
#     f, e = os.path.splitext(infile)
#     outfile = f + ".jpg"
#     if infile != outfile:
#         try:
#             Image.open(infile).save(outfile)
#         except IOError:
#             print("cannot convert", infile)
# extractFrames('ban_ccccccccccc.gif', 'output')

# while i < NUMBER_OF_POKEMON:
#
#     # if condition needed to check whether pokemon has mega/alternate form
#     im = Image.open('..//sprites/pokemon/gen5/animated/'+i+'.gif')