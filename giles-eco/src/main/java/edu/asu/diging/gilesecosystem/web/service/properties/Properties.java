package edu.asu.diging.gilesecosystem.web.service.properties;


public interface Properties {
    
    public final static String GITHUB_SHOW_LOGIN = "github_show_login";
    public final static String GITHUB_CLIENT_ID = "github_client_id";
    public final static String GITHUB_SECRET = "github_secret";
    public final static String GOOGLE_SHOW_LOGIN = "google_show_login";
    public final static String GOOGLE_CLIENT_ID = "google_client_id";
    public final static String GOOGLE_SECRET = "google_secret";
    public final static String MITREID_SHOW_LOGIN = "mitreid_show_login";
    public final static String MITREID_CLIENT_ID = "mitreid_client_id";
    public final static String MITREID_SECRET = "mitreid_secret";
    public final static String MITREID_SERVER_URL = "mitreid_server_url";
    public final static String MITREID_INTROSPECT_CLIENT_ID = "mitreid_introspect_clientId";
    public final static String MITREID_INTROSPECT_SECRET = "mitreid_introspect_secret";
    public final static String MITREID_INTROSPECT_URL = "mitreid_introspect_url";
    
    public final static String SIGNING_KEY = "jwt_signing_secret";
    public final static String SIGNING_KEY_APPS = "jwt_signing_secret_apps";
    
    public final static String DIGILIB_SCALER_URL = "digilib_scaler_url";
    public final static String GILES_URL = "giles_url";
    public final static String PDF_TO_IMAGE_DPI = "pdf_to_image_dpi";
    public final static String PDF_TO_IMAGE_TYPE = "pdf_to_image_type";
    public final static String PDF_EXTRACT_TEXT = "pdf_extract_text";
    public final static String PDF_TO_IMAGE_FORMAT = "pdf_to_image_format";
    public final static String JARS_URL = "jars_url";
    public final static String JARS_FILE_URL = "jars_file_url";
    
    public final static String DEFAULT_PAGE_SIZE = "default_page_size";
    public final static String OCR_IMAGES_FROM_PDFS = "ocr_images_from_pdfs";
    public final static String GILES_DIGILIB_ENDPOINT = "giles_digilib_endpoint";
    public final static String GILES_FILE_ENDPOINT = "giles_file_endpoint";
    public final static String GILES_FILE_CONTENT_SUFFIX = "giles_file_content_suffix";
    public final static String METADATA_SERVICE_DOC_ENDPOINT = "metadata_service_document_url";
    
    public final static String ALLOW_IFRAMING_FROM = "allow_iframing_from";
    public final static String EXPIRATION_TIME_UPLOADS_MS = "expiration_time_uploads_ms";
 
    public final static String KAFKA_HOSTS = "kafka_hosts";
    public final static String KAFKA_TOPIC_OCR_REQUEST = "request_ocr_topic";
    public final static String KAFKA_TOPIC_OCR_COMPLETE_REQUEST = "topic_orc_request_complete";
    public final static String KAFKA_TOPIC_STORAGE_REQUEST = "request_storage_topic";
    public final static String KAFKA_TOPIC_STORAGE_COMPLETE_REQUEST = "topic_storage_request_complete";
    public final static String KAFKA_TOPIC_TEXT_EXTRACTION_REQUEST = "request_text_extraction_topic";
    public final static String KAFKA_TOPIC_TEXT_EXTRACTION_COMPLETE_REQUEST = "topic_text_extraction_request_complete";
    public final static String KAFKA_TOPIC_IMAGE_EXTRACTION_REQUEST = "topic_image_extraction_request";
    public final static String KAFKA_TOPIC_IMAGE_EXTRACTION_COMPLETE_REQUEST = "topic_image_extraction_request_complete";
    
    public final static String GILES_TMP_FOLDER = "giles_files_tmp_dir";

    public final static String FREDDIE_HOST = "freddie_host";

    public final static String AUTHORIZATION_TYPE_ACCESS_TOKEN = "authorization_type_access_token";

    public final static String APPLICATION_ID = "application_id";
}
