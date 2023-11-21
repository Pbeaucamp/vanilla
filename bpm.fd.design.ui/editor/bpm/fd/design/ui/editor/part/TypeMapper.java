package bpm.fd.design.ui.editor.part;


public class TypeMapper {}/*implements ITypeMapper {

	
	public Class mapType(Object object) {
		if (object instanceof EditPart){
			if (((EditPart)object).getModel() instanceof CellWrapper){
				try{
					return ((CellWrapper)((EditPart)object).getModel()).getCell().getContent().get(0).getClass();
				}catch(Exception ex){
					return null;
				}
			}
			else if (((EditPart)object).getModel() instanceof IStructureElement){
				return ((EditPart)object).getModel().getClass();
			}
			
		}
		return null;
	}
}*/