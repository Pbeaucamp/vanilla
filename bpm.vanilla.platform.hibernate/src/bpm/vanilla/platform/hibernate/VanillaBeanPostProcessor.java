package bpm.vanilla.platform.hibernate;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;

public class VanillaBeanPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
		FileSystemResource resource = new FileSystemResource(System.getProperty("bpm.vanilla.configurationFile"));
		cfg.setLocation(resource);

		cfg.postProcessBeanFactory(beanFactory);

	}

}
