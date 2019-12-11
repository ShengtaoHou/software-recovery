# Final Project

## Subject System
ActiveMq(Version 5.5.0)

## Environment Set Up
- Spark 2.4.4

- Python 3.7

## General Ddea

![img](https://lh5.googleusercontent.com/kzJY-CE27AlSHg6s9J-WywYFV_cj-zAjyROySlvi1Ek7y-Bv94Aznk56vO0NATVSM4Zvziv3KEau8PGAcaFmXhSdsWcWDFNaO5tB7a2tdqcp4TWUQ3CTb1IcoLiJ_LZd0NC2qkdc)

Represent each file as a node in graph. Add an edge between two nodes if there is a dependency relationship between them. Finished construction of dependency graph. 

Run finding similar documents algorithm on all the source code. Add edge between two node if their corresponding files are very simlar. Finished construction of sementic graph.

Combine two graph into combined graph and run Girvan–Newman algorithm on this graph to detect communities(clusters) 

## How To Run
1. Extra `activemq-all-5.10.0.jar` and decompile it to java source code (See activemq folder).
2. Run `structure_graph.py` to generate dependency graph. Store graph information in intermediate files (`edgeList.csv` and `indexToNode.csv`).
3. Run `semantic_preprocess.py` to prepare for semamtic analysis.
4. Run `semantic_graph.py` to generate semantic graph. Add additional graph information to intermediate files
5. Run `cluster.py` to detect communities(clusters). The result is in `output_cluster.csv`


## Results
[clustering results](https://github.com/ShengtaoHou/HYZ-final-project/blob/master/output_cluster.csv)

## Hierachical Decomposition Visualization
![img](https://github.com/ShengtaoHou/HYZ-final-project/blob/master/Visualization/hierachical-decomposition.png)
