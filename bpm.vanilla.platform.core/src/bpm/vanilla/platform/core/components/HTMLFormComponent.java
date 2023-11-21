package bpm.vanilla.platform.core.components;

import java.util.List;

import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.components.forms.IForm;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface HTMLFormComponent {
	public static enum ActionType implements IXmlActionType{
		LIST(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	
	public static final String LIST_FORMS_SERVLET = "/vanilla/htmlForms/list";
	
	/**
	 * @param vanillaGroupId
	 * @return all the IFOrm that must be submited by the VanillaGroup with the given id 
	 */
	public List<IForm> getActiveForms(Integer vanillaGroupId) throws Exception;
}
