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

package io.github.stream.test;

import java.util.List;

import org.springframework.stereotype.Component;

import io.github.stream.core.Consumer;
import io.github.stream.core.Message;
import io.github.stream.core.annotation.Sink;
import lombok.extern.slf4j.Slf4j;

/**
 * 测试consumer
 * @author wendy512@yeah.net
 * @date 2023-05-22 17:17:53
 * @since 1.0.0
 */
@Sink("test")
@Component
@Slf4j
public class TestConsumer implements Consumer<String> {

    @Override
    public void accept(List<Message<String>> messages) {
        String threadName = Thread.currentThread().getName();
        messages.forEach(m -> {
            System.out.println(String.format("[%s] Received message %s", threadName, m.getPayload()));
        });
    }
}
