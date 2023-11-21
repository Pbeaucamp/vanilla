package bpm.gateway.core.veolia;

import java.util.List;

import bpm.gateway.core.veolia.abonnes.AbonnesDAO.ClassAbonnee;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;

public interface IXmlManager {
	
	public LogInsertXML createLog(ClassAbonnee classAb, String fileName, Integer idChg);
	
	public ClassDefinition getClassDefinition(ClassAbonnee classAb, ClassDefinition mainClass);
	
	public void processItem(ClassAbonnee classAb, ClassDefinition classDef, Object item, Integer idChg, LogInsertXML logInsert, List<LogXML> logs);
	
	public boolean insertLog(String fileName, Integer idChg, LogInsertXML log, List<LogXML> logs);
}