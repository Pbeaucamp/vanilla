
/*
 * @latitude : string for latitude coordinate
 * @longitude : string for longitude coordinate
 * @text : string HTML formated for the content displayed in the tooltip
 * @map : instanceof google.maps.Map
 */
function GoogleMarkerTooltip(latitude, longitude, text, map){
	this.latitude = latitude;
	this.longitude = longitude;
	this.text = text;
	this.map = map;
}

GoogleMarkerTooltip.prototype.create = function(){
	//create the position for the marker
	this.position = new google.maps.LatLng(this.latitude , this.longitude);
	
	
	//create the Marker
	this.myMarker = new google.maps.Marker({
		position: myLatlng,
		map: this.map
	});
	
	//create the infoBulle
	var t = this.txt;
	var p = this.position;
	this.infobulle = new google.maps.InfoWindow({
		content: t,
		position: p
		});
	
	//attach listener
	var it = this;
	var tooltp=this.infobulle;
	this.popup = function(){
	alert('yuyu');
	//alert(tooltp);
	//alert(it.map);
	//alert(it.mark);
	//alert(it.latitude);
		tooltp.open(it.map, it.mark);
	}
	
	var m = this.myMarker ;
	var f = this.popup;
	
	google.maps.event.addListener(m, "click", f); 
}