function Comment(user, password, group, dirItemId, repId) {
	this.user = user;
	this.password = password;
	this.group = group;
	this.dirItemId = dirItemId;
	this.repId = repId;
}

Comment.prototype.draw = function(parent, allowComments, showComments, commentList) {

	var mainDiv = document.createElement("div");

	if(showComments) {
	
		for(var i = 0 ; i < commentList.length ; i++) {
			commentList[i].draw(mainDiv);
		}
	
	}

	document.getElementById(parent).appendChild(mainDiv);
	/*var btn = document.createElement("input");
	
	btn.type = "submit";
	btn.value = "Add comment";
	
	if (navigator.userAgent.toLowerCase().indexOf("msie") != -1) {
		var thisObj = this;
		btn.onmousedown = function(){thisObj.addComment(this);};
	}
	
	else {
		btn.setAttribute("onclick", this.id + ".addComment(this)");
	}
	
	parent.appendChild(btn);*/
}

Comment.prototype.addComment = function(thisComment) {

	

}