package bpm.mdm.ui.model.composites.viewer;

import java.util.List;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.Model;

public class MdmObservableFactory implements IObservableFactory{

	@Override
	public IObservable createObservable(Object target) {
		IObservableList r = null;
		if (target instanceof List){
			r = new WritableList((List)target, Model.class);
		}
		else if (target instanceof Model){
			r = new WritableList(((Model)target).getEntities(), Entity.class);
		}
		else if (target instanceof Entity){
			r = new WritableList(((Entity)target).getAttributes(), Attribute.class);
		
		}
		return r;
	}

}
