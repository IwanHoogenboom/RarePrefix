# -*- coding: utf-8 -*-
"""
Created on Tue Apr  7 11:02:18 2020

@author: iwanh
"""
import pandas as pd
import glob
import os
import string


def normalize(x):
    return str(x).lower().translate(str.maketrans(string.punctuation, ' '*len(string.punctuation)))

def one_word(x):
    if len(str(x.split())) == 1 and not str(x).endswith(' '):
        return ""
    else:
        return str(x)

#Folder with 10 datafiles, MAKE SURE THE README IS NOT IN THIS FOLDER ANYMORE.
path = './data/'
all_files = glob.glob(os.path.join(path, "*.txt"))

#load every file and concat, there are values in id and url column of file 8
#since we purge them ignore the warning.
df_from_each_file = (pd.read_csv(f, sep='\t') for f in all_files)
df_each_dropped = (df.drop(columns=['AnonID', 'ItemRank', 'ClickURL']) for df in df_from_each_file)
c_df = pd.concat(df_each_dropped, ignore_index=True)

# Normalize all queries by removing punctuation and lowercase.
c_df['Query'] = c_df['Query'].apply(one_word)
c_df['Query'] = c_df['Query'].apply(normalize)

# Drop all empty values or whitespaces.
c_df = c_df[c_df["Query"] != ""]
c_df = c_df[c_df["Query"] != " "]

# Print the info.
print(c_df.info())

##generate datasets
# PRINT OOK EVEN QUERY TIME
background = c_df[(c_df['QueryTime'] >= '2006-03-01') & (c_df['QueryTime'] <= '2006-04-30')]
train = c_df[(c_df['QueryTime'] >= '2006-05-01') & (c_df['QueryTime'] < '2006-05-15')]
validation = c_df[(c_df['QueryTime'] >= '2006-05-15') & (c_df['QueryTime'] < '2006-05-22')]
test = c_df[(c_df['QueryTime'] >= '2006-05-22') & (c_df['QueryTime'] < '2006-05-29')]

##drop date aswell
background = background.drop(columns=['QueryTime'])
train = train.drop(columns=['QueryTime'])
val = validation.drop(columns=['QueryTime'])
test = test.drop(columns=['QueryTime'])

# Drop duplicates.
background = background.drop_duplicates(subset='Query', keep='first')
train = train.drop_duplicates(subset='Query', keep='first')
val = val.drop_duplicates(subset='Query', keep='first')
test = test.drop_duplicates(subset='Query', keep='first')

print("BACKGROUND")
background.info()
print("TRAIN")
train.info()
print("VALIDATION")
val.info()
print("TEST")
test.info()

#write the datasets
background.to_csv('background.txt', sep='\t', index=False)
train.to_csv('train.txt', sep='\t', index=False)
val.to_csv('validation.txt', sep='\t', index=False)
test.to_csv('test.txt', sep='\t', index=False)
