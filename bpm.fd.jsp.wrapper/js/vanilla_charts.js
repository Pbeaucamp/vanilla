function renderChart(type, xml, divContainer, width, height) {
	var chart = new FusionCharts({
		"type": type,
	    "renderAt": divContainer,
	    "width": width,
	    "height": height,
	    "dataFormat": "xml",
	    "dataSource": xml
	});
	window.actualChart = chart;
	chart.render();
}
var fusionChart;
function renderChartJs(json, divContainer, width, height) {
	if(fusionChart != undefined) {
		//fusionChart.destroy();
	}
	
	//json = json.replace('grape', 'blue');
	
	chartXml = JSON.parse(json);
	var ctx = document.getElementById(divContainer).getContext('2d');
	fusionChart = new Chart(ctx, chartXml);
}

function getImageData(div) {
	return window.actualChart.getSVGString();
}