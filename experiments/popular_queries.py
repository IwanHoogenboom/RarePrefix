from background_processing import get_background_popularity
from collections import Counter

queries = get_background_popularity(directory="../")

print("-- Most popular queries --")
for i in queries.most_common(12):
    print(i)
