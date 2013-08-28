#!/usr/bin/env python

import os

numIntelligentAgents = [1, 5, 10]
threshold            = [100, 300, 500, 700]
delay                = [1000, 500, 250, 100, 50, 10, 5]

for i in numIntelligentAgents:
  for j in threshold:
    for k in delay:
      dirname = os.path.join("trials", "intelligentAgentCount_" + str(i), "threshold_" + str(j), "delay_" + str(k))
      for m in range(1):
        job = "#!/bin/bash\n\
        #PBS -l nodes=1\n\
        #PBS -l walltime=2:00:00\n\
        #PBS -m e\n\
        #PBS -M spt9np@virginia.edu\n\
        cd \$PBS_O_WORKDIR\n\
        java -jar jinsup.jar --destIAProfitFile " + os.path.join(dirname, str(m + 1).zfill(2)) + ".csv --buy 160000 --start 300000 --trade 21700000 --threshold " + str(j) + " --delay " + str(k) + " --test --numIntelligentAgents " + str(i) + "\n"

        f = open('job.sh', 'w')
        f.write(job)
        f.close()
        os.system("qsub job.sh")
