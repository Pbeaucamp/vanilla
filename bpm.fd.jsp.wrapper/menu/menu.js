
/**
 * 
 * @param handle : variable Name to allox internal call of methods
 * @param containerId : the id of the Menu HTML container element
 * @param size : number of visible elements in the menu at the same time
 * @param onSelection : javaScript called when an Item is selected
 *  
 * @param currentValue : the current selection of the Menu
 * @param width : menu width
 * @return : a Menu object
 */
function Menu(handle, containerId, vertical, size, labels, onSelection, width, currentValue){
	this.label = labels;
	this.containerId = containerId;
	this.handle = handle;
	this.size = size;
	this.onSelection = onSelection;
	
	this.startRendering = 0;
	
	if (currentValue = undefined){
		this.currentValue = this.label[0];
	}
	else{
		this.currentValue = currentValue; 
	}

	this.vertical = vertical;
	
	
	if (width != undefined){
		this.width = width;
	}
	
	
	
	//internal variable to detect IE
	this.isIE = (navigator.userAgent.toLowerCase().indexOf("msie") != -1);
	
	return this;
}

Menu.prototype.getCurrentPosition = function(){
	for(var i = 0; i < this.label.length && i < this.size; i++){
		if (this.label[i] == this.currentValue){
			return i;
		}
	}
	return 0;
}

Menu.prototype.setValue = function(value){
	this.currentValue = value;
	this.render();
}
Menu.prototype.next = function(){
	if (this.startRendering + this.size <= this.label.length){
		this.startRendering += this.size;
		this.render();
	}
	
}



Menu.prototype.prev = function(){
	if (this.startRendering - this.size >= 0){
		this.startRendering -= this.size;
		this.render();
	}
}





Menu.prototype.renderHorizontal = function(){
	var container = document.getElementById(this.containerId);
	var menuElement = document.getElementById(this.containerId + "_Menu");
	
	
	if ( menuElement == null){
		menuElement = document.createElement("table");
		menuElement.setAttribute("id", "" + this.containerId + "_Menu");
		
		
	}
	else{
		var nodes = menuElement.childNodes;
		for(var i = 0; i<nodes.length; i++){
			menuElement.removeChild(nodes[i]);
		}
	}
	
	if (this.width != undefined){
		menuElement.setAttribute("width", "" + this.width);
		
	}
	
	var tBody = document.createElement("tbody");
	var trNode = document.createElement("tr");
	
	
	if (this.isIE){
		trNode.className="menu";
	}
	else{
		trNode.setAttribute("class" , "menu");	
	}
	
	
	var tdNode = document.createElement("td");
	trNode.appendChild(tdNode);
	if (this.isIE){
		tdNode.className="menu-right-arrow";
		var execPrev = this.handle + ".prev();";
		tdNode.onclick = function(){eval(execPrev);};
	}
	else{
		tdNode.setAttribute("class", "menu-right-arrow");
		tdNode.setAttribute("onClick", this.handle + ".prev();");
	}
	
		
	var start = this.startRendering;
	
	var aNode;
	

	for(var i = start; i < this.label.length && i-start < this.size; i++){
		tdNode = document.createElement("td");
		aNode = document.createElement("a");
		
		
		if (this.isIE){
			var execSet = this.handle + ".setValue('" + this.label[i] + "');" + this.onSelection;
			aNode.onclick = function(){eval(execSet);};
		
			if (this.label[i] == this.currentValue){
				tdNode.className="selectedItem";
			}
		}
		else{
			aNode.setAttribute("onClick", this.handle + ".setValue('" + this.label[i] + "');" + this.onSelection);
			if (this.label[i] == this.currentValue){
				tdNode.setAttribute("class", "selectedItem");
			}
		}
		
		
		 
		
		aNode.appendChild(document.createTextNode(this.label[i]));
		
		
		
		tdNode.appendChild(aNode);
		trNode.appendChild(tdNode);

	}
	tdNode = document.createElement("td");
	if (this.isIE){
		tdNode.className="menu-left-arrow";
		var execNext = this.handle + ".next();";
		tdNode.onclick = function(){eval(execNext);};
	}
	else{
		tdNode.setAttribute("class", "menu-left-arrow");
		tdNode.setAttribute("onClick", this.handle + ".next();");
	}
	
	
	
	
	trNode.appendChild(tdNode);

	tBody.appendChild(trNode);
	menuElement.appendChild(tBody);
	container.appendChild(menuElement);
	
	
}

Menu.prototype.render = function(){
	if (this.vertical){
		this.renderVertical();
	}
	else{
		this.renderHorizontal();
	}
}



Menu.prototype.renderVertical = function(){
	var container = document.getElementById(this.containerId);
	var menuElement = document.getElementById(this.containerId + "_Menu");
	
	if ( menuElement == null){
		menuElement = document.createElement("table");
		menuElement.setAttribute("id", "" + this.containerId + "_Menu");
		
		
	}
	else{
		var nodes = menuElement.childNodes;
		for(var i = 0; i<nodes.length; i++){
			menuElement.removeChild(nodes[i]);
		}
	}
	
	if (this.width != undefined){
		menuElement.setAttribute("width", "" + this.width);
		
	}
	
	var tBody = document.createElement("tbody");
	var trNode = document.createElement("tr");
	tBody.appendChild(trNode);
	
	if (this.isIE){
		trNode.className="menu";
	}
	else{
		trNode.setAttribute("class" , "menu");	
	}
	
	
	var tdNode = document.createElement("td");
	trNode.appendChild(tdNode);
	if (this.isIE){
		tdNode.className="menu-up-arrow";
		var exec = this.handle + ".prev();";
		tdNode.onclick = function(){eval(exec);};
	}
	else{
		tdNode.setAttribute("class", "menu-up-arrow");
		tdNode.setAttribute("onClick", this.handle + ".prev();");
	}
	
		
	var start = this.startRendering;
	
	var aNode;
	for(var i = start; i < this.label.length && i-start < this.size; i++){
		trNode = document.createElement("tr");
		tdNode = document.createElement("td");
		aNode = document.createElement("a");
		
		
		if (this.isIE){
			trNode.className="menu"; 
			var exec = this.handle + ".setValue('" + this.label[i] + "');" + this.onSelection;
			aNode.onclick = function(){eval(exec);};
			if (this.label[i] == this.currentValue){
				tdNode.className="selectedItem";
			}
		}
		else{
			trNode.setAttribute("class" , "menu"); 
			if (this.label[i] == this.currentValue){
				tdNode.setAttribute("class", "selectedItem");
			}
			aNode.setAttribute("onClick", this.handle + ".setValue('" + this.label[i] + "');" + this.onSelection);
			if (this.label[i] == this.currentValue){
				tdNode.setAttribute("class", "selectedItem");
			}
		}
		
		
		 
		
		aNode.appendChild(document.createTextNode(this.label[i]));
		
		
		
		tdNode.appendChild(aNode);
		trNode.appendChild(tdNode);
		tBody.appendChild(trNode);

	}
	trNode = document.createElement("tr");
	tdNode = document.createElement("td");
	if (this.isIE){
		trNode.className="menu"; 
		tdNode.className="menu-down-arrow";
		var exec = this.handle + ".next();";
		tdNode.onclick = function(){eval(exec);};
	}
	else{
		trNode.setAttribute("class" , "menu"); 
		tdNode.setAttribute("class", "menu-down-arrow");
		tdNode.setAttribute("onClick", this.handle + ".next();");
	}
	
	
	
	
	trNode.appendChild(tdNode);

	tBody.appendChild(trNode);
	menuElement.appendChild(tBody);
	container.appendChild(menuElement);

}
