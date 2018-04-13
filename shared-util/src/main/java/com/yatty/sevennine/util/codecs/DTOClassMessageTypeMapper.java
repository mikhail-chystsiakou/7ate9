package com.yatty.sevennine.util.codecs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yatty.sevennine.api.dto.ErrorResponse;
import com.yatty.sevennine.api.dto.KeepAliveRequest;
import com.yatty.sevennine.api.dto.TestRequest;
import com.yatty.sevennine.api.dto.auth.LogInRequest;
import com.yatty.sevennine.api.dto.auth.LogInResponse;
import com.yatty.sevennine.api.dto.auth.LogOutRequest;
import com.yatty.sevennine.api.dto.game.*;
import com.yatty.sevennine.api.dto.lobby.*;

import java.util.*;

public class DTOClassMessageTypeMapper {
    public static final String MAPPING_FIELD = "_type";
    
    private static final Map<String, Class<?>> classTypeMapping = new HashMap<>();
    
    static {
        List<Class<?>> encodedClasses = Arrays.asList(
            LogInRequest.class,
            LogInResponse.class,
            LogOutRequest.class,
            MoveRequest.class,
            LeaveGameRequest.class,
            CreateLobbyRequest.class,
            LobbyListSubscribeRequest.class,
            LobbyListUnsubscribeRequest.class,
            LeaveLobbyRequest.class,
            TestRequest.class,
            LogInResponse.class,
            GameStartedNotification.class,
            MoveRejectedResponse.class,
            NewStateNotification.class,
            CreateLobbyResponse.class,
            EnterLobbyRequest.class,
            EnterLobbyResponse.class,
            LobbyStateChangedNotification.class,
            LobbyListUpdatedNotification.class,
            ErrorResponse.class
        );
        
        for (Class<?> clazz : encodedClasses) {
            classTypeMapping.put(getMessageTypeByDTOClass(clazz), clazz);
        }
    }
    
    public static String getMessageTypeByDTOClass(Class<?> DTOClass) {
        return DTOClass.getSimpleName();
    }
    
    public static Class<?> getDTOClassByMessageType(String messageType) {
        return classTypeMapping.get(messageType);
    }
    
    public static ObjectMapper prepareObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        classTypeMapping.values().forEach(c -> objectMapper.addMixIn(c, DTOTypeMappingMixin.class));
        return objectMapper;
    }
}
