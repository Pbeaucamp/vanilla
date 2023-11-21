FdDynamicStackableCell.prototype.render = function (content){
	var div = document.getElementById(this.id);
	for(var i=0; i<div.children.length;i++){
		if (div.children[i].nodeName.toLowerCase() == "div"){
			if (div.children[i].id == content){
				div.children[i].style.display='block';
				//div.children[i].style.visibility='visible';
			}
			else{
				div.children[i].style.display='none';
				//div.children[i].style.visibility='hidden';
			}
		}
		
	}
}

function FdDynamicStackableCell(id, defaultShownComponentName){
	this.id = id;
	this.render(defaultShownComponentName);

}