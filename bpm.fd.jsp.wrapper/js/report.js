FdReport.prototype.render = function (content){
	var filterContainer = document.getElementById(this.id);
	if ( filterContainer.hasChildNodes() ){
	    while ( filterContainer.childNodes.length >= 1 ){
	    	filterContainer.removeChild( filterContainer.firstChild );       
	    } 
	}
	filterContainer.innerHTML=content;
}

function FdReport(id){
	this.id = id;
}