package com.weiwan.dsp.client.shutdown;
import com.weiwan.dsp.client.deploy.DeployRunner;

/**
 * @Author: xiaozhennan
 * @Date: 2020/9/30 14:51
 * @Package: com.weiwan.dsp.launcher.shutdown.ShutdownHook
 * @ClassName: ShutdownHook
 * @Description:
 **/
public class ShutdownHook extends Thread {

    public ShutdownHook(DeployRunner deployRunner) {
        this.deployRunner = deployRunner;
    }

    private DeployRunner deployRunner;

    @Override
    public void run() {
        //处理退出内容
        try {
            deployRunner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ;
    }
}