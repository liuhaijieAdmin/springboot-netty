package netty.knowledge.NettyEventLoopGroup;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.TimeUnit;

public class NettyEventLoopGroup {

    public static void main(String[] args) {
        EventLoopGroup threadPool = new NioEventLoopGroup();
        // 递交Runnable类型的普通异步任务
        threadPool.execute(()->{
            System.out.println("execute()方法提交的任务....");
        });
        // 递交Callable类型的有返回异步任务
        threadPool.submit(() -> {
            System.out.println("submit()方法提交的任务....");
            return "我是执行结果噢！";
        });
        // 递交Callable类型的延时调度任务
        threadPool.schedule(()->{
            System.out.println("schedule()方法提交的任务，三秒后执行....");
            return "调度执行后我会返回噢！";
        },3,TimeUnit.SECONDS);
        // 递交Runnable类型的延迟间隔调度任务
        threadPool.scheduleAtFixedRate(()->{
            System.out.println("scheduleAtFixedRate()方法提交的任务....");
        },3,1,TimeUnit.SECONDS);

        threadPool.shutdownGracefully();
    }

}
