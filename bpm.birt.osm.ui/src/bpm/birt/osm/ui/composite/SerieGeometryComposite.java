package bpm.birt.osm.ui.composite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import bpm.birt.osm.core.model.ColorRange;
import bpm.birt.osm.core.model.OsmSerie;
import bpm.birt.osm.core.model.OsmSerieGeometry;
import bpm.birt.osm.ui.Activator;
import bpm.birt.osm.ui.icons.Icons;
import bpm.birt.osm.ui.wizard.ColorRangeDialog;
import bpm.birt.osm.ui.wizard.OsmSeriePage;

public class SerieGeometryComposite extends Group {

	private OsmSerieGeometry serie;
	private TableViewer colors;
	private List<ColorRange> colorRanges = new ArrayList<ColorRange>();
	private Button display;

	public SerieGeometryComposite(Composite parent, int style, OsmSerieGeometry serieOsm, final OsmSeriePage osmSeriePage) {
		super(parent, style);
		this.serie = serieOsm;
		
		this.setLayout(new GridLayout(2, false));
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		this.setText(serie.getName());
		
		display = new Button(this, SWT.RADIO);
		display.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		display.setText("Display this layer");
		display.setSelection(serie.isDisplay());
		display.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				serie.setDisplay(display.getSelection());
				for(Composite comp : osmSeriePage.getSerieComposites()) {
					if(!comp.equals(SerieGeometryComposite.this)) {
						if(comp instanceof SerieGeometryComposite) {
							((SerieGeometryComposite)comp).unSelect();
						}
						else {
							((SerieMarkerComposite)comp).unSelect();
						}
					}
				}
			}
		});
		
		createColor();
		
		
	}
	
	private void createColor(){
		Composite compButton = new Composite(this, SWT.NONE);
		compButton.setLayout(new GridLayout(3, false));
		compButton.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false, 2, 1));
		
		Button addColor = new Button(compButton, SWT.PUSH);
		addColor.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		addColor.setImage(Activator.getDefault().getImageRegistry().get(Icons.ADD_COLOR));
		addColor.setToolTipText("Add Color");
		addColor.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {					
				ColorRangeDialog dial = new ColorRangeDialog(getShell(), colorRanges);
				if(dial.open() == Dialog.OK){
					refreshTableColor();
				}
			}
	
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}	
		});
		
		Button editColor = new Button(compButton, SWT.PUSH);
		editColor.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		editColor.setImage(Activator.getDefault().getImageRegistry().get(Icons.EDIT_COLOR));
		editColor.setToolTipText("Edit Color");
		editColor.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {				
				
				IStructuredSelection ss = ((IStructuredSelection)colors.getSelection());
				
				if (ss.isEmpty()){
					return; 
				}
				
				ColorRangeDialog dial = new ColorRangeDialog(getShell(), colorRanges, (ColorRange)ss.getFirstElement());
				if(dial.open() == Dialog.OK){
					refreshTableColor();
				}
			}
	
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}	
		});
		
		Button deleteColor = new Button(compButton, SWT.PUSH);
		deleteColor.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		deleteColor.setImage(Activator.getDefault().getImageRegistry().get(Icons.DEL_COLOR));
		deleteColor.setToolTipText("Delete Color");
		deleteColor.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)colors.getSelection();
				
				if (ss.isEmpty()){
					return; 
				}
				
				ColorRange color = (ColorRange)ss.getFirstElement();
				colorRanges.remove(color);
				
				refreshTableColor();
			}
	
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}	
		});
		
		colors = new TableViewer(this);
		colors.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		colors.setContentProvider(new ArrayContentProvider());
		colors.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((ColorRange)element).getMin() + " -> " + ((ColorRange)element).getMax() + " = " + ((ColorRange)element).getColor();
			}
		});
		
		colorRanges = serie.getColorRanges();
		
		refreshTableColor();
	}

	private void refreshTableColor() {
		colors.setInput(colorRanges);
	}
	
	//Because of SWT not wanting me to extends Group
	//and I don't want to create a class with a Group field
	@Override
	protected void checkSubclass() {
	}

	public OsmSerie fillSerie() {
		serie.setColorRanges(colorRanges);
		return serie;
	}
	
	public void unSelect() {
		serie.setDisplay(false);
		display.setSelection(false);
	}

}
