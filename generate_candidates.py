from enum import IntEnum
import os.path
from collections import Counter
from background_processing import get_background_popularity
from background_processing import get_prefix_tree


class CandidateScenario (IntEnum):
    NO_SUFFIXES = 1
    TENK_SUFFIXES = 2
    HUNDERDK_SUFFIXES = 3


class DataSet (IntEnum):
    TRAINING = 1
    TEST = 2
    VALIDATION = 3
    BACKGROUND = 4

def load_dataset(set: DataSet):
    file_name = ""
    if set is DataSet.TRAINING:
        file_name = "training.txt"
    elif set is DataSet.VALIDATION:
        file_name = "validation.txt"
    elif set is DataSet.TEST:
        file_name =  "test.txt"
    elif set is DataSet.BACKGROUND:
        file_name = "background.txt"
    else:
        raise Exception(f"Dataset not found: {set}")

    if not os.path.isfile(file_name):
        raise Exception(f"{file_name} is not available. Please run the `parse_dataset.py` script.")

    print(f"Now loading {file_name}")

    f = open(file_name, 'r')
    data = f.read().splitlines()
    f.close()

    return data[1:]


def most_popular_completion(scenario: CandidateScenario, dataset: DataSet):
    data = load_dataset(dataset)
    print(f"Size of set is {len(data)}.")

    background_popularity = get_background_popularity()
    background_popularity_tree = get_prefix_tree(background_popularity, "background_popularity_tree.txt")
    mrr = 0
    count = 0
    j = 0
    for query in data:
        j += 1
        if j % 1000 == 0:
            prog = "{:.2f}".format(j / len(data) * 100)
            print(mrr / count)
            print(f"Evaluating MostPopularCompletion: {prog}%")

        query_split = query.split(" ", 2)
        q = query_split[0]

        count += 1
        mrr += computeReciprocalRank(query, get_full_query_candidates(background_popularity_tree, q))

        if len(query_split) <= 1:
            continue

        for char in " " + query_split[1]:
            # Skip whitespace.
            if char == " ":
                q += char
                continue
            q += char
            count += 1
            mrr += computeReciprocalRank(query, get_full_query_candidates(background_popularity_tree, q))
    print(f"MRR: {mrr/count}")


def computeReciprocalRank(query, candidates):
    i = 0
    for candidate, count in candidates:
        if query == candidate:
            return 1.0 / (i + 1)
        i += 1
    return 0


def get_full_query_candidates(background_popularity, prefix, max = 10):
    return Counter(dict(background_popularity.items(prefix))).most_common(max)




most_popular_completion(CandidateScenario.NO_SUFFIXES, DataSet.BACKGROUND)

