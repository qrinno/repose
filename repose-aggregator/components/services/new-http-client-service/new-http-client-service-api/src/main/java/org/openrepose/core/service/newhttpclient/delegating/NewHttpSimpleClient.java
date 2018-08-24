package org.openrepose.core.service.newhttpclient.delegating;

public abstract class NewHttpSimpleClient extends NewHttpClient {
    public NewHttpSimpleClient(String clientInstanceId, String userId) {
        super(clientInstanceId, userId);
    }
}
