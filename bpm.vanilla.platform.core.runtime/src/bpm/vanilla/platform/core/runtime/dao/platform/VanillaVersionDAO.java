package bpm.vanilla.platform.core.runtime.dao.platform;


import java.util.List;

import bpm.vanilla.platform.core.beans.VanillaVersion;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class VanillaVersionDAO extends HibernateDaoSupport {
	
	public VanillaVersion findLast() {
		List<VanillaVersion> vers =  getHibernateTemplate().find("from VanillaVersion");
		
		VanillaVersion last = null;
		for(VanillaVersion v : vers){
			if (last == null){
				last = v;
			}
			else{
				if (last.getId() < v.getId()){
					last = v;
				}
			}
		}
		
		return last;
	}


}
