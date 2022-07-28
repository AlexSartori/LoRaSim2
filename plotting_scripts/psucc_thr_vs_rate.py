import os
import matplotlib.pyplot as plt


thr_data = {}
psucc_data = {}

for fname in os.listdir('sim_res/'):
    typ, rate, _, _, _ = fname.split('.')
    lines = open('sim_res/' + fname).readlines()[1:]
    rate = int(rate)

    if typ == 'psucc':
        if rate not in psucc_data:
            psucc_data[rate] = []
        for l in lines:
            p = float(l.split(',')[1])
            psucc_data[rate].append(p)
    elif typ == 'thr':
        if rate not in thr_data:
            thr_data[rate] = []
        for l in lines:
            t = float(l.split(',')[1])
            thr_data[rate].append(t)


rates = sorted(thr_data.keys())
fig, axs = plt.subplots(len(rates), 2)

for i, rate in enumerate(rates):
    ax = axs[i][0]
    ax.hist(psucc_data[rate])
    ax.set_title("P(success) at TX rate of " + str(rate) + " pkts/h")
    ax.set_xlim([0, 1])
    ax.set_ylim([0, 300])

    ax = axs[i][1]
    ax.hist(thr_data[rate])
    ax.set_title("Throughput at TX rate of " + str(rate) + " pkts/h")
    ax.set_xlim([0, 40])
    ax.set_ylim([0, 300])

plt.title("100 nodes at DR1, 500 pkts each")
plt.show()
