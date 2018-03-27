package com.OGC.WebServices.client;

import java.util.HashMap;
import java.util.HashSet;

import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.Pixel;
import org.gwtopenmaps.openlayers.client.control.LayerSwitcher;
import org.gwtopenmaps.openlayers.client.control.MouseDefaults;
import org.gwtopenmaps.openlayers.client.control.OverviewMap;
import org.gwtopenmaps.openlayers.client.control.PanZoomBar;
import org.gwtopenmaps.openlayers.client.control.ScaleLine;
import org.gwtopenmaps.openlayers.client.control.SelectFeature;
import org.gwtopenmaps.openlayers.client.event.MapClickListener;
import org.gwtopenmaps.openlayers.client.geometry.Point;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3MapType;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3Options;
import org.gwtopenmaps.openlayers.client.layer.Layer;
import org.gwtopenmaps.openlayers.client.layer.TransitionEffect;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSOptions;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;
import org.gwtopenmaps.openlayers.client.util.JObjectArray;
import org.gwtopenmaps.openlayers.client.util.JSObject;

import com.OGC.WebServices.shared.UtilityClass;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

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
public class OGCWebServices implements EntryPoint {
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final ConnectServerAsync connectServer = GWT.create(ConnectServer.class);
	
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
		
		final VerticalPanel vPanel13 = new VerticalPanel();
		vPanel13.setSpacing(4);
		vPanel13.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		final ListBox reqMenu = new ListBox(false);
		vPanel13.add(new HTML("Requests"));
		vPanel13.add(reqMenu);
		vPanel13.setVisible(false);
		hPanel1.add(vPanel13);
		
		final VerticalPanel vPanel14 = new VerticalPanel();
		vPanel14.setSpacing(4);
		vPanel14.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		final TextArea urlArea = new TextArea();
	    urlArea.setSize("45px", "10px");
	    final ListBox layerMenu = new ListBox(false);
	    layerMenu.addItem("US states", "topp:states");
	    layerMenu.addItem("Boundaries", "topp:tasmania_state_boundaries");
	    layerMenu.addItem("Atlas", "cryosphere_atlas_north");
	    final Button sendUrl = new Button("Request");
	    vPanel14.add(urlArea);
	    vPanel14.add(layerMenu);
	    vPanel14.add(sendUrl);
	    hPanel1.add(vPanel14);
		
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
		
		MapOptions mapOptions = new MapOptions();
		mapOptions.setControls(new JObjectArray(new JSObject[] {}));
		mapOptions.setNumZoomLevels(16);
		mapOptions.setProjection("EPSG:4326");

		// Let's create map widget and map objects
		mapWidget = new MapWidget("1303px", "503px", mapOptions);
		map = mapWidget.getMap();
		
        // Google map layers  
		GoogleV3Options gNormalOptions = new GoogleV3Options();
		gNormalOptions.setIsBaseLayer(true);
        gNormalOptions.setType(GoogleV3MapType.G_NORMAL_MAP);
        GoogleV3 gNormal = new GoogleV3("Google Normal", gNormalOptions);
		 		        
        // Adding google Base Map layer on map   
        map.addLayer(gNormal);
        gNormal.setIsVisible(true);
        LonLat lonLat = new LonLat(-100.99, 40.73);
        lonLat.transform(UtilityClass.DEFAULT_PROJECTION.getProjectionCode(),map.getProjection()); 
        map.setCenter(lonLat, 3);
		
        // See the various layers in geoserver under "layers preview". you should be able to see
		// it in http://localhost:8080/geoserver. Make sure the server is running.
		// Below is an example of adding a layer called "topp:states"
		// Try to add more layers (see the geoserver " layers preview" to see what other layers are available)
		 
		// Let's create WMS map layer
		final WMSParams wmsParams1 = new WMSParams();
		wmsParams1.setFormat("image/png");
		wmsParams1.setTransparent(true); // to make base layer remove transparency
	    wmsParams1.setStyles("");
	    final WMSOptions wmsLayerParams1 = new WMSOptions();
		wmsLayerParams1.setDisplayOutsideMaxExtent(true);
	    wmsLayerParams1.setTransitionEffect(TransitionEffect.RESIZE);
		
		
        /*WMSParams wmsParams2 = new WMSParams();
        wmsParams2.setFormat("image/png");
        wmsParams2.setLayers("topp:tasmania_state_boundaries");
        wmsParams2.setTransparent(true); 
        wmsParams2.setStyles("green");
        WMSOptions wmsLayerParams2 = new WMSOptions();
		wmsLayerParams2.setDisplayOutsideMaxExtent(true);
	    wmsLayerParams2.setTransitionEffect(TransitionEffect.RESIZE);
        wmsLayer2 = new WMS ("Boundaries","http://localhost:8080/geoserver/wms",wmsParams2,wmsLayerParams2);
        wmsLayer2.setIsBaseLayer(false);
        
        WMSParams wmsParams3 = new WMSParams();
        wmsParams3.setFormat("image/png");
        wmsParams3.setLayers("topp:tasmania_water_bodies");
        wmsParams3.setTransparent(true);
        wmsParams3.setStyles("cite_lakes");
        WMSOptions wmsLayerParams3 = new WMSOptions();
		wmsLayerParams3.setDisplayOutsideMaxExtent(true);
	    wmsLayerParams3.setTransitionEffect(TransitionEffect.RESIZE);
        wmsLayer3 = new WMS ("Water Bodies","http://localhost:8080/geoserver/wms",wmsParams3,wmsLayerParams3);
        wmsLayer3.setIsBaseLayer(false);*/
        
        /*WMSParams wmsParams1 = new WMSParams();
		wmsParams1.setFormat("image/png");
		wmsParams1.setLayers("cryosphere_atlas_north");
		wmsParams1.setBgColor("0x000787");
		wmsParams1.setTransparent(true); // to make base layer remove transparency
	    wmsParams1.setStyles("");
	    WMSOptions wmsLayerParams1 = new WMSOptions();
		wmsLayerParams1.setDisplayOutsideMaxExtent(true);
	    wmsLayerParams1.setTransitionEffectResize();
		wmsLayer1 = new WMS ("Atlas of the Cryosphere","http://nsidc.org/cgi-bin/atlas_north",wmsParams1,wmsLayerParams1);
		wmsLayer1.setIsBaseLayer(false);
		
        WMSParams wmsParams2 = new WMSParams();
        wmsParams2.setFormat("image/png");
        wmsParams2.setLayers("blue_marble_01");
        wmsParams1.setBgColor("0x000787");
        wmsParams2.setTransparent(true); 
        wmsParams2.setStyles("");
        WMSOptions wmsLayerParams2 = new WMSOptions();
		wmsLayerParams2.setDisplayOutsideMaxExtent(true);
	    wmsLayerParams2.setTransitionEffectResize();
        wmsLayer2 = new WMS ("Blue1","http://nsidc.org/cgi-bin/atlas_north",wmsParams2,wmsLayerParams2);
        wmsLayer2.setIsBaseLayer(false);
        
        WMSParams wmsParams3 = new WMSParams();
        wmsParams3.setFormat("image/png");
        wmsParams3.setLayers("blue_marble_07");
        wmsParams3.setTransparent(true);
        wmsParams3.setStyles("");
        WMSOptions wmsLayerParams3 = new WMSOptions();
		wmsLayerParams3.setDisplayOutsideMaxExtent(true);
	    wmsLayerParams3.setTransitionEffectResize();
        wmsLayer3 = new WMS ("Blue7","http://nsidc.org/cgi-bin/atlas_north",wmsParams3,wmsLayerParams3);
        wmsLayer3.setIsBaseLayer(false);*/
        
		// Let's add layers and controls to map
        // map.addLayers(new Layer[] {wmsLayer1,wmsLayer2,wmsLayer3});
        map.addControl(new LayerSwitcher()); 
        map.addControl(new OverviewMap()); 
        map.addControl(new ScaleLine()); 
        map.addControl(new MouseDefaults());
		map.addControl(new PanZoomBar());
		
		bottomPanel.add(mapWidget);
		
		final Vector vectorLayer = new Vector("Vectorlayer");
        map.addLayer(vectorLayer);
        
        sendUrl.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String serverType = serverMenu.getSelectedItemText();
				String server = serverMenu.getSelectedValue();
				String serviceType = serviceMenu.getSelectedItemText();
				String service = UtilityClass.getServiceVer(serviceType, UtilityClass.SERVICE);
				String version = UtilityClass.getServiceVer(serviceType, UtilityClass.VERSION);
				if (service == UtilityClass.WMS) {
					if (serverType == "Geo Server") {
						server = server + service.toLowerCase();
					} else {
						server = server.substring(0, server.length()-1);
					}
					wmsParams1.setLayers(layerMenu.getSelectedValue());
					wmsLayer1 = new WMS (layerMenu.getSelectedItemText(),server,wmsParams1,wmsLayerParams1);
					wmsLayer1.setIsBaseLayer(false);
					map.addLayer(wmsLayer1);
					
				} else {
					String[] url = {"", "", urlArea.getText()};
					connectServer.sendContent(url, new AsyncCallback<String>() {
						public void onSuccess(String responseXML) {
							textArea.setText(responseXML);
						}

						public void onFailure(Throwable caught) {
							textArea.setText("Some exception got caught ...\n" + caught.getMessage());
						}
					});
				}				
			}
		});
 
        map.addMapClickListener(new MapClickListener() {
			@Override
			public void onClick(MapClickEvent mapClickEvent) {
				vectorLayer.removeAllFeatures();
				LonLat lonLat2 = mapClickEvent.getLonLat();
				Point point = new Point(lonLat2.lon(), lonLat2.lat());
				VectorFeature pointFeature = new VectorFeature(point);
		        vectorLayer.addFeature(pointFeature);
				Pixel pixel = mapClickEvent.getPixel();
		        String url = "http://localhost:8080/geoserver/wms?bbox=-130,24,-66,50"
		        	+ "&styles=population&format=jpeg&info_format=text/plain&request=GetFeatureInfo"
		        	 + "&layers=topp:states&query_layers=topp:states&width=500&height=400&x=" + pixel.x() + "&y=" + pixel.y();
		        String[] data = {UtilityClass.WMS, "GetFeatureInfo", url};
		        connectServer.sendContent(data, new AsyncCallback<String>() {
					public void onSuccess(String responseXML) {
						textArea.setText(responseXML);
					}

					public void onFailure(Throwable caught) {
						textArea.setText("Some exception got caught ...\n" + caught.getMessage());
					}
				});
			}
        });
 
        //Lets add some default controls to the map
        map.addControl(new LayerSwitcher()); //+ sign in the upperright corner to display the layer switcher
        map.addControl(new OverviewMap()); //+ sign in the lowerright to display the overviewmap
        map.addControl(new ScaleLine()); //Display the scaleline
 
        //Center and zoom to a location
        map.setCenter(new LonLat(130.7, -41.8), 2);
		
		/*---------------------------------------------------End of GUI Design Part---------------------------------------------------*/
		
		serverMenu.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				hPanel2.clear();
				vPanel13.setVisible(false);
			}
		});
		serviceMenu.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				hPanel2.clear();
				vPanel13.setVisible(false);
			}
		});
		getCap.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String serverType = serverMenu.getSelectedItemText();
				String server = serverMenu.getSelectedValue();
				String serviceType = serviceMenu.getSelectedItemText();
				String service = UtilityClass.getServiceVer(serviceType, UtilityClass.SERVICE);
				String version = UtilityClass.getServiceVer(serviceType, UtilityClass.VERSION);
				String param = "";
				if (serverType == "Geo Server") {
					param = service.toLowerCase() + "?";
				}
				String url = server + param + "service=" + service
						+ "&request=GetCapabilities&version=" + version;
				String[] urlData = {service, UtilityClass.GET_CAPABILITIES, url};
				sendParamsToServer(urlData);
			}
			
			private void sendParamsToServer(String[] urlData) {
				connectServer.sendContent(urlData, new AsyncCallback<String>() {
					public void onSuccess(String responseXML) {
						textArea.setText(responseXML);
						parseResponse(responseXML);
					}

					public void onFailure(Throwable caught) {
						textArea.setText("Some exception got caught ...\n" + caught.getMessage());
					}
				});
			}
			
			private void parseResponse(String xmlRes) {
				String service = UtilityClass.getServiceVer(serviceMenu.getSelectedItemText(), UtilityClass.SERVICE);
				ResponseParser resParser = new ResponseParser(service, xmlRes);
				
				// Populate Cababilities ListBox
				HashSet<String> capabilities = resParser.getCapabilities();
				reqMenu.clear();
				if (capabilities != null && capabilities.size() > 0) {
					for (String i: capabilities) {
						reqMenu.addItem(i);
					}
				}
				vPanel13.setVisible(true);
				
				/*VerticalPanel vPanel21 = new VerticalPanel();
				vPanel21.setSpacing(4);
				vPanel21.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
				ListBox layerMenu = new ListBox(false);
				HashSet<String> layers = resParser.getLyrNames();
				if (layers != null && layers.size() > 0) {
					for (String i: layers) {
						layerMenu.addItem(i);
					}
				}
				
				ListBox queryableMenu = new ListBox(false);
				HashMap<String, Boolean> lyrQueryable = resParser.getLyrQueryable();
				if (lyrQueryable != null && lyrQueryable.size() > 0) {
					for (String key: lyrQueryable.keySet()) {
						if (key == layerMenu.getSelectedItemText() && lyrQueryable.get(key)) {
							queryableMenu.addItem("Yes");
						}
					}
				}*/
				
				/*vPanel21.add(new HTML("Layers"));
				vPanel21.add(layerMenu);
				vPanel21.add(new HTML("Queryable"));
				vPanel21.add(queryableMenu);
				hPanel2.add(vPanel21);*/
			}
		});
	}
}