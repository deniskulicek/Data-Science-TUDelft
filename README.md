# README #

This README would normally document whatever steps are necessary to get your application up and running.

### What is this repository for? ###

* Quick summary
* Version
* [Learn Markdown](https://bitbucket.org/tutorials/markdowndemo)

### How do I get set up? ###

* Summary of set up
* Configuration
* Dependencies
* Database configuration
* How to run tests
* Deployment instructions

### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Who do I talk to? ###

* Repo owner or admin
* Other community or team contact

# Data-Science-TUDelft

##How to test if your mapper/reducer works before running it on cluster

```
./map.py < inputfile.csv | sort | ./reduce.py >> output.txt
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
hadoop jar /usr/lib/hadoop-0.20-mapreduce/contrib/streaming/hadoop-streaming-2.5.0-mr1-cdh5.3.0.jar -mapper map.py -reducer reduce.py -file map.py -file reduce.py -input jobinput -output joboutput
```