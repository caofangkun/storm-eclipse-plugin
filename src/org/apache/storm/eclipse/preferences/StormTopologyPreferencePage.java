package org.apache.storm.eclipse.preferences;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.apache.storm.eclipse.Activator;

public class StormTopologyPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public StormTopologyPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Storm Topology Tools");
	}

	@Override
	public void init(IWorkbench arg0) {
		// TODO Auto-generated method stub

	}

	public static final String P_PATH = "pathPreference";

	@Override
	protected void createFieldEditors() {
		addField(new DirectoryFieldEditor(P_PATH,
				"&Storm installation directory:", getFieldEditorParent()));

	}

}
