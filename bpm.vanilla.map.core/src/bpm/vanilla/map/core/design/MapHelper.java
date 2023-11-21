package bpm.vanilla.map.core.design;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.remote.MdmRemote;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.components.ged.GedLoadRuntimeConfig;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Geometry;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.micromata.opengis.kml.v_2_2_0.MultiGeometry;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Point;
import de.micromata.opengis.kml.v_2_2_0.Polygon;

public class MapHelper {

	public static Map<String, MapZone> getMapZone(MapDataSet dataset, IVanillaAPI api, IMapDefinitionService mapService, int metadataId) throws Exception {
		Map<String, MapZone> zones = getMapZone(dataset, api, mapService);
		for(MapZone z : zones.values()) {
			if(z.getMetadataMappings().keySet().contains(metadataId)) {
				z.setGeoId(z.getMetadataMappings().get(metadataId).getMetadataElementId());
			}
		}
		return zones;
	}
	
	public static Map<String, MapZone> getMapZone(MapDataSet dataset, IVanillaAPI api, IMapDefinitionService mapService) throws Exception {
		try {
			Map<String, MapZone> result = new HashMap<String, MapZone>();
			MapDataSource ds = dataset.getDataSource();
			
			//getMetadataMapping
			Map<String, Map<Integer, ZoneMetadataMapping>> mappings = mapService.getMetadataMappings(dataset.getId());

			if(ds instanceof MapDatasourceKML) {

				MdmRemote mdmRemote = new MdmRemote(api.getVanillaContext().getLogin(), api.getVanillaContext().getPassword(), api.getVanillaContext().getVanillaUrl());
				RemoteGedComponent ged = new RemoteGedComponent(api.getVanillaContext().getVanillaUrl(), api.getVanillaContext().getLogin(), api.getVanillaContext().getPassword());

				Contract contract = mdmRemote.getContract(((MapDatasourceKML) ds).getContractId());
				GedDocument gedDoc = ged.getDocumentDefinitionById(contract.getDocId());
				int userId = api.getVanillaSecurityManager().getUserByLogin(api.getVanillaContext().getLogin()).getId();
				GedLoadRuntimeConfig config = new GedLoadRuntimeConfig(gedDoc, userId);

				if(contract.getVersionId() != null) {
					DocumentVersion docVersion = ged.getDocumentVersionById(contract.getVersionId());
					config = new GedLoadRuntimeConfig(gedDoc, userId, docVersion.getVersion());
				}
				InputStream is = ged.loadGedDocument(config);

				Kml kml = Kml.unmarshal(is);
				Document document;
				try {
					document = (Document) ((Document) kml.getFeature()).getFeature().get(0);
				} catch(Exception e) {
					document = ((Document) kml.getFeature());
				}
				Iterator<Feature> rowIterator = null;
				if(document.getFeature().get(0) instanceof Folder) {
					List<Feature> features = new ArrayList<Feature>();
					for(Feature f : document.getFeature()) {
						features.addAll(((Folder) f).getFeature());
					}
					rowIterator = features.iterator();
				}
				else {
					rowIterator = document.getFeature().iterator();
				}
				while(rowIterator.hasNext()) {
					Feature feature = rowIterator.next();
					if(feature instanceof Placemark) {
						Placemark placemark = (Placemark) feature;

						String name = placemark.getName();
						if(result.get(name) == null) {
							MapZone zone = new MapZone();
							try {
								for(ZoneMetadataMapping mapping : mappings.get(name).values()) {
									zone.getMetadataMappings().put(mapping.getMetadataId(), mapping);
								}
							} catch(Exception e) {
							}
							zone.setGeoId(name);
							result.put(name, zone);
						}
						MapZone zone = result.get(name);
						Geometry g = placemark.getGeometry();
						if(g instanceof Point) {
							zone.setMapType("point");
							Coordinate coord = ((Point) g).getCoordinates().get(0);
							zone.addPoint(String.valueOf(coord.getLatitude()), String.valueOf(coord.getLongitude()));
						}
						else if(g instanceof LineString) {
							zone.setMapType("line");
							List<Coordinate> coords = ((LineString) g).getCoordinates();
							for(Coordinate coord : coords) {
								zone.addPoint(String.valueOf(coord.getLatitude()), String.valueOf(coord.getLongitude()));
							}
						}
						else if(g instanceof Polygon) {
							zone.setMapType("polygon");
							List<Coordinate> coords = ((Polygon) g).getOuterBoundaryIs().getLinearRing().getCoordinates();
							for(Coordinate coord : coords) {
								zone.addPoint(String.valueOf(coord.getLatitude()), String.valueOf(coord.getLongitude()));
							}
						}
						else if(g instanceof MultiGeometry) {
							zone.setMapType("polygon");
							List<Coordinate> coords = ((Polygon) ((MultiGeometry)g).getGeometry().get(0)).getOuterBoundaryIs().getLinearRing().getCoordinates();
							for(Coordinate coord : coords) {
								zone.addPoint(String.valueOf(coord.getLatitude()), String.valueOf(coord.getLongitude()));
							}
						}
					}
				}

			}
			else if(ds instanceof MapDatasourceWFS) {
				String url = ((MapDatasourceWFS) ds).getUrl();
				String wfsUrl = url + "?SERVICE=WFS&REQUEST=GetFeature&typename=" + ((MapDatasourceWFS) ds).getLayer() + "&VERSION=1.1.0&outputFormat=geojson";
				String json;
				JSONObject root;
				try {
					json = jsonGetRequest(wfsUrl);
					root = new JSONObject(json);
				} catch(Exception e1) {
					wfsUrl = url + "?SERVICE=WFS&REQUEST=GetFeature&typename=" + ((MapDatasourceWFS) ds).getLayer() + "&VERSION=1.1.0&outputFormat=application/json";
					json = jsonGetRequest(wfsUrl);
					root = new JSONObject(json);
				}
				
				JSONArray features = root.getJSONArray("features");
				for(int i = 0 ; i < features.length() ; i++) {
					JSONObject feat = features.getJSONObject(i);
					
					//get id
					JSONObject props = feat.getJSONObject("properties");
					String id;
					try {
						id = props.getString(((MapDatasourceWFS) ds).getField());
						if(id == null) {
							id = feat.getString(((MapDatasourceWFS) ds).getField());
						}
					} catch(Exception e) {
						id = feat.getString(((MapDatasourceWFS) ds).getField());
					}
					
					//get geom
					JSONObject geom = feat.getJSONObject("geometry");
					String type = geom.getString("type");
					
					MapZone zone = new MapZone();
					try {
						for(ZoneMetadataMapping mapping : mappings.get(id).values()) {
							zone.getMetadataMappings().put(mapping.getMetadataId(), mapping);
						}
					} catch(Exception e) {
					}
					zone.setGeoId(id);
					zone.setMapType("WFS");
					
					result.put(id, zone);
					
					if(type.equalsIgnoreCase("point")) {
						JSONArray coords = geom.getJSONArray("coordinates");
						result.get(id).addPoint(coords.getString(1), coords.getString(0));
					}
					else if(type.equalsIgnoreCase("MultiPolygon")) {
						JSONArray coords = geom.getJSONArray("coordinates");
						JSONArray coords2 = coords.getJSONArray(0);
//						JSONArray coords3 = coords2.getJSONArray(0);
						JSONArray pts = coords2.getJSONArray(0);
						for(int j = 0 ; j < pts.length() ; j++) {
							JSONArray p = pts.getJSONArray(j);
							result.get(id).addPoint(p.getString(1), p.getString(0));
						}
					}
					else {
						JSONArray coords = geom.getJSONArray("coordinates");
						JSONArray pts = coords.getJSONArray(0);
						for(int j = 0 ; j < pts.length() ; j++) {
							JSONArray p = pts.getJSONArray(j);
							result.get(id).addPoint(p.getString(1), p.getString(0));
						}
					}
				}
			}
			else {
				VanillaJdbcConnection valueConnection = null;
				try {
					valueConnection = ConnectionManager.getInstance().getJdbcConnection(ds.getUrl(), ds.getLogin(), ds.getMdp(), ds.getDriver());

					VanillaPreparedStatement statement = valueConnection.prepareQuery(dataset.getQuery());

					ResultSet resultSet = statement.executeQuery();

					while(resultSet.next()) {

						String zone = resultSet.getString(dataset.getIdZone());
						String latitude = resultSet.getString(dataset.getLatitude());
						String longitude = resultSet.getString(dataset.getLongitude());

						MapZone val = null;
						if(result.get(zone) != null) {
							val = result.get(zone);
						}
						else {
							val = new MapZone();
							try {
								for(ZoneMetadataMapping mapping : mappings.get(zone).values()) {
									val.getMetadataMappings().put(mapping.getMetadataId(), mapping);
								}
							} catch(Exception e) {
							}
							val.setMarker(dataset.getMarkerUrl());
							val.setMinSize(dataset.getMarkerSizeMin());
							val.setMaxSize(dataset.getMarkerSizeMax());
							val.setMapType(dataset.getType());
							val.setGeoId(zone);
							result.put(zone, val);
						}
						if(dataset.getType().equals("point")) {
							if(val.getLatitudes().size() > 0) {
								continue;
							}
						}

						val.addPoint(latitude, longitude);

					}

					resultSet.close();
					statement.close();

					ConnectionManager.getInstance().returnJdbcConnection(valueConnection);
				} catch(Exception e) {
					if(valueConnection != null) {
						ConnectionManager.getInstance().returnJdbcConnection(valueConnection);
					}
					e.printStackTrace();
					throw e;
				}
			}

			return result;
		} catch(Exception e) {
			
			e.printStackTrace();
			throw e;
		}
	}

	private static String streamToString(InputStream inputStream) {
		String text = new Scanner(inputStream, "UTF-8").useDelimiter("\\Z").next();
		return text;
	}

	public static String jsonGetRequest(String urlQueryString) throws Exception {
		String json = null;
		try {
			URL url = new URL(urlQueryString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("charset", "utf-8");
			connection.connect();
			InputStream inStream = connection.getInputStream();
			json = streamToString(inStream); // input stream to string
		} catch(Exception ex) {
			throw ex;
		}
		return json;
	}
	
	public static String generateGeojson(Map<String, MapZone> zones, String projection) throws Exception {
		String json = null;

		// main jsonobject
		JSONObject main = new JSONObject();
		main.put("type", "FeatureCollection");

		// map properties
		JSONObject mapProps = new JSONObject();
		mapProps.put("name", "EPSG:4326");
		JSONObject crs = new JSONObject();
		crs.put("type", "name");
		crs.put("properties", mapProps);
		main.put("crs", crs);

		JSONArray features = new JSONArray();
		for (MapZone line : zones.values()) {
			if(line.getProperties().isEmpty()) {
				continue;
			}
			JSONObject feature = new JSONObject();
			feature.put("type", "Feature");

			JSONObject props = new JSONObject();

			for(String prop : line.getProperties().keySet()) {
				props.put(prop, line.getProperties().get(prop));
			}
			JSONObject geom = new JSONObject();
			if(line.getLatitudes().size() > 1) {
				geom.put("type", "Polygon");
				JSONArray coordinates = new JSONArray();
				JSONArray coord = new JSONArray();
				coord.put(0);
				coord.put(0);
				for(int  i = 0 ; i < line.getLatitudes().size() ; i++) {
					coord = new JSONArray();
					coord.put(line.getLongitudes().get(i));
					coord.put(line.getLatitudes().get(i));

					coordinates.put(coord);
				}
				JSONArray extraCoord = new JSONArray();
				extraCoord.put(coordinates);

				geom.put("coordinates", extraCoord);
			}
			else {
				geom.put("type", "Point");
				JSONArray coordinates = new JSONArray();
				coordinates.put(Double.parseDouble(line.getLongitudes().get(0)));
				coordinates.put(Double.parseDouble(line.getLatitudes().get(0)));
				
				geom.put("coordinates", coordinates);
			}
			feature.put("geometry", geom);
			
			feature.put("properties", props);
			features.put(feature);
		}

		main.put("features", features);
		json = main.toString();
		System.out.println(json);
		return json;
	}

}
