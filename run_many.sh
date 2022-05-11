#!/bin/bash

N=20
n_nodes=(10 100 300 700 1000 1500 2000)

for n in "${n_nodes[@]}"
do
	echo -e "\n\n\n-------------------------------- N of nodes: $n"
	mkdir -p sim_res/$n

	for iteration in $(seq 1 $N)
	do
		echo -e "\n---------------- Iteration: $iteration"
		java -jar LoRaSim2-1.0-jar-with-dependencies.jar --num-nodes $n --out-thr-fname "sim_res/$n/$iteration.n{id}.csv"
	done
done

