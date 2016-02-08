/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.client.spring.loggregator;

import lombok.ToString;
import org.cloudfoundry.client.loggregator.LoggregatorMessage;
import org.cloudfoundry.client.loggregator.RecentLogsRequest;
import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.reactivestreams.Publisher;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.SchedulerGroup;
import reactor.fn.Consumer;
import reactor.fn.Function;
import reactor.rx.Stream;

import java.net.URI;
import java.util.List;

/**
 * The Spring-based implementation of the Loggregator recent API
 */
@ToString(callSuper = true)
public final class SpringRecent extends AbstractSpringOperations {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param schedulerGroup The group to use when making requests
     */
    public SpringRecent(RestOperations restOperations, URI root, SchedulerGroup schedulerGroup) {
        super(restOperations, root, schedulerGroup);
    }

    @SuppressWarnings("rawtypes")
    public Publisher<LoggregatorMessage> recent(final RecentLogsRequest request) {
        return get(request, List.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("recent").queryParam("app", request.getApplicationId());
            }

        })
            .flatMap(new Function<List, Stream<LoggregatorMessage>>() {

                @Override
                @SuppressWarnings("unchecked")
                public Stream<LoggregatorMessage> apply(List messages) {
                    return Stream.fromIterable(messages);
                }

            });
    }

}
