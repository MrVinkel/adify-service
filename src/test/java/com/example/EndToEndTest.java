package com.example;


import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class EndToEndTest {

  @Test
  @Tag("slow")
  public void testService() throws Exception {
    String sessionId = UUID.randomUUID().toString();

    List<String> receivedMessages = new ArrayList<>();

    Subscription.Sender globalSender = Service.service(new Subscription[]{
            new Subscription("test-river", "display", (body, sender) -> {
              synchronized (receivedMessages) {
                System.out.println("Received a message!");
                receivedMessages.add(body);
              }
            })
    });

    globalSender.send("fetch-product-page", sessionId + ",USER_ID1,PRODUCT_ID1");

    System.out.println("Waiting for a message..");
    synchronized (receivedMessages) {
      receivedMessages.wait(1000);
    }

    assertEquals(1, receivedMessages.size());
    String[] bodyParts = receivedMessages.get(0).split(",");
    assertEquals(4, bodyParts.length);
    assertEquals(sessionId, bodyParts[0]);
    assertEquals("advert", bodyParts[1]);
//              assertEquals("PRODUCT_ID", bodyParts[2]);
//              assertEquals("", bodyParts[3]);
  }
}
