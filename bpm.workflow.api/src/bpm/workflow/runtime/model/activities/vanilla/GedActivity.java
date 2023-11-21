package bpm.workflow.runtime.model.activities.vanilla;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Element;

import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;
import bpm.mdm.remote.MdmRemote;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.beans.ged.ComProperties;
import bpm.vanilla.platform.core.beans.ged.Definition;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.ged.IComProperties;
import bpm.vanilla.platform.core.beans.ged.constant.RuntimeFields;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.ged.GedIndexRuntimeConfig;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IAcceptInput;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IComment;

public class GedActivity extends AbstractActivity implements IComment, IAcceptInput {

	private static int number = 0;

	private String comment;
	private String repositoryId;

	private List<String> filesToTreat = new ArrayList<String>();
	private HashMap<String, Integer> itemsIds = new HashMap<String, Integer>();

	private boolean addToMDM;
	private Integer supplierId;
	private Integer contractId;

	/**
	 * Create an interface activity with the specified name
	 * 
	 * @param name
	 */
	public GedActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
	}

	public GedActivity() {
		super();
		number++;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String text) {
		this.comment = text;
	}

	public IActivity copy() {
		return null;
	}

	public void decreaseNumber() {
		number--;
	}

	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("gedactivity");
		if (comment != null) {
			e.addElement("comment").setText(comment);
		}
		if (!this.filesToTreat.isEmpty()) {
			Element files = e.addElement("filestotreat");
			for (String p : this.filesToTreat) {
				files.addElement("path").setText(p);
			}

		}

		if (!itemsIds.isEmpty()) {
			Element ids = e.addElement("itemsids");
			for (String key : itemsIds.keySet()) {
				Element i = ids.addElement("item");
				i.addElement("itemid").setText(itemsIds.get(key) + "");
				i.addElement("activityoutput").setText(key);
			}
		}

		e.addElement("addToMDM").setText(String.valueOf(addToMDM));

		if (supplierId != null) {
			e.addElement("supplierId").setText(String.valueOf(supplierId));
		}

		if (contractId != null) {
			e.addElement("contractId").setText(String.valueOf(contractId));
		}

		if (repositoryId != null) {
			e.addElement("repositoryid").setText(repositoryId);
		}

		return e;
	}

	public String getProblems() {
		return "";
	}

	public void addInput(String input) {
		this.filesToTreat.add(input);
	}

	public List<String> getInputs() {
		return this.filesToTreat;
	}

	public void removeInput(String input) {
		this.filesToTreat.remove(input);
		for (String key : itemsIds.keySet()) {
			if (key.equals(input)) {
				itemsIds.remove(key);
				break;
			}
		}
	}

	public void setInputs(List<String> inputs) {
		this.filesToTreat = inputs;
	}

	public void addItemIdForFile(Integer itemId, String ra) {
		itemsIds.put(ra, itemId);
	}

	public void addItemIdForFile(String itemId, String ra) {
		itemsIds.put(ra, new Integer(itemId));
	}

	public String getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(String repositoryId) {
		this.repositoryId = repositoryId;
	}

	public String getPhrase() {
		return "Select the activities which provide files";
	}

	@Override
	public void execute() throws Exception {
		if (super.checkPreviousActivities()) {

			IVanillaContext ctx = workflowInstance.getRepositoryApi().getContext().getVanillaContext();

			IGedComponent remote = new RemoteGedComponent(ctx);
			int userId = workflowInstance.getVanillaApi().getVanillaSecurityManager().getUserByLogin(ctx.getLogin()).getId();

			List<Integer> listGroupId = new ArrayList<Integer>();
			listGroupId.add(workflowInstance.getRepositoryApi().getContext().getGroup().getId());

			List<Group> listGroup = new ArrayList<Group>();
			listGroup.add(workflowInstance.getRepositoryApi().getContext().getGroup());

			try {
				if (addToMDM) {
					Contract selectedContract = null;

					IMdmProvider provider = new MdmRemote(ctx.getLogin(), ctx.getPassword(), ctx.getVanillaUrl(), null, null);
					List<Supplier> suppliers = provider.getSuppliers();
					if (suppliers != null) {
						for (Supplier sup : suppliers) {
							if (sup.getId().equals(supplierId) && sup.getContracts() != null) {
								for (Contract cont : sup.getContracts()) {
									if (cont.getId().equals(contractId)) {
										selectedContract = cont;
										break;
									}
								}
								break;
							}
						}
					}
					else {
						throw new Exception("There is no supplier in Vanilla Architect.");
					}

					if (selectedContract == null) {
						throw new Exception("The selected contract could not be found.");
					}
					else if (selectedContract.getFileVersions() == null && selectedContract.getDocId() != null) {
						GedDocument doc = remote.getDocumentDefinitionById(selectedContract.getDocId());
						selectedContract.setFileVersions(doc);
					}

					for (String path : filesToTreat) {
						path = workflowInstance.parseString(path);

						String format = path.substring(path.lastIndexOf(".") + 1, path.length());
						String name = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));

						if (selectedContract.getFileVersions() != null) {
							try {
								DocumentVersion vers = remote.addVersionToDocument(selectedContract.getFileVersions(), format, new FileInputStream(new File(path)));
								selectedContract.getFileVersions().addDocumentVersion(vers);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						else {
							GedDocument doc = new GedDocument();

							doc.setDirectoryId(0);
							doc.setName(name);
							doc.setCreationDate(new Date());
							doc.setCreatedBy(userId);
							doc.setMdmAttached(true);

							ComProperties com = new ComProperties();
							com.setSimpleProperty(RuntimeFields.TITLE.getName(), name);

							GedIndexRuntimeConfig config = new GedIndexRuntimeConfig(com, userId, -1, listGroupId, -1, format, null, -1);
							config.setMdmAttached(true);

							int id = remote.index(config, new FileInputStream(new File(path)));
							for (Integer groupId : listGroupId) {
								remote.addAccess(id, groupId, workflowInstance.getRepositoryApi().getContext().getRepository().getId());
							}

							doc = remote.getDocumentDefinitionById(id);
							selectedContract.setFileVersions(doc);
						}
					}

					provider.saveSuppliers(suppliers);
				}
				else {
					for (String path : filesToTreat) {

						path = workflowInstance.parseString(path);

						String format = path.substring(path.lastIndexOf(".") + 1, path.length());
						String name = path.substring(path.lastIndexOf(File.separator) + 1, path.lastIndexOf("."));
						if (name.contains("/")) {
							name = path.substring(name.lastIndexOf("/") + 1, path.length());
						}

						IComProperties comProps = new ComProperties();

						List<Definition> fields = remote.getFieldDefinitions(false);
						for (Definition f : fields) {
							if (f.getName().equals("category")) {
								comProps.setProperty(f, "0");
							}
							else if (f.getName().equals("title")) {
								comProps.setProperty(f, name);
							}
							else if (f.getName().equals("author")) {
								comProps.setProperty(f, workflowInstance.getRepositoryApi().getContext().getVanillaContext().getLogin());
							}
							else if (f.getName().equals("version")) {
								comProps.setProperty(f, "1");
							}
						}

						GedIndexRuntimeConfig config = new GedIndexRuntimeConfig(comProps, userId, workflowInstance.getRepositoryApi().getContext().getGroup().getId(), listGroupId, workflowInstance.getRepositoryApi().getContext().getRepository().getId(), format, null, 0);
						remote.index(config, new FileInputStream(new File(path)));
					}
				}
				activityResult = true;
			} catch (Exception e) {
				activityResult = false;
				activityState = ActivityState.FAILED;
				failureCause = e.getMessage();
				e.printStackTrace();
			}

			super.finishActivity();
		}
	}

	public boolean addToMDM() {
		return addToMDM;
	}

	public void setAddToMDM(boolean addToMDM) {
		this.addToMDM = addToMDM;
	}

	public void setAddToMDM(String addToMDM) {
		this.addToMDM = Boolean.parseBoolean(addToMDM);
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public Integer getContractId() {
		return contractId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = Integer.parseInt(supplierId);
	}

	public void setContractId(String contractId) {
		this.contractId = Integer.parseInt(contractId);
	}
}
