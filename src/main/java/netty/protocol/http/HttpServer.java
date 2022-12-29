package netty.protocol.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

// 基于Netty提供的处理器实现HTTP服务器
public class HttpServer {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap server = new ServerBootstrap();
        server
            .group(boss,worker)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) {
                    ChannelPipeline pipeline = ch.pipeline();

                    // 添加一个Netty提供的HTTP处理器
                    pipeline.addLast(new HttpServerCodec());
                    pipeline.addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRead(ChannelHandlerContext ctx,
                                                Object msg) throws Exception {
                            // 在这里输出一下消息的类型
                            System.out.println("消息类型：" + msg.getClass());
                            super.channelRead(ctx, msg);
                        }
                    });
                    pipeline.addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx,
                                                    HttpRequest msg) throws Exception {
                            System.out.println("客户端的请求路径：" + msg.uri());

                            // 创建一个响应对象，版本号与客户端保持一致，状态码为OK/200
                            DefaultFullHttpResponse response =
                                    new DefaultFullHttpResponse(
                                            msg.protocolVersion(),HttpResponseStatus.OK);

                            // 构造响应内容
                            byte[] content = "<h1>Hi, ZhuZi!</h1>".getBytes();

                            // 设置响应头：告诉客户端本次响应的数据长度
                            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH,content.length);
                            // 设置响应主体
                            response.content().writeBytes(content);

                            // 向客户端写入响应数据
                            ctx.writeAndFlush(response);
                        }
                    });
                }
            })
            .bind("127.0.0.1",8888)
            .sync();
    }
}
