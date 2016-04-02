# Airline Routes And Earthquakes Plotting

This project consists of 2 parts:

* Plotting cities and earthquakes around the world
* Plotting cities and airline routes between them, color coded based on distance

The primary purpose of this project is to develop an object-oriented design using concepts like inheritance and polymorphism.

## Libraries Used
* [UnfoldingMaps](http://unfoldingmaps.org/javadoc/)
* [Processing](https://processing.org/reference/)

## Description of Classes
Earthquake data was obtained from this USGS [link](http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom).

* CommonMarker - abstract super class that has methods common to all Markers
* CityMarker - extends from CommonMarker. Used to mark cities on the map
* AirportMarker - extends from CommonMarker. Used to mark airports on the map
* EarthquakeMarker - extends from CommonMarker. Used to mark earthquakes on the map
* LandQuakeMarker - extends from EarthquakeMarker. Used to mark earthquakes on land
* OceanQuakeMarker - extends from EarthquakeMarker. Used to mark earthquakes on ocean
* EarthquakeCityMap - extends from processing.PApplet. This is the main applet used to display the interactive map with all City and Earthquake markers.
* AirportMap - extends from processing.PApplet. This applet is used to display an interactive map with Airport markers and airline routes

## Earthquake plot features
* Hovering over an earthquakes displays its location and magnitude
* Clicking on an earthquake displays the cities that could be affected by it (threat radius)
* Hovering over a city displays its name, country and population
* Clicking on a city displays the earthquakes around it
* 
## Airport plot features
* Hovering over an airport displays its name, airport code and city
* Clicking an airport displays all routes out of it

Screenshots can be viewed in the Output-Screenshots folder.
