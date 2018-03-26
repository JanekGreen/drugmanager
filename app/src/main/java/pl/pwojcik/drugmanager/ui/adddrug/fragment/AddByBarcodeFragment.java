package pl.pwojcik.drugmanager.ui.adddrug.fragment;


import android.Manifest;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.EOFException;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.pwojcik.drugmanager.ui.adddrug.fragment.barcodedetection.BarcodeGraphic;
import pl.pwojcik.drugmanager.ui.adddrug.fragment.barcodedetection.BarcodeGraphicTracker;
import pl.pwojcik.drugmanager.ui.adddrug.fragment.barcodedetection.BarcodeTrackerFactory;
import pl.pwojcik.drugmanager.ui.adddrug.fragment.barcodedetection.CameraSourcePreview;
import pl.pwojcik.drugmanager.ui.adddrug.fragment.barcodedetection.GraphicOverlay;
import pl.pwojcik.drugmanager.ui.adddrug.viewmodel.DrugViewModel;
import pl.pwojcik.drugmanager.ui.druginfo.AddDrugManualActivity;
import pl.pwojcik.drugmanager.ui.druginfo.DrugInfoActivity;
import pl.pwojcik.drugmanager.ui.uicomponents.DialogUtil;
import pl.pwojcik.drugmanager.utils.Constants;
import pwojcik.pl.archcomponentstestproject.R;


public class AddByBarcodeFragment extends Fragment implements DialogUtil.DialogUtilButtonListener, BarcodeGraphicTracker.BarcodeUpdateListener {

    private static final int RC_HANDLE_GMS = 9001;
    private CameraSource mCameraSource;
    private DrugViewModel drugViewModel;
    private int tryCounter = 0;

    @BindView(R.id.svCameraPreview)
    CameraSourcePreview mPreview;
    @BindView(R.id.graphicOverlay)
    GraphicOverlay<BarcodeGraphic> graphicOverlay;
    private boolean dialogActive = false;


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

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    Constants.CAMERA_PERMISSIONS);

        } else {
            System.out.println("Camera already permissions granted");
            setupCamera();
        }
        drugViewModel = ViewModelProviders.of(this).get(DrugViewModel.class);
    }

    private void setupCamera() {

        BarcodeDetector barcodeDetector = new BarcodeDetector
                .Builder(getContext())
                .setBarcodeFormats(Barcode.EAN_8 | Barcode.EAN_13)
                .build();

        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(graphicOverlay, getContext(), this);
        if (!barcodeDetector.isOperational()) {
            System.err.println("Barcode detector not working");
            return;
        }

        barcodeDetector.setProcessor(
                new MultiProcessor.Builder<>(barcodeFactory).build());

        mCameraSource = new CameraSource.Builder(getContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f)
                .build();

        if (mCameraSource == null) {
            System.err.println("Camera source is null");
            return;
        }

        startCameraSource();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions, grantResults);
        System.out.println("RequestPermissionsResult");
        switch (requestCode) {
            case Constants.CAMERA_PERMISSIONS: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Camera permissions granted after dialog response");
                    setupCamera();

                } else {
                    getActivity().finish();
                    System.out.println("Camera permissions denied");
                }

            }

        }
    }

    @Override
    public void onPositiveButtonClicked() {
        dialogActive = false;
        Intent intent = new Intent(getContext(), AddDrugManualActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onNegativeButtonClicked() {
        dialogActive = false;
        tryCounter = 0;
    }

    public void getDrugData(String ean) {
        drugViewModel.getDrugByEan(ean)
                .subscribe(drug -> {
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
                                String title = "Nie znaleziono leku w bazie ";
                                if (/*tryCounter == NO_TRIES && */!dialogActive) {
                                    dialogActive = true;
                                    DialogUtil dialog = new DialogUtil(this);
                                    dialog.showYestNoDialog(getContext(), title, "Chcesz dodać lek ręcznie?");
                                } else {
                                    tryCounter++;
                                    System.out.println("try counter" + tryCounter);

                                }
                            }
                        }
                );
    }


    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, graphicOverlay);
            } catch (IOException e) {
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    @Override
    public void onBarcodeDetected(Barcode barcode) {
        System.out.println("Barcode detected: " + barcode.displayValue);
        getDrugData(barcode.displayValue);
    }


    @Override
    public void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }
}
