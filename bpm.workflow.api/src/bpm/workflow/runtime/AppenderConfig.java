package bpm.workflow.runtime;

/**
 * Appender
 * @author Ludovic CAMUS
 *
 */
public class AppenderConfig {
	private int id;
	private Integer dirItId;
	private String repositoryUrl;
	
	private String activityName;
	private String activityClassName;
	
	public AppenderConfig(int id, Integer dirItId, String repositoryUrl,
			String activityName, String activityClassName) {
		super();
		this.id = id;
		this.dirItId = dirItId;
		this.repositoryUrl = repositoryUrl;
		this.activityName = activityName;
		this.activityClassName = activityClassName;
	}

	/**
	 * @return the activityClassName
	 */
	public final String getActivityClassName() {
		return activityClassName;
	}

	/**
	 * @return the id
	 */
	public final int getId() {
		return id;
	}

	/**
	 * @return the dirItId
	 */
	public final Integer getDirItId() {
		return dirItId;
	}

	/**
	 * @return the repositoryUrl
	 */
	public final String getRepositoryUrl() {
		return repositoryUrl;
	}

	
	/**
	 * @return the activityName
	 */
	public final String getActivityName() {
		return activityName;
	}
	
	
	
	
}
