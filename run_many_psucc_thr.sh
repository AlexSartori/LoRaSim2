#!/bin/bash

N=10
rates=(10 30 60 100 200 500)
tot_pkts=500
mkdir -p sim_res

for r in "${rates[@]}"
do
    time_for_pkts_h=$(( $tot_pkts / $r ))
    sim_dur_ms=$(( $time_for_pkts_h * 3600000 ))

	echo -e "\n\n---------------- TX rate: $r pkts/h   /  Sim duration: $time_for_pkts_h h  ($sim_dur_ms ms)"
	echo "Launching $N parallel simulations..."

	for iteration in $(seq 1 $N)
	do
		java -jar target/LoRaSim2-1.0-jar-with-dependencies.jar --sim-duration $sim_dur_ms --tx-rate $r --final-thr-csv "sim_res/thr.$r.pkth.$iteration.csv" --psucc-csv "sim_res/psucc.$r.pkth.$iteration.csv" > /dev/null &
	done

	echo "Done. Waiting for their termination..."
	wait
done
