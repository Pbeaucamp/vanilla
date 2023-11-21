function StackableCell(id, elementsType, elementsName, actual, type) {

	this.id = id;
	this.actual = actual;
	this.elementsType = elementsType;
	this.elementsName = elementsName;
	this.type = type;
	
	/*
	if(parameters[id + "_index"]) {
		this.actual = parseInt(parameters[id + "_index"]);
		setParameter(this.id + "_index", this.actual);
	}
	*/
	
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
		
		actualDiv.className="hide";
		actualDiv.setAttribute("class", "hide");
		
		this.actual = selectionPosition;
		
		/*
		if(this.type == "top" || this.type == "left") {
			setParameter(this.id + "_index", this.actual - 1);
		}
		else {
			setParameter(this.id + "_index", this.actual);
		}
		*/
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
	for(i = 0 ; i < stack.children.length ; i++) {
		
		var compo = stack.children[i];
		
		if(compo.tagName != "SCRIPT") {
		
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
		
			var iconPath = this.getIconByType(this.elementsType[i]);
			
			if(i != this.actual) {
				//compo.setAttribute("className", "hide");
				//compo.setAttribute("class", "hide");
				compo.className = "hide";
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
					btn.setAttribute("onclick", this.id + ".changeSelection(" + (i + 1) + ", this)");
				}
				else {
					btn.setAttribute("onclick", this.id + ".changeSelection(" + i + ", this)");
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
			buttonDiv.style.height = maxHeight + "px";
			buttonDiv.style.width = "24px";
			if(buttonDiv.children.length * 34 > maxHeight) {
				buttonDiv.style.overflow = "auto";
				buttonDiv.style.width = "45px";
			}
		}
		else {
			buttonDiv.style.width = maxWidth + "px";
			buttonDiv.style.height = "24px";
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
		return "../../stackableCell/icons/chart.png";
	}
	else if(type == "filter") {
		return "../../stackableCell/icons/filter.png";
	}
	else if(type == "report") {
		return "../../stackableCell/icons/report.png";
	}
	else if(type == "button") {
		return "../../stackableCell/icons/button.png";
	}
	else if(type == "datagrid") {
		return "../../stackableCell/icons/datagrid.png";
	}
	else if(type == "cube") {
		return "../../stackableCell/icons/cube.png";
	}
	else if(type == "gauge") {
		return "../../stackableCell/icons/gauge.png";
	}
	else if(type == "jsp") {
		return "../../stackableCell/icons/jsp.png";
	}
	else if(type == "link") {
		return "../../stackableCell/icons/link.png";
	}
	else if(type == "picture") {
		return "../../stackableCell/icons/picture.png";
	}
	else if(type == "textinput") {
		return "../../stackableCell/icons/textinput.png";
	}
	else if(type == "timer") {
		return "../../stackableCell/icons/timer.png";
	}
	else if(type == "label") {
		return "../../stackableCell/icons/label.png";
	}
	return "../../stackableCell/icons/chart_pie.png";
}