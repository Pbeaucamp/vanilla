
function FdFormatedText(id){
	this.id = id;
	
	CKEDITOR.replace(id + "_ftext",{
		toolbar :[
		['Source','-','NewPage','Preview'],
		['Cut','Copy','Paste','PasteText','PasteFromWord','-','Print'],
		['Undo','Redo','-','Find','Replace','-','SelectAll','RemoveFormat'],
		'/'
		,['NumberedList','BulletedList','-','Outdent','Indent','Blockquote'],
		['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
		['Link','Unlink','Anchor'],
		['Table','HorizontalRule','SpecialChar','PageBreak'],
		'/'
		,['Bold','Italic','Underline','Strike','-','Subscript','Superscript'],['Font','FontSize'],
		['TextColor','BGColor'],
		['Maximize', 'ShowBlocks','-','About'
		]],fullPage : false});
}