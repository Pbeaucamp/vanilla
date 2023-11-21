package bpm.android.vanilla.core;

import bpm.android.vanilla.core.beans.AndroidVanillaContext;
import bpm.android.vanilla.core.xstream.IXmlActionType;

public interface IAndroidVanillaManager {
	
	public enum ActionType implements IXmlActionType {
		GET_GROUP_REPOSITORY,
		GET_CONTEXT_WITH_PUBLIC_GROUP_AND_REPOSITORY,
		CONNECT
	}
	
	public AndroidVanillaContext getGroupsAndRepositories(AndroidVanillaContext vanillaContext) throws Exception;
	
	public AndroidVanillaContext getContextWithPublicGroupAndRepository(AndroidVanillaContext vanillaContext) throws Exception;
	
	public String connect(AndroidVanillaContext vanillaContext) throws Exception;
}
