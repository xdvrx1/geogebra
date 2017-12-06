/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.BoundingBox;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.CurvePlotter;
import org.geogebra.common.euclidian.plot.CurvePlotter.Gap;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.kernel.AlgoCasCellInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.cas.AlgoIntegralFunctions;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Draws definite Integral of a GeoFunction
 * 
 * @author Markus Hohenwarter
 */
public class DrawIntegralFunctions extends Drawable {

	private GeoNumeric n;
	private GeoFunction f, g;
	private NumberValue a, b;

	private GeneralPathClippedForCurvePlotter gp;
	private boolean isVisible, labelVisible;
	private boolean isCasObject;

	/**
	 * Creates drawable for integral between two functions
	 * 
	 * @param view
	 *            view
	 * @param n
	 *            integral between functions
	 * @param casObject
	 *            true if n was created from a GeoCasCell
	 */
	public DrawIntegralFunctions(EuclidianView view, GeoNumeric n,
			boolean casObject) {
		this.view = view;
		this.n = n;
		geo = n;
		isCasObject = casObject;

		n.setDrawable(true);

		init();

		update();
	}

	private void init() {
		if (isCasObject) {
			initFromCasObject();
			return;
		}
		AlgoIntegralFunctions algo = (AlgoIntegralFunctions) n
				.getDrawAlgorithm();
		f = algo.getF();
		g = algo.getG();
		a = algo.getA();
		b = algo.getB();
	}

	private void initFromCasObject() {
		AlgoCasCellInterface algo = (AlgoCasCellInterface) n.getDrawAlgorithm();
		GeoCasCell cell = algo.getCasCell();
		Command cmd = cell.getInputVE().getTopLevelCommand();
		Kernel kernel = cmd.getKernel();

		ExpressionValue arg0 = cmd.getArgument(0).unwrap();
		if (arg0 instanceof GeoCasCell) {
			// https://help.geogebra.org/topic/integraaltussen-wordt-grafisch-verkeerd-weergegeven-via-cas
			f = (GeoFunction) ((GeoCasCell) arg0).getTwinGeo();
		} else {
			f = new GeoFunction(kernel.getConstruction(), new Function(
					cmd.getArgument(0).wrap().replaceCasCommands()));
		}

		ExpressionValue arg1 = cmd.getArgument(1).unwrap();
		if (arg1 instanceof GeoCasCell) {
			g = (GeoFunction) ((GeoCasCell) arg1).getTwinGeo();
		} else {
			g = new GeoFunction(kernel.getConstruction(), new Function(
					cmd.getArgument(1).wrap().replaceCasCommands()));
		}

		ExpressionValue arg2 = cmd.getArgument(2).unwrap();
		if (arg2 instanceof GeoCasCell) {
			a = new MyDouble(cmd.getKernel(),
					((GeoCasCell) arg2).getTwinGeo().evaluateDouble());
		} else {
			a = new MyDouble(cmd.getKernel(), cmd.getArgument(2).wrap()
					.replaceCasCommands().evaluateDouble());
		}

		ExpressionValue arg3 = cmd.getArgument(3).unwrap();
		if (arg3 instanceof GeoCasCell) {
			b = new MyDouble(cmd.getKernel(),
					((GeoCasCell) arg3).getTwinGeo().evaluateDouble());
		} else {
			b = new MyDouble(cmd.getKernel(), cmd.getArgument(3).wrap()
					.replaceCasCommands().evaluateDouble());
		}

	}

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible) {
			return;
		}
		labelVisible = geo.isLabelVisible();
		updateStrokes(n);

		if (n.isAlgoMacroOutput() || isCasObject) {
			init();
		}

		// init gp
		double aRW = Math.min(a.getDouble(), b.getDouble());
		double bRW = Math.max(a.getDouble(), b.getDouble());

		double clipX = view.toRealWorldCoordX(EuclidianStatic.CLIP_DISTANCE)
				- view.toRealWorldCoordX(0);
		// double clipY = view.toRealWorldCoordY(0)
		// - view.toRealWorldCoordY(EuclidianStatic.CLIP_DISTANCE);

		// for DrawParametricCurve.plotCurve to work with special values,
		// these changes are needed (also filter out out of screen integrals)
		// see TRAC-1036
		aRW = Math.max(aRW, view.getXmin() - clipX);
		if (aRW > view.getXmax() + clipX) {
			// make invisible to prevent NPE on draw
			isVisible = false;
			return;
		}

		bRW = Math.min(bRW, view.getXmax() + clipX);
		if (bRW < view.getXmin() - clipX) {
			isVisible = false;
			return;
		}

		// init first point of gp as (ax, ay)
		double ax = view.toClippedScreenCoordX(aRW);
		double ay = view.toClippedScreenCoordY(f.value(aRW));

		// plot area between f and g
		if (gp == null) {
			gp = new GeneralPathClippedForCurvePlotter(view);
		}
		gp.reset();
		gp.moveTo(ax, ay);
		CurvePlotter.plotCurve(f, aRW, bRW, view, gp, false, Gap.LINE_TO);
		CurvePlotter.plotCurve(g, bRW, aRW, view, gp, false, Gap.LINE_TO);
		gp.closePath();

		// gp on screen?
		if (!gp.intersects(0, 0, view.getWidth(), view.getHeight())) {
			isVisible = false;
			// don't return here to make sure that getBounds() works for
			// offscreen points too
		}

		if (labelVisible) {
			int bx = view.toClippedScreenCoordX(bRW);
			xLabel = (int) Math.round((ax + bx) / 2);
			aRW = view.toRealWorldCoordX(xLabel);
			double y = (f.value(aRW) + g.value(aRW)) / 2;
			yLabel = view.toClippedScreenCoordY(y);
			labelDesc = geo.getLabelDescription();
			addLabelOffset();
		}
	}

	@Override
	final public void draw(GGraphics2D g2) {
		if (isVisible) {
			if (geo.doHighlighting()) {
				g2.setPaint(n.getSelColor());
				g2.setStroke(selStroke);
				g2.draw(gp);
			}
			if (gp != null) {
				fill(g2, gp); // fill using default/hatching/image as
			} // appropriate

			if (objStroke.getLineWidth() > 0) {
				g2.setPaint(getObjectColor());
				g2.setStroke(objStroke);
				g2.draw(gp);
			}

			if (labelVisible) {
				g2.setFont(view.getFontConic());
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);
			}
		}
	}

	@Override
	final public boolean hit(int x, int y, int hitThreshold) {
		return gp != null
				&& (gp.contains(x, y) || gp.intersects(x, y, hitThreshold));
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		return gp != null && gp.intersects(rect);
	}

	@Override
	final public boolean isInside(GRectangle rect) {
		return false;
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	@Override
	final public GRectangle getBounds() {
		if (!geo.isDefined() || !geo.isEuclidianVisible()) {
			return null;
		}
		return gp.getBounds();
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	@Override
	public BoundingBox getBoundingBox() {
		// TODO Auto-generated method stub
		return null;
	}
}
