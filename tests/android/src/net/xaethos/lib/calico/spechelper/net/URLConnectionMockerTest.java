package net.xaethos.lib.calico.spechelper.net;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import junit.framework.TestCase;

public class URLConnectionMockerTest extends TestCase
{

    URLConnectionMocker urlMocker;

    public void testStartMocking() throws Exception {
        URLConnection conn;
        URL url;

        url = new URL("http://example.com/");
        conn = url.openConnection();
        assertFalse(conn instanceof MockHttpURLConnection);

        urlMocker = URLConnectionMocker.startMocking();
        urlMocker.addRequest(url);

        url = new URL("http://example.com/");
        conn = url.openConnection();
        assertTrue(conn instanceof MockHttpURLConnection);

        urlMocker.finishMocking();
    }

    public void testHandlesExistingFactory() throws Exception {
        URLStreamHandlerFactory dummyFactory = new URLStreamHandlerFactory() {
            @Override
            public URLStreamHandler createURLStreamHandler(String protocol) {
                return null;
            }
        };

        Field factoryField = URL.class.getDeclaredField("streamHandlerFactory");
        factoryField.setAccessible(true);
        assertNull(factoryField.get(null));

        URL.setURLStreamHandlerFactory(dummyFactory);
        assertSame(dummyFactory, factoryField.get(null));

        urlMocker = URLConnectionMocker.startMocking();
        assertSame(urlMocker, factoryField.get(null));

        urlMocker.finishMocking();
        assertSame(dummyFactory, factoryField.get(URL.class));
    }

    public void testFinishMocking() throws Exception {
        URLConnection conn;
        URL url;

        urlMocker = URLConnectionMocker.startMocking();

        url = new URL("http://example.com/");
        urlMocker.addRequest(url);

        conn = url.openConnection();
        assertTrue(conn instanceof MockHttpURLConnection);

        urlMocker.finishMocking();
        url = new URL("http://example.com/");
        conn = url.openConnection();
        assertFalse(conn instanceof MockHttpURLConnection);
    }

    public void testHandlesRequests() throws Exception {
        urlMocker = URLConnectionMocker.startMocking();
        urlMocker.addRequest("http://example.com/");

        URL url = new URL("http://example.com/");
        assertTrue(url.openConnection() instanceof MockHttpURLConnection);

        urlMocker.finishMocking();
    }

    public void testThrowsOnUnhandledRequest() throws Exception {
        urlMocker = URLConnectionMocker.startMocking();

        URL url = new URL("http://example.com/");
        try {
            url.openConnection();
            fail("Exception expected");
        }
        catch (RuntimeException e) {
            // All is good
        }

        urlMocker.finishMocking();
    }

}
