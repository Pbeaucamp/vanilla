//FdChart.prototype.render = function render(chartXml){
//	this.fusionChart.setXMLData(chartXml);
//	this.fusionChart.render(this.id);
//	
//	/*this.fusionChart.addEventListener('internal.animationComplete', function(e, a) {
//        e.sender.resizeTo(this.width, this.height);
//        this.fusionChart.removeEventListener('internal.animationComplete');
//      });*/
//	
//	doExportThings();
//}
FdChart.prototype.render = function render(chartXml){
	chartXml = JSON.parse(chartXml);

	if (chartXml) {
//		chartXml.options.legendCallback = function(chart) {
//			var text = [];
//			text.push('<ul class=\"' + chart.id + '-legend\">');
//			for (var i = 0; i < chart.data.datasets.length; i++) {
//				text.push('<li><span style=\"background-color:' + chart.data.datasets[i].backgroundColor[0] + '; height: 15px; width: 15px; display: inline-block; margin-left: 5px; margin-right: 5px;\""></span>');
//		        if (chart.data.datasets[i].label) {
//		            text.push(chart.data.datasets[i].label);
//		        }
//		        text.push('</li>');
//		    }
//		    text.push('</ul>');
//		    return text.join('');
//		}
		
		chartXml.options.legendCallback = function(chart) {
	          var text = [];
	          var $legend = $('<ul class="' + chart.id + '-legend"></ul>');
	          var items = chart.legend.legendItems;
	          $.each(items, function(prop,item) {
	            var $item = $('<li>'+item.text+'</li>');
	            $item
	              .attr('title', item.text)
	              .prepend('<span class="legend-item" style="background-color:'+item.fillStyle+'; height: 15px; width: 15px; display: inline-block; margin-left: 5px; margin-right: 5px;\"></span>')
	              .on('click', function() {
	                $(this).toggleClass('excluded');
	                var index = prop;
	                var ci = returnChart.chart;
	                var meta = ci.legend.legendItems[index];
	                ci.data.datasets[0]._meta[ci.id].data[index].hidden = (!meta.hidden)
	                  ? true
	                  : null;
	                // We changed a dataset ... rerender the chart
	                ci.update();
	              });
	            $legend.append($item);
	          });
	          return $legend[0].innerHTML;
	        }
	}
	
	var ctx = document.getElementById(this.chartId).getContext('2d');
	
	fusionChart = new Chart(ctx, chartXml);
	
	arryCharts[this.chartId] = fusionChart;
	
	document.getElementById(this.chartId).onclick = function (event){
		var activePoints = arryCharts[this.id].getElementsAtEvent(event);
        var firstPoint = activePoints[0];
        var label = arryCharts[this.id].data.catValues[firstPoint._index];
        var value = arryCharts[this.id].data.datasets[firstPoint._datasetIndex].data[firstPoint._index];
        
        setParameter(this.id.replace("_canvas",""), label, true);
	};

	if (chartXml) {
		var myLegendContainer = document.getElementById(this.legendId);
		myLegendContainer.innerHTML = fusionChart.generateLegend();
		
		//Not working for now, we remove the click on the legend for now
//		// bind onClick event to all LI-tags of the legend
//		var legendItems = myLegendContainer.getElementsByTagName('li');
//		for (var i = 0; i < legendItems.length; i += 1) {
//		  legendItems[i].addEventListener("click", legendClickCallback, false);
//		}
	}
	
	/*this.fusionChart.addEventListener('internal.animationComplete', function(e, a) {
        e.sender.resizeTo(this.width, this.height);
        this.fusionChart.removeEventListener('internal.animationComplete');
      });*/
	
	//doExportThings();
}

//FdChart.prototype.update= function update(chartSWF) {	
//	var width = this.fusionChart.width;
//	var height = this.fusionChart.height;
//	var chart1 = new FusionCharts(chartSWF, this.id + '_js', width, height, '0', '0'); 
//	
//	chart1.setDataXML(this.fusionChart.getXML());
//	chart1.render(this.id);
//}
FdChart.prototype.update= function update(chartSWF) {	
//	var width = this.fusionChart.width;
//	var height = this.fusionChart.height;
//	var chart1 = new FusionCharts(chartSWF, this.id + '_js', width, height, '0', '0'); 
//	
//	chart1.setDataXML(this.fusionChart.getXML());
//	chart1.render(this.id);
}

/*FdHighChart.prototype.render = function render(chartXml){
	$('#'+id+'').highcharts({+chartXml+});
	
	doExportThings();
}*/

function doExportThings() {
	
}

function drillChart(event, array){
	alert(array);
}

/*
FdChart.prototype.drillUp = function drillUp(){
	getComponentHTML(this.id, false);
	var chartId = this.id;
	//if ($("#"+this.id + "_button")==null || $("#"+this.id + "_button")==undefined){
		$("#"+this.id).append("<div id='" + this.id+"_button'>Hello</div>");
		$("#"+this.id + "_button").click(function(){drillUp(chartId);});
	//}
}*/

/**
 * fusionChart : the FusionCHart variable
 * id : the id of the html where the chart will be renderered
 * chartXml : the inition chartXml
 */

var fusionChart;
var id;
var arryCharts = new Object();

function FdChart(idd, chartXml, legendId){
	var ctx = document.getElementById(idd).getContext('2d');
	id = idd;
	this.chartId = idd;
	this.legendId = legendId;
	
	if (chartXml) {
//		chartXml.options.legendCallback = function(chart) {
//			var text = [];
//			text.push('<ul class=\"' + chart.id + '-legend\">');
//			for (var i = 0; i < chart.data.datasets.length; i++) {
//				text.push('<li><span style=\"background-color:' + chart.data.datasets[i].backgroundColor[0] + '; height: 15px; width: 15px; display: inline-block; margin-left: 5px; margin-right: 5px;\""></span>');
//		        if (chart.data.datasets[i].label) {
//		            text.push(chart.data.datasets[i].label);
//		        }
//		        text.push('</li>');
//		    }
//		    text.push('</ul>');
//		    return text.join('');
//		}
		
		chartXml.options.legendCallback = function(chart) {
	          var text = [];
	          var $legend = $('<ul class="' + chart.id + '-legend"></ul>');
	          var items = chart.legend.legendItems;
	          $.each(items, function(prop,item) {
	            var $item = $('<li>'+item.text+'</li>');
	            $item
	              .attr('title', item.text)
	              .prepend('<span class="legend-item" style="background-color:'+item.fillStyle+'; height: 15px; width: 15px; display: inline-block; margin-left: 5px; margin-right: 5px;\"></span>')
	              .on('click', function() {
	                $(this).toggleClass('excluded');
	                var index = prop;
	                var ci = returnChart.chart;
	                var meta = ci.legend.legendItems[index];
	                ci.data.datasets[0]._meta[ci.id].data[index].hidden = (!meta.hidden)
	                  ? true
	                  : null;
	                // We changed a dataset ... rerender the chart
	                ci.update();
	              });
	            $legend.append($item);
	          });
	          return $legend[0].innerHTML;
	        }
	}
	
	fusionChart = new Chart(ctx, chartXml);
	
	arryCharts[id] = fusionChart;
	
	document.getElementById(id).onclick = function (event){
		var activePoints = arryCharts[this.id].getElementsAtEvent(event);
        var firstPoint = activePoints[0];
        var label = arryCharts[this.id].data.catValues[firstPoint._index];
        var value = arryCharts[this.id].data.datasets[firstPoint._datasetIndex].data[firstPoint._index];
        
        setParameter(this.id.replace("_canvas",""), label, true);
        
	};
	
	if (chartXml) {
		var myLegendContainer = document.getElementById(this.legendId);
		myLegendContainer.innerHTML = fusionChart.generateLegend();

		//Disable for now
//		// bind onClick event to all LI-tags of the legend
//		var legendItems = myLegendContainer.getElementsByTagName('li');
//		for (var i = 0; i < legendItems.length; i += 1) {
//		  legendItems[i].addEventListener("click", legendClickCallback, false);
//		}
	}
	
//	if (chartXml != undefined){
//		this.render(chartXml);
//	}
	
}

function legendClickCallback(event) {
	  event = event || window.event;

	  var target = event.target || event.srcElement;
	  while (target.nodeName !== 'LI') {
	      target = target.parentElement;
	  }
	  var parent = target.parentElement;
	  var chartId = parseInt(parent.classList[0].split("-")[0], 10);
	  var chart = Chart.instances[chartId];
	  var index = Array.prototype.slice.call(parent.children).indexOf(target);

	  chart.legend.options.onClick.call(chart, event, chart.legend.legendItems[index]);
	  if (chart.isDatasetVisible(index)) {
	    target.classList.remove('hidden');
	  } else {
	    target.classList.add('hidden');
	  }
	}

/*function FdHighChart(fusionChart, id, chartXml){
	this.id = id;
	this.fusionChart = fusionChart;
	
	if (chartXml != undefined){
		this.render(chartXml);
	}
}*/




FdFusionChart.prototype.render = function renderFusion(chartXml){
	this.fusionChart.setXMLData(chartXml);
	this.fusionChart.render(this.id);
	doExportThings();
}

FdFusionChart.prototype.update= function updateFusion(chartSWF) {	
	var width = this.fusionChart.width;
	var height = this.fusionChart.height;
	var chart1 = new FusionCharts(chartSWF, this.id + '_js', width, height, '0', '0'); 
	
	chart1.setDataXML(this.fusionChart.getXML());
	chart1.render(this.id);
}

function doExportThings() {
	
}

/**
 * fusionChart : the FusionCHart variable
 * id : the id of the html where the chart will be renderered
 * chartXml : the inition chartXml
 */
function FdFusionChart(fusionChart, id, chartXml, width, height){
	this.id = id;
	this.fusionChart = fusionChart;
	this.width = width;
	this.height = height;
	
	if (chartXml != undefined){
		this.render(chartXml);
	}
}