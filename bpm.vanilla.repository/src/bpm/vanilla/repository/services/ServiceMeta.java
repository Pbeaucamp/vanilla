package bpm.vanilla.repository.services;

import java.util.List;

import bpm.vanilla.platform.core.ManageAction;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.meta.Meta;
import bpm.vanilla.platform.core.beans.meta.MetaForm;
import bpm.vanilla.platform.core.beans.meta.MetaLink;
import bpm.vanilla.platform.core.beans.meta.MetaLink.TypeMetaLink;
import bpm.vanilla.platform.core.beans.meta.MetaValue;
import bpm.vanilla.platform.core.repository.services.IMetaService;
import bpm.vanilla.repository.beans.RepositoryRuntimeComponent;

public class ServiceMeta implements IMetaService {
	
	private RepositoryRuntimeComponent component;
	private int groupId;
	private int repositoryId;
	private User user;
	private String clientIp;
	
	public ServiceMeta(RepositoryRuntimeComponent repositoryRuntimeComponent, int groupId, int repositoryId, User user, String clientIp) {
		this.component = repositoryRuntimeComponent;
		this.groupId = groupId;
		this.repositoryId = repositoryId;
		this.user = user;
		this.clientIp = clientIp;
	}

	@Override
	public List<Meta> getMetaByForm(int formId) throws Exception {
		return component.getRepositoryDao(repositoryId).getMetaDAO().getMetaByForm(formId);
	}

	@Override
	public List<MetaLink> getMetaLinks(int itemId, TypeMetaLink type, boolean loadResponse) throws Exception {
		return component.getRepositoryDao(repositoryId).getMetaDAO().getMetaLinks(itemId, type, loadResponse);
	}

	@Override
	public void manageMetaValues(List<MetaLink> values, ManageAction action) throws Exception {
		component.getRepositoryDao(repositoryId).getMetaDAO().manageItems(values, action);
	}

	@Override
	public List<Integer> getItemsByMeta(TypeMetaLink type, List<MetaValue> values) throws Exception {
		return component.getRepositoryDao(repositoryId).getMetaDAO().getItemsByMeta(type, values);
	}

	@Override
	public List<MetaForm> getMetaForms() throws Exception {
		return component.getRepositoryDao(repositoryId).getMetaDAO().getMetaForms();
	}

	@Override
	public Meta getMeta(String key) throws Exception {
		return component.getRepositoryDao(repositoryId).getMetaDAO().getMeta(key);
	}
	
	@Override
	public Meta manageMeta(Meta meta, ManageAction action) throws Exception {
		return (Meta) component.getRepositoryDao(repositoryId).getMetaDAO().manageItem(meta, action);
	}
}
