/*

 Copyright 2012 David Rusek

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

*/

package org.robotninjas.riemann.sample;

import com.aphyr.riemann.Proto;
import org.robobninjas.riemann.Client;
import org.robobninjas.riemann.Connection;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.io.Closeables.closeQuietly;
import static org.robobninjas.riemann.Clients.makeTcpClient;

public class SampleClient {

  public static void main(String[] args) {

    final Client client = makeTcpClient("localhost");
    Connection connection = null;

    try {

      connection = client.makeConnection();
      final Future<Boolean> isOk = connection.sendEvent(
          Proto.Event
              .newBuilder()
              .setMetricF(1000000)
              .setService("thing")
              .build());

      isOk.get(1, TimeUnit.SECONDS);

    } catch (Throwable t) {
      propagate(t);
    } finally {
      closeQuietly(connection);
      client.shutdown();
    }

  }

}