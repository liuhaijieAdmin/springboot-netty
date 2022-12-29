package netty.hello;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;

public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        // 创建两个EventLoopGroup，boss：处理连接事件，worker处理I/O事件
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        // 创建一个ServerBootstrap服务端（同之前的ServerSocket类似）
        ServerBootstrap server = new ServerBootstrap();
        try {
            // 将前面创建的两个EventLoopGroup绑定在server上
            server.group(boss,worker)
                    // 指定服务端的通道为Nio类型
                    .channel(NioServerSocketChannel.class)
                    // 为到来的客户端Socket添加处理器
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        // 这个只会执行一次（主要是用于添加更多的处理器）
                        @Override
                        protected void initChannel(NioSocketChannel ch) {
                            // 添加一个字符解码处理器：对客户端的数据解码
                            ch.pipeline().addLast(
                                    new StringDecoder(CharsetUtil.UTF_8));
                            // 添加一个入站处理器，对收到的数据进行处理
                            ch.pipeline().addLast(
                                    new SimpleChannelInboundHandler<String>() {
                                        // 读取事件的回调方法
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext
                                                                            ctx,String msg) {
                                            System.out.println("收到客户端信息：" + msg);
                                        }
                                    });
                        }
                    });
            // 为当前服务端绑定IP与端口地址(sync是同步阻塞至连接成功为止)
            ChannelFuture cf = server.bind("127.0.0.1",8888).sync();
            // 关闭服务端的方法（之后不会在这里关闭）
            cf.channel().closeFuture().sync();
        }finally {
            // 优雅停止之前创建的两个Group
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
