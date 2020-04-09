from enum import IntEnum
import os.path


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
    background_data = load_dataset(DataSet.BACKGROUND)
    data = load_dataset(dataset)
    print(f"Size of set is {len(data)}.")

    for query in data:
        q = ""
        for char in query:
            # Skip whitespace
            if char == " ":
                q += char
                continue
            q += char
            print(q)

def get_full_candidates(background_data, prefix, stop = 10):
    candidates = []
    



most_popular_completion(CandidateScenario.NO_SUFFIXES, DataSet.TEST)
