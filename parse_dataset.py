# -*- coding: utf-8 -*-
"""
Created on Tue Apr  7 11:02:18 2020

@author: iwanh
"""
import pandas as pd
import glob
import os



#Folder with 10 datafiles, MAKE SURE THE README IS NOT IN THIS FOLDER ANYMORE.
path = './data/'
all_files = glob.glob(os.path.join(path, "*.txt"))

#load every file and concat, there are values in id and url column of file 8
#since we purge them ignore the warning.
df_from_each_file = (pd.read_csv(f, sep='\t') for f in all_files)
df_each_dropped = (df.drop(columns=['AnonID', 'ItemRank' , 'ClickURL']) for df in df_from_each_file)
c_df = pd.concat(df_each_dropped, ignore_index=True)
#
##generate datasets
train = c_df[c_df['QueryTime'] < '2006-05-01']
validation =  c_df[(c_df['QueryTime'] > '2006-04-30') & (c_df['QueryTime'] < '2006-05-15')]
test =  c_df[(c_df['QueryTime'] > '2006-05-14') & (c_df['QueryTime'] < '2006-05-29')]

##drop date aswell
train = train.drop(columns=['QueryTime'])
val = validation.drop(columns=['QueryTime'])
test = test.drop(columns=['QueryTime'])
    
#write the datasets
train.to_csv('train.txt', sep='\t', index=False)
val.to_csv('validation.txt', sep='\t', index=False)
test.to_csv('test.txt', sep='\t', index=False)
