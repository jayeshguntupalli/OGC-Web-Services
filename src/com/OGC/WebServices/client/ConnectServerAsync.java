package com.OGC.WebServices.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ConnectServerAsync {
	void sendContent(String[] content, AsyncCallback<String> callback) throws IllegalArgumentException;
}
