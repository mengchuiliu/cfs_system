package subscribers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import progress.ProgressCancelListener;
import progress.ProgressDialogHandler;
import rx.Subscriber;

/**
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束是，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
 * Created by liukun on 16/3/10.
 */
public class ProgressSubscriber<T> extends Subscriber<T> implements ProgressCancelListener {

    private SubscriberOnNextListener mSubscriberOnNextListener;
    private ProgressDialogHandler mProgressDialogHandler;

    private Context context;

    public ProgressSubscriber(SubscriberOnNextListener mSubscriberOnNextListener, Context context) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.context = context;
        mProgressDialogHandler = new ProgressDialogHandler(context, this, true);
    }

    public ProgressSubscriber(SubscriberOnNextListener mSubscriberOnNextListener, Context context, boolean cancelable) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.context = context;
        if (!cancelable) {
            mProgressDialogHandler = null;
        } else {
            mProgressDialogHandler = new ProgressDialogHandler(context, this, true);
        }
    }

    private void showProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            mProgressDialogHandler = null;
        }
    }

    /**
     * 订阅开始时调用
     * 显示ProgressDialog
     */
    @Override
    public void onStart() {
        showProgressDialog();
    }

    /**
     * 完成，隐藏ProgressDialog
     */
    @Override
    public void onCompleted() {
        dismissProgressDialog();
    }

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     */
    @Override
    public void onError(Throwable e) {
        dismissProgressDialog();
        if (!isConnected(context)) {
            mSubscriberOnNextListener.onError("网络连接失败，请稍后重试");
//            Toast.makeText(context, "网络连接失败，请稍后", Toast.LENGTH_LONG).show();
        } else if (e instanceof SocketTimeoutException) {
            mSubscriberOnNextListener.onError("网络连接失败，请稍后重试");
//            Toast.makeText(context, "网络连接失败，请稍后重试", Toast.LENGTH_LONG).show();
        } else if (e instanceof ConnectException) {
            mSubscriberOnNextListener.onError("网络连接失败，请稍后重试");
//            Toast.makeText(context, "网络连接失败，请稍后重试", Toast.LENGTH_LONG).show();
        } else {
            mSubscriberOnNextListener.onError(e.getMessage());
//            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("my_log", "==httpError错误==>" + e.getMessage());
        }
    }

    private boolean isConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivity) {
            @SuppressLint("MissingPermission") NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param t 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(T t) {
        if (mSubscriberOnNextListener != null) {
            dismissProgressDialog();
            mSubscriberOnNextListener.onNext(t);
        }
    }

    /**
     * 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
     */
    @Override
    public void onCancelProgress() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }
}