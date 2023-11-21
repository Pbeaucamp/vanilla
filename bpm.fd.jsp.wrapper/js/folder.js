FdFolder.prototype.render = function (content){
	var div = document.getElementById(this.id);
	for(var i=0; i<div.children.length;i++){
		if (div.children[i].nodeName.toLowerCase() == "div"){
			if (div.children[i].id == content){
				div.children[i].style.display='block';				
			}
			else{
				div.children[i].style.display='none';
			}
		}
		//update menu css
		else if (div.children[i].nodeName.toLowerCase() == "ul"){
			var ul = div.children[i];
			for(var j=0; j<ul.children.length;j++){
				if (ul.children[j].nodeName.toLowerCase() == "li"){
					if (ul.children[j].id == content + '_item'){
						ul.children[j].setAttribute('class','active');
						ul.children[j].className='active';
						ul.children[j].children[0].setAttribute('class','active');
						ul.children[j].children[0].className='active';
		
					}
					else{
						ul.children[j].setAttribute('class','');
						ul.children[j].className='';
						ul.children[j].children[0].setAttribute('class','');
						ul.children[j].children[0].className='';
					}
				}
				
			}
		}
	}
}

function FdFolder(id, defaultShownComponentName){
	this.id = id;
	this.render(defaultShownComponentName);
	try {
		setParameter('_defaultfolder_', defaultShownComponentName, false);
	} catch(e) {}
}

FdFolder.prototype.showAllForExport = function () {
	var div = document.getElementById(this.id);
	for(var i=0; i<div.children.length;i++){
		if (div.children[i].nodeName.toLowerCase() == "div"){
//			if (div.children[i].id == content){
				div.children[i].style.display='block';				
//			}
//			else{
//				div.children[i].style.display='none';
//			}
		}
	}
}