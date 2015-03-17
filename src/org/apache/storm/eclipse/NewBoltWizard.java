package org.apache.storm.eclipse;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class NewBoltWizard extends NewElementWizard implements INewWizard,
		IRunnableWithProgress {
	private Page page;

	public NewBoltWizard() {
		setWindowTitle("New Bolt");
	}

	public void run(IProgressMonitor monitor) {
		try {
			page.createType(monitor);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);

		page = new Page();
		addPage(page);
		page.setSelection(selection);
	}

	public static class Page extends NewTypeWizardPage {
		private Button isCreateMapMethod;

		public Page() {
			super(true, "Spout");

			setTitle("Spout");
			setDescription("Create a new Spout implementation.");
			setImageDescriptor(ImageLibrary.get("wizard.bolt.new"));
		}

		public void setSelection(IStructuredSelection selection) {
			initContainerPage(getInitialJavaElement(selection));
			initTypePage(getInitialJavaElement(selection));
		}

		@Override
		public void createType(IProgressMonitor monitor) throws CoreException,
				InterruptedException {
			super.createType(monitor);
		}

		@Override
		protected void createTypeMembers(IType newType, ImportsManager imports,
				IProgressMonitor monitor) throws CoreException {
			super.createTypeMembers(newType, imports, monitor);
			imports.addImport("backtype.storm.topology.base.BaseRichBolt");
			newType.createMethod(
					"public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {\n\n}\n",
					null, false, monitor);

			newType.createMethod("public void execute(Tuple input) {\n\n}\n",
					null, false, monitor);

			newType.createMethod(
					"public void declareOutputFields(OutputFieldsDeclarer declarer) {\n\n}\n",
					null, false, monitor);

		}

		public void createControl(Composite parent) {
			// super.createControl(parent);

			initializeDialogUnits(parent);
			Composite composite = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.numColumns = 4;
			composite.setLayout(layout);

			createContainerControls(composite, 4);
			createPackageControls(composite, 4);
			createSeparator(composite, 4);
			createTypeNameControls(composite, 4);
			createSuperClassControls(composite, 4);
			createSuperInterfacesControls(composite, 4);
			// createSeparator(composite, 4);

			setControl(composite);

			setSuperClass("backtype.storm.topology.base.BaseRichSpout", true);

			setFocus();
			validate();
		}

		@Override
		protected void handleFieldChanged(String fieldName) {
			super.handleFieldChanged(fieldName);

			validate();
		}

		private void validate() {
			updateStatus(new IStatus[] { fContainerStatus, fPackageStatus,
					fTypeNameStatus, fSuperClassStatus, fSuperInterfacesStatus });
		}
	}

	@Override
	public boolean performFinish() {
		if (super.performFinish()) {
			if (getCreatedElement() != null) {
				openResource((IFile) page.getModifiedResource());
				selectAndReveal(page.getModifiedResource());
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void finishPage(IProgressMonitor monitor)
			throws InterruptedException, CoreException {
		this.run(monitor);
	}

	@Override
	public IJavaElement getCreatedElement() {
		return page.getCreatedType().getPrimaryElement();
	}

}
