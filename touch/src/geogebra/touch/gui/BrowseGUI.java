package geogebra.touch.gui;

import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.html5.util.ggtapi.GeoGebraTubeAPI;
import geogebra.html5.util.ggtapi.JSONparserGGT;
import geogebra.touch.FileManagerT;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.elements.ggt.FileContainer;
import geogebra.touch.gui.elements.ggt.MaterialListElement;
import geogebra.touch.gui.elements.ggt.VerticalMaterialPanel;
import geogebra.touch.gui.elements.header.BrowseHeaderPanel;
import geogebra.touch.gui.elements.header.BrowseHeaderPanel.SearchListener;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * GeoGebraTube Search and Browse GUI
 * 
 */
public class BrowseGUI extends HeaderPanel {
	
	private final List<ResizeListener> resizeListeners = new ArrayList<ResizeListener>();
	private BrowseHeaderPanel header;
	private final FileManagerT fm;
	private final AppWeb app;

	// HorizontalMaterialPanel featuredMaterials;
	private VerticalMaterialPanel localFilePanel, tubeFilePanel;
	private FileContainer localFileContainer, tubeFileContainer;

	private List<Material> localList = new ArrayList<Material>();
	private List<Material> tubeList = new ArrayList<Material>();

	public final static int HEADING_HEIGHT = 50;
	public final static int CONTROLS_HEIGHT = 50;

	/**
	 * Sets the viewport and other settings, creates a link element at the end
	 * of the head, appends the css file and initializes the GUI elements.
	 * 
	 * @param app
	 */
	public BrowseGUI(final AppWeb app) {
		this.setStyleName("browsegui");
		this.fm = ((TouchApp) app).getFileManager();
		this.app = app;

		addHeader();
		addContent();
	}

	private void addHeader() {
		this.header = TouchEntryPoint.getLookAndFeel().buildBrowseHeader(this);
		this.header.addSearchListener(new SearchListener() {
			@Override
			public void onSearch(final String query) {
				BrowseGUI.this.displaySearchResults(query);
			}
		});
	}

	private void addContent() {
		initLocalFilePanel();
		initTubeFilePanel();

		final HorizontalPanel fileList = new HorizontalPanel();
		fileList.add(this.localFileContainer);
		fileList.add(this.tubeFileContainer);
		this.setContentWidget(fileList);
	}

	private void initTubeFilePanel() {
		this.tubeFilePanel = new VerticalMaterialPanel(this.app);
		this.tubeFileContainer = new FileContainer("GeoGebraTube", this.tubeFilePanel);
		this.tubeFileContainer.setStyleName("tubeFilePanel");
		this.addResizeListener(this.tubeFileContainer);
		this.addResizeListener(this.tubeFilePanel);
	}

	private void initLocalFilePanel() {
		this.localFilePanel = new VerticalMaterialPanel(this.app);
		this.localFileContainer = new FileContainer(this.app.getMenu("MyProfile"), this.localFilePanel);
		this.localFileContainer.addStyleName("localFilePanel");
		this.addResizeListener(this.localFileContainer);
		this.addResizeListener(this.localFilePanel);
	}

	protected void displaySearchResults(final String query) {
		this.localList = this.fm.search(query);
		GeoGebraTubeAPI.getInstance(
				geogebra.common.move.ggtapi.models.GeoGebraTubeAPI.url).search(
				query, new RequestCallback() {
					@Override
					public void onError(final Request request,
							final Throwable exception) {
						// FIXME implement Error Handling!
						BrowseGUI.this.updateGUI();
						exception.printStackTrace();
					}

					@Override
					public void onResponseReceived(final Request request,
							final Response response) {
						App.debug(response.getText());
						onSearchResults(response);
						onResize();
					}
				});
	}

	public MaterialListElement getChosenMaterial() {
		return this.tubeFilePanel.getChosenMaterial();
	}

	public void loadFeatured() {
		this.localList = this.fm.getAllFiles();
		GeoGebraTubeAPI.getInstance(
				geogebra.common.move.ggtapi.models.GeoGebraTubeAPI.url)
				.getFeaturedMaterials(new RequestCallback() {
					@Override
					public void onError(final Request request,
							final Throwable exception) {
						BrowseGUI.this.updateGUI();
					}

					@Override
					public void onResponseReceived(final Request request,
							final Response response) {
						onSearchResults(response);
						onResize();
					}
				});
	}

	protected void onSearchResults(final Response response) {
		this.tubeList = JSONparserGGT.parseResponse(response.getText());
		this.updateGUI();
	}

	public void addResizeListener(final ResizeListener rl) {
		this.resizeListeners.add(rl);
	}
	
	@Override
	public void onResize() {
		this.header.onResize();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				resizeElements();
			}
		});

	}

	protected void resizeElements() {
		for (final ResizeListener res : this.resizeListeners) {
			res.onResize();
		}
	}
	
	/**
	 * Loads the featured materials
	 */
	public void reloadLocalFiles() {
		this.localList = this.fm.getAllFiles();
		this.updateGUI();
	}

	public void setLabels() {
		this.header.setLabels();
		this.tubeFilePanel.setLabels();
		this.localFilePanel.setLabels();
		this.localFileContainer.setHeading(this.app.getMenu("MyProfile"));
	}

	protected void updateGUI() {
		if (this.tubeList.isEmpty()) {
			this.localFilePanel.setMaterials(2, this.localList);
			this.tubeFileContainer.setVisible(false);
			this.localFileContainer.setVisible(true);
		} else if (this.localList.isEmpty()) {
			this.tubeFilePanel.setMaterials(2, this.tubeList);
			this.localFileContainer.setVisible(false);
			this.tubeFileContainer.setVisible(true);
		} else {
			this.localFilePanel.setMaterials(1, this.localList);
			this.tubeFilePanel.setMaterials(1, this.tubeList);
			this.tubeFileContainer.setVisible(true);
			this.localFileContainer.setVisible(true);
		}
	}
}
