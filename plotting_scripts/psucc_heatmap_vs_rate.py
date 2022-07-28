import matplotlib.pyplot as plt
from succ_prob_heatmap import get_matrix
from matplotlib.colors import LinearSegmentedColormap


pkt_rates = [10, 30, 60, 100, 200, 500]
mat_size = 30
max_dist = 2000

cmap = LinearSegmentedColormap.from_list('rg', ["r", "orange", "g"], N=50)
fig, axs = plt.subplots(2, 3)

for i, pr in enumerate(pkt_rates):
    mat, node_info = get_matrix("sim_res/" + str(pr) + ".map.csv", "sim_res/" + str(pr) + ".psucc.csv", mat_size, max_dist)
    ax_y = i//3
    ax_x = i%3
    ax = axs[ax_y][ax_x]

    im = ax.imshow(mat, cmap=cmap, interpolation='gaussian', aspect='equal', origin='upper')
    fig.colorbar(im, ax=ax)

    for x, y, dr in node_info:
        if dr == -1:
            ax.scatter(x, y, marker='^', s=50, c='yellow', label='Gateway')
        else:
            ax.scatter(x, y, marker='$'+str(dr)+'$', s=25, c='k', alpha=0.9, label='Node')

    ax.set_title("TX rate: " + str(pr) + " pkts/h")
    ax.set_xticks(range(0, mat_size+1, int(mat_size/10)), range(0, max_dist+1, int(max_dist/10)))
    ax.set_yticks(range(0, mat_size+1, int(mat_size/10)), range(0, max_dist+1, int(max_dist/10)))

plt.show()
