package edu.asu.diging.gilesecosystem.web.core.service.properties;


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
    public final static String KAFKA_TOPIC_COMPLETION_NOTIFICATION_REQUEST = "topic_completion_notification";
    
    public final static String GILES_TMP_FOLDER = "giles_files_tmp_dir";

    public final static String FREDDIE_HOST = "freddie_host";

    public final static String AUTHORIZATION_TYPE_ACCESS_TOKEN = "authorization_type_access_token";

    public final static String APPLICATION_ID = "application_id";
    
    public final static String BADGE_TEXT_EXTRACTION_SUBJECT = "text_extraction_subject";
    public final static String BADGE_TEXT_EXTRACTION_COLOR = "text_extraction_color";
    public final static String BADGE_IMAGE_EXTRACTION_SUBJECT = "image_extraction_subject";
    public final static String BADGE_IMAGE_EXTRACTION_COLOR = "image_extraction_color";
    public final static String BADGE_OCR_SUBJECT = "ocr_subject";
    public final static String BADGE_OCR_COLOR = "ocr_color";
    
    public final static String BADGE_STATUS_PREFIX = "status_";
    
    public final static String BADGE_PROCESSING_UPLOAD_IN_PROGRESS = "processing_upload_in_progress";
    public final static String BADGE_PROCESSING_UPLOAD_COMPLETE = "processing_upload_complete";
    public final static String BADGE_PROCESSING_UPLOAD_LABEL = "processing_upload_label";
    public final static String BADGE_PROCESSING_UPLOAD_COLOR = "processing_upload_color";
    
    public final static String EXTERNAL_BADGE_PREFIX = "processor_prefix";
    public final static String EXTERNAL_BADGE_COLOR_PREFIX = "processor_color_prefix";

    public final static String EMAIL_ENABLED = "email_enabled";
    public final static String EMAIL_FROM = "email_from";
    
    public final static String ZOOKEEPER_HOST = "zookeeper_host";
    public final static String ZOOKEEPER_PORT = "zookeeper_port";
    
    public final static String ZOOKEEPER_NEPOMUK_SERVICE_NAME = "zookeeepr_service_nepomuk_name";
    
    public final static String NEPOMUK_PING_ENDPOINT = "nepomuk_ping_endpoint";
    public final static String NEPOMUK_FILES_ENDPOINT = "nepomuk_files_endpoint";
    
    public final static String CURRENT_REQUESTS_MAX = "current_requests_max_number";
    
    public final static String CITESPHERE_CLIENT_ID = "citesphere_client_id";
    public final static String CITESPHERE_CLIENT_SECRET = "citesphere_client_secret";
    public final static String CITESPHERE_BASE_URL = "citesphere_base_url";
    public final static String CITESPHERE_TOKEN_ENDPOINT = "citesphere_token_endpoint";
    public final static String CITESPHERE_CHECK_TOKEN_ENDPOINT = "citesphere_check_token_endpoint";
    public final static String CITESPHERE_CHECK_ACCESS_ENDPOINT = "citesphere_check_access_endpoint";
    public final static String CITESPHERE_SCOPES = "citesphere_scopes";
}
