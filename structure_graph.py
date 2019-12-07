import os

nodeToIndex = {}
indexToNode= {}
edgeList = []
nodeID = 0

def readFile(file_path, file_name):
    global nodeID
    global nodeToIndex
    global indexToNode
    global edgeList
    curNode = ''
    dependentNode = []

    file = open(file_path, "r")
    for line in file:
        if line.startswith("package "):
            curNode = line[7:-2] + "." + file_name[:-5]
            curNode = curNode.lstrip()
        if line.startswith("import org.apache."):
            dependentNode.append(line[7:-2].lstrip())
    
    # print(curNode)
    # print(dependentNode)

    if curNode not in nodeToIndex:
        nodeToIndex[curNode] = nodeID
        indexToNode[nodeID] = curNode
        if(nodeID == 0): print(curNode)
        nodeID += 1
    
    for dNode in dependentNode:
        if dNode not in nodeToIndex:
            nodeToIndex[dNode] = nodeID
            indexToNode[nodeID] = dNode
            nodeID += 1
        content = tuple([nodeToIndex[curNode], nodeToIndex[dNode]])
        edgeList.append(content)

    return curNode, dependentNode


if __name__ == "__main__":
    count = 0
    for root, dirs, files in os.walk("/Users/shengtao/Desktop/578project/activemq"):
        for file in files:
            if file.endswith(".java"):
                filePath = os.path.join(root, file)
                if "target" not in filePath:
                    count += 1
                    readFile(filePath, file)

    print(edgeList)

    output_file = open("edgeList.csv", "w+")
    for e in edgeList:
        n1 = min(int(e[0]), int(e[1]))
        n2 = max(int(e[0]), int(e[1]))
        output_file.write(str(n1) + "," + str(n2) + "\n")

    output_file2 = open("indexToNode.csv", "w+")
    for index in indexToNode:
        output_file2.write(str(index) + "," + indexToNode[index] + "\n") 

    print("total number of files:", count)