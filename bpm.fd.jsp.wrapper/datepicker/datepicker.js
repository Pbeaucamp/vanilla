// User Changeable Vars
// The month names in your native language can be substituted below
var MonthNames = new Array("January","February","March","April","Mai","June","July","August","September","October","November","December");
var MonthAbr = new Array("janv.","febr.","march","apr.","mai","june","july","aug.","sept.","octo.","nove.","dece.");

// Global Vars

var dest = null;
//var now = new Date();
//var sy = 0; // currently Selected date
//var sm = 0;
//var sd = 0;
//var y = now.getFullYear(); // Working Date
//var m = now.getMonth();
//var d = now.getDate() ;

var MonthLengths = new Array(31,28,31,30,31,30,31,31,30,31,30,31);




/*
  function DestoryCalendar()
  
  Purpose: Destory any already drawn calendar so a new one can be drawn
*/
Calendar.prototype.DestroyCalendar = function() {
  var cal = document.getElementById("dpCalendar" + this.name);
  if(cal != null) {
    cal.innerHTML = null;
    cal.style.display = "none";
  }
  return
}

/*
handleName : name of the javascript variable
name : id of the HTML container
begin year : the actual year
begin month : the actual month
seldat : the actual day
selectFuntion: the javascript function to call on selecting day event
*/
function Calendar(handleName, name, selectFunction, beginyear, beginmonth, selday) {
	
	this.selectedDayId = new Array();
	this.selectedMonthId = new Array();


	this.handleName = handleName;
	this.name = name;
	this.selectFunction = selectFunction;
	
	var now = new Date();
	if (beginyear != undefined && beginyear !='null' && beginyear != '') {
		this.year = beginyear;
	}
	else {
		this.year = now.getFullYear();
	}
	if (beginmonth != undefined && beginmonth !='null' && beginmonth != '') {
		this.month = beginmonth;
	}
	else {
		this.month = now.getMonth() ;
	}
	
	
	if (selday != undefined && selday != 'null' && selday != '') {
		this.day = selday ;
	}
	else{
		this.day = now.getDate() ;
	}
	
}

Calendar.prototype.Draw = function() {
	this.DestroyCalendar();
	  /* style="position:relative; top: 100px; height:100px;*/
	  var eCalendar = document.createElement("div");
	  eCalendar.setAttribute("id", "dpCalendar"+this.name);
	 /* eCalendar.setAttribute("class", "dpCalendar");*/
	  eCalendar.setAttribute("class", "dpCalendar");
	  
	  var content = document.getElementById(this.name);
	  	  
	  content.style.position = 'relative';
	  content.style.top = '1px';
	  content.style.height = '130px';
	  content.style.width ='200px';
	  content.appendChild(eCalendar);
	  cal = document.getElementById("dpCalendar"+this.name);
	  cal.style.left = "30px";
	  cal.style.top = "0px";
	  
	 var sCal = "<table class=\"tableCalendar\" width=\"100px\"><tr><td>&nbsp;</td><td class=\"cellButton\"><a class=\"calendarItem\" href=\"javascript: " + this.handleName + ".PrevMonth();\" title=\"Previous Month\">&lt;&lt;</a></td>"+
	    "<td id=\"m"+this.name + "_" + this.month +"\" class=\"cellMonth\" width=\"80px\" colspan=\"5\"><a class=\"calendarItem\" href=\"javascript: \">"+MonthNames[this.month]+" "+this.year+"</a></td>"+
	    "<td class=\"cellButton\"><a class=\"calendarItem\" href=\"javascript:" + this.handleName + ".NextMonth();\" title=\"Next Month\">&gt;&gt;</a></td></tr>"+
	    "<tr><td>D</td><td>L</td><td>M</td><td>M</td><td>J</td><td>V</td><td>S</td></tr>";
	  var wDay = 1;
	  var wDate = new Date(this.year,this.month,wDay);
	  if(this.isLeapYear(wDate)) {
	    MonthLengths[1] = 29;
	  } else {
	    MonthLengths[1] = 28;
	  }
	  var dayclass = "cellDay";
	  var isToday = false;
	  for(var r=1; r<7; r++) {

	    for(var c=0; c<7; c++) {
	      var wDate = new Date(this.year,this.month,wDay);
	      if(wDate.getDay() == c && wDay<=MonthLengths[this.month]) {
			sCal = sCal + "<td id =\"d" + this.name + "_"+ wDay +"\"  class=\""+dayclass+"\">";
			sCal += "<a class=\"calendarItem\" href=\"javascript: "  + this.handleName +".setSelectedDay('d"+this.name + "_"+ wDay  +"'); " + this.selectFunction + ";" + "\">"+wDay+"</a></td>";
			wDay++;
	      } else {
			sCal = sCal + "<td class=\"unused\"></td>";
	      }
	    }
	    sCal = sCal + "</tr>";
	  }
	  
	  
	  
	  sCal = sCal + "</table>";
	  cal.innerHTML = sCal; // works in FireFox, opera
	  cal.style.display = "inline";
	  //select the day
	  this.setSelectedDay('d' + this.name + "_" + this.day);
}

/*
 Add one month and refresh the rendering
*/
Calendar.prototype.PrevMonth = function () {
  this.month--;
  if(this.month==-1) {
    this.month = 11;
    this.year--;
  }
  this.Draw();
  
}
/*
 Add one month and refresh the rendering
*/
Calendar.prototype.NextMonth = function () {
  this.month++;
  if(this.month==12) {
    this.month = 0;
    this.year++;
  }
  this.Draw();
}
/*
 return a String yyyy-mm-dd
*/
Calendar.prototype.getFormatedDate = function () {
var m = 1 + parseInt(this.month) ;
return this.year + '-' + m + '-' + this.day;
}

/*
select the day 
*/
Calendar.prototype.setSelectedDay = function (id) {
	var dayid = '';
	
	dayid = this.selectedDayId[this.name];
	//alert('select id=' + id +'\nelement=' + document.getElementById(id) + '\ntext=' + document.getElementById(id).textContent + '\ntext2=' + document.getElementById(id).innerText);

	if (document.getElementById(dayid) != undefined) {
		document.getElementById(dayid).style.border  = '1px solid #ddddff';
	}
	this.selectedDayId[this.name] = id;
	document.getElementById(id).style.border  = '1px solid red';
	
	
		

	var elem = document.getElementById(id);
	if (elem != null){
	
		var hasInnerText =(document.getElementsByTagName("body")[0].innerText!= undefined) ? true : false;
		
		if(!hasInnerText){
			this.day = elem.textContent ;
		} else{
			this.day = elem.innerText;
		}
		
	}
		
}







Calendar.prototype.isLeapYear = function (dTest) {
  var y = dTest.getYear();
  var bReturn = false;
  
  if(y % 4 == 0) {
    if(y % 100 != 0) {
      bReturn = true;
    } else {
      if (y % 400 == 0) {
        bReturn = true;
      }
    }
  }
  
  return bReturn;
}