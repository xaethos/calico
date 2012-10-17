package net.xaethos.lib.calico.spechelper.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.xaethos.lib.calico.integration.R;
import net.xaethos.lib.calico.spechelper.net.URLConnectionMocker.RequestHandler;
import android.test.InstrumentationTestCase;

public class MockHttpURLConnectionTest extends InstrumentationTestCase
{
    URLConnectionMocker urlMocker;
    URL url;
    RequestHandler requestHandler;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        urlMocker = URLConnectionMocker.startMocking();
        url = new URL("http://www.example.com");
        requestHandler = urlMocker.addRequest(url);
    }

    @Override
    protected void tearDown() throws Exception {
        urlMocker.finishMocking();
        super.tearDown();
    }

    public void testInputStream() throws Exception {
        requestHandler.setResponseStream(
                getInstrumentation(),
                R.raw.sample_text);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(conn.getInputStream()));
        assertEquals("Lorem ipsum", reader.readLine());
        assertEquals(200, conn.getResponseCode());
    }

    public void testResponseCode() throws Exception {
        requestHandler.setResponseCode(304);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        assertEquals(304, conn.getResponseCode());
    }

    public void testRequestProperties() throws Exception {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        Map<String, List<String>> props;

        conn.addRequestProperty("foo", "foo");
        conn.addRequestProperty("foo", "bar");
        assertEquals("bar", conn.getRequestProperty("foo"));

        props = conn.getRequestProperties();
        assertEquals(2, props.get("foo").size());
        assertEquals("foo", props.get("foo").get(0));
        assertEquals("bar", props.get("foo").get(1));

        conn.setRequestProperty("foo", "cat");
        assertEquals("cat", conn.getRequestProperty("foo"));

        props = conn.getRequestProperties();
        assertEquals(1, props.get("foo").size());
        assertEquals("cat", props.get("foo").get(0));

        // assert unmodifiable
        try {
            props.put("foo", Arrays.asList("dog"));
            fail("Request properties map should be unmodifiable");
        }
        catch(UnsupportedOperationException e) {}

        try {
            props.get("foo").add("dog");
            fail("Request properties values should be unmodifiable");
        }
        catch(UnsupportedOperationException e) {}
    }

}
