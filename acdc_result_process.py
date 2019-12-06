file = open("acdc_result.txt", "r")

clusters = {}
for line in file:
    contain,cluster,item = line.strip().split(" ")
    # print(cluster)
    # print(item)
    if cluster not in clusters:
        clusters[cluster] = []

    if item.startswith("org.apache.activemq"):
        clusters[cluster].append(item)

outputFile = open("acdc_cluster.txt", "w+")

# clusters = sorted(clusters, key=lambda x: len(x))

for c in clusters:
    print(c)
    if len(clusters[c]) == 0: continue
    outputFile.write(str(len(clusters[c]))+",")
    for i in clusters[c]:
        outputFile.write(i+",")
    outputFile.write("\n")