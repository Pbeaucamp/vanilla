package bpm.fd.design.ui.component.map;

import org.eclipse.swt.widgets.Composite;

import bpm.fd.api.core.model.components.definition.maps.IMapDatas;

public interface ICompositeMapDatas {
	public IMapDatas getMapDatas();
	public Composite createContent(Composite parent);
	public Composite getClient();
	public void fill(IMapDatas datas);
}
