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

package org.robotninjas.riemann.client;

import com.aphyr.riemann.Proto;

class ReturnableEvent extends ReturnableMessage<Boolean> {

  public ReturnableEvent(Proto.Msg msg) {
    super(msg);
  }

  public ReturnableEvent(Proto.Msg.Builder builder) {
    super(builder);
  }

  @Override
  public void handleResult(Proto.Msg msg) {
    if (msg.hasError()) {
      setException(new RiemannClientException(msg.getError()));
    } else {
      set(msg.getOk());
    }
  }

}
