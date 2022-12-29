package netty.knowledge.heartbeat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

// 心跳机制的客户端处理器
public class HeartbeatClientHandler extends ChannelInboundHandlerAdapter {
    // 通用的心跳包数据
    private static final ByteBuf HEARTBEAT_DATA =
            Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("I am Alive", CharsetUtil.UTF_8));

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object event) throws Exception {
        // 如果当前触发的事件是闲置事件
        if (event instanceof IdleStateEvent) {
            IdleStateEvent idleEvent = (IdleStateEvent) event;
            // 如果当前通道触发了写闲置事件
            if (idleEvent.state() == IdleState.WRITER_IDLE){
                // 表示当前客户端有一段时间未向服务端发送数据了，
                // 为了防止服务端关闭当前连接，手动发送一个心跳包
                ctx.channel().writeAndFlush(HEARTBEAT_DATA.duplicate());
//                System.out.println("成功向服务端发送心跳包....");
            } else {
                super.userEventTriggered(ctx, event);
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("正在与服务端建立连接....");
        // 建立连接成功之后，先向服务端发送一条数据
        ctx.channel().writeAndFlush("我是会发心跳包的客户端-A！");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务端主动关闭了连接....");
        super.channelInactive(ctx);
    }
}
