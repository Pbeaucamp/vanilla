function Listcommentpart(user, group, date, comment) {

	this.user = user;
	this.group = group;
	this.date = date;
	this.comment = comment;

}

Listcommentpart.prototype.draw = function(parent) {

	var mainDiv = document.createElement("div");
	
	mainDiv.className="listcommentpart-maindiv";

	var infoDiv = document.createElement("div");
	
	infoDiv.className="infosDiv";
	infoDiv.style.height = "30px";
	
	var lblUser = document.createElement("label");
	var lblGrp = document.createElement("label");
	var lblDate = document.createElement("label");
	
	lblUser.style.cssFloat = "left";
	lblUser.style.styleFloat = "left";
	lblUser.innerHTML = "User : " + this.user;
	
	lblGrp.style.cssFloat = "right";
	lblGrp.style.styleFloat = "right";
	lblGrp.innerHTML = "Group : " + this.group;
	
	lblDate.style.cssFloat = "left";
	lblDate.style.styleFloat = "left";
	lblDate.innerHTML = "Date : " + this.date;
	
	var txtComment = document.createElement("div");
	txtComment.innerHTML = this.comment;
	txtComment.style.overflow = "auto";
	txtComment.style.border = "3px outset";
	
	infoDiv.appendChild(lblGrp);
	infoDiv.appendChild(lblDate);
	
	mainDiv.appendChild(infoDiv);
	mainDiv.appendChild(txtComment);
	
	parent.appendChild(mainDiv);
	
}