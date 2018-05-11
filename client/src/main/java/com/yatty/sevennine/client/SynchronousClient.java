package com.yatty.sevennine.client;

import com.yatty.sevennine.api.dto.ErrorResponse;

import java.net.InetSocketAddress;
import java.util.concurrent.Semaphore;

/**
 * Provides synchronous communication with server
 */
public class SynchronousClient extends SevenAteNineClient {
    private volatile Object lastResponse;
    private Semaphore responseAvailable = new Semaphore(0);
    
    public SynchronousClient(InetSocketAddress serverAddress,
                             SevenAteNineClientChannelInitializer channelInitializer) {
        super(serverAddress, channelInitializer);
    }
    
    public <T> T sendMessage(Object request,
                             Class<T> responseType,
                             boolean keepAlive) {
        MessageHandler<T> messageHandler = addMessageHandler(o -> {
            lastResponse = o;
            responseAvailable.release();
        }, responseType);
        super.sendMessage(request, keepAlive);
        T response = waitForResponse(responseType);
        removeMessageHandler(messageHandler);
        return response;
    }
    
    public <T> T sendMessage(Object request,
                             Class<T> responseType) {
        MessageHandler<T> messageHandler = addMessageHandler(o -> {
            lastResponse = o;
            responseAvailable.release();
        }, responseType);
        super.sendMessage(request);
        T response = waitForResponse(responseType);
        removeMessageHandler(messageHandler);
        return response;
    }
    
    public void sendMessage(Object request,
                             boolean keepAlive) {
        super.sendMessage(request, keepAlive);
    }
    
    public void sendMessage(Object request) {
        super.sendMessage(request);
    }
    
    private <T> T waitForResponse(Class<T> responseType) {
        try {
            while (true) {
                responseAvailable.acquire();
                if (responseType.isInstance(lastResponse)) {
                    return responseType.cast(lastResponse);
                } else if (ErrorResponse.class.isInstance(lastResponse)) {
                    throw new ServerException((ErrorResponse) lastResponse);
                } else {
                    throw new ClientException("Got unexpected response type "
                            + lastResponse.getClass() + ". Expected type: " + responseType);
                }
            }
        } catch (InterruptedException e) {
            throw new ClientException("Client was interrupted during waiting for response", e);
        }
    }
}
