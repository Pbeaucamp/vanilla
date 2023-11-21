package bpm.birt.wms.map.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.birt.wms.map.core.reportitem.WMSMapItem;

public class WMSDefinitionPage extends WizardPage {

	private WMSMapItem item;
	
	private Text txtBaseLayerUrl;
	private Text txtBaseLayerName;
	private Text txtVectorLayerUrl;
	
	private Text txtVectorLayerName;
	private Text txtBounds;
	private Text txtTileOrigin;
	private Text txtProjection;
	
	private TextModifyListener listener = new TextModifyListener();

	private Text txtWidth;

	private Text txtHeight;

	private Text txtVanilla;

	public WMSDefinitionPage(String pageName, WMSMapItem item) {
		super(pageName);
		this.item = item;
	}

	@Override
	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(2,false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	
		Label lblVanillaUrl = new Label(mainComposite, SWT.NONE);
		lblVanillaUrl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblVanillaUrl.setText("Vanilla Runtime Url");
		
		txtVanilla = new Text(mainComposite, SWT.BORDER);
		txtVanilla.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtVanilla.addModifyListener(listener);
		
		Label lblWidth = new Label(mainComposite, SWT.NONE);
		lblWidth.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblWidth.setText("Width");
		
		txtWidth = new Text(mainComposite, SWT.BORDER);
		txtWidth.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtWidth.addModifyListener(listener);
		
		Label lblHeight = new Label(mainComposite, SWT.NONE);
		lblHeight.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblHeight.setText("Height");
		
		txtHeight = new Text(mainComposite, SWT.BORDER);
		txtHeight.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtHeight.addModifyListener(listener);
		
		Label lblUrl = new Label(mainComposite, SWT.NONE);
		lblUrl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblUrl.setText("Base Layer Url");
		
		txtBaseLayerUrl = new Text(mainComposite, SWT.BORDER);
		txtBaseLayerUrl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtBaseLayerUrl.addModifyListener(listener);
		
		Label lblLayer = new Label(mainComposite, SWT.NONE);
		lblLayer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblLayer.setText("Base Layer Name");
		
		txtBaseLayerName = new Text(mainComposite, SWT.BORDER);
		txtBaseLayerName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtBaseLayerName.addModifyListener(listener);
		
		Label lblType = new Label(mainComposite, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblType.setText("Vector Layer Url");
		
		txtVectorLayerUrl = new Text(mainComposite, SWT.BORDER);
		txtVectorLayerUrl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtVectorLayerUrl.addModifyListener(listener);
		
		Label lblLong = new Label(mainComposite, SWT.NONE);
		lblLong.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblLong.setText("Vector Layer Name");
		
		txtVectorLayerName = new Text(mainComposite, SWT.BORDER);
		txtVectorLayerName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtVectorLayerName.addModifyListener(listener);
		
		Label lblLat = new Label(mainComposite, SWT.NONE);
		lblLat.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblLat.setText("Bounds");
		
		txtBounds = new Text(mainComposite, SWT.BORDER);
		txtBounds.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtBounds.addModifyListener(listener);
		
		Label lblZoom = new Label(mainComposite, SWT.NONE);
		lblZoom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblZoom.setText("Tile Origin");
		
		txtTileOrigin = new Text(mainComposite, SWT.BORDER);
		txtTileOrigin.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtTileOrigin.addModifyListener(listener);
		
		Label lblProj = new Label(mainComposite, SWT.NONE);
		lblProj.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblProj.setText("Projection");
		
		txtProjection = new Text(mainComposite, SWT.BORDER);
		txtProjection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtProjection.addModifyListener(listener);
		
		setControl(mainComposite);
		
		initValues();
	}

	private void initValues() {
		if(item != null) {
			if(item.getPropertyString(WMSMapItem.P_BASE_LAYER_URL) != null) {
				txtBaseLayerUrl.setText(item.getPropertyString(WMSMapItem.P_BASE_LAYER_URL));
			}
			if(item.getPropertyString(WMSMapItem.P_BASE_LAYER_NAME) != null) {
				txtBaseLayerUrl.setText(item.getPropertyString(WMSMapItem.P_BASE_LAYER_NAME));
			}
			if(item.getPropertyString(WMSMapItem.P_VECTOR_LAYER_URL) != null) {
				txtBaseLayerUrl.setText(item.getPropertyString(WMSMapItem.P_VECTOR_LAYER_URL));
			}
			if(item.getPropertyString(WMSMapItem.P_VECTOR_LAYER_NAME) != null) {
				txtBaseLayerUrl.setText(item.getPropertyString(WMSMapItem.P_VECTOR_LAYER_NAME));
			}
			if(item.getPropertyString(WMSMapItem.P_BOUNDS) != null) {
				txtBaseLayerUrl.setText(item.getPropertyString(WMSMapItem.P_BOUNDS));
			}
			if(item.getPropertyString(WMSMapItem.P_TILE_ORIGIN) != null) {
				txtBaseLayerUrl.setText(item.getPropertyString(WMSMapItem.P_TILE_ORIGIN));
			}
			if(item.getPropertyString(WMSMapItem.P_PROJECTION) != null) {
				txtBaseLayerUrl.setText(item.getPropertyString(WMSMapItem.P_PROJECTION));
			}
			if(item.getPropertyString(WMSMapItem.P_WIDTH) != null) {
				txtWidth.setText(item.getPropertyString(WMSMapItem.P_WIDTH));
			}
			if(item.getPropertyString(WMSMapItem.P_HEIGHT) != null) {
				txtHeight.setText(item.getPropertyString(WMSMapItem.P_HEIGHT));
			}
			if(item.getPropertyString(WMSMapItem.P_VANILLARUNTIME) != null) {
				txtVanilla.setText(item.getPropertyString(WMSMapItem.P_VANILLARUNTIME));
			}
		}
		
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
			
			if(txtBaseLayerUrl.getText() != null && !txtBaseLayerUrl.getText().equalsIgnoreCase("")) {
				item.setProperty(WMSMapItem.P_BASE_LAYER_URL, txtBaseLayerUrl.getText());
			}
			if(txtBaseLayerName.getText() != null && !txtBaseLayerName.getText().equalsIgnoreCase("")) {
				item.setProperty(WMSMapItem.P_BASE_LAYER_NAME, txtBaseLayerName.getText());
			}
			if(txtProjection.getText() != null && !txtProjection.getText().equalsIgnoreCase("")) {
				item.setProperty(WMSMapItem.P_PROJECTION, txtProjection.getText());
			}
			if(txtTileOrigin.getText() != null && !txtTileOrigin.getText().equalsIgnoreCase("")) {
				item.setProperty(WMSMapItem.P_TILE_ORIGIN, txtTileOrigin.getText());
			}
			if(txtVectorLayerName.getText() != null && !txtVectorLayerName.getText().equalsIgnoreCase("")) {
				item.setProperty(WMSMapItem.P_VECTOR_LAYER_NAME, txtVectorLayerName.getText());
			}
			if(txtVectorLayerUrl.getText() != null && !txtVectorLayerUrl.getText().equalsIgnoreCase("")) {
				item.setProperty(WMSMapItem.P_VECTOR_LAYER_URL, txtVectorLayerUrl.getText());
			}
			if(txtBounds.getText() != null && !txtBounds.getText().equalsIgnoreCase("")) {
				item.setProperty(WMSMapItem.P_BOUNDS, txtBounds.getText());
			}
			if(txtWidth.getText() != null && !txtWidth.getText().equalsIgnoreCase("")) {
				if(txtWidth.getText().contains("px")) {
					item.setProperty(WMSMapItem.P_WIDTH, txtWidth.getText());
				}
				else {
					item.setProperty(WMSMapItem.P_WIDTH, txtWidth.getText() + "px");
				}
			}
			if(txtHeight.getText() != null && !txtHeight.getText().equalsIgnoreCase("")) {
				if(txtHeight.getText().contains("px")) {
					item.setProperty(WMSMapItem.P_HEIGHT, txtHeight.getText());
				}
				else {
					item.setProperty(WMSMapItem.P_HEIGHT, txtHeight.getText() + "px");
				}
			}
			if(txtVanilla.getText() != null && !txtVanilla.getText().equalsIgnoreCase("")) {
				item.setProperty(WMSMapItem.P_VANILLARUNTIME, txtVanilla.getText());
			}
			
			getWizard().getContainer().updateButtons();
		}
		
	}
}
