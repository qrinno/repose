package org.openrepose.core.service.newhttpclient.delegating

import org.apache.http.client.entity.EntityBuilder
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.protocol.{BasicHttpContext, HttpContext, HttpCoreContext}
import org.openrepose.core.service.newhttpclient.delegating.HttpCacheContext._

// Based on BasicHttpContext, HttpCoreContext, and HttpClientContext
// Rather than adapt a context, this context will use the hierarchy of the BasicHttpContext
class HttpCacheContext private(httpContext: HttpContext) extends HttpCoreContext(httpContext) {
  def getForceRefresh: Boolean = {
    getAttribute(CACHE_FORCE_REFRESH, classOf[Boolean])
  }

  def setForceRefresh(forceRefresh: Boolean): Unit = {
    setAttribute(CACHE_FORCE_REFRESH, forceRefresh)
  }

  def getUseCache: Boolean = {
    getAttribute(CACHE_USE, classOf[Boolean])
  }

  def setUseCache(useCache: Boolean): Unit = {
    setAttribute(CACHE_USE, useCache)
  }

  def getCacheKey: String = {
    getAttribute(CACHE_KEY, classOf[String])
  }

  def setCacheKey(key: String): Unit = {
    setAttribute(CACHE_KEY, key)
  }
}

object HttpCacheContext {
  // Note: These constants do not conform to the Scala constants format since they are siblings to HttpClient constants
  // Note: using the Java syntax. For the sake of consistency, these constants will also use the Java syntax.
  final val CACHE_USE: String = "repose.cache.use"
  final val CACHE_KEY: String = "repose.cache.key"
  final val CACHE_FORCE_REFRESH: String = "repose.cache.refresh"

  def apply: HttpCacheContext = create

  def apply(it: HttpContext): HttpCacheContext = adapt(it)

  def create: HttpCacheContext = new HttpCacheContext(new BasicHttpContext)

  def adapt(context: HttpContext): HttpCacheContext = context match {
    case ctx: HttpCacheContext => ctx
    case ctx => new HttpCacheContext(ctx)
  }

}
