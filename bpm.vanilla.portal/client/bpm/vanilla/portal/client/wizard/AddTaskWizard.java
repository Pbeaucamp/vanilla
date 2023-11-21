package bpm.vanilla.portal.client.wizard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.wizard.GwtWizard;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.alerts.ActionMail;
import bpm.vanilla.platform.core.beans.scheduler.Job;
import bpm.vanilla.platform.core.beans.scheduler.JobDetail;
import bpm.vanilla.platform.core.beans.scheduler.JobDetail.Period;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.panels.center.ProcessManagerPanel;
import bpm.vanilla.portal.client.utils.ToolsGWT;

public class AddTaskWizard extends GwtWizard implements IWait {

	private ProcessManagerPanel processManagerPanel;

	private IGwtPage currentPage;

	private AddTaskDefinitionPage definitionPage;
	private AddTaskRepositoryPage repositoryPage;
	private AddTaskParamPage paramPage;
	private AddTaskOptionsPage cronOptionsPage;
	private AddTaskMailPage mailPage;

	private boolean edit = false;
	private Job selectedJob;
	private PortailRepositoryItem item;

	public AddTaskWizard(ProcessManagerPanel processManagerPanel) {
		super(ToolsGWT.lblCnst.AddANewTask());
		this.edit = false;
		this.processManagerPanel = processManagerPanel;

		definitionPage = new AddTaskDefinitionPage(this, 0);
		setCurrentPage(definitionPage);
	}

	public AddTaskWizard(ProcessManagerPanel processManagerPanel, Job selectedJob, PortailRepositoryItem item) {
		super(ToolsGWT.lblCnst.Edit());
		this.edit = true;
		this.processManagerPanel = processManagerPanel;
		this.selectedJob = selectedJob;
		this.item = item;

		int index = 0;
		definitionPage = new AddTaskDefinitionPage(this, index);
		index++;
		definitionPage.setName(selectedJob.getName());
		definitionPage.setDescription(selectedJob.getDescription());
		setCurrentPage(definitionPage);

		int groupId = biPortal.get().getInfoUser().getGroup().getId();

		if (selectedJob.getParameters() != null && !selectedJob.getParameters().isEmpty()) {
			LaunchReportInformations itemInfo = buildItemInfo(groupId, selectedJob.getDetail().getParameters(), selectedJob.getItemId(), isReport());

			paramPage = new AddTaskParamPage(AddTaskWizard.this, index);
			paramPage.setItem(itemInfo);
			index++;
		}
		
		if (item.isReport()) {
			mailPage = new AddTaskMailPage(AddTaskWizard.this, index, selectedJob.getDetail());
			index++;
		}

		cronOptionsPage = new AddTaskOptionsPage(AddTaskWizard.this, index);
		cronOptionsPage.setDetail(selectedJob.getDetail());
	}

	private LaunchReportInformations buildItemInfo(int groupId, List<VanillaGroupParameter> params, int itemId, boolean isReport) {
		RepositoryItem item = new RepositoryItem();
		item.setId(itemId);
		if (isReport) {
			item.setType(IRepositoryApi.CUST_TYPE);
		}
		else {
			item.setType(IRepositoryApi.GTW_TYPE);
		}

		Group selectedGroup = new Group();
		selectedGroup.setId(groupId);

		PortailRepositoryItem portailItem = new PortailRepositoryItem(item, "");

		LaunchReportInformations itemInfo = new LaunchReportInformations();
		itemInfo.setGroupParameters(params);
		itemInfo.setItem(portailItem);
		itemInfo.setSelectedGroup(selectedGroup);
		return itemInfo;
	}

	@Override
	public boolean canFinish() {
		return definitionPage.isComplete() && (repositoryPage != null && repositoryPage.isComplete()) && (cronOptionsPage != null && cronOptionsPage.isComplete());
	}

	@Override
	public void setCurrentPage(IGwtPage page) {
		if (page instanceof AddTaskDefinitionPage)
			setContentPanel((AddTaskDefinitionPage) page);
		else if (page instanceof AddTaskRepositoryPage)
			setContentPanel((AddTaskRepositoryPage) page);
		else if (page instanceof AddTaskParamPage)
			setContentPanel((AddTaskParamPage) page);
		else if (page instanceof AddTaskOptionsPage)
			setContentPanel((AddTaskOptionsPage) page);
		else if (page instanceof AddTaskMailPage)
			setContentPanel((AddTaskMailPage) page);
		currentPage = page;
		updateBtn();
	}

	@Override
	public void updateBtn() {
		setBtnBackState(currentPage.canGoBack() ? true : false);
		setBtnNextState(currentPage.canGoFurther() ? true : false);
		setBtnFinishState(canFinish() ? true : false);
	}

	@Override
	protected void onClickFinish() {
		String name = definitionPage.getName();
		String description = definitionPage.getDescription();

		List<VanillaGroupParameter> params = new ArrayList<VanillaGroupParameter>();
		if (hasParameters()) {
			params = paramPage.getParameters();
		}
		
		ActionMail actionMail = null;
		List<Integer> subscribers = null;
		if (isReport()) {
			actionMail = mailPage.getAction();
			subscribers = mailPage.getSubscribers();
		}

		Date beginDate = cronOptionsPage.getBeginDate();
		Date stopDate = cronOptionsPage.getStopDate();
		Period period = cronOptionsPage.getPeriod();
		int interval = cronOptionsPage.getInterval();

		if (edit) {
			JobDetail jobDetails = selectedJob.getDetail();
			jobDetails.setName(name);
			jobDetails.setDescription(description);

			jobDetails.setBeginDate(beginDate);
			jobDetails.setStopDate(stopDate);
			jobDetails.setPeriod(period);
			jobDetails.setInterval(interval);

			jobDetails.setGroupId(biPortal.get().getInfoUser().getGroup().getId());
			jobDetails.setParameters(params);
			jobDetails.setRepositoryId(biPortal.get().getInfoUser().getRepository().getId());
			jobDetails.setUserId(biPortal.get().getInfoUser().getUser().getId());
			
			jobDetails.setSubject(actionMail != null ? actionMail.getSubject() : null);
			jobDetails.setContent(actionMail != null ? actionMail.getContent() : null);
			jobDetails.setSubscribers(subscribers);

			processManagerPanel.addOrEditJob(selectedJob);
		}
		else {
			int itemId = getSelectedItem().getId();

			String format = "";
			if (isReport()) {
				format = repositoryPage.getFormat();
			}

			JobDetail jobDetails = new JobDetail();
			jobDetails.setName(name);
			jobDetails.setDescription(description);

			jobDetails.setBeginDate(beginDate);
			jobDetails.setStopDate(stopDate);
			jobDetails.setPeriod(period);
			jobDetails.setInterval(interval);

			jobDetails.setFormat(format);
			jobDetails.setGroupId(biPortal.get().getInfoUser().getGroup().getId());
			jobDetails.setItemId(itemId);
			jobDetails.setParameters(params);
			jobDetails.setRepositoryId(biPortal.get().getInfoUser().getRepository().getId());
			jobDetails.setUserId(biPortal.get().getInfoUser().getUser().getId());
			
			jobDetails.setSubject(actionMail != null ? actionMail.getSubject() : null);
			jobDetails.setContent(actionMail != null ? actionMail.getContent() : null);
			jobDetails.setSubscribers(subscribers);

			Job job = new Job();
			job.setDetail(jobDetails);

			processManagerPanel.addOrEditJob(job);
		}
		AddTaskWizard.this.hide();
	}

	@Override
	protected void onBackClick() {

		if (currentPage instanceof AddTaskRepositoryPage) {
			setCurrentPage(definitionPage);
		}
		else if (currentPage instanceof AddTaskParamPage) {
			if (edit) {
				setCurrentPage(definitionPage);
			}
			else {
				setCurrentPage(repositoryPage);
			}
		}
		else if (currentPage instanceof AddTaskMailPage) {
			if (hasParameters()) {
				setCurrentPage(paramPage);
			}
			else {
				if (edit) {
					setCurrentPage(definitionPage);
				}
				else {
					setCurrentPage(repositoryPage);
				}
			}
		}
		else if (currentPage instanceof AddTaskOptionsPage) {
			if (isReport()) {
				setCurrentPage(mailPage);
			}
			else if (hasParameters()) {
				setCurrentPage(paramPage);
			}
			else {
				if (edit) {
					setCurrentPage(definitionPage);
				}
				else {
					setCurrentPage(repositoryPage);
				}
			}
		}
	}

	@Override
	protected void onNextClick() {
		if (currentPage instanceof AddTaskDefinitionPage) {
			if (edit) {
				if (repositoryPage == null) {
					repositoryPage = new AddTaskRepositoryPage(this, 1, item);
				}
				setCurrentPage(repositoryPage);
				// if(paramPage != null) {
				// setCurrentPage(paramPage);
				// }
				// else {
				// setCurrentPage(cronOptionsPage);
				// }
			}
			else {
				if (repositoryPage == null) {
					repositoryPage = new AddTaskRepositoryPage(this, 1);
				}
				setCurrentPage(repositoryPage);
			}
		}
		else if (currentPage instanceof AddTaskRepositoryPage) {
			showWaitPart(true);

			RepositoryItem item = getSelectedItem() != null ? getSelectedItem() : null;
			if (item != null) {
				final Group selectedGroup = biPortal.get().getInfoUser().getGroup();

				PortailRepositoryItem portailItem = new PortailRepositoryItem(item, "");

				final LaunchReportInformations itemInfo = new LaunchReportInformations();
				itemInfo.setItem(portailItem);
				itemInfo.setSelectedGroup(selectedGroup);

				ReportingService.Connect.getInstance().getParameters(itemInfo, new AsyncCallback<List<VanillaGroupParameter>>() {
					@Override
					public void onFailure(Throwable caught) {
						showWaitPart(false);

						caught.printStackTrace();

						ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());

						if (isReport()) {
							if (mailPage == null) {
								mailPage = new AddTaskMailPage(AddTaskWizard.this, 2, null);
							}
							setCurrentPage(mailPage);
						}
						else {
							if (cronOptionsPage == null) {
								cronOptionsPage = new AddTaskOptionsPage(AddTaskWizard.this, 2);
							}
							setCurrentPage(cronOptionsPage);
						}
					}

					@Override
					public void onSuccess(List<VanillaGroupParameter> groupParams) {
						showWaitPart(false);

						itemInfo.setGroupParameters(groupParams);

						if (groupParams != null && !groupParams.isEmpty()) {
							if (paramPage == null) {
								paramPage = new AddTaskParamPage(AddTaskWizard.this, 2);
							}
							paramPage.setItem(itemInfo);
							
							if (paramPage.hasParameters()) {
								setCurrentPage(paramPage);
							}
							else {
								if (isReport()) {
									if (mailPage == null) {
										mailPage = new AddTaskMailPage(AddTaskWizard.this, 2, null);
									}
									setCurrentPage(mailPage);
								}
								else {
									if (cronOptionsPage == null) {
										cronOptionsPage = new AddTaskOptionsPage(AddTaskWizard.this, 2);
									}
									setCurrentPage(cronOptionsPage);
								}
							}
						}
						else {
							if (isReport()) {
								if (mailPage == null) {
									mailPage = new AddTaskMailPage(AddTaskWizard.this, 2, null);
								}
								setCurrentPage(mailPage);
							}
							else {
								if (cronOptionsPage == null) {
									cronOptionsPage = new AddTaskOptionsPage(AddTaskWizard.this, 2);
								}
								setCurrentPage(cronOptionsPage);
							}
						}
					}
				});
			}
		}
		else if (currentPage instanceof AddTaskParamPage) {
			if (isReport()) {
				if (mailPage == null) {
					mailPage = new AddTaskMailPage(this, 3, null);
				}
			}
			else {
				if (cronOptionsPage == null) {
					cronOptionsPage = new AddTaskOptionsPage(this, 3);
				}
				setCurrentPage(cronOptionsPage);
			}
		}
		else if (currentPage instanceof AddTaskMailPage) {
			if (cronOptionsPage == null) {
				cronOptionsPage = new AddTaskOptionsPage(this, 4);
			}
			setCurrentPage(cronOptionsPage);
		}
	}

	public RepositoryItem getSelectedItem() {
		if (repositoryPage != null) {
			return repositoryPage.getSelectedItem();
		}
		else {
			return null;
		}
	}

	public boolean isReport() {
		if (repositoryPage != null) {
			if (repositoryPage.getSelectedItem() != null && repositoryPage.getSelectedItem().isReport()) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	public boolean hasParameters() {
		return paramPage != null && paramPage.hasParameters();
	}

}
