package bpm.vanilla.portal.client.widget.custom;

import bpm.gwt.commons.shared.repository.PortailRepositoryDirectory;
import bpm.vanilla.portal.client.panels.center.RepositoryContentPanel;
import bpm.vanilla.portal.client.panels.navigation.TypeViewer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class BreadCrumb extends Composite {

	private static BreadCrumbUiBinder uiBinder = GWT.create(BreadCrumbUiBinder.class);

	interface BreadCrumbUiBinder extends UiBinder<Widget, BreadCrumb> {
	}

	@UiField
	Label lblElem;

	private RepositoryContentPanel repositoryContentPanel;
	private PortailRepositoryDirectory directory;
	private TypeViewer typeViewer;
	
	public BreadCrumb(RepositoryContentPanel repositoryContentPanel, PortailRepositoryDirectory directory, TypeViewer typeViewer) {
		initWidget(uiBinder.createAndBindUi(this));
		this.repositoryContentPanel = repositoryContentPanel;
		this.directory = directory;
		this.typeViewer = typeViewer;
		
		lblElem.setText(directory.getName());
	}

	@UiHandler("lblElem")
	public void onSelect(ClickEvent e) {
		repositoryContentPanel.displayData(directory, typeViewer);
	}

}
