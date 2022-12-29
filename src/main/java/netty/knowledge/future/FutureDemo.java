package netty.knowledge.future;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FutureDemo {

    // 测试JDK-Future的方法
    public static void jdkFuture() throws Exception {
        System.out.println("--------JDK-Future测试--------");
        // 创建一个JDK线程池用于执行异步任务
        ExecutorService threadPool = Executors.newSingleThreadExecutor();

        System.out.println("主线程：步骤①");

        // 向线程池提交一个带有返回值的Callable任务
        java.util.concurrent.Future<String> task =
                threadPool.submit(() ->
                    "我是JDK-Future任务.....");
        // 输出获取到的任务执行结果（阻塞式获取）
        System.out.println(task.get());

        System.out.println("主线程：步骤②");
        // 关闭线程池
        threadPool.shutdownNow();
    }

    // 测试Netty-Future的方法
    public static void nettyFuture(){
        System.out.println("--------Netty-Future测试--------");
        // 创建一个Netty中的事件循环组（本质是线程池）
        NioEventLoopGroup group = new NioEventLoopGroup();
        EventLoop eventLoop = group.next();

        System.out.println("主线程：步骤①");

        // 向线程池中提交一个带有返回值的Callable任务
        io.netty.util.concurrent.Future<String> task =
                eventLoop.submit(() ->
                    "我是Netty-Future任务.....");

        // 添加一个异步任务执行完成之后的回调方法
        task.addListener(listenerTask ->
                System.out.println(listenerTask.getNow()));

        System.out.println("主线程：步骤②");
        // 关闭事件组（线程池）
        group.shutdownGracefully();
    }

    // 测试Netty-Promise的方法
    public static void nettyPromise() throws Exception {
        System.out.println("--------Netty-Promise测试--------");
        // 创建一个Netty中的事件循环组（本质是线程池）
        NioEventLoopGroup group = new NioEventLoopGroup();
        EventLoop eventLoop = group.next();

        // 主动创建一个传递异步任务结果的容器
        DefaultPromise<String> promise = new DefaultPromise<>(eventLoop);
        // 创建一条线程执行，往结果中添加数据
        new Thread(() -> {
            try {
                // 主动抛出一个异常
                int i = 100 / 0;
                // 如果异步任务执行成功，向容器中添加数据
                promise.setSuccess("我是Netty-Promise容器：执行成功！");
            }catch (Throwable throwable){
                // 如果任务执行失败，将异常信息放入容器中
                promise.setFailure(throwable);
            }
        }).start();
        // 输出容器中的任务结果
        System.out.println(promise.get());
    }

    public static void main(String[] args) throws Exception {
        jdkFuture();
        nettyFuture();
        nettyPromise();
    }
}
