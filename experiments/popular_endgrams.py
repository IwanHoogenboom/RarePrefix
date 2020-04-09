from background_suffixes import get_background_endgrams
from collections import Counter

end_grams = get_background_endgrams(directory="../")
one_gram = Counter(el for el in end_grams.elements() if len(el.split()) == 1)
two_grams = Counter(el for el in end_grams.elements() if len(el.split()) == 2)
three_grams = Counter(el for el in end_grams.elements() if len(el.split()) == 3)

print("-- Most popular query suffixes --")
print("Top suffixes")
for i in one_gram.most_common(12):
    print(i)

print("Top 2-word suffixes")
for i in two_grams.most_common(12):
    print(i)

print("Top 3-word suffixes")
for i in three_grams.most_common(12):
    print(i)