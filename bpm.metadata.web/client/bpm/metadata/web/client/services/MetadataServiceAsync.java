package bpm.metadata.web.client.services;

import bpm.gwt.commons.shared.fmdt.metadata.Metadata;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataData;
import bpm.vanilla.platform.core.beans.data.DatabaseColumn;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MetadataServiceAsync {

	public void initSession(AsyncCallback<Void> callback);

	public void save(RepositoryDirectory target, Metadata metadata, boolean update, AsyncCallback<Integer> callback);

}
