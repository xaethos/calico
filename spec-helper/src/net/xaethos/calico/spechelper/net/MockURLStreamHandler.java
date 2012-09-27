package net.xaethos.calico.spechelper.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Instrumentation;
import android.content.res.Resources;

public class MockURLStreamHandler extends URLStreamHandler
    implements
        URLStreamHandlerFactory
{

    public MockURLStreamHandler() {
        mRequestHandlers =
                new HashMap<URL, MockURLStreamHandler.RequestHandler>();
        mConnections = new ArrayList<MockHttpURLConnection>();
    }

    // **********
    // *** Instance

    private final HashMap<URL, RequestHandler> mRequestHandlers;
    private final ArrayList<MockHttpURLConnection> mConnections;

    // *** Request handling

    public RequestHandler addRequest(String urlString)
        throws MalformedURLException
    {
        return addRequest(new URL(urlString));
    }

    public RequestHandler addRequest(URL url) {
        RequestHandler requestHandler = new RequestHandler();
        mRequestHandlers.put(url, requestHandler);
        return requestHandler;
    }

    public URLConnection getConnection() {
        int count = mConnections.size();
        if (count == 0) return null;
        return mConnections.get(count - 1);
    }

    // *** URLStreamHandler

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        if (!mRequestHandlers.containsKey(u)) {
            throw new RuntimeException("Unhandled URLConnection request: " + u);
        }

        MockHttpURLConnection mockConnection =
                new MockHttpURLConnection(u, mRequestHandlers.get(u));
        mConnections.add(mockConnection);

        return mockConnection;
    }

    // *** URLStreamHandlerFactory

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        return this;
    }

    // **********
    // *** Inner classes

    public class RequestHandler
    {
        private Resources mResources;
        private int mResId;

        public void setResponseStream(Instrumentation instr, int resid) {
            setResponseStream(instr.getContext().getResources(), resid);
        }

        public void setResponseStream(Resources res, int resid) {
            mResources = res;
            mResId = resid;
        }

        public InputStream getResponseStream() {
            return mResources.openRawResource(mResId);
        }
    }

}
