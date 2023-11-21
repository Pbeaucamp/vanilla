function zoomChart(elementId, width, height, name){
		var htmlDiv = document.getElementById(elementId).innerHTML;
		var backup = document.getElementById(elementId).innerHTML;
		
		
		  var start = htmlDiv.indexOf('chartWidth=');
		  var end = htmlDiv.indexOf('&amp;', start);
		  htmlDiv = htmlDiv.substring(0,start) + "chartWidth=" + width + htmlDiv.substring(end); 
		  
		  start = htmlDiv.indexOf('chartHeight=');
		  end = htmlDiv.indexOf('&amp;', start);
		  htmlDiv = htmlDiv.substring(0,start) + "chartHeight=" + height + htmlDiv.substring(end); 
		  
		  start = htmlDiv.indexOf('height=');
		  end = htmlDiv.indexOf('\'', start);
		  htmlDiv = htmlDiv.substring(0,start) + 'height=\'' + height + '\''+ htmlDiv.substring(end); 
		  
		  start = htmlDiv.indexOf('height=', start);
		  end = htmlDiv.indexOf('\'', start);
		  htmlDiv = htmlDiv.substring(0,start) + 'height=\'' + height + '\''+ htmlDiv.substring(end); 
		  
		  start = htmlDiv.indexOf('width=');
		  end = htmlDiv.indexOf('\'', start);
		  htmlDiv = htmlDiv.substring(0,start) + 'width=\'' + width + '\''+ htmlDiv.substring(end); 
		  
		  start = htmlDiv.indexOf('width='), start;
		  end = htmlDiv.indexOf('\'', start);
		  htmlDiv = htmlDiv.substring(0,start) + 'width=\'' + width + '\''+ htmlDiv.substring(end); 

		var generator= window.open('',name,'height=' + height + ',width=' + width);
		generator.document.write(html);
		generator.document.close();

}