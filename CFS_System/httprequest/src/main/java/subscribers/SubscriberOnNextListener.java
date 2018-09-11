package subscribers;

public interface SubscriberOnNextListener<T> {
    void onNext(T t);

    void onError(String msg);
}
