package pl.pwojcik.drugmanager.ui.adddrug.fragment;


import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
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

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.EOFException;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.pwojcik.drugmanager.DrugmanagerApplication;
import pl.pwojcik.drugmanager.ui.adddrug.IDrugFound;
import pl.pwojcik.drugmanager.ui.adddrug.viewmodel.DrugViewModel;
import pl.pwojcik.drugmanager.ui.druginfo.AddDrugManualActivity;
import pl.pwojcik.drugmanager.ui.druginfo.DrugInfoActivity;
import pl.pwojcik.drugmanager.ui.uicomponents.DialogUtil;
import pwojcik.pl.archcomponentstestproject.R;


public class AddByBarcodeFragment extends Fragment implements DialogUtil.DialogUtilButtonListener {

    private static final int NO_TRIES = 3;
    @BindView(R.id.svCameraPreview)
    SurfaceView svCameraPreview;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private DrugViewModel drugViewModel;
    private int tryCounter = 0;




    public void getDrugData(String ean) {
        drugViewModel.getDrugByEan(ean)
                .subscribe(drug ->{
                            if (drug != null) {
                                Intent intent = new Intent(getContext(), DrugInfoActivity.class);
                                if (drug.getId() != 0) {
                                    intent.putExtra("DRUG_ID", drug.getId());
                                } else {
                                    intent.putExtra("DRUG", drug);
                                }
                                startActivity(intent);
                                getActivity().finish();
                            }
                        },
                        e -> {
                            if (e instanceof EOFException) {
                               String title = "Nie znaleziono leku w bazie";
                                tryCounter++;
                                if(tryCounter == NO_TRIES) {
                                    DialogUtil dialog = new DialogUtil(this);
                                    dialog.showYestNoDialog(getContext(), title, "Chcesz dodać lek ręcznie?");
                                }
                            }
                        }
                );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_by_barcode, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        drugViewModel = ViewModelProviders.of(this).get(DrugViewModel.class);
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
                        if (barcodes.size() > 0 && !(tryCounter == NO_TRIES)) {
                            getDrugData(barcodes.valueAt(0).displayValue);


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
    public void onPositiveButtonClicked() {
        tryCounter = NO_TRIES;
        Intent intent = new Intent(getContext(), AddDrugManualActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onNegativeButtonClicked() {
        tryCounter = 0;
    }
}
