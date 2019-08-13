package com.baffalotech.integration.http.netty.springboot;

import java.math.BigDecimal;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.Filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baffalotech.integration.http.netty.core.util.AbstractRecycler;
import com.baffalotech.integration.http.netty.core.util.ThreadPoolX;
import com.baffalotech.integration.http.netty.servlet.NettyServletFilterChain;
import com.baffalotech.integration.http.netty.servlet.handler.HttpMessageToServletRunnable;

/**
 * 统计服务器信息的任务
 * @author 84215
 */
public class NettyReportRunnable implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(NettyReportRunnable.class);
    private AtomicInteger reportCount = new AtomicInteger();
    private long beginTime = System.currentTimeMillis();

    public static void start(){
        ThreadPoolX.getDefaultInstance().scheduleAtFixedRate(new NettyReportRunnable(),5,5, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        try {
          
            long totalTime = System.currentTimeMillis() - beginTime;

            long servletQueryCount = HttpMessageToServletRunnable.SERVLET_QUERY_COUNT.get();
            long servletAndFilterTime = HttpMessageToServletRunnable.SERVLET_AND_FILTER_TIME.get();
            long servletTime = NettyServletFilterChain.SERVLET_TIME.get();
            long filterTime = NettyServletFilterChain.FILTER_TIME.get();

            double servletAndFilterAvgRuntime = servletQueryCount == 0? 0:(double)servletAndFilterTime/(double)servletQueryCount;
            double servletAvgRuntime = servletQueryCount ==0? 0:(double)servletTime/(double)servletQueryCount;
            double filterAvgRuntime = servletQueryCount ==0? 0:(double)filterTime/(double)servletQueryCount;

            StringJoiner filterJoin = new StringJoiner(", ");
            for(Filter filter : NettyServletFilterChain.FILTER_SET){
//                    double filterAvgTime = (double)e.getValue().get() / (double)servletQueryCount;
                filterJoin.add(
                        filter.getClass().getSimpleName()
                );
            }

            StringJoiner joiner = new StringJoiner(", ");
            joiner.add("\r\n第"+reportCount.incrementAndGet()+"次统计 ");
            joiner.add("时间="+(totalTime/60000)+"分"+((totalTime % 60000 ) / 1000)+"秒 ");
            joiner.add("servlet执行次数="+ servletQueryCount);
            joiner.add("servlet+filter平均时间="+ formatRate(servletAndFilterAvgRuntime,4)+"ms,");
            joiner.add("servlet平均时间="+ formatRate(servletAvgRuntime,4)+"ms, ");
            joiner.add("filter平均时间="+ formatRate(filterAvgRuntime,4)+"ms, ");
//            joiner.add("\r\n "+filterJoin.toString());

            int recyclerTotal = AbstractRecycler.TOTAL_COUNT.get();
            int recyclerHit = AbstractRecycler.HIT_COUNT.get();
            double hitRate = (double) recyclerHit/(double) recyclerTotal;
            joiner.add("\r\n获取实例次数="+ recyclerTotal+"次");
            joiner.add("实例命中="+ recyclerHit+"次");
            joiner.add("实例命中率="+ formatRate(hitRate * 100,0)+"%");

            addMessage(joiner);

            logger.info(joiner.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String formatRate(double num, int rate){
        if(Double.isNaN(num)){
            return "0";
        }
        if(num == (int)num){
            return String.valueOf(((int)num));
        }
        return new BigDecimal(num).setScale(rate,BigDecimal.ROUND_HALF_DOWN).stripTrailingZeros().toString();
    }

    protected void addMessage(StringJoiner messageJoiner){

    }

}