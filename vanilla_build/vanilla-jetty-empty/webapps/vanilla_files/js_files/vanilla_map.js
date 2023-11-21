function renderMap(divContainer, originLat, originLong, zoom, boundLeft, boundBottom, boundRight, boundTop, values, features) {
	var fromProjection = new OpenLayers.Projection("EPSG:4326");
	var toProjection = new OpenLayers.Projection("EPSG:900913");
	
	//create map element
	var extent = new OpenLayers.Bounds(boundLeft,boundBottom,boundRight,boundTop).transform( fromProjection, toProjection);
	var controls = [
		new OpenLayers.Control.Navigation(),
		new OpenLayers.Control.PanZoomBar(),
		new OpenLayers.Control.MousePosition(),
		new OpenLayers.Control.LayerSwitcher(),
		new OpenLayers.Control.KeyboardDefaults()
	];
	var	proj = new OpenLayers.Projection(fromProjection);
	var map1 = new OpenLayers.Map({
		div: divContainer,
		maxExtent : extent,
		controls: controls,
		projection: proj,
		maxResolution: 256
	});
	var layers = [];
	
	//Osm layer
	layers.push(new OpenLayers.Layer.OSM());
	
	//Vector layer
	var layer_vectors = new OpenLayers.Layer.Vector("vectorLayer", { displayInLayerSwitcher: true } );
	for(i = 0 ; i < features.length ; i++) {
		var zone = features[i];
		var value = values[i];
		var points = [];
		for(var j = 0 ; j < zone.length ; j++) {
			var p = new OpenLayers.Geometry.Point(zone[j][0], zone[j][1]).transform(fromProjection, toProjection);
			points.push(p);
		}
		var ring = new OpenLayers.Geometry.LinearRing(points);
		var poly = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Polygon([ring]));
		
		//put the style
		if(value > 0) {
			poly.style = {fill: true, fillColor: "#85c879", fillOpacity: 0.5};
		}
		else if(value < 0) {
			poly.style = {fill: true, fillColor: "#f27742", fillOpacity: 0.5};
		}
		else {
			poly.style = {fill: true, fillColor: "#d9c16f", fillOpacity: 0.5};
		}
		
		layer_vectors.addFeatures(poly);
	}
	
	layers.push(layer_vectors);
	
	//display map
	map1.addLayers(layers);
	
	map1.setCenter(new OpenLayers.LonLat(originLong, originLat).transform(fromProjection, toProjection));
	
	map1.zoomToExtent(extent);
}