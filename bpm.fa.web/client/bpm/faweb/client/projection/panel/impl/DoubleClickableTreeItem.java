package bpm.faweb.client.projection.panel.impl;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TreeItem;

public class DoubleClickableTreeItem extends TreeItem {

	private HTML html;
	private String mesUname;
	private TextArea txt;
	
	public DoubleClickableTreeItem(String text, String mesUname, TextArea txt) {
		html = new HTML(text);
		this.setWidget(html);
		this.mesUname = mesUname;
		this.txt = txt;
		addHandler(html);
	}

	public DoubleClickableTreeItem(HTML text, String mesUname, TextArea txt) {
		super(text);
		html = text;
		this.mesUname = mesUname;
		this.txt = txt;
		addHandler(html);
	}
	
	private void addHandler(HTML html2) {
		
		html2.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				if(DoubleClickableTreeItem.this.getUserObject() != null) {
					txt.setText(txt.getText() + DoubleClickableTreeItem.this.getUserObject().toString());
				}
				else if(DoubleClickableTreeItem.this.getText().equals(" Actual Measure")) {
					txt.setText(txt.getText() + mesUname);
				}
				else {
					txt.setText(txt.getText() + DoubleClickableTreeItem.this.getText());
				}
			}
		});
		

	}
	
	
}
