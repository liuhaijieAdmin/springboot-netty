package netty.knowledge.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

// 演示心跳机制的服务端
public class Server {
    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        ServerBootstrap server = new ServerBootstrap();

        server.group(group);
        server.channel(NioServerSocketChannel.class);
        // 在这里开启了长连接配置，以及配置了自定义的初始化器
        server.childOption(ChannelOption.SO_KEEPALIVE, true);
        server.childHandler(new ServerInitializer());
        server.bind("127.0.0.1",8888);
    }
}
