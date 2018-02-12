package pl.pwojcik.drugmanager.ui.adddrug;


import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.pwojcik.drugmanager.DrugmanagerApplication;
import pwojcik.pl.archcomponentstestproject.R;


public class AddByBarcodeFragment extends Fragment {


    @BindView(R.id.svCameraPreview)
    SurfaceView svCameraPreview;
    @BindView(R.id.tvDetectedDrugName)
    TextView tvDetectedDrugName;
    @BindView(R.id.tvDetectedDrugProducer)
    TextView getTvDetectedDrugProducer;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private AddByBarcodeViewModel addByBarcodeViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_by_barcode,
                container,
                false);
        ButterKnife.bind(this, view);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println("onViewCreated called");

        // handling live data stuff

        addByBarcodeViewModel = ViewModelProviders.of(this).get(AddByBarcodeViewModel.class);
        subscribeToData();

        // barcode detection code

        barcodeDetector = new BarcodeDetector
                .Builder(getContext())
                .setBarcodeFormats(Barcode.EAN_8 | Barcode.EAN_13)
                .build();

        if (!barcodeDetector.isOperational()) {
            System.err.println("Barcode detector not working");
           return;
        }

        cameraSource = new CameraSource.Builder(getContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f)
                .build();

        if (cameraSource == null) {
            System.err.println("Camera source is null");
            return;
        }
        svCameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

                if (ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    System.err.println("Permissions are not added");
                    return;
                }


                DrugmanagerApplication.getExecutorSingleThread()
                        .submit(() -> {

                            try {
                                cameraSource.start(svCameraPreview.getHolder());

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                    @Override
                    public void release() {

                    }

                    @Override
                    public void receiveDetections(Detector.Detections<Barcode> detections) {

                            final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                            if (barcodes.size() > 0) {
                                    getActivity().runOnUiThread(() -> {
                                        addByBarcodeViewModel
                                                .getDrugByEan(barcodes.valueAt(0).displayValue);

                                    });
                            }
                    }
                });
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

                DrugmanagerApplication.getExecutorSingleThread()
                        .submit((() -> cameraSource.stop()));

            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        addByBarcodeViewModel.getData().removeObservers(this);
    }

    private void subscribeToData() {
        addByBarcodeViewModel.getData()
                .observe(this, drug -> {
                    assert drug != null;
                    tvDetectedDrugName.setText(drug.getName());
                    getTvDetectedDrugProducer.setText(drug.getProducer());
                });
    }
}
