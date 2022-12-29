package netty.knowledge.frameDecoder.shortConnection;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import netty.knowledge.frameDecoder.ServerInitializer;

// 演示半包问题的服务端
public class HalfPackageServer {

    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        ServerBootstrap server = new ServerBootstrap();

        server.group(group);
        server.channel(NioServerSocketChannel.class);
        // 调整服务端的接收缓冲区大小为16字节（最小为16，无法设置更小）
        server.childOption(ChannelOption.RCVBUF_ALLOCATOR,
                new AdaptiveRecvByteBufAllocator(16,16,16));
        server.childHandler(new ServerInitializer());
        server.bind("127.0.0.1",8888);
    }
}
