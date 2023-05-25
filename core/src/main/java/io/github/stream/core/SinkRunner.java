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

package io.github.stream.core;

import io.github.stream.core.lifecycle.AbstractLifecycleAware;
import lombok.extern.slf4j.Slf4j;

/**
 * 获取message runnable
 * @author wendy512@yeah.net
 * @date 2023-05-18 17:00:46
 * @since 1.0.0
 */
@Slf4j
public class SinkRunner<T> extends AbstractLifecycleAware {

    private final SinkProcessor<T> processor;
    private final int interval;
    private final String threadName;

    private PollingRunner<T> runner;
    private Thread runnerThread;

    public SinkRunner(SinkProcessor<T> processor, int interval, String threadName) {
        this.processor = processor;
        this.interval = interval;
        this.threadName = threadName;
    }

    @Override
    public void start() {
        processor.start();
        runner = new PollingRunner<>(processor, interval);
        runnerThread = new Thread(runner, threadName);
        runnerThread.start();
        super.start();
    }

    @Override
    public void stop() {
        if (runnerThread != null) {
            runner.shutdown();

            while (runnerThread.isAlive()) {
                try {
                    log.debug("Waiting for runner thread to exit");
                    runnerThread.join(500);
                } catch (InterruptedException e) {
                    log.debug("Interrupted while waiting for runner thread to exit. Exception follows.",
                            e);
                }
            }
        }
        super.stop();
    }

    public static class PollingRunner<T> extends AbstractAutoRunnable {

        private final SinkProcessor<T> processor;
        private final int interval;

        public PollingRunner(SinkProcessor<T> processor, int interval) {
            this.processor = processor;
            this.interval = interval;
        }

        @Override
        public void runInternal() {
            while (isRunning()) {
                int count = processor.process();

                if (interval > 0 && count > 0) {
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException ex) {
                        // 有可能调用interrupt会触发sleep interrupted异常
                        return;
                    }
                }
            }

        }
    }
}
