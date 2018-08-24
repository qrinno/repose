package org.openrepose.core.service.newhttpclient.delegating

import org.apache.http.{HttpHost, HttpRequest}
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.conn.ClientConnectionManager
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.params.HttpParams
import org.apache.http.protocol.HttpContext

class HttpClientUserWrapper(clientInstanceId: String, userId: String, httpClient: CloseableHttpClient)
  extends NewHttpClient(clientInstanceId, userId) {

  override def doExecute(target: HttpHost, request: HttpRequest, context: HttpContext): CloseableHttpResponse = httpClient.doExecute(target, request, context)

  override def getParams: HttpParams = httpClient.getParams

  override def getConnectionManager: ClientConnectionManager = httpClient.getConnectionManager

  override def close(): Unit = httpClient.close()
}
