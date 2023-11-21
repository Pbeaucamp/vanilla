function clean(d){
	var bal=d.getElementsByTagName('*');

	for(i=0;i<bal.length;i++){
		a=bal[i].previousSibling;
		if(a && a.nodeType==3){
			go(a);
		}
   
		b=bal[i].nextSibling;
		if(b && b.nodeType==3){
			go(b);
		}
	}
	return d;
}
function go(c){
	if(!c.data.replace(/\s/g,'')){
		c.parentNode.removeChild(c);
	}
}
		 	 
function gridData_delRow(tableId){
	var tableElement = document.getElementById(tableId);
	var size = tableElement.childNodes.length;
	var z = tableElement.getElementsByTagName('tbody')[0];
	clean(z);

	var child;
	if (navigator.userAgent.toLowerCase().indexOf("msie") != -1){
		child = z.childNodes[0];
		
	}
	else{
		child = z.childNodes[ z.childNodes.length - 1];
		
	}
	while (child.hasChildNodes()) {
		child.removeChild( child.lastChild );
	}
	z.removeChild(child);
}
 /*
 add a Row in the Table with the given id
 	each cell will contain an input with id = tableId_rowNum_colNum
 	(rowNum and colNum starts at 1)
 */
function gridData_addRow(tableId){
	var tableElement = document.getElementById(tableId);
	var z = tableElement.getElementsByTagName('tbody')[0];
	clean(z);
	var rowNumber = tableElement.childNodes.length;
	var colNumber =z.lastChild.childNodes.length;		
	
	var newRow = document.createElement("tr");
				
				
				
	z.appendChild(newRow);
		
				
	var i = 0;
	for(i=0; i < colNumber; i++){
		var newCell =document.createElement("td");
		var input = document.createElement("input");
		input.setAttribute('id', tableId + "_" + (rowNumber+1) + "_"+ (i+1));
		newCell.appendChild(input);
		newRow.appendChild(newCell);
	}			
}

 function showGridMenu(divId){
		var rowHtml = document.getElementById(divId);

		if (rowHtml.style.visibility == 'visible'){
			rowHtml.style.visibility='hidden';
		}
		else{
			rowHtml.style.visibility='visible';
		}
		
		
		
}