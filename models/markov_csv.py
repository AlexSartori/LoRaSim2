import sys
import random
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np


def markov_chain(data, dr):
    print("Training Markov Chain for dr = {:}".format(dr))
    P = [
        [0, 0],
        [0, 0]
    ]
    Ptrain = [
        [0, 0],
        [0, 0]
    ] # size dependent on nStates
    stateVisits = [] # to train the Markov chain

    for index, row in data.iterrows():
        if row['ACKed']:
            stateVisits.append(0)
        else:
            stateVisits.append(1)

    nStates = max(stateVisits) + 1
    nTr = len(data)

    print("true:", stateVisits.count(0), "false:", stateVisits.count(1))
    for i in range(len(stateVisits) - 1):
        Ptrain[stateVisits[i]][stateVisits[i+1]] = Ptrain[stateVisits[i]][stateVisits[i+1]] + 1

    for r in range(nStates):
        for c in range(nStates):
            if sum(Ptrain[r][:])==0:    #to avoid not visited states
                    P[r][c] = -1
            else:
                P[r][c] = Ptrain[r][c] / sum(Ptrain[r][:])

    print("    Transitions matrix:")
    print("        [{:}, {:}]".format(Ptrain[0][0], Ptrain[0][1]))
    print("        [{:}, {:}]".format(Ptrain[1][0], Ptrain[1][1]))
    print("    Probability matrix:")
    print("        [{:.3f}, {:.3f}]".format(P[0][0], P[0][1]))
    print("        [{:.3f}, {:.3f}]".format(P[1][0], P[1][1]))
    print("######################################################")

    print("Creating .ini model...")

    folder = sys.argv[1].split('.') # Isolate the path
    #print(folder)                   # Careful when moving data around, this may help
    
    folder = folder[2].split('/')   # Isolate the triplet "500-0-0"
    #print(folder)

    tmp = folder[3].split('-')      # Get the 3 components from the triplet
    distance_TX_RX = tmp[0]         # Distance RX-TX
    distance_Int_RX = tmp[1]        # Distance Int-TX
    pr_Int = tmp[2]                 # Probability of transmission
    
    with open("./files_jan/" + folder[3] + "_DR" + str(dr) + ".ini", 'w') as file:
        file.write('dr=' + str(dr) + '\n')
        file.write('distance_TX_RX=' + distance_TX_RX + '\n')
        file.write('distance_Int_RX=' + distance_Int_RX + '\n')
        file.write('pr_Int=' + pr_Int + '\n')
        file.write('p00={:.03f}\n'.format(P[0][0]))
        file.write('p01={:.03f}\n'.format(P[0][1]))
        file.write('p10={:.03f}\n'.format(P[1][0]))
        file.write('p11={:.03f}\n'.format(P[1][1]))

    res = []
    for r in range(nStates):
        for c in range(nStates):
            res.append(P[r][c])
    return res


def simulate(P):
    print("Simulating Markov Chain...")
    node = random.randint(0, 1) # Starting node
    state_counters = [0, 0]     # Number of transitions to each state
    fractions = []              # Fraction of time for each state at each step
    Ntr = 100000                # Number of trials

    for x in range(Ntr):
        U = random.uniform(0, 1)
        next_node = 0
        while U >= P[node][next_node]:
            U -= P[node][next_node]
            next_node += 1

        state_counters[next_node] += 1
        node = next_node

        frac = (
            state_counters[0]/(x+1),
            state_counters[1]/(x+1),
        )
        fractions.append(frac)

    print("Fractions: {0[0]:.3f}, {0[1]:.3f}".format(fractions[-1]))


def divide(data, dr):
    bottom = 1000*dr
    upper = 1000*(dr + 1)
    partition = data.iloc[bottom:upper]
    return partition


def main():
    file = open(sys.argv[1], 'r')
    procData = {}
    data = pd.read_csv(file)
    no_dr = [True]*7

    for dr in range(7):
        part = []
        part = divide(data, dr)
        if (part.empty):
            no_dr[dr] = False
            print(">>>>>>>>>> No more data from", dr, "on.")
        else:
            procData['DR' + str(dr)] = markov_chain(part, dr)

    # Print the results
    for i in range(7):
        if (no_dr[i]):
            print(f"DR{i}:", procData['DR' + str(i)])


#########################  FUNCTIONS ABOVE  #########################


if __name__ == "__main__":
    main()
