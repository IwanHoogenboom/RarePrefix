import os.path
from collections import Counter
import pickle
import pygtrie as trie
import datrie
import string

# Computes end-grams of the background data.
def get_background_endgrams(directory = ""):
    background_data_file = directory + "background.txt"
    background_ngrams_file = directory + "background_ngrams.txt"

    if not os.path.isfile(background_data_file):
        raise Exception("Background data is not available. Please run the `parse_dataset.py` script.")

    # Get counter if already computed.
    if os.path.isfile(background_ngrams_file):
        print("N-grams were already computed, now loading the file.")
        return pickle.load(open(background_ngrams_file, 'rb'))

    print("Generating n-grams and storing to file.")

    # Load data
    f = open(background_data_file, 'r')
    background_data = f.read().splitlines()
    f.close()

    # Compute n-grams of background_data.
    counter = Counter()
    j = 0
    for query in background_data:
        j += 1
        if j % 100000 == 0:
            prog = "{:.2f}".format(j / len(background_data) * 100)
            print(f"{prog}%")
        counter.update(compute_end_grams(query))

    with open(background_ngrams_file, 'wb') as outputfile:
        pickle.dump(counter, outputfile)

    return counter

# Computes end-grams of the background data.
def get_background_popularity(directory = ""):
    background_data_file = directory + "background.txt"
    background_popularity_file = directory + "background_popularity.txt"

    if not os.path.isfile(background_data_file):
        raise Exception("Background data is not available. Please run the `parse_dataset.py` script.")

    # Get counter if already computed.
    if os.path.isfile(background_popularity_file):
        print("Popular queries were already computed, now loading the file.")
        return pickle.load(open(background_popularity_file, 'rb'))

    print("Generating popularity scores and storing to file.")

    # Load data
    f = open(background_data_file, 'r')
    background_data = f.read().splitlines()
    f.close()

    # Compute n-grams of background_data.
    counter = Counter()
    j = 0
    for query in background_data:
        j += 1
        if j % 100000 == 0:
            prog = "{:.2f}".format(j / len(background_data) * 100)
            print(f"Building popularity counter: {prog}%")
        counter.update({query})

    with open(background_popularity_file, 'wb') as outputfile:
        pickle.dump(counter, outputfile)

    return counter

def get_prefix_tree(counter, filename):

    # Get tree if already computed.
    if os.path.isfile(filename):
        print(f"{filename} was already computed, now load the file.")
        return datrie.Trie.load(filename)

    tree = datrie.Trie(string.ascii_lowercase + string.whitespace + string.digits)

    j = 0
    counter_common = counter.most_common()
    for el, count in counter_common:
        j += 1
        if j % 100000 == 0:
            prog = "{:.2f}".format(j / len(counter_common) * 100)
            print(f"Build prefix-tree: {prog}%")
        tree[el] = count


    # Store tree.
    tree.save(filename)

    return tree

# For a query computes all end-grams.
def compute_end_grams(query):
    query_split = query.split()
    return {' '.join(query_split[-i:]) for i in range(1, len(query_split) + 1)}
