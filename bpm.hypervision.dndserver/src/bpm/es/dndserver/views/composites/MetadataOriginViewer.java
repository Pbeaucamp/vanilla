package bpm.es.dndserver.views.composites;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import bpm.es.dndserver.Messages;
import bpm.es.dndserver.api.fmdt.FMDTDataSource;
import bpm.es.dndserver.api.repository.RepositoryWrapper;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class MetadataOriginViewer extends MetadataViewer{

	private Text itemName;
	private Text businessModel, businessPackage, connectionName;
	
	public MetadataOriginViewer(Composite parent, int style, FormToolkit toolkit, int type) {
		super(parent, style, toolkit, type);
	}

	protected void refreshViewer(RepositoryWrapper rep){
		
	}
	@Override
	protected Composite createContent(Composite parent, FormToolkit toolkit) {
		Composite main = toolkit.createComposite(parent);
		main.setLayout(new GridLayout(2, false));
		
		Label l = toolkit.createLabel(main, Messages.MetadataOriginViewer_0);
		l.setLayoutData(new GridData());
		
		itemName = toolkit.createText(main, ""); //$NON-NLS-1$
		itemName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		l = toolkit.createLabel(main, "BusinessModel"); //$NON-NLS-1$
		l.setLayoutData(new GridData());
		
		businessModel = toolkit.createText(main, ""); //$NON-NLS-1$
		businessModel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		l = toolkit.createLabel(main, Messages.MetadataOriginViewer_4);
		l.setLayoutData(new GridData());
		
		businessPackage = toolkit.createText(main, ""); //$NON-NLS-1$
		businessPackage.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		l = toolkit.createLabel(main, Messages.MetadataOriginViewer_6);
		l.setLayoutData(new GridData());
		
		
		connectionName = toolkit.createText(main, ""); //$NON-NLS-1$
		connectionName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		return main;
	}
	
	@Override
	protected void showMetadata(FMDTDataSource source) {
		if (source == null){
			itemName.setText(""); //$NON-NLS-1$
			businessModel.setText(""); //$NON-NLS-1$
			businessPackage.setText(""); //$NON-NLS-1$
			connectionName.setText(""); //$NON-NLS-1$
		}
		else{
			
			try {
				RepositoryItem it = project.getInputRepository().getRepositoryClient().getRepositoryService().getDirectoryItem(source.getDirItemId());
				itemName.setText(it.getItemName());
			} catch (Exception e) {
				itemName.setText(""); //$NON-NLS-1$
				e.printStackTrace();
			}
			
			businessModel.setText(source.getBusinessModel());
			businessPackage.setText(source.getBusinessPackage());
			connectionName.setText(source.getConnectionName());
		}
		
	}
}
