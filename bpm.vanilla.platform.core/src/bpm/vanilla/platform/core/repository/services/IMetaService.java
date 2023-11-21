package bpm.vanilla.platform.core.repository.services;

import java.util.List;

import bpm.vanilla.platform.core.ManageAction;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.beans.meta.Meta;
import bpm.vanilla.platform.core.beans.meta.MetaForm;
import bpm.vanilla.platform.core.beans.meta.MetaLink;
import bpm.vanilla.platform.core.beans.meta.MetaLink.TypeMetaLink;
import bpm.vanilla.platform.core.beans.meta.MetaValue;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface IMetaService {
	
	public static enum ActionType implements IXmlActionType {
		GET_META_BY_FORM(Level.DEBUG), GET_META_LINKS(Level.DEBUG), MANAGE_VALUES(Level.DEBUG), GET_ITEMS_BY_META(Level.DEBUG), GET_FORMS(Level.DEBUG),
		MANAGE_META(Level.DEBUG), GET_META_BY_KEY(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
		
	}
	
	public List<Meta> getMetaByForm(int formId) throws Exception;
	
	public List<MetaLink> getMetaLinks(int itemId, TypeMetaLink type, boolean loadResponse) throws Exception;
	
	public void manageMetaValues(List<MetaLink> values, ManageAction action) throws Exception;
	
	public List<Integer> getItemsByMeta(TypeMetaLink type, List<MetaValue> values) throws Exception;

	public List<MetaForm> getMetaForms() throws Exception;
	
	public Meta manageMeta(Meta meta, ManageAction action) throws Exception;

	public Meta getMeta(String key) throws Exception;

}
