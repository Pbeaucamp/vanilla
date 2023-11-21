var globalMap;

function loadMirrors(divContainer, JsonData) {
	var data = JSON.parse(JsonData);
	var originLat = data.lat;
	var originLong = data.long;
	var zoom = data.zoom;
	var boundLeft = data.minLong;
	var boundBottom = data.minLat;
	var boundRight = data.maxLat;
	var boundTop = data.maxLong;
	var content = data.mirrors;
	
	document.getElementById(divContainer).innerHTML = "";

	var fromProjection = new ol.proj.Projection("EPSG:4326");
	var toProjection = new ol.proj.Projection("EPSG:900913");
	//var	proj = new ol.proj.Projection(fromProjection);
	
	var layers = [];
	var mirror_feature = [];
	var preselected_feature = new ol.Feature();
	
	//calcul de l'extent : A REVOIR
	//var extentValue = new ol.extent.boundingExtent([boundLeft,boundBottom,boundRight,boundTop]);
	var extentValue = [boundLeft,boundBottom,boundRight,boundTop];
	var tfn = ol.proj.getTransform('EPSG:4326', 'EPSG:900913');
	extentValue = ol.extent.applyTransform(extentValue, tfn);
	
	//definition des controles
	var controls = [
		new ol.control.Zoom(),
		new ol.control.ZoomSlider({
			target: divContainer
		})
	];
	

	style_selected = new ol.style.Style({
			image: new ol.style.Circle({
		          radius: 12,
		          stroke: new ol.style.Stroke({
		            color: '#fff'
		          }),
		          fill: new ol.style.Fill({
		            color: '#96d72d'
		          })
		        }),
			text: new ol.style.Text({
			    text: '',
			    scale: 2,
			    offsetX: 60,
			    offsetY: 0,
			    stroke: new ol.style.Stroke({
		            color: '#fff'
		        }),
			    fill: new ol.style.Fill({
		            color: '#000'
		        })
			}),
			zIndex: 99
		});
	
	//Osm layer
//	var layer_osm = new ol.layer.Tile({
//		source: new ol.source.Stamen({
//			layer: 'watercolor'
//	    })
//	});
	var layer_osm = new ol.layer.Tile({
	      source: new ol.source.OSM(),
	      name: "Tile"
	    });
	layers.push(layer_osm);
	
	/* Debut décryptage des valeurs */
	for(i=0; i< content.length; i++){    /* parcours des mirrors */
		var mirror = content[i];
		var id = mirror.id;
		var lat = mirror.lat;
		var long = mirror.long;
		var name = mirror.name;
		var url = mirror.url;
		var state = mirror.state;
		var color;
		if(state == 0){ //unvailable
			color = "#ff4c4c";
		} else if(state == 1){ //available
			color = "#34c4fb";
		} else {
			color = "#34c4fb";//color = "#96d72d"; selected : mais la couleur change au niveau du layer
		}

		var p = new ol.geom.Point(ol.proj.transform([Number(long), Number(lat)], 'EPSG:4326', 'EPSG:900913'));
		
		var mirror_feat = new ol.Feature({
			//geometry: new ol.geom.Circle(p.getCoordinates(), 100000),
			geometry: p,
			id: id,
			url: url.toString(),
        	name: name.toString(),
        	color: color
        });
		
		if(state == 2){
			preselected_feature = mirror_feat;
		}
		
		var style_circle = new ol.style.Style({
			image: new ol.style.Circle({
		          radius: 12,
		          stroke: new ol.style.Stroke({
		            color: '#fff'
		          }),
		          fill: new ol.style.Fill({
		            color: color
		          })
		        }),
			text: new ol.style.Text({
			    text: name.toString(),
			    stroke: new ol.style.Stroke({
		            color: '#fff'
		        }),
			    fill: new ol.style.Fill({
		            color: '#000'
		        })
			})
		});
		//mirror.setStyle(style_circle);
		
		mirror_feature.push(mirror_feat);

				
	}	
			

	if(content.length != 0){
		var source_mirror = new ol.source.Vector({
			features: mirror_feature
		});
		var layer_mirror = new ol.layer.Vector({
			source: source_mirror,
			opacity: 1,
			name: 'mirror',
			style: function(feature, resolution) {
				return [new ol.style.Style({
					image: new ol.style.Circle({
				          radius: 12,
				          stroke: new ol.style.Stroke({
				            color: '#fff'
				          }),
				          fill: new ol.style.Fill({
				            color: feature.get('color')
				          })
				        }),
					text: new ol.style.Text({
					    text: resolution < 5000 ? feature.get('name').toString() : '',
					    offsetX: 30,
					    offsetY: 0,
					    stroke: new ol.style.Stroke({
				            color: '#fff'
				        }),
					    fill: new ol.style.Fill({
				            color: '#000'
				        })
					})
				})];
			}
		});
				
		layers.push(layer_mirror);
	}
	

	var map = new ol.Map({
		layers: layers,
		controls: controls,
		target: divContainer,
		view: new ol.View({
		    zoom: 6,
		    center: ol.proj.transform([originLong, originLat], 'EPSG:4326', 'EPSG:900913')
		})
	});
	
	if(content.length != 0){
		map.getView().fitExtent(source_mirror.getExtent(), map.getSize());
	} else {
		map.setSize([map.getSize()[0], map.getSize()[1]]); //pour avoir le bon rendu : 400 = toolbar + graph panel
	}
	
	selectInteraction = new ol.interaction.Select({
        layers: function(layer) {
        	preselected_feature.setStyle(null); //pour remttre a zero le pré-selectionné
          	return layer.get('selectable') == true;
        },
        style: [style_selected]
  	});

	map.getInteractions().extend([selectInteraction]);
	layer_mirror.set('selectable', true);
	style_selected.getText().setText(preselected_feature.get('name'));
	preselected_feature.setStyle(style_selected);

	map.on("click", function(e) {
	    map.forEachFeatureAtPixel(e.pixel, function (feature, layer) {
	    	clickMirror(feature);
	    	//setTimeout("style_selected.getText().setText(feature.get('name'));",200);
	    	
	    	//style_selected.getText().setText(feature.get('name'));
	    	selectMirror(feature.get('url'));
	    });
	});
//	var displayFeatureInfo = function(pixel) {
//
//		  var feature = map.forEachFeatureAtPixel(pixel, function(feature, layer) {
//			  feature.getStyle().getText().setText(feature.get('name').toString());
//		    return feature;
//		  });
//		  
//		  
//		};
//	
//	map.on('pointermove', function(evt) {
//		  if (evt.dragging) {
//		    return;
//		  }
//		  var pixel = map.getEventPixel(evt.originalEvent);
//		  displayFeatureInfo(pixel);
//		});
	
	globalMap = map;
	
	
}

function resizeMap() {
	globalMap.updateSize();
} 

function selectMirror(url) {
	
	function selectFeature(element, index, array) {
		if(element.get('url') == url){
			//console.log("a[" + index + "] = " + element);
			style_selected.getText().setText(element.get('name'));
			element.setStyle(style_selected);
		} else {
			element.setStyle(new ol.style.Style({
				image: new ol.style.Circle({
			          radius: 12,
			          stroke: new ol.style.Stroke({
			            color: '#fff'
			          }),
			          fill: new ol.style.Fill({
			            color: element.get('color')
			          })
			        }),
				text: new ol.style.Text({
				    text: '',
				    offsetX: 30,
				    offsetY: 0,
				    stroke: new ol.style.Stroke({
			            color: '#fff'
			        }),
				    fill: new ol.style.Fill({
			            color: '#000'
			        })
				})
			}));
		}
	}
	var table = globalMap.getLayers().getArray()[1].getSource().getFeatures().forEach(selectFeature);
} 

