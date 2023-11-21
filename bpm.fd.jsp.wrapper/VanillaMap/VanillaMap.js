function VanillaMap(mapId, swfFile, divId, width, height) {
	this.mapId = mapId;
	this.swfFile = swfFile;
	this.divId = divId;
	this.width = width;
	this.height = height;

}

VanillaMap.prototype.setDatas = function(xml) {
	this.xml = xml;
}

VanillaMap.prototype.draw = function() {	

	var isIE = navigator.appName.indexOf("Microsoft") != -1;

	if(isIE) {
		var div = document.getElementById(this.divId);
		div.innerHTML = '<object id="' + this.mapId + '" type="application/x-shockwave-flash" width="' + this.width + '" height="' + this.height + '" data="' + this.swfFile + '">'+
		'<param name="movie" value="' + this.swfFile + '" />'+
		'<param name="allowScriptAccess" value="always" />'+
		'<param name="flashvars" value=""xmlData=' + this.xml + '" />'+
		'</object>';
	}
	
	else {
	
		var obj = document.createElement("object");
		obj.width = this.width;
		obj.height = this.height;
		obj.id = this.mapId;
		obj.data = this.swfFile;
		
		var p1 = document.createElement("param");
		p1.name = "movie";
		p1.value = this.swfFile;
		
		var p2 = document.createElement("param");
		p2.name = "allowScriptAccess";
		p2.value = "always";
		
		var p3 = document.createElement("param");
		p3.name = "flashvars";
		p3.value = "xmlData=" + this.xml;

		//var emb = document.createElement("embed");
		
		var div = document.getElementById(this.divId);
		
		div.innerHTML = "";
		
		obj.appendChild(p1);
		obj.appendChild(p2);	
		obj.appendChild(p3);
		//obj.appendChild(emb);
		
		div.appendChild(obj);
	
	}
}

FdWMSMap.prototype.render = function render(xml) {
	
	document.getElementById(this.id).innerHTML = '';
	var script = document.getElementById('scriptWmsGen');
	if(script == undefined) {
	
	}
	else {
		script.parentNode.removeChild(script);
	}
	var script = document.createElement('script');
	script.id = 'scriptWmsGen';
	script[(script.innerText===undefined?"textContent":"innerText")] = xml;
	document.documentElement.appendChild(script);
}
function FdWMSMap(id){
	this.id = id;
}