package org.example;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@ExtendWith(VertxExtension.class)
public final class LatchTest {
  @Test
  public void test(Vertx vertx) {
    final var latch1 = new CountDownLatch(1);
    final var latch2 = new CountDownLatch(1);
    final var latch3 = new CountDownLatch(1);

    vertx.executeBlocking(promise1 -> {
      try {
        latch1.await();
      } catch (InterruptedException e) {
        promise1.fail(e);
      }
      vertx.executeBlocking(promise2 -> {
        latch2.countDown();
        promise2.complete();
      }, null);
      promise1.complete();
    }, null);

    vertx.executeBlocking(promise3 -> {
      try {
        latch3.await();
      } catch (InterruptedException e) {
        promise3.fail(e);
      }
      promise3.complete();
    }, null);

    latch1.countDown();
    try {
      Assertions.assertTrue(latch2.await(10, TimeUnit.SECONDS));
    } catch (InterruptedException e) {
      Assertions.fail(e);
    }
    latch3.countDown();
  }
}
