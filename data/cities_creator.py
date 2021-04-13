import pandas as pd

df = pd.read_csv('major_towns.csv')
df = df[['Name of City','State']]
df.to_csv('cities_list.csv', index=False)