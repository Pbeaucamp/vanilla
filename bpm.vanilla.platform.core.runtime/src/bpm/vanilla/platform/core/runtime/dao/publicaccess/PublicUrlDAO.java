package bpm.vanilla.platform.core.runtime.dao.publicaccess;


import java.util.Date;
import java.util.List;
import java.util.UUID;

import bpm.vanilla.platform.core.beans.PublicUrl;
import bpm.vanilla.platform.core.beans.PublicUrl.TypeURL;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class PublicUrlDAO extends HibernateDaoSupport {


	public List<PublicUrl> findAll() {
		return getHibernateTemplate().find("from PublicUrl");
	}

	public List<PublicUrl> findByPrimaryKey(Integer key) {
		return (List<PublicUrl>) getHibernateTemplate().find("from PublicUrl where id=" + key);
	}

	public int save(PublicUrl d) {
		d.setPublicKey(UUID.randomUUID().toString());
		d.setCreationDate(new Date());
		return (Integer)getHibernateTemplate().save(d);
	}
	
	public void delete(PublicUrl d) {
		getHibernateTemplate().delete(d);
	}
	
	public void update(PublicUrl d) {
		getHibernateTemplate().update(d);
	}
	
	public List<PublicUrl> findByPublicKey(String key) {
		return (List<PublicUrl>) getHibernateTemplate().find("from PublicUrl where publicKey='" + key + "'");
	}
	
	public List<PublicUrl> findByGroupdId(int groupId) {
		return (List<PublicUrl>) getHibernateTemplate().find("from PublicUrl where groupId=" + groupId);
	}

	public List<PublicUrl> getByItemIdRepositoryId(int itemId, int repId) {
		return (List<PublicUrl>) getHibernateTemplate().find("from PublicUrl where itemId=" + itemId + " And repositoryId=" +repId);
	}

	public List<PublicUrl> getUrls(int itemId, int repId, TypeURL typeUrl) {
		return (List<PublicUrl>) getHibernateTemplate().find("from PublicUrl where itemId=" + itemId + " And repositoryId=" +repId + " AND typeUrl=" + typeUrl.getType());
	}
}
