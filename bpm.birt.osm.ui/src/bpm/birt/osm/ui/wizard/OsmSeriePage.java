package bpm.birt.osm.ui.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import bpm.birt.osm.core.model.OsmSerie;
import bpm.birt.osm.core.model.OsmSerieGeometry;
import bpm.birt.osm.core.model.OsmSerieMarker;
import bpm.birt.osm.core.reportitem.OsmReportItem;
import bpm.birt.osm.ui.composite.SerieGeometryComposite;
import bpm.birt.osm.ui.composite.SerieMarkerComposite;

public class OsmSeriePage extends WizardPage {

	private OsmReportItem item;
	private ExtendedItemHandle handle;
	private WizardOsmMap wizard;
	private Composite main;
	
	private List<Composite> composites = new ArrayList<Composite>();
	private Composite parentComposite;

	public OsmSeriePage(String pageName, OsmReportItem item, ExtendedItemHandle handle) {
		super(pageName);
		this.item = item;
		this.handle = handle;
		this.wizard = (WizardOsmMap)getWizard();
	}

	@Override
	public void createControl(Composite parent) {
		
		this.parentComposite = parent;
		
		ScrolledComposite scroll = new ScrolledComposite(parent, SWT.V_SCROLL);
		scroll.setLayout(new GridLayout());
		scroll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		main = new Composite(scroll, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		scroll.setContent(main);
		scroll.setMinSize(600, 400);
		
		scroll.setAlwaysShowScrollBars(false);
		scroll.setExpandHorizontal(true);
		scroll.setExpandVertical(true);
		
		setControl(scroll);
	}
	
	public void fillPage() {
		try {
			for(Control c : main.getChildren()) {
				c.dispose();
			}
			composites.clear();
		} catch (Exception e1) {
		}
		if(item.getSerieList() != null) {
			for(Object obj : item.getSerieList()) {
				final OsmSerie serie = (OsmSerie) obj;
				if(serie instanceof OsmSerieGeometry) {
					composites.add(new SerieGeometryComposite(main, SWT.NONE, (OsmSerieGeometry)serie, this));
				}
				else {
					composites.add(new SerieMarkerComposite(main, SWT.NONE, (OsmSerieMarker)serie, this));
				}
				
			}
			
		}
		
		parentComposite.layout(true);
		main.layout(true);
		fillItem();
	}

	public List<OsmSerie> fillItem() {
		List<OsmSerie> series = new ArrayList<OsmSerie>();
		for(Composite comp : composites) {
			if(comp instanceof SerieGeometryComposite) {
				series.add(((SerieGeometryComposite)comp).fillSerie());
			}
			else {
				series.add(((SerieMarkerComposite)comp).fillSerie());
			}
		}
		return series;
	}
	
	public List<Composite> getSerieComposites() {
		return composites;
	}
	
}
