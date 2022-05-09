package com.weiwan.dsp.core.engine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author: xiaozhennan
 * @Date: 2021/6/13 17:41
 * @Package: com.weiwan.dsp.core.flow
 * @ClassName: ReliableRuntimeEntrance
 * @Description: 这里提供一个入口包装类, 防止异常的启动
 **/
public class ReliableRuntimeEntrance {
    public static final String RUN_JOB_FUNC = "runJob";
    private DspJobFlowEngine engine;
    private Throwable throwable;

    public ReliableRuntimeEntrance(DspJobFlowEngine engine) {
        this.engine = engine;
    }

    public void start() {
        //反射执行runJob方法
        if (engine == null) {
            throw new RuntimeException("Unable to start the task, the task engine could not be found");
        }

        Method runMethod = null;
        try {
            runMethod = DspJobFlowEngine.class.getDeclaredMethod(RUN_JOB_FUNC);
            if(runMethod != null){
                runMethod.setAccessible(true);
            }
            runMethod.invoke(engine);
        } catch (IllegalAccessException e) {
            throwable = e;
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            throwable = e;
            throw new RuntimeException("Error in calling Run Job method", e);
        } catch (NoSuchMethodException e) {
            throwable = e;
            throw new RuntimeException("Cannot find the Run Job method", e);
        }catch (Exception e){
            throwable = e;
            throw new RuntimeException("Unknown error occurred during task startup.", e);
        }finally {
            if(runMethod != null){
                runMethod.setAccessible(false);
            }
        }

    }


    public Throwable getThrowable(){
        return this.throwable;
    }


}
