# Data-Science-TUDeflt

##How to test if your mapper/reducer works before running it on cluster

```
./map.py < inputfile.csv | sort | ./reduce.py
```

##How to run it on cluster:

###Put the txt file into the cluster
```
hadoop fs -mkdir jobinput
hadoop fs -put inputfile.csv
hadoop fs -mv inputfile.csv jobinput/inputfile.csv
```

###Run mapreduce job
```
hadoop jar /usr/lib/hadoop-0.20-mapreduce/contrib/streaming/hadoop-streaming-2.6.0-mr1-cdh5.4.0.jar -mapper map.py -reducer reduce.py -file map.py -file reduce.py -input jobinput -output joboutput
```
