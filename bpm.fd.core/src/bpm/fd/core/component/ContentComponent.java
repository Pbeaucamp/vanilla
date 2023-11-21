package bpm.fd.core.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.fd.core.DashboardComponent;
import bpm.fd.core.IComponentOption;

public abstract class ContentComponent extends DashboardComponent implements IComponentOption {

	public enum TabDisplay {
		BOTTOM(0), LEFT(1), RIGHT(2), TOP(3);

		private int type;

		private static Map<Integer, TabDisplay> map = new HashMap<Integer, TabDisplay>();
		static {
			for (TabDisplay serverType : TabDisplay.values()) {
				map.put(serverType.getType(), serverType);
			}
		}

		private TabDisplay(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static TabDisplay valueOf(int type) {
			return map.get(type);
		}
	}
	
	private static final long serialVersionUID = 1L;
	
	private TabDisplay tabDisplay = TabDisplay.TOP;
	private List<ContainerComponent> containers;
	
	public void setTabDisplay(TabDisplay tabDisplay) {
		this.tabDisplay = tabDisplay;
	}
	
	public TabDisplay getTabDisplay() {
		return tabDisplay;
	}
	
	public void setContainers(List<ContainerComponent> containers) {
		this.containers = containers;
	}
	
	public List<ContainerComponent> getContainers() {
		return containers;
	}
}
