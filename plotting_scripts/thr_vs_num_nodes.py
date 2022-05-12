import os
import matplotlib.pyplot as plt

datasets = {}

for file in os.listdir('sim_res/'):
    _, n, _, it, _ = file.split('.')
    n = int(n)
    if n not in datasets:
        datasets[n] = []

    for line in open('sim_res/' + file).readlines()[1:]:
        datasets[n].append(float(line.split(',')[1]))

keys = sorted(datasets.keys())
plt.boxplot([datasets[k] for k in keys], notch=False, sym='', labels=keys)

# plt.legend()
plt.tight_layout()
plt.title("Throughput distribution")
plt.xlabel("Num. of nodes")
plt.ylabel("Throughput (bps)")
plt.show()
