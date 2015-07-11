# README #

##Things JW knows about the project##

### 0. SurfSarah ###

* Surfsarah VM: 
    * http://norvigaward.github.io/gettingstarted.html

* VM root password: 
    * award2014

* User data: hdfs://hathi-surfsara/user/TUD-DS04/
* Test data: /data/public/common-crawl/crawl-data/CC-TEST-2014-10/ (80 GB)
* Full data: /data/public/common-crawl/CC-MAIN-2014-10/ (48.6 TB)

### 1. Example setup: ###
    git clone https://github.com/norvigaward/warcexamples

### 2. Install Maven: ###
    sudo apt-get install maven

### 3. Build examples: ###
    cd /warcexamples
    mvn install

### 4. Initialise Hadoop: (once every terminal I think) ###
    kinit TUD-DS04@CUA.SURFSARA.NL  
(Password : DataScience1)

### 5. Look at the Hadoop File Structure: ###
    hadoop fs -ls /data/public/common-crawl/crawl-data/CC-TEST-2014-10
    hadoop fs -ls /data/public/common-crawl/crawl-data/CC-TEST-2014-10/segments
    hadoop fs -ls /data/public/common-crawl/crawl-data/CC-TEST-2014-10/segments/1394678706211
    hadoop fs -ls /data/public/common-crawl/crawl-data/CC-TEST-2014-10/segments/1394678706211/warc


### 6. Run example: ###
    yarn jar target/warcexamples-1.1-fatjar.jar headers /data/public/common-crawl/crawl-data/CC-TEST-2014-10/segments/1394678706211/warc/CC-MAIN-20140313024506-00000-ip-10-183-142-35.ec2.internal.warc.gz

or

    yarn jar target/warcexamples-1.1-fatjar.jar href /data/public/common-crawl/crawl-data/CC-TEST-2014-10/segments/1394678706211/seq/CC-MAIN-20140313024506-00000-ip-10-183-142-35.ec2.internal.warc.seq href_output
(Note that a folder ´href_output´ will be created in the user data folder)

### 7. There are 4 examples (defined by the main(String[] args) methods in the jar file) ###
- NER: mapreduce example that performs Named Entity Recognition on text in wet files. See the nl.surfsara.warcexamples.hadoop.wet package for relevant code. Usage:

    yarn jar warcexamples.jar ner hdfs_input_path hdfs_output_path

- servertype: extracts the servertype information from the wat files. See the nl.surfsara.warcexamples.hadoop.wat package for relevant code. Usage:

    yarn jar warcexamples.jar servertype hdfs_input_path hdfs_output_path

- href: parses the html in warc files and outputs the url of the crawled page and the links (href attribute) from the parsed document. See the nl.surfsara.warcexamples.hadoop.warc package for relevant code. Usage:

    yarn jar warcexamples.jar href hdfs_input_path hdfs_output_path

    Note that the input path should consist of sequence files.

- headers: dumps the headers from a wat, warc or wet file (gzipped ones). This is not a mapreduce example but files are read from HDFS. This can be run from your local computer or the VM. See the nl.surfsara.warcexamples.hadoop.hdfs package for relevant code. Usage:

    yarn jar warcexamples.jar headers hdfs_input_file

### 8. Possible Optimization (WHEN USING PIG) ###
* mapred.reduce.slowstart.completed.maps=0.90
  
  Adding this to the example gives:
    yarn jar target/warcexamples-1.1-fatjar.jar headers -D mapred.reduce.slowstart.completed.maps=0.90 /data/public/common-crawl/crawl-data/CC-TEST-2014-10/segments/1394678706211/warc/CC-MAIN-20140313024506-00000-ip-10-183-142-35.ec2.internal.warc.gz

### 9. See Job Progress on the cluster ###

* On the vm go to: http://head05.hathi.surfsara.nl/cluster/
* Or, on your won computer, make sure that this is added:
    * (Firefox) about:config >> network.negotiate-auth.trusted-uris >> hathi.surfsara.nl.

### 10. Get the output to your local VM: ###
    hadoop fs -ls href_output
    hadoop fs -cat href_output/part-r-00000 >> href_output.txt
(Note: default href output size on TEST data is 1.4GB, 1382002689 bytes to be precise)

### 11. (Optional) Kill a job ###
> yarn application -kill application_#########
(Where you have to get the application number from http://head05.hathi.surfsara.nl/cluster/)

### 12. Build your own java files, based on the examples, rebuild, and run on hadoop. ###

# Data-Science-TUDelft (OLD README)

##How to test if your mapper/reducer works before running it on cluster

```
./map.py < inputfile.csv | sort | ./reduce.py >> output.txt
```
