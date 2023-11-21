FdMap.prototype.render = function render(chartXml){
	this.fusionMap.setDataXML(chartXml);
	this.fusionMap.render(this.id);
}
/**
 * fusionMap : the FusionMap variable
 * id : the id of the html where the chart will be renderered
 * chartXml : the inition chartXml
 */
function FdMap(fusionMap, id, chartXml){
	this.id = id;
	this.fusionMap = fusionMap;
	
	if (chartXml != undefined){
		this.render(chartXml);
	}
	
}

FdVanillaMap.prototype.render = function render(chartXml){
	this.fusionMap.setDatas(chartXml);
	this.fusionMap.draw();
}
/**
 * fusionMap : the FusionMap variable
 * id : the id of the html where the chart will be renderered
 * chartXml : the inition chartXml
 */
function FdVanillaMap(fusionMap, id, chartXml){
	this.id = id;
	this.fusionMap = fusionMap;
	
	if (chartXml != undefined){
		this.render(chartXml);
	}
	
}

function Lon2Merc(lon) {
    return 20037508.34 * lon / 180;
}
 
function Lat2Merc(lat) {
    var PI = 3.14159265358979323846;
    lat = Math.log(Math.tan( (90 + lat) * PI / 360)) / (PI / 180);
    return 20037508.34 * lat / 180;
}

function makeIcon(icons) {
	var size = new OpenLayers.Size(icons[1],icons[2]);
	var offset = new OpenLayers.Pixel(-(size.w*icons[3]), -(size.h*icons[4]));
	var icon = new OpenLayers.Icon(icons[0],size,offset);
	return icon;
}

function drawPolygon(coordinates,style) {
	var points = createPointsArrayFromCoordinates(coordinates);

	var linearRing = new OpenLayers.Geometry.LinearRing(points);
	var polygon = new OpenLayers.Geometry.Polygon([linearRing]);
	var vector = new OpenLayers.Feature.Vector(polygon,null,style);
	
	return vector;
}

function createPointsArrayFromCoordinates(coordinates) {
	var points = new Array();
	for (var i=0;i<coordinates.length;++i) {
		var lonlat = new OpenLayers.LonLat(coordinates[i][0],coordinates[i][1]).transform(new OpenLayers.Projection("EPSG:4326"),new OpenLayers.Projection("EPSG:900913"))
		points.push(new OpenLayers.Geometry.Point(lonlat.lon,lonlat.lat))
	}
	return points;	
}