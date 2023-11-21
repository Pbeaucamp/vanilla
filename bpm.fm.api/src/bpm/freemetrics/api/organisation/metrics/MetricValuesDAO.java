package bpm.freemetrics.api.organisation.metrics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import bpm.freemetrics.api.utils.Tools;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class MetricValuesDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<MetricValues> findAll() {
		return getHibernateTemplate().find("FROM MetricValues ORDER BY mvValueDate ASC");
	}


	@SuppressWarnings("unchecked")
	public MetricValues findByPrimaryKey(int key) {
		List<MetricValues> c = getHibernateTemplate().find("FROM MetricValues d WHERE d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public int save(MetricValues d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from MetricValues");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public void delete(MetricValues d) {
		getHibernateTemplate().delete(d);
	}
	public void update(MetricValues d) {
		getHibernateTemplate().update(d);
	}
	
	@SuppressWarnings("unchecked")
	public MetricValues getLastValueForAssocId(Integer associd) {
		MetricValues val = null;

		List<MetricValues> c = getHibernateTemplate().find("FROM MetricValues d WHERE d.mvGlAssoc_ID="+associd+" " +
				"AND d.mvValueDate IN (SELECT MAX(f.mvValueDate) FROM MetricValues f WHERE f.mvGlAssoc_ID="+associd+")  ORDER BY d.id DESC");

		if(c != null && !c.isEmpty())
			val = c.get(0);

		return val;
	}

	@SuppressWarnings("unchecked")
	public List<MetricValues> getMetricValuesByAssocId(int id) {
		return getHibernateTemplate().find("FROM MetricValues d WHERE d.mvGlAssoc_ID=" + id + "  ORDER BY d.mvPeriodDate ASC");
	}

	@SuppressWarnings("unchecked")
	public List<MetricValues> getValuesForAssocIdAndDates(int id, Date dateFrom, Date dateTo) {

		if(dateFrom != null && dateTo != null){

			return getHibernateTemplate().find("FROM MetricValues d WHERE d.mvGlAssoc_ID="+id+" AND d.mvValueDate BETWEEN '"+dateFrom+"' AND '"+dateTo+"' ORDER BY d.mvValueDate ASC ");

		}else if(dateFrom != null){

			return getHibernateTemplate().find("FROM MetricValues d WHERE d.mvGlAssoc_ID="+id+" AND d.mvValueDate > '"+dateFrom+"'  ORDER BY d.mvValueDate ASC");

		}else if(dateTo != null){

			return getHibernateTemplate().find("FROM MetricValues d WHERE d.mvGlAssoc_ID="+id+" AND d.mvValueDate < '"+dateTo+"'  ORDER BY d.mvValueDate ASC");

		}else{

			return getMetricValuesByAssocId(id);
		}
	}

	@SuppressWarnings("unchecked")
	public MetricValues getPreviousMetricsValueFor(MetricValues val) {
		MetricValues res = null;

		List<MetricValues> c = getHibernateTemplate().find("FROM MetricValues d WHERE d.mvGlAssoc_ID="+val.getMvGlAssoc_ID()+" " +
				"AND d.mvPeriodDate IN (SELECT MAX(f.mvPeriodDate) FROM MetricValues f WHERE f.mvPeriodDate < '"+val.getMvPeriodDate()+"' AND f.mvGlAssoc_ID="+val.getMvGlAssoc_ID()+")  ORDER BY d.id DESC");

		if(c != null && !c.isEmpty())
			res = c.get(0);

		return res;
	}


	public List<MetricValues> getValuesForAssocId(List<Integer> assoIds) {
		String params = "";
		int first = assoIds.get(0);
		for(int id : assoIds) {
			if(id == first) {
				params += "'" + id + "'";
			}
			else {
				params += ",'" + id + "'";
			}
		}	
		
		return getHibernateTemplate().find("select v from MetricValues v where v.mvGlAssoc_ID in (" + params + ")");
	}


	public void deleteValues(List<MetricValues> deleteValues) {
		List<Integer> ids = new ArrayList<Integer>();
		for(MetricValues val : deleteValues) {
			ids.add(val.getId());
		}
		Query q = getHibernateTemplate().getSessionFactory().openSession().createQuery("delete from MetricValues v where v.id in (:ids)").setParameterList("ids", ids);
		q.executeUpdate();
	}


	public void insertValues(List<MetricValues> insertValues) {
		getHibernateTemplate().saveOrUpdateAll(insertValues);
	}


	public List<MetricValues> getMetricsValuesForAssoIdPeriodeDate(List<Integer> assoIds, String periode, Date date) {
		String params = "";
		int first = assoIds.get(0);
		for(int id : assoIds) {
			if(id == first) {
				params += "'" + id + "'";
			}
			else {
				params += ",'" + id + "'";
			}
		}	
		return getHibernateTemplate().find("select v from MetricValues v where v.mvPeriodDate " + Tools.getBetweenForDatePeriode(periode,date) + " AND v.mvGlAssoc_ID in (" + params + ")");
	}


	public Date getPreviousDateByAssoId(int assoId) {
		String query = "FROM MetricValues d WHERE d.mvGlAssoc_ID=" + assoId +" AND d.mvValueDate IN (SELECT MAX(f.mvValueDate) FROM MetricValues f WHERE f.mvGlAssoc_ID=" + assoId +")";
		List<MetricValues> c = getHibernateTemplate().find(query);
		if (!c.isEmpty()) {
			return c.get(0).getMvValueDate();
		}
		else {
			return null;
		}
	}


	public MetricValues getLastObjectifFor(int objeAssocId) {
		String query = "FROM MetricValues d WHERE d.mvGlAssoc_ID=" + objeAssocId +" ORDER BY d.id DESC LIMIT 1";
		List<MetricValues> c = getHibernateTemplate().find(query);
		if (c.isEmpty()){
			return null;
		}
		return c.get(0);
	}


	public List<MetricValues> getMetricValuesByAssocId(int id, Date startDate, Date endDate, String datePattern) {
		if(startDate == null && endDate == null) {
			return getHibernateTemplate().find("FROM MetricValues d WHERE d.mvGlAssoc_ID=" + id + "  ORDER BY d.mvPeriodDate ASC");
		}
		else if(startDate == null || endDate == null) {
			SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
			if(endDate == null) {
				return getHibernateTemplate().find("FROM MetricValues d WHERE d.mvGlAssoc_ID=" + id + " and d.mvPeriodDate LIKE '" + sdf.format(startDate) + "%' ORDER BY d.mvPeriodDate ASC");
			}
			else {
				return getHibernateTemplate().find("FROM MetricValues d WHERE d.mvGlAssoc_ID=" + id + " and d.mvPeriodDate LIKE '" + sdf.format(endDate) + "%' ORDER BY d.mvPeriodDate ASC");
			}
		}
		else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return getHibernateTemplate().find("FROM MetricValues d WHERE d.mvGlAssoc_ID=" + id + " and d.mvPeriodDate between '" + sdf.format(startDate) + "' and '" + sdf.format(endDate) + "' ORDER BY d.mvPeriodDate ASC");
		}
	}
	
	
}
