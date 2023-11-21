package org.fasd.views.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.fasd.datasource.DataObject;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPRelation;
import org.freeolap.FreemetricsPlugin;

public class DialogGraphRelations extends Dialog {
	private Label label;
	private DataObject table;
	public DialogGraphRelations(Shell parentShell, DataObject table) {
		super(parentShell);
		this.table = table;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout());
		ScrolledComposite sc = new ScrolledComposite(container, SWT.H_SCROLL);
		Composite child = new Composite(sc, SWT.NONE);
		child.setLayout(new FillLayout());
		
    	label= new Label(child, SWT.NONE);
    	child.setSize(800, 600);
    	draw();
    	sc.setContent(child);
		
    	return parent;
	}
	
	private void draw(){
		Image image = new Image(Display.getCurrent(), 8000, 6000);
		GC gc = new GC(image);
		
		gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
		Point cent = new Point(400, 300);
		
		//draw selected table
		gc.fillRectangle(cent.x, cent.y, 50, 50);
		gc.drawText(table.getName(), cent.x, cent.y, true);
		
		//
		List<DataObject> obj = new ArrayList<DataObject>();
		for(OLAPRelation r : FreemetricsPlugin.getDefault().getFAModel().getRelations()){
			if (r.getLeftObject() == table){
				obj.add(r.getRightObject());
			}
			else if (r.getRightObject() == table){
				obj.add(r.getLeftObject());
			}
		}
		
		//
		
		if (obj.size() == 0){
			MessageDialog.openInformation(this.getShell(), LanguageText.DialogGraphRelations_Info, LanguageText.DialogGraphRelations_No_Rel);
			this.close();
			return;
			
		}
		int height = 600 / obj.size();
		int i =0;
		
		for(DataObject o : obj){
			gc.fillRectangle(cent.x + 150, height * i + 5, 50, 50);
			gc.drawText(o.getName(), cent.x + 150, height * i + 5, true);
			gc.drawLine(cent.x + 50, cent.y + 25, cent.x + 150, height * i + 25);
			i++;
		}
		
		
		gc.dispose();
		label.setImage(image);
	}

	@Override
	protected void initializeBounds() {
		this.getShell().setText(LanguageText.DialogGraphRelations_Graph_Rep);
		super.initializeBounds();
	}

}
