package bpm.vanilla.designer.ui.common;

import bpm.vanilla.platform.core.IRepositoryContext;

public interface IRepositoryContextChangedListener {

	public void repositoryContextChanged(IRepositoryContext oldValue, IRepositoryContext newValue);
}
