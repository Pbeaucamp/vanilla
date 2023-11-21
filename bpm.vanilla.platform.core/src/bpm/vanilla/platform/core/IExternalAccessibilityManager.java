package bpm.vanilla.platform.core;

import java.util.List;

import bpm.vanilla.platform.core.beans.FmdtUrl;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.PublicParameter;
import bpm.vanilla.platform.core.beans.PublicUrl;
import bpm.vanilla.platform.core.beans.PublicUrl.TypeURL;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface IExternalAccessibilityManager {

	public static enum ActionType implements IXmlActionType{
		
		GRANT_ACCESS(Level.INFO), REMOVE_ACCESS(Level.INFO), REMOVE_GROUP_ACCESS(Level.INFO), GET_GRANTED_GROUPS(Level.DEBUG),
		LIST_GRANTS(Level.DEBUG),DEL_GRANT(Level.INFO),
		
		SAVE_URL(Level.INFO),DEL_URL(Level.INFO), FIND_URL(Level.DEBUG),ADD_PARAM(Level.INFO),CLEAR_URL_PARAM(Level.INFO),FIND_PARAM(Level.INFO),
		
		ADD_FMDT(Level.INFO),DEL_FMDT(Level.INFO),UPDATE_FMDT(Level.INFO),FIND_FMDT_BY_NAME(Level.DEBUG), FIND_FMDT_4ITEM(Level.DEBUG), LIST_URL_4ITEM(Level.DEBUG),
		GET_URL_BY_ID(Level.DEBUG), CALL_CLASSIFICATION_KMEAN(Level.DEBUG), CALL_DECISIONTREE(Level.DEBUG), GET_PUBLIC_URLS(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	
	}
	
	public int savePublicUrl(PublicUrl publicUrl) throws Exception;
	/**
	 * delete the PublicUrl and its PublicParameters
	 * @param publicUrlId
	 * @throws Exception
	 */
	public void deletePublicUrl(int publicUrlId) throws Exception;
	public List<PublicUrl> getPublicUrlsByItemIdRepositoryId(int adressableId, int repId) throws Exception;
	public PublicUrl getPublicUrlsByPublicKey(String key) throws Exception;
	public List<PublicUrl> getPublicUrls(int itemId, int repId, TypeURL typeUrl) throws Exception;
	
	public int addPublicParameter(PublicParameter param) throws Exception;
	public void deletePublicParameterForPublicUrlId(int publicUrlId) throws Exception;
	public List<PublicParameter> getParametersForPublicUrl(int publicUrlId) throws Exception;
	
	public int addFmdtUrl(FmdtUrl fmdtUrl) throws Exception;
	public void deleteFmdtUrl(FmdtUrl fmdtUrl) throws Exception;
	public void updateFmdtUrl(FmdtUrl fmdtUrl) throws Exception;
	public FmdtUrl getFmdtUrlByName(String name) throws Exception;
	public List<FmdtUrl> getFmdtUrlForItemId(int directoryItemId) throws Exception;
	
	public PublicUrl getPublicUrlById(int id) throws Exception;
	public Double[][] launchRFonctions(List<List<Double>> varX, List<List<Double>> varY, String function, int nbcluster) throws Exception;
	public String launchRDecisionTree(List<List<Double>> varX, List<String> varY, Double train,  List<String> names) throws Exception;
	
}
