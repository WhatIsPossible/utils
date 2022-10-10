package lex.utils.templet

import java.util.concurrent.*

/**
 * Thread pool
 * 1,降低资源消耗。通过复用已存在的线程和降低线程关闭的次数来尽可能降低系统性能损耗；
 * 2,提升系统响应速度。通过复用线程，省去创建线程的过程，因此整体上提升了系统的响应速度；
 * 3,提高线程的可管理性。线程是稀缺资源，如果无限制的创建，不仅会消耗系统资源，还会降低系统的稳定性，因此，需要使用线程池来管理线程。
 * @constructor Create empty Thread pool
 */
class ThreadPool private constructor() {

    private val executor = ThreadPoolExecutor(
        CORE_POOL_SIZE,
        MAXIMUM_POOL_SIZE,
        KEEP_ALIVE_TIME,
        /**
         * 超时时间单位，等待时间的单位。
         */
        TimeUnit.SECONDS,
        /**
         * 阻塞队列。存放还未被执行的任务，Queue队列结构遵循先进先出原则。
         * ArrayBlockingQueue,
         * LinkedBlockingQueue,
         * SynchronousQueue,
         * PriorityBlockingQueue。优先级队列,优先级高的任务先执行.
         */
        LinkedBlockingDeque(BLOCK_DEQUE_MAX_SIZE),
        /**
         * 创建新线程的工厂，可以用来设置线程名，在日志中可根据线程名来快速定位问题。
         * 默认为正常优先级、非守护线程
         */
        Executors.defaultThreadFactory(),
        /**
         * 拒绝策略。当任务超出线程池处理范围，定义如何抛弃任务。
         * ThreadPoolExecutor.AbortPolicy 满了就报错，但是已经进入的任务会执行完毕，这里 shutdown() 方法要放在 finally 中
         * ThreadPoolExecutor.CallerRunsPolicy 满了丢弃，直接在execute方法的调用线程中运行被拒绝的任务
         * ThreadPoolExecutor.DiscardOldestPolicy 满了就丢弃最早进入并且未处理的任务,执行当前任务.
         * ThreadPoolExecutor.DiscardPolicy 满了就丢弃
         */
        ThreadPoolExecutor.DiscardOldestPolicy()
    )

    private fun execute(runnable: Runnable) {
        executor.execute(runnable)
    }

    /**
     * 提交任务
     *
     * @param task
     * @return 根据返回值可以判断任务是否结束
     */
    fun submit(task: Runnable?): Future<*>? {
        return executor.submit(task)
    }

    fun isDone(future: Future<*>?): Boolean {
        return future == null || future.isDone
    }

    /**
     * 当提交一个任务时，
     * 1,如果当前核心线程池的线程个数没有达到corePoolSize,则会创建新的线程来执行所提交的任务.即使当前核心线程池有空闲的线程;
     * 如果当前核心线程池的线程个数已经达到了 corePoolSize，则不再重新创建线程;
     * 2,如果运行的线程个数等于或者大于 corePoolSize，则会将提交的任务存放到阻塞队列 workQueue 中;
     * 3,如果当前 workQueue 队列已满的话，则会创建新的线程来执行任务;
     * 4,如果线程个数已经超过了 maximumPoolSize，则会使用饱和策略 RejectedExecutionHandler 来进行处理。
     */
    companion object {

        /**
         * 核心线程数.
         * 不会被关闭和回收.
         * 可同时执行的任务得最大数量
         * 默认情况下即使线程空闲也需要保留，可通过allowCoreThreadTimeOut参数修改为true，可实现核心线程的回收。
         *
         */
        private const val CORE_POOL_SIZE = 10

        /**
         * 最大线程数。
         * 在线程池中允许存在的最大线程数。必须大于>=核心线程数。
         * 如果当阻塞队列已满时，并且当前线程池线程个数没有超过 maximumPoolSize 的话，就会创建新的线程来执行任务。
         */
        private const val MAXIMUM_POOL_SIZE = 10

        /**
         * 等待时间。
         * 如果当前线程池的线程个数已经超过了 corePoolSize，
         * 并且线程空闲时间超过了 keepAliveTime 的话，
         * 就会将这些空闲线程销毁，这样可以尽可能降低系统资源消耗。
         */
        private const val KEEP_ALIVE_TIME = 0L

        /**
         * 阻塞队列最大数量
         */
        private const val BLOCK_DEQUE_MAX_SIZE = 50


        @Volatile
        private var instance: ThreadPool? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: ThreadPool().also { instance = it }
        }
    }


}