package netty.knowledge.handler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.Charset;

public class HandlerServer {
    public static void main(String[] args) {
        // 0.准备工作：创建一个事件循环组、一个ServerBootstrap服务端
        EventLoopGroup group = new NioEventLoopGroup();
        ServerBootstrap server = new ServerBootstrap();

        server
            // 1.绑定前面创建的事件循环组
            .group(group)
            // 2.声明通道类型为服务端NIO通道
            .channel(NioServerSocketChannel.class)
            // 3.通过ChannelInitializer完成通道的初始化工作
            .childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel nsc) throws Exception {
                    // 4.获取通道的ChannelPipeline处理器链表
                    ChannelPipeline pipeline = nsc.pipeline();
                    // 5.基于pipeline链表向通道上添加入站处理器
                    pipeline.addLast("In-①",new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg)
                                                throws Exception {
                            System.out.println("俺是In-①入站处理器...");
                            ByteBuf buffer = (ByteBuf) msg;
                            String message = buffer.toString(Charset.defaultCharset());
                            buffer.release();
                            super.channelRead(ctx, message);
                        }
                    });
                    pipeline.addLast("In-②",new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg)
                                                throws Exception {
                            System.out.println("我是In-②入站处理器...");
                            super.channelRead(ctx, msg);
                        }
                    });
                    pipeline.addLast("In-③",new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg)
                                                throws Exception {
                            System.out.println("朕是In-③入站处理器...");

                            // 利用通道向客户端返回数据
                            ByteBuf resultMsg = ctx.channel().alloc().buffer();
                            resultMsg.writeBytes("111".getBytes());
                            nsc.writeAndFlush(resultMsg);
                        }
                    });

                    // 基于pipeline链表向通道上添加出站处理器
                    pipeline.addLast("Out-A",new ChannelOutboundHandlerAdapter(){
                        @Override
                        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
                                throws Exception {
                            System.out.println("在下是Out-A出站处理器...");
                            super.write(ctx, msg, promise);
                        }
                    });
                    pipeline.addLast("Out-B",new ChannelOutboundHandlerAdapter(){
                        @Override
                        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
                                throws Exception {
                            System.out.println("鄙人是Out-B出站处理器...");
                            super.write(ctx, msg, promise);
                        }
                    });
                    pipeline.addLast("Out-C",new ChannelOutboundHandlerAdapter(){
                        @Override
                        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
                                throws Exception {
                            System.out.println("寡人是Out-C出站处理器...");
                            super.write(ctx, msg, promise);
                        }
                    });
                }
            })
            // 为当前启动的服务端绑定IP和端口地址
            .bind("127.0.0.1",8888);
    }
}
