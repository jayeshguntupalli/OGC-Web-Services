package com.OGC.WebServices.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.spi.ObjectFactoryBuilder;

import com.OGC.WebServices.shared.UtilityClass;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;

public class ResponseParser {
	private static Logger logger = Logger.getLogger("ResponseParser");
	private HashSet<String> lyrNames;
	private HashMap<String, HashSet<String>> lyrStyles;
	private HashMap<String, Boolean> lyrQueryable;
	private HashMap<String, HashSet<String>> lyrSRSs;
	private HashMap<String, HashSet<String>> lyrFormats;
	private HashMap<String, ArrayList<String>> lyrBBox;
	private HashMap<String, HashSet<String>> featOperations;
	private HashSet<String> capabilities;
	
	@SuppressWarnings("unused")
	private ResponseParser() {}
	
	ResponseParser(String service, String resXML) {
		Document parse = XMLParser.parse(resXML);
		this.capabilities = new HashSet<>();
		if (service == UtilityClass.WMS) {
			NodeList request = parse.getElementsByTagName("Request");
			NodeList reqServices = request.item(0).getChildNodes();
			for (int i=0; i<reqServices.getLength(); i++) {
				Node node = reqServices.item(i);
				String nodeName = node.getNodeName();
				if (!(node instanceof Text || nodeName == UtilityClass.GET_CAPABILITIES)) {
					this.capabilities.add(nodeName);
				}
			}
		} else {
			NodeList operation = parse.getElementsByTagName("ows:OperationsMetadata");
			NodeList childs = operation.item(0).getChildNodes();
			for (int i=0; i<childs.getLength(); i++) {
				Node node = childs.item(i);
				if (!(node instanceof Text)) {
					String val = node.getAttributes().getNamedItem("name").getNodeValue();
					if (val != UtilityClass.GET_CAPABILITIES) {
						this.capabilities.add(val);
					}
				}
			}
		}
		if (service == UtilityClass.WMS) {/*
			NodeList capNodes = parse.getElementsByTagName("Capability");
			NodeList capChildNodes = capNodes.item(0).getChildNodes();
			for (int i=0; i<capChildNodes.getLength(); i++) {
				Node capChild = capChildNodes.item(i);
				String capChildName = capChild.getNodeName();
				if (capChildName == "Layer") {
					NodeList layerNodes = capChild.getChildNodes();
					for (int j=0; j<layerNodes.getLength(); j++) {
						Node layerNode = layerNodes.item(j);
						if (!(layerNode instanceof Text)) {
							logger.log(record);
							String queryable = layerNode.getAttributes().getNamedItem("queryable").getNodeValue();
							
							NodeList layerChilds = layerNode.getChildNodes();
							for (int k=0; i<layerChilds.getLength(); k++) {
								Node layerChild = layerChilds.item(k);
								if (!(layerChild instanceof Text)) {
									this.lyrNames = new HashSet<>();
									String layerChildName = layerChild.getNodeName();
									String nameVal = "";
									if (layerChildName == "Name") {
										nameVal = layerChild.getNodeValue();
										this.lyrNames.add(nameVal);
										this.lyrQueryable = new HashMap<>();
										if (queryable == "1") {
											this.lyrQueryable.put(nameVal, true);
										} else {
											this.lyrQueryable.put(nameVal, true);
										}
									}
								}
							}
						}
					}
					break;
				}
			}
			
			this.lyrNames = new HashSet<>();
			this.lyrFormats = new HashMap<>();
			this.lyrBBox=new HashMap<>();
		*/} else if (service == UtilityClass.WFS) {
			
		}
		
	}

	public HashSet<String> getLyrNames() {
		return lyrNames;
	}

	public HashMap<String, HashSet<String>> getLyrStyles() {
		return lyrStyles;
	}

	public HashMap<String, Boolean> getLyrQueryable() {
		return lyrQueryable;
	}

	public HashMap<String, HashSet<String>> getLyrSRSs() {
		return lyrSRSs;
	}

	public HashMap<String, HashSet<String>> getLyrFormats() {
		return lyrFormats;
	}

	public HashMap<String, ArrayList<String>> getLyrBBox() {
		return lyrBBox;
	}

	public HashSet<String> getCapabilities() {
		return capabilities;
	}

	public HashMap<String, HashSet<String>> getFeatOperations() {
		return featOperations;
	}
	
}
