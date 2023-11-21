function FMMap(divId) {
	this.divId = divId;
}

FMMap.prototype.createMetricList = function(metricElements ,selected) {
	
	var metricDiv = document.createElement("div");
	metricDiv.className = "metricDiv"
	
	var lblMet = document.createElement("label");
	lblMet.value = "Select a metric : ";
	lblMet.innerHTML = "Select a metric : ";
	
	var select = document.createElement("select");
	select.id = this.divId + "_metricSelect";
	
	for(var i = 0 ; i < metricElements.length ; i++) {
		var opt = metricElements[i].createOptionElement();
		if(metricElements[i].metricId == selected) {
			opt.selected = true;
		}
		select.appendChild(opt);
	}
	
	metricDiv.appendChild(lblMet);
	metricDiv.appendChild(select);
	
	document.getElementById(this.divId).appendChild(metricDiv);
	
}

FMMap.prototype.createDatesLists = function(period, possibleYears, dateString) {
	this.createDateLists(period, 
			possibleYears, 
			new Array("01","02","03","04","05","06","07","08","09","10","11","12"), 
			new Array("01","02","03","04","05","06","07","08","09","10"
					,"11","12","13","14","15","16","17","18","19","20"
					,"21","22","23","24","25","26","27","28","29","30","31"), dateString);
}

FMMap.prototype.createEvents = function() {
	
	var thisObj = this;
	
	var btn = document.createElement("input");
	btn.type = "button";
	btn.value = "Refresh map";
	btn.onclick = function () { 
		var year = document.getElementById(thisObj.divId + "_yearSelect").value;
		var month = document.getElementById(thisObj.divId + "_monthSelect").value;
		var day = document.getElementById(thisObj.divId + "_daySelect").value;
		
		var met = document.getElementById(thisObj.divId + "_metricSelect").value;
		var date = year + "-" + month + "-" + day;
		
		setParameter(thisObj.divId + "_metric", met);
		setParameter(thisObj.divId + "_date", date);
		setLocation();
	}
	btn.className = "metricDate-btn";
	document.getElementById(this.divId).appendChild(btn);
	
}

FMMap.prototype.createDateLists = function(period, years, months, days, dateString) {
	
	var selectedYear = null;
	var selectedMonth = null;
	var selectedDay = null;
	
	if(dateString != null && dateString != "null") {
		var dateParts = dateString.split("-");
		var selectedYear = dateParts[0];
		var selectedMonth = dateParts[1];
		var selectedDay = dateParts[2];
	}
	
	var dateDiv = document.createElement("div");
	dateDiv.className = "dateDiv";
	
	var datebr = document.createElement("br");
	var yearbr = document.createElement("br");
	var monthbr = document.createElement("br");
	var daybr = document.createElement("br");
	
	var rootDiv = document.getElementById(this.divId);
	
	var lblDate = document.createElement("label");
	lblDate.value = "Select a date : ";
	lblDate.innerHTML = "Select a date : ";
	
	var lblYear = document.createElement("label");
	lblYear.value = "Year : ";
	lblYear.innerHTML = "Year : ";
	lblYear.className = "dateDiv-label-box";
	
	//var yearSelect = document.createElement("select");
	var ieYearDiv = document.createElement("span");
	var yearSelect = "<select id=\""+this.divId + "_yearSelect\">";
	//yearSelect.id = this.divId + "_yearSelect";
	var yearhtml = "";
	
	for(var i = 0, ys = years.length ; i < ys ; i++) {
		
		var y = years[i];
		
//		var opt = document.createElement("option");
//		opt.label = years[i];
//		opt.value = years[i];
//		opt.innerHTML = years[i];
//		yearhtml += opt.outerHTML;
		
		if(selectedYear != null && selectedYear == y) {
			yearhtml += "<option label='";
			yearhtml += y;
			yearhtml += "' value='";
			yearhtml += y;
			yearhtml += "' selected='selected'>";
			yearhtml += y;
			yearhtml += "</option>";
			
		}
		else {
			yearhtml += "<option label='";
			yearhtml += y;
			yearhtml += "' value='";
			yearhtml += y;
			yearhtml += "'>";
			yearhtml += y;
			yearhtml += "</option>";
		}
		
		//yearSelect.appendChild(opt);
	}
	//yearSelect.innerHTML = yearhtml;
	yearSelect += yearhtml + "</select>";
	
	ieYearDiv.innerHTML = yearSelect;
	
	dateDiv.appendChild(lblDate);
	dateDiv.appendChild(datebr);
	dateDiv.appendChild(lblYear);
	dateDiv.appendChild(ieYearDiv);
	dateDiv.appendChild(yearbr);
	
//	if(this.showMonths(period)) {
		
	var lblMonth = document.createElement("label");
	lblMonth.value = "Month : ";
	lblMonth.innerHTML = "Month : ";
	lblMonth.className = "dateDiv-label-box";
	
		var monthhtml = "";
		
		var ieMonthSpan = document.createElement("span");
		
		var monthSelect = "<select id=\""+this.divId + "_monthSelect\">";
		//monthSelect.id = this.divId + "_monthSelect";
		
		for(var m = 0, ms = months.length ; m < ms ; m++) {
			
			var mon = months[m];
			
			if(selectedMonth != null && selectedMonth == mon) {
				monthhtml += "<option label='" + mon + "' value='" + mon + "' selected='selected'>" + mon + "</option>";
			}
			else {
				monthhtml += "<option label='" + mon + "' value='" + mon + "'>" + mon + "</option>";
			}
			
//			var optM = document.createElement("option");
//			optM.label = months[m];
//			optM.value = months[m];
//			optM.innerHTML = months[m];
//			monthhtml += optM.innerHTML;
			//monthSelect.appendChild(optM);
		}
		
		monthSelect += monthhtml;
		ieMonthSpan.innerHTML = monthSelect + "</select>";
		dateDiv.appendChild(lblMonth);
		dateDiv.appendChild(ieMonthSpan);
		dateDiv.appendChild(monthbr);
//	}
	
//	if(this.showDays(period)) {
		
		var lblDay = document.createElement("label");
		lblDay.value = "Day : ";
		lblDay.innerHTML = "Day : ";
		lblDay.className = "dateDiv-label-box";
		
		var dayhtml = "";
		var ieDaySpan = document.createElement("span");
		var daySelect = "<select id=\""+this.divId + "_daySelect\">";
		
		
		for(var d = 0, ds = days.length ; d < ds ; d++) {
			
			var da = days[d];
			
			if(selectedDay != null && selectedDay == da) {
				dayhtml += "<option label='" + da + "' value='" + da + "' selected='selected'>" + da + "</option>";
			}
			else {
				dayhtml += "<option label='" + da + "' value='" + da + "'>" + da + "</option>";
			}
//			var optD = document.createElement("option");
//			optD.label = days[d];
//			optD.value = days[d];
//			optD.innerHTML = days[d];
//			dayhtml += optD.outerHTML;
			//daySelect.appendChild(optD);
		}
		
		daySelect += dayhtml;
		ieDaySpan.innerHTML = daySelect + "</select>";
		dateDiv.appendChild(lblDay);
		dateDiv.appendChild(ieDaySpan);
		dateDiv.appendChild(daybr);
//	}
		
		rootDiv.appendChild(dateDiv);
	
}

FMMap.prototype.showMonths = function(period) {
	var possiblePeriodes = new Array("BIANNUAL","QUARTER","MONTH","WEEK","DAY","HOUR","MINUTES","SECONDES","REALTIME");
	if(possiblePeriodes.indexOf(period) != -1) {
		return true;
	}
	return false;
}

FMMap.prototype.showDays = function(period) {
	var possiblePeriodes = new Array("WEEK","DAY","HOUR","MINUTES","SECONDES","REALTIME");
	if(possiblePeriodes.indexOf(period) != -1) {
		return true;
	}
	return false;
}

FMMap.prototype.createMetricsSampleData = function() {

	var metrics = new Array();
	
	var m1 = new MetricElement("compteur 1", 1, "YEAR");
	var m2 = new MetricElement("compteur 2", 2, "WEEK");
	
	metrics[0] = m1;
	metrics[1] = m2;
	
	return metrics;
}