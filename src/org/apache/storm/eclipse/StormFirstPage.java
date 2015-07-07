package org.apache.storm.eclipse;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.storm.eclipse.preferences.StormTopologyPreferencePage;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

public class StormFirstPage extends WizardNewProjectCreationPage implements
    SelectionListener {

  private Link openPreferences;
  private Button workspaceStorm;
  private Button projectStorm;
  private Text location;
  private Button browse;
  private String path;
  public String currentPath;

  public StormFirstPage() {
    super("New Apache Storm Topology Project");
    setImageDescriptor(ImageLibrary.get("wizard.topology.project.new"));
  }

  @Override
  public void createControl(Composite parent) {
    super.createControl(parent);

    setTitle("Apache Storm Topology Project");
    setDescription("Create a Apache Storm Topology Project.");

    Group group = new Group((Composite) getControl(), SWT.NONE);
    group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    group.setText("Apache Storm Topology Library Installation Path");
    GridLayout layout = new GridLayout(3, true);
    layout.marginLeft =
        convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
    layout.marginRight =
        convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
    layout.marginTop =
        convertHorizontalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
    layout.marginBottom =
        convertHorizontalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
    group.setLayout(layout);

    workspaceStorm = new Button(group, SWT.RADIO);
    GridData d =
        new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false);
    d.horizontalSpan = 2;
    workspaceStorm.setLayoutData(d);
    workspaceStorm.setSelection(true);

    updateStormDirLabelFromPreferences();

    openPreferences = new Link(group, SWT.NONE);
    openPreferences
        .setText("<a>Configure Apache Storm install directory...</a>");
    openPreferences.setLayoutData(new GridData(GridData.END, GridData.CENTER,
        false, false));
    openPreferences.addSelectionListener(this);

    projectStorm = new Button(group, SWT.RADIO);
    projectStorm.setLayoutData(new GridData(GridData.BEGINNING,
        GridData.CENTER, false, false));
    projectStorm.setText("Specify Apache Storm library location");

    location = new Text(group, SWT.SINGLE | SWT.BORDER);
    location.setText("");
    d = new GridData(GridData.END, GridData.CENTER, true, false);
    d.horizontalSpan = 1;
    d.widthHint = 250;
    d.grabExcessHorizontalSpace = true;
    location.setLayoutData(d);
    location.setEnabled(false);

    browse = new Button(group, SWT.NONE);
    browse.setText("Browse...");
    browse.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER,
        false, false));
    browse.setEnabled(false);
    browse.addSelectionListener(this);

    projectStorm.addSelectionListener(this);
    workspaceStorm.addSelectionListener(this);
  }

  @Override
  public boolean isPageComplete() {
    boolean validStorm = validateStormLocation();

    if (!validStorm && isCurrentPage()) {
      setErrorMessage("Invalid Apache Storm Runtime specified; please click 'Configure Apache Storm install directory' or fill in library location input field");
    } else {
      setErrorMessage(null);
    }

    return super.isPageComplete() && validStorm;
  }

  private boolean validateStormLocation() {
    FilenameFilter gotStormJar = new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return true;
      }
    };

    if (workspaceStorm.getSelection()) {
      this.currentPath = path;
      return new Path(path).toFile().exists()
          && (new Path(path).toFile().list(gotStormJar).length > 0);
    } else {
      this.currentPath = location.getText();
      File file = new Path(location.getText()).toFile();
      return file.exists()
          && (new Path(location.getText()).toFile().list(gotStormJar).length > 0);
    }
  }

  public static final String P_PATH = "pathPreference";

  private void updateStormDirLabelFromPreferences() {
    path = Activator.getDefault().getPreferenceStore().getString(P_PATH);

    if ((path != null) && (path.length() > 0)) {
      workspaceStorm.setText("Use default Apache Storm");
    } else {
      workspaceStorm.setText("Use default Apache Storm (currently not set)");
    }
  }

  public void widgetDefaultSelected(SelectionEvent e) {
  }

  public void widgetSelected(SelectionEvent e) {
    if (e.getSource() == openPreferences) {
      PreferenceManager manager = new PreferenceManager();
      manager.addToRoot(new PreferenceNode(
          "Apache Storm Installation Directory",
          new StormTopologyPreferencePage()));
      PreferenceDialog dialog = new PreferenceDialog(this.getShell(), manager);
      dialog.create();
      dialog.setMessage("Select Apache Storm Installation Directory");
      dialog.setBlockOnOpen(true);
      dialog.open();

      updateStormDirLabelFromPreferences();
    } else if (e.getSource() == browse) {
      DirectoryDialog dialog = new DirectoryDialog(this.getShell());
      dialog
          .setMessage("Select a Apache Storm installation, containing storm-core-*.jar");
      dialog.setText("Select Apache Storm Installation Directory");
      String directory = dialog.open();

      if (directory != null) {
        location.setText(directory);

        if (!validateStormLocation()) {
          setErrorMessage("No Apache Storm jar found in specified directory");
        } else {
          setErrorMessage(null);
        }
      }
    } else if (projectStorm.getSelection()) {
      location.setEnabled(true);
      browse.setEnabled(true);
    } else {
      location.setEnabled(false);
      browse.setEnabled(false);
    }

    getContainer().updateButtons();
  }
}
