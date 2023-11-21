package bpm.es.datasource.analyzer.ui.remapper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import bpm.es.datasource.analyzer.ui.Messages;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class CompositeItem {
	
	private Composite client;
	private Text itemName;
	private Text directoryItemId;
	private Text description;
	private Text xmlModel;

	public Composite getClient() {
		return client;
	}

	public Composite createContent(Composite parent) {

		TabFolder folder = new TabFolder(parent, SWT.TOP);
		folder.setLayout(new GridLayout());
		// folder.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabItem general = new TabItem(folder, SWT.NONE);
		general.setText(Messages.CompositeItem_0);
		/*
		 * details ids, desc,...
		 */
		Composite g = new Composite(folder, SWT.NONE);
		g.setLayout(new GridLayout(2, false));
		g.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label l = new Label(g, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeItem_1);

		itemName = new Text(g, SWT.BORDER);
		itemName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		// l = new Label(g, SWT.NONE);
		// l.setLayoutData(new GridData());
		// l.setText("Item Path");
		//		
		// itemPath = new Text(g, SWT.BORDER);
		// itemPath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER,
		// true, false));

		l = new Label(g, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeItem_2);

		directoryItemId = new Text(g, SWT.BORDER);
		directoryItemId.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		l = new Label(g, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeItem_5);

		description = new Text(g, SWT.BORDER);
		description.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		general.setControl(g);

		TabItem xml = new TabItem(folder, SWT.NONE);
		xml.setText(Messages.CompositeItem_6);

		xmlModel = new Text(folder, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		xmlModel.setLayoutData(new GridData(GridData.FILL_BOTH));

		xml.setControl(xmlModel);

		client = folder;
		return client;
	}

	public void setDatas(RepositoryItem item, String xml) {
		directoryItemId.setText(item.getId() + ""); //$NON-NLS-1$
		itemName.setText(item.getItemName());
		description.setText(item.getComment());
		xmlModel.setText(xml);
	}

	public void clear() {
		directoryItemId.setText(""); //$NON-NLS-1$
		itemName.setText(""); //$NON-NLS-1$
		description.setText(""); //$NON-NLS-1$
		xmlModel.setText(""); //$NON-NLS-1$

	}
}
