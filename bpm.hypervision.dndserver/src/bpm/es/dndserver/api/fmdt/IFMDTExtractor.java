package bpm.es.dndserver.api.fmdt;

import java.util.List;

import bpm.es.dndserver.api.Message;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public interface IFMDTExtractor {
	public List<FMDTDataSource> extractFmdtDataSources(RepositoryItem item, IRepositoryApi sock) throws Exception ;
	
	public List<Message> getMessages();
}
