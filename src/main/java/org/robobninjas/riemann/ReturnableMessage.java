package org.robobninjas.riemann;

import com.aphyr.riemann.Proto;
import com.google.common.util.concurrent.SettableFuture;

abstract class ReturnableMessage<T> {

  private final Proto.Msg msg;
  protected final SettableFuture<T> future;

  public ReturnableMessage(Proto.Msg msg) {
    this.msg = msg;
    this.future = SettableFuture.create();
  }

  public SettableFuture<T> getFuture() {
    return future;
  }

  public Proto.Msg getMsg() {
    return msg;
  }

  public abstract void handleResult(Proto.Msg msg);

}