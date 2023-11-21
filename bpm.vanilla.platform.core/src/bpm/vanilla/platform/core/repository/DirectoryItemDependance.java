package bpm.vanilla.platform.core.repository;

import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "rpy_dependancies")
public class DirectoryItemDependance {
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column(name = "item_id")
	private Integer dirItemId;
	
	@Column(name = "dependant_item_id")
	private Integer dependantDirItemId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setId(String id) {
		this.id = new Integer(id);
	}
	public Integer getDirItemId() {
		return dirItemId;
	}
	public void setDirItemId(Integer dirItemId) {
		this.dirItemId = dirItemId;
	}
	public void setDirItemId(String dirItemId) {
		this.dirItemId = new Integer(dirItemId);
	}
	public Integer getDependantDirItemId() {
		return dependantDirItemId;
	}
	public void setDependantDirItemId(Integer dependantDirItemId) {
		this.dependantDirItemId = dependantDirItemId;
	}
	public void setDependantDirItemId(String dependantDirItemId) {
		this.dependantDirItemId = new Integer(dependantDirItemId);
	}
	
	public String getXml() {
		StringBuffer buf = new StringBuffer();
		buf.append("<directoryitemdependance>");
		buf.append("<id>" + id + "</id>");
		buf.append("<diritemid>" + dirItemId + "</diritemid>");
		buf.append("<dependantdiritemid>" + dependantDirItemId + "</dependantdiritemid>");
		buf.append("</directoryitemdependance>");
		return buf.toString();
	}
	
	public static void main(String[] args) {
		Field[] fields = DirectoryItemDependance.class.getDeclaredFields();
		StringBuffer buf = new StringBuffer();
		buf.append("StringBuffer buf = new StringBuffer();\n");
		buf.append("buf.append(\"<" + DirectoryItemDependance.class.getSimpleName().toLowerCase() + ">\");\n");
		for (Field f : fields) {
			String n = f.getName();
			String nl = f.getName().toLowerCase();
			buf.append("buf.append(\"<" + nl + ">\" + " + n + " + \"</" + nl + ">\");\n");
		}
		buf.append("buf.append(\"</" + DirectoryItemDependance.class.getSimpleName().toLowerCase() + ">\");\n");
		buf.append("return buf.toString();");
		
		System.out.println(buf.toString());
		try {
			generateDigester(new DirectoryItemDependance());
		} catch (SecurityException e) {
			
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			
			e.printStackTrace();
		}
	}
	
	
	public static void generateDigester(Object o) throws SecurityException, NoSuchMethodException {
		StringBuffer buf = new StringBuffer();
		String n = o.getClass().getSimpleName();
		String nl = n.toLowerCase();
		
		buf.append("private " + n + " o;\n");

		buf.append("@SuppressWarnings(value = \"unchecked\")\n");
		buf.append("public " + n + "Digester(InputStream is) {\n");
		buf.append("	Digester dig = new Digester();\n");
		buf.append("	dig.setValidating(false);\n");
		buf.append("	dig.setErrorHandler(new ErrorHandler());\n");
		buf.append("\n");
		buf.append("	String root = \"" + nl + "\";\n");

		buf.append("	dig.addObjectCreate(root , " + n +".class);\n");

		Field[] fields = o.getClass().getDeclaredFields();
		for (Field f : fields) {
			String _n = f.getName();
			String _nl = f.getName().toLowerCase();
			String s = _n.substring(0, 1);
			_n = _n.replaceFirst(s, s.toUpperCase());
			String setter = o.getClass().getDeclaredMethod("set" + _n , String.class).getName();
			buf.append("		dig.addCallMethod(root + \"/" + _nl + "\", \"" + setter + "\", 0);\n");
		}
			
		buf.append("	try {\n");
		buf.append("		o = (" + n + ") dig.parse(is);\n");
		buf.append("\n");
		buf.append("	} catch (Exception e) {\n");
		buf.append("		e.printStackTrace();\n");
		buf.append("	}\n");
		buf.append("}\n");
		buf.append("\n");
		buf.append("public " + n + " get" + n + "() {\n");
		buf.append("	return o;\n");
		buf.append("}\n");

		buf.append("public class ErrorHandler implements org.xml.sax.ErrorHandler {\n");
		buf.append("	public void warning(SAXParseException ex) throws SAXParseException {\n");
		buf.append("		throw ex;\n");
		buf.append("	}\n");

		buf.append("	public void error(SAXParseException ex) throws SAXParseException {\n");
		buf.append("		throw ex;\n");
		buf.append("	}\n");

		buf.append("	public void fatalError(SAXParseException ex) throws SAXParseException {\n");
		buf.append("		throw ex;\n");
		buf.append("	}\n");
		buf.append("}\n");
	}
	
}
