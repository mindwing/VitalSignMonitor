package kr.re.etri.iotivity.smartwearable;

import android.util.Log;
import android.widget.TextView;

import org.iotivity.base.OcException;
import org.iotivity.base.OcHeaderOption;
import org.iotivity.base.OcRepresentation;
import org.iotivity.base.OcResource;

import java.util.List;
import java.util.Observer;

/**
 * Observing 할 Resource 데이터와 이 데이터를 화면에 보여줄 컴포넌트를 함께 관리하는 클래스
 */
public abstract class ObservableData<T> implements
        HealthCareResourceSpec,
        OcResource.OnObserveListener,
        Runnable {
    String TAG;

    Observer observer;
    TextView[] views;

    T data;

    /**
     * 생성자
     * @param _views 데이터를 표시할 TextView 배열
     * @param _observer Observer 객체
     */
    public ObservableData(TextView[] _views, Observer _observer) {
        views = _views;
        observer = _observer;

        TAG = getClass().getSimpleName();
    }

    /**
     * 서버로부터 전달받은 데이터를 파싱함
     * @param ocRepresentation
     * @throws OcException
     */
    public abstract void parseData(OcRepresentation ocRepresentation) throws OcException;

    @Override
    public synchronized void onObserveCompleted(List<OcHeaderOption> headerOptionList, OcRepresentation ocRepresentation, int sequenceNumber) {
        if (OcResource.OnObserveListener.REGISTER == sequenceNumber) {
            Utils.log("onObserveCompleted(): Observe registration action is successful");
            Log.d(TAG, "Observe registration action is successful");
        } else if (OcResource.OnObserveListener.DEREGISTER == sequenceNumber) {
            Utils.log("onObserveCompleted(): Observe De-registration action is successful");
            Log.d(TAG, "Observe De-registration action is successful");
        } else if (OcResource.OnObserveListener.NO_OPTION == sequenceNumber) {
            Utils.log("onObserveCompleted(): Observe registration or de-registration action is failed");
            Log.e(TAG, "Observe registration or de-registration action is failed");
        }

        Log.d(TAG, "onObserveCompleted(): OBSERVE Result:\tSequenceNumber: " + sequenceNumber);

        try {
            parseData(ocRepresentation);

            Log.d(TAG, "parseData() - " + data);
            views[0].post(this);
        } catch (OcException e) {
            Utils.log("onObserveCompleted(): Failed to get the attribute values");
            Log.e(TAG, e.toString());
            Log.e(TAG, "Failed to get the attribute values");
        }

        //        mFoundLightResource.cancelObserve();
    }

    @Override
    public synchronized void onObserveFailed(Throwable e) {
        Utils.log("onObserveFailed()");
        Log.e(TAG, e.toString());
    }

    /**
     * 직접 데이터를 설정해서 화면에 표시함
     * @param _data
     */
    public void setData(T _data) {
        data = _data;

        views[0].post(this);
    }

    void notifyObserver() {
        observer.update(null, null);
    }

    @Override
    public void run() {
        views[3].setText(views[2].getText());
        views[2].setText(views[1].getText());
        views[1].setText(views[0].getText());
        views[0].setText(data.toString());
    }
}
