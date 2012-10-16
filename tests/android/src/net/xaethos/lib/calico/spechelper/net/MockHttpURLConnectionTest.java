package net.xaethos.lib.calico.spechelper.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

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

}
