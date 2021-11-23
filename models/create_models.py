import os
import subprocess as sub

BASE = "../data/Dati-Jan/"

# Debugging list
#files = ["500-0-0.csv", "500-0-20.csv", "500-0-60.csv", "500-500-20.csv", "500-500-60.csv", "500-1000-20.csv", "500-1000-60.csv", "1000-0-0.csv", "1000-0-20.csv", "1000-0-60.csv", "1000-500-20.csv", "1000-500-60.csv", "1000-1000-20.csv", "1000-1000-60.csv", "1500-0-0.csv"]
files = os.listdir(BASE)
#print(files)
files.remove('README.txt')
#print(files)
print(len(files))

args = ['python', 'markov_csv.py', '']

for f in files:
    args[2] = (BASE + f)
    sub.run(args)
