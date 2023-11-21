/** ----------------- NEW OPEN LAYERS VERSION ----------------------* */

function previewMapPortail(divContainer, originLat, originLong, zoom, boundTop, boundLeft, boundBottom, boundRight, fromProjection, dataSetType, features, marker) {
	document.getElementById(divContainer).innerHTML = "";
	var toProjection = 'EPSG:900913';

	var osm = new ol.layer.Tile({
		source: new ol.source.OSM()
	})
	
	var layersArray = [];
	layersArray.push(osm);
	
	// Vector layer
	if (dataSetType == "polygon") {
		var vector_features = [];
		for (i = 0; i < features.length; i++) {
			var zone = features[i];
			
			var points = [];
			for (var j = 0; j < zone.length; j++) {
				var p = new ol.geom.Point(ol.proj.transform([parseFloat(zone[j][0]), parseFloat(zone[j][1])], fromProjection, toProjection));
				points.push(p.getCoordinates());
			}
			
			var poly = new ol.geom.Polygon([points]);
			poly.style = {
				fill : true,
				fillColor : "#3399FF",
				fillOpacity : 0.5
			}; // Modifer la couleur important (bleu)
			
			var featurePolygon = new ol.Feature({
				geometry: poly
			});
			
			vector_features.push(featurePolygon);
		}

		var layer = new ol.layer.Vector({
			source: new ol.source.Vector({
				features: vector_features
			})
		});
		layersArray.push(layer);
	}
	// Line
	else if (dataSetType == "line") {
		var vector_features = [];
		for (i = 0; i < features.length; i++) {
			var zone = features[i];
			
			var points = [];
			for (var j = 0; j < zone.length; j++) {
				var p = new ol.geom.Point(ol.proj.transform([parseFloat(zone[j][0]), parseFloat(zone[j][1])], fromProjection, toProjection));
				points.push(p.getCoordinates());
			}
			
			var line = new ol.Geometry.LineString(points);
			line.style = {
				strokeColor : "#3399FF",
				strokeWidth : 5
			}; // Modifer la couleur important (bleu)

			var featureLine = new ol.Feature({
				geometry: line
			});
			
			vector_features.push(featureLine);
		}

		var layer = new ol.layer.Vector({
			source: new ol.source.Vector({
				features: vector_features
			})
		});
		layersArray.push(layer);
	}
	// point
	else {
		var vector_features = [];
		for (i = 0; i < features.length; i++) {
			var point = features[i][0];
			var lonlat = point.toString().split(",");
			
			if (lonlat[0] != null && lonlat[0] != "" && lonlat[1] != null && lonlat[1] != "") {
				var marker = new ol.Feature({
				    geometry: new ol.geom.Point(ol.proj.transform([parseFloat(lonlat[0]), parseFloat(lonlat[1])], fromProjection, toProjection))
				});
				//TODO: Support des markers custom, ne fonctionne pas pour l'instant
				// specific style for that one point
//				marker.setStyle(new ol.style.Style({
//				  image: new ol.style.Icon({
//						src: marker,
//						size: [21, 25]
//				  })
//				}));
				vector_features.push(marker);
			}
		}

		var layer = new ol.layer.Vector({
			source: new ol.source.Vector({
				features: vector_features
			})
		});
		layersArray.push(layer);
	}
	
	var controls = [
		new ol.control.Zoom(),
		new ol.control.ZoomSlider({
			target : divContainer
		}),
		new ol.control.MousePosition()
	];
	
	var map1 = new ol.Map({
		target : divContainer,
		layers: layersArray,
		controls : controls,
		view : new ol.View({
			zoom : zoom,
			center : ol.proj.transform([ originLong, originLat ], fromProjection, toProjection)
		})
	});
}

function previewMapV2(divContainer, originLat, originLong, zoom, boundTop,
		boundLeft, boundBottom, boundRight, fromProjection, dataSetType,
		features, marker) {
	var meas = [];
	previewMapV2WithValues(divContainer, originLat, originLong, zoom, boundTop, boundLeft, boundBottom, boundRight, fromProjection, dataSetType, features, marker, meas, "", "", "", "");
}

function previewMapV2WithValues(divContainer, originLat, originLong, zoom, boundTop, boundLeft, boundBottom, boundRight, fromProjection, dataSetType, geojsonObject, marker, meas, values, colorArray, wmtsSource, wmsSource) {
	measures = meas;
	
	document.getElementById(divContainer).innerHTML = "";
	var toProjection = 'EPSG:3857';

	var osm = new ol.layer.Tile({
		source : new ol.source.OSM()
	});
	
	// popup
	var container = document.getElementById('popup');
	var content = document.getElementById('popup-content');
	var closer = document.getElementById('popup-closer');

	var bulleContainer = document.getElementById('bullePopup');
	var bulleContent = document.getElementById('bullePopup-content');
	
	/**
	 * Create an overlay to anchor the popup to the map.
	 */
	var overlay = new ol.Overlay({
		element : container,
		autoPan : true,
		autoPanAnimation : {
			duration : 250
		}
	});

	var bulleOverlay = new ol.Overlay({
		element : bulleContainer,
		autoPan : true,
		autoPanAnimation : {
			duration : 250
		}
	});
	
	
	// styles
	var image = new ol.style.Circle({
		radius : 5,
		fill : new ol.style.Fill({
			color : '#00BFFF'
		}),
		stroke : new ol.style.Stroke({
			color : 'white',
			width : 1
		})
	});

	var styles = {
		'Point' : new ol.style.Style({
			image : image
		}),
		'Polygon' : new ol.style.Style({
			stroke : new ol.style.Stroke({
				color : 'rgba(0,255,0,0.2)',
				width : 2
			}),
			fill : new ol.style.Fill({
				color : 'rgba(0,255,0,0.1)'
			})
		}),
		'Circle' : new ol.style.Style({
			stroke : new ol.style.Stroke({
				color : 'blue',
				width : 2
			}),
			fill : new ol.style.Fill({
				color : 'rgba(0,0,255,0.2)'
			})
		})
	};

	var chooseStyle = function(feature) {
		var size;
		var features = feature.get('features');
		if (measures.length > 0) {
			/*
			 * if (measures[0][1] == 'SUM') { nbFeatures =
			 * getMeasureValue(measures[0], vectorSource.getFeatures().length,
			 * vectorSource.getFeatures()); } else if (measures[0][1] == 'MIN') {
			 * nbFeatures = 0; for(var i; i<clusterSource.getFeatures().length;
			 * i++){ nbFeatures += getMeasureValue(measures[0],
			 * clusterSource.getFeatures().length, clusterSource.getFeatures()); } }
			 * else if (measures[0][1] == 'MAX') { nbFeatures = 0; for(var i; i<clusterSource.getFeatures().length;
			 * i++){ nbFeatures += getMeasureValue(measures[0],
			 * clusterSource.getFeatures().length, clusterSource.getFeatures()); } }
			 * else if (measures[0][1] == 'AVG') {
			 */
			nbFeatures = 0;
			for (var i = 0; i < clusterSource.getFeatures().length; i++) {
				nbFeatures += getMeasureValue(measures[0], clusterSource
						.getFeatures()[i].get('features').length, clusterSource
						.getFeatures()[i].get('features'));
			}
			// }

		} else {
			nbFeatures = vectorSource.getFeatures().length;
		}

		if (measures.length > 0) {
			size = parseInt(getMeasureValue(measures[0], features.length,
					features));
		} else {
			size = features.length;
		}

		var hasProp = false;
//		var value = features[0].getProperties()[columnName];
//		for (var i = 0; i < features.length; i++) {
//			if (features[i].getProperties()[columnName] != value) {
//				hasProp = false;
//				break;
//			}
//		}

		// var rond = (size / 10) > 25 ? 25 : (size / 10) < 10 ? 10 : (size /
		// 10);
		var rond = parseInt(size * 100 / nbFeatures);
		if (rond < 15)
			rond = 15;
		if (rond > 50)
			rond = 50 + parseInt(30 * (rond - 50) / 100);

		if (hasProp) {
			// return styles['Point'];
			return new ol.style.Style({
				image : image,
				text : new ol.style.Text({
					text : size != 1 ? size.toString() : '',
					fill : new ol.style.Fill({
						color : 'black'
					}),
					placement : 'point',
					offsetY : -15,
					font : '12px sans-serif'
				})
			});
		} else {
			var style = styleCache[size];
			if (!style) {
				style = new ol.style.Style({
					image : new ol.style.Circle({
						radius : rond, // proportionnelle au size
						stroke : new ol.style.Stroke({
							color : 'white'
						}),
						fill : new ol.style.Fill({
							color : '#00BFFF'
						})
					}),
					text : new ol.style.Text({
						text : size.toString(),
						fill : new ol.style.Fill({
							color : 'black'
						}),
						stroke : new ol.style.Stroke({
							color : '#777'
						}),
						overflow : true,
						font : '12px sans-serif'
					})
				});
				styleCache[size] = style;
			}
			return style;
		}

	};

	var styleFunction = function(feature) {
		if (dataSetType == "polygon") {
			try {
				var zone = feature;
	
				var value = zone.get("Value");
	
				if ((colorArray != null) && (colorArray.length != 0)) {
					for (c = 0; c < colorArray.length; c++) {
						var min = parseInt(colorArray[c][1]);
						var max = parseInt(colorArray[c][2]);
	
						if (value >= min && value <= max) {
							var style = new ol.style.Style({
								fill : new ol.style.Fill({
									color : '#' + colorArray[c][0],
									fillOpacity : 0.5
								}),
								stroke : new ol.style.Stroke({
									color : '#000000',
									width : 1
								})
	
							});
							return style;
						}
					}
				}
				
				var style = new ol.style.Style({
					stroke : new ol.style.Stroke({
						color : 'rgba(0,255,0,0.2)',
						width : 2
					}),
					fill : new ol.style.Fill({
						color : 'rgba(0,255,0,0.1)'
					})
				});
				return style;
			} catch(error) {
				console.error(error);
			}
		}
		else {
			return styles[feature.getGeometry().getType()];
		}
	};
	
	vectorSource = new ol.source.Vector({
		features : (new ol.format.GeoJSON()).readFeatures(geojsonObject, {
			featureProjection : 'EPSG:3857'
		})
	});

	var polySource = new ol.source.Vector();
	if (measures.length > 0) {
		nbFeatures = getMeasureValue(measures[0],
				vectorSource.getFeatures().length, vectorSource.getFeatures());
	} else {
		nbFeatures = vectorSource.getFeatures().length;
	}

	for (var i = 0; i < vectorSource.getFeatures().length; i++) {
		if (vectorSource.getFeatures()[i].getGeometry() != null) {
			if (vectorSource.getFeatures()[i].getGeometry().getType() === 'Polygon') {
				polySource.addFeature(vectorSource.getFeatures()[i]);
			}
		}
	}
	var polygon = new ol.layer.Vector({
		opacity : 1,
		source : polySource,
		style : styleFunction
	});

	clusterSource = new ol.source.Cluster({
		distance : 55,
		source : vectorSource,
		geometryFunction : function(feature) {
			var geom = feature.getGeometry();
			return (geom != null && geom.getType() == 'Point') ? geom : null;
		}
	});

	var clusters = new ol.layer.Vector({
		source : clusterSource,
		style : chooseStyle
	});
	
	var layersArray = [];
	layersArray.push(osm);
	layersArray.push(polygon);
	layersArray.push(clusters);
	
	if (wmtsSource) {
		var wms = new ol.layer.Tile({
			source: new ol.source.OSM({
				layer : 'local-tile',
				url: wmtsSource,
				crossOrigin: null
			}),
			name: "WMTS_Tile"
		});
		layersArray.push(wms);
	}
	
	if ((wmsSource != null) && (wmsSource.length != 0)) {
		for (var i = 0; i < wmsSource.length; i++) {
			var wmsArray = wmsSource[i];
			var wmsUrl = wmsArray[0];
			var wmsLayerName = wmsArray[1];
			var wmsOpacity = wmsArray[2] / 100;
			var type = wmsArray[3];
			
			if (type === 'WFS') {
				/* D�claration de la source de la couche en format WFS */
				var sourceWFS = new ol.source.Vector({
					/* Chargement du lien WFS en format json*/
					url: wmsUrl,
					format: new ol.format.GeoJSON(),
				})
				/* D�claration de la couche WFS */
				var coucheWFS = new ol.layer.Vector({ source: sourceWFS });
				layersArray.push(coucheWFS);
			}
			else {
				var wms = new ol.layer.Tile({
					source: new ol.source.TileWMS({
						url: wmsUrl,
						params: {'LAYERS': wmsLayerName, 'TILED': true},
					}),
					opacity: wmsOpacity
				});
				layersArray.push(wms);
			}
		}
	}
	
	
	var controls = [
		new ol.control.Zoom(),
		new ol.control.ZoomSlider({
			target : divContainer
		}),
		new ol.control.MousePosition()
	];

	var map1 = new ol.Map({
		target : divContainer,
		layers : layersArray,
		overlays : [ overlay, bulleOverlay ],
		controls : controls,
		view : new ol.View({
			zoom : zoom,
			center : ol.proj.transform([ originLong, originLat ],
					fromProjection, toProjection)
		})
	});
	
//	map1.on('singleclick', function(evt) {
//		
//		// alert(coucheWFS.features);
//		bulleOverlay.setPosition(undefined);
//		/*
//		 * var feature = map.forEachFeatureAtPixel(evt.pixel, function(feature,
//		 * vectorLayer) { return feature; });
//		 * 
//		 * if(feature){ map.getView().setZoom(map.getView().getZoom()+1);
//		 * map.getView().setCenter(evt.coordinate); }
//		 */
//		var f = map1.getFeaturesAtPixel(evt.pixel);
//		if (f == null) {
//			overlay.setPosition(undefined);
//			return;
//		}
//		
////		content.innerHTML = "<div>Test</div>";
//		
////		var size = f[0].get('features').length;
////		feats = f[0].get('features');
////
////		var hasProp = true;
////		var value = feats[0].getProperties()[columnName];
////		for (var i = 0; i < size; i++) {
////			if (feats[i].getProperties()[columnName] != value) {
////				hasProp = false;
////				break;
////			}
////		}
////
////		if (hasProp) {
////			commune = feats[0].getProperties()[columnName];
////			index = 0;
////			pager.innerHTML = (index + 1) + "/" + (feats.length > total ? total : feats.length);
////			var prop = feats[index].getProperties();
////			content.innerHTML = getPopupTextContent(prop);
////
////			var coordinate = evt.coordinate;
////
////			overlay.setPosition(coordinate);
////
////			if ((links != null) && (links.length != 0)) {
////				for (var i = 0; i < links.length; i++) {
////					var id = document.getElementById('lien' + i);
////							
////							var itemName = links[i][0];
////							var publicUrl = links[i][1];
////							var paramsNames = links[i][2];
////							var paramsColumns = links[i][3];
////							
////							if (paramsNames != null && paramsNames.length != 0) {
////								for (var j = 0; j < paramsNames.length; j++) {
////									var paramName = paramsNames[j];
////									var columnName = paramsColumns[j];
////									var value = feats[0].getProperties()[columnName];
////									
////									publicUrl += "&" + paramName + "=" + value;
////								}
////							}
////							
////							id.innerHTML = links[i][0] + "<br/>";
////							id.href = "#";
////							id.target = "_blank";
////							id.onclick = function() {
////								openReport(publicUrl);
////								return false;
////							};
////
////					container.insertBefore(id.parentNode, closer);
////				}
////			}
////
////		} else {
////			map.getView().setZoom(map.getView().getZoom() + 1);
////			map.getView().setCenter(evt.coordinate);
////			/*
////			 * var selectClick = new ol.interaction.Select({ condition:
////			 * ol.events.condition.click });
////			 * 
////			 * map.addInteraction(selectClick);
////			 * 
////			 * selectClick.on('select', function(e) { var extent =
////			 * e.target.getFeatures().getArray()[0].getGeometry().getExtent();
////			 * map.getView().fit(extent); });
////			 */
////		}
//	});
//	map1.on('pointermove', function(evt) {
//		bulleOverlay.setPosition(undefined);
//		var feature = map1.forEachFeatureAtPixel(evt.pixel, function(feature, vectorLayer) {
//			return feature;
//		});
//
//		if (feature) {
//
//			var f = map1.getFeaturesAtPixel(evt.pixel);
//			var value;
//			try {
//				value = f[0].get('Value');
//			} catch (e) {
//				value = 0;
//			}
//
//			bulleContent.innerHTML = '<code>';
//			bulleContent.innerHTML += value;
//			bulleContent.innerHTML += '</code>';
//
//			if (measures.length > 0) {
//				bulleOverlay.setPosition(evt.coordinate);
//			} else {
//				bulleOverlay.setPosition(undefined);
//			}
//		}
//	});
}

function pointStyleFunction(feature, resolution) {
	  return new ol.style.Style({
		  text: new ol.style.Text({
	            text: feature.get('data'),
	            textAlign: 'center',

	            font: '11px roboto,sans-serif',
	            fill: new ol.style.Fill({
	                color: 'white'
	            }),
	            stroke: new ol.style.Stroke({
	                color: 'black',
	                lineCap: 'butt',
	                width: 4
	            }),
	            offsetX: 0,
	            offsetY: 25.5,
	        })
	  });
	}

/** ----------------- SELOME ----------------------* */

function filterFunction(geojsonObj, paramsJson) {
	if (paramsJson == null) {
		var vectorSrc = new ol.source.Vector({
			features : (new ol.format.GeoJSON()).readFeatures(geojsonObj, {
				featureProjection : 'EPSG:3857'
			})
		});
		vectorSource.clear(true);
		vectorSource.addFeatures(vectorSrc.getFeatures());
		return geojsonObj;
	} else {
		var vectorSrc = new ol.source.Vector({
			features : (new ol.format.GeoJSON()).readFeatures(geojsonObj, {
				featureProjection : 'EPSG:3857'
			})
		});

		var newFeatures = [];
		var features = vectorSrc.getFeatures();
		var params = JSON.parse(paramsJson);
		for (var i = 0; i < features.length; i++) {
			var props = features[i].getProperties();
			var fitParam = true;
			DIM: for (var j = 0; j < Object.keys(params).length; j++) {
				var key = Object.keys(params)[j];
				var value = params[key];
				if (key == "__location__") {
					var fitLocation = false;
					for ( var k in props) {
						var v = props[k];
						if (v == value) {
							fitLocation = true;
							break;
						}
					}
					if (!fitLocation) {
						fitParam = false;
						break;
					}
				} else {
					if (props.hasOwnProperty(key)
							|| ((Array.isArray(value) && value.indexOf('null') != -1) || value == "null")) {
						if (Array.isArray(value)) {
							/*
							 * for(var k=0; k<value.length; k++){ if
							 * (props[key] == value[k]) { continue DIM; }
							 * fitParam = false; break; }
							 */
							var fitDisjunctive = false;
							for (var k = 0; k < value.length; k++) {
								if ((value[k] != "null" && props[key] == value[k])
										|| (value[k] == "null" && !props
												.hasOwnProperty(key))) {
									fitDisjunctive = true;
									break;
								}

							}
							if (!fitDisjunctive) {
								fitParam = false;
								break;
							}
						} else {
							if ((value != "null" && props[key] != value)
									|| (value == "null" && props
											.hasOwnProperty(key))) {
								fitParam = false;
								break;
							}
						}

					} else {
						fitParam = false;
						break;
					}
				}

			}
			if (fitParam) {
				newFeatures.push(features[i]);
			}
		}

		vectorSource.clear(true);
		vectorSource.addFeatures(newFeatures);

		return JSON.stringify((new ol.format.GeoJSON()).writeFeaturesObject(
				vectorSource.getFeatures(), {
					featureProjection : 'EPSG:3857'
				}));
	}
}

var vectorSource;
var clusterSource;
var measures;
var nbFeatures = 0;
var styleCache = {};

function changeMeasures(newMeasures) {
	measures = newMeasures;

	styleCache = {};

	var features = vectorSource.getFeatures();
	vectorSource.clear(true);
	vectorSource.addFeatures(features);
	// globalMap.renderSync();
}

function renderGeoJsonMap(divContainer, geojsonObject, columnName, links, meas) {
	renderGeoJsonMapWithWms(divContainer, geojsonObject, columnName, links,
			meas, "", "", false);
}

function renderGeoJsonMapWithWms(divContainer, geojsonObject, columnName,
		links, meas, wmtsSource, wmsSource, refresh) {
	measures = meas;
	// popup
	var container = document.getElementById('popup');
	var content = document.getElementById('popup-content');
	var closer = document.getElementById('popup-closer');

	var bulleContainer = document.getElementById('bullePopup');
	var bulleContent = document.getElementById('bullePopup-content');

	// styles
	var image = new ol.style.Circle({
		radius : 5,
		fill : new ol.style.Fill({
			color : '#00BFFF'
		}),
		stroke : new ol.style.Stroke({
			color : 'white',
			width : 1
		})
	});

	var styles = {
		'Point' : new ol.style.Style({
			image : image
		}),
		'Polygon' : new ol.style.Style({
			stroke : new ol.style.Stroke({
				color : 'rgba(0,255,0,0.2)',
				width : 2
			}),
			fill : new ol.style.Fill({
				color : 'rgba(0,255,0,0.1)'
			})
		}),
		'Circle' : new ol.style.Style({
			stroke : new ol.style.Stroke({
				color : 'blue',
				width : 2
			}),
			fill : new ol.style.Fill({
				color : 'rgba(0,0,255,0.2)'
			})
		})
	};

	var styleFunction = function(feature) {
		return styles[feature.getGeometry().getType()];
	};

	var chooseStyle = function(feature) {
		var size;
		var features = feature.get('features');
		if (measures.length > 0) {
			/*
			 * if (measures[0][1] == 'SUM') { nbFeatures =
			 * getMeasureValue(measures[0], vectorSource.getFeatures().length,
			 * vectorSource.getFeatures()); } else if (measures[0][1] == 'MIN') {
			 * nbFeatures = 0; for(var i; i<clusterSource.getFeatures().length;
			 * i++){ nbFeatures += getMeasureValue(measures[0],
			 * clusterSource.getFeatures().length, clusterSource.getFeatures()); } }
			 * else if (measures[0][1] == 'MAX') { nbFeatures = 0; for(var i; i<clusterSource.getFeatures().length;
			 * i++){ nbFeatures += getMeasureValue(measures[0],
			 * clusterSource.getFeatures().length, clusterSource.getFeatures()); } }
			 * else if (measures[0][1] == 'AVG') {
			 */
			nbFeatures = 0;
			for (var i = 0; i < clusterSource.getFeatures().length; i++) {
				nbFeatures += getMeasureValue(measures[0], clusterSource
						.getFeatures()[i].get('features').length, clusterSource
						.getFeatures()[i].get('features'));
			}
			// }

		} else {
			nbFeatures = vectorSource.getFeatures().length;
		}

		if (measures.length > 0) {
			size = parseInt(getMeasureValue(measures[0], features.length,
					features));
		} else {
			size = features.length;
		}

		var hasProp = true;
		var value = features[0].getProperties()[columnName];
		for (var i = 0; i < features.length; i++) {
			if (features[i].getProperties()[columnName] != value) {
				hasProp = false;
				break;
			}
		}

		// var rond = (size / 10) > 25 ? 25 : (size / 10) < 10 ? 10 : (size /
		// 10);
		var rond = parseInt(size * 100 / nbFeatures);
		if (rond < 15)
			rond = 15;
		if (rond > 50)
			rond = 50 + parseInt(30 * (rond - 50) / 100);

		if (hasProp) {
			// return styles['Point'];
			return new ol.style.Style({
				image : image,
				text : new ol.style.Text({
					text : size != 1 ? size.toString() : '',
					fill : new ol.style.Fill({
						color : 'black'
					}),
					placement : 'point',
					offsetY : -15,
					font : '12px sans-serif'
				})
			});
		} else {
			var style = styleCache[size];
			if (!style) {
				style = new ol.style.Style({
					image : new ol.style.Circle({
						radius : rond, // proportionnelle au size
						stroke : new ol.style.Stroke({
							color : 'white'
						}),
						fill : new ol.style.Fill({
							color : '#00BFFF'
						})
					}),
					text : new ol.style.Text({
						text : size.toString(),
						fill : new ol.style.Fill({
							color : 'white'
						}),
						stroke : new ol.style.Stroke({
							color : '#777'
						}),
						overflow : true,
						font : '12px sans-serif'
					})
				});
				styleCache[size] = style;
			}
			return style;
		}

	};

	// map
	vectorSource = new ol.source.Vector({
		features : (new ol.format.GeoJSON()).readFeatures(geojsonObject, {
			featureProjection : 'EPSG:3857'
		})
	});

	var polySource = new ol.source.Vector();
	if (measures.length > 0) {
		nbFeatures = getMeasureValue(measures[0],
				vectorSource.getFeatures().length, vectorSource.getFeatures());
	} else {
		nbFeatures = vectorSource.getFeatures().length;
	}

	for (var i = 0; i < vectorSource.getFeatures().length; i++) {
		if (vectorSource.getFeatures()[i].getGeometry() != null) {
			if (vectorSource.getFeatures()[i].getGeometry().getType() === 'Polygon') {
				polySource.addFeature(vectorSource.getFeatures()[i]);
			}
		}
	}
	var polygon = new ol.layer.Vector({
		opacity : 1,
		source : polySource,
		style : styleFunction
	});

	clusterSource = new ol.source.Cluster({
		distance : 55,
		source : vectorSource,
		geometryFunction : function(feature) {
			var geom = feature.getGeometry();
			return (geom != null && geom.getType() == 'Point') ? geom : null;
		}
	});

	var clusters = new ol.layer.Vector({
		source : clusterSource,
		style : chooseStyle
	});

	var osmLayer = null;
	
	if (wmtsSource) {
		var layers = [];

		// Exemple : url:
		// 'https://svi-vanilla.data4citizen.com:443/geoserver/gwc/service/wmts/rest/nurc:Img_Sample/raster/EPSG:4326/EPSG:4326:{z}/{y}/{x}?format=image/png',
		var wms = new ol.layer.Tile({
			source : new ol.source.OSM({
				layer : 'local-tile',
				url : wmtsSource,
				crossOrigin : null
			}),
			name : "WMTS_Tile"
		});
		layers.push(wms);

		layers.push(polygon);
		layers.push(clusters);
	} else {
		var raster = new ol.layer.Tile({
			source : new ol.source.OSM()
		});

		var layers = [ raster, polygon, clusters ];
		
		osmLayer = raster;
	}

	if ((wmsSource != null) && (wmsSource.length != 0)) {
		for (var i = 0; i < wmsSource.length; i++) {
			var wmsArray = wmsSource[i];
			var wmsUrl = wmsArray[0];
			var wmsLayerName = wmsArray[1];
			var wmsOpacity = wmsArray[2] / 100;
			var type = wmsArray[3];

			if (type === 'WFS') {
				/* D�claration de la source de la couche en format WFS */
				var sourceWFS = new ol.source.Vector({
					/* Chargement du lien WFS en format json */
					url : wmsUrl,
					format : new ol.format.GeoJSON(),
				})
				/* D�claration de la couche WFS */
				var coucheWFS = new ol.layer.Vector({
					source : sourceWFS
				});
				layers.push(coucheWFS);
			} else {
				var wms = new ol.layer.Tile({
					source : new ol.source.TileWMS({
						url : wmsUrl,
						params : {
							'LAYERS' : wmsLayerName,
							'TILED' : true
						},
					}),
					opacity : wmsOpacity
				});
				layers.push(wms);
			}
		}
	}
	// columnName = "city";
	/**
	 * Create an overlay to anchor the popup to the map.
	 */
	var overlay = new ol.Overlay({
		element : container,
		autoPan : true,
		autoPanAnimation : {
			duration : 250
		}
	});

	var bulleOverlay = new ol.Overlay({
		element : bulleContainer,
		autoPan : true,
		autoPanAnimation : {
			duration : 250
		}
	});

	/**
	 * Add a click handler to hide the popup.
	 * 
	 * @return {boolean} Don't follow the href.
	 */
	closer.onclick = function() {
		overlay.setPosition(undefined);
		closer.blur();
		return false;
	};

	if (globalMap && refresh) {
		var layersOSM = new ol.layer.Group({
		    layers: layers
		});
		
		globalMap.setLayerGroup(layersOSM);
		
		globalMap.getOverlays().clear();
		globalMap.addOverlay(overlay);
		globalMap.addOverlay(bulleOverlay);
		
		map = globalMap;
	}
	else {
		
		var map = new ol.Map({
			layers : layers,
			overlays : [ overlay, bulleOverlay ],
			target : divContainer,
			controls : ol.control.defaults({
				attributionOptions : {
					collapsible : false
				}
			}),
			view : new ol.View({
				// center: [0, 0],
				center : ol.proj.fromLonLat([ 2.21, 46.22 ]),// france
				zoom : 2
			})
		});

		map.render(divContainer);
		globalMap = map;
	}
	
	var feats;
	var index;
	var total = 15;

	var previous = document.getElementById('previous');
	previous.innerHTML = "&laquo;";
	var pager = document.getElementById('pager');
	var next = document.getElementById('next');
	next.innerHTML = "&raquo;";

	previous.onclick = function() {
		if (index - 1 >= 0) {

			var prop = feats[index - 1].getProperties();

			content.innerHTML = getPopupTextContent(prop);

			pager.innerHTML = index + "/" + (feats.length > total ? total : feats.length);
			index--;
		}
		return false;
	};

	next.onclick = function() {
		if (index + 1 < total) {

			var prop = feats[index + 1].getProperties();
			content.innerHTML = getPopupTextContent(prop);
			index++;
			pager.innerHTML = (index + 1) + "/" + (feats.length > total ? total : feats.length);
		}
		return false;
	};

	var commune;
	var annee;
	map.on('singleclick', function(evt) {
		
		// alert(coucheWFS.features);
		bulleOverlay.setPosition(undefined);
		/*
		 * var feature = map.forEachFeatureAtPixel(evt.pixel, function(feature,
		 * vectorLayer) { return feature; });
		 * 
		 * if(feature){ map.getView().setZoom(map.getView().getZoom()+1);
		 * map.getView().setCenter(evt.coordinate); }
		 */
		var f = map.getFeaturesAtPixel(evt.pixel);
		if (f == null) {
			overlay.setPosition(undefined);
			return;
		}
		var size = f[0].get('features').length;
		feats = f[0].get('features');

		var hasProp = true;
		var value = feats[0].getProperties()[columnName];
		for (var i = 0; i < size; i++) {
			if (feats[i].getProperties()[columnName] != value) {
				hasProp = false;
				break;
			}
		}

		if (hasProp) {
			commune = feats[0].getProperties()[columnName];
			index = 0;
			pager.innerHTML = (index + 1) + "/" + (feats.length > total ? total : feats.length);
			var prop = feats[index].getProperties();
			content.innerHTML = getPopupTextContent(prop);

			var coordinate = evt.coordinate;

			overlay.setPosition(coordinate);

			if ((links != null) && (links.length != 0)) {
				for (var i = 0; i < links.length; i++) {
					var id = document.getElementById('lien' + i);
							
							var itemName = links[i][0];
							var publicUrl = links[i][1];
							var paramsNames = links[i][2];
							var paramsColumns = links[i][3];
							
							if (paramsNames != null && paramsNames.length != 0) {
								for (var j = 0; j < paramsNames.length; j++) {
									var paramName = paramsNames[j];
									var columnName = paramsColumns[j];
									var value = feats[0].getProperties()[columnName];
									
									publicUrl += "&" + paramName + "=" + value;
								}
							}
							
							id.innerHTML = links[i][0] + "<br/>";
							id.href = "#";
							id.target = "_blank";
							id.onclick = function() {
								openReport(publicUrl);
								return false;
							};

					container.insertBefore(id.parentNode, closer);
				}
			}

		} else {
			map.getView().setZoom(map.getView().getZoom() + 1);
			map.getView().setCenter(evt.coordinate);
			/*
			 * var selectClick = new ol.interaction.Select({ condition:
			 * ol.events.condition.click });
			 * 
			 * map.addInteraction(selectClick);
			 * 
			 * selectClick.on('select', function(e) { var extent =
			 * e.target.getFeatures().getArray()[0].getGeometry().getExtent();
			 * map.getView().fit(extent); });
			 */
		}
	});

	map.on('pointermove', function(evt) {
		bulleOverlay.setPosition(undefined);
		var feature = map.forEachFeatureAtPixel(evt.pixel, function(feature,
				vectorLayer) {
			return feature;
		});

		if (feature) {

			var f = map.getFeaturesAtPixel(evt.pixel);
			var size;
			var feats;
			try {
				size = f[0].get('features').length;
				feats = f[0].get('features');
			} catch (e) {
				size = 1;
				feats = f;
			}
			var hasProp = true;
			var value = feats[0].getProperties()[columnName];
			for (var i = 0; i < size; i++) {
				if (feats[i].getProperties()[columnName] != value) {
					hasProp = false;
					break;
				}
			}

			// if (!hasProp) {
			// overlay.setPosition(undefined);
			var listSomme = new Array();
			var listMin = new Array();
			var listMax = new Array();
			var listAvg = new Array();

			for (var i = 0; i < measures.length; i++) {
				var res = getMeasureValue(measures[i], size, feats);
				if (measures[i][1] == 'SUM') {
					listSomme[measures[i][0]] = res;
				} else if (measures[i][1] == 'MIN') {
					listMin[measures[i][0]] = res;
				} else if (measures[i][1] == 'MAX') {
					listMax[measures[i][0]] = res;
				} else if (measures[i][1] == 'AVG') {
					listAvg[measures[i][0]] = res;
				}

			}

			var sommeText = '';

			var sommekeys = Object.keys(listSomme);
			var sommevalues = sommekeys.map(function(key) {
				return listSomme[key];
			});

			for (var m = 0; m < measures.length; m++) {
				if (sommevalues[m] != null) {
					sommeText += sommekeys[m] + " : "
							+ sommevalues[m].toFixed(2) + '<br/>';
				}
			}

			var minText = '';

			var minkeys = Object.keys(listMin);
			var minvalues = minkeys.map(function(key) {
				return listMin[key];
			});

			for (var m = 0; m < measures.length; m++) {
				if (minvalues[m] != null) {
					minText += minkeys[m] + " : " + minvalues[m] + '<br/>';
				}
			}

			var maxText = '';

			var maxkeys = Object.keys(listMax);
			var maxvalues = maxkeys.map(function(key) {
				return listMax[key];
			});

			for (var m = 0; m < measures.length; m++) {
				if (maxvalues[m] != null) {
					maxText += maxkeys[m] + " : " + maxvalues[m] + '<br/>';
				}
			}

			var avgText = '';

			var avgkeys = Object.keys(listAvg);
			var avgvalues = avgkeys.map(function(key) {
				return listAvg[key];
			});

			for (var m = 0; m < measures.length; m++) {
				if (avgvalues[m] != undefined) {
					avgText += avgkeys[m] + " : " + avgvalues[m].toFixed(2)
							+ '<br/>';
				}
			}

			bulleContent.innerHTML = '<code>';
			if (sommeText != '') {
				bulleContent.innerHTML += "Somme: <br/>" + sommeText;
			}
			if (minText != '') {
				bulleContent.innerHTML += "Min: <br/>" + minText;
			}
			if (maxText != '') {
				bulleContent.innerHTML += "Max: <br/>" + maxText;
			}
			if (avgText != '') {
				bulleContent.innerHTML += "Moyenne: <br/>" + avgText;
			}

			bulleContent.innerHTML += '</code>';

			if (measures.length > 0) {
				bulleOverlay.setPosition(evt.coordinate);
			} else {
				bulleOverlay.setPosition(undefined);
			}

			// }

		}

	});
}

function openReport(reportUrl) {
	window.parent.openReportInTab(reportUrl);
}

function getPopupTextContent(props) {
	var keys = Object.keys(props);
	var values = keys.map(function(key) {
		return props[key];
	});

	var characLabels = "<div class='ol-popup-label'>";
	var characValues = "<div class='ol-popup-value'>";
	for (var i = 0; i < keys.length; i++) {
		if (!keys[i].match("geometry")) {
			characLabels += "<strong>" + keys[i] + "</strong><br/>"
			characValues += values[i] + "<br/>";
		}
	}
	characLabels += "</div>";
	characValues += "</div>";

	return '<div class="ol-popup-content">' + characLabels + characValues + '</div>';
}

function getMeasureValue(measure, size, feats) {
	var res = null;

	var first = true;
	for (var j = 0; j < feats.length; j++) {

		var prop = feats[j].getProperties();
		var keys = Object.keys(prop);
		var values = keys.map(function(key) {
			return prop[key];
		});

		for (var k = 0; k < keys.length; k++) {
			if (measure[0] == keys[k]) {
				if (measure[1] == 'SUM') {
					res += parseFloat(values[k]);
				} else if (measure[1] == 'MIN') {
					if (first) {
						res = parseFloat(values[k]);
					} else {
						if (parseFloat(values[k]) <= min) {
							res = parseFloat(values[k]); // parseInt(values[k],
							// 10);
						}
					}
					first = false;
				} else if (measure[1] == 'MAX') {
					if (parseFloat(values[k]) >= max) {
						res = parseFloat(values[k]);
					}
				} else if (measure[1] == 'AVG') {
					res += parseFloat(values[k]);
				}
			}
		}
	}

	if (measure[1] == 'AVG') {
		res = res / feats.length;
	}
	return res;
}

var globalMap;

// Osm layer
var layer_watercolor;
var layer_osm;
var layer_toner_lite;
var layer_toner;
// var layer_sat_quest;
// var layer_osm_quest;
// var layer_hyb_quest;

function renderComplexMap(divContainer, JsonData, action) {
	renderComplexMapWithWms(divContainer, JsonData, action, "", "");
}

function renderComplexMapWithWms(divContainer, JsonData, action, wmtsSource,
		wmsSource) {
	var data = JSON.parse(JsonData);
	var originLat = data.lat;
	var originLong = data.long;
	var zoom = data.zoom;
	var boundLeft = data.minLong;
	var boundBottom = data.minLat;
	var boundRight = data.maxLat;
	var boundTop = data.maxLong;
	var content = data.levels;

	var fromProjection = new ol.proj.Projection("EPSG:4326");
	var toProjection = new ol.proj.Projection("EPSG:900913");
	// var proj = new ol.proj.Projection(fromProjection);

	var layers = [];
	var vector_feature = [];
	var circle_feature = [];
	var heatmap_feature = [];

	var displayedNames = [];

	var texts = {
		text : "normal",
		align : "center",
		baseline : "middle",
		rotation : 0,
		font : "Verdana",
		weight : "bold",
		size : "8px",
		offsetX : 0,
		offsetY : 0,
		color : "black",
		outline : "#ffffff",
		outlineWidth : 3,
		maxreso : '1200'
	};

	var getText = function(feature, resolution, dom) {
		var type = dom.text.value;
		var maxResolution = dom.maxreso.value;
		var text = feature.get('name');

		if (resolution > maxResolution) {
			text = '';
		} /*
			 * else if (type == 'hide') { text = ''; } else if (type ==
			 * 'shorten') { text = text.trunc(12); } else if (type == 'wrap') {
			 * text = stringDivider(text, 16, '\n'); }
			 */

		return text;
	};

	var createTextStyle = function(feature, resolution, dom) {
		var align = dom.align.value;
		var baseline = dom.baseline.value;
		var size = dom.size.value;
		var offsetX = parseInt(dom.offsetX.value, 10);
		var offsetY = parseInt(dom.offsetY.value, 10);
		var weight = dom.weight.value;
		var rotation = parseFloat(dom.rotation.value);
		var font = weight + ' ' + size + ' ' + dom.font.value;
		var fillColor = dom.color.value;
		var outlineColor = dom.outline.value;
		var outlineWidth = parseInt(dom.outlineWidth.value, 10);

		return new ol.style.Text({
			textAlign : align,
			textBaseline : baseline,
			font : font,
			text : getText(feature, resolution, dom),
			fill : new ol.style.Fill({
				color : fillColor
			}),
			stroke : new ol.style.Stroke({
				color : outlineColor,
				width : outlineWidth
			}),
			offsetX : offsetX,
			offsetY : offsetY,
			rotation : rotation
		});
	};

	var createCircleStyleFunction = function() {

		return function(feature, resolution) {
			var style = new ol.style.Style({
				fill : new ol.style.Fill({
					// color: '#' + metricColor
					color : '#BBBBBB'
				}),
				stroke : new ol.style.Stroke({
				// color: '#' + metricColor,
				// width: 1
				}),
				text : createTextStyle(feature, resolution, texts)
			});
			return [ style ];
		};
	};

	// calcul de l'extent : A REVOIR
	// var extentValue = new
	// ol.extent.boundingExtent([boundLeft,boundBottom,boundRight,boundTop]);
	var extentValue = [ boundLeft, boundBottom, boundRight, boundTop ];
	var tfn = ol.proj.getTransform('EPSG:4326', 'EPSG:900913');
	extentValue = ol.extent.applyTransform(extentValue, tfn);

	// definition des controles
	var controls = [
	// new ol.control.Control(),
	new ol.control.Zoom(), new ol.control.ZoomSlider({
		target : divContainer
	}), new ol.control.MousePosition()
	// new ol.control.Control.LayerSwitcher(),
	// new ol.control.Control.KeyboardDefaults()
	];

	// Osm layer
	layer_watercolor = new ol.layer.Tile({
		source : new ol.source.Stamen({
			layer : 'watercolor'
		}),
		name : "Tile"
	});
	layer_toner_lite = new ol.layer.Tile({
		source : new ol.source.Stamen({
			layer : 'toner-lite'
		}),
		name : "Tile"
	});
	layer_toner = new ol.layer.Tile({
		source : new ol.source.Stamen({
			layer : 'toner'
		}),
		name : "Tile"
	});

	if (wmtsSource) {
		var layers = [];

		// Exemple : url:
		// 'https://svi-vanilla.data4citizen.com:443/geoserver/gwc/service/wmts/rest/nurc:Img_Sample/raster/EPSG:4326/EPSG:4326:{z}/{y}/{x}?format=image/png',
		var wms = new ol.layer.Tile({
			source : new ol.source.OSM({
				layer : 'local-tile',
				url : wmtsSource,
				crossOrigin : null
			}),
			name : "WMTS_Tile"
		});
		layers.push(wms);
	} else {
		layer_osm = new ol.layer.Tile({
			source : new ol.source.OSM(),
			name : "Tile"
		});
		layers.push(layer_osm);
	}

	if ((wmsSource != null) && (wmsSource.length != 0)) {
		for (var i = 0; i < wmsSource.length; i++) {
			var wmsArray = wmsSource[i];
			var wmsUrl = wmsArray[0];
			var wmsLayerName = wmsArray[1];
			var wmsOpacity = wmsArray[2] / 100;
			var type = wmsArray[3];

			if (type === 'WFS') {
				/* D�claration de la source de la couche en format WFS */
				var sourceWFS = new ol.source.Vector({
					/* Chargement du lien WFS en format json */
					url : wmsUrl,
					format : new ol.format.GeoJSON(),
				})
				/* D�claration de la couche WFS */
				var coucheWFS = new ol.layer.Vector({
					source : sourceWFS
				});
				layers.push(coucheWFS);
			} else {
				var wms = new ol.layer.Tile({
					source : new ol.source.TileWMS({
						url : wmsUrl,
						params : {
							'LAYERS' : wmsLayerName,
							'TILED' : true
						},
					}),
					opacity : wmsOpacity
				});
				layers.push(wms);
			}
		}
	}

	/* Debut d�cryptage des valeurs */
	for (i = 0; i < content.length; i++) { /* parcours des levels */
		var cpxlevel = content[i];
		var cpxmetrics = cpxlevel.metrics;
		var levelColor = cpxlevel.couleur;

		for (j = 0; j < cpxmetrics.length; j++) { /* parcours des metrics */
			var cpxmetric = cpxmetrics[j];

			var mapzonevalues = cpxmetric.mapzonevalues;
			var metricColor = cpxmetric.couleur;
			var metricName = cpxmetric.name;
			if (cpxmetric.displayed == 0) {
				var metricDisplayed = false;
			} else {
				var metricDisplayed = true;
				displayedNames.push(metricName);
			}
			var metricRepresentation = cpxmetric.type;
			var maxRadius = 0;
			var maxValue = 0;
			var sumRadius = 0;

			for (k = 0; k < mapzonevalues.length; k++) { /*
															 * parcours des
															 * zones
															 */
				var zone = mapzonevalues[k].zone;
				var value = mapzonevalues[k].value;
				var zoneName = mapzonevalues[k].name;
				var datas = mapzonevalues[k].datas;
				var points = [];

				for (l = 0; l < zone.length; l++) { /* parcours des points */
					var p = new ol.geom.Point(ol.proj.transform([
							Number(zone[l].long), Number(zone[l].lat) ],
							'EPSG:4326', 'EPSG:900913'));
					points.push(p.getCoordinates());
				}

				/* construcion des polygones */
				var poly = new ol.geom.Polygon([ points ]);
				var polygon = new ol.Feature({
					geometry : poly,
					name : zoneName
				});

				var style = new ol.style.Style({
					fill : new ol.style.Fill({
						color : 'rgba(255, 255, 255, 1)'
					}),
					stroke : new ol.style.Stroke({
						color : '#' + levelColor,
						width : 1
					})

				});
				// if(action == "EVOLUTION"){
				// if(value > 0) {
				// style.getFill().setColor("#85c879");
				// }
				// else if(value < 0) {
				// style.getFill().setColor("#f27742");
				// }
				// else {
				// style.getFill().setColor("#d9c16f");
				// }
				// }

				polygon.setStyle(style);
				var exists = false;
				var ii = 0;
				for (ii = 0; ii < vector_feature.length; ii++) {
					if (polygon.get('name') == vector_feature[ii].get('name')) {
						exists = true;
					}
				}
				if (!exists) {
					vector_feature.push(polygon);
				}

				/* construcion des metric layers */

				// centre du polygon
				var p = polygon.getGeometry().getInteriorPoint();
				if (metricRepresentation == "Bulles") {

					//alert(polygon.getGeometry());
					//alert(polygon.getGeometry().flatCoordinates);
					if (polygon.getGeometry().flatCoordinates != undefined && polygon.getGeometry().flatCoordinates.length > 2) { // c'est
						// une
						// zone

						var point2 = poly.getClosestPoint(p.getCoordinates()); // on
						// cherche
						// le
						// rayon
						// max
						// du
						// cercle
						// dans
						// le
						// polygone
						var line = new ol.geom.LineString([ p.getCoordinates(),
								point2 ]);
						var rayon = line.getLength();
					} else { // c'est un point
						var rayon = (1 / Math.sqrt(zoom)) * 30000;
					}

					/*
					 * if(maxRadius < rayon){ maxRadius = rayon; } //on cherche
					 * le rayon max
					 */sumRadius += rayon
					if (maxValue < value) {
						maxValue = value;
					}
					var circle = new ol.Feature({
						geometry : new ol.geom.Circle(p.getCoordinates(), Math
								.abs(value)), // la taille du cercle
						// est
						// proportionnelle a la valeur
						// et adapt�e � la zone
						name : zoneName,
						value : value,
						metricName : metricName,
						data : datas
					});

					if (value >= 0) {
						var strokecolor = '#51f210';
					} else {
						var strokecolor = '#f21010';
					}

					var style_circle = new ol.style.Style({
						fill : new ol.style.Fill({
							color : '#' + metricColor
						}),
						stroke : new ol.style.Stroke({
							color : strokecolor,
							width : 3
						}),
						text : new ol.style.Text({
							textAlign : 'center',
							font : texts.font.value,
							text : (Math.round(Math.abs(value) * 100) / 100)
									.toLocaleString('fr'),
							fill : new ol.style.Fill({
								color : texts.color.value
							}),
							stroke : new ol.style.Stroke({
								color : texts.outline.value,
								width : texts.outlineWidth.value
							})
						})
					});
					circle.setStyle(style_circle);
					circle_feature.push(circle);
				}

				if (metricRepresentation == "Cartes de chaleurs") {
					var heat = new ol.Feature({
						geometry : p,
						weight : Math.abs(value),
						value : value,
						metricName : metricName,
						data : datas,
						name : zoneName
					});

					heatmap_feature.push(heat);
				}

			}

			if (metricRepresentation == "Bulles") {
				for (go = 0; go < circle_feature.length; go++) {
					rad = circle_feature[go].getGeometry().getRadius();
					value = parseInt(circle_feature[go].get('value'));
					circle_feature[go].getGeometry().setRadius(
							value * (sumRadius / mapzonevalues.length)
									/ maxValue);
				}
				var source_circles = new ol.source.Vector({
					features : circle_feature
				});
				var layer_circles = new ol.layer.Vector({
					source : source_circles,
					visible : metricDisplayed,
					opacity : 0.6,
					name : metricName
				/*
				 * , maxResolution: 500
				 */

				});
				layers.push(layer_circles);

				circle_feature = [];
			}
			if (metricRepresentation == "Cartes de chaleurs") {
				var source_heatmap = new ol.source.Vector({
					features : heatmap_feature
				});
				var layer_heatmap = new ol.layer.Heatmap({
					source : source_heatmap,
					blur : 30,
					// radius: function(feature){
					// if(feature.get('value') > 0){
					// return 30;
					// } else {
					// return 10;
					// }

					// },
					radius : 15,
					weight : function(feature) {
						return (feature.get('weight') / 100).toString();
					},
					visible : metricDisplayed,
					name : metricName
				});

				layers.push(layer_heatmap);

				heatmap_feature = [];
			}

		}

	}
	if (content.length != 0) {
		// var icon = new ol.style.Icon(/** @type {olx.style.IconOptions} */
		// ({
		// anchor: [0.5, 0.5],
		// src:
		// 'https://www.pragueeventscalendar.com/src/templates/images/web/marker_icon.png'
		// }));
		// icon.load();
		var source_vectors = new ol.source.Vector({
			features : vector_feature
		});
		var layer_vectors = new ol.layer.Vector({
			source : source_vectors,
			opacity : 0.7,
			/*
			 * style: new ol.style.Style({ image: icon }),
			 */
			name : "Vector_layer"
		});

		layers.splice(1, 0, layer_vectors);

	}

	// coloration si �volution
	if (action == "EVOLUTION") {
		colorEvolutionLayer(displayedNames, layers);
	}

	var map = new ol.Map({
		layers : layers,
		controls : controls,
		target : divContainer,
		view : new ol.View({
			zoom : 6,
			center : ol.proj.transform([ originLong, originLat ], 'EPSG:4326',
					'EPSG:900913')
		}),
		displayed : displayedNames
	});

	if (content.length != 0) {
		map.getView().fitExtent(source_vectors.getExtent(), map.getSize());
	} else {
		map.setSize([ map.getSize()[0], map.getSize()[1] - 400 ]); // pour
		// avoir le
		// bon rendu
		// : 400 =
		// toolbar +
		// graph
		// panel
	}

	globalMap = map;

	// map1.getView().setCenter(ol.proj.transform([originLong, originLat],
	// 'EPSG:4326', 'EPSG:900913'));
	// map1.getView().fitExtent(extent, map1.getSize());
	// globalMap.render();
	// globalMap.updateSize();

}

function updateComplexMap(JsonData, action) {
	var json = JSON.parse(JsonData);
	var displayed = json.display;
	var layers = globalMap.getLayers().getArray();
	var displayedNames = [];
	for (i = 0; i < layers.length; i++) {
		if (layers[i].get('name') == 'Tile'
				|| layers[i].get('name') == 'Vector_layer') {
			continue;
		}
		layers[i].setVisible(false);
		for (j = 0; j < displayed.length; j++) {
			if (displayed[j].name == layers[i].get('name')) {
				layers[i].setVisible(true);
				displayedNames.push(displayed[j].name);
			}
		}
	}
	if (action == "EVOLUTION") {
		colorEvolutionLayer(displayedNames, layers);
	}
	globalMap.set('displayed', displayedNames);
}

function changeTile(name) {
	var layers = globalMap.getLayers();
	switch (name) {
	case "none":
		layers.removeAt(0);
		layers.insertAt(0, new ol.layer.Tile({}));
		break;
	case "osm":
		layers.removeAt(0);
		layers.insertAt(0, layer_osm);
		break;
	case "watercolor":
		layers.removeAt(0);
		layers.insertAt(0, layer_watercolor);
		break;
	case "toner":
		layers.removeAt(0);
		layers.insertAt(0, layer_toner);
		break;
	case "toner-lite":
		layers.removeAt(0);
		layers.insertAt(0, layer_toner_lite);
		break;
	// case "osm-quest":
	// layers.removeAt(0);
	// layers.insertAt(0, layer_osm_quest);
	// break;
	// case "sat-quest":
	// layers.removeAt(0);
	// layers.insertAt(0, layer_sat_quest);
	// break;
	// case "hyb-quest":
	// layers.removeAt(0);
	// layers.insertAt(0, layer_hyb_quest);
	// break;
	default:
		layers.removeAt(0);
		layers.insertAt(0, layer_osm);
	}
}

// function hexToR(h) {return parseInt((cutHex(h)).substring(0,2),16)}
// function hexToG(h) {return parseInt((cutHex(h)).substring(2,4),16)}
// function hexToB(h) {return parseInt((cutHex(h)).substring(4,6),16)}
// function cutHex(h) {return (h.charAt(0)=="#") ? h.substring(1,7):h}

function resizeMap() {
	globalMap.updateSize();
}

function colorEvolutionLayer(displayed, layers) {
	var evoltable = [];
	for (c = 0; c < layers[1].getSource().getFeatures().length; c++) {
		evoltable.push(1);
	}
	for (i = 0; i < layers.length; i++) {
		if (layers[i].get('name') == 'Tile'
				|| layers[i].get('name') == 'Vector_layer') {
			continue;
		}

		for (j = 0; j < displayed.length; j++) {
			if (displayed[j] == layers[i].get('name')) {
				for (k = 0; k < layers[i].getSource().getFeatures().length; k++) {
					if (layers[i].getSource().getFeatures()[k].get('value') <= 0) {
						evoltable[k] = 0;
					}
				}
			}
		}
	}
	if (displayed.length == 0) {
		for (c = 0; c < layers[1].getSource().getFeatures().length; c++) {
			layers[1].getSource().getFeatures()[c].getStyle().fill_ = new ol.style.Fill(
					{
						color : 'rgba(255, 255, 255, 1)'
					});

		}
	} else {
		for (c = 0; c < layers[1].getSource().getFeatures().length; c++) {
			if (evoltable[c] == 0) {
				layers[1].getSource().getFeatures()[c].getStyle().fill_ = new ol.style.Fill(
						{
							color : 'rgba(200, 0, 0, 0.7)'
						});
			} else {
				layers[1].getSource().getFeatures()[c].getStyle().fill_ = new ol.style.Fill(
						{
							color : 'rgba(0, 200, 0, 0.7)'
						});
			}

		}
	}

	layers[1].changed();
	// globalMap.getLayers().getArray()[1].getSource().getFeatures().length
	// globalMap.getLayers().getArray()[2].getSource().getFeatures()[1].get('value')
	// globalMap.getLayers().getArray()[1].getSource().getFeatures()[1].getStyle().fill_
	// = "#fff"
}

function initTooltip() {

	var info = $('#info');
	info.tooltip({
		animation : false,
		trigger : 'manual',
		html : true,
		placement : "auto top"
	});

	var displayFeatureInfo = function(pixel) {
		info.css({
			left : pixel[0] + 'px',
			top : (pixel[1] - 15) + 'px'
		});
		var feature = globalMap
				.forEachFeatureAtPixel(
						pixel,
						function(feature) {
							return feature;
						},
						null,
						function(layer) {
							return (layer === globalMap.getLayers().getArray()[1])
									|| (globalMap.getLayers().getArray().length > 1
											&& globalMap.getLayers().getArray()[1]
													.getSource().getFeatures()[0]
													.getGeometry().flatCoordinates.length <= 2 && layer instanceof ol.layer.Vector);
						});
		if (feature) {
			info.tooltip('hide').attr(
					'title',
					"<h3>" + feature.get('name') + "</h3>"
							+ getDataDisplayedMetrics(feature.get('name')))
					.tooltip('fixTitle').tooltip('show');
		} else {
			info.tooltip('hide');
		}
	};

	globalMap.on('pointermove', function(evt) {
		if (evt.dragging) {
			info.tooltip('hide');
			return;
		}
		displayFeatureInfo(globalMap.getEventPixel(evt.originalEvent));
	});

	globalMap.on('click', function(evt) {
		displayFeatureInfo(evt.pixel);
	});

}

function getDataDisplayedMetrics(name) {
	var displayed = globalMap.get('displayed');
	if (!displayed) {
		return "";
	}
	var layers = globalMap.getLayers().getArray();
	var result = "";
	for (i = 0; i < layers.length; i++) {
		if (layers[i].get('name') == 'Tile'
				|| layers[i].get('name') == 'Vector_layer') {
			continue;
		}

		for (j = 0; j < displayed.length; j++) {
			if (displayed[j] == layers[i].get('name')) {
				var feature;
				for (b = 0; b < layers[i].getSource().getFeatures().length; b++) {
					if (layers[i].getSource().getFeatures()[b].get('name') == name) {
						feature = layers[i].getSource().getFeatures()[b];
						break;
					}
				}
				/**/result = result + "<h4>" + feature.get('metricName')
						+ "</h4>";
				var datas = feature.get('data');
				for (k = 0; k < datas.length; k++) {
					result = result + "<p>" + datas[k].text + "</p>";
				}
			}
		}
	}
	return result;
}

/** ----------------- OLD OPEN LAYERS VERSION ----------------------* */

function renderMap(divContainer, originLat, originLong, zoom, boundLeft,
		boundBottom, boundRight, boundTop, values, features, mapType, marker,
		minSize, maxSize, colorRanges, projorigin) {
	var fromProjection = new OpenLayers.Projection(projorigin);
	var toProjection = new OpenLayers.Projection("EPSG:900913");

	// create map element
	var extent = new OpenLayers.Bounds(boundLeft, boundBottom, boundRight,
			boundTop).transform(fromProjection, toProjection);
	var controls = [ new OpenLayers.Control.Navigation(),
			new OpenLayers.Control.PanZoomBar(),
			new OpenLayers.Control.MousePosition(),
			new OpenLayers.Control.LayerSwitcher(),
			new OpenLayers.Control.KeyboardDefaults() ];
	var proj = new OpenLayers.Projection(fromProjection);
	var map1 = new OpenLayers.Map({
		div : divContainer,
		maxExtent : extent,
		controls : controls,
		projection : proj,
		maxResolution : 256
	});
	var layers = [];

	// Osm layer
	layers.push(new OpenLayers.Layer.OSM());

	// Vector layer
	var layer_vectors;
	if (mapType == "point") {
		layer_vectors = new OpenLayers.Layer.Markers("Markers");
	} else {
		layer_vectors = new OpenLayers.Layer.Vector("vectorLayer", {
			displayInLayerSwitcher : true
		});
	}

	for (i = 0; i < features.length; i++) {
		var zone = features[i];
		var value = values[i];
		var points = [];

		for (var j = 0; j < zone.length; j++) {
			var p = new OpenLayers.Geometry.Point(zone[j][0], zone[j][1])
					.transform(fromProjection, toProjection);
			points.push(p);
		}

		// if polygon
		if (mapType == "polygon") {
			var ring = new OpenLayers.Geometry.LinearRing(points);

			var polygon = new OpenLayers.Geometry.Polygon([ ring ]);
			var poly = new OpenLayers.Feature.Vector(polygon);

			// put the style
			if (colorRanges == undefined) {
				if (value > 0) {
					poly.style = {
						fill : true,
						fillColor : "#85c879",
						fillOpacity : 0.5
					};
				} else if (value < 0) {
					poly.style = {
						fill : true,
						fillColor : "#f27742",
						fillOpacity : 0.5
					};
				} else {
					poly.style = {
						fill : true,
						fillColor : "#d9c16f",
						fillOpacity : 0.5
					};
				}
			} else {
				for (c = 0; c < colorRanges.length; c++) {
					var min = parseInt(colorRanges[c][1]);
					var max = parseInt(colorRanges[c][2]);

					if (value >= min && value <= max) {
						poly.style = {
							fill : true,
							fillColor : "#" + colorRanges[c][0],
							fillOpacity : 0.5
						};
					}
				}
			}

			layer_vectors.addFeatures(poly);
		} else if (mapType == "line") {
			// if line
			var line = new OpenLayers.Feature.Vector(
					new OpenLayers.Geometry.LineString(points));
			if (colorRanges == undefined) {

				if (value > 0) {
					line.style = {
						strokeColor : "#85c879",
						strokeWidth : 5
					};
				} else if (value < 0) {
					line.style = {
						strokeColor : "#f27742",
						strokeWidth : 5
					};
				} else {
					line.style = {
						strokeColor : "#d9c16f",
						strokeWidth : 5
					};
				}
			} else {
				for (c = 0; c < colorRanges.length; c++) {
					var min = parseInt(colorRanges[c][1]);
					var max = parseInt(colorRanges[c][2]);

					if (value >= min && value <= max) {
						line.style = {
							strokeColor : "#" + colorRanges[c][0],
							strokeWidth : 5
						};
					}
				}
			}

			layer_vectors.addFeatures(line);
		} else {
			var size = new OpenLayers.Size(21, 25);

			if (value > 0) {
				var size = new OpenLayers.Size(maxSize, maxSize);
			} else if (value < 0) {
				var size = new OpenLayers.Size(minSize, minSize);
			} else {
				var size = new OpenLayers.Size(maxSize - minSize, maxSize
						- minSize);
			}

			var offset = new OpenLayers.Pixel(-(size.w / 2), -size.h);
			var icon = new OpenLayers.Icon(marker, size, offset);

			for (i = 0; i < features.length; i++) {
				var point = features[i][0];

				var lonlat = point.toString().split(",");
				if (lonlat[0] != null && lonlat[0] != "" && lonlat[1] != null
						&& lonlat[1] != "") {
				var marker = new OpenLayers.Feature({
					geometry : new OpenLayers.Geometry.Point(OpenLayers.Projection.transform([
					                    							parseFloat(lonlat[0]), parseFloat(lonlat[1]) ],
					                    							fromProjection, toProjection))
				});
				layer_vectors.addFeatures(marker);
				}

			}
			layers.push(layer_vectors);
		}

	}

	layers.push(layer_vectors);

	// display map
	map1.addLayers(layers);

	map1.zoomToExtent(extent);
	map1.setCenter(new OpenLayers.LonLat(originLong, originLat).transform(
			fromProjection, toProjection), zoom);
}

function previewMap(divContainer, originLat, originLong, zoom, boundTop,
		boundLeft, boundBottom, boundRight, projection, dataSetType, features,
		marker) {
	var fromProjection = new OpenLayers.Projection(projection); // projection
	var toProjection = new OpenLayers.Projection("EPSG:900913");

	document.getElementById(divContainer).innerHTML = "";

	// create map element
	var extent = new OpenLayers.Bounds(boundLeft, boundBottom, boundRight,
			boundTop).transform(fromProjection, toProjection);
	var controls = [ new OpenLayers.Control.Navigation(),
			new OpenLayers.Control.PanZoomBar(),
			new OpenLayers.Control.MousePosition(),
			new OpenLayers.Control.LayerSwitcher(),
			new OpenLayers.Control.KeyboardDefaults() ];
	var proj = new OpenLayers.Projection(fromProjection);
	var map1 = new OpenLayers.Map({
		div : divContainer,
		maxExtent : extent,
		controls : controls,
		projection : proj,
		maxResolution : 256
	});
	var layers = [];

	// Osm layer
	layers.push(new OpenLayers.Layer.OSM());

	if (dataSetType == "polygon") {
		// Vector layer
		var layer_vectors = new OpenLayers.Layer.Vector("vectorLayer", {
			displayInLayerSwitcher : true
		});

		for (i = 0; i < features.length; i++) {
			var zone = features[i];
			var points = [];
			for (var j = 0; j < zone.length; j++) {
				var p = new OpenLayers.Geometry.Point(zone[j][0], zone[j][1])
						.transform(fromProjection, toProjection);
				points.push(p);
			}
			var ring = new OpenLayers.Geometry.LinearRing(points);
			var poly = new OpenLayers.Feature.Vector(
					new OpenLayers.Geometry.Polygon([ ring ]));

			poly.style = {
				fill : true,
				fillColor : "#3399FF",
				fillOpacity : 0.5
			}; // Modifer la couleur important (bleu)
			layer_vectors.addFeatures(poly);
		}
		layers.push(layer_vectors);
	} else if (dataSetType == "line") {

		var layer_vectors = new OpenLayers.Layer.Vector("Line layer", {
			displayInLayerSwitcher : true
		});

		for (i = 0; i < features.length; i++) {
			var zone = features[i];
			var points = [];
			for (var j = 0; j < zone.length; j++) {
				var p = new OpenLayers.Geometry.Point(zone[j][0], zone[j][1])
						.transform(fromProjection, toProjection);
				points.push(p);
			}
			var poly = new OpenLayers.Feature.Vector(
					new OpenLayers.Geometry.LineString(points));

			poly.style = {
				strokeColor : "#3399FF",
				strokeWidth : 5
			}; // Modifer la couleur important (bleu)
			layer_vectors.addFeatures(poly);
		}
		layers.push(layer_vectors);
	} else { // point

		// Marker layer
		var layer_markers = new OpenLayers.Layer.Markers("Markers");

		for (i = 0; i < features.length; i++) {
			var size = new OpenLayers.Size(21, 25);
			var offset = new OpenLayers.Pixel(-(size.w / 2), -(size.h / 2));
			var icon = new OpenLayers.Icon(marker, size, offset);
			var point = features[i][0];

			var lonlat = point.toString().split(",");
			var p = new OpenLayers.Geometry.Point(lonlat[0], lonlat[1])
					.transform(fromProjection, toProjection);
			layer_markers.addMarker(new OpenLayers.Marker(
					new OpenLayers.LonLat(p.x, p.y), icon));

		}
		layers.push(layer_markers);

	}

	// display map
	map1.addLayers(layers);

	map1.zoomToExtent(extent);
	map1.setCenter(new OpenLayers.LonLat(originLong, originLat).transform(
			fromProjection, toProjection), zoom);
}
