package snae.tmcandroid.traffic;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

class TMProxySelector extends ProxySelector{

	private String host;
	private int port;
	private List<Proxy> proxyList;
	
	public TMProxySelector(String phost, int pport){
		this.host = phost;
		this.port = pport;
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
		proxyList = new ArrayList<Proxy>();
		proxyList.add(proxy);
	}

	@Override
	public List<Proxy> select(URI uri) {
		return proxyList;
	}

	@Override
	public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
		Log.e(TMManager.LOG_TAG, String.format("failed to connect uri: %s", uri.toString()));
	}
}

public class TMManager {
	public static String LOG_TAG="SNAE_ANDROID";
	
	private static final String START_URL="http://www.doxings.com";//any url
	
	//private static final String proxyhost = "127.0.0.1";
	private static final String proxyhost = "52.1.96.115";
	private static final int proxyport = 8080;
	
	//http req custom headers
	private static String HEADER_CMD = "command";
	private static String HEADER_CMDVAL_START = "start";
	private static String HEADER_CMDVAL_STOP = "stop";
	
	private static String HEADER_USERID = "userid";
	private static String HEADER_TENANTID= "tenantid";
	
	//http rsp custom headers
	private static String HEADER_REASON = "rejectreason";
	public static int RSP_REASON_VAL_SUCCESS=0;
	public static int RSP_REASON_VAL_NOREQHEAD=1;
	public static int RSP_REASON_VAL_REQHEAD_MISSINGINFO=2;
	public static int RSP_REASON_VAL_NOUSER=3;
	public static int RSP_REASON_VAL_NOBAL=4;
	public static int RSP_REASON_VAL_NOUSERSESSION=5;
	public static int RSP_REASON_VAL_USERONLINE=6;
	public static int RSP_REASON_NOTSET=-1;

	private ProxySelector orgProxySelector;
	private int rejectReasonId = RSP_REASON_NOTSET;
	
	public static int STATUS_DISCONNECTED=0;
	public static int STATUS_CONNECTING=1;
	public static int STATUS_CONNECTED=2;
	public static int STATUS_DISCONNECTING=3;
	public static int STATUS_ERROR=4;
	private int status;

	public TMManager(){
	}
	
	public boolean start(String userId, String tenantId){
		
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
            }else{
            	rejectReasonId = con.getHeaderFieldInt(HEADER_REASON, RSP_REASON_NOTSET);
            	setStatus(STATUS_ERROR);
            	return false;
            }
		}catch(Exception e){
			setStatus(STATUS_ERROR);
			Log.e(LOG_TAG, "exeption in start session.", e);
			return false;
		}finally{
			if (status == STATUS_ERROR){
				ProxySelector.setDefault(orgProxySelector);
			}
		}
	}
	
	public boolean end(){
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
            }else{
            	rejectReasonId = con.getHeaderFieldInt(HEADER_REASON, RSP_REASON_NOTSET);
        		setStatus(STATUS_ERROR);
        		return false;
            }
		}catch(Exception e){
			setStatus(STATUS_ERROR);
			Log.e(LOG_TAG, "exception in closing session.", e);
			return false;
		}finally{
			ProxySelector.setDefault(orgProxySelector);
		}
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getRejectReasonId() {
		return rejectReasonId;
	}

	public void setRejectReasonId(int rejectReasonId) {
		this.rejectReasonId = rejectReasonId;
	}

}
