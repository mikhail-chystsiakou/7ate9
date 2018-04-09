package com.yatty.sevennine.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.yatty.sevennine.api.dto.auth.LogInRequest;
import com.yatty.sevennine.api.dto.auth.LogOutRequest;
import com.yatty.sevennine.api.dto.game.MoveRequest;
import com.yatty.sevennine.api.dto.lobby.CreateLobbyRequest;
import com.yatty.sevennine.api.dto.lobby.EnterLobbyRequest;
import com.yatty.sevennine.api.dto.lobby.LobbyListSubscribeRequest;
import com.yatty.sevennine.api.dto.lobby.LobbyListUnsubscribeRequest;
import com.yatty.sevennine.util.PropertiesProvider;
import com.yatty.sevennine.util.codecs.JsonMessageDecoder;
import com.yatty.sevennine.util.codecs.JsonMessageEncoder;
import io.netty.channel.Channel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TCPAliveClientStub extends Application implements Initializable {
    public static final Logger logger = LoggerFactory.getLogger(TCPAliveClientStub.class);
    private static volatile String token;
    private static volatile String lastLobbyId;
    private boolean responseExpected;
    private static SynchronousClient client;
    private JsonMessageEncoder encoder = new JsonMessageEncoder();
    private JsonMessageDecoder decoder = new JsonMessageDecoder();

    private ObjectWriter objectWriter = new ObjectMapper()
            .writerWithDefaultPrettyPrinter();

    @FXML
    Button sendButton;
    @FXML
    TextArea inputArea;
    @FXML
    volatile TextArea outputArea;

    public static void main(String[] args) throws Exception {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Pane mainPane = loader.load(this.getClass().getClassLoader()
                .getResourceAsStream("TCPAliveClientStub.fxml")
        );
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    
        Properties environmentProperties = PropertiesProvider.getEnvironmentProperties();
        InetSocketAddress destinationAddress = new InetSocketAddress(
                environmentProperties.getProperty(PropertiesProvider.Environment.HOST),
                Integer.valueOf(environmentProperties.getProperty(PropertiesProvider.Environment.PORT))
        );
        SevenAteNineClientFactory factory = new SevenAteNineClientFactory();
        factory.setCustomDecoder(s -> s);
        factory.addMessageHandler(msg -> {
            logger.debug(msg);
            Pattern p = Pattern.compile("\"authToken\":\"([^\"]*)\"");
            Matcher m = p.matcher(msg);
            if (m.find()) {
                token = m.group(1);
            }
            Pattern p2 = Pattern.compile("\"lobbyList\":\\[\\{\"lobbyId\":\"([^\"]*)\"");
            Matcher m2 = p2.matcher(msg);
            if (m2.find()) {
                lastLobbyId = m2.group(1);
            }
            Platform.runLater(() -> {
                // fucking bug...
                TextArea area = (TextArea) mainPane.getChildren()
                        .filtered(e -> "outputArea".equals(e.getId())).get(0);
                area.setText(msg);
            });
        }, String.class);
        client = factory.getSynchronousClient(destinationAddress);
        client.start();
    }
    
    @Override
    public void stop() throws Exception {
        client.stop();
    }
    
    @FXML
    public void sendMessageAction() throws Exception {
        outputArea.setText("Waiting for response...");
        try {
            if (responseExpected) {
                String response = client.sendMessage(
                    decoder.apply(inputArea.getText()),
                    String.class,
                    true
                );
                outputArea.setText(encoder.apply(response));
            } else {
                client.sendMessage(decoder.apply(inputArea.getText()), true);
            }
        } catch (ServerException e) {
            outputArea.setText(encoder.apply(e.getError()));
        }
    }
    
    @FXML
    public void setLoginTemplate() throws Exception {
        responseExpected = true;
        inputArea.setText(encoder.apply(new LogInRequest("Mike")));
    }
    
    @FXML
    public void setLogoutTemplate() throws Exception {
        responseExpected = false;
        inputArea.setText(encoder.apply(new LogOutRequest(token)));
    }
    
    @FXML
    public void setMoveRequestTemplate() throws Exception {
        responseExpected = true;
        inputArea.setText(encoder.apply(new MoveRequest(token)));
    }
    
    @FXML
    public void setCreateLobbyTemplate() throws Exception {
        responseExpected = true;
        inputArea.setText(encoder.apply(new CreateLobbyRequest("Mike", 2, token)));
    }
    
    @FXML
    public void setSubscribeTemplate() throws Exception {
        responseExpected = false;
        inputArea.setText(encoder.apply(new LobbyListSubscribeRequest(token)));
    }
    
    @FXML
    public void setUnsubscribeTemplate() throws Exception {
        responseExpected = false;
        inputArea.setText(encoder.apply(new LobbyListUnsubscribeRequest(token)));
    }
    
    @FXML
    public void setJoinTemplate() throws Exception {
        responseExpected = true;
        inputArea.setText(encoder.apply(new EnterLobbyRequest(token, lastLobbyId)));
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    
    }
}
