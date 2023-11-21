package org.fasd.cubewizard.dimension.preload;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPHierarchy;

public class CompositePreloadDimension extends Composite {
	private CheckboxTreeViewer dimensions, levels;

	public CompositePreloadDimension(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, true));

		Label lblDimensionPreloadStrategy = new Label(this, SWT.NONE);
		lblDimensionPreloadStrategy.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		lblDimensionPreloadStrategy.setBounds(0, 0, 55, 15);
		lblDimensionPreloadStrategy.setText(LanguageText.CompositePreloadDimension_0);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);

		Label lblDimensions = new Label(this, SWT.NONE);
		lblDimensions.setBounds(0, 0, 55, 15);
		lblDimensions.setText(LanguageText.CompositePreloadDimension_1);

		Label lblLevels = new Label(this, SWT.NONE);
		lblLevels.setBounds(0, 0, 55, 15);
		lblLevels.setText(LanguageText.CompositePreloadDimension_2);

		dimensions = new CheckboxTreeViewer(this, SWT.BORDER);
		dimensions.setContentProvider(new PreloadDimensionContentProvider());
		dimensions.setLabelProvider(new FasdModelElementsLabelProvider());
		dimensions.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection) dimensions.getSelection();

				if (ss.isEmpty() || !(ss.getFirstElement() instanceof OLAPHierarchy)) {
					levels.setInput(null);
				} else {
					levels.setInput(((OLAPHierarchy) ss.getFirstElement()).getLevels());
				}

			}
		});

		Tree tree = dimensions.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		levels = new CheckboxTreeViewer(this, SWT.BORDER);
		levels.setContentProvider(new PreloadDimensionContentProvider());
		levels.setLabelProvider(new FasdModelElementsLabelProvider());
		Tree tree_1 = levels.getTree();
		tree_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}
}
