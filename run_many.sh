#!/bin/bash

N=70
n_nodes=(5 10 100 500 1000 2000)
mkdir -p sim_res

for n in "${n_nodes[@]}"
do
	echo -e "\n\n-------------------------------- N of nodes: $n"
	echo "Launching $N parallel simulations..."

	for iteration in $(seq 1 $N)
	do
		java -jar LoRaSim2-1.0-jar-with-dependencies.jar --num-nodes $n --final-thr-csv "sim_res/thr.$n.nodes.$iteration.csv" > /dev/null &
	done

	echo "Done. Waiting for their termination..."
	wait
done
