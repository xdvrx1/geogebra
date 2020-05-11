package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.AbstractEnumerableProperty;

public class RatioUnitProperty extends AbstractEnumerableProperty {

    private EuclidianView3D view3D;

    /**
     * Constructs an ratio unit property.
     *
     * @param view3D       Euclidian View 3D
     * @param localization localization
     */
    RatioUnitProperty(Localization localization, EuclidianView3D view3D) {
        super(localization, "Unit");
        this.view3D = view3D;
        setValuesAndLocalize(new String[]{"cm", "inch"});

    }

    @Override
    protected void setValueSafe(String value, int index) {
        int lengthUnit;
        switch (index) {
            case 1:
                lengthUnit = EuclidianView3D.RATIO_UNIT_INCHES;
                break;
            case 0:
            default:
                lengthUnit = EuclidianView3D.RATIO_UNIT_METERS_CENTIMETERS_MILLIMETERS;
                break;
        }
        view3D.getRenderer().setARRatioMetricSystem(lengthUnit);
    }

    @Override
    public int getIndex() {
        switch (view3D.getRenderer().getARRatioMetricSystem()) {
            case EuclidianView3D.RATIO_UNIT_INCHES:
                return 1;
            case EuclidianView3D.RATIO_UNIT_METERS_CENTIMETERS_MILLIMETERS:
            default:
                return 0;
        }
    }

    @Override
    public boolean isEnabled() {
        return view3D.isARRatioShown();
    }
}
