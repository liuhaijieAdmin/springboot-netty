package netty.hello;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class NettyClient {
    public static void main(String[] args) {
        // 由于无需处理连接事件，所以只需要创建一个EventLoopGroup
        EventLoopGroup worker = new NioEventLoopGroup();
        // 创建一个客户端（同之前的Socket、SocketChannel）
        Bootstrap client = new Bootstrap();
        try {
            client.group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc)
                                throws Exception {
                            // 添加一个编码处理器，对数据编码为UTF-8格式
                            sc.pipeline().addLast(new
                                    StringEncoder(CharsetUtil.UTF_8));
                        }
                    });
            // 与指定的地址建立连接
            ChannelFuture cf = client.connect("127.0.0.1", 8888).sync();
            // 建立连接成功后，向服务端发送数据
            System.out.println("正在向服务端发送信息......");
            cf.channel().writeAndFlush("我是<竹子爱熊猫>！");
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }
}
