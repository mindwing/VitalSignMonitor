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
    TextView view;

    T data;

    /**
     * 생성자
     * @param _view 데이터를 표시할 TextView
     * @param _observer Observer 객체
     */
    public ObservableData(TextView _view, Observer _observer) {
        view = _view;
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
            Log.d(TAG, "Observe registration action is successful:");
        } else if (OcResource.OnObserveListener.DEREGISTER == sequenceNumber) {
            Log.d(TAG, "Observe De-registration action is successful");
        } else if (OcResource.OnObserveListener.NO_OPTION == sequenceNumber) {
            Log.d(TAG, "Observe registration or de-registration action is failed");
        }

        Log.d(TAG, "OBSERVE Result:\tSequenceNumber: " + sequenceNumber);

        try {
            parseData(ocRepresentation);

            Log.w(TAG, "parseData() - " + data);
            view.post(this);
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            Log.d(TAG, "Failed to get the attribute values");
        }

        //        mFoundLightResource.cancelObserve();
    }

    @Override
    public synchronized void onObserveFailed(Throwable ex) {

    }

    /**
     * 직접 데이터를 설정해서 화면에 표시함
     * @param _data
     */
    public void setData(T _data) {
        data = _data;

        view.post(this);
    }

    void notifyObserver() {
        observer.update(null, null);
    }

    @Override
    public void run() {
        view.setText(data.toString());
    }
}
