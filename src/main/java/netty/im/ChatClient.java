package netty.im;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import netty.im.message.*;
import netty.protocol.chatMessage.ChatMessageCodec;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();

        ChatMessageCodec MESSAGE_CODEC = new ChatMessageCodec();
        CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);
        AtomicBoolean LOGIN = new AtomicBoolean(false);
        AtomicBoolean EXIT = new AtomicBoolean(false);
        Scanner scanner = new Scanner(System.in);

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    // 用来判断是不是 读空闲时间过长，或 写空闲时间过长
                    // 3s 内如果没有向服务器写数据，会触发一个 IdleState#WRITER_IDLE 事件
                    ch.pipeline().addLast(new IdleStateHandler(0, 3, 0));
                    // ChannelDuplexHandler 可以同时作为入站和出站处理器
                    ch.pipeline().addLast(new ChannelDuplexHandler() {
                        // 用来触发特殊事件
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
                            IdleStateEvent event = (IdleStateEvent) evt;
                            // 触发了写空闲事件
                            if (event.state() == IdleState.WRITER_IDLE) {
                                //
                                ctx.writeAndFlush(new PingMessage());
                            }
                        }
                    });
                    ch.pipeline().addLast("client handler", new ChannelInboundHandlerAdapter() {
                        // 接收响应消息
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            System.out.println("msg：" + msg);
                            if ((msg instanceof LoginResponseMessage)) {
                                LoginResponseMessage response = (LoginResponseMessage) msg;
                                if (response.isSuccess()) {
                                    // 如果登录成功
                                    LOGIN.set(true);
                                }
                                // 唤醒 system in 线程
                                WAIT_FOR_LOGIN.countDown();
                            }
                        }

                        // 在连接建立后触发 active 事件
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            // 负责接收用户在控制台的输入，负责向服务器发送各种消息
                            new Thread(() -> {
                                System.out.println("请输入用户名:");
                                String username = scanner.nextLine();
                                if(EXIT.get()){
                                    return;
                                }
                                System.out.println("请输入密码:");
                                String password = scanner.nextLine();
                                if(EXIT.get()){
                                    return;
                                }
                                // 构造消息对象
                                LoginRequestMessage message = new LoginRequestMessage(username, password);
                                System.out.println(message);
                                // 发送消息
                                ctx.writeAndFlush(message);
                                System.out.println("等待后续操作...");
                                try {
                                    WAIT_FOR_LOGIN.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                // 如果登录失败
                                if (!LOGIN.get()) {
                                    ctx.channel().close();
                                    return;
                                }
                                while (true) {
                                    System.out.println("==================================");
                                    System.out.println("\t1、发送单聊消息");
                                    System.out.println("\t2、发送群聊消息");
                                    System.out.println("\t3、创建一个群聊");
                                    System.out.println("\t4、获取群聊成员");
                                    System.out.println("\t5、加入一个群聊");
                                    System.out.println("\t6、退出一个群聊");
                                    System.out.println("\t7、退出聊天系统");
                                    System.out.println("==================================");
                                    String command = null;
                                    try {
                                        command = scanner.nextLine();
                                    } catch (Exception e) {
                                        break;
                                    }
                                    if(EXIT.get()){
                                        return;
                                    }

                                    switch (command){
                                        case "1":
                                            System.out.print("请选择你要发送消息给谁：");
                                            String toUserName = scanner.nextLine();
                                            System.out.print("请输入你要发送的消息内容：");
                                            String content = scanner.nextLine();
                                            ctx.writeAndFlush(new ChatRequestMessage(username, toUserName, content));
                                            break;
                                        case "2":
                                            System.out.print("请选择你要发送消息的群聊：");
                                            String groupName = scanner.nextLine();
                                            System.out.print("请输入你要发送的消息内容：");
                                            String groupContent = scanner.nextLine();
                                            ctx.writeAndFlush(new GroupChatRequestMessage(username, groupName, groupContent));
                                            break;

                                        case "3":
                                            System.out.print("请输入你要创建的群聊昵称：");
                                            String newGroupName = scanner.nextLine();
                                            System.out.print("请选择你要邀请的群成员（不同成员用、分割）：");
                                            String members = scanner.nextLine();
                                            Set<String> memberSet = new HashSet<>(Arrays.asList(members.split("、")));
                                            memberSet.add(username); // 加入自己
                                            ctx.writeAndFlush(new GroupCreateRequestMessage(newGroupName, memberSet));
                                            break;

                                        case "4":
                                            System.out.print("请问要查看哪个群聊的成员呢：");
                                            String oldGroupName = scanner.nextLine();
                                            ctx.writeAndFlush(new GroupMembersRequestMessage(oldGroupName));
                                            break;

                                        case "5":
                                            System.out.print("请输入要加入的群聊昵称：");
                                            String joinGroupName = scanner.nextLine();
                                            ctx.writeAndFlush(new GroupJoinRequestMessage(username, joinGroupName));
                                            break;

                                        case "6":
                                            System.out.print("请输入要退出的群聊昵称：");
                                            String quitGroupName = scanner.nextLine();
                                            ctx.writeAndFlush(new GroupQuitRequestMessage(username, quitGroupName));
                                            break;

                                        case "7":
                                            ctx.channel().close();
                                            return;
                                    }
                                }
                            }, "system in").start();
                        }

                        // 在连接断开时触发
                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            System.out.println("连接已经断开，按任意键退出..");
                            EXIT.set(true);
                        }

                        // 在出现异常时触发
                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            System.out.println("连接已经断开，按任意键退出.." + cause.getMessage());
                            EXIT.set(true);
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect("localhost", 8888).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            System.out.println("客户端出现错误：" + e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
