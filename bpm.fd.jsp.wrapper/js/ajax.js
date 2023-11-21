function createHttpRequest(){
	var xhr_object;
	if(window.XMLHttpRequest) // Firefox 
		xhr_object = new XMLHttpRequest(); 
	else if(window.ActiveXObject) // Internet Explorer 
		xhr_object = new ActiveXObject("Microsoft.XMLHTTP"); 
	else { // XMLHttpRequest non supporté par le navigateur 
		alert("Votre navigateur ne supporte pas les objets XMLHTTPRequest..."); 
		 
	} 
	return xhr_object;
}

function getDashInstanceUuid(){
	return "?uuid=" + URL.getParameter("uuid"); 
}
/**
 * 
 * @param componentId : name of the component to set
 * @param value : value
 * @param refresh : if the dependant components must be refreshed
 * @return
 */
function setParameter(componentId, value, refresh){
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
			//alert(err.message);
		}
		
	}
	var data = componentId + "=" + value;
	req.send(data); 
}

function setParameter(componentId, value, refresh, async){
	if(async || async == undefined) {
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
				//alert(err.message);
			}
			
		}
		var data = componentId + "=" + value;
		req.send(data); 
	}
	else {
		var req = createHttpRequest();
		req.open("POST", "../../freedashboardRuntime/setParameter" + getDashInstanceUuid(), false);
	//	req.onreadystatechange = function() { 
	//		try{
	//			if(req.readyState == 4) {
	//				if (req.status == 409){
	//					alert('The model has changed, this instance is no more valide.');
	//				}
	//				else{
	//					if (refresh){
	//						refreshUi(req.responseText);	
	//					}
	//					
	//				}
	//				
	//			}
	//		}catch(err){
	//			//alert(err.message);
	//		}
	//		
	//	}
		var data = componentId + "=" + value;
		req.send(data); 
		if (refresh){
			refreshUi(req.responseText);	
		}
	}
}

function showWaiter(componentId){
	var div = $("#"+componentId);
	
	if(div.parent()[0].id.indexOf('DrillDrivenStackableCell') == 0) {
		div.append('<div id=\"_waiter_' + componentId + '\" class=\"_loading\"></div');
		$("#_waiter_"+componentId).append("<div></div>")//for picture
		$("#_waiter_"+componentId).css("position", "absolute");
		$("#_waiter_"+componentId).css("width", div.css("width"));
		$("#_waiter_"+componentId).css("height", div.css("height"));
	}
	else {
		div.append('<div id=\"_waiter_' + componentId + '\" class=\"_loading\"></div');
		$("#_waiter_"+componentId).append("<div></div>")//for picture
		$("#_waiter_"+componentId).css("position", "absolute");
		$("#_waiter_"+componentId).css("width", div.css("width"));
		$("#_waiter_"+componentId).css("height", div.css("height"));
		
	}

}
function hideWaiter(componentId){
	$("#_waiter_"+componentId).remove();
}

function drillDown(componentId, value){
	var req = createHttpRequest();
	req.open("POST", "../../freedashboardRuntime/drillComponent" + getDashInstanceUuid() + "&drillType=drillDown", true);
	req.onreadystatechange = function() { 
		try{
			if(req.readyState == 4) {
				if (req.status == 409){
					alert('The model has changed, this instance is no more valide.');
				}
				else{
					getComponentHTML(req.responseText, false);
					enableDrillUpButton(componentId);
				}
				hideWaiter(componentId);
			}
			
		}catch(err){
			//$("#"+componentId).removeClass('_loading');
			hideWaiter(componentId);
		}
		
	}
	//$("#"+componentId).addClass('_loading');
	showWaiter(componentId);
	var data = componentId + "=" + value;
	req.send(data); 
}

function enableDrillUpButton(componentId){
	var req = createHttpRequest();
	req.open("POST", "../../freedashboardRuntime/drillComponent" + getDashInstanceUuid()+ "&drillType=canDrillUp", true);
	req.onreadystatechange = function() { 
		//try{
			
			if(req.readyState == 4) {
				if (req.status == 409){
					alert('The model has changed, this instance is no more valide.');
				}
				else{
					/*
					if ('true' == req.responseText){
						$("#"+componentId+ "_button").css('display', 'block');
					}
					else{
						$("#"+componentId+ "_button").css('display', 'none');
					}*/
					if ($("#"+componentId+ "_button ul").size() == 0){
						$("#"+componentId+ "_button ul").append(req.responseText);
					}
					else{
						$("#"+componentId+ "_button ul").replaceWith(req.responseText);
					}
					
				}
				
			}
//		}catch(err){
//		}
		
	}
	var data = componentId;
	req.send(data); 
}

function drillUp(componentId, levelToUp){
	var req = createHttpRequest();
	var prm = '';
	if (levelToUp != undefined && levelToUp != null){
		prm = '&drillUpLevel=' + levelToUp;
	}
	req.open("POST", "../../freedashboardRuntime/drillComponent" + getDashInstanceUuid()+ "&drillType=drillUp" + prm, true);
	req.onreadystatechange = function() { 
		try{
			if(req.readyState == 4) {
				if (req.status == 409){
					alert('The model has changed, this instance is no more valide.');
				}
				else{
					getComponentHTML(req.responseText, false);
					enableDrillUpButton(req.responseText);
				}
				hideWaiter(componentId);
			}
		}catch(err){
			//$("#"+componentId).removeClass('_loading');
			hideWaiter(componentId);
		}
		
	}
	//$("#"+componentId).addClass('_loading');
	showWaiter(componentId);
	var data = componentId;
	req.send(data); 
}

/**
 * this function asks for the dirycomponents names and call for each one the renderFunction
 * @return
 */
function refreshDirtyComponents(){
	var req = createHttpRequest();
	req.open("POST", "../../freedashboardRuntime/dirtyComponents"+ getDashInstanceUuid(), true);
	req.onreadystatechange = function() { 
		if(req.readyState == 4) {
			if(req.readyState == 4) {
				refreshUi(req.responseText);
				
			}
			$('.body').removeClass('_loading');
//			render(componentId, req.responseText);
		}
	}
	$('.body').addClass('_loading');
	var data = null;
	req.send(data);
}

function getComponentHTML(componentId, isasynchronous){
	var req = createHttpRequest();
	var async = (isasynchronous == undefined) ? true : isasynchronous;
	req.open("POST", "../../freedashboardRuntime/getComponentHTML"+ getDashInstanceUuid(), isasynchronous);
	req.onreadystatechange = function() { 
		if(req.readyState == 4) {
			if(req.readyState == 4) {
				if (req.status == 409){
					alert('The model has changed, this instance is no more valide.');
				}
				else{
					render(componentId, req.responseText);
				}
				hideWaiter(componentId);
			}
//			render(componentId, req.responseText);
		}
	}
	showWaiter(componentId);
	var data = componentId;
	req.send(data);

}

function getComponentHTML(componentId){
	var req = createHttpRequest();
	req.open("POST", "../../freedashboardRuntime/getComponentHTML"+ getDashInstanceUuid(), true);
	req.onreadystatechange = function() { 
		if(req.readyState == 4) {
			if(req.readyState == 4) {
				if (req.status == 409){
					alert('The model has changed, this instance is no more valide.');
				}
				else{
					render(componentId, req.responseText);
				}
				hideWaiter(componentId);
			}
		}
	}
	showWaiter(componentId);
	var data = componentId;
	req.send(data);

}


function refreshUi(componentId){
	var ids = componentId.split(';');
	for(var i = 0; i < ids.length; i++){
		getComponentHTML(ids[i], true);
	}
}


var fdObjects = new Object();

function render(componentId, content){
	
	try {
		var fdObject = fdObjects[componentId];
		if (fdObject != null){
			fdObject.render(content);
			$("#"+componentId).removeClass('_loading');
		}
	}
	catch(err) {
	    
	}
	
	
	
}

function slice(componentId){
	alert(fdObjects[componentId].getValues());
}

function popupModelPage(modelPageName, width, height){
	var src = '../../freedashboardRuntime/popupHTML'+ getDashInstanceUuid() + '&modelPage='+modelPageName;
	$.modal('<iframe src="' + src + '" height="' + height + '" width="' + width + '" style="border:0">', {containerCss:{backgroundColor:"#fff",height:height,padding:0,width:width},overlayClose:true});
}

function zoomComponent(componentName, width, height){
	var h = (height+10);
	var w = (width+10) ;
	var src = '../../freedashboardRuntime/zoomComponent'+ getDashInstanceUuid() + '&componentName='+componentName+'&width='+width+'&height='+height;
	$.modal('<iframe src="' + src + '" height="' + h + '" width="' + w + '" style="border:0">', {containerCss:{backgroundColor:"#fff",height:h,padding:0,width:w },overlayClose:true});
}
//vanillaForm submitFunction
function submitForm(submissionUrl){
	document.mainForm.action=submissionUrl;
	document.mainForm.submit();
}

function exportDashboard(isExport) {
	window.open("../../freedashboardRuntime/exportDashboard"+ getDashInstanceUuid(), '');
	return false;
}

function prepareExport() {
	fdObjects["folder"].showAllForExport();
	
	var thisUrl = ''+window.location+'';

	thisUrl = thisUrl.replace('&_isExport=true','');
	
	var res = document.documentElement.innerHTML.replace(thisUrl, '');
	
	//$(window).load(function(){window.status='readyToExport';});
	
	document.documentElement.innerHTML = res;
	
	setTimeout(function(){window.print();}, 10000)
}