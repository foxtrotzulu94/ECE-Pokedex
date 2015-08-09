__author__ = "Nicole"

# Script to move the database from where it is here, to where it needs to be in app
#
# IMPORTANT NOTE: This script will only work if the directory is made!

import os
import shutil

here = "../database/pokedex.sqlite3"
there = "../app/src/main/assets/pokedex.db"

# first remove the old database
os.remove(there)

# then copy the updated database there
shutil.copyfile(here, there)