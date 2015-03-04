package snae.tmcandroid.traffic;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

public class TMURL {
	
	URL url;
	Proxy proxy = null;
	
	public TMURL(URL url, Proxy proxy){
		this.url = url;
		this.proxy = proxy;
	}
	
	//for normal access
	public TMURL(String url) throws MalformedURLException{
		this.url = new URL(url);
	}
	
	public URL getUrl(){
		return url;
	}
	
	public HttpURLConnection getHttpUrlConnection() throws IOException{
		if (proxy==null){
			return (HttpURLConnection) url.openConnection();
		}else{
			HttpURLConnection con = (HttpURLConnection) url.openConnection(proxy);
			return con;
		}
	}
}
