package com.OGC.WebServices.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.OGC.WebServices.client.ConnectServer;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ConnectServerImpl extends RemoteServiceServlet implements ConnectServer {

	public String sendContent(String[] content) throws IOException {
		StringBuilder strBuf = new StringBuilder();
		try {
		URL url = new URL(content[2]);
		HttpURLConnection conn=(HttpURLConnection)url.openConnection();  
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("HTTP GET Request Failed with Error code : "
                          + conn.getResponseCode());
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String output = null;  
        while ((output = reader.readLine()) != null)  
            strBuf.append(output);
		} catch (IOException ioe) {
			throw new IOException("Some error occured while reading response");
		}
		String prettyXML = strBuf.toString();
		if (!"GetFeatureInfo" .equals(content[1])) {
			prettyXML = prettyPrint(prettyXML);
		}
		return prettyXML;	
	}
	
	private static String prettyPrint(String xmlContent) {		
		if (StringUtils.isBlank(xmlContent)) {
	        throw new RuntimeException("xml was null or blank in prettyPrint()");
	    }
	    final StringWriter sw;
	    try {
	        final OutputFormat format = OutputFormat.createPrettyPrint();
	        final Document document = DocumentHelper.parseText(xmlContent);
	        sw = new StringWriter();
	        final XMLWriter writer = new XMLWriter(sw, format);
	        writer.write(document);
	    }
	    catch (Exception e) {
	        throw new RuntimeException("Error while pretty printing xml:\n" + e);
	    }
	    return sw.toString();
	}
}
