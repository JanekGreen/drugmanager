package pl.pwojcik.drugmanager.utils;

/**
 * Created by wojci on 29.01.2018.
 */

public class Constants {
    public static final String BASE_URL ="http://194.182.65.130:8080/drugmanager/";
    public static final String GET_DRUG_BY_EAN ="drugs/{ean}";
    public static final String GET_DRUG_BY_NAME="drugs/name/{name}";
    public static final String GET_NAME_SUGGESTION ="drugs/suggestions/{name}";

    public final static String ADD_BARCODE_TAG_NAME="GET_BY_BARCODE";
    public final static String ADD_NAME_TAG_NAME="GET_BY_NAME";
    public final static String RESULTS_FRAGMENT="RESULTS_FRAGMENT";

    public static final int INTENT_REQUEST_CODE = 17;
    public static final String DRUG_LIST = "DRUG_LIST__";
    public static final String DRUG_NOTIFICATION = "DRUG_NOTIFICATION__";
    public static final int CAMERA_PERMISSIONS = 666;
    public static final int STORAGE_PERMISSIONS = 667;
    public static final int EMPTY_VIEW = 668;
    public static final int BUSY_VIEW = 669;
}
