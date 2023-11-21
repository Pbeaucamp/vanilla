package org.fasd.cubewizard.dimension;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.fasd.i18N.LanguageText;

public class CompositeHelpPattern extends Composite {

	public CompositeHelpPattern(Composite parent, int style) {
		super(parent, style);

		this.setLayout(new GridLayout(1, false));

		createContent();

	}

	private void createContent() {

		Label lblPatterns = new Label(this, SWT.NONE);
		lblPatterns.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		lblPatterns.setText(LanguageText.CompositeHelpPattern_0 + LanguageText.CompositeHelpPattern_1 + LanguageText.CompositeHelpPattern_2 + LanguageText.CompositeHelpPattern_3 + LanguageText.CompositeHelpPattern_4 + LanguageText.CompositeHelpPattern_5 + LanguageText.CompositeHelpPattern_6);

		Label lblExamples = new Label(this, SWT.NONE);
		lblExamples.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		lblExamples.setText(LanguageText.CompositeHelpPattern_7 + LanguageText.CompositeHelpPattern_8 + LanguageText.CompositeHelpPattern_9 + LanguageText.CompositeHelpPattern_10);
	}

}
