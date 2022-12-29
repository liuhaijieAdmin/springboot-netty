package netty.knowledge.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class HandlerClient {
    public static void main(String[] args) {
        // 0.准备工作：创建一个事件循环组、一个Bootstrap启动器
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap client = new Bootstrap();
        try {
            client
                // 1.绑定事件循环组
                .group(group)
                // 2.声明通道类型为NIO客户端通道
                .channel(NioSocketChannel.class)
                // 3.初始化通道，添加一个UTF-8的编码器
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc)
                            throws Exception {
                        // 添加一个编码处理器，对数据编码为UTF-8格式
                        ChannelPipeline pipeline = sc.pipeline();
                        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                    }
                });

            // 4.与指定的地址建立连接
            ChannelFuture cf = client.connect("127.0.0.1", 8888).sync();
            // 5.建立连接成功后，向服务端发送数据
            System.out.println("正在向服务端发送信息......");
            cf.channel().writeAndFlush("我是<竹子爱熊猫>！");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
