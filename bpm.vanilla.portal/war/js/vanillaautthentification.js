	var http = getHTTPObject();
	var ajax;
	var lang;
	
	function Authentify(target, login, password, _lang) {
		this.target = target;
		this.login = login;
		this.password = password;
		lang = _lang;
		ajax = this;
	}
	
	Authentify.prototype.test = function() {
		var url = 'Authentification';
		//password is encrypted

		var xml = '<root>\n';
		xml += '<login>' + this.login + '</login>';
		xml += '<password>' + this.password + '</password>';
		xml += '</root>';			
		http.open("POST", url, true);
		http.setRequestHeader("Content-Type", "text/xml");
		http.onreadystatechange = handleHttpResponse
		http.send(xml);
	}
	

	
	function handleHttpResponse(){
		if (http.readyState == 4) {
			var res = '';
				//'<style type="text/css">' +
				//'	element.style {' +
				//'		background-color:#6B6D82;' +
				//'	}' +
				//'<style/>';
			if (http.status == 200) {              
			// Or for XML formatted response text:
				var reponse = clean(http.responseXML.documentElement);
				
				if (undefined != reponse.getElementsByTagName('baduser') [0]) {
					res +='<table class="select_group" padding="0" border="0" width="300px">';
					res += '<tr><td align="right"><img src="close.gif" onclick="Close(\'groups\');" /></td></tr>';
					res += '<tr><td>&nbsp;</td></tr>';
					res += '<tr><td><font color="#ff0000"><b>Authentification failed</b></font></td></tr>';
					res += '<tr><td>&nbsp;</td></tr>';
					res += '<tr><td>User doesn\'t exist</td></tr></table>';
				}
				else if (undefined != reponse.getElementsByTagName('badpassword') [0]) {
					res +='<table class="select_group" padding="0" border="0" width="300px">';
					res += '<tr><td align="right"><img src="close.gif" onclick="Close(\'groups\');" /></td></tr>';
					res += '<tr><td>&nbsp;</td></tr>';
					res += '<tr><td><font color="#ff0000"><b>Authentification failed</b></font></td></tr>';
					res += '<tr><td>&nbsp;</td></tr>';
					res += '<tr><td>Entered password is wrong</td></tr></table>';
				}
				else {
					res +='<form id="_groups_" method=post action="/vanilla/vanilla.jsp">';
					res +='<table class="select_group" padding="0" border="0" width="300px">';
					res += '<tr><td align="right"><img src="close.gif" onclick="Close(\'groups\');" /></td></tr>';

                    if (lang == 'en_EN') {
                            res += '<tr><td>Authentification succeeded</td></tr>';
                            res += '<tr><td>&nbsp;</td></tr>';
                            res += '<tr><td>';
                            res += 'Availables Groups: ';
                    }
                    else if (lang == 'fr_FR') {
                            res += '<tr><td>Authentification r&eacute;ussie</td></tr>';
                            res += '<tr><td>&nbsp;</td></tr>';
                            res += '<tr><td>';
                            res += 'Groupes disponibles: ';
                    }
                    else if (lang == 'es_ES') {
                            res += '<tr><td>&Eacute;xito Autentificaci&oacute;n</td></tr>';
                            res += '<tr><td>&nbsp;</td></tr>';
                            res += '<tr><td>';
                            res += 'Grupos disponible: ';
                    }
					
					res +=  '<select name="__group" id="__group" class="input">';
                    
					for (i = 0; i < reponse.childNodes.length; i++) {
						var gr = reponse.getElementsByTagName('group')[i].firstChild.nodeValue;
						res += '<option value="' + gr + '"> ' + gr + ' </option>';
					}
					//ere, added for language forwarding
					res += '<input type="hidden" name="__lang2" value=' + lang + '>';
					res += '</select>';
					res += '</td></tr>';
					res += '<tr align="right"><td><input type="submit" value="submit">';
					res += '</td></tr></table>';
					res += '</form>';
				}

				
				
				/*
				var newImage = "url(arrondi.png)";
				document.getElementById('groups').style.backgroundImage = newImage;
				*/
			}
			else {
				res +='<table class="select_group">';
				res += '<tr><td><font color="#ff0000"><b>Authentification failed</b></font></td></tr>';
				res += '<tr><td>&nbsp;</td></tr>';
				res += '<tr><td>Refresh your browser and try again</td></tr></table>';
				
			}
			
			var winW = 630, winH = 460;

			if (parseInt(navigator.appVersion)>3) {
			 if (navigator.appName=="Netscape") {
			  winW = window.innerWidth;
			  winH = window.innerHeight;
			 }
			 if (navigator.appName.indexOf("Microsoft")!=-1) {
			  winW = document.body.offsetWidth;
			  winH = document.body.offsetHeight;
			 }
			}
				
			
			var left = (winW - 200)/2;
			var top = (winH - 200)/2;
			document.getElementById(ajax.target).innerHTML = res;	
			document.getElementById('groups').display = 'block';
			document.getElementById('groups').style.left = left + 'px';
			document.getElementById('groups').style.top = top + 'px';
			document.getElementById('groups').style.zIndex = '5';
			document.getElementById('groups').style.backgroundColor = '#EAEEF7';
			document.getElementById('groups').style.border = '2px outset';
		}
	}
	
	
	function getHTTPObject() {
		var xmlhttp;
		if (window.XMLHttpRequest) {
			xmlhttp = new XMLHttpRequest();
		} else if (window.ActiveXObject) {
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		} 
		return xmlhttp;
	}


	function go(c){
		if(!c.data.replace(/\s/g,''))
			c.parentNode.removeChild(c);
	}

	function clean(d){
		var bal=d.getElementsByTagName('*');

		for(i=0;i<bal.length;i++){
			a=bal[i].previousSibling;
			if(a && a.nodeType==3)
				go(a);
			
			b=bal[i].nextSibling;
			if(b && b.nodeType==3)
				go(b);
			}
		return d;
	}