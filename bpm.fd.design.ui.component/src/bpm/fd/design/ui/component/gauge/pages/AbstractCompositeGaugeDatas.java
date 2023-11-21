package bpm.fd.design.ui.component.gauge.pages;

import org.eclipse.swt.widgets.Composite;

import bpm.fd.api.core.model.components.definition.IComponentDatas;

public abstract class AbstractCompositeGaugeDatas extends Composite{

	public AbstractCompositeGaugeDatas(Composite parent, int style) {
		super(parent, style);
		
	}
	public abstract IComponentDatas getDatas() throws Exception;
	public abstract boolean isComplete();
	public abstract void fill(IComponentDatas data) ;
}
