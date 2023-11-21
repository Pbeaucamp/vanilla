StackableCell.prototype.render = function (content){
	var div = document.getElementById(this.id);
	for(var i=0; i<div.children.length;i++){
		if (div.children[i].nodeName.toLowerCase() == "div"){
			if ((div.children[i].getAttribute('class') +'').contains('buttons_') || (div.children[i].getAttribute('classname') + '').contains('buttons_')){
				continue;	
			}
			if (div.children[i].id == content){
				div.children[i].style.display='block';
				//div.children[i].style.style.display='visible';
				div.children[i].style.position='relative';
				div.children[i].style.width='';
				div.children[i].style.height='';
			}
			else{
				div.children[i].style.display='none';
				//div.children[i].style.visibility='hidden';
				div.children[i].style.position='relative';
				div.children[i].style.width='';
				div.children[i].style.height='';
				
			}
		}
		
	}
}

function StackableCell(id, elementsType, elementsName, actual, type) {

	this.id = id;
	this.actual = actual;
	this.elementsType = elementsType;
	this.elementsName = elementsName;
	this.type = type;
	
	this.render(elementsName[0]);
}

StackableCell.prototype.changeSelection = function(selectionPosition, clickedButton) {
	
	if (navigator.userAgent.toLowerCase().indexOf("msie") != -1) {
		if(this.type == "bottom" || this.type == "top") {
			var tr = clickedButton.parentNode.parentNode;
			for(k = 0 ; k < tr.children.length ; k++) {
				if(tr.children[k].firstChild.title == clickedButton.title) {
					if(this.type == "bottom") {
						selectionPosition = k;
					}
					else {
						selectionPosition = k + 1;
					}
					break;
				}
			}
		}
		else {
			var tr = clickedButton.parentNode;
			for(k = 0 ; k < tr.children.length ; k++) {
				if(tr.children[k].title == clickedButton.title) {
					if(this.type == "right") {
						selectionPosition = k;
					}
					else {
						selectionPosition = k + 1;
					}
					break;
				}
			}
		}
	}
	
	if(!(this.actual == selectionPosition)) {
	
		var stack = document.getElementById(this.id);
		var actualDiv = stack.children[this.actual];
		var selectedDiv = stack.children[selectionPosition];
		
		if(selectedDiv.firstChild && selectedDiv.firstChild.tagName == "TABLE") {
			selectedDiv.firstChild.firstChild.firstChild.children[selectionPosition].firstChild.removeAttribute("className");
			selectedDiv.firstChild.firstChild.firstChild.children[selectionPosition].firstChild.removeAttribute("class");
		}
		else {
			selectedDiv.removeAttribute("className");
			selectedDiv.removeAttribute("class");
		}
		
		var buttonDiv = clickedButton.parentNode;
		
		if(buttonDiv.tagName == "TD") {
			for(j = 0 ; j < buttonDiv.parentNode.children.length ; j++) {
				var td = buttonDiv.parentNode.children[j].firstChild;
				td.removeAttribute("className");
				td.removeAttribute("class");
			}
		}
		
		else {
			for(i = 0 ; i < buttonDiv.children.length ; i++) {
				var btn = buttonDiv.children[i];
				if(btn.tagName == "INPUT") {
					btn.removeAttribute("className");
					btn.removeAttribute("class");
				}
			}
		}
		
		clickedButton.className="selected";
		clickedButton.setAttribute("class", "selected");
		
//		actualDiv.setAttribute("className", "hide");
//		actualDiv.setAttribute("class", "hide");
		
		this.actual = selectionPosition;
		this.render(this.elementsName[this.actual]);
//		if(this.type == "top" || this.type == "left") {
//			
//			setParameter(this.id + "_index", this.actual - 1);
//		}
//		else {
//			setParameter(this.id + "_index", this.actual);
//		}
	}
}

/*use this to draw the button part of the cell*/
StackableCell.prototype.draw = function() {

	//div for the buttons
	var stack = document.getElementById(this.id);
	var buttonDiv = document.createElement("div");
	
	var btnTable = document.createElement("table");
	var btnTbody = document.createElement("tbody");
	var btnTr = document.createElement("tr");
	btnTable.appendChild(btnTbody);
	btnTbody.appendChild(btnTr);
	
	if(this.type == "top" || this.type == "bottom") {
		buttonDiv.className="buttons_horizontal";
		buttonDiv.setAttribute("class", "buttons_horizontal");
	}
	else {
		buttonDiv.className="buttons_vertical";
		buttonDiv.setAttribute("class", "buttons_vertical");
	}
	
	var maxHeight = 0;
	var maxWidth = 0;
	
	//components
	var k = -1;
	for(i = 0 ; i < stack.children.length ; i++) {
		
		var compo = stack.children[i];
		
		if(compo.tagName != "SCRIPT") {
			k++;
			if(this.type == "left") {
				compo.style.styleFloat = 'right';
				compo.style.cssFloat = 'right';
			}
			else if(this.type == "right") {
				compo.style.styleFloat = 'left';
				compo.style.cssFloat = 'left';
			}
		
			//get the size for elements

			if (navigator.userAgent.toLowerCase().indexOf("msie") != -1) {
				maxWidth = "200";
			}
			
			if(compo.offsetHeight) {
				if(maxHeight < compo.offsetHeight) {
					maxHeight=compo.offsetHeight;
					maxWidth=compo.offsetWidth;
				}
				else if(maxWidth < compo.offsetWidth) {
					maxHeight=compo.offsetHeight;
					maxWidth=compo.offsetWidth;
				}
				
			}
			
			else if(compo.style.pixelHeight) {
				if(maxHeight < compo.style.pixelHeight) {
					maxHeight=compo.style.pixelHeight;
					maxWidth=compo.style.pixelWidth;
				}
				else if(maxWidth < compo.style.pixelWidth) {
					maxHeight=compo.style.pixelHeight;
					maxWidth=compo.style.pixelWidth;
				}
			}
		
			var iconPath = this.getIconByType(this.elementsType[k]);
			
			if(i != this.actual) {
				//compo.setAttribute("className", "hide");
				//compo.setAttribute("class", "hide");
//				compo.className = "hide";
			}
			
			//create the button
			var btn = document.createElement("input");
			btn.type = "image";
			btn.src = iconPath;
			btn.title = this.elementsName[i];
			btn.style.display = "block";
			if(this.type == "top" || this.type == "bottom") {
				btn.style.marginLeft = "10px";
				btn.style.styleFloat = "left";
				btn.style.cssFloat = "left";
			}
			else {
				btn.style.marginTop = "10px";
				buttonDiv.style.styleFloat = this.type;
				buttonDiv.style.cssFloat = this.type;
			}
			if(i == this.actual) {
				btn.className="selected";
				btn.setAttribute("class", "selected");
			}
			
			if (navigator.userAgent.toLowerCase().indexOf("msie") != -1) {
				if(this.type == "top" || this.type == "left") {
					var thisObj = this;
					//btn.onClick = thisObj.changeSelection(i + 1, this);
					btn.onmousedown = function(){thisObj.changeSelection(i + 1, this);};
				}
				else {
					var thisObj = this;
					//btn.onClick = thisObj.changeSelection(i, this);
					btn.onmousedown = function(){thisObj.changeSelection(i, this);};
				}
			}
			
			else {
				if(this.type == "top" || this.type == "left") {
					btn.setAttribute("onclick", "fdObjects['" + this.id + "'].changeSelection(" + (k + 1) + ", this)");
				}
				else {
					btn.setAttribute("onclick", "fdObjects['" + this.id + "'].changeSelection(" + k + ", this)");
				}
			}
			
			if(this.type == "top" || this.type == "bottom") {
				var btnTd = document.createElement("td");
				btnTd.appendChild(btn);
				btnTr.appendChild(btnTd);
			}
			else {
				buttonDiv.appendChild(btn);
			}
		}
		
	}
	
	//set elements size
	for(i = 0 ; i < stack.children.length ; i++) {
	
		var compo = stack.children[i];
		compo.style.height = maxHeight + "px";
		compo.style.width = maxWidth + "px";
		
		if(this.type == "left" || this.type == "right") {
//			buttonDiv.style.height = maxHeight + "px";
			buttonDiv.style.width = "25px";
			if(buttonDiv.children.length * 34 > maxHeight) {
				buttonDiv.style.overflow = "auto";
				buttonDiv.style.width = "45px";
			}
		}
		else {
//			buttonDiv.style.width = maxWidth + "px";
			buttonDiv.style.height = "25px";
			if((btnTr && btnTr.children.length * 34 > maxWidth) || (buttonDiv.children.length * 34 > maxWidth)) {
				buttonDiv.style.overflow = "auto";
				buttonDiv.style.height = "45px";
			}
		}
		
	}
	
	//add the buttons div
	if(this.type == "top" || this.type == "bottom") {
		buttonDiv.appendChild(btnTable);
	}
	
	if(this.type == "top" || this.type == "left") {
		stack.insertBefore(buttonDiv, stack.firstChild);
		this.actual = this.actual + 1;
	}
	else {
		stack.appendChild(buttonDiv);
	}

}

StackableCell.prototype.getIconByType = function(type) {
	if(type == "chart") {
		return "../../freedashboardRuntime/stackableCell/icons/chart.png";
	}
	else if(type == "filter") {
		return "../../freedashboardRuntime/stackableCell/icons/filter.png";
	}
	else if(type == "report") {
		return "../../freedashboardRuntime/stackableCell/icons/report.png";
	}
	else if(type == "button") {
		return "../../freedashboardRuntime/stackableCell/icons/button.png";
	}
	else if(type == "datagrid") {
		return "../../freedashboardRuntime/stackableCell/icons/datagrid.png";
	}
	else if(type == "cube") {
		return "../../freedashboardRuntime/stackableCell/icons/cube.png";
	}
	else if(type == "gauge") {
		return "../../freedashboardRuntime/stackableCell/icons/gauge.png";
	}
	else if(type == "jsp") {
		return "../../freedashboardRuntime/stackableCell/icons/jsp.png";
	}
	else if(type == "link") {
		return "../../freedashboardRuntime/stackableCell/icons/link.png";
	}
	else if(type == "picture") {
		return "../../freedashboardRuntime/stackableCell/icons/picture.png";
	}
	else if(type == "textinput") {
		return "../../freedashboardRuntime/stackableCell/icons/textinput.png";
	}
	else if(type == "timer") {
		return "../../freedashboardRuntime/stackableCell/icons/timer.png";
	}
	else if(type == "label") {
		return "../../freedashboardRuntime/stackableCell/icons/label.png";
	}
	return "../../freedashboardRuntime/stackableCell/icons/chart_pie.png";
}