from __future__ import division
import sys
from pyspark.context import SparkContext
from pyspark.storagelevel import StorageLevel
from itertools import combinations
import time

# return sorted user pair from user list
def getUserPair(x):
    # x is (product, [user1, user2, user3 ...])
    for comb in combinations(x[1] , 2):
        user1 = min(comb[0], comb[1])
        user2 = max(comb[0], comb[1])
        yield ((user1, user2) ,1)

def GirvanNewman(root):

    # construct bfs tree
    visited = set([root])
    queue = [root]
    level = 1
    levelDict = {root: 0}
    parentDict = {}
    shortPathDict = {root: 1}
    bfsResult = [root]
    while len(queue) > 0:
        size = len(queue)
        # print("queue", queue)
        for i in range(size):

            curNode = queue.pop(0)

            children = userDict[curNode]

            # visit all its children
            for child in children:
                if child not in visited:
                    visited.add(child)
                    bfsResult.append(child)
                    levelDict[child] = level
                    queue.append(child)
                    parentDict[child] = [curNode] # visit sibling here, cause bug
                    shortPathDict[child] = shortPathDict[curNode]
                else:
                    if levelDict[child] == levelDict[curNode] + 1:
                        parentDict[child] += [curNode]
                        shortPathDict[child] += shortPathDict[curNode]
        level += 1

    # print("total level", level)

    nodeCreditDict = {}
    # calculate betweeness
    for i in range(len(bfsResult) - 1, -1, -1):
        node = bfsResult[i]
        # give every node credit of one
        if node not in nodeCreditDict:
            nodeCreditDict[node] = 1
        # if not the root node
        if node in parentDict:
            parents = parentDict[node]
            parentSize = len(parents)

            sum = 0
            for parent in parents:
                if parent in shortPathDict:
                    sum += shortPathDict[parent]
            # give all parents credit
            for parent in parents:
                weight = 1
                if sum != 0:
                    weight = shortPathDict[parent] / sum
                edgeCredit = float(nodeCreditDict[node]) * weight
                # edgeCredit = float(nodeCreditDict[node]) / parentSize
                if parent not in nodeCreditDict:
                    nodeCreditDict[parent] = 1
                nodeCreditDict[parent] += edgeCredit
                edgeTuple = (min(node, parent), max(node, parent))
                yield (edgeTuple, edgeCredit / 2 )


def isConnected(user1, user2, userDict, visited):
    visited.add(user1)

    for neighbor in userDict[user1]:
        if neighbor == user2:
            return True
        else:
            if neighbor not in visited:
                if isConnected(neighbor, user2, userDict, visited):
                    return True
    return False


def dfs(i, userDict, visited, subGraph):
    visited.add(i)
    subGraph.append(i)
    for neighbor in userDict[i]:
        if neighbor not in visited:
            dfs(neighbor, userDict, visited, subGraph)


# fixup: does i include all nodes
def findConnectedGraph(userDict):
    connectedGraph = []
    visited = set()
    res = 0
    for i in userDict:
        if i not in visited:
            subGraph = []
            dfs(i, userDict, visited, subGraph)
            connectedGraph.append(sorted(subGraph))

    return sorted(connectedGraph, key=lambda x: -len(x))


def calculateModularity(communities, modularityDict):
    Q = 0
    for c in communities:
        for i in c:
            for j in c:
                Q += modularityDict[(i,j)]
    return Q / (2 * M)

if __name__ == "__main__":

    startTime = time.time()
    sc = SparkContext('local[*]', '578task')
    sc.setLogLevel("WARN")

    input_file = "edgeList.csv"
    output_file = "output_cluster_best_size.csv"

    rdd = sc.textFile(input_file)

    edges = rdd.map(lambda x: x.split(","))\
        .map(lambda x: (x[0], x[1]))

    M = edges.count()
    print("Total edge number:", M)

    # record every users' neighbor, (user1, [user2, user3...])
    userNeighbor = edges.flatMap(lambda x: [(x[0], [x[1]]) , (x[1], [x[0]])]).reduceByKey(lambda x, y : x + y)


    # key is userID, value is neighbor userID list
    userDict = dict(userNeighbor.collect())

    # print(userDict['3'])

    betweenness = userNeighbor.flatMap(lambda x: GirvanNewman(x[0])).reduceByKey(lambda x, y: x + y)\
    .sortBy(lambda x: - x[1]).collect()

    print("Finish betweeness calculation")
    nodes = sorted(edges.flatMap(lambda x: [x[0], x[1]]).distinct().collect())
    nodesNumber = len(nodes)
    print("Nodes number:",nodesNumber, "\n")


    K = dict(userNeighbor.map(lambda x: (x[0], len(x[1]))).collect())

    ADict = {}
    for i in nodes:
        for j in nodes:
            if j in userDict[i] or i in userDict[j]:
                ADict[(i,j)] = 1
            else:
                ADict[(i,j)] = 0

    # modularity dict for all calculation
    modularityDict = {}
    for i in nodes:
        for j in nodes:
            if j in userDict[i] or i in userDict[j]:
                A = 1
            else:
                A = 0
            modularityDict[(i,j)] = (A - K[i] * K[j] / (2 * M))


    # calculate the initial communities and modularity
    maxCommunities = findConnectedGraph(userDict)
    maxQ = calculateModularity(maxCommunities, modularityDict)
    preQ = maxQ


    bList = betweenness

    sizeDiff = []

    preMaxSize = 1648

    preCommunities = maxCommunities

    while(len(bList) > 0):
        b = bList.pop(0)
        user1 = b[0][0]
        user2 = b[0][1]

        # remove edge
        userDict[user1].remove(user2)
        userDict[user2].remove(user1)

        if isConnected(user1, user2, userDict, set([])):
            continue

        # recalculate the betweenness
        bList = userNeighbor.flatMap(lambda x: GirvanNewman(x[0])).reduceByKey(lambda x, y: x + y) \
            .sortBy(lambda x: - x[1]).collect()
        # calculate the modularity of communities
        communities = findConnectedGraph(userDict)
        Q = calculateModularity(communities, modularityDict)


        print("\nCommunities number: ", len(communities))
        print("Max size communities: ",len(communities[0]))
        print("Current Modularity: ", Q)

        curComLen = []
        for c in communities:
            curComLen.append(len(c))

        preComLen = []
        for c in preCommunities:
            preComLen.append(len(c))


        interSet = list(set(curComLen) & set(preComLen))
        parent = list(set(preComLen) - set(interSet))
        children = list( set(curComLen) - set(interSet))

        print("Split communities ", parent, " into two communities: ", children)

        # if len(communities[0]) < 605:  # 最佳的modularity 下的 cluster




        if len(communities[0]) <= 350: # 最小的cluster size的结果
            maxQ = Q
            maxCommunities = communities
            break



        preMaxSize = len(communities[0])


        preQ = Q
        preCommunities = communities

    maxCommunities = sorted(maxCommunities, key=lambda x: (len(x), x[0]))


    firstCommunity = []


    t = 0
    while(len(maxCommunities[0]) == 1):
        firstCommunity.append(maxCommunities[0][0])
        maxCommunities.pop(0)
    maxCommunities.append(firstCommunity)

    file2 = open("indexToNode.csv", "r")
    indexToNode = {}
    for line in file2:
        index, node = line.strip().split(",")
        # print(index, node)
        indexToNode[str(index)] = str(node)

    file = open(output_file, "w")
    for c in maxCommunities:
        file.write(str(len(c))+",")
        for j in range(len(c)):
            if str(c[j]) in indexToNode:
                file.write(indexToNode[str(c[j])] + ", " )
            else:
                print(c[j])
        file.write("\n")

    endTime = time.time()
    totalTime = endTime - startTime
    print("Duration",totalTime)


