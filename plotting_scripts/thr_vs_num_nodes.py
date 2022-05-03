import os
import matplotlib.pyplot as plt

dists = ['10', '100', '500', '1000', '2000', '3000']
datasets = {}

for n_nodes in dists:
    datasets[n_nodes] = []

    for file in os.listdir('datasets/n_' + n_nodes):
        if 'rx_data' in file:
            continue

        last_line = open('datasets/n_' + n_nodes + '/' + file).readlines()[-1]
        datasets[n_nodes].append(float(last_line.split(',')[1]))


# plt.hist(datasets.values(), density=True,
#          label=[l + ' nodes' for l in datasets.keys()]
#          )

for d in dists:
    k, v = zip(*datasets.items())
    plt.boxplot(v, notch=False, sym='', labels=k)

# plt.legend()
plt.tight_layout()
plt.title("Throughput distribution")
plt.xlabel("Num. of nodes")
plt.ylabel("Throughput (bps)")
plt.show()
