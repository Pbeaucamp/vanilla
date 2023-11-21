function decisionTree(container, jsonTree, height, width) {

	var cluster = d3.layout.cluster().size([ width, height - 180 ]);

	var diagonal = d3.svg.diagonal().projection(function(d) {
		return [ d.x, d.y ];
	});

	var svg = d3.select("#" + container).append("svg").attr("width", width)
			.attr("height", height).append("g").attr("transform",
					"translate(0,20)");

	json = JSON.parse(jsonTree);

	var nodes = cluster.nodes(json), links = cluster.links(nodes);

	var link = svg.selectAll(".link").data(links).enter().append("path").attr(
			"class", "link").attr("d", diagonal).style("stroke", "#ccc").style(
			"fill", "none").style("stroke-width", "1.5px");

	var node = svg.selectAll(".node").data(nodes).enter().append("g").attr(
			"class", "node").attr("transform", function(d) {
		return "translate(" + d.x + "," + d.y + ")";
	});

	var circle = node.append("circle").attr("r", 15).style("stroke",
			"steelblue").style("fill", "#fff").style("stroke-width", "1.5px");

	node.append("text").attr("dx", -4).attr("dy", 3).text(function(d) {
		return d.name;
	});

	node.append("text").text(function(d) {
		return (d.field + " " + d.operator + "\n" + d.value);
	}).attr("font-family", "sans-serif").attr("font-size", "14px").attr("dx",
			10).attr("dy", -30);
	var rectangle = node.selectAll("rect").data(nodes).enter().append("rect");

	rectangle.attr("width", 80).attr("height", 50).attr("x", -40).attr("y", 16)
			.attr("stroke", "steelblue").style("fill", "#fff").style(
					"stroke-width", "1.5px");

	node.append("text").text(function(d) {
		return "Effectif : ";
	}).attr("font-family", "sans-serif").attr("width", 90).attr("dx", 0).attr(
			"dy", 40).attr("font-size", "16px").attr("text-anchor", "middle");

	node.append("text").attr("width", 90).attr("dx", 0).attr("dy", 60).text(
			function(d) {
				return d.score;
			}).attr("font-family", "sans-serif").attr("font-size", "16px")
			.attr("text-anchor", "middle");

	node.on("mouseover", function(d) {
		mouseOverMirror(d.id.toString(), d.x.toString(), d.y.toString());
	});

	node.on("mouseout", function(d) {
		mouseOutMirror();
	});

	d3.select(self.frameElement).style("height", height + "px");
}
