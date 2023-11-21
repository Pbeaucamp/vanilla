package bpm.fd.design.ui.component.map.wms.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.fd.api.core.model.components.definition.maps.openlayers.MapWMSOptions;
import bpm.fd.design.ui.component.Messages;

public class MapWmsOptionsPage extends WizardPage{
	public static final String PAGE_NAME = "bpm.fd.design.ui.component.map.wms.pages.MapWmsOptionsPage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = Messages.MapWmsOptionsPage_1;
	public static final String PAGE_DESCRIPTION = Messages.MapWmsOptionsPage_2;
	
	private Text txtBaseLayerUrl;
	private Text txtBaseLayerName;
	private Text txtVectorLayerUrl;
	
	private Text txtVectorLayerName;
	private Text txtBounds;
	private Text txtTileOrigin;
	private Text txtProjection;
	
	private Text txtOpacity;
	
	private TextModifyListener listener = new TextModifyListener();
	
	public MapWmsOptionsPage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
	
	protected MapWmsOptionsPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {

		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(2,false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	
		Label lblUrl = new Label(mainComposite, SWT.NONE);
		lblUrl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblUrl.setText(Messages.MapWmsOptionsPage_3);
		
		txtBaseLayerUrl = new Text(mainComposite, SWT.BORDER);
		txtBaseLayerUrl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtBaseLayerUrl.addModifyListener(listener);
		
		Label lblLayer = new Label(mainComposite, SWT.NONE);
		lblLayer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblLayer.setText(Messages.MapWmsOptionsPage_4);
		
		txtBaseLayerName = new Text(mainComposite, SWT.BORDER);
		txtBaseLayerName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtBaseLayerName.addModifyListener(listener);
		
		Label lblType = new Label(mainComposite, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblType.setText(Messages.MapWmsOptionsPage_5);
		
		txtVectorLayerUrl = new Text(mainComposite, SWT.BORDER);
		txtVectorLayerUrl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtVectorLayerUrl.addModifyListener(listener);
		
		Label lblLong = new Label(mainComposite, SWT.NONE);
		lblLong.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblLong.setText(Messages.MapWmsOptionsPage_6);
		
		txtVectorLayerName = new Text(mainComposite, SWT.BORDER);
		txtVectorLayerName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtVectorLayerName.addModifyListener(listener);
		
		Label lblLat = new Label(mainComposite, SWT.NONE);
		lblLat.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblLat.setText(Messages.MapWmsOptionsPage_8);
		
		txtBounds = new Text(mainComposite, SWT.BORDER);
		txtBounds.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtBounds.addModifyListener(listener);
		
		Label lblZoom = new Label(mainComposite, SWT.NONE);
		lblZoom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblZoom.setText(Messages.MapWmsOptionsPage_10);
		
		txtTileOrigin = new Text(mainComposite, SWT.BORDER);
		txtTileOrigin.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtTileOrigin.addModifyListener(listener);
		
		Label lblProj = new Label(mainComposite, SWT.NONE);
		lblProj.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblProj.setText(Messages.MapWmsOptionsPage_9);
		
		txtProjection = new Text(mainComposite, SWT.BORDER);
		txtProjection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtProjection.addModifyListener(listener);
		
		Label lblOp = new Label(mainComposite, SWT.NONE);
		lblOp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblOp.setText("Opacity");
		
		txtOpacity = new Text(mainComposite, SWT.BORDER);
		txtOpacity.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtOpacity.addModifyListener(listener);
		
		setControl(mainComposite);
	}

	public MapWMSOptions getOptions() {
		MapWMSOptions opt = new MapWMSOptions();

		opt.setBaseLayerUrl(txtBaseLayerUrl.getText());
		opt.setBaseLayerName(txtBaseLayerName.getText());
		opt.setBounds(txtBounds.getText());
		opt.setProjection(txtProjection.getText());
		opt.setTileOrigin(txtTileOrigin.getText());
		opt.setVectorLayerName(txtVectorLayerName.getText());
		opt.setVectorLayerUrl(txtVectorLayerUrl.getText());
		opt.setOpacity(txtOpacity.getText());
		
		return opt;
	}

	@Override
	public boolean isPageComplete() {
		
		if(txtBaseLayerUrl.getText() != null && !txtBaseLayerUrl.getText().equalsIgnoreCase("") //$NON-NLS-1$
				&& txtBaseLayerName.getText() != null && !txtBaseLayerName.getText().equalsIgnoreCase("") //$NON-NLS-1$
				&& txtProjection.getText() != null && !txtProjection.getText().equalsIgnoreCase("") //$NON-NLS-1$
				&& txtTileOrigin.getText() != null && !txtTileOrigin.getText().equalsIgnoreCase("") //$NON-NLS-1$
				&& txtVectorLayerName.getText() != null && !txtVectorLayerName.getText().equalsIgnoreCase("") //$NON-NLS-1$
				&& txtVectorLayerUrl.getText() != null && !txtVectorLayerUrl.getText().equalsIgnoreCase("") //$NON-NLS-1$
				&& txtBounds.getText() != null && !txtBounds.getText().equalsIgnoreCase("")) { //$NON-NLS-1$
			return true;
		}
		
		return false;
	}
	
	private class TextModifyListener implements ModifyListener {

		@Override
		public void modifyText(ModifyEvent e) {
			getWizard().getContainer().updateButtons();
		}
		
	}
	
	
}
