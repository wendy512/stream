/**
 * Copyright wendy512@yeah.net
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.github.stream.sample;

import io.github.stream.core.Consumer;
import io.github.stream.core.Message;
import io.github.stream.core.annotation.Sink;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 测试Consumer
 * @author jujiale
 * @date 2024/04
 */
@Component
@Sink("test")
public class PulsarConsumer implements Consumer<Object> {
    
    @Override
    public void accept(List<Message<Object>> messages) {
        messages.forEach(m -> System.out.println("Received pulsar message is " + m.getPayload()));
    }
}
