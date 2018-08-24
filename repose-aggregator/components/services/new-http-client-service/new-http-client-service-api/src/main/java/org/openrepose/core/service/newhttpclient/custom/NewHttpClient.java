package org.openrepose.core.service.newhttpclient.custom;

import org.openrepose.core.service.newhttpclient.custom.http.Response;

import java.nio.charset.Charset;
import java.util.Map;

public interface NewHttpClient {
    // note: use Apache's RequestBuilder to translate between parameters and an HttpRequest
    // note: use Apache's org.apache.http.entity.ContentType to

    // todo: declare exceptions and wrap Apache exceptions with our own

    Response get(String uri, Map<String, String> headers) throws NewHttpClientServiceException;

    Response get(String uri, Map<String, String> headers, String cacheKey) throws NewHttpClientServiceException;

    Response get(String uri, Map<String, String> headers, String cacheKey, boolean checkCache) throws NewHttpClientServiceException;

    Response get(String uri, Map<String, String> headers, String cacheKey, boolean checkCache, boolean writeCache) throws NewHttpClientServiceException;

    Response post(String uri, Map<String, String> headers, byte[] payload, String mimeType, String cacheKey) throws NewHttpClientServiceException;

    Response post(String uri, Map<String, String> headers, byte[] payload, String mimeType, Charset charset, String cacheKey) throws NewHttpClientServiceException;

    Response post(String uri, Map<String, String> headers, byte[] payload, String mimeType, Charset charset, String cacheKey, boolean checkCache) throws NewHttpClientServiceException;

    Response post(String uri, Map<String, String> headers, byte[] payload, String mimeType, Charset charset, String cacheKey, boolean checkCache, boolean writeCache) throws NewHttpClientServiceException;

    // todo: I don't like having to put these here, but this information needs to be tracked for decommissioning
    String getClientInstanceId();

    String getUserId();
}
