package com.yatty.sevennine.client;

import com.yatty.sevennine.api.dto.ErrorResponse;
import com.yatty.sevennine.api.dto.auth.LogInResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Semaphore;

/**
 * Provides synchronous communication with server
 */
public class SynchronousClient extends SevenAteNineClient {
    private static final Logger logger = LoggerFactory.getLogger(SynchronousClient.class);
    private volatile Object lastResponse;
    private Semaphore responseAvailable = new Semaphore(0);
    
    public SynchronousClient(InetSocketAddress serverAddress,
                             SevenAteNineClientChannelInitializer channelInitializer) {
        super(serverAddress, channelInitializer);
        
        channelInitializer.addHandler(o -> {
            lastResponse = o;
            responseAvailable.release();
        }, Object.class);
    }
    
    public <T> T sendMessage(Object request,
                             Class<T> responseType,
                             boolean keepAlive) {
        super.sendMessage(request, keepAlive);
        return waitForResponse(responseType);
    }
    
    public <T> T sendMessage(Object request,
                             Class<T> responseType) {
        super.sendMessage(request);
        return waitForResponse(responseType);
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
            responseAvailable.acquire();
            if (responseType.isInstance(lastResponse)) {
                return responseType.cast(lastResponse);
            } else if (ErrorResponse.class.isInstance(lastResponse)) {
                throw new ServerException((ErrorResponse)lastResponse);
            } else {
                logger.error("Got unexpected error {}. Expected type: {}.",
                        lastResponse, responseType);
                throw new ClientException("Got unexpected response type "
                        + lastResponse.getClass() + ". Expected type: " + responseType);
            }
        } catch (InterruptedException e) {
            throw new ClientException("Client was interrupted during waiting for response", e);
        }
    }
}
