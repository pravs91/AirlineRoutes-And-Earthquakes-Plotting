package module6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;
import processing.core.PGraphics;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Praveen Sankaranarayanan
 */
public class AirportMap extends PApplet {
	
	UnfoldingMap map;
	private List<Marker> airportList;
	List<Marker> routeList;

	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	private HashMap<Integer, Location> airports;
	private HashMap<String, Marker> airportMarkerMap;
	PGraphics buffer;
	public void setup() {
		// setting up PApplet
		size(1024,600, OPENGL);
		buffer = createGraphics(1024,600,OPENGL);
		// setting up map and default events
		map = new UnfoldingMap(this, 125, 50, 1000, 550, new Google.GoogleMapProvider());
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data using provided method
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<Marker>();
		airports = new HashMap<Integer, Location>();
		airportMarkerMap = new HashMap<String, Marker>();
		// create markers from features
		for(PointFeature feature : features) {
			AirportMarker m = new AirportMarker(feature);
	
			m.setRadius(5);
			m.setId(feature.getId());
			airportList.add(m);
			
			// put airport in hashmap with OpenFlights unique id for key
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
			airportMarkerMap.put(feature.getId(), m);
		}
		
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
		
			//System.out.println(sl.getProperties());
			sl.setHidden(true);
			//UNCOMMENT TO SEE ALL ROUTES
			routeList.add(sl);
		}
		
		
		
		//UNCOMMENT TO SEE ALL ROUTES
		map.addMarkers(routeList);
		
		map.addMarkers(airportList);
		
	}
	
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		selectMarkerIfHover(airportList);
		//loop();
	}
	
	private void selectMarkerIfHover(List<Marker> markers)
	{
		// Abort if there's already a marker selected
		if (lastSelected != null) {
			return;
		}
		
		for (Marker m : markers) 
		{
			CommonMarker marker = (CommonMarker)m;
			if (marker.isInside(map,  mouseX, mouseY)) {
				lastSelected = marker;
				marker.setSelected(true);
				return;
			}
		}
	}

	public void mouseClicked(){
		if(lastClicked != null){
			for(Marker marker: airportList){
				marker.setHidden(false);
			}
			for(Marker route: routeList){
				route.setHidden(true);
			}
			lastClicked = null;
		}
		processAirportClick(airportList);
	}
	
	public void processAirportClick(List<Marker> markers){
		String airportId;
		List<Marker> destList = new ArrayList<Marker>();
		for(Marker airport: markers){
			if(!airport.isHidden() && airport.isInside(map, mouseX, mouseY)){
				lastClicked = (AirportMarker) airport;
				airportId = airport.getId();
				System.out.println(airportId);
				// hide all other airports
				for(Marker m: markers){
					if(m != lastClicked)
						m.setHidden(true);
				}
				for(Marker route:routeList){
					//System.out.println(route.getProperties());
					if(airportId.equals(route.getStringProperty("source")) || 
							airportId.equals(route.getStringProperty("destination"))){
						route.setHidden(false);
						Marker source = airportMarkerMap.get(route.getStringProperty("source"));
						Marker dest = airportMarkerMap.get(route.getStringProperty("destination"));
						double distance = source.getDistanceTo(dest.getLocation());
						//System.out.println(distance);
						if(distance < 1000.0){
							route.setStrokeWeight(3);
							route.setColor(color(0,204,0));
						}
						else if(distance >= 1000.0 && distance < 3000.0){
							route.setStrokeWeight(2);
							route.setColor(color(0,128,255));
						}
						else
							route.setColor(color(255,0,0));
					}
					if(airportId.equals(route.getStringProperty("source"))){
						airportMarkerMap.get(route.getStringProperty("destination")).setHidden(false);
					}
				}
				break;
			}
		}
	}
	
	public void draw() {
		background(0);
		map.draw();
		if(lastClicked != null){
			drawKey();
		}
	}
	
	public void drawKey(){
		fill(255,255,255);
		rect(10,50,100,200);
		
		stroke(0,204,0);
		line(20,85,35,75);
		
		stroke(0,128,255);
		line(20,105,35,95);
		
		stroke(255,0,0);
		line(20,135,35,125);
		
		fill(0,0,0);
		text("Distance",40,65);
		text("< 1000km",40,85);
		text("1000 - \n 3000km",40,105);
		text(">3000 km",40,135);
	}

}
