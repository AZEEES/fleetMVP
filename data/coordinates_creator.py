import pandas as pd

df = pd.read_csv("./IN/IN.txt", delimiter="\t")
df = df[['name', 'asciiname','latitude','longitude','feature_class', 'feature_code','population']]

# df = df.sort_values('population', ascending=False)
df = df.loc[df['feature_class']=='P']
df = df[['name', 'asciiname','latitude','longitude','feature_code','population']]
print(df[df.asciiname=='Jamshedpur'])
# print(df.shape)

# print(df.head())
# df.to_csv('generated.csv')