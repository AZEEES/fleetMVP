import requests
import pandas as pd
import googlemaps 
import json
import math

api_key = "AIzaSyAKmKRd6i8I_4PVNtZqBmj4hnGAaOy1RgQ"
server_ip = "http://localhost:3000"
min_geocode_diff = 0.05 #6Km
gmaps = googlemaps.Client(key=api_key)


def update_coordinate_data(latitude, longitude, city_name, location_type):
    url = server_ip + "/api/coordinates"
    data = { "latitude" : latitude, "longitude" : longitude, "city_name" : city_name, "location_type" : location_type }
    try:
        resp = requests.post(url, data)
        response = resp.json()
        return response
    except:
        pass

def reverse_geocode(latitude, longitude):
    try:
        reverse_geocode_result = gmaps.reverse_geocode((latitude, longitude))
        city_details = reverse_geocode_result[0]
        address_components = city_details["address_components"]
        formatted_address = city_details["formatted_address"]
        try:
            city = "NA"
            for address in address_components:
                address_type = address["types"]
                if("administrative_area_level_1" in address_type) and ("political" in address_type):
                    state = address["long_name"]
                if("administrative_area_level_2" in address_type) and ("political" in address_type):
                    district = address["long_name"]
                if("locality" in address_type) and ("political" in address_type):
                    city = address["long_name"]
            if(city!="NA"):
                formatted_address = city + ", " + district + ", " + state
                location_type = "L0"
            else:
                formatted_address = district + ", " + state
                location_type = "L1"
        except Exception as e:
            print(e)
        city_name = formatted_address
        update_coordinate_data(latitude, longitude, city_name, location_type)
    except Exception as e:
        city_name = "NAN"
    return city_name

def get_untagged_cities():
    url = server_ip + "/api/location/untagged"
    try:
        resp = requests.get(url)
        return resp.json()
    except Exception as e:
        return None

def tag_city(location, city_name):
    try:
        id = location['_id']
        location['city_name'] = city_name
        location = json.dumps(location)
        url = server_ip + "/api/location/update"
        data = {"id" : id, "location" : location}
        resp = requests.post(url, data)
        response = resp.json()
        return 0
    except Exception as e:
        print(e)
        return 1

def get_approx_distance(lat1, lat2, long1, long2):
    x = abs(float(lat2) - float(lat1)) * 120
    y = abs(float(long2) - float(long1)) * 120
    try:
        dist = int(math.sqrt( x * x + y * y ))
    except:
        dist = 0.0
    return dist


def get_closest_city_text(latitude, longitude, location_results):
    min_index = 0
    min_distance = 1e6
    for index, location in enumerate(location_results):
        latitude_found = float(location["latitude"])
        longitude_found = float(location["longitude"])
        distance = get_approx_distance(latitude, latitude_found, longitude, longitude_found)
        if(distance < min_distance):
            min_index = index
            min_distance = distance
    city_name = location_results[min_index]["city_name"]
    return city_name, min_distance

def reverse_geocode_city(latitude, longitude):
    url = server_ip + "/api/coordinates/find_all_cities"
    data = {"latitude" : latitude, "longitude" : longitude, "diff" : min_geocode_diff}
    try:
        resp = requests.post(url, data)
        cities_list = resp.json()
    except Exception as e:
        cities_list = []
    if(len(cities_list)>=1):
        print("city found")
        city_name, distance = get_closest_city_text(latitude, longitude, cities_list)
    else:
        print("city not found, moving to google")
        city_name = reverse_geocode(latitude, longitude)
        distance = 0
    return city_name, distance

untagged_cities = get_untagged_cities()

for untagged_city in untagged_cities:
    city_name, distance = reverse_geocode_city(untagged_city['latitude'], untagged_city['longitude'])
    if(distance==0):
        city_name = city_name
    else:
        city_name = str(distance) + " KM from " + city_name
    tag_city(untagged_city, city_name)
    print(city_name)