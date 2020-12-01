package com.example;

import org.junit.Assert;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdifyServiceTest {

  static class SenderSpy implements Subscription.Sender {
    String event;
    String body;

    @Override
    public void send(String event, String body) {
      this.event = event;
      this.body = body;
    }
  }

  @Test
  @Tag("slow")
  void testEndToEnd() {
    String sessionId = UUID.randomUUID().toString();

    SenderSpy spy = new SenderSpy();
    AdifyService a = new AdifyService(new Adify(new HerokuGetRequest("adify")), sessionId + ",USER_ID,PRODUCT_ID", spy);
    a.execute();

    System.out.println(spy.event);
    assertEquals("display", spy.event);

    System.out.println(spy.body);
    String[] bodyParts = spy.body.split(",");
    Assert.assertEquals(4, bodyParts.length);
    Assert.assertEquals(sessionId, bodyParts[0]);
    Assert.assertEquals("advert", bodyParts[1]);
  }

  static class ExternalServiceStub implements ExternalService {

    @Override
    public String get(String arg) {
      return "{\"product-name\":\"Paper\"}";
    }
  }

  @Test
  @Tag("fast")
  public void testComponent() {
    String sessionId = UUID.randomUUID().toString();

    SenderSpy spy = new SenderSpy();
    AdifyService a = new AdifyService(new Adify(new ExternalServiceStub()), sessionId + ",USER_ID,PRODUCT_ID", spy);
    a.execute();

    System.out.println(spy.event);
    assertEquals("display", spy.event);

    System.out.println(spy.body);
    String[] bodyParts = spy.body.split(",");
    Assert.assertEquals(4, bodyParts.length);
    Assert.assertEquals(sessionId, bodyParts[0]);
    Assert.assertEquals("advert", bodyParts[1]);
    Assert.assertEquals("PRODUCT_ID", bodyParts[2]);
    Assert.assertEquals("Paper", bodyParts[3]);
  }

}