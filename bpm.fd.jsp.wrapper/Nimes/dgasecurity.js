//set the drilledCellsParameters
function setDrilledCellsParameter(filterId) {
	for (var element in fdObjects) {
		if(element.indexOf('DrillDrivenStackableCell') > -1) {
		
			var req = createHttpRequest();
			req.open("POST", "../../freedashboardRuntime/setParameter" + getDashInstanceUuid(), true);
			req.onreadystatechange = function() { 
				try{
					if(req.readyState == 4) {
						if (req.status == 409){
							alert('The model has changed, this instance is no more valide.');
						}				
					}
				}catch(err){
					alert(err.message);
				}
				
			}
			var data = element + "=" + filterId;
			req.send(data); 
		}
	}
}

function checkDga(dga, filterid, arrayDga, arraySuperUser) {

	if (!Array.prototype.indexOf) {
	  Array.prototype.indexOf = function (searchElement /*, fromIndex */ ) {
		"use strict";
		if (this == null) {
		  throw new TypeError();
		}
		var t = Object(this);
		var len = t.length >>> 0;

		if (len === 0) {
		  return -1;
		}
		var n = 0;
		if (arguments.length > 1) {
		  n = Number(arguments[1]);
		  if (n != n) { // shortcut for verifying if it's NaN
			n = 0;
		  } else if (n != 0 && n != Infinity && n != -Infinity) {
			n = (n > 0 || -1) * Math.floor(Math.abs(n));
		  }
		}
		if (n >= len) {
		  return -1;
		}
		var k = n >= 0 ? n : Math.max(len - Math.abs(n), 0);
		for (; k < len; k++) {
		  if (k in t && t[k] === searchElement) {
			return k;
		  }
		}
		return -1;
	  }
	}
	
	var index = arraySuperUser.indexOf(dga);
	if(index > -1) {
		return;
	}
	else {
		
		var filter = document.getElementById(filterid);
		var dgaKey = arrayDga[dga];
		var resultParam='';
		if(dgaKey == null || dgaKey == undefined) {
			alert('Votre DGA ne figure pas dans l\'organigramme du tableau de bord.');
		}
		var resIndex = new Array(dgaKey.length);
		look:for(i=0; i < filter.options.length; i++) {
			for(j=0; j < dgaKey.length; j++) {
				if(filter.options[i].text == dgaKey[j]) {
					resIndex[j] = i;
					resultParam=filter.options[i].value;
				}
			}
		}
		for(i=filter.options.length;i>-1;i--) {
			if(resIndex.indexOf(i) < 0) {
				filter.remove(i);
			}
		}
		
		
		if(dgaKey.length > 1) {
			setParameter(filterid,resultParam,true,'');
		}
		else {
			filter.selectedIndex=0;
			setParameter(filterid,resultParam,true,'');
		}
	}

}

//return 0 for GF collectivity, 1 for GF DGA, 2 for GF direction, 3 for RH collectivity, 4 for RH DGA, 5 for RH direction
function findOrganigramLevel() {

	var add = 0;

	var direSelect = document.getElementById('Toolbar_Filter_Direction');
	if(direSelect == null || direSelect == undefined) {
		direSelect = document.getElementById('FilterDirection');
		add = 3;
	}
	
	var val = direSelect.value;
	if(val != '%') {
		return add+2;
	}
	
	var dgaSelect = document.getElementById('Toolbar_Filter_DGA');
	if(dgaSelect == null || dgaSelect == undefined) {
		dgaSelect = document.getElementById('FilterDGA');
		add = 3;
	}

	val = dgaSelect.value;
	if(val != '%') {
		return add+1;
	}

	var collecSelect = document.getElementById('Toolbar_Filter_Collectivites');
	if(collecSelect == null || collecSelect == undefined) {
		collectSelect = document.getElementById('FilterCollectivites');
		add = 3;
	}
	
	return add+0;
}

var original_setParameter = setParameter;

//Do some thing for Nimes (assign drilledDrivenStackCellsValues)
function setParameter(componentId, value, refresh, a){
	
	/*var drilledCellValue = '';
	var organigramLevel = findOrganigramLevel();
	if(organigramLevel == 0) {
		setDrilledCellsParameter('Toolbar_Filter_Collectivites');
		drilledCellValue='Toolbar_Filter_Collectivites';
	}
	else if(organigramLevel == 1) {
		setDrilledCellsParameter('Toolbar_Filter_DGA');
		drilledCellValue='Toolbar_Filter_DGA';
	}
	else if(organigramLevel == 2) {
		setDrilledCellsParameter('Toolbar_Filter_Direction');
		drilledCellValue='Toolbar_Filter_Direction';
	}
	else if(organigramLevel == 3) {
		setDrilledCellsParameter('FilterCollectivites');
		drilledCellValue='FilterCollectivites';
	}
	else if(organigramLevel == 4) {
		setDrilledCellsParameter('FilterDGA');
		drilledCellValue='FilterDGA';
	}
	else if(organigramLevel == 5) {
		setDrilledCellsParameter('FilterDirection');
		drilledCellValue='FilterDirection';
	}*/

	var req = createHttpRequest();
	req.open("POST", "../../freedashboardRuntime/setParameter" + getDashInstanceUuid(), true);
	req.onreadystatechange = function() { 
		try{
			if(req.readyState == 4) {
				if (req.status == 409){
					alert('The model has changed, this instance is no more valide.');
				}
				else{
					if (refresh){
						refreshUi(req.responseText);	
					}
					
				}
				
			}
		}catch(err){
			alert(err.message);
		}
		
	}
	
	/*if(componentId.indexOf('DrillDrivenStackableCell') > -1) {
		value = drilledCellValue;
	}*/
	
	var data = componentId + "=" + value;
	req.send(data); 
}