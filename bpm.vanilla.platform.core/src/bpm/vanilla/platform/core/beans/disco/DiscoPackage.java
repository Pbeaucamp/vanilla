package bpm.vanilla.platform.core.beans.disco;

import java.util.ArrayList;
import java.util.List;

public class DiscoPackage {

	private String name;
	private String description;
	private List<DiscoReportConfiguration> configs;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<DiscoReportConfiguration> getConfigs() {
		return configs;
	}

	public void setConfigs(List<DiscoReportConfiguration> configs) {
		this.configs = configs;
	}
	
	public void addConfig(DiscoReportConfiguration config){
		if(configs == null){
			this.configs = new ArrayList<DiscoReportConfiguration>();
		}
		this.configs.add(config);
	}
}
