package netty.knowledge.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ZhuziHandler extends ChannelInboundHandlerAdapter {
    public ZhuziHandler() {
        super();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 在这里面编写处理入站msg的核心代码.....
        // （如果要自定义msg的处理逻辑，请记住去掉下面这行代码）
        super.channelRead(ctx, msg);
    }
}
