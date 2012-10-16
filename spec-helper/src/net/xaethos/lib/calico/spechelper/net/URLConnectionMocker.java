package net.xaethos.lib.calico.spechelper.net;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import android.app.Instrumentation;
import android.content.res.Resources;

public class URLConnectionMocker extends URLStreamHandler
    implements
        URLStreamHandlerFactory
{

    @SuppressWarnings("unchecked")
    public static URLConnectionMocker startMocking() throws Exception {
        Field factoryField = URL.class.getDeclaredField("streamHandlerFactory");
        factoryField.setAccessible(true);
        URLStreamHandlerFactory oldFactory =
                (URLStreamHandlerFactory) factoryField.get(null);
        factoryField.set(null, null);
        factoryField.setAccessible(false);

        Field handlersField = URL.class.getDeclaredField("streamHandlers");
        handlersField.setAccessible(true);
        ((Hashtable<String, URLStreamHandler>) handlersField.get(null)).clear();
        handlersField.setAccessible(false);

        URLConnectionMocker mocker = new URLConnectionMocker(oldFactory);
        URL.setURLStreamHandlerFactory(mocker);

        return mocker;
    }

    private URLConnectionMocker(URLStreamHandlerFactory oldFactory) {
        mOldFactory = oldFactory;
        mRequestHandlers =
                new HashMap<URL, URLConnectionMocker.RequestHandler>();
        mConnections = new ArrayList<MockHttpURLConnection>();
    }

    // **********
    // *** Instance

    private final URLStreamHandlerFactory mOldFactory;

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

    @SuppressWarnings("unchecked")
    public void finishMocking() throws Exception {
        Field factoryField = URL.class.getDeclaredField("streamHandlerFactory");
        factoryField.setAccessible(true);
        factoryField.set(null, mOldFactory);
        factoryField.setAccessible(false);

        Field handlersField = URL.class.getDeclaredField("streamHandlers");
        handlersField.setAccessible(true);
        ((Hashtable<String, URLStreamHandler>) handlersField.get(null)).clear();
        handlersField.setAccessible(false);
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
        public RequestHandler() {
            mResponseCode = 200;
        }

        private int mResponseCode;

        private Resources mResources;
        private int mResId;

        public void setResponseStream(Instrumentation instr, int resid) {
            setResponseStream(instr.getContext().getResources(), resid);
        }

        public int getResponseCode() {
            return mResponseCode;
        }

        public void setResponseCode(int code) {
            mResponseCode = code;
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
