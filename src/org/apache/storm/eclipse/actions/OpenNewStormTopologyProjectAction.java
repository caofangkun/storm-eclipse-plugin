package org.apache.storm.eclipse.actions;

import org.apache.storm.eclipse.NewStormTopoloyProjectWizard;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class OpenNewStormTopologyProjectAction extends Action {
	
	  @Override
	  public void run() {
	    IWorkbench workbench = PlatformUI.getWorkbench();
	    Shell shell = workbench.getActiveWorkbenchWindow().getShell();
	    NewStormTopoloyProjectWizard wizard = new NewStormTopoloyProjectWizard();
	    wizard.init(workbench, new StructuredSelection());
	    WizardDialog dialog = new WizardDialog(shell, wizard);
	    dialog.create();
	    dialog.open();
	    // did the wizard succeed?
	    notifyResult(dialog.getReturnCode() == Window.OK);
	  }
}
