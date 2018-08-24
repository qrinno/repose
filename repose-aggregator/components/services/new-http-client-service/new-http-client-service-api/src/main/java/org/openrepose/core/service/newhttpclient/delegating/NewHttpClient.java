package org.openrepose.core.service.newhttpclient.delegating;

import org.apache.http.impl.client.CloseableHttpClient;

public abstract class NewHttpClient extends CloseableHttpClient {

    private final String clientInstanceId;
    private final String userId;

    public NewHttpClient(String clientInstanceId, String userId) {
        this.clientInstanceId = clientInstanceId;
        this.userId = userId;
    }

    protected String getClientInstanceId() {
        return clientInstanceId;
    }

    protected String getUserId() {
        return userId;
    }
}
