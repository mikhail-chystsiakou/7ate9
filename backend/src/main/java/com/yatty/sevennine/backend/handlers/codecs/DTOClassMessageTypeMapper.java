package com.yatty.sevennine.backend.handlers.codecs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yatty.sevennine.api.dto.*;
import com.yatty.sevennine.api.dto.auth.LogInRequest;
import com.yatty.sevennine.api.dto.auth.LogInResponse;
import com.yatty.sevennine.backend.testing.TestMessage;

import java.util.HashMap;
import java.util.Map;

public class DTOClassMessageTypeMapper {
    public static final String MAPPING_FIELD = "_type";
    private static final Map<String, Class<?>> classTypeMapping = new HashMap<>();
    
    static {
        classTypeMapping.put(getMessageTypeByDTOClass(TestMessage.class), TestMessage.class);
        classTypeMapping.put(getMessageTypeByDTOClass(LogInRequest.class), LogInRequest.class);
        classTypeMapping.put(getMessageTypeByDTOClass(LogInResponse.class), LogInResponse.class);
        classTypeMapping.put(getMessageTypeByDTOClass(ConnectRequest.class), ConnectRequest.class);
        classTypeMapping.put(getMessageTypeByDTOClass(ConnectResponse.class), ConnectResponse.class);
        classTypeMapping.put(getMessageTypeByDTOClass(MoveRequest.class), MoveRequest.class);
        classTypeMapping.put(getMessageTypeByDTOClass(DisconnectRequest.class), DisconnectRequest.class);
        classTypeMapping.put(getMessageTypeByDTOClass(TestRequest.class), TestRequest.class);
    }
    
    public static String getMessageTypeByDTOClass(Class<?> DTOClass) {
        return DTOClass.getSimpleName();
    }
    
    public static Class<?> getDTOClassByMessageType(String messageType) {
        return classTypeMapping.get(messageType);
    }
}
