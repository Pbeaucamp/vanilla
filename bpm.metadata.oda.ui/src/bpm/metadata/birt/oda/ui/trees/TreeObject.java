package bpm.metadata.birt.oda.ui.trees;


public class TreeObject {
		private String name;
		private TreeParent parent;
		;
		
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
		public String toString() {
			return getName();
		}
		public void setName (String name) {
			this.name = name;
		}
}
