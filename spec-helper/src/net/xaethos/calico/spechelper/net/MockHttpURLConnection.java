package net.xaethos.calico.spechelper.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.xaethos.calico.spechelper.net.MockURLStreamHandler.RequestHandler;

public class MockHttpURLConnection extends HttpURLConnection
{

    protected MockHttpURLConnection(URL url, RequestHandler handler) {
        super(url);

        mRequestHandler = handler;
        mHeaders = new HashMap<String, List<String>>();
    }

    private final RequestHandler mRequestHandler;
    private final HashMap<String, List<String>> mHeaders;

    // *** Headers

    protected List<String> getRequestPropertyList(String field) {
        List<String> properties = mHeaders.get(field);
        if (properties == null) {
            properties = new ArrayList<String>();
            mHeaders.put(field, properties);
        }
        return properties;
    }

    @Override
    public String getRequestProperty(String field) {
        if (mHeaders.containsKey(field)) {
            return mHeaders.get(field).get(0);
        }
        return null;
    }

    @Override
    public Map<String, List<String>> getRequestProperties() {
        return mHeaders;
    }

    @Override
    public void addRequestProperty(String field, String newValue) {
        List<String> properties = getRequestPropertyList(field);
        properties.add(newValue);
    }

    @Override
    public void setRequestProperty(String field, String newValue) {
        List<String> properties = getRequestPropertyList(field);
        properties.clear();
        properties.add(newValue);
    }

    // *** HttpURLConnection

    @Override
    public InputStream getInputStream() throws IOException {
        return mRequestHandler.getResponseStream();
    }

    @Override
    public void connect() throws IOException {
    }

    @Override
    public void disconnect() {
    }

    @Override
    public boolean usingProxy() {
        return false;
    }

}
