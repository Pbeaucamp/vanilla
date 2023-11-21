function MetricElement(id, name, periodicity) {
	this.metricId = id;
	this.metricName = name;
	this.metricPeriod = periodicity;
}

MetricElement.prototype.createOptionElement = function() {
	var opt = document.createElement("option");
	
	opt.value = this.metricId;
	opt.label = this.metricName;
	opt.innerHTML = this.metricName;
	
	return opt;
}