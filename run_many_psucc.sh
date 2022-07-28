#!/bin/bash

rates=(10 30 60 100 200 500)
mkdir -p sim_res

for n in "${rates[@]}"
do
	echo -e "\n\n-------------------------------- TX rate: $n pkts/h"
	java -jar target/LoRaSim2-1.0-jar-with-dependencies.jar --tx-rate $n
	mv sim_res/succ_prob.csv sim_res/$n.psucc.csv
	mv sim_res/topology.csv sim_res/$n.map.csv
done
