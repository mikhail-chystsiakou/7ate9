package com.yatty.sevennine.backend.handlers;

import com.yatty.sevennine.api.dto.ErrorResponse;
import com.yatty.sevennine.backend.exceptions.SevenNineException;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@ChannelHandler.Sharable
public class ExceptionHandler extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ErrorResponse response = new ErrorResponse();
        response.setErrorUUID(UUID.randomUUID().toString());
        
        if (cause instanceof SevenNineException) {
            SevenNineException sevenNineException = (SevenNineException)cause;
            response.setShortDescription(sevenNineException.getShortDescription());
            response.setAdditionalInfo(sevenNineException.getAdditionalInfo());
        } else {
            response.setShortDescription("Internal server error");
        }
        
        logger.error("Exception handler caught an exception. \n "
                        + "UUID: {}. \n"
                        + "Provided short description: {}. \n"
                        + "Provided additional info: {} ",
                response.getErrorUUID(), response.getShortDescription(),
                response.getAdditionalInfo(), cause
        );
        
        ctx.channel().writeAndFlush(response);
    }
}
