package bpm.freemetrics.api.features.forum;

import java.util.ArrayList;
import java.util.List;

public class ForumManager {
	private ForumDAO dao;

	public ForumManager() {
		super();
	}

	public void setDao(ForumDAO d) {
		this.dao = d;
	}

	public ForumDAO getDao() {
		return dao;
	}

	public List<Forum> getForums() {
		return dao.findAll();
	}

	public Forum getForumById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addForum(Forum d) throws Exception{
		return dao.save(d);
	}

	public void delForum(Forum d) {
		dao.delete(d);
	}

	public void updateForum(Forum d) throws Exception {

		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
		}else{
			throw new Exception("This Forum doesnt exists");
		}

	}

	public List<Forum> getForumForAppMetrAndMetrValId(int appId, int metrId,
			int metrValId) {
		List<Forum> res = new ArrayList<Forum>();

		List<Forum> c = dao.getForumForAppMetrAndMetrValId(appId, metrId,metrValId);

		if(c!= null && !c.isEmpty()){
			for (Forum forum : c) {

				if(forum != null ){
					res.add(forum);
				}
			}
		}
		return res;
	}

	public List<Forum> getForumForAppAndMetrId(int appId, int metrId) {
		List<Forum> res = new ArrayList<Forum>();

		List<Forum> c = dao.getForumForAppAndMetrId(appId, metrId);

		if(c!= null && !c.isEmpty()){
			for (Forum forum : c) {

				if(forum != null){
					res.add(forum);
				}
			}
		}
		return res;
	}

	public List<Forum> getForumByValueId(int valueId) {
		return dao.getForumByValueId(valueId);
	}
}
