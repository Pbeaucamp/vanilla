package bpm.birep.admin.client.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.vanilla.platform.core.beans.Widgets;

import adminbirep.Activator;
import adminbirep.Messages;

public class DialogAddWidgets extends Dialog{

	private Combo widgetType;
	private Composite composite ;
	private Text nameText, urlText;
	
	public DialogAddWidgets(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		
		composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));
		
		Label name = new Label(composite, SWT.NONE);
		name.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		name.setText(Messages.DialogAddWidgets_0);
		
		nameText = new Text(composite, SWT.BORDER);
		nameText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label widgetUrl = new Label(composite, SWT.NONE);
		widgetUrl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		widgetUrl.setText(Messages.DialogAddWidgets_1);
		
		urlText = new Text(composite, SWT.BORDER);
		urlText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		
		Label widgetTypeLabel = new Label(composite, SWT.NONE);
		widgetTypeLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		widgetTypeLabel.setText(Messages.DialogAddWidgets_2);

		widgetType = new Combo(composite, SWT.READ_ONLY);
		widgetType.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		widgetType.setItems(Widgets.WIDGET_TYPES);
		
		return composite;
		
	}
	@Override
	protected void okPressed() {
		
//		Widgets w = new Widgets();
//		w.setName(nameText.getText());
//		
//			int index = widgetType.getSelectionIndex();
//			
//		w.setWidgetType(widgetType.getItem(index));
//		w.setWidgetUrl(urlText.getText());
//		
//		try {
//			Widgets ww = Activator.getDefault().getVanillaApi().getVanillaPreferencesManager().getExistingWidget(w);
//				if(ww.equals(w)){
//					// do nothing
//				}
//				else {
//					Activator.getDefault().getVanillaApi().getVanillaPreferencesManager().addWidget(w);
//				}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		super.okPressed();
	}

	public Combo getWidgetType() {
		return widgetType;
	}

	public void setWidgetType(Combo widgetType) {
		this.widgetType = widgetType;
	}

	public Text getNameText() {
		return nameText;
	}

	public void setNameText(Text nameText) {
		this.nameText = nameText;
	}

	public Text getUrlText() {
		return urlText;
	}

	public void setUrlText(Text urlText) {
		this.urlText = urlText;
	}	
}
