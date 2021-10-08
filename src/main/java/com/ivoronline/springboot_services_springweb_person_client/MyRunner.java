package com.ivoronline.springboot_services_springweb_person_client;

import com.ivoronline.soap.GetPersonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MyRunner implements CommandLineRunner {

  @Autowired PersonClient personClient;

  //===============================================================================
  // RUN
  //===============================================================================
  @Override
  public void run(String... args) throws Exception {

    //SEND REQUEST
    GetPersonResponse response = personClient.getPerson(1);

    //PRINT RESPONSE
    System.out.println(response.getName());

  }

}
