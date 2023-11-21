package bpm.metadata.query;

import java.util.List;

import bpm.vanilla.platform.core.IVanillaContext;

public interface IQueryExecutor {

	public List<List<Object>> execute(IVanillaContext context, IQuery query, String connectionName, List<List<String>> promptValues) throws Exception;
}
