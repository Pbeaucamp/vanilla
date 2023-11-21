package bpm.united.olap.runtime.engine;

import bpm.united.olap.api.result.OlapResult;
import bpm.united.olap.api.runtime.IRuntimeContext;


public interface IOlapRunner {
	
	 /**
	  * execute an mdx query in 2 different ways :
	  * 	- if computeDatas is false : only the result structure will be sent with empty Values
	  * 	- the query will be run on the factTable and the grid filled ith values
	  * @param mdx
	  * @param computeDatas
	  * @return a result item
	  * @throws Exception
	  */
	 OlapResult executeQuery(String mdx, boolean computeDatas) throws Exception;
	 
	 /**
	  * If Sql queries need to be in streamMode
	  * @param isInStreamMode
	  */
	 void setSqlStreamMode(boolean isInStreamMode);
}
