import re, os
import matplotlib.pyplot as plt


tx_data = {}

for file in os.listdir('sim_res/'):
    if not re.match(r'[0-9]+_rx_data\.csv', file):
        continue

    for line in open('sim_res/' + file).readlines()[1:]:
        if line.strip() == '':
            continue
        src, start, end, _, _ = line.strip().split(',')
        if src not in tx_data:
            tx_data[src] = []
        tx_data[src].append(float(start)/1000)


wait_times = {}

for src, tx_times in tx_data.items():
    wait_times[src] = []
    tx_times = sorted(tx_times)
    t_prev = tx_times[0]

    for t in tx_times[1:]:
        wait_times[src].append(t - t_prev)
        t_prev = t


plt.hist(wait_times.values(), label=list('Node ' + n for n in wait_times.keys()))
plt.title("TX wait times with rate = 10 pkt/hour")
plt.xlabel("Wait time between packets (s)")
plt.legend()
plt.tight_layout()
plt.show()
