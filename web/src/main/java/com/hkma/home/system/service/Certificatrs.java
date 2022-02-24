package com.hkma.home.system.service;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Certificatrs {
	public static void trustAllHttpsCertificates() throws Exception {
        TrustManager[] trustManagerArray = new TrustManager[1];
        trustManagerArray[0] = new X509TrustManager(){
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
        };
        
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustManagerArray, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
}
