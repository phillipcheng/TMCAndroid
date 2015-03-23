package snae.tmcandroid.traffic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.ArrayMap;
import android.util.Log;
import android.webkit.WebView;

import org.apache.http.HttpHost;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class TMProxySelector extends ProxySelector {

    private String host;
    private int port;
    private List<Proxy> proxyList;

    public TMProxySelector(String phost, int pport) {
        this.host = phost;
        this.port = pport;
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
        proxyList = new ArrayList<Proxy>();
        proxyList.add(proxy);
    }

    @Override
    public List<Proxy> select(URI uri) {
        Log.d(TMManager.LOG_TAG, String.format("hit proxy uri: %s", uri.toString()));
        return proxyList;
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        Log.e(TMManager.LOG_TAG, String.format("failed to connect uri: %s", uri.toString()));
    }
}

public class TMManager {

    public static String LOG_TAG = "SNAE_ANDROID";

    private static final String START_URL = "http://www.doxings.com";//any url
    //private static final String proxyhost = "127.0.0.1";
    private String proxyhost = "52.1.96.115";
    private int proxyport = 8080;
    public static final int RSP_REASON_VAL_SUCCESS = 0;
    public static final int RSP_REASON_VAL_NOREQHEAD = 1;
    public static final int RSP_REASON_VAL_REQHEAD_MISSINGINFO = 2;
    public static final int RSP_REASON_VAL_NOUSER = 3;
    public static final int RSP_REASON_VAL_NOBAL = 4;
    public static final int RSP_REASON_VAL_NOUSERSESSION = 5;
    public static final int RSP_REASON_VAL_USERONLINE = 6;
    public static final int RSP_REASON_NOTSET = -1;
    private int rejectReasonId = RSP_REASON_NOTSET;
    public static final int STATUS_DISCONNECTED = 0;
    public static final int STATUS_CONNECTING = 1;
    public static final int STATUS_CONNECTED = 2;
    public static final int STATUS_DISCONNECTING = 3;
    public static final int STATUS_ERROR = 4;
    //http req custom headers
    private static final String HEADER_CMD = "command";
    private static final String HEADER_CMDVAL_START = "start";
    private static final String HEADER_CMDVAL_STOP = "stop";
    private static final String HEADER_USERID = "userid";
    private static final String HEADER_TENANTID = "tenantid";
    //http rsp custom headers
    private static final String HEADER_REASON = "rejectreason";

    private ProxySelector orgProxySelector;

    private int status = STATUS_DISCONNECTED;

    public TMManager(){
    	
    }
    
    public TMManager(String proxyHost, int proxyPort) {
    	this.proxyhost = proxyHost;
    	this.proxyport = proxyPort;
    }

    private static Object getFieldValueSafely(Field mWebViewCoreField, Object webViewClassic) {
        try {
            mWebViewCoreField.setAccessible(true);
            return mWebViewCoreField.get(webViewClassic);
        } catch (IllegalAccessException e) {
            Log.e(LOG_TAG, "getFieldValueSafely", e);
            return null;
        }
    }

    public static boolean setProxy(WebView webview, String host, int port, String applicationClassName, boolean clearProxy) {

        // 3.2 (HC) or lower
        if (Build.VERSION.SDK_INT <= 13) {
            return setProxyUpToHC(webview, host, port, clearProxy);
        }
        // ICS: 4.0
        else if (Build.VERSION.SDK_INT <= 15) {
            return setProxyICS(webview, host, port, clearProxy);
        }
        // 4.1-4.3 (JB)
        else if (Build.VERSION.SDK_INT <= 18) {
            return setProxyJB(webview, host, port, clearProxy);
        }
        // 4.4 (KK) & 5.0 (Lollipop)
        else {
            return setProxyKKPlus(webview, host, port, applicationClassName, clearProxy);
        }
    }

    /**
     * Set Proxy for Android 3.2 and below.
     */
    @SuppressWarnings("all")
    private static boolean setProxyUpToHC(WebView webview, String host, int port, boolean clearProxy) {
        Log.d(LOG_TAG, "Setting proxy with <= 3.2 API.");

        HttpHost proxyServer = new HttpHost(host, port);
        // Getting network
        Class networkClass = null;
        Object network = null;
        try {
            networkClass = Class.forName("android.webkit.Network");
            if (networkClass == null) {
                Log.e(LOG_TAG, "failed to get class for android.webkit.Network");
                return false;
            }
            Method getInstanceMethod = networkClass.getMethod("getInstance", Context.class);
            if (getInstanceMethod == null) {
                Log.e(LOG_TAG, "failed to get getInstance method");
            }
            network = getInstanceMethod.invoke(networkClass, new Object[]{webview.getContext()});
        } catch (Exception ex) {
            Log.e(LOG_TAG, "error getting network: " + ex);
            return false;
        }
        if (network == null) {
            Log.e(LOG_TAG, "error getting network: network is null");
            return false;
        }
        Object requestQueue = null;
        try {
            Field requestQueueField = networkClass
                    .getDeclaredField("mRequestQueue");
            requestQueue = getFieldValueSafely(requestQueueField, network);
        } catch (Exception ex) {
            Log.e(LOG_TAG, "error getting field value");
            return false;
        }
        if (requestQueue == null) {
            Log.e(LOG_TAG, "Request queue is null");
            return false;
        }
        Field proxyHostField = null;
        try {
            Class requestQueueClass = Class.forName("android.net.http.RequestQueue");
            proxyHostField = requestQueueClass
                    .getDeclaredField("mProxyHost");
        } catch (Exception ex) {
            Log.e(LOG_TAG, "error getting proxy host field");
            return false;
        }

        boolean temp = proxyHostField.isAccessible();
        try {
            proxyHostField.setAccessible(true);
            proxyHostField.set(requestQueue, clearProxy ? null : proxyServer);
        } catch (Exception ex) {
            Log.e(LOG_TAG, "error setting proxy host");
        } finally {
            proxyHostField.setAccessible(temp);
        }

        Log.d(LOG_TAG, "Setting proxy with <= 3.2 API successful!");
        return true;
    }

    @SuppressWarnings("all")
    private static boolean setProxyICS(WebView webview, String host, int port, boolean clearProxy) {
        try {
            Log.d(LOG_TAG, "Setting proxy with 4.0 API.");

            Class jwcjb = Class.forName("android.webkit.JWebCoreJavaBridge");
            Class params[] = new Class[1];
            params[0] = Class.forName("android.net.ProxyProperties");
            Method updateProxyInstance = jwcjb.getDeclaredMethod("updateProxy", params);

            Class wv = Class.forName("android.webkit.WebView");
            Field mWebViewCoreField = wv.getDeclaredField("mWebViewCore");
            Object mWebViewCoreFieldInstance = getFieldValueSafely(mWebViewCoreField, webview);

            Class wvc = Class.forName("android.webkit.WebViewCore");
            Field mBrowserFrameField = wvc.getDeclaredField("mBrowserFrame");
            Object mBrowserFrame = getFieldValueSafely(mBrowserFrameField, mWebViewCoreFieldInstance);

            Class bf = Class.forName("android.webkit.BrowserFrame");
            Field sJavaBridgeField = bf.getDeclaredField("sJavaBridge");
            Object sJavaBridge = getFieldValueSafely(sJavaBridgeField, mBrowserFrame);

            Class ppclass = Class.forName("android.net.ProxyProperties");
            Class pparams[] = new Class[3];
            pparams[0] = String.class;
            pparams[1] = int.class;
            pparams[2] = String.class;
            Constructor ppcont = ppclass.getConstructor(pparams);

            updateProxyInstance.invoke(sJavaBridge, new Object[]{clearProxy ? null : ppcont.newInstance(host, port, null)});

            Log.d(LOG_TAG, "Setting proxy with 4.0 API successful!");
            return true;
        } catch (Exception ex) {
            Log.e(LOG_TAG, "failed to set HTTP proxy: " + ex);
            return false;
        }
    }

    /**
     * Set Proxy for Android 4.1 - 4.3.
     */
    @SuppressWarnings("all")
    private static boolean setProxyJB(WebView webview, String host, int port, boolean clearProxy) {
        Log.d(LOG_TAG, "Setting proxy with 4.1 - 4.3 API.");

        try {
            Class wvcClass = Class.forName("android.webkit.WebViewClassic");
            Class wvParams[] = new Class[1];
            wvParams[0] = Class.forName("android.webkit.WebView");
            Method fromWebView = wvcClass.getDeclaredMethod("fromWebView", wvParams);
            Object webViewClassic = fromWebView.invoke(null, webview);

            Class wv = Class.forName("android.webkit.WebViewClassic");
            Field mWebViewCoreField = wv.getDeclaredField("mWebViewCore");
            Object mWebViewCoreFieldInstance = getFieldValueSafely(mWebViewCoreField, webViewClassic);

            Class wvc = Class.forName("android.webkit.WebViewCore");
            Field mBrowserFrameField = wvc.getDeclaredField("mBrowserFrame");
            Object mBrowserFrame = getFieldValueSafely(mBrowserFrameField, mWebViewCoreFieldInstance);

            Class bf = Class.forName("android.webkit.BrowserFrame");
            Field sJavaBridgeField = bf.getDeclaredField("sJavaBridge");
            Object sJavaBridge = getFieldValueSafely(sJavaBridgeField, mBrowserFrame);

            Class ppclass = Class.forName("android.net.ProxyProperties");
            Class pparams[] = new Class[3];
            pparams[0] = String.class;
            pparams[1] = int.class;
            pparams[2] = String.class;
            Constructor ppcont = ppclass.getConstructor(pparams);

            Class jwcjb = Class.forName("android.webkit.JWebCoreJavaBridge");
            Class params[] = new Class[1];
            params[0] = Class.forName("android.net.ProxyProperties");
            Method updateProxyInstance = jwcjb.getDeclaredMethod("updateProxy", params);

            updateProxyInstance.invoke(sJavaBridge, new Object[]{clearProxy ? null : ppcont.newInstance(host, port, null)});
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Setting proxy with >= 4.1 API failed with error: " + ex.getMessage());
            return false;
        }

        Log.d(LOG_TAG, "Setting proxy with 4.1 - 4.3 API successful!");
        return true;
    }

    // from https://stackoverflow.com/questions/19979578/android-webview-set-proxy-programatically-kitkat
    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    private static boolean setProxyKKPlus(WebView webView, String host, int port, String applicationClassName, boolean clearProxy) {
        Log.d(LOG_TAG, "Setting proxy with >= 4.4 API.");

        Context appContext = webView.getContext().getApplicationContext();
        if (clearProxy) {
            System.clearProperty("http.proxyHost");
            System.clearProperty("http.proxyPort");
            System.clearProperty("https.proxyHost");
            System.clearProperty("https.proxyPort");
        } else {
            System.setProperty("http.proxyHost", host);
            System.setProperty("http.proxyPort", port + "");
            System.setProperty("https.proxyHost", host);
            System.setProperty("https.proxyPort", port + "");
        }
        try {
            Class applictionCls = Class.forName(applicationClassName);
            Field loadedApkField = applictionCls.getField("mLoadedApk");
            loadedApkField.setAccessible(true);
            Object loadedApk = loadedApkField.get(appContext);
            Class loadedApkCls = Class.forName("android.app.LoadedApk");
            Field receiversField = loadedApkCls.getDeclaredField("mReceivers");
            receiversField.setAccessible(true);
            ArrayMap receivers = (ArrayMap) receiversField.get(loadedApk);
            for (Object receiverMap : receivers.values()) {
                for (Object rec : ((ArrayMap) receiverMap).keySet()) {
                    Class clazz = rec.getClass();
                    if (clazz.getName().contains("ProxyChangeListener")) {
                        Method onReceiveMethod = clazz.getDeclaredMethod("onReceive", Context.class, Intent.class);
                        Intent intent = new Intent(android.net.Proxy.PROXY_CHANGE_ACTION);

                        onReceiveMethod.invoke(rec, appContext, intent);
                    }
                }
            }

            Log.d(LOG_TAG, "Setting proxy with >= 4.4 API successful!");
            return true;
        } catch (ClassNotFoundException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(LOG_TAG, e.getMessage());
            Log.v(LOG_TAG, exceptionAsString);
        } catch (NoSuchFieldException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(LOG_TAG, e.getMessage());
            Log.v(LOG_TAG, exceptionAsString);
        } catch (IllegalAccessException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(LOG_TAG, e.getMessage());
            Log.v(LOG_TAG, exceptionAsString);
        } catch (IllegalArgumentException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(LOG_TAG, e.getMessage());
            Log.v(LOG_TAG, exceptionAsString);
        } catch (NoSuchMethodException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(LOG_TAG, e.getMessage());
            Log.v(LOG_TAG, exceptionAsString);
        } catch (InvocationTargetException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(LOG_TAG, e.getMessage());
            Log.v(LOG_TAG, exceptionAsString);
        }
        return false;
    }

    

    public boolean setProxy(WebView webview) {
        String applicationClassName = "android.app.Application";
        return setProxy(webview, applicationClassName);
    }

    public boolean clearProxy(WebView webview) {
        String applicationClassName = "android.app.Application";
        return clearProxy(webview, applicationClassName);
    }

    public boolean setProxy(WebView webview, String applicationClassName) {
        return setProxy(webview, proxyhost, proxyport, applicationClassName, false);
    }

    public boolean clearProxy(WebView webview, String applicationClassName) {
        return setProxy(webview, proxyhost, proxyport, applicationClassName, true);
    }
    
    public boolean start(String userId, String tenantId) {
    	orgProxySelector = ProxySelector.getDefault();
        ProxySelector.setDefault(new TMProxySelector(proxyhost, proxyport));

        setStatus(STATUS_CONNECTING);
        HttpURLConnection con = null;

        try {
            URL url = new URL(START_URL);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty(HEADER_CMD, HEADER_CMDVAL_START);
            con.setRequestProperty(HEADER_USERID, userId);
            con.setRequestProperty(HEADER_TENANTID, tenantId);
            con.setRequestMethod("GET");
            int code = con.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                setStatus(STATUS_CONNECTED);
                return true;
            } else {
                rejectReasonId = con.getHeaderFieldInt(HEADER_REASON, RSP_REASON_NOTSET);
                setStatus(STATUS_ERROR);
                return false;
            }
        } catch (Exception e) {
            setStatus(STATUS_ERROR);
            Log.e(LOG_TAG, "exeption in start session.", e);
            return false;
        } finally {
            if (status == STATUS_ERROR) {
                ProxySelector.setDefault(orgProxySelector);
            }
        }
        
    }
    
    public boolean end() {
        setStatus(STATUS_DISCONNECTING);
        HttpURLConnection con = null;
        try {
            URL url = new URL(START_URL);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty(HEADER_CMD, HEADER_CMDVAL_STOP);
            con.setRequestMethod("GET");
            int code = con.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                setStatus(STATUS_DISCONNECTED);
                return true;
            } else {
                rejectReasonId = con.getHeaderFieldInt(HEADER_REASON, RSP_REASON_NOTSET);
                setStatus(STATUS_ERROR);
                return false;
            }
        } catch (Exception e) {
            setStatus(STATUS_ERROR);
            Log.e(LOG_TAG, "exception in closing session.", e);
            return false;
        } finally {
            ProxySelector.setDefault(orgProxySelector);
        }
    }

    public int getStatus() {
        return status;
    }

    private void setStatus(int status) {
        this.status = status;

        Log.w(LOG_TAG, "new status:" + status);
    }

    public int getRejectReasonId() {
        return rejectReasonId;
    }

    public void setRejectReasonId(int rejectReasonId) {
        this.rejectReasonId = rejectReasonId;
    }

}
