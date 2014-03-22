/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.communication.request.cud;

import java.io.InputStream;
import java.net.URI;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataPropertyUpdateRequest;
import org.apache.olingo.client.api.communication.response.ODataPropertyUpdateResponse;
import org.apache.olingo.client.api.domain.ODataProperty;
import org.apache.olingo.client.api.format.ODataFormat;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.client.core.communication.request.AbstractODataBasicRequest;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;

/**
 * This class implements an OData update entity property request.
 */
public class ODataPropertyUpdateRequestImpl extends AbstractODataBasicRequest<ODataPropertyUpdateResponse, ODataFormat>
        implements ODataPropertyUpdateRequest, ODataBatchableRequest {

  /**
   * Value to be created.
   */
  private final ODataProperty property;

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param method request method.
   * @param targetURI entity set or entity or entity property URI.
   * @param property value to be created.
   */
  ODataPropertyUpdateRequestImpl(final CommonODataClient odataClient,
          final HttpMethod method, final URI targetURI, final ODataProperty property) {

    super(odataClient, ODataFormat.class, method, targetURI);
    // set request body
    this.property = property;
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public ODataPropertyUpdateResponse execute() {
    final InputStream input = getPayload();
    ((HttpEntityEnclosingRequestBase) request).setEntity(URIUtils.buildInputStreamEntity(odataClient, input));

    try {
      return new ODataPropertyUpdateResponseImpl(httpClient, doExecute());
    } finally {
      IOUtils.closeQuietly(input);
    }
  }

  /**
   * {@inheritDoc }
   */
  @Override
  protected InputStream getPayload() {
    return odataClient.getWriter().writeProperty(property, ODataFormat.fromString(getContentType()));
  }

  /**
   * Response class about an ODataPropertyUpdateRequest.
   */
  private class ODataPropertyUpdateResponseImpl extends AbstractODataResponse implements ODataPropertyUpdateResponse {

    private ODataProperty property = null;

    /**
     * Constructor.
     * <p>
     * Just to create response templates to be initialized from batch.
     */
    private ODataPropertyUpdateResponseImpl() {
    }

    /**
     * Constructor.
     *
     * @param client HTTP client.
     * @param res HTTP response.
     */
    private ODataPropertyUpdateResponseImpl(final HttpClient client, final HttpResponse res) {
      super(client, res);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ODataProperty getBody() {
      if (property == null) {
        try {
          property = odataClient.getReader().
                  readProperty(getRawResponse(), ODataFormat.fromString(getAccept()));
        } finally {
          this.close();
        }
      }
      return property;
    }
  }
}