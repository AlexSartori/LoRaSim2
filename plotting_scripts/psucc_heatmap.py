import sys
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.colors import LinearSegmentedColormap


def get_matrix(t_fname, ps_fname, mat_size, max_dist):
    mat = np.ones([mat_size+1, mat_size+1])
    topology = open(t_fname).readlines()[1:]
    succ_prob = open(ps_fname).readlines()[1:]

    topology_map = {}
    gateway_coords = []
    for line in topology:
        n_id, type, dr, x, y = line.strip().split(',')
        if type == 'node':
            topology_map[n_id] = [int(x), int(y), int(dr)]
        else:
            gateway_coords.append([int(x), int(y)])

    nodes_info = []

    for x, y in gateway_coords:
        nodes_info.append(
            (int(x/max_dist*mat_size), int(y/max_dist*mat_size), -1)
        )

    for line in succ_prob:
        n_id, prob = line.strip().split(',')
        x, y, dr = topology_map[n_id]
        sq_x = int(x/max_dist*mat_size)
        sq_y = int(y/max_dist*mat_size)
        mat[sq_y][sq_x] = min(float(prob), mat[sq_y][sq_x])
        scaled_x = (x/max_dist*mat_size)
        scaled_y = (y/max_dist*mat_size)
        nodes_info.append((int(scaled_x), int(scaled_y), dr))

    return mat, nodes_info


def main():
    mat_size = 30
    max_dist = 2000
    cmap = LinearSegmentedColormap.from_list('rg', ["r", "orange", "g"], N=50)
    mat, nodes = get_matrix("sim_res/topology.csv", "sim_res/succ_prob.csv", mat_size, max_dist)

    plt.imshow(mat, cmap=cmap, interpolation='gaussian', aspect='equal', origin='upper')
    plt.xticks(range(0, mat_size+1, int(mat_size/10)), range(0, max_dist+1, int(max_dist/10)))
    plt.yticks(range(0, mat_size+1, int(mat_size/10)), range(0, max_dist+1, int(max_dist/10)))
    plt.colorbar()

    for x, y, dr in nodes:
        if dr == -1:
            plt.scatter(x, y, marker='^', s=50, c='yellow', label='Gateway')
        else:
            plt.scatter(x, y, marker='$'+str(dr)+'$', s=25, c='k', alpha=0.9, label='Node')

    plt.title("Success probability")
    plt.xlabel("X location (m)")
    plt.ylabel("Y location (m)")
    plt.tight_layout()
    plt.xlim([0, mat_size])
    plt.ylim([0, mat_size])
    plt.show()


if __name__ == '__main__':
    main()
