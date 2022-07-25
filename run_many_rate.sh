#!/bin/bash

N=10
rates=(10 30 60 100 200 500)
mkdir -p sim_res

for n in "${rates[@]}"
do
	echo -e "\n\n-------------------------------- TX rate: $n pkts/h"
	echo "Launching $N parallel simulations..."

	for iteration in $(seq 1 $N)
	do
		java -jar target/LoRaSim2-1.0-jar-with-dependencies.jar --tx-rate $n --final-thr-csv "sim_res/thr.$n.pkth.$iteration.csv" > /dev/null &
	done

	echo "Done. Waiting for their termination..."
	wait
done
