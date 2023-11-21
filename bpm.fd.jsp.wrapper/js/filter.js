/*
 * 
function renderFilterCombo(filterElement, content){
	if (filterElement.nodeName.toLowerCase()  == "select".toLowerCase() ){
		//remove options
		if ( filterElement.hasChildNodes() ){
		    while ( filterElement.childNodes.length >= 1 ){
		    	filterElement.removeChild( filterElement.firstChild );       
		    } 
		}
		filterElement.innerHTML=content;
	}
}
*/

FdFilter.prototype.render = function (content){
	if (this.type == 'datepicker'){
		return;
	}else if (this.type == 'slider'){
		var id = this.id;
		var refresh = this.refresh;
		$('#' + this.id).html('');
		$('#' + this.id).html(content);
		//$('select#slider_' + this.id).html(content);
		
		var labels = $('select#slider_' + this.id).children().length ;
		$('select#slider_' + this.id).selectToUISlider({labels:labels,sliderOptions:{change:function(event,ui){setParameter(id,$($('select#slider_' + id).children()[ui.value]).attr('value'), refresh);}}});
	}
	else{
		var filterContainer = document.getElementById(this.id);
		if ( filterContainer.hasChildNodes() ){
		    while ( filterContainer.childNodes.length >= 1 ){
		    	filterContainer.removeChild( filterContainer.firstChild );       
		    } 
		}
		filterContainer.innerHTML=content;
	}
	
	
}

function FdFilter(id, type, refresh){
	this.id = id;
	this.refresh = refresh;
	this.type = type;
}