# README / And sufrsara's missing guide #

### 0. SurfSarah ###

* Surfsarah VM: 
    * http://norvigaward.github.io/gettingstarted.html

* VM root password: 
    * award2014

* User data: hdfs://hathi-surfsara/user/TUD-DS04/
* Test data: /data/public/common-crawl/crawl-data/CC-TEST-2014-10/ (80 GB)
* Full data: /data/public/common-crawl/CC-MAIN-2014-10/ (48.6 TB)

### 1. Example setup: ###
    git clone https://bitbucket.org/Spoetnic/data-science-tudelft

### 2. Install Maven: ###
    sudo apt-get install maven

### 3. Build code: ###
    cd /data-science-tudelft
    mvn install

### 4. Initialise Hadoop: (once every terminal I think) ###
    kinit TUD-DS04@CUA.SURFSARA.NL  
(Password : *************)

### 5. Look at the Hadoop File Structure: ###
    hadoop fs -ls /data/public/common-crawl/crawl-data/CC-TEST-2014-10
    hadoop fs -ls /data/public/common-crawl/crawl-data/CC-TEST-2014-10/segments
    hadoop fs -ls /data/public/common-crawl/crawl-data/CC-TEST-2014-10/segments/1394678706211
    hadoop fs -ls /data/public/common-crawl/crawl-data/CC-TEST-2014-10/segments/1394678706211/warc

### 5.a) Compiling and packaging examples

   mvn compile // compiles Java classes
   mvn package // creates .jar in target directory
   
   *use target/warcexamples-1.1-fatjar.jar it contains all the libraries that you will need

### 6. Run example: ###
    yarn jar target/warcexamples-1.1-fatjar.jar headers /data/public/common-crawl/crawl-data/CC-TEST-2014-10/segments/1394678706211/warc/CC-MAIN-20140313024506-00000-ip-10-183-142-35.ec2.internal.warc.gz

or

    yarn jar target/warcexamples-1.1-fatjar.jar href /data/public/common-crawl/crawl-data/CC-TEST-2014-10/segments/1394678706211/seq/CC-MAIN-20140313024506-00000-ip-10-183-142-35.ec2.internal.warc.seq href_output
(Note that a folder ´href_output´ will be created in the user data folder)

### 6.a) 
In order to run job on whole TEST or MAIN dataset you will need some magic. This is how you do it:


yarn jar warcexamples-1.1-fatjar.jar wordcount /data/public/common-crawl/crawl-data/CC-TEST-2014-10/segments/*/seq/ final_job_80GB_lc -D mapred.reduce.slowstart.completed.maps=0.95 > lc80.stdout 2> lc80.stderr &

notice the * in input_path.

You can disregard the part after -D, it is not essential (and I am too lazy to explain it at the moment)

### 7. There are 4 examples and our code (defined by the @run methods in the jar file) ###
* wordcount: Our own little slice of heaven. Usage:
~~~~
    yarn jar warcexamples.jar wordcount hdfs_input_path hdfs_output_path
~~~~

* NER: mapreduce example that performs Named Entity Recognition on text in wet files. See the nl.surfsara.warcexamples.hadoop.wet package for relevant code. Usage:
~~~~
    yarn jar warcexamples.jar ner hdfs_input_path hdfs_output_path
~~~~
* servertype: extracts the servertype information from the wat files. See the nl.surfsara.warcexamples.hadoop.wat package for relevant code. Usage:
~~~~
    yarn jar warcexamples.jar servertype hdfs_input_path hdfs_output_path
~~~~
* href: parses the html in warc files and outputs the url of the crawled page and the links (href attribute) from the parsed document. See the nl.surfsara.warcexamples.hadoop.warc package for relevant code. Usage:
~~~~
    yarn jar warcexamples.jar href hdfs_input_path hdfs_output_path
~~~~

Note that the input path should consist of sequence files.

* headers: dumps the headers from a wat, warc or wet file (gzipped ones). This is not a mapreduce example but files are read from HDFS. This can be run from your local computer or the VM. See the nl.surfsara.warcexamples.hadoop.hdfs package for relevant code. Usage:
~~~~
    yarn jar warcexamples.jar headers hdfs_input_file
~~~~
### 8. Possible Optimization (WHEN USING PIG) ###
* mapred.reduce.slowstart.completed.maps=0.90
  
  Adding this to the example gives:
~~~~
    yarn jar target/warcexamples-1.1-fatjar.jar headers -D mapred.reduce.slowstart.completed.maps=0.90 /data/public/common-crawl/crawl-data/CC-TEST-2014-10/segments/1394678706211/warc/CC-MAIN-20140313024506-00000-ip-10-183-142-35.ec2.internal.warc.gz
~~~~
### 9. See Job Progress on the cluster ###

* On the vm go to: http://head05.hathi.surfsara.nl/cluster/
* Or, on your won computer, make sure that this is added:
    * (Firefox) about:config >> network.negotiate-auth.trusted-uris >> hathi.surfsara.nl.

### 10. Get the output to your local VM: ###
    hadoop fs -ls href_output
    hadoop fs -cat href_output/part-r-00000 >> href_output.txt
(Note: default href output size on TEST data is 1.4GB, 1382002689 bytes to be precise)

### 11. (Optional) Kill a job ###
    yarn application -kill application_#########
(Where you have to get the application number from http://head05.hathi.surfsara.nl/cluster/)

### 12. (Optional) cleanup after yourself ###
Because you have to define a new directory every time you run a test, you might want to clean up after yourself:

    hadoop fs -rm -r hdfs_output_path

### 13. Build your own java files, based on the examples, rebuild, and run on hadoop. ###

###̉ TODO LIST (FOR DENIS AND TAHA)###
We have changed the project expectations a little bit by making it easier: we are going to look on how page are either shared or original (so not which one is the original and which one not), and therefore dropping some of our ambition. 

So we have done the Mapper (nl.surfsara.warcexamples.datascience.WordCountMapper). What is missing is to do the Reducer (nl.surfsara.warcexamples.datascience.WordCountReducer).
The Mapper goes through all the pages, creates a HashMap of the word frequency(with the HTML tags filtered out beforehand) and all the links for a page. Since the Reducer can only receive things one way, we decided to pass everything as (String,String) with the first argument as the key - the URL of the page.
 
The Reducer receives:

* string, Iterable<string>

* The String in the Iterable can either be a link (starts with http://) or the word counts (starting with Count:{ )

    * in this latter case you need to convert back from string to Hashmap, you will recognise because all the (URL, str(HashMap(string:int))) start with the word "count". To convert it back, just revert the function parsetoMap we did in the WordCountMapper

* It has to compute the similarity between the pages (the keys) with an algorith that looks how similar the hashmaps are. Do compute only the similarity between pages that have a link!

* the algorithm should give a low score to the pages that have a really similar word hashmap and a high score to those that have a original hashmap. So the pages that are highly "shared" gets a low value, while the pages that are "uncommon" gets a high value. The output should be something like (URL,score)

* When all of the above it-s done, do not run it only on the example, but on the whole common crawl dataset! For that you have to change the file in step 6

For this you want to look at the following files:

* Work on file by extending Reducer and overload reduce method. A file for this for you to work has been created, it is src/nl/surfsara/warcexamples/datascience/WordCounterReducer.java, with the Reducer code decompiled, and commented out, in there.

* If you want to look at our Mapper, it is in src/nl/surfsara/warcexamples/datascience/WordCountMapper.java

SideNote:

* The current test is done with
~~~
yarn jar target/warcexamples-1.1-fatjar.jar wordcount /data/public/common-crawl/crawl-data/CC-TEST-2014-10/segments/1394678706211/seq/CC-MAIN-20140313024506-00000-ip-10-183-142-35.ec2.internal.warc.seq href_output
~~~
Note the 00000 near the end. There are 99999 (lucky guess, but at least a lot :p) more of these files, and this is only the first in the sequence. 
You could look into parsing the other ones as well.


***********************************************************************************
VISUALISATION DATA:
{
    nodes: [
        {
        url:"http://website1.com/",
        words: [ { w:'Bob', c:14 }, { w:'test', c:123}]
        },
        {
        url:"http://website2.com/",
        words: [ { w:'Bob', c:14 }, { w:'test', c:123}]
        },
        {
        url:"http://website3.com/",
        words: [ { w:'Bob', c:14 }, { w:'test', c:123}]
        },
        {
        url:"http://website4.com/",
        words: [ { w:'Bob', c:14 }, { w:'test', c:123}]
        },
        {
        url:"http://website5.com/",
        words: [ { w:'Bob', c:14 }, { w:'test', c:123}]
        },
        {
        url:"http://website6.com/",
        words: [ { w:'Bob', c:14 }, { w:'test', c:123}]
        }
    ],
    edges: [
        {
            source: 0, target: 0,
            srce: "http://website1.com/", 
            dest: "http://website2.com/",
            count: 100
        },
        {
            source: 0, target: 0,
            srce: "http://website1.com/", 
            dest: "http://website3.com/",
            count: 130
        },
        {
            source: 0, target: 0,
            srce: "http://website2.com/", 
            dest: "http://website4.com/",
            count: 234
        },
        {
            source: 0, target: 0,
            srce: "http://website3.com/", 
            dest: "http://website4.com/",
            count: 240
        },
        {
            source: 0, target: 0,
            srce: "http://website4.com/", 
            dest: "http://website5.com/",
            count: 300
        },
        {
            source: 0, target: 0,
            srce: "http://website4.com/", 
            dest: "http://website6.com/",
            count: 140
        },
        {
            source: 0, target: 0,
            srce: "http://website3.com/", 
            dest: "http://website6.com/",
            count: 175
        }
        ,
        {
            source: 0, target: 0,
            srce: "http://website6.com/", 
            dest: "http://website3.com/",
            count: 200
        }
    ]
}
