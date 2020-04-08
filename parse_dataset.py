# -*- coding: utf-8 -*-
"""
Created on Tue Apr  7 11:02:18 2020

@author: iwanh
"""
import pandas as pd
import glob
import os



def generate_dateset():
    path = './data/'
    all_files = glob.glob(os.path.join(path, "*.txt"))
    
    #print(all_files[1])
    #df1 = pd.read_csv(all_files[1], sep='\t').drop(columns=['AnonID', 'ItemRank' , 'ClickURL'])
    #print(df1)
    
    df_from_each_file = (pd.read_csv(f, sep='\t') for f in all_files)
    df_each_dropped = (df.drop(columns=['AnonID', 'ItemRank' , 'ClickURL']) for df in df_from_each_file)
    concatenated_df = pd.concat(df_each_dropped, ignore_index=True)
    sorted_df = concatenated_df.sort_values('QueryTime')
    
generate_dateset()


