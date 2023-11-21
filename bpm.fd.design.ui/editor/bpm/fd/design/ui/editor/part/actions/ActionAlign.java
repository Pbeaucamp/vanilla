package bpm.fd.design.ui.editor.part.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;

import bpm.fd.api.core.model.structure.IContainer;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.icons.Icons;

public class ActionAlign extends Action{
	public static final String ALIGN_LEFT_ID = "bpm.fd.design.ui.editor.part.actions.align.left";
	public static final String ALIGN_RIGHT_ID = "bpm.fd.design.ui.editor.part.actions.align.right";
	public static final String ALIGN_TOP_ID = "bpm.fd.design.ui.editor.part.actions.align.top";
	public static final String ALIGN_BOTTOM_ID = "bpm.fd.design.ui.editor.part.actions.align.bottom";
	public static final String RESIZE_HORIZONTAL_ID = "bpm.fd.design.ui.editor.part.actions.resize.horizontal";
	public static final String RESIZE_VERTICAL_ID = "bpm.fd.design.ui.editor.part.actions.resize.vertical";
	public static enum Type{
		LEFT, RIGHT, TOP, BOTTOM, RESIZE_H, RESIZE_V;
	}
	private Type type;
	
	public ActionAlign(Type type){
		this.type = type;
		setText(type.name());
		switch (type) {
		case LEFT:
			setId(ALIGN_LEFT_ID);
			setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.ALIGN_LEFT));
			break;
		case RIGHT:
			setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.ALIGN_RIGHT));
			setId(ALIGN_RIGHT_ID);
			break;
		case TOP:	
			setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.ALIGN_TOP));
			setId(ALIGN_TOP_ID);
			break;
		case BOTTOM:	
			setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.ALIGN_BOTTOM));
			setId(ALIGN_BOTTOM_ID);
			break;
		case RESIZE_H:
			setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.RESIZE_HORIZONTAL));
			setId(RESIZE_HORIZONTAL_ID);
			break;
		case RESIZE_V:
			setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.RESIZE_VERTICAL));
			setId(RESIZE_VERTICAL_ID);
			break;
		}
		
	}
	
	public void run(){
		ISelectionService s = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		IStructuredSelection ss = (IStructuredSelection)s.getSelection();
		
		List<IContainer> toAlign = new ArrayList<IContainer>();
		IContainer ref = null;
		

		for(Object o : ss.toList()){
			if (o instanceof EditPart){
				if (((EditPart)o).getModel() instanceof IContainer){
					IContainer c = (IContainer)((EditPart)o).getModel() ;
					toAlign.add(c);
					if (ref == null){
						ref = c;
					}
					else{
						switch (type) {
						case TOP:
							if (c.getPosition().y < ref.getPosition().y){
								ref = c;
							}
							break;
						case BOTTOM:
							if (c.getPosition().y > ref.getPosition().y){
								ref = c;
							}
							break;
						case LEFT:
							if (c.getPosition().x < ref.getPosition().x){
								ref = c;
							}
							break;
						case RIGHT:
							if (ref == c){
								ref = null;
							}
							else if (ref != c && c.getPosition().x + c.getSize().x > ref.getPosition().x + ref.getSize().x){
								ref = c;
							}
							break;
						case RESIZE_H:
							if (ref == c){
								ref = null;
							}
							else if (ref != c  && c.getSize().x > ref.getSize().x){
								ref = c;
							}
							break;
						case RESIZE_V:
							if (toAlign.indexOf(c) > 0 && c.getSize().y > ref.getSize().y){
								ref = c;
							}
							break;
							
						}
					}
				}
			}
		}
		
		if (ref != null && !toAlign.isEmpty()){
			for(IContainer c : toAlign){
				if (c != ref){
					switch (type) {
					case TOP:
					case BOTTOM:
						c.setPosition(c.getPosition().x, ref.getPosition().y);
						break;
					case LEFT:
						c.setPosition(ref.getPosition().x, c.getPosition().y);
						break;
					case RIGHT:
						
						int right = ref.getPosition().x + ref.getSize().x;
						int actualRight = c.getPosition().x + c.getSize().x;
						
						c.setPosition(c.getPosition().x + right - actualRight, c.getPosition().y);
						break;
					
					}
				}
			}
			if (toAlign.size() > 1){
				switch(type){
				case RESIZE_H:
					toAlign.get(0).setSize(toAlign.get(1).getSize().x, toAlign.get(0).getSize().y);
					break;
				case RESIZE_V:
					toAlign.get(0).setSize(toAlign.get(0).getSize().x, toAlign.get(1).getSize().y);
					break;
				}
			}
			
		}
		for(Object o : ss.toList()){
			
			if (o instanceof EditPart){
				((EditPart)o).refresh();
			}
		}
	}
}
