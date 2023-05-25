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

package io.github.stream.core.configuration;

import java.util.List;
import java.util.Map;

import io.github.stream.core.Channel;
import io.github.stream.core.Sink;
import io.github.stream.core.SinkRunner;
import io.github.stream.core.Source;
import io.github.stream.core.channel.ChannelProcessor;

/**
 * 配置管理
 * @author wendy512@yeah.net
 * @date 2023-05-19 15:12:46
 * @since 1.0.0
 */
public interface MaterializedConfiguration {
    void addSource(String name, Source source);

    void addSinkRunner(String name, SinkRunner sinkRunner);

    void addSink(String name, Sink sink);

    void addChannel(String name, Channel channel);

    void addChannelProcessor(String name, ChannelProcessor channelProcessor);

    Map<String, Source> getSources();

    Map<String, List<SinkRunner>> getSinkRunners();

    Map<String, List<Sink>> getSinks();

    Map<String, List<Channel>> getChannels();

    Map<String, ChannelProcessor> getChannelProcessors();
}
