package bpm.vanilla.map.design.ui.composite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.norparena.mapmanager.Messages;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapObject;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapObjectProperty;
import bpm.vanilla.map.model.openlayers.impl.OpenLayersMapObjectProperty;

public class WMSMapComposite extends Composite {

	private Text txtUrl;
	private Text txtName;
	private Text txtLayers;
	private Text txtType;
	
	public WMSMapComposite(Composite parent, int style) {
		super(parent, style);
		createComposite();
	}

	public void createComposite() {
		
		this.setLayout(new GridLayout(2, false));
		
		Label lblName = new Label(this, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblName.setText(Messages.WMSMapComposite_0);
		
		txtName = new Text(this, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Label lblUrl = new Label(this, SWT.NONE);
		lblUrl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblUrl.setText(Messages.WMSMapComposite_1);
		
		txtUrl = new Text(this, SWT.BORDER);
		txtUrl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Label lblType = new Label(this, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblType.setText(Messages.WMSMapComposite_2);
		
		txtType = new Text(this, SWT.BORDER);
		txtType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Label lblLayers = new Label(this, SWT.NONE);
		lblLayers.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblLayers.setText(Messages.WMSMapComposite_3);
		
		txtLayers = new Text(this, SWT.BORDER);
		txtLayers.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
	}
	
	public List<IOpenLayersMapObjectProperty> getProperties() {
		IOpenLayersMapObjectProperty propName = new OpenLayersMapObjectProperty();
		propName.setName(IOpenLayersMapObjectProperty.PROP_NAME);
		propName.setValue(txtName.getText());
		
		IOpenLayersMapObjectProperty propUrl = new OpenLayersMapObjectProperty();
		propUrl.setName(IOpenLayersMapObjectProperty.PROP_URL);
		propUrl.setValue(txtUrl.getText());
		
		IOpenLayersMapObjectProperty propLayers = new OpenLayersMapObjectProperty();
		propLayers.setName(IOpenLayersMapObjectProperty.PROP_LAYERS);
		propLayers.setValue(txtLayers.getText());
		
		IOpenLayersMapObjectProperty propType = new OpenLayersMapObjectProperty();
		propType.setName(IOpenLayersMapObjectProperty.PROP_TYPE);
		propType.setValue(txtType.getText());
		
		List<IOpenLayersMapObjectProperty> props = new ArrayList<IOpenLayersMapObjectProperty>();
		props.add(propName);
		props.add(propUrl);
		props.add(propLayers);
		props.add(propType);
		
		return props;
	}

	public void fillData(IOpenLayersMapObject map) {
		
		if(map.getId() != null) {
			List<IOpenLayersMapObjectProperty> props = map.getProperties();
			
			for(IOpenLayersMapObjectProperty prop : props) {
				
				if(prop.getName().equals(IOpenLayersMapObjectProperty.PROP_LAYERS)) {
					txtLayers.setText(prop.getValue());
				}
				else if(prop.getName().equals(IOpenLayersMapObjectProperty.PROP_NAME)) {
					txtName.setText(prop.getValue());
				}
				else if(prop.getName().equals(IOpenLayersMapObjectProperty.PROP_TYPE)) {
					txtType.setText(prop.getValue());
				}
				else if(prop.getName().equals(IOpenLayersMapObjectProperty.PROP_URL)) {
					txtUrl.setText(prop.getValue());
				}
			}
			
		}
		
	}
}
