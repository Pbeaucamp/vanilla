package bpm.gateway.core.transformations;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.sorting.RunSort;
import bpm.vanilla.platform.core.IRepositoryContext;

public class SortTransformation extends AbstractTransformation {

	
	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	private List<SortElement> sorts = new ArrayList<SortElement>();
	
	private List<SortElement> sortsLoading = null;
	private List<SortElementIndex> sortsLoadingIndex = null;
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("sortStream");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		if (getContainer() != null){
			e.addElement("container-ref").setText(getContainer());
		}
		
		for(SortElement s : sorts){
			Element c = e.addElement("sortElements");
			c.addElement("typeSort").setText(s.isType() + "");
			c.addElement("columnSort").setText(s.getColumnSort());
		}
		
		return e;
	}
	
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunSort(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		descriptor = new DefaultStreamDescriptor();

		if (!isInited()){
			return;
		}
		for(Transformation t : inputs){
			try {
//				int i = 0;
				for(StreamElement e : t.getDescriptor(this).getStreamElements()){
					descriptor.addColumn(e.clone(getName(), t.getName()));
					
					//Seems that this is not used
//					if (sorts.size() < i){
//						SortElement s = new SortElement();
//						s.setIndice(i++);
//						s.setType(true);
//						sorts.add(s);
//					}
					
				}
			} catch (ServerException e) {
				e.printStackTrace();
			}
			
		}
		
//		int count = 0;
//		for(int i = descriptor.getStreamElements().size(); i < sorts.size() - count; i++){
//			sorts.remove(i - count);
//			count++;
//		}
		
		List<SortElement> toRm = new ArrayList<SortElement>();
		for(SortElement elem : sorts) {
			boolean finded = false;
			for(StreamElement e : descriptor.getStreamElements()) {
				if((e.originTransfo + "::" + e.name).equals(elem.getColumnSort())) {
					finded = true;
					break;
				}
			}
			if(!finded) {
				toRm.add(elem);
			}
		}
		sorts.removeAll(toRm);
		
		if (sortsLoading != null){
			sorts = new ArrayList<SortElement>();
			for(SortElement e : sortsLoading){
				sorts.add(e);
			}
			sortsLoading = null;
		}
		else if(sortsLoadingIndex != null){
			sorts = new ArrayList<SortElement>();
			for(SortElementIndex e : sortsLoadingIndex){
				addSort(e.getIndice(), e.isType());
			}
			sortsLoadingIndex = null;			
		}
		
		
		for(Transformation t : outputs){
			t.refreshDescriptor();
		}
	}
	
	public Integer getIndiceForColumn(String columnSort){
		List<StreamElement> elements = null;
		try {
			elements = descriptor.getStreamElements();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(elements != null){
			
			for(int i=0; i<elements.size(); i++){
				if(columnSort.equals(elements.get(i).originTransfo + "::" + elements.get(i).name)){
					return i;
				}
			}
		}
		return null;
	}
	
	private void addSort(int index, boolean isAscendant){
		StreamElement element = null;
		try {
			element = descriptor.getStreamElements().get(index);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(element != null){
			SortElement sort = new SortElement();
			sort.setColumnSort(element.originTransfo + "::" + element.name);
			sort.setType(isAscendant);
			
			sorts.add(sort);
		}
	}

	public Transformation copy() {
		SortTransformation copy = new SortTransformation();
		
		
		copy.setDescription(description);
		copy.setName("copy of " + name);
		

		return copy;
	}

	@Override
	public boolean addInput(Transformation t)throws Exception{
		if (inputs.size() != 0 && !inputs.contains(t)){
			throw new Exception("Cannot have more tha one Stream input");
		}
		
		
		
		boolean b =  super.addInput(t);
		
		
		if (b){
			refreshDescriptor();
		}
		return b;
	}
	
	@Override
	public void removeInput(Transformation t){
		super.removeInput(t);
		refreshDescriptor();
	}
	
	
	public void setOrder(String indice, String ascendant){
		try{
			SortElementIndex se = new SortElementIndex();
			se.setIndice(Integer.parseInt(indice));
			se.setType(Boolean.parseBoolean(ascendant));
			
			if(sortsLoadingIndex == null){
				sortsLoadingIndex = new ArrayList<SortElementIndex>();
			}
			sortsLoadingIndex.add(se);
		}catch(Exception e){
			
		}
	}
	
	
	public void setOrderElements(String columnSort, String ascendant){
		try{
			SortElement se = new SortElement();
			se.setColumnSort(columnSort);
			se.setType(Boolean.parseBoolean(ascendant));
			
			if(sortsLoading == null){
				sortsLoading = new ArrayList<SortElement>();
			}
			sortsLoading.add(se);
		}catch(Exception e){
			
		}
		
	}
	
	public void setOrder(StreamElement e , boolean ascendant){
		for(int k = 0; k < sorts.size(); k++){
			if (sorts.get(k).getColumnSort().equals(e.getFullName())){
				sorts.get(k).setType(ascendant);
				return;
			}
		}
		
		SortElement se = new SortElement();
		se.setColumnSort(e.originTransfo + "::" + e.name);
		se.setType(ascendant);
		
		sorts.add(se);
	}
	
	public void removeOrder(StreamElement e){
		for(int k = 0; k < sorts.size(); k++){
			if (sorts.get(k).getColumnSort().equals(e.originTransfo + "::" + e.name)){
				sorts.remove(sorts.get(k));
				break;
			}
		}
	}
	
	
	public void swapOrders(int i1, int i2) throws Exception {
		
		StreamElement element1 = null;
		try {
			descriptor.getStreamElements().get(i1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		StreamElement element2 = null;
		try {
			element2 = descriptor.getStreamElements().get(i2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(element1 != null && element2 != null){
			SortElement e1 = null;
			SortElement e2 = null;
			
			for(SortElement s : sorts){
				if (s.getColumnSort().equals(element1.getFullName())){
					e1 = s;
				}
				if (s.getColumnSort().equals(element2.getFullName())){
					e2 = s;
				}
			}
			
			
			i1 = sorts.indexOf(e1);
			i2 = sorts.indexOf(e2);
			
			SortElement buf = sorts.get(i1);
			sorts.set(i1, sorts.get(i2));
			sorts.set(i2, buf);
		}
		else {
			throw new Exception("An error occured during the swap. It seems that a column does not exist anymore.");
		}
	}

	public List<SortElement> getSorts() {
		return sorts;
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("Sort on Fields : \n");
		for(SortElement se : sorts){
			buf.append("\t- " + se.getColumnSort().split("::")[1] + " " + (se.isType()? " ASC " : " DESC ") + "\n");
		}
		return buf.toString();
	}
	
	/**
	 * 
	 * This class is use for old model based on index. We use it just for the digester
	 * 
	 * @author svi
	 *
	 */
	private class SortElementIndex {
		private int indice;
		private boolean type;
		
		public boolean isType() {
			return type;
		}
		
		public void setType(boolean type) {
			this.type = type;
		}
		
		public int getIndice() {
			return indice;
		}
		
		public void setIndice(int indice) {
			this.indice = indice;
		}
	}
}
