package bpm.birt.osm.ui.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.birt.osm.core.model.OsmSerie;
import bpm.birt.osm.core.model.OsmSerieMarker;
import bpm.birt.osm.ui.wizard.OsmSeriePage;

public class SerieMarkerComposite extends Group {

	private OsmSerieMarker serie;
	
	private Text txtMin, txtMax;
	
	private Button display;

	public SerieMarkerComposite(Composite parent, int style, OsmSerieMarker serieMarker, final OsmSeriePage osmSeriePage) {
		super(parent, style);
		this.serie = serieMarker;
		
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
					if(!comp.equals(SerieMarkerComposite.this)) {
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
		
		Label lblMin = new Label(this, SWT.NONE);
		lblMin.setLayoutData(new GridData(SWT.FILL, SWT.FILL,false, false, 1, 1));
		lblMin.setText("Minimum marker size");
		
		txtMin = new Text(this, SWT.BORDER);
		txtMin.setLayoutData(new GridData(SWT.FILL, SWT.FILL,true, false, 1, 1));
		txtMin.setText(serie.getMinMarkerSize()+"");
		
		Label lblMax = new Label(this, SWT.NONE);
		lblMax.setLayoutData(new GridData(SWT.FILL, SWT.FILL,false, false, 1, 1));
		lblMax.setText("Maximum marker size");
		
		txtMax = new Text(this, SWT.BORDER);
		txtMax.setLayoutData(new GridData(SWT.FILL, SWT.FILL,true, false, 1, 1));
		txtMax.setText(serie.getMaxMarkerSize()+"");
	}

	public OsmSerie fillSerie() {
		serie.setMinMarkerSize(Integer.parseInt(txtMin.getText()));
		serie.setMaxMarkerSize(Integer.parseInt(txtMax.getText()));
		return serie;
	}
	
	//Because of SWT not wanting me to extends Group
	//and I don't want to create a class with a Group field
	@Override
	protected void checkSubclass() {
	}
	
	public void unSelect() {
		serie.setDisplay(false);
		display.setSelection(false);
	}

}
