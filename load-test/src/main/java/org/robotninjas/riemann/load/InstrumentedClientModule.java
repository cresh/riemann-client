package org.robotninjas.riemann.load;

import com.google.common.collect.Queues;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.yammer.metrics.core.Gauge;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.core.MetricsRegistry;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.MessageEvent;
import org.robobninjas.riemann.guice.RiemannClientModule;
import org.robotninjas.riemann.client.ReturnableMessage;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public class InstrumentedClientModule extends RiemannClientModule {

  public InstrumentedClientModule(String address, int port, int numWorkers, GenericObjectPool.Config poolConfig) {
    super(address, port, numWorkers, poolConfig);
  }

  @Override
  protected void bindOutstandingMessagesQueue(Key<BlockingQueue<ReturnableMessage>> key) {
    bind(key).toProvider(new Provider<BlockingQueue<ReturnableMessage>>() {
      @Inject MetricsRegistry registry = null;
      @Override
      public BlockingQueue<ReturnableMessage> get() {
        final BlockingQueue<ReturnableMessage> queue = Queues.newLinkedBlockingQueue();
        registry.newGauge(new MetricName(getClass(), "unacked"), new Gauge<Integer>() {
          @Override
          public Integer value() {
            return queue.size();
          }
        });
        return queue;
      }
    });
  }

  @Override
  protected void bindSendBufferQueue(Key<Queue<MessageEvent>> key) {
    bind(key).toProvider(new Provider<Queue<MessageEvent>>() {
      @Inject MetricsRegistry registry = null;
      @Override
      public Queue<MessageEvent> get() {
        final Queue<MessageEvent> queue = Queues.newConcurrentLinkedQueue();
        registry.newGauge(new MetricName(getClass(), "buffered-sends"), new Gauge<Integer>() {
          @Override
          public Integer value() {
            return queue.size();
          }
        });
        return queue;
      }
    });
  }

  @Override
  protected void configureBootstrap(ClientBootstrap bootstrap) {
    bootstrap.setOption("writeBufferHighWaterMark", 65536 * 2);
    bootstrap.setOption("writeBufferLowWaterMark", 65536 );
    bootstrap.setOption("tcpNoDelay", true);
    bootstrap.setOption("child.tcpNoDelay", true);
  }
}
