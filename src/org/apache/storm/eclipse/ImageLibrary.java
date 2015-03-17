package org.apache.storm.eclipse;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;

public class ImageLibrary {
	private final Bundle bundle = Activator.getDefault().getBundle();

	private static volatile ImageLibrary instance = null;

	private ISharedImages sharedImages = PlatformUI.getWorkbench()
			.getSharedImages();

	private static final String RESOURCE_DIR = "icons/";

	public static ImageDescriptor get(String name) {
		return getInstance().getImageDescriptorByName(name);
	}

	public static Image getImage(String name) {
		return getInstance().getImageByName(name);
	}

	public static ImageLibrary getInstance() {
		if (instance == null) {
			synchronized (ImageLibrary.class) {
				if (instance == null)
					instance = new ImageLibrary();
			}
		}
		return instance;
	}

	private Map<String, ImageDescriptor> descMap = new HashMap<String, ImageDescriptor>();

	private Map<String, Image> imageMap = new HashMap<String, Image>();

	private ImageLibrary() {
		newImage("server.view.location.entry", "alt_window16.gif");
		newImage("server.view.job.entry", "alt_window16.gif");
		newImage("server.view.action.location.new", "alt_window16.gif");
		newImage("server.view.action.location.edit", "alt_window16.gif");
		newSharedImage("server.view.action.delete",
				ISharedImages.IMG_TOOL_DELETE);

		newImage("wizard.spout.new", "alt_window16.gif");
		newImage("wizard.bolt.new", "alt_window16.gif");
		newImage("wizard.topology.project.new", "alt_window16.gif");
	}

	
	private ImageDescriptor getImageDescriptorByName(String name) {
		return this.descMap.get(name);
	}

	private Image getImageByName(String name) {
		return this.imageMap.get(name);
	}

	private ImageDescriptor getSharedByName(String name) {
		return sharedImages.getImageDescriptor(name);
	}

	
	private boolean newImage(String name, String filename) {
		ImageDescriptor id;
		boolean success;

		try {
			URL fileURL = FileLocator.find(bundle, new Path(RESOURCE_DIR
					+ filename), null);
			id = ImageDescriptor.createFromURL(FileLocator.toFileURL(fileURL));
			success = true;

		} catch (Exception e) {

			e.printStackTrace();
			id = ImageDescriptor.getMissingImageDescriptor();
			success = false;
		}

		descMap.put(name, id);
		imageMap.put(name, id.createImage(true));

		return success;
	}

	
	private boolean newSharedImage(String name, String sharedName) {
		boolean success = true;
		ImageDescriptor id = getSharedByName(sharedName);

		if (id == null) {
			id = ImageDescriptor.getMissingImageDescriptor();
			success = false;
		}

		descMap.put(name, id);
		imageMap.put(name, id.createImage(true));

		return success;
	}

	private boolean newPluginImage(String name, String pluginId, String filename) {

		boolean success = true;
		ImageDescriptor id = AbstractUIPlugin.imageDescriptorFromPlugin(
				pluginId, filename);

		if (id == null) {
			id = ImageDescriptor.getMissingImageDescriptor();
			success = false;
		}

		descMap.put(name, id);
		imageMap.put(name, id.createImage(true));

		return success;
	}
}