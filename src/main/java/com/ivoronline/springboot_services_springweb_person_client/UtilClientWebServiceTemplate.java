package com.ivoronline.springboot_services_springweb_person_client;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.ws.transport.WebServiceMessageSender;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;
import javax.net.ssl.SSLContext;
import java.security.KeyStore;

public class UtilClientWebServiceTemplate {

  //==========================================================================
  // GET WEB SERVICE MESSAGE SENDER
  //==========================================================================
  static public WebServiceMessageSender getWebServiceMessageSender(
    String trustStoreName,
    String trustStorePassword,
    String trustStoreType
  ) throws Exception {

    //LOAD TRUST STORE
    KeyStore trustStore = UtilKeys.getStore(trustStoreName, trustStorePassword, trustStoreType);

    //CONFIGURE WEB SERVICE TEMPLATE
    SSLContext sslContext = new SSLContextBuilder()
      .loadTrustMaterial(trustStore, null)  //For One-Way TLS
      .build();

    SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
      sslContext,
      NoopHostnameVerifier.INSTANCE
    );

    CloseableHttpClient httpClient= HttpClients
      .custom()
      .setSSLSocketFactory(socketFactory)
      .addInterceptorFirst(new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor())
      .build();

    HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender(httpClient);
    WebServiceMessageSender     sender        = messageSender;

    //RETURN SENDER
    return sender;

  }

}
