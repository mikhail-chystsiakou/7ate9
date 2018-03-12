package com.yatty.sevennine.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.yatty.sevennine.api.dto.ConnectRequest;
import com.yatty.sevennine.api.dto.DisconnectRequest;
import com.yatty.sevennine.api.dto.auth.LogInRequest;
import com.yatty.sevennine.backend.handlers.FinalCleanupHandler;
import com.yatty.sevennine.backend.handlers.codecs.JsonMessageDecoder;
import com.yatty.sevennine.backend.handlers.codecs.JsonMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
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
import java.util.ResourceBundle;

public class TCPClientStub extends Application implements Initializable {
    public static final Logger logger = LoggerFactory.getLogger(TCPClientStub.class);
    
    private static final SocketAddress DESTINATION_ADDRESS =
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

    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Pane mainPane = loader.load(this.getClass().getClassLoader()
                .getResourceAsStream("TCPClientStub.fxml")
        );
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }
    
    @Override
    public void stop() throws Exception {
        elg.shutdownGracefully();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    
    private Channel connect() throws Exception {
        
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(elg)
                .channel(NioSocketChannel.class)
                .remoteAddress(DESTINATION_ADDRESS)
                .handler(ci);
        return bootstrap.connect().sync().channel();
    }
    
    @FXML
    public void sendMessageAction() throws Exception {
        Channel channel = connect();
        logger.debug("Message sent: {}", channel.writeAndFlush(
                JsonMessageDecoder.decode(inputArea.getText())).sync().isSuccess()
        );
    }

    @FXML
    public void setConnectTemplate() throws Exception {
        ConnectRequest connectRequest = new ConnectRequest();
        connectRequest.setName("name");
        inputArea.setText(JsonMessageEncoder.encode(connectRequest));
    }

    @FXML
    public void setDisconnectTemplate() throws Exception {
        DisconnectRequest disconnectRequest = new DisconnectRequest();
        inputArea.setText(JsonMessageEncoder.encode(disconnectRequest));
    }
    
    @FXML
    public void setLoginTemplate() throws Exception {
        LogInRequest logInRequest = new LogInRequest();
        logInRequest.setName("Mike");
        inputArea.setText(JsonMessageEncoder.encode(logInRequest));
    }
    
    @ChannelHandler.Sharable
    public class InboundHandler
            extends SimpleChannelInboundHandler<Object> {
        
        @Override
        protected void channelRead0(ChannelHandlerContext ctx,
                                    Object msg) throws Exception {
            logger.debug("Message received");
            outputArea.setText(objectWriter.writeValueAsString(msg));
        }
    }
    
    @ChannelHandler.Sharable
    public class TCPChannelInitializer extends ChannelInitializer<SocketChannel> {
        private JsonMessageEncoder encoder = new JsonMessageEncoder();
        private InboundHandler handler = new InboundHandler();
        private FinalCleanupHandler finalCleanupHandler = new FinalCleanupHandler();
        
        @Override
        public void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addFirst(new JsonMessageDecoder());
            ch.pipeline().addLast(handler);
            ch.pipeline().addLast(new JsonMessageEncoder());
            ch.pipeline().addLast(finalCleanupHandler);
        }
    }
}
