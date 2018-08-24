package org.openrepose.core.service.newhttpclient.delegating

import javax.inject.Named

@Named
class NewHttpClientServiceImpl extends NewHttpClientService {
  // todo: when returning a client, wrap the actual client to inject the instance id and user id

  override def getDefaultClient: NewHttpClient = ???

  override def getClient(clientId: String): NewHttpClient = ???

  override def isAvailable(clientId: String): Boolean = ???

  override def releaseClient(httpClientContainer: NewHttpClient): Unit = ???

  // todo: handle configuration
}
