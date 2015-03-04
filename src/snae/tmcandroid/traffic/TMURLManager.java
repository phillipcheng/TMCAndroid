package snae.tmcandroid.traffic;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;


public class TMURLManager {
	
	private static final String START_URL="http://www.google.com";//any url
	
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

	private Proxy proxy;
	private int rejectReasonId = RSP_REASON_NOTSET;
	
	public static int STATUS_DISCONNECTED=0;
	public static int STATUS_CONNECTING=1;
	public static int STATUS_CONNECTED=2;
	public static int STATUS_DISCONNECTING=3;
	public static int STATUS_ERROR=4;
	private int status;

	public TMURLManager(){
		proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyhost, proxyport));
	}
	
	public boolean start(String userId, String tenantId){
		setStatus(STATUS_CONNECTING);
		HttpURLConnection con = null;
		
		try {
			URL url = new URL(START_URL);
			con = (HttpURLConnection) url.openConnection(proxy);
			con.setRequestProperty(HEADER_CMD, HEADER_CMDVAL_START);
			con.setRequestProperty(HEADER_USERID, userId);
			con.setRequestProperty(HEADER_TENANTID, tenantId);
			con.setRequestMethod("GET");
	        int code = con.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
            	InputStream is = con.getInputStream();
            	try{
            		setStatus(STATUS_ERROR);
            		return false;
            	}finally{
	            	if (is!=null){
	    				try{
	    					is.close();
	    				}catch(Exception e){
	    					;
	    				}
	    			}
            	}
            }else{
            	rejectReasonId = con.getHeaderFieldInt(HEADER_REASON, RSP_REASON_NOTSET);
            	setStatus(STATUS_ERROR);
            	return false;
            }
		}catch(Exception e){
			setStatus(STATUS_ERROR);
			return false;
		}
	}
	
	public boolean end(){
		setStatus(STATUS_DISCONNECTING);
		HttpURLConnection con = null;
		InputStream is = null;
		try {
			URL url = new URL(START_URL);
			con = (HttpURLConnection) url.openConnection(proxy);
			con.setRequestProperty(HEADER_CMD, HEADER_CMDVAL_STOP);
			con.setRequestMethod("GET");
	        is = con.getInputStream();
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
			return false;
		}finally{
			if (is!=null){
				try{
					is.close();
				}catch(Exception e){
					;
				}
			}
		}
	}
	
	public TMURL getUrl(String strUrl) throws MalformedURLException{
		URL url = new URL(strUrl);
		return new TMURL(url, proxy);
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
