package com.ivoronline.springboot_services_springweb_person_client;

import com.ivoronline.soap.GetPersonRequest;
import com.ivoronline.soap.GetPersonResponse;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.transport.WebServiceMessageSender;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

//https://newbedev.com/how-do-i-use-an-ssl-client-certificate-with-apache-httpclient
@Component
public class PersonClient extends WebServiceGatewaySupport {

  //PROPERTIES
  String generatedClasses   = "com.ivoronline.soap";
  String webServiceEndpoint = "https://localhost:8080/PersonWebService";

  //CLIENT TRUST STORE
  static String clientTrustStoreName     = "ClientTrustStore.jks";
  static String clientTrustStoreType     = "JKS";
  static String clientTrustStorePassword = "mypassword";

  //==========================================================================
  // GET PERSON
  //==========================================================================
  // Input Parameters are only used to create Custom Request Object.
  // Request Object will be marshalled into XML Request.
  // SOAP Envelope, Header and Body will be added around generated XML Request.
  // This is all done by calling marshalSendAndReceive().
  public GetPersonResponse getPerson(Integer id) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException, CertificateException {

    //CREATE REQUEST OBJECT
    GetPersonRequest getPersonRequest = new GetPersonRequest();
                     getPersonRequest.setId(id);  //Set some Parameters

    //POINT MARSHALLER TO GENERATED CLASSES
    Jaxb2Marshaller  marshaller = new Jaxb2Marshaller();
                     marshaller.setContextPath(generatedClasses);

    //CONFIGURE CLIENT
    setDefaultUri  (webServiceEndpoint);
    setMarshaller  (marshaller);
    setUnmarshaller(marshaller);

    //LOAD TRUST STORE
    ClassPathResource classPathResource = new ClassPathResource(clientTrustStoreName);
    InputStream       inputStream = classPathResource.getInputStream();
    KeyStore          trustStore  = KeyStore.getInstance(clientTrustStoreType);
                      trustStore.load(inputStream, clientTrustStorePassword.toCharArray());

    //CONFIGURE REQUEST FACTORY
    SSLContext sslContext = new SSLContextBuilder()
      .loadTrustMaterial(trustStore, null)
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
    WebServiceMessageSender     sender = messageSender;
    WebServiceTemplate          template = getWebServiceTemplate();
                                template.setMessageSender(sender);

    //SEND REQUEST
    GetPersonResponse  response = (GetPersonResponse) template.marshalSendAndReceive(getPersonRequest);

    //RETURN RESPONSE OBJECT
    return response;

  }

}