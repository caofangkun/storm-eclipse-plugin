package org.apache.storm.eclipse.launch;

import java.util.ArrayList;
import java.util.List;

import org.apache.storm.eclipse.servers.RunOnStormWizard;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaApplicationLaunchShortcut;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class StormApplicationLaunchShortcut extends
		JavaApplicationLaunchShortcut {
	
	public StormApplicationLaunchShortcut() {
		
	}
	
	@Override
	  protected ILaunchConfiguration findLaunchConfiguration(IType type,
	      ILaunchConfigurationType configType) {

	    // Find an existing or create a launch configuration (Standard way)
	    ILaunchConfiguration iConf =
	        super.findLaunchConfiguration(type, configType);
	    if (iConf == null) iConf = super.createConfiguration(type);
	    ILaunchConfigurationWorkingCopy iConfWC;
	    try {
	      /*
	       * Tune the default launch configuration: setup run-time classpath
	       * manually
	       */
	      iConfWC = iConf.getWorkingCopy();

	      iConfWC.setAttribute(
	          IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);

	      List<String> classPath = new ArrayList<String>();
	      IResource resource = type.getResource();
	      IJavaProject project =
	          (IJavaProject) resource.getProject().getNature(JavaCore.NATURE_ID);
	      IRuntimeClasspathEntry cpEntry =
	          JavaRuntime.newDefaultProjectClasspathEntry(project);
	      classPath.add(0, cpEntry.getMemento());

	      iConfWC.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH,
	          classPath);

	    } catch (CoreException e) {
	      e.printStackTrace();
	      // FIXME Error dialog
	      return null;
	    }
	    
	    /*
	     * Update the selected configuration with a specific Hadoop location
	     * target
	     */
	    IResource resource = type.getResource();
	    if (!(resource instanceof IFile))
	      return null;
	    RunOnStormWizard wizard =
	        new RunOnStormWizard((IFile) resource, iConfWC);
	    WizardDialog dialog =
	        new WizardDialog(Display.getDefault().getActiveShell(), wizard);

	    dialog.create();
	    dialog.setBlockOnOpen(true);
	    if (dialog.open() != WizardDialog.OK)
	      return null;

	    try {
	      iConfWC.doSave();

	    } catch (CoreException e) {
	      e.printStackTrace();
	      // FIXME Error dialog
	      return null;
	    }

	    return iConfWC;
	  }
	
	  static class Dialog extends WizardDialog {
		    public Dialog(Shell parentShell, IWizard newWizard) {
		      super(parentShell, newWizard);
		    }

		    @Override
		    public void create() {
		      super.create();

		      ((RunOnStormWizard) getWizard())
		          .setProgressMonitor(getProgressMonitor());
		    }
		  }


}
