import os.path
from nltk import ngrams
from collections import Counter
from functools import reduce
import pickle
import pickle

lengt = 0
def get_background_data(top = -1):
    global lengt
    background_data_file = "background.txt"

    if not os.path.isfile(background_data_file):
        raise Exception("Background data is not available. Please run the `parse_dataset.py` script.")

    # load data
    f = open(background_data_file, 'r')
    background_data = f.readlines()
    f.close()

    lengt = len(background_data)
    print(lengt)

    ngrams_file = "background_ngrams.txt"
    if os.path.isfile(ngrams_file):
        print("N-grams were already generated, now loading them.")
        # load here and return

    counter = reduce((lambda x, y: x + y), map(lambda q: compute_end_grams(q), background_data))

    f = open(ngrams_file)
    pickle.dump(counter, f)
    if top is -1:
        return counter
    else:
        return counter.most_common(top)

j = 0

# For a query computes all end-grams.
def compute_end_grams(query):
    global j
    global lengt
    j += 1
    if j % 10000 is 0:
        print((j / lengt) * 100)
    query_split = query.split()
    all_ngrams = []
    for i in range(1, len(query_split) + 1):
        all_ngrams.append(' '.join(query_split[-i:]))

    return Counter(all_ngrams)




get_background_data(10)
print(compute_end_grams("bank of america"))

