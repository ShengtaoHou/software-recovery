from __future__ import division
import sys
from pyspark.context import SparkContext
from pyspark.storagelevel import StorageLevel
from itertools import combinations
import time


def hash_func(hash_p, x):
    res = [0 for x in range(len(hash_p))]
    for i in range(len(hash_p)):
        a = hash_p[i][0]
        b = hash_p[i][1]
        res[i] = (a * x + b) % user_len
    return res


def jaccard(a, b):
    return len(a & b) / len(a | b)


if __name__ == "__main__":
    start_time = time.time()
    sc = SparkContext('local[*]', 'task1')

    input_file = "file_word_list.csv"
    output_file = "edgeList.csv"

    rdd = sc.textFile(input_file)

    # (word, document)
    data = rdd.map(lambda line: line.split(",")) \
        .map(lambda line: (line[0], line[1])) \
        .persist(storageLevel=StorageLevel.DISK_ONLY)

    user = data.keys().distinct().collect()

    business = data.values().distinct().collect()

    business_len = len(business)
    # print("business len", business_len)

    user_dict = {}
    for i in range(len(user)):
        user_dict[user[i]] = i

    business_dict2 = {}
    business_dict = {}
    for i in range(len(business)):
        business_dict[business[i]] = i
        business_dict2[i] = business[i]

    matrix = data.map(lambda x: (user_dict[x[0]], business_dict[x[1]])).collect()

    user_len = len(user)
    # print("user len", user_len)

    a = [1, 3, 9, 11, 13, 17, 19, 27, 29, 31, 33, 37, 39, 41, 43, 47, 51, 53, 57, 59, 61, 67, 71, 73, 79, 81, 83, 87, 89, 93]
    b = [ x*375 for x in range(1, 31)]

    hash_parameter = []

    for k in range(30):
        hash_parameter.append([1, b[k]])

    for k in range(30):
        hash_parameter.append([a[k], 0])

    # print(hash_parameter)

    # hash_parameter = [[13, 2], [13, 5], [13, 7], [13, 13], [13, 17], [13, 23], [13, 29], [13, 37],
    #                   [13, 53], [13, 97], [13, 193], [17, 2], [17, 5], [17, 7], [17, 13], [17, 17],
    #                   [17, 23], [17, 29], [17, 37], [17, 53], [17, 97], [17, 193], [29, 2], [29, 5],
    #                   [29, 7], [29, 13], [29, 17], [29, 23], [29, 29], [29, 37], [29, 53], [29, 97],
    #                   [29, 193], [37, 2], [37, 5], [37, 7], [37, 13], [37, 17], [37, 23], [37, 29],
    #                   [37, 37], [37, 53], [37, 97], [37, 193], [53, 2], [53, 5], [53, 7], [53, 13],
    #                   [53, 17], [53, 23], [53, 29], [53, 37], [53, 53], [53, 97], [53, 193], [97, 2],
    #                   [97, 5], [97, 7], [97, 13], [97, 17], [97, 23], [97, 29], [97, 37], [97, 53]]

    b = 30
    r = int(len(hash_parameter) / b)

    sig_row_num = 60
    sig_matrix = []
    for i in range(sig_row_num):
        sig_matrix.append([user_len + 1] * business_len)


    # hash_table = [[0 for x in range(len(hash_parameter))] for y in range(len(user))]
    # for i in range(len(user)):
    #     hash_table[i] = hash_func(hash_parameter, i)

    # minhash
    true_matrix = {}
    # print("matrix len:", len(matrix))
    for i in range(len(matrix)):
        user_index = matrix[i][0]
        business_index = matrix[i][1]

        # generate true matrix by the way
        if business_index in true_matrix:
            true_matrix[business_index].add(user_index)
        else:
            true_matrix[business_index] = {user_index}

        for k in range(len(hash_parameter)):
            hash = (user_index * hash_parameter[k][0] + hash_parameter[k][1] ) % user_len
            if hash < sig_matrix[k][business_index]:
                sig_matrix[k][business_index] = hash

    # for i in range(60):
    #     print("sig matrix:", sig_matrix[0][:30])


    cand_pair = set()
    for i in range(b):
        bucket = {}
        for j in range(len(business)):
            index = ''
            for row in range(i * r, i * r + r):
                index += str(sig_matrix[row][j]) + ','

            if index not in bucket:
                bucket[index] = []
            bucket[index].append(j)

        # print("bucket len:",len(bucket))

        for key in bucket:
            if len(bucket[key]) > 1:
                if (len(bucket[key]) < 5000):
                    for comb in list(combinations(bucket[key], 2)):
                        cand_pair.add(comb)

    # print("candidate length", len(cand_pair))

    result = []
    for cand in cand_pair:
        list1 = true_matrix[cand[0]]
        list2 = true_matrix[cand[1]]
        similar = jaccard(list1, list2)
        if similar >= 0.97:
            business_id1 = business_dict2[cand[0]]
            business_id2 = business_dict2[cand[1]]
            if business_id1 < business_id2:
                cand_list = tuple([business_id1, business_id2, similar])
            else:
                cand_list = tuple([business_id2, business_id1, similar])
            result.append(cand_list)

    result = sorted(list(set(result)), key=lambda x: x[0])

    # print("recall: ", len(result) / 645)

    file = open(output_file, "a+")
    for i in range(len(result)):
        file.write(result[i][0] + "," + result[i][1] + "\n")

    end_time = time.time()
    total_time = end_time - start_time
    print("Duration:", total_time)
