package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;

public class ShowLabelModel extends OptionsModel {
	public interface IShowLabelListener extends PropertyListener {
		void update(boolean isEqualVal, boolean isEqualMode);

	}

	private Kernel kernel;

	private boolean showNameValue;
	private IShowLabelListener listener;

	public ShowLabelModel(App app, IShowLabelListener listener) {
		super(app);
		kernel = app.getKernel();
		this.listener = listener;
	}

	@Override
	public void updateProperties() {

		// check if properties have same values
		GeoElement temp, geo0 = getGeoAt(0);
		boolean equalLabelVal = true;
		boolean equalLabelMode = true;
		showNameValue = geo0.isLabelValueShowable() && !isDropDownList(geo0)
				&& !geo0.isGeoInputBox();

		for (int i = 1; i < getGeosLength(); i++) {
			temp = getGeoAt(i);
			// same label visible value
			if (geo0.isLabelVisible() != temp.isLabelVisible()) {
				equalLabelVal = false;
			}
			// same label mode
			if (geo0.getLabelMode() != temp.getLabelMode()) {
				equalLabelMode = false;
			}

			showNameValue = showNameValue && temp.isLabelValueShowable()
					&& !isDropDownList(temp) && !temp.isGeoInputBox();
		}

		// change "Show Label:" to "Show Label" if there's no menu
		// Michael Borcherds 2008-02-18
		if (listener != null) {
			listener.update(equalLabelVal, equalLabelMode);
		}
	}

	private static boolean isDropDownList(GeoElement geo) {
		return (geo.isGeoList() && ((GeoList) geo).drawAsComboBox());

	}

	public void applyShowChanges(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			geo.setLabelVisible(value);
			geo.updateRepaint();
		}
		updateProperties();
		storeUndoInfo();
	}

	public void applyModeChanges(int mode, boolean visible) {
		GeoElement geo;
		for (int i = 0; i < getGeosLength(); i++) {
			geo = getGeoAt(i);
			geo.setLabelVisible(visible);
			geo.setLabelMode(mode);
			geo.updateVisualStyle(GProperty.LABEL_STYLE);
		}
		kernel.notifyRepaint();
		storeUndoInfo();
	}

	public boolean isNameValueShown() {
		return showNameValue;
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	}

	@Override
	protected boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index);
		return match(geo);
		// geo.isLabelShowable() || isDropDownList(geo)
		// || geo.isGeoInputBox();
	}

	/**
	 * @param geo
	 *            The geo to math returns true if geo meets the requirements of
	 *            this model
	 */
	public static boolean match(GeoElement geo) {
		return geo.isLabelShowable() || isDropDownList(geo)
				|| geo.isGeoInputBox();

	}
}