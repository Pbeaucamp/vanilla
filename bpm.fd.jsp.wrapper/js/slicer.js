/**
 * 
 * @param componentId : name of the component to set
 * @param value : value
 * @param refresh : if the dependant components must be refreshed
 * @return
 */
FdSlicer.prototype.slice = function slice(){
	var req = createHttpRequest();
	var fdObject = this;
	req.open("POST", "../../freedashboardRuntime/applySlicer" + getDashInstanceUuid() + "&componentName=" + this.id, true);
	req.onreadystatechange = function() { 
		try{
			if(req.readyState == 4) {
				if (req.status == 409){
					alert('The model has changed, this instance is no more valide.');
				}
				else{
					if (fdObject.submitOnChange){
						refreshUi(req.responseText);	
					}
					
				}
				
			}
		}catch(err){
			
		}
		
	}
	var data = '';
	
	var slicer = $('#' + this.id + "_slicer");
	var checked =  slicer.children("div").children("div").children("input:checked");
	
	for(var i = 0 ; i < checked.length; i++){
		if ($($(checked).get(i)).parent().parent().parent().id != slicer.id){
			continue;
		}
		
		var id = $($(checked).get(i)).parent().attr('id').substring($($(checked).get(i)).parent().attr('id').indexOf("_") + 1);
		id = id.substring(id.indexOf("_")+ 1);
		if (data.length > 0){
			data = data + ';';
		}
		data = data + id;
		
	}
	
	
	req.send(data); 
}

FdSlicer.prototype.checkLevel = function (lvlNumber){
	var checked = $($('#' + this.id + "_slicer").children('h3').get(lvlNumber)).children('input').attr("checked");
	if (checked != undefined && (checked=="checked" || checked==true)){
		checked = true;
	}
	else{
		checked = false;
	}
	$($('#' + this.id + "_slicer").children('div').get(lvlNumber)).children('div').children('input').attr("checked", checked);
	if (this.submitOnChange){
		this.slice();
	}
}

FdSlicer.prototype.render = function (content){
	$('#' + this.id + "_slicer").multiAccordion('destroy');
	$('#' + this.id + "_slicer").remove();
	$('#' + this.id).append(content);
	$('#' + this.id + "_slicer").multiAccordion({active: 'all' });
	
	/*
	 * remove the background-image
	 */

}
/*
FdSlicer.prototype.getValues = function(){
	var s = '';
	if (this.values == undefined || this.values == null){
		return s;
	}
	var firstRow = true;
	for(var i =0; i < this.values.length; i++){
		if (firstRow){
			firstRow = false;
		}
		else{
			s += ';';
		}
		s += '[';
		
		var row = this.values[i];
		var firstVal = true;
		if (row != undefined && row != null){
			for(var k = 0 ; k < row.length; k ++){
				if (firstVal){
					firstVal = false;
				}
				else{
					s += ',';
				}
				var lvlValues = row[k].split("_");
				s += lvlValues[lvlValues.length - 1];
			}
		}
		
		
		s += ']';
	}
	return s;
}
*/

function escapeCharacters(string){
	var current = '';
	for(var i = 0; i < string.length; i++){
		var sub = string.charAt(i);
		
		if (sub.match("[A-Za-z0-9\-\_\;\,]") == null){
			current = current +"\\" + sub;
		}
		else{
			
			current = current + sub;
		}
	}
	return current;
}

FdSlicer.prototype.store = function (lvlIndex, value){
	if (this.linkedLevels){
		value = escapeCharacters(value);
		var elements = $("div[id*="+value+"]");

		for(var z = 0; z < elements.length; z++){
			var divSmallId = elements[z].id.substring(elements[z].id.indexOf('_') + 1 );
			divSmallId = divSmallId.substring(divSmallId.indexOf('_') + 1 )
			
			
			if ((elements[z].id +'').indexOf(value) != 1 && divSmallId.length > value.length){
				var lvlNum = divSmallId.split("_").length - 1;
				
				var input = $("div[id="+escapeCharacters(elements[z].id)+"]").children('input');
				
				/*
				 * check if the value has been checked or not
				 */
				var current = $("div[id="+this.id + "_"+ value+"]");
				var parentchecked = current.children("input").attr("checked") != undefined && current.children("input").attr("checked") == "checked";
				
				var displayCss = 'none';
				displayCss = parentchecked ? 'block' : 'none';
				$("div[id="+escapeCharacters(elements[z].id)+"]").css('display', displayCss);
			}
		}
	}
	this.slice();
	
}

function FdSlicer(id, submitOnChange, linkedLevels){
	this.id = id;
	this.submitOnChange = submitOnChange;
	this.linkedLevels = linkedLevels;
}