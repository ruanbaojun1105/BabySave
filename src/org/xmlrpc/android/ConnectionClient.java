package org.xmlrpc.android;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import mybaby.util.TrustAllSSLSocketFactory;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;



public class ConnectionClient extends DefaultHttpClient {
    public ConnectionClient(Credentials cred,HttpParams httpParams,SchemeRegistry registry) {
        super(new ThreadSafeClientConnManager(httpParams, registry), httpParams);
        setCredentials(cred);
        HttpConnectionParams.setConnectionTimeout(this.getParams(), 15000);
    }

    public ConnectionClient(Credentials cred, int port,HttpParams httpParams,SchemeRegistry registry) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        super(new ThreadSafeClientConnManager(httpParams, registry), httpParams);
        //registerTrustAllScheme(port);
        setCredentials(cred);
    }

    private void registerTrustAllScheme(int port) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        TrustAllSSLSocketFactory tasslf = new TrustAllSSLSocketFactory();
        Scheme sch = new Scheme("https", tasslf, port);
        getConnectionManager().getSchemeRegistry().register(sch);
    }

    private void setCredentials(Credentials cred) {
        BasicCredentialsProvider cP = new BasicCredentialsProvider();
        cP.setCredentials(AuthScope.ANY, cred);
        setCredentialsProvider(cP);
    }
}
