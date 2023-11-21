package bpm.smart.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "r_script_model")
public class RScriptModel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column (name = "idScript")
	private int idScript;
	
	@Column (name = "numVersion")
	private int numVersion;
	
	@Column (name = "dateVersion")
	private Date dateVersion;
	
	@Column (name = "inputs")
	private String[] inputs;
	
	@Column (name = "outputs")
	private String[] outputs;
	
	@Column (name = "script", length = 10000000)
	private String script;

	@Transient
	private String outputLog;
	
	@Transient
	private String[] outputFiles;
	
	@Transient
	private transient List<Serializable> outputVars;
	
	@Transient
	private Map<String, Serializable> inputVars;
	
	@Transient
	private List<String> outputVarstoString;
	
	@Transient
	private String userREnv;
	
	@Transient
	private boolean scriptError;
	
	public RScriptModel() {
		super();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String[] getInputs() {
		return inputs;
	}

	public void setInputs(String[] inputs) {
		this.inputs = inputs;
	}

	public String[] getOutputs() {
		return outputs;
	}

	public void setOutputs(String[] outputs) {
		this.outputs = outputs;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getOutputLog() {
		return outputLog;
	}

	public void setOutputLog(String outputLog) {
		this.outputLog = outputLog;
	}

	public List<Serializable> getOutputVars() {
		return outputVars;
	}

	public void setOutputVars(List<Serializable> outputVars) {
		this.outputVars = outputVars;
	}

	public List<String> getOutputVarstoString() {
		return outputVarstoString;
	}

	public void setOutputVarstoString(List<String> outputVarstoString) {
		this.outputVarstoString = outputVarstoString;
	}

	public int getIdScript() {
		return idScript;
	}

	public void setIdScript(int idScript) {
		this.idScript = idScript;
	}

	public int getNumVersion() {
		return numVersion;
	}

	public void setNumVersion(int numVersion) {
		this.numVersion = numVersion;
	}

	public Date getDateVersion() {
		return dateVersion;
	}

	public void setDateVersion(Date dateVersion) {
		this.dateVersion = dateVersion;
	}

	public String[] getOutputFiles() {
		return outputFiles;
	}

	public void setOutputFiles(String[] outputFiles) {
		this.outputFiles = outputFiles;
	}

	public String getUserREnv() {
		return userREnv;
	}

	public void setUserREnv(String userREnv) {
		this.userREnv = userREnv;
	}

	public boolean isScriptError() {
		return scriptError;
	}

	public void setScriptError(boolean scriptError) {
		this.scriptError = scriptError;
	}

	public Map<String, Serializable> getInputVars() {
		return inputVars;
	}

	public void setInputVars(Map<String, Serializable> inputVars) {
		this.inputVars = inputVars;
	}
	
}
