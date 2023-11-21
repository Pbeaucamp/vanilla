package bpm.fd.design.ui.properties.model.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;

import bpm.fd.api.core.model.components.definition.markdown.ComponentMarkdown;

public class MarkdownEditor extends VanillaObjectEditor{

	public MarkdownEditor(Composite parent) {
		super(parent);
		
	}

	@Override
	protected void setHeight(Integer height) {
		//rien
	}

	@Override
	protected void setWidth(Integer width) {
		//rien
	}

	@Override
	protected int getHeight() {
		return 0;
	}

	@Override
	protected int getWidth() {
		return 0;
	}

	@Override
	protected void setItemId(Integer id) {
		((ComponentMarkdown)getComponentDefinition()).setDirectoryItemId(id);
	}

	@Override
	protected void createContent(ExpandBar parent) {
		//rien
	}
}
