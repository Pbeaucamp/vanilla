function renderChart(type, xml, divContainer, width, height) {
	var chart = new FusionCharts({
	"type": type,
    "renderAt": divContainer,
    "width": width,
    "height": height,
    "dataFormat": "xml",
    "dataSource": xml
	});

	chart.render();
}