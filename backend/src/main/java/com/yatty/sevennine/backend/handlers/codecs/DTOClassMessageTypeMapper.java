package com.yatty.sevennine.backend.handlers.codecs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yatty.sevennine.api.dto.*;
import com.yatty.sevennine.api.dto.auth.LogInRequest;
import com.yatty.sevennine.api.dto.auth.LogInResponse;
import com.yatty.sevennine.api.dto.auth.LogOutRequest;
import com.yatty.sevennine.api.dto.game.GameStartedEvent;
import com.yatty.sevennine.api.dto.game.MoveRejectedResponse;
import com.yatty.sevennine.api.dto.game.MoveRequest;
import com.yatty.sevennine.api.dto.game.NewStateEvent;
import com.yatty.sevennine.api.dto.lobby.*;
import com.yatty.sevennine.backend.testing.TestMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DTOClassMessageTypeMapper {
    private static final Map<String, Class<?>> classTypeMapping = new HashMap<>();
    private static final List<Class<?>> encodedClasses = new ArrayList<>(30);
    static {
        classTypeMapping.put(getMessageTypeByDTOClass(TestMessage.class), TestMessage.class);
        classTypeMapping.put(getMessageTypeByDTOClass(LogInRequest.class), LogInRequest.class);
        classTypeMapping.put(getMessageTypeByDTOClass(LogOutRequest.class), LogOutRequest.class);
        classTypeMapping.put(getMessageTypeByDTOClass(MoveRequest.class), MoveRequest.class);
        classTypeMapping.put(getMessageTypeByDTOClass(CreateLobbyRequest.class), CreateLobbyRequest.class);
        classTypeMapping.put(getMessageTypeByDTOClass(EnterLobbyRequest.class), EnterLobbyRequest.class);
        classTypeMapping.put(getMessageTypeByDTOClass(LobbySubscribeRequest.class), LobbySubscribeRequest.class);
        classTypeMapping.put(getMessageTypeByDTOClass(LobbyUnsubscribeRequest.class), LobbyUnsubscribeRequest.class);
        classTypeMapping.put(getMessageTypeByDTOClass(KeepAliveRequest.class), KeepAliveRequest.class);
        classTypeMapping.put(getMessageTypeByDTOClass(TestRequest.class), TestRequest.class);
    
        encodedClasses.add(TestMessage.class);
        encodedClasses.add(LogInRequest.class);
        encodedClasses.add(LogOutRequest.class);
        encodedClasses.add(MoveRequest.class);
        encodedClasses.add(CreateLobbyRequest.class);
        encodedClasses.add(EnterLobbyRequest.class);
        encodedClasses.add(LobbySubscribeRequest.class);
        encodedClasses.add(LobbyUnsubscribeRequest.class);
        encodedClasses.add(KeepAliveRequest.class);
        encodedClasses.add(TestRequest.class);
        
        encodedClasses.add(LogInResponse.class);
        encodedClasses.add(GameStartedEvent.class);
        encodedClasses.add(MoveRejectedResponse.class);
        encodedClasses.add(NewStateEvent.class);
        encodedClasses.add(CreateLobbyResponse.class);
        encodedClasses.add(EnterLobbyResponse.class);
        encodedClasses.add(LobbyListUpdatedNotification.class);
        encodedClasses.add(ErrorResponse.class);
    }
    
    public static String getMessageTypeByDTOClass(Class<?> DTOClass) {
        return DTOClass.getSimpleName();
    }
    
    public static final String MAPPING_FIELD = "_type";
    
    public static Class<?> getDTOClassByMessageType(String messageType) {
        return classTypeMapping.get(messageType);
    }
    
    public static ObjectMapper prepareObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        encodedClasses.forEach(c -> objectMapper.addMixIn(c, DTOTypeMappingMixin.class));
        return objectMapper;
    }
}
