import os
import matplotlib.pyplot as plt

datasets = {}
dr_c = {
    0: 'red',
    1: 'blue',
    2: 'green',
    3: 'black',
    4: 'pink',
    5: 'orange',
    6: 'cyan'
}

n_dr = {}
for line in open("sim_res/topology.csv").readlines()[1:]:
    n_id, type, dr, x, y = line.strip().split(',')
    n_dr[int(n_id)] = int(dr)

for file in os.listdir('sim_res/'):
    if file in ['topology.csv', 'succ_prob.csv']:
        continue

    n = int(file.split('.')[0].split('_')[1])
    datasets[n] = list(
        map(float, l.split(',')) for l in open('sim_res/' + file).readlines()[1:]
    )

for k, d in datasets.items():
    x, y = zip(*d)
    plt.plot(x, y, c=dr_c[n_dr[k]], linewidth=1)

# plt.legend()
plt.tight_layout()
plt.title("Nodes Throughput")
plt.xlabel("Time (ms)")
plt.ylabel("Throughput (bps)")
plt.show()
