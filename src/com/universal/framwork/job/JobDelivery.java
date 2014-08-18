package com.universal.framwork.job;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
/**
 * 分发消息
 * <p/>
 * @author mengliwei@qiyi.com<br/> 
 * @created  2014-5-12下午5:58:46
 * @updated  2014-5-12下午5:58:46
 *
 */
public class JobDelivery{

    private  InternalHandler mHandler;
    //message:complete task
    public static final int MSG_TASK_SUCCESS = 0x01;
    public static final int MSG_TASK_START=0x02;
    public static final int MSG_TASK_CANCLE=0x03;
    public static final int MSG_TASK_FAILURE=0x04;
    
    private static class InternalHandler extends Handler {
        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public void handleMessage(Message msg) {
            TaskResult result = (TaskResult) msg.obj;
            switch(msg.what)
            {
              case MSG_TASK_START:
                result.mTask.onStart();
                break;
              case MSG_TASK_SUCCESS:
                result.mTask.onSuccess(result.mData[0]);
                break;
              case MSG_TASK_CANCLE:
                result.mTask.onCancel();
                break;
              case MSG_TASK_FAILURE:
                result.mTask.onFailure((Exception) result.mData[0]);
            }
        }
    }

    public JobDelivery() {
        mHandler = new InternalHandler();
    }

    /**
     * delivery message
     * 
     * @param task
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <RESULT> void  delivery(int what,BaseJob job, RESULT result) {
        Message message = mHandler.obtainMessage(what, new TaskResult<RESULT>(job, result));
        message.sendToTarget();
    }

}
class TaskResult<RESULT> {
    @SuppressWarnings("rawtypes")
    public BaseJob mTask;
    public RESULT[] mData;
    public TaskResult(@SuppressWarnings("rawtypes") BaseJob task, RESULT... data) {
        mTask = task;
        mData = data;
    }
}
