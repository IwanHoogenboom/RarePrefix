from background_processing import get_background_popularity
from collections import Counter

queries = get_background_popularity(directory="../")

print("-- Most popular queries --")
print(len(queries.most_common()))
for i in queries.most_common(12):
    print(i)

