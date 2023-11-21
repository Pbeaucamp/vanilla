package bpm.freemetrics.api.features.infos;

import java.util.List;

public class LogManager {
	private LogDAO dao;

	public LogManager() {
		super();
	}

	public void setDao(LogDAO d) {
		this.dao = d;
	}

	public LogDAO getDao() {
		return dao;
	}

	public List<Log> getLogs() {
		return dao.findAll();
	}

	public Log getLogById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addLog(Log d){

		return dao.save(d);
	}

	public void delLog(Log d) {
		dao.delete(d);
	}

	public void updateLog(Log d) throws Exception {
		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
		}else{
			throw new Exception("This Log/collectivity doesnt exists");
		}
	}

}
