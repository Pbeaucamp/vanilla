package bpm.architect.web.client.panels;

import java.util.ArrayList;
import java.util.List;

import bpm.architect.web.client.panels.fields.CheckBoxPanel;
import bpm.architect.web.client.panels.fields.FieldPanel;
import bpm.architect.web.client.panels.fields.ListboxPanel;
import bpm.architect.web.client.panels.fields.TextBoxPanel;
import bpm.vanilla.platform.core.beans.forms.Form;
import bpm.vanilla.platform.core.beans.forms.FormField;
import bpm.vanilla.platform.core.beans.forms.FormField.TypeField;
import bpm.vanilla.platform.core.beans.forms.ListFormField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class FormFieldPanel extends Composite {

	private static FormFieldPanelUiBinder uiBinder = GWT.create(FormFieldPanelUiBinder.class);

	interface FormFieldPanelUiBinder extends UiBinder<Widget, FormFieldPanel> {}

	private static FormFieldPanel instance;
	
	@UiField
	FocusPanel leftFieldPanel, rightFieldPanel;
	
	@UiField
	HTMLPanel leftPanel, rightPanel;

	private Form form;
	
	public FormFieldPanel(Form form) {
		initWidget(uiBinder.createAndBindUi(this));
		instance = this;
		this.form = form;
		
		leftFieldPanel.addDropHandler(new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				drop(event, leftPanel);
			}
			
		});
		leftFieldPanel.addDragLeaveHandler(new DragLeaveHandler() {		
			@Override
			public void onDragLeave(DragLeaveEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		leftFieldPanel.addDragOverHandler(new DragOverHandler() {			
			@Override
			public void onDragOver(DragOverEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		rightFieldPanel.addDropHandler(new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				drop(event, rightPanel);
			}
		});
		rightFieldPanel.addDragLeaveHandler(new DragLeaveHandler() {		
			@Override
			public void onDragLeave(DragLeaveEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		rightFieldPanel.addDragOverHandler(new DragOverHandler() {			
			@Override
			public void onDragOver(DragOverEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		
		for(FormField f : form.getFields()) {
			if(f.isLeft()) {
				switch(f.getType()) {
					case TEXTBOX:
						leftPanel.add(new TextBoxPanel(f));
						break;
					case LISTBOX:
						leftPanel.add(new ListboxPanel((ListFormField) f));
						break;
					case CHECKBOX:
						leftPanel.add(new CheckBoxPanel(f));
						break;
				}
			}
			else {
				switch(f.getType()) {
					case TEXTBOX:
						rightPanel.add(new TextBoxPanel(f));
						break;
					case LISTBOX:
						rightPanel.add(new ListboxPanel((ListFormField) f));
						break;
					case CHECKBOX:
						rightPanel.add(new CheckBoxPanel(f));
						break;
				}
			}
		}
	}
	
	public static FormFieldPanel getInstance() {
		return instance;
	}

	private void drop(DropEvent event, HTMLPanel fieldPanel) {
		String type = event.getData("FieldType");
		
		switch(TypeField.valueOf(Integer.parseInt(type))) {
			case TEXTBOX:
				fieldPanel.add(new TextBoxPanel(new FormField(TypeField.TEXTBOX)));
				break;
			case LISTBOX:
				fieldPanel.add(new ListboxPanel(new ListFormField(TypeField.LISTBOX)));
				break;
			case CHECKBOX:
				fieldPanel.add(new CheckBoxPanel(new FormField(TypeField.CHECKBOX)));
				break;
		}
		
		
	}
	
	public void changeFieldType(FieldPanel fieldPanel, TypeField newType) {
		fieldPanel.getField().setType(newType);
		
		HTMLPanel parentPanel = leftPanel;
		int index = leftPanel.getWidgetIndex((Widget)fieldPanel);
		if(index < 0) {
			parentPanel = rightPanel;
		}
		List<FieldPanel> panels = new ArrayList<FieldPanel>();
		for(int i = 0 ; i < parentPanel.getWidgetCount() ; i++) {
			FieldPanel p = (FieldPanel) parentPanel.getWidget(i);
			if(i == index) {
				switch(newType) {
					case TEXTBOX:
						FormField f = new FormField(fieldPanel.getField());
						TextBoxPanel pan = new TextBoxPanel(f);
						panels.add(pan);
						break;
					case LISTBOX:
						ListFormField ff = new ListFormField(fieldPanel.getField());
						ListboxPanel pann = new ListboxPanel(ff);
						panels.add(pann);
						break;
					case CHECKBOX:
						FormField fff = new FormField(fieldPanel.getField());
						CheckBoxPanel pannn = new CheckBoxPanel(fff);
						panels.add(pannn);
						break;
				}
			}
			else {
				panels.add(p);
			}
		}
		parentPanel.clear();
		for(FieldPanel p : panels) {
			parentPanel.add((Widget) p);
		}
		
	}

	public List<FormField> getFields() {
		List<FormField> fields = new ArrayList<FormField>();
		
		for(int i = 0 ; i < leftPanel.getWidgetCount(); i++) {
			FieldPanel p = (FieldPanel) leftPanel.getWidget(i);
			FormField f = p.getField();
			f.setLeft(true);
			fields.add(f);
		}
		for(int i = 0 ; i < rightPanel.getWidgetCount(); i++) {
			FieldPanel p = (FieldPanel) rightPanel.getWidget(i);
			FormField f = p.getField();
			f.setLeft(false);
			fields.add(f);
		}
		
		return fields;
	}

	public void deleteField(FieldPanel field) {
		if(field.getField().isLeft()) {
			leftPanel.remove((Widget)field);
		}
		else {
			rightPanel.remove((Widget)field);
		}
	}
	
	
}
