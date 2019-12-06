# Final project

## Subject system
ActiveMq(Version 5.5.0)

## Environment step up
Spark 2.4.4

Python 3.7


## General idea
Represent each file as a node in graph. Add an edge between two nodes if there is a dependency relationship between them. Finished construction of dependency graph. 

Run finding similar documents algorithm on all the source code. Add edge between two node if their corresponding files are very simlar. Finished construction of sementic graph.

Combine two graph into combined graph and run Girvan–Newman algorithm on this graph to detect communities(clusters) 

## Experiment step
1. Extra activemq-all-5.10.0.jar and decompile it to java source code (See activemq folder).
2. Run preprocess.py to generate dependency graph and sementic graph. Combine two graph into one. Store combined graph information in intermediate file (edgeList.csv and indexToNode.csv).
3. Run cluster.py to detect communities(clusters). The result is output_cluster.csv



