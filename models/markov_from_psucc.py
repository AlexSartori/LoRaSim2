from collections import namedtuple

Model = namedtuple("Model", "dr dist dist_int p_int p_succ setting")
models = [
    Model(0, 500, 0, 0, 0.625, 'urban'),
    Model(1, 500, 0, 0, 0.600, 'urban'),
    Model(2, 500, 0, 0, 0.025, 'urban'),
    Model(3, 500, 0, 0, 0, 'urban'),
    Model(4, 500, 0, 0, 0, 'urban'),
    Model(5, 500, 0, 0, 0, 'urban'),
    Model(6, 500, 0, 0, 0, 'urban'),
    
    Model(0, 1000, 0, 0, 0, 'urban'),
    Model(1, 1000, 0, 0, 0, 'urban'),
    Model(2, 1000, 0, 0, 0, 'urban'),
    Model(3, 1000, 0, 0, 0, 'urban'),
    Model(4, 1000, 0, 0, 0, 'urban'),
    Model(5, 1000, 0, 0, 0, 'urban'),
    Model(6, 1000, 0, 0, 0, 'urban'),
    
    Model(0, 1000, 0, 0, 0.700, 'mixed'),
    Model(1, 1000, 0, 0, 0.725, 'mixed'),
    Model(2, 1000, 0, 0, 0.625, 'mixed'),
    Model(3, 1000, 0, 0, 0, 'mixed'),
    Model(4, 1000, 0, 0, 0, 'mixed'),
    Model(5, 1000, 0, 0, 0, 'mixed'),
    Model(6, 1000, 0, 0, 0, 'mixed'),
    
    Model(0, 2000, 0, 0, 1, 'rural'),
    Model(1, 2000, 0, 0, 1, 'rural'),
    Model(2, 2000, 0, 0, 1, 'rural'),
    Model(3, 2000, 0, 0, 1, 'rural'),
    Model(4, 2000, 0, 0, 0.95, 'rural'),
    Model(5, 2000, 0, 0, 0.85, 'rural'),
    Model(6, 2000, 0, 0, 0.975, 'rural')
]

for m in models:
    with open(str(m.dist) + '_' + str(m.p_int) + '_DR' + str(m.dr) + ".ini", 'w') as file:
        file.write('dr=' + str(m.dr) + '\n')
        file.write('distance_TX_RX=' + str(m.dist) + '\n')
        file.write('distance_Int_RX=' + str(m.dist_int) + '\n')
        file.write('pr_Int=' + str(m.p_int) + '\n')
        file.write('p00={:.03f}\n'.format(m.p_succ))
        file.write('p01={:.03f}\n'.format(1 - m.p_succ))
        file.write('p10={:.03f}\n'.format(m.p_succ))
        file.write('p11={:.03f}\n'.format(1 - m.p_succ))
