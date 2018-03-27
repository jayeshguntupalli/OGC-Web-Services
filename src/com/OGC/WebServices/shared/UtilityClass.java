package com.OGC.WebServices.shared;

import org.gwtopenmaps.openlayers.client.Projection;

public class UtilityClass {
	
	 // The message displayed to the user when the server cannot be reached or returns an error.
	 public static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network " + "connection and try again.";
	 public static final Projection DEFAULT_PROJECTION = new Projection("EPSG:4326");
	 public static final String SERVICE = "service";
	 public static final String VERSION = "version";
	 public static final String WMS = "WMS";
	 public static final String WFS = "WFS";
	 public static final String WCS = "WCS";
	 public static final String GET_CAPABILITIES = "GetCapabilities";
	 
	public static String getServiceVer(String param, String name) {
		String[] strValues = param.split("-");
		if (name == SERVICE) {
			return strValues[0];
		} else {
			return strValues[1];
		}
	}
}
