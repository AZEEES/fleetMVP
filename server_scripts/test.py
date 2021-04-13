import requests
import pandas as pd
import googlemaps 
import json
import math

api_key = "AIzaSyAKmKRd6i8I_4PVNtZqBmj4hnGAaOy1RgQ"
gmaps = googlemaps.Client(key=api_key)

reverse_geocode_result = gmaps.reverse_geocode((24.0886, 82.6478))
print(json.dumps(reverse_geocode_result))