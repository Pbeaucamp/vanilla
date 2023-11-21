package bpm.vanilla.repository.beans.model;

import java.util.List;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.VanillaImage;
import bpm.vanilla.platform.core.repository.Template;
import bpm.vanilla.platform.core.repository.Template.TypeTemplate;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

import com.thoughtworks.xstream.XStream;

public class TemplateDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public <T> List<Template<T>> getTemplates(IVanillaAPI vanillaAPI, boolean lightWeight, TypeTemplate type) {
		List<Template<T>> templates = getHibernateTemplate().find("FROM Template WHERE type=" + type.ordinal());
		for(Template<T> template : templates) {
			loadTemplate(vanillaAPI, template, lightWeight);
		}
		return templates;
	}
	
	@SuppressWarnings("unchecked")
	public <T> Template<T> getTemplate(IVanillaAPI vanillaAPI, int templateId) {
		List<Template<T>> templates = getHibernateTemplate().find("FROM Template WHERE id = " + templateId);
		if (templates == null || templates.isEmpty()) {
			return null;
		}
		
		Template<T> template = templates.get(0);
		loadTemplate(vanillaAPI, template, false);
		return template;
	}
	
	public <T> void saveTemplate(Template<T> template) {
		buildTemplate(template);
		
		template.setId((Integer)getHibernateTemplate().save(template));
	}

	public <T> void delete(Template<T> template) {
		getHibernateTemplate().delete(template);
	}

	@SuppressWarnings("unchecked")
	private <T> void loadTemplate(IVanillaAPI vanillaAPI, Template<T> template, boolean lightWeight) {
		if (!lightWeight && template.getModel() != null && !template.getModel().isEmpty()) {
			template.setItem((T) new XStream().fromXML(template.getModel()));
		}
		
		Integer imageId = template.getImageId();
		if (imageId != null && imageId > 0) {
			try {
				VanillaImage image = vanillaAPI.getImageManager().getImage(template.getImageId());
				template.setImage(image);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private <T> void buildTemplate(Template<T> template) {
		if (template.getItem() != null) {
			template.setModel(new XStream().toXML(template.getItem()));
		}
	}
	 
}
