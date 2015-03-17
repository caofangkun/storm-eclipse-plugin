package org.apache.storm.eclipse.actions;

import java.util.logging.Logger;

import org.apache.storm.eclipse.NewBoltWizard;
import org.apache.storm.eclipse.NewSpoutWizard;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;

public class OpenNewStormTopologyClassWizardAction extends Action implements
		ICheatSheetAction {

	static Logger log = Logger
			.getLogger(OpenNewStormTopologyClassWizardAction.class.getName());

	public void run(String[] params, ICheatSheetManager manager) {

		if ((params != null) && (params.length > 0)) {
			IWorkbench workbench = PlatformUI.getWorkbench();
			INewWizard wizard = getWizard(params[0]);
			wizard.init(workbench, new StructuredSelection());
			WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), wizard);
			dialog.create();
			dialog.open();

			// did the wizard succeed ?
			notifyResult(dialog.getReturnCode() == Window.OK);
		}
	}

	private INewWizard getWizard(String typeName) {
		if (typeName.equals("Spout")) {
			return new NewSpoutWizard();
		} else if (typeName.equals("Bolt")) {
			return new NewBoltWizard();
		}  else {
			log.severe("Invalid Wizard requested");
			return null;
		}
	}
}
