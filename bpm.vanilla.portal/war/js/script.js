function gup( name ) {
  name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
  var regexS = "[\\?&]"+name+"=([^&#]*)";
  
  var regex = new RegExp( regexS );
  var results = regex.exec( window.location.href );
  
  return results == null ? null : results[1];
}

//var locale = gup('locale');
//if(locale == 'fr_FR')  {
//	document.getElementById('conteneur').className='fr';
//}
//else {
//	document.getElementById('conteneur').className='en';
//}
