package com.OGC.WebServices.shared;

import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.Projection;
import org.gwtopenmaps.openlayers.client.control.LayerSwitcher;
import org.gwtopenmaps.openlayers.client.control.MouseDefaults;
import org.gwtopenmaps.openlayers.client.control.OverviewMap;
import org.gwtopenmaps.openlayers.client.control.PanZoomBar;
import org.gwtopenmaps.openlayers.client.control.ScaleLine;
import org.gwtopenmaps.openlayers.client.control.WMSGetFeatureInfo;
import org.gwtopenmaps.openlayers.client.control.WMSGetFeatureInfoOptions;
import org.gwtopenmaps.openlayers.client.control.WMSGetFeatureInfoOptions.GetFeatureInfoFormat;
import org.gwtopenmaps.openlayers.client.event.GetFeatureInfoListener;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3MapType;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3Options;
import org.gwtopenmaps.openlayers.client.layer.Layer;
import org.gwtopenmaps.openlayers.client.layer.TransitionEffect;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.layer.VectorOptions;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSOptions;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;
import org.gwtopenmaps.openlayers.client.util.JObjectArray;
import org.gwtopenmaps.openlayers.client.util.JSObject;

import com.OGC.WebServices.shared.UtilityClass;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;

/**
 * Make sure you have the gwt-openlayers library that is available at 
 * http://sourceforge.net/projects/gwt-openlayers/files/gwt-openlayers/
 */
/**
 * 1. put this line in YOURPROJECTNAME.gwt.xml (look for it in the src folder and under your project
 * package name. Open it with the builtin text editor (right click on the .xml file and then openwith ...): 
 * <inherits name='org.gwtopenmaps.openlayers.OpenLayers'/>
 * 
 * 2.put this in the HTMl file <script src="http://openlayers.org/api/2.11/OpenLayers.js"></script>
 * 3. put this in the html file <script src="http://maps.google.com/maps/api/js?v=3&sensor=false"></script>
 * After this step you are ready to use the gwt-openlayers library. 
 */
/**After running it, you will get a URL such as : 
 * 
 * 1. http://127.0.0.1:8888/GNR4022013.html?gwt.codesvr=127.0.0.1:9997
 * copy it and paste it in your chrome or firefox browser. When you run it first time
 * it will prompt for installing a GWT plugin, accept it and install it. and run again the above url
 * wait for a few seconds you should see the map! Right now do not worry about the perfect Alignment of the 
 * Google map with the other layers. This is because Google layer works only in the spherical mercator projection.
 * 2. Click on the zoom slider to see the  the map layers
 * 3. Click on the small + buttons on the right side to enable the layer switcher.
 * 4. Check where you can set the extent and center parameters to align the map to the full extent
 * 5. Make sure to add this to HTML file if you have not added before <script src="http://maps.google.com/maps/api/js?v=3&sensor=false"></script>
 */

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OGCWebServices1 implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	 private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network " + "connection and try again.";
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final ConnectServerAsync connectServer = GWT.create(ConnectServer.class);
	private static final Projection DEFAULT_PROJECTION = new Projection("EPSG:4326");
	
	private MapWidget mapWidget;
	private Map map;
	private WMS wmsLayer1,wmsLayer2,wmsLayer3;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		/*---------------------------------------------------Start of GUI Design Part---------------------------------------------------*/
		
		final String width = "650px";
		final String height = "400px";
		
		// TopLeft-Panel
		final AbsolutePanel topLPanel = new AbsolutePanel();
		topLPanel.setSize(width, height);
		topLPanel.setStyleName("allPanel", true);
		
		final HorizontalPanel hPanel1 = new HorizontalPanel();
		hPanel1.setSpacing(4);
		hPanel1.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		
		final HorizontalPanel hPanel2 = new HorizontalPanel();
		hPanel2.setSpacing(4);
		hPanel2.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		
		// Add to hPanel1
		final VerticalPanel vPanel11 = new VerticalPanel();
		vPanel11.setSpacing(4);
		vPanel11.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		final ListBox serverMenu = new ListBox(false);
		serverMenu.addItem("Geo Server", "http://localhost:8080/geoserver/");
		serverMenu.addItem("NSIDC Server", "http://nsidc.org/cgi-bin/atlas_north?");
		final Button getCap = new Button("GetCapabilities");
		vPanel11.add(new HTML("Server"));
		vPanel11.add(serverMenu);
		vPanel11.add(getCap);
		hPanel1.add(vPanel11);
		
		final VerticalPanel vPanel12 = new VerticalPanel();
		vPanel12.setSpacing(4);
		vPanel12.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		final ListBox serviceMenu = new ListBox(false);
		serviceMenu.addItem("WMS-1.1.1");
		serviceMenu.addItem("WFS-1.1.0");
		serviceMenu.addItem("WCS-1.1.1");
		vPanel12.add(new HTML("Service"));
		vPanel12.add(serviceMenu);
		hPanel1.add(vPanel12);
		
		// Add to hPanel2
		final VerticalPanel vPanel21 = new VerticalPanel();
		vPanel21.setSpacing(4);
		vPanel21.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		final ListBox reqMenu = new ListBox(false);
		vPanel21.add(new HTML("Requests"));
		vPanel21.add(reqMenu);
		vPanel21.setVisible(false);
		hPanel2.add(vPanel21);
		
		topLPanel.add(hPanel1);
		topLPanel.add(hPanel2);
		
		// TopRight-Panel
		final AbsolutePanel topRPanel = new AbsolutePanel();
		topRPanel.setSize(width, height);
		topRPanel.setStyleName("allPanel");
		
		final TextArea textArea = new TextArea();
	    textArea.setSize(width, height);
	    topRPanel.add(textArea);
		
		// Bottom-Panel
		final AbsolutePanel bottomPanel = new AbsolutePanel();
		bottomPanel.setSize("1310px", "505px");
		bottomPanel.setStyleName("allPanel");
		
		// Add all panels to RootPanel
		RootPanel.get("TLPanel").add(topLPanel);
		RootPanel.get("TRPanel").add(topRPanel);
		RootPanel.get("BPanel").add(bottomPanel);
		
		MapOptions defaultMapOptions = new MapOptions();
        defaultMapOptions.setDisplayProjection(new Projection("EPSG:4326")); //causes the mouse popup to display coordinates in this format
        defaultMapOptions.setNumZoomLevels(16);
 
        //Create a MapWidget
        MapWidget mapWidget = new MapWidget("500px", "500px", defaultMapOptions);
        //Create a WMS layer as base layer
        WMSParams wmsParams = new WMSParams();
        wmsParams.setFormat("image/png");
        wmsParams.setLayers("topp:states");
        wmsParams.setStyles("");
 
        WMSOptions wmsLayerParams = new WMSOptions();
        wmsLayerParams.setUntiled();
        wmsLayerParams.setTransitionEffect(TransitionEffect.RESIZE);
 
        WMS wmsLayer = new WMS("Basic WMS", "http://localhost:8080/geoserver/wms", wmsParams, wmsLayerParams);
 
        //Add the WMS to the map
        Map map = mapWidget.getMap();
        map.addLayer(wmsLayer);
 
        //Adds the WMSGetFeatureInfo control
        WMSGetFeatureInfoOptions wmsGetFeatureInfoOptions = new WMSGetFeatureInfoOptions();
        wmsGetFeatureInfoOptions.setMaxFeaturess(50);
        wmsGetFeatureInfoOptions.setLayers(new WMS[]{wmsLayer});
        wmsGetFeatureInfoOptions.setDrillDown(true);
        //to request a GML string instead of HTML : wmsGetFeatureInfoOptions.setInfoFormat(GetFeatureInfoFormat.GML.toString());
 
        WMSGetFeatureInfo wmsGetFeatureInfo = new WMSGetFeatureInfo(
                wmsGetFeatureInfoOptions);
 
        wmsGetFeatureInfo.addGetFeatureListener(new GetFeatureInfoListener() {
            public void onGetFeatureInfo(GetFeatureInfoEvent eventObject) {
                //if you did a wmsGetFeatureInfoOptions.setInfoFormat(GetFeatureInfoFormat.GML.toString()) you can do a VectorFeature[] features = eventObject.getFeatures(); here
            	
            }
        });
        
        map.addControl(wmsGetFeatureInfo);
        wmsGetFeatureInfo.activate();
        //Lets add some default controls to the map
        map.addControl(new LayerSwitcher()); //+ sign in the upperright corner to display the layer switcher
        map.addControl(new OverviewMap()); //+ sign in the lowerright to display the overviewmap
        map.addControl(new ScaleLine()); //Display the scaleline
 
        //Center and zoom to a location
        map.setCenter(new LonLat(146.7, -41.8), 6); 
        mapWidget.getElement().getFirstChildElement().getStyle().setZIndex(0); //force the map to fall behind popups
 
		
        map.addControl(new LayerSwitcher()); 
        map.addControl(new OverviewMap()); 
        map.addControl(new ScaleLine()); 
        map.addControl(new MouseDefaults());
		map.addControl(new PanZoomBar());
		
		bottomPanel.add(mapWidget);
		
		/*---------------------------------------------------End of GUI Design Part---------------------------------------------------*/
	
		getCap.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String serverType = serverMenu.getSelectedItemText();
				String server = serverMenu.getSelectedValue();
				System.out.println("------------"+server+"------------");
				String serviceType = serviceMenu.getSelectedItemText();
				String[] strValues = serviceType.split("-");
				String service = strValues[0];
				String version = strValues[1];
				String param = "";
				if (serverType == "Geo Server") {
					param = service.toLowerCase() + "?";
				}
				String url = server + param + "service=" + service
						+ "&request=GetCapabilities&version=" + version;
				sendParamsToServer(url);
			}
			
			private void sendParamsToServer(String url) {
				getCap.setEnabled(false);
				serverMenu.setEnabled(false);
				serviceMenu.setEnabled(false);
				connectServer.sendContent(url, new AsyncCallback<String>() {
					public void onSuccess(String responseXML) {
						textArea.setText(responseXML);
						getCap.setEnabled(true);
						serverMenu.setEnabled(true);
						serviceMenu.setEnabled(true);
						parseResponse(responseXML);
					}

					public void onFailure(Throwable caught) {
						textArea.setText("Some exception got caught ...\n" + caught.getMessage());
						getCap.setEnabled(true);
						serverMenu.setEnabled(true);
						serviceMenu.setEnabled(true);
					}
				});
			}
			private void parseResponse(String xmlRes) {
				Document parse = XMLParser.parse(xmlRes);
				String serviceType = serviceMenu.getSelectedItemText();
				String[] strValues = serviceType.split("-");
				String service = strValues[0];
				if (service == "WMS") {
					NodeList request = parse.getElementsByTagName("Request");
					NodeList reqServices = request.item(0).getChildNodes();
					reqMenu.clear();
					for (int i=0; i<reqServices.getLength(); i++) {
						Node node = reqServices.item(i);
						if (!(node instanceof Text)) {
							reqMenu.addItem(node.getNodeName());
						}
					}
					vPanel21.setVisible(true);
				}
			}
		});
	}
}






