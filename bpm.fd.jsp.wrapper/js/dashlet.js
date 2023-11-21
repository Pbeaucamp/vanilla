FdFrame.prototype.render = function (content){
	var filterContainer = document.getElementById(this.id);
	if ( filterContainer.hasChildNodes() ){
	    while ( filterContainer.childNodes.length >= 1 ){
	    	filterContainer.removeChild( filterContainer.firstChild );       
	    } 
	}
	filterContainer.innerHTML=content;
}
/**
 * fusionMap : the FusionMap variable
 * id : the id of the html where the chart will be renderered
 * chartXml : the inition chartXml
 */
function FdFrame(id){
	this.id = id;
}