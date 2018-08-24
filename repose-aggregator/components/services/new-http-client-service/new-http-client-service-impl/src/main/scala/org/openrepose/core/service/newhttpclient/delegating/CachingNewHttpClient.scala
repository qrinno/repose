package org.openrepose.core.service.newhttpclient.delegating

import com.google.common.cache.{Cache, CacheBuilder, CacheLoader}
import org.apache.http.client.entity.EntityBuilder
import org.apache.http.{HttpHost, HttpRequest}
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.conn.ClientConnectionManager
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.params.HttpParams
import org.apache.http.protocol.HttpContext

import scala.concurrent.duration.Duration

class CachingNewHttpClient(cacheDuration: Duration)
  extends CloseableHttpClient {

  val cache: Cache[String, Any] = CacheBuilder.newBuilder()
    .expireAfterWrite(cacheDuration.length, cacheDuration.unit)
    .build()

  override def doExecute(target: HttpHost, request: HttpRequest, context: HttpContext): CloseableHttpResponse = {
    val useCache = Option(context.getAttribute(HttpCacheContext.CACHE_USE)).getOrElse(true)
    val cacheKey = Option(context.getAttribute(HttpCacheContext.CACHE_KEY)).getOrElse(true)
    val forceRefresh = Option(context.getAttribute(HttpCacheContext.CACHE_FORCE_REFRESH)).getOrElse(true)

    if (useCache) {
      // Use the cache, populating it if
    } else if (forceRefresh) {
      //
    } else {
      // Do not use the cache at all
    }

    EntityBuilder.create().getContentType

    ???
  }

  override def getParams: HttpParams = ???

  override def getConnectionManager: ClientConnectionManager = ???

  // todo: call this when decommissioning this type of client
  override def close(): Unit = {
    super.close()
    cache.cleanUp()
  }
}
