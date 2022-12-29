package netty.knowledge.heartbeat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

// 演示心跳机制的客户端（会发送心跳包）
public class ClientA {
    public static void main(String[] args) {
        EventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap client = new Bootstrap();
        try {
            client.group(worker);
            client.channel(NioSocketChannel.class);
            // 打开长连接配置
            client.option(ChannelOption.SO_KEEPALIVE, true);
            // 指定一个自定义的初始化器
            client.handler(new ClientInitializer());
            client.connect("127.0.0.1", 8888).sync();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
