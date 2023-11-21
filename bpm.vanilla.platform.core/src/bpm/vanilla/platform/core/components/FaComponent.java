package bpm.vanilla.platform.core.components;

import java.io.InputStream;

import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
/**
 * This class is used to generate HTML from a FaView.
 * 
 * I think this should become more a UolapComponent to manage execution on the UOLAP runtime...
 * we'll see later....
 * @author ludo
 *
 */
public interface FaComponent {
	public static enum ActionType implements IXmlActionType{
		RUN_VIEW(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	/** 
	 * generate HTML from a FaView identifier
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public InputStream getFaViewHtml(IRuntimeConfig config) throws Exception;
}
