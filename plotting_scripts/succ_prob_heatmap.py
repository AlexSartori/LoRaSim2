import sys
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.colors import LinearSegmentedColormap


mat_size = 30
max_size = 2000
cmap = LinearSegmentedColormap.from_list('rg', ["r", "orange", "g"], N=50)

topology = open("sim_res/topology.csv").readlines()[1:]
succ_prob = open("sim_res/succ_prob.csv").readlines()[1:]
mat = np.ones([mat_size+1, mat_size+1])

topology_map = {}
gateway_coords = []
for line in topology:
    n_id, type, dr, x, y = line.strip().split(',')
    if type == 'node':
        topology_map[n_id] = [int(x), int(y), int(dr)]
    else:
        gateway_coords.append([int(x), int(y)])

scaled_x = []
scaled_y = []
drs = []

for line in succ_prob:
    n_id, prob = line.strip().split(',')
    x, y, dr = topology_map[n_id]
    sq_x = int(x/max_size*mat_size)
    sq_y = int(y/max_size*mat_size)
    mat[sq_y][sq_x] = min(float(prob), mat[sq_y][sq_x])
    scaled_x.append(x/max_size*mat_size)
    scaled_y.append(y/max_size*mat_size)
    drs.append(dr)


plt.imshow(mat, cmap=cmap, interpolation='gaussian', aspect='equal', origin='upper')
plt.xticks(range(0, mat_size+1, int(mat_size/10)), range(0, max_size+1, int(max_size/10)))
plt.yticks(range(0, mat_size+1, int(mat_size/10)), range(0, max_size+1, int(max_size/10)))
plt.colorbar()

for i, dr in enumerate(drs):
    plt.scatter(scaled_x[i], scaled_y[i], marker='$'+str(dr)+'$', s=25, c='k', alpha=0.9, label='Node')

plt.scatter([c[0]/max_size*mat_size for c in gateway_coords], [c[1]/max_size
                                                               * mat_size for c in gateway_coords], marker='^', s=50, c='yellow', label='Gateway')
# plt.legend()

plt.title("Success probability")
plt.xlabel("X location (m)")
plt.ylabel("Y location (m)")
plt.tight_layout()
plt.xlim([0, mat_size])
plt.ylim([0, mat_size])
plt.show()
