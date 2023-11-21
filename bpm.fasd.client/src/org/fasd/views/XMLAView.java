package org.fasd.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.fasd.i18N.LanguageText;
import org.fasd.views.composites.CompositeXMLA;
import org.freeolap.FreemetricsPlugin;

public class XMLAView extends ViewPart {
	public static final String ID = "org.fasd.views.xmlaView"; //$NON-NLS-1$
	private CompositeXMLA comp;

	public XMLAView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		comp = new CompositeXMLA(parent, SWT.NONE);
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		FreemetricsPlugin.getDefault().registerXMLAView(this);
		comp.fillData();
	}

	@Override
	public void setFocus() {
		if (FreemetricsPlugin.getDefault().isRepositoryConnectionDefined()) {
			getViewSite().getActionBars().getStatusLineManager().setMessage(LanguageText.XMLAView_1 + FreemetricsPlugin.getDefault().getRepositoryConnection().getContext().getRepository().getUrl());
		} else {
			getViewSite().getActionBars().getStatusLineManager().setMessage(LanguageText.XMLAView_2);
		}

	}

	public void refresh() {
		if (!comp.isDisposed())
			comp.fillData();

	}

}
