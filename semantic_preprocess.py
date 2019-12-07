import os
import re
pattern = re.compile('[\W_]+')

nodeToIndex = {}
wordSet = set([])
wordDict = {}
file_word_list = set([])

def readFile(file_path, file_name):
    global nodeToIndex
    global file_word_list
    curNode = ''

    file = open(file_path, "r")
    for line in file:
        if line.startswith("package "):
            curNode = line[7:-2] + "." + file_name[:-5]
            curNode = curNode.lstrip()

    file_index = nodeToIndex[curNode]

    file = open(file_path, "r")
    for line in file:
        wordList = line.strip().split(" ")
        for word in wordList:
            wordIndex = wordDict[ pattern.sub('', word) ]
            file_word_list.add(tuple([file_index, wordIndex]))



def generateWordSet(file_path, file_name):
    file = open(file_path, "r")
    for line in file:
        wordList = line.strip().split(" ")
        for word in wordList:
            wordSet.add(pattern.sub('', word))


if __name__ == "__main__":

    # generate word set
    for root, dirs, files in os.walk("/Users/shengtao/Desktop/578project/activemq"):
        for file in files:
            if file.endswith(".java"):
                filePath = os.path.join(root, file)
                generateWordSet(filePath, file)

    idx = 0
    for word in wordSet:
        wordDict[word] = idx
        idx += 1

    print(len(wordSet))
    print(len(wordDict))

    # read indexToNode file
    file2 = open("indexToNode.csv", "r")

    for line in file2:
        index, node = line.strip().split(",")
        nodeToIndex[str(node)] = str(index)

    print(len(nodeToIndex))


    # read files again, generate input for finding similar item

    for root, dirs, files in os.walk("/Users/shengtao/Desktop/578project/activemq"):
        for file in files:
            if file.endswith(".java"):
                filePath = os.path.join(root, file)
                readFile(filePath, file)

    print("file word list len",len(file_word_list))
    output_file = open("file_word_list.csv", "w+")
    for item in file_word_list:
        output_file.write(str(item[1]) + "," + str(item[0]) + "\n")
