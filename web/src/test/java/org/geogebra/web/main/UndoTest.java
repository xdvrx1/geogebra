package org.geogebra.web.main;

import org.geogebra.web.full.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.main.BrowserDevice;
import org.geogebra.web.geogebra3D.AppletFactory3D;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ArticleElement;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.user.client.DOM;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.web.parser.NodeW;
import com.himamis.retex.renderer.web.resources.xml.XmlResources;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ ArticleElement.class, TextAreaElement.class, NodeW.class })
public class UndoTest {

	@Test
	public void createUndo() {
		GwtMockito.useProviderForType(XmlResources.class,
				new TextResourceProvider());
		
		FactoryProvider.setInstance(new MockFactoryProviderGWT());
		GeoGebraFrameBoth fr = new GeoGebraFrameBoth(new AppletFactory3D(), new GLookAndFeel(), new BrowserDevice(), false);
		fr.ae = ArticleElement.as(DOM.createElement("article"));
		fr.runAsyncAfterSplash();
		AppW app = (AppW) fr.getApplication();
		app.setUndoRedoEnabled(true);
		app.setUndoActive(true);
		app.getKernel().getConstruction().initUndoInfo();
		app.getKernel().getAlgebraProcessor().processAlgebraCommand("x", true);
		app.getKernel().getAlgebraProcessor().processAlgebraCommand("-x", true);

		Assert.assertEquals(2, app.getKernel().getConstruction()
				.getUndoManager().getHistorySize());
		app.getGgbApi().undo();
		Assert.assertEquals(1, app.getKernel().getConstruction()
				.getUndoManager().getHistorySize());
	}
}
