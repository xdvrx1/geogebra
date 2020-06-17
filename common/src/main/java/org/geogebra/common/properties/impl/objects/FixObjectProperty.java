package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.properties.BooleanProperty;

/**
 * Fix object
 */
public class FixObjectProperty extends AbstractGeoElementProperty implements BooleanProperty {

	public FixObjectProperty(GeoElement geoElement) throws NotApplicablePropertyException {
		super("fixed", geoElement);
	}

	@Override
	public boolean getValue() {
		return getElement().isLocked();
	}

	@Override
	public void setValue(boolean fixObject) {
		GeoElement element = getElement();
		element.setFixed(fixObject);
	}

	@Override
	boolean isApplicableTo(GeoElement element) {
		if (element instanceof GeoList) {
			return isApplicableToGeoList((GeoList) element);
		}
		AppConfig config = element.getApp().getConfig();
		if (hasFunctionProperties(element) && config.isObjectDraggingRestricted()) {
			return false;
		}
		return element.showFixUnfix();
	}

	private boolean hasFunctionProperties(GeoElement element) {
		if (element instanceof GeoList && !isApplicableTo(element)) {
			return false;
		} else {
			return element.isFunctionOrEquationFromUser();
		}
	}

	@Override
	boolean isApplicableToGeoList(GeoList list) {
		GeoElement elementForProperties = list.getGeoElementForPropertiesDialog();
		return elementForProperties instanceof GeoFunction;
	}
}