import requests
import pandas as pd
import googlemaps 
import json

api_key = "AIzaSyAKmKRd6i8I_4PVNtZqBmj4hnGAaOy1RgQ"
server_ip = "localhost:3000"


# result = eval(json.dumps(geocode_result))
# print(result)

def remove_non_ascii(string_with_nonASCII):
    encoded_string = string_with_nonASCII.encode("ascii", "ignore")
    decode_string = encoded_string.decode()
    return decode_string

def read_cities_list():
    df = pd.read_csv('../data/cities_list.csv')
    return df

def get_coordinates(city_name):
    geocode_result = gmaps.geocode(city_name)
    latitude = geocode_result[0]["geometry"]["location"]["lat"]
    longitude = geocode_result[0]["geometry"]["location"]["lng"]
    formatted_address = geocode_result[0]["formatted_address"]
    return latitude, longitude, formatted_address

cities_df = read_cities_list()

gmaps = googlemaps.Client(key=api_key)

city_names = []
for ei in cities_df.index:
    city = cities_df.loc[ei, 'City']
    state = cities_df.loc[ei, 'State']
    city_name = city + "," + state + ", IN"
    city_names.append(city_name)

df = pd.DataFrame(city_names, columns=['City'])
# df = df.head(2)
dsize = df.shape[0]
df = df.tail(dsize - 772)
# print(df.head(10))
# df = df.head(2)
latitude_array = []
longitude_array = []
formatted_address_array = []
for ei in df.index:
    city_name = df.loc[ei, 'City']
    latitude, longitude, formatted_address = get_coordinates(city_name)
    formatted_address = remove_non_ascii(formatted_address)
    print(ei, latitude, longitude, formatted_address)
    latitude_array.append(latitude)
    longitude_array.append(longitude)
    formatted_address_array.append(formatted_address)
df['latitude'] = latitude_array
df['longitude'] = longitude_array
df['city_name'] = formatted_address_array
df = df[['latitude', 'longitude', 'city_name']]
df.to_csv('location_results2.csv')