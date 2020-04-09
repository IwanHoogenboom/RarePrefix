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

def load_dataset(set: DataSet):
    file_name = ""
    if set is DataSet.TRAINING:
        file_name = "training.txt"
    elif set is DataSet.VALIDATION:
        file_name = "validation.txt"
    elif set is DataSet.TEST:
        file_name =  "test.txt"
    else:
        raise Exception(f"Dataset not found: {set}")

    if not os.path.isfile(file_name):
        raise Exception(f"{file_name} is not available. Please run the `parse_dataset.py` script.")

    print(f"Now loading {file_name}")

    f = open(file_name, 'r')
    data = f.readlines()
    f.close()

    return data[1:]


def generate_candidates(scenario: CandidateScenario, dataset: DataSet):
    data = load_dataset(dataset)


generate_candidates(CandidateScenario.NO_SUFFIXES, DataSet.TEST)