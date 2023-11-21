function Timer(handleId, delay, label, startOnDraw){
	this.handleId = handleId;
	this.delay = delay;
	this.time = delay;
	
	this.nodeCounter = null;
	this.buttonOn = null;
	this.buttonOff = null;
	this.buttonManual = null;
	
	this.ctimeout = null;
	if (label == undefined){
		this.label = "Next Refresh in ";
	}
	else{
		this.label = label;
	}
	
	this.startOnDraw = startOnDraw;
}

Timer.prototype.formatedValue = function(){

	var currentTime = new Date();
	currentTime.setTime(this.time);
	
	var l =  " " ;
	if (('' + currentTime.getUTCHours()).length < 2){
		l = l + "0" + currentTime.getUTCHours() ;
	}
	else{
		l = l + currentTime.getUTCHours() ;
	}
	
	l = l + ":";
	
	if (('' + currentTime.getUTCMinutes()).length < 2){
		l = l + "0" + currentTime.getUTCMinutes() ;
	}
	else{
		l = l + currentTime.getUTCMinutes() ;
	}
	l = l + ":";
	if (('' + currentTime.getUTCSeconds()).length < 2){
		l = l + "0" + currentTime.getUTCSeconds() ;
	}
	else{
		l = l + currentTime.getUTCSeconds() ;
	}
	if (this.ctimeout == null || this.ctimeout == undefined){
		var rootN = document.createElement('p');
		rootN.appendChild(document.createTextNode(this.label));
		var span = document.createElement('span');
		span.appendChild(document.createTextNode(l));
		rootN.appendChild(span);
		
		span.setAttribute("style", "color:red");
		
		return rootN;
	}
	else{
		return document.createTextNode(this.label + l);
	}
}


Timer.prototype.CountDown = function(){
	if (this.nodeCounter  == undefined || this.nodeCounter == null){
		return;
	}
	
	this.time = this.time - 1000;
	__timerCleaner(this.nodeCounter);
	
	/*
	 * remove text
	 */
	
	this.nodeCounter.removeChild(this.nodeCounter.childNodes[0]);
	this.nodeCounter.appendChild((this.formatedValue()));
	
	var thisObj = this;
	
	if (this.time > 0){
		this.ctimeout = setTimeout(function() { thisObj.CountDown();},3000);
	}
	else{
		location.href=location.href;
		this.startCountDown();
	}
	
	
}
Timer.prototype.startCountDown = function(){
	this.time = this.delay;
	var thisObj = this;
	this.ctimeout = setTimeout(function() {thisObj.CountDown(); if (thisObj.time <= 0){thisObj.startCountDown();}},3000);
	/*
	 * update buttons state
	 
	if (navigator.userAgent.toLowerCase().indexOf("msie") != -1){
		this.buttonOn.disabled=false;
		this.buttonOff.disabled=true;
		
	}
	else{
		this.buttonOn.setAttribute("disabled", "true");
		this.buttonOff.setAttribute("disabled", "false");
	}*/
}

Timer.prototype.stopCountDown = function(){
	clearTimeout(this.ctimeout);
	this.ctimeout = null;
	this.nodeCounter.removeChild(this.nodeCounter.childNodes[0]);
	this.nodeCounter.appendChild((this.formatedValue()));
}

Timer.prototype.Draw = function(){
	var node = document.getElementById(this.handleId);
	__timerCleaner(node);
	
	var table = document.createElement('table');
	
	var body = document.createElement('tbody');
	table.appendChild(body);
	
	var lineControl = document.createElement('tr');
	var lineTimer = document.createElement('tr');
	var cellOn = document.createElement('td');
	var cellOff = document.createElement('td');
	
	this.nodeCounter = document.createElement('td');
	
	this.buttonOn = document.createElement('a');
	var pict = document.createElement('img');
	pict.setAttribute("src", "../../freedashboardRuntime/timer/on.png");
	pict.setAttribute("title", "Start Timer");
	
	this.buttonOn.appendChild(pict);
		
	this.buttonOff = document.createElement('a');
	
	pict = document.createElement('img');
	pict.setAttribute("src", "../../freedashboardRuntime/timer/off.png");
	pict.setAttribute("title", "Stop Timer");
	
	this.buttonOff.appendChild(pict);

	this.buttonManual = document.createElement('a');
	pict = document.createElement('img');
	pict.setAttribute("src", "../../freedashboardRuntime/timer/refresh.png");
	pict.setAttribute("title", "Refresh Page");
	
	this.buttonManual.appendChild(pict);
	
	lineControl.appendChild(lineTimer);

	this.nodeCounter.appendChild(this.formatedValue());
	
	lineTimer.appendChild(this.nodeCounter);
	body.appendChild(lineTimer);
	
	cellOn.appendChild(this.buttonOn);
	cellOff.appendChild(this.buttonOff);
	cellOff.appendChild(this.buttonManual);
	
	
	lineControl.appendChild(cellOn);
	lineControl.appendChild(cellOff);
	
	body.appendChild(lineControl);
	
	node.appendChild(table);
	
	
	/*
	 * register callBacks and styles
	 */
	if (navigator.userAgent.toLowerCase().indexOf("msie") != -1){
		var thisObj = this;
		this.buttonOn.onmousedown = function(){thisObj.startCountDown();};
		this.buttonOff.onmousedown = function(){thisObj.stopCountDown();};
		this.buttonManual.onmousedown = function(){location.href=location.href;};
		this.buttonOff.disabled=true;
		this.nodeCounter.colSpan = 3;
		
		table.className="_timer";
		pict.className="_timer_button";	
		pict.className="_timer_button";
		pict.className="_timer_button";
	}
	else{
		this.buttonOn.setAttribute("onclick", this.handleId + ".startCountDown()");
		this.buttonOff.setAttribute("onclick", this.handleId + ".stopCountDown()");
		this.buttonManual.setAttribute("onclick", "location.href=location.href");
		//this.buttonOff.setAttribute("disabled", "true");
		this.nodeCounter.setAttribute("colSpan","3");
		
		table.setAttribute("class", "_timer");
		pict.setAttribute("class", "_timer_button");
		pict.setAttribute("class", "_timer_button");
		pict.setAttribute("class", "_timer_button");
	}
	
	if (this.startOnDraw){
		this.startCountDown();
	}
}


function __timerCleaner(d){
	var bal=d.getElementsByTagName('*');

	for(i=0;i<bal.length;i++){
		a=bal[i].previousSibling;
		if(a && a.nodeType==3){
			 __timerGo(a);
		}
   
		b=bal[i].nextSibling;
		if(b && b.nodeType==3){
			 __timerGo(b);
		}
	}
	
	
	return d;
}
function __timerGo(c){
	if(!c.data.replace(/\s/g,'')){
		c.parentNode.removeChild(c);
	}
}



