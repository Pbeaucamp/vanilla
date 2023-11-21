package bpm.vanilla.platform.core.runtime.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;

import bpm.vanilla.platform.core.IExternalAccessibilityManager;
import bpm.vanilla.platform.core.beans.FmdtUrl;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.PublicParameter;
import bpm.vanilla.platform.core.beans.PublicUrl;
import bpm.vanilla.platform.core.beans.PublicUrl.TypeURL;
import bpm.vanilla.platform.core.runtime.dao.publicaccess.FmdtUrlDAO;
import bpm.vanilla.platform.core.runtime.dao.publicaccess.PublicParameterDAO;
import bpm.vanilla.platform.core.runtime.dao.publicaccess.PublicUrlDAO;
import bpm.vanilla.platform.core.runtime.dao.security.GroupDAO;
import bpm.vanilla.platform.core.runtime.ged.r.RServer;

public class ExternalAccessibilityManager extends AbstractVanillaManager implements IExternalAccessibilityManager{

	private FmdtUrlDAO fmdtUrlDao;
	private PublicUrlDAO publicUrldao;
	private PublicParameterDAO publicParameterdao;
	private GroupDAO groupDao;
	
	@Override
	public int addFmdtUrl(FmdtUrl fmdtUrl) throws Exception {
		return this.fmdtUrlDao.save(fmdtUrl);
	}
	@Override
	public FmdtUrl getFmdtUrlByName(String name) throws Exception {
		return this.fmdtUrlDao.findByName(name);
	}
	@Override
	public void deleteFmdtUrl(FmdtUrl fmdturl) {
		this.fmdtUrlDao.delete(fmdturl);
	}
	@Override
	public void updateFmdtUrl(FmdtUrl fmdturl) {
		this.fmdtUrlDao.update(fmdturl);
	}
	@Override
	public List<FmdtUrl> getFmdtUrlForItemId(int itemId) throws Exception {
		return this.fmdtUrlDao.findByItemId(itemId);
	}
	@Override
	public int savePublicUrl(PublicUrl pu) {
		return publicUrldao.save(pu);
	}
	@Override
	public PublicUrl getPublicUrlsByPublicKey(String key) {
		List<PublicUrl> l = publicUrldao.findByPublicKey(key);
		
		if (l != null && !l.isEmpty())
			return l.get(0);
		
		return null;
	}
	public List<PublicUrl> getAllPublicUrls() {
		return publicUrldao.find("from PublicUrl");
	}
	@Override
	public void deletePublicUrl(int publicUrlId) {
		getLogger().info("Deleting PublicUrl.is=" + publicUrlId + "...");
		for(PublicParameter pp : publicParameterdao.findByPublicUrlId(publicUrlId)){
			publicParameterdao.delete(pp);
		}
		getLogger().info("Deleted PublicParameters for PublicUrl.is=" + publicUrlId);
		
		List<PublicUrl> pu = publicUrldao.findByPrimaryKey(publicUrlId);
		for (PublicUrl p : pu) {
			publicUrldao.delete(p);
		}
		getLogger().info("DeletedPublicUrl.is=" + publicUrlId);
	}
	@Override
	public int addPublicParameter(PublicParameter pp) {
		return publicParameterdao.save(pp);
	}
	@Override
	public List<PublicParameter> getParametersForPublicUrl(int publicUrlId) {
		return publicParameterdao.findByPublicUrlId(publicUrlId);
	}
	@Override
	public void deletePublicParameterForPublicUrlId(int publicUrlId) {
		List<PublicParameter> pp = publicParameterdao.findByPublicUrlId(publicUrlId);
		for (PublicParameter p : pp) {
			publicParameterdao.delete(p);
		}
	}

	@Override
	public String getComponentName() {
		return getClass().getName();
	}
	@Override
	protected void init() throws Exception {
		
		this.fmdtUrlDao = getDao().getFmdtUrlDao();
		if (this.fmdtUrlDao == null){
			throw new Exception("Missing FmdtUrlDao");
		}
		this.groupDao = getDao().getGroupDao();
		if (this.groupDao == null){
			throw new Exception("Missing GroupDao");
		}
		
		this.publicParameterdao = getDao().getPublicParameterDao();
		if (this.publicParameterdao == null){
			throw new Exception("Missing PublicParameterDao");
		}
		this.publicUrldao = getDao().getPublicUrlDao();
		if (this.publicUrldao == null){
			throw new Exception("Missing PublicUrlDao");
		}
		
		getLogger().info("init done!");
		
	}
	@Override
	public List<PublicUrl> getPublicUrlsByItemIdRepositoryId(int itemId, int repId) throws Exception {
		return publicUrldao.getByItemIdRepositoryId(itemId, repId);
	}
	@Override
	public PublicUrl getPublicUrlById(int id) throws Exception {
		return publicUrldao.findByPrimaryKey(id).get(0);
	}
	
	@Override
	public Double[][] launchRFonctions(List<List<Double>> varX, List<List<Double>> varY, String function, int nbcluster) throws Exception{
		RServer rServer=new RServer();
		return rServer.solrFunctions(varX, varY, function, nbcluster);
	}
	
	@Override
	public String launchRDecisionTree(List<List<Double>> varX, List<String> varY, Double train, List<String> names) throws Exception{
		RServer rServer=new RServer();
		return rServer.solrDecisionTree(varX, varY,train, names);
	}
	@Override
	public List<PublicUrl> getPublicUrls(int itemId, int repId, TypeURL typeUrl) throws Exception {
		return publicUrldao.getUrls(itemId, repId, typeUrl);
	}
}
