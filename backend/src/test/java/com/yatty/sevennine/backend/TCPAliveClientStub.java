package com.yatty.sevennine.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.yatty.sevennine.api.dto.auth.LogInRequest;
import com.yatty.sevennine.api.dto.auth.LogOutRequest;
import com.yatty.sevennine.api.dto.game.MoveRequest;
import com.yatty.sevennine.api.dto.lobby.CreateLobbyRequest;
import com.yatty.sevennine.api.dto.lobby.EnterLobbyRequest;
import com.yatty.sevennine.api.dto.lobby.LobbyListSubscribeRequest;
import com.yatty.sevennine.api.dto.lobby.LobbyListUnsubscribeRequest;
import com.yatty.sevennine.backend.handlers.ExceptionHandler;
import com.yatty.sevennine.backend.handlers.codecs.JsonMessageDecoder;
import com.yatty.sevennine.backend.handlers.codecs.JsonMessageEncoder;
import com.yatty.sevennine.backend.util.PropertiesProvider;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import javafx.application.Application;
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
import java.net.SocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TCPAliveClientStub extends Application implements Initializable {
    public static final Logger logger = LoggerFactory.getLogger(TCPAliveClientStub.class);
    private static volatile Channel aliveChannel;
    private static volatile String token;
    private static volatile String lastLobbyId;
    
    private static SocketAddress destinationAddress =
            new InetSocketAddress("127.0.0.1", 39405);
    private EventLoopGroup elg = new NioEventLoopGroup();
    private ChannelInitializer ci = new TCPChannelInitializer();

    private ObjectWriter objectWriter = new ObjectMapper()
            .writerWithDefaultPrettyPrinter();

    @FXML
    Button sendButton;
    @FXML
    TextArea inputArea;
    @FXML
    TextArea outputArea;

    public static void main(String[] args) throws Exception {
        Properties environmentProperties = PropertiesProvider.getEnvironmentProperties();
        destinationAddress = new InetSocketAddress(
                environmentProperties.getProperty(PropertiesProvider.Environment.HOST),
                Integer.valueOf(environmentProperties.getProperty(PropertiesProvider.Environment.PORT))
        );
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        connect();
        FXMLLoader loader = new FXMLLoader();
        Pane mainPane = loader.load(this.getClass().getClassLoader()
                .getResourceAsStream("TCPAliveClientStub.fxml")
        );
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }
    
    @Override
    public void stop() throws Exception {
        elg.shutdownGracefully();
    }
    
    private void connect() throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(elg)
                .channel(NioSocketChannel.class)
                .remoteAddress(destinationAddress)
                .handler(ci);
        aliveChannel = bootstrap.connect().sync().channel();
        aliveChannel.closeFuture().addListener(e -> {
            logger.debug("Connection closed, reopening...");
            connect();
        });
    }
    
    @FXML
    public void sendMessageAction() throws Exception {
        aliveChannel.writeAndFlush(JsonMessageDecoder.decode(inputArea.getText())).sync();
    }
    
    @FXML
    public void setLoginTemplate() throws Exception {
        inputArea.setText(JsonMessageEncoder.encode(new LogInRequest("Mike")));
    }
    
    @FXML
    public void setLogoutTemplate() throws Exception {
        inputArea.setText(JsonMessageEncoder.encode(new LogOutRequest(token)));
    }
    
    @FXML
    public void setMoveRequestTemplate() throws Exception {
        inputArea.setText(JsonMessageEncoder.encode(new MoveRequest(token)));
    }
    
    @FXML
    public void setCreateLobbyTemplate() throws Exception {
        inputArea.setText(JsonMessageEncoder.encode(new CreateLobbyRequest("Mike", 2, token)));
    }
    
    @FXML
    public void setSubscribeTemplate() throws Exception {
        inputArea.setText(JsonMessageEncoder.encode(new LobbyListSubscribeRequest(token)));
    }
    
    @FXML
    public void setUnsubscribeTemplate() throws Exception {
        inputArea.setText(JsonMessageEncoder.encode(new LobbyListUnsubscribeRequest(token)));
    }
    
    @FXML
    public void setJoinTemplate() throws Exception {
        inputArea.setText(JsonMessageEncoder.encode(new EnterLobbyRequest(token, lastLobbyId)));
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    
    }
    
    @ChannelHandler.Sharable
    public class InboundHandler
            extends SimpleChannelInboundHandler<String> {
        
        @Override
        protected void channelRead0(ChannelHandlerContext ctx,
                                    String msg) throws Exception {
//            logger.debug("Message received");
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
//            Platform.runLater(() -> {
//                logger.debug("output: {}, msg: {}", outputArea, msg);
//            });
        }
    }
    
    @ChannelHandler.Sharable
    public class TCPChannelInitializer extends ChannelInitializer<SocketChannel> {
        private JsonMessageEncoder encoder = new JsonMessageEncoder();
        private InboundHandler handler = new InboundHandler();
        private ExceptionHandler finalCleanupHandler = new ExceptionHandler();
        
        @Override
        public void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addFirst(new ByteToMessageDecoder() {
                @Override
                protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                    String data = in.toString(StandardCharsets.UTF_8);
                    System.out.println(data);
                    out.add(data);
                    in.readBytes(in.readableBytes());
                }
            });
            ch.pipeline().addLast(handler);
            ch.pipeline().addLast(encoder);
//            ch.pipeline().addLast(finalCleanupHandler);
        }
    }
}
