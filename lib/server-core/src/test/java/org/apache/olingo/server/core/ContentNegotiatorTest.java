/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.server.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.processor.CustomContentTypeSupportProcessor;
import org.apache.olingo.server.api.processor.FormatContentTypeMapping;
import org.apache.olingo.server.api.processor.MetadataProcessor;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.api.processor.ServiceDocumentProcessor;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.queryoption.FormatOption;
import org.junit.Test;

public class ContentNegotiatorTest {

  static final private String ACCEPT_CASE_MIN = "application/json;odata.metadata=minimal";
  static final private String ACCEPT_CASE_MIN_UTF8 = "application/json;charset=UTF-8;odata.metadata=minimal";
  static final private String ACCEPT_CASE_FULL = "application/json;odata.metadata=full";
  static final private String ACCEPT_CASE_JSONQ = "application/json;q=0.2";
  static final private String ACCEPT_CASE_XML = "application/xml";
  static final private String ACCEPT_CASE_WILDCARD1 = "*/*";
  static final private String ACCEPT_CASE_WILDCARD2 = "application/*";

  //@formatter:off (Eclipse formatter)
  //CHECKSTYLE:OFF (Maven checkstyle)

  String[][] casesServiceDocument = {
      /* expected               $format           accept                 alias        ct mapping    */
      { ACCEPT_CASE_MIN,        null,             null,                  null         ,null             },
      { ACCEPT_CASE_MIN,        "json",           null,                  null         ,null             },
      { ACCEPT_CASE_MIN,        "json",           ACCEPT_CASE_JSONQ,     null         ,null             },
      { "a/a",                  "a",              null,                  "a"          ,"a/a"            },
      { ACCEPT_CASE_MIN,        null,             ACCEPT_CASE_JSONQ,     null         ,null             },
      { ACCEPT_CASE_MIN,        null,             ACCEPT_CASE_WILDCARD1, null         ,null             },
      { ACCEPT_CASE_MIN,        null,             ACCEPT_CASE_WILDCARD2, null         ,null             },
      { "a/a",                  "a",              null,                  "a, b"       ,"a/a,b/b"        },
      { "a/a",                  " a ",            null,                  " a , b"     ," a/a , b/b "    },
      { "a/a;x=y",              "a",              ACCEPT_CASE_WILDCARD1, "a"          ,"a/a;x=y"        },
      { ACCEPT_CASE_MIN,        "json",           ACCEPT_CASE_MIN,       null         ,null             },
      { ACCEPT_CASE_FULL,       null,             ACCEPT_CASE_FULL,      "dummy"     ,ACCEPT_CASE_FULL  }, 
      { ACCEPT_CASE_MIN_UTF8,   null,             ACCEPT_CASE_MIN_UTF8,  null         ,null             },
  };                                                                                          

  String[][] casesMetadata = {                                                                 
      /* expected               $format           accept                 alias        ct mapping    */
      { "application/xml",      null,             null,                  null         ,null             },
      { "application/xml",      "xml",            null,                  null         ,null             },
      { "application/xml",      "xml",            ACCEPT_CASE_XML,       null         ,null             },
      { "a/a",                  "a",              null,                  "a"          ,"a/a"            },
      { "application/xml",      null,             ACCEPT_CASE_XML,       null         ,null             },
      { "application/xml",      null,             ACCEPT_CASE_WILDCARD1, null         ,null             },
      { "application/xml",      null,             ACCEPT_CASE_WILDCARD2, null         ,null             },
      { "a/a",                  "a",              null,                  "a, b"       ,"a/a,b/b"        },
      { "a/a",                  " a ",            null,                  " a , b"     ," a/a , b/b "    },
      { "a/a;x=y",              "a",              ACCEPT_CASE_WILDCARD1, "a"          ,"a/a;x=y"        },
  };

  String[][] casesFail = {                                                                 
      /* expected               $format           accept                 alias        ct mapping    */
      { "application/xml",      "xxx",            null,                  null         ,null             },
      { "a/a",                  "a",              null,                  "b"          ,"b/b"            },
      { "application/xml",      null,             ACCEPT_CASE_JSONQ,     null         ,null             },
      { "application/json",     null,             ACCEPT_CASE_FULL,      null         ,null             }, // not jet supported
  };
  //CHECKSTYLE:ON
  //@formatter:on

  @Test
  public void testServiceDocumentSingleCase() throws Exception {
    String[] useCase = { ACCEPT_CASE_MIN_UTF8, null, ACCEPT_CASE_MIN_UTF8, null, null };

    testContentNegotiation(useCase, ServiceDocumentProcessor.class);
  }

  @Test
  public void testServiceDocument() throws Exception {
    for (String[] useCase : casesServiceDocument) {
      testContentNegotiation(useCase, ServiceDocumentProcessor.class);
    }
  }

  @Test
  public void testMetadataSingleCase() throws Exception {
    String[] useCase = { "application/xml", null, null, null, null };

    testContentNegotiation(useCase, MetadataProcessor.class);
  }

  @Test
  public void testMetadata() throws Exception {
    for (String[] useCase : casesMetadata) {
      testContentNegotiation(useCase, MetadataProcessor.class);
    }
  }

  @Test
  public void testMetadataFail() {
    for (String[] useCase : casesFail) {
      try {
        testContentNegotiation(useCase, MetadataProcessor.class);
        fail("Exeption expected!");
      } catch (ContentNegotiatorException e) {

      }catch (Exception e) {
        e.printStackTrace();
        fail("Wrong Exception: " + e.getClass().getName());
      }
    }
  }

  public void testContentNegotiation(final String[] useCase, final Class<? extends Processor> processorClass)
      throws Exception {
    ODataRequest request = new ODataRequest();
    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("/" + (useCase[1] == null ? "" : "?$format=" + useCase[1]));

    ProcessorStub p = new ProcessorStub(createCustomContentTypeMapping(useCase[3], useCase[4]));

    FormatOption fo = null;
    if (useCase[1] != null) {
      fo = mock(FormatOption.class);
      when(fo.getText()).thenReturn(useCase[1].trim());
    }

    if (useCase[2] != null) {
      request.addHeader(HttpHeader.ACCEPT, Arrays.asList(useCase[2]));
    }

    ContentType requestedContentType = ContentNegotiator.doContentNegotiation(fo, request, p, processorClass);

    assertNotNull(requestedContentType);
    assertEquals(ContentType.create(useCase[0]), requestedContentType);
  }

  private List<FormatContentTypeMapping> createCustomContentTypeMapping(final String formatString,
      final String contentTypeString) {
    List<FormatContentTypeMapping> map = null;

    assertTrue(!(formatString == null ^ contentTypeString == null));

    if (formatString != null) {
      String[] formats = formatString.split(",");
      String[] contentTypes = contentTypeString.split(",");

      assertEquals(formats.length, contentTypes.length);

      map = new ArrayList<FormatContentTypeMapping>();
      for (int i = 0; i < formats.length; i++) {
        map.add(new FormatContentTypeMapping(formats[i], contentTypes[i]));
      }
    }

    return map;
  }

  private class ProcessorStub implements ServiceDocumentProcessor, MetadataProcessor,
      EntityCollectionProcessor,
      CustomContentTypeSupportProcessor {

    List<FormatContentTypeMapping> customMapping;

    ProcessorStub(final List<FormatContentTypeMapping> mapping) {
      customMapping = mapping;
    }

    @Override
    public void init(final OData odata, final Edm edm) {}

    @Override
    public List<FormatContentTypeMapping> modifySupportedContentTypes(
        final List<FormatContentTypeMapping> supportedContentTypes,
        final Class<? extends Processor> processorClass) {
      if (customMapping != null) {
        supportedContentTypes.addAll(customMapping);
      }
      return supportedContentTypes;
    }

    @Override
    public void readServiceDocument(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
        final ContentType format) {
      response.setHeader(HttpHeader.CONTENT_TYPE, format.toContentTypeString());
    }

    @Override
    public void readCollection(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
        final ContentType requestedContentType) {
      response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
    }

    @Override
    public void readMetadata(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
        final ContentType requestedContentType) {
      response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
    }
  }
}
