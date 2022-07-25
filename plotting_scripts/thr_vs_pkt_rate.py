import os
import matplotlib.pyplot as plt

datasets = {}

for file in os.listdir('sim_res/'):
    _, rate, _, it, _ = file.split('.')
    rate = int(rate)
    if rate not in datasets:
        datasets[rate] = []

    for line in open('sim_res/' + file).readlines()[1:]:
        datasets[rate].append(float(line.split(',')[1]))

keys = sorted(datasets.keys())
plt.boxplot([datasets[k] for k in keys], notch=False, sym='', labels=keys)

plt.tight_layout()
plt.title("Throughput vs. TX rate")
plt.xlabel("Pkt/h")
plt.ylabel("Throughput (bps)")
plt.show()
