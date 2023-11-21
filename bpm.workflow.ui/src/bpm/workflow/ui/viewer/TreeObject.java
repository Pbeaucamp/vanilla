package bpm.workflow.ui.viewer;


public abstract class TreeObject {
		private String name;
		private TreeParent parent;

		
		public TreeObject(String name) {
			setName(name);
		}
		public String getName() {
			return name;
		}
		public void setParent(TreeParent parent) {
			this.parent = parent;
		}
		public TreeParent getParent() {
			return parent;
		}
		
		@Override
		public abstract String toString(); 
		
		public void setName (String name) {
			this.name = name;
		}

		
}
