# Final project

## Subject system
active-mq

## Environment step up
Spark 2.4.4
Python 3.7


## Idea
use the dependence between java file to generate a graph, apply community detection algorithm (Girvan-Newman) to cluster source code into different group

## Experiment step
1. extra activemq-all-5.10.0.jar and decompile it to java source code files(See activemq folder)
2. run preprocess.py to get activemq structure information according to dependence of java files. This step will generate two intermediate files include edgeList.csv and indexToNode.csv
3. run cluster.py to group all java files into different clusters. The result is output_cluster.csv

## Experiment result
