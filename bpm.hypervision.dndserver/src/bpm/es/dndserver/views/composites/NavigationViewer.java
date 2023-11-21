package bpm.es.dndserver.views.composites;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import bpm.es.dndserver.Activator;
import bpm.es.dndserver.Messages;
import bpm.es.dndserver.icons.IconsName;
import bpm.es.dndserver.tools.OurLogger;
import bpm.es.dndserver.views.View;

public class NavigationViewer extends Composite {

	public static int TYPE_FORWARD = 0;
	public static int TYPE_BACKWARD = 1;
	
	public static int STYLE_ENABLED = 0;
	public static int STYLE_DISABLED = 1;
	public static int STYLE_FINISHED = 2;
	
	private Button b1;
	
	private int currentStyle;
	private int currentType;
	
	private View mainView;
	
	public NavigationViewer(Composite parent, int style, View mainView) {
		super(parent, style);
		
		this.mainView = mainView;
		
		setBackground(new Color(Display.getDefault(), 255, 255, 255));
		
		createContent();
	}
	
	public void setNavigation(int type, int style) {
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		
		this.currentStyle = style;
		this.currentType = type;
		
		if (type == TYPE_BACKWARD && style == STYLE_DISABLED) {
			b1.setImage(reg.get(IconsName.BACK_YELLOW));
			b1.setEnabled(false);
		}
		else if (type == TYPE_BACKWARD && style == STYLE_ENABLED) {
			b1.setImage(reg.get(IconsName.BACK_YELLOW));
			b1.setEnabled(true);
		}
		else if (type == TYPE_FORWARD && style == STYLE_DISABLED) {
			b1.setImage(reg.get(IconsName.NEXT_YELLOW));
			b1.setEnabled(false);
		}
		else if (type == TYPE_FORWARD && style == STYLE_ENABLED) {
			b1.setImage(reg.get(IconsName.NEXT_YELLOW));
			b1.setEnabled(true);
		}
		else if (type == TYPE_FORWARD && style == STYLE_FINISHED) {
			b1.setImage(reg.get(IconsName.NEXT_GREEN));
			b1.setEnabled(true);
		}
		
	}
	

	private void createContent() {
		setLayout(new GridLayout());
		
		GridData gridData = new GridData();
		
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		
		gridData.verticalAlignment = SWT.CENTER;
		
		b1 = new Button(this, SWT.NONE);
		b1.setText(""); //$NON-NLS-1$
		b1.setLayoutData(new GridData());
		b1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (currentType == TYPE_BACKWARD && currentStyle == STYLE_DISABLED) {
					//mainView.
				}
				else if (currentType == TYPE_BACKWARD && currentStyle == STYLE_ENABLED) {
					mainView.previousStep();
				}
				else if (currentType == TYPE_FORWARD && currentStyle == STYLE_ENABLED) {
					mainView.nextStep();
				}
				else if (currentType == TYPE_FORWARD && currentStyle == STYLE_FINISHED) {
					try {
						mainView.performFinish();
					} catch (Exception e1) {
						OurLogger.error(e1.getMessage(), e1);
						MessageDialog.openError(Display.getDefault().getActiveShell(), 
								Messages.NavigationViewer_1, e1.getMessage());
					}
				}
			}
		});
		
		this.setLayoutData(gridData);
	}
	
}
