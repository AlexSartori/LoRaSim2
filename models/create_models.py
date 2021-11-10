import subprocess as sub

base = "../data/Dati-Jan/"

files = ["500-0-0.csv", "500-0-20.csv", "500-0-60.csv", "500-500-20.csv", "500-500-60.csv", "500-1000-20.csv", "500-1000-60.csv", ]
args = ['python', 'markov_csv.py', '']

for f in files:
    args[2] = (base + f)
    sub.run(args)
