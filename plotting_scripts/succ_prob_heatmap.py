import sys
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.colors import LinearSegmentedColormap


mat_size = 100
max_size = 2000
cmap = LinearSegmentedColormap.from_list('rg', ["r", "g"], N=50)

topology = open("sim_res/topology.csv").readlines()[1:]
succ_prob = open("sim_res/succ_prob.csv").readlines()[1:]
mat = np.ones([mat_size, mat_size])

topology_map = {}
gateway_coords = []
for line in topology:
    n_id, type, x, y = line.strip().split(',')
    if type == 'node':
        topology_map[n_id] = [int(x), int(y)]
    else:
        gateway_coords.append([int(x), int(y)])

scaled_x = []
scaled_y = []

for line in succ_prob:
    n_id, prob = line.strip().split(',')
    x, y = topology_map[n_id]
    x = int(x/max_size*mat_size)
    y = int(y/max_size*mat_size)
    mat[y][x] = min(float(prob), mat[y][x])
    scaled_x.append(x)
    scaled_y.append(y)


plt.imshow(mat, cmap=cmap, interpolation='gaussian', aspect='equal', origin='upper')
plt.xticks(range(0, mat_size, int(mat_size/10)), range(0, max_size, int(max_size/10)))
plt.yticks(range(0, mat_size, int(mat_size/10)), range(0, max_size, int(max_size/10)))
plt.colorbar()

plt.scatter(scaled_x, scaled_y, marker='.', s=4, c='black', alpha=0.5, label='Node')
plt.scatter([c[0]/max_size*mat_size for c in gateway_coords], [c[1]/max_size*mat_size for c in gateway_coords], marker='^', s=50, c='yellow', label='Gateway')
plt.legend()

plt.title("Success probability")
plt.xlabel("X location (m)")
plt.ylabel("Y location (m)")
plt.tight_layout()
plt.show()
