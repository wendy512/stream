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

package io.github.stream.core.lifecycle;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 抽象LifecycleAware
 * @author wendy512@yeah.net
 * @date 2023-05-19 10:44:12
 * @since 1.0.0
 */
public abstract class AbstractLifecycleAware implements LifecycleAware {

    private final AtomicReference<LifecycleState> state = new AtomicReference<>(LifecycleState.IDLE);

    protected AbstractLifecycleAware() {}

    @Override
    public void start() {
        state.compareAndSet(LifecycleState.IDLE, LifecycleState.START);
    }

    @Override
    public void stop() {
        state.compareAndSet(LifecycleState.START, LifecycleState.STOP);
    }

    @Override
    public LifecycleState getLifecycleState() {
        return state.get();
    }
}
