package com.yatty.sevennine.backend.handlers.codecs;

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

import java.util.HashMap;
import java.util.Map;

public class DTOClassMessageTypeMapper {
    public static final String MAPPING_FIELD = "_type";
    private static final Map<String, Class<?>> classTypeMapping = new HashMap<>();
    
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
    }
    
    public static String getMessageTypeByDTOClass(Class<?> DTOClass) {
        return DTOClass.getSimpleName();
    }
    
    public static Class<?> getDTOClassByMessageType(String messageType) {
        return classTypeMapping.get(messageType);
    }
}
