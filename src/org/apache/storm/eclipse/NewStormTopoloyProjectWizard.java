package org.apache.storm.eclipse;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPage;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

public class NewStormTopoloyProjectWizard extends Wizard implements
		IWorkbenchWizard, IExecutableExtension {
	static Logger log = Logger.getLogger(NewStormTopoloyProjectWizard.class
			.getName());
	private StormFirstPage firstPage;

	private NewJavaProjectWizardPage javaPage;

	private IConfigurationElement config;

	public NewStormTopoloyProjectWizard() {
		setWindowTitle("New Storm Topology Project Wizard");
	}
	
	  @Override
	  public void addPages() {

	    firstPage = new StormFirstPage();
	    javaPage =
	        new NewJavaProjectWizardPage(ResourcesPlugin.getWorkspace()
	            .getRoot(), firstPage);
	    addPage(firstPage);
	    addPage(javaPage);
	  }

	@Override
	public boolean performFinish() {
		try {
			PlatformUI.getWorkbench().getProgressService()
					.runInUI(this.getContainer(), new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor) {
							try {
								monitor.beginTask("Create Hadoop Project", 300);

								javaPage.getRunnable().run(
										new SubProgressMonitor(monitor, 100));

								// if( firstPage.generateDriver.getSelection())
								// {
								// newDriverPage.setPackageFragmentRoot(javaPage.getNewJavaProject().getAllPackageFragmentRoots()[0],
								// false);
								// newDriverPage.getRunnable().run(new
								// SubProgressMonitor(monitor,100));
								// }

								IProject project = javaPage.getNewJavaProject()
										.getResource().getProject();
								IProjectDescription description = project
										.getDescription();
								String[] existingNatures = description
										.getNatureIds();
								String[] natures = new String[existingNatures.length + 1];
								for (int i = 0; i < existingNatures.length; i++) {
									natures[i + 1] = existingNatures[i];
								}

								natures[0] = StormTopologyNature.ID;
								description.setNatureIds(natures);

								project.setPersistentProperty(
										new QualifiedName(Activator.PLUGIN_ID,
												"hadoop.runtime.path"),
										firstPage.currentPath);
								project.setDescription(description,
										new NullProgressMonitor());

								String[] natureIds = project.getDescription()
										.getNatureIds();
								for (int i = 0; i < natureIds.length; i++) {
									log.fine("Nature id # " + i + " > "
											+ natureIds[i]);
								}

								monitor.worked(100);
								monitor.done();

								BasicNewProjectResourceWizard
										.updatePerspective(config);
							} catch (CoreException e) {
								// TODO Auto-generated catch block
								log.log(Level.SEVERE, "CoreException thrown.",
										e);
							} catch (InvocationTargetException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}, null);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		this.config = config;
	}

	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canFinish() {
		return firstPage.isPageComplete() && javaPage.isPageComplete();
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage answer = super.getNextPage(page);
		if (answer == javaPage) {
			return answer;
		} else {
			return answer;
		}
	}

	@Override
	public IWizardPage getPreviousPage(IWizardPage page) {

		return super.getPreviousPage(page);
	}

}
