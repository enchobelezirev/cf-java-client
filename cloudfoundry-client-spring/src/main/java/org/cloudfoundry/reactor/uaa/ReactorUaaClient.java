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

package org.cloudfoundry.reactor.uaa;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import org.cloudfoundry.reactor.uaa.accesstokenadministration.ReactorAccessTokens;
import org.cloudfoundry.reactor.uaa.identityzonemanagement.ReactorIdentityZones;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import org.cloudfoundry.reactor.util.ConnectionContextSupplier;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.accesstokens.AccessTokens;
import org.cloudfoundry.uaa.identityzones.IdentityZones;
import reactor.core.publisher.Mono;
import reactor.io.netty.http.HttpClient;

/**
 * The Spring-based implementation of {@link UaaClient}
 */
public final class ReactorUaaClient implements UaaClient {

    private final AccessTokens accessTokens;

    private final IdentityZones identityZones;

    @Builder
    ReactorUaaClient(ConnectionContextSupplier cloudFoundryClient) {
        this(cloudFoundryClient.getConnectionContext().getAuthorizationProvider(), cloudFoundryClient.getConnectionContext().getHttpClient(),
            cloudFoundryClient.getConnectionContext().getObjectMapper(), cloudFoundryClient.getConnectionContext().getRoot("token_endpoint"));
    }

    ReactorUaaClient(AuthorizationProvider authorizationProvider, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        this.accessTokens = new ReactorAccessTokens(authorizationProvider, httpClient, objectMapper, root);
        this.identityZones = new ReactorIdentityZones(authorizationProvider, httpClient, objectMapper, root);
    }

    @Override
    public AccessTokens accessTokens() {
        return this.accessTokens;
    }

    @Override
    public IdentityZones identityZones() {
        return this.identityZones;
    }

}