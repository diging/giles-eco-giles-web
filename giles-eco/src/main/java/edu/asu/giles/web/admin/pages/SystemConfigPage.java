package edu.asu.giles.web.admin.pages;

public class SystemConfigPage {

    private String registeredApps;
    private String digilibScalerUrl;
    private String gilesUrl;
    private String pdfToImageDpi;
    private String pdfToImageType;
    private String pdfToImageFormat;
    private boolean pdfExtractText;
    private String jarsUrl;
    private String jarsFileUrl;
    private String metadataServiceDocUrl;
    private String tesseractBinFolder;
    private String tesseractDataFolder;
    private boolean tesseractCreateHOCR;
    private boolean ocrImagesFromPdfs;
    private int defaultPageSize;
    private String iframingAllowedHosts;
    private boolean showGithubLogin;
    private boolean showGoogleLogin;
    private boolean showMitreidLogin;
    
    public String getRegisteredApps() {
        return registeredApps;
    }
    public void setRegisteredApps(String registeredApps) {
        this.registeredApps = registeredApps;
    }
    public String getDigilibScalerUrl() {
        return digilibScalerUrl;
    }
    public void setDigilibScalerUrl(String digilibScalerUrl) {
        this.digilibScalerUrl = digilibScalerUrl;
    }
    public String getGilesUrl() {
        return gilesUrl;
    }
    public void setGilesUrl(String gilesUrl) {
        this.gilesUrl = gilesUrl;
    }
    public String getPdfToImageDpi() {
        return pdfToImageDpi;
    }
    public void setPdfToImageDpi(String pdfToImageDpi) {
        this.pdfToImageDpi = pdfToImageDpi;
    }
    public String getPdfToImageType() {
        return pdfToImageType;
    }
    public void setPdfToImageType(String pdfToImageType) {
        this.pdfToImageType = pdfToImageType;
    }
    public boolean isPdfExtractText() {
        return pdfExtractText;
    }
    public void setPdfExtractText(boolean pdfExtractText) {
        this.pdfExtractText = pdfExtractText;
    }
    public String getJarsUrl() {
        return jarsUrl;
    }
    public void setJarsUrl(String jarsUrl) {
        this.jarsUrl = jarsUrl;
    }
    public String getJarsFileUrl() {
        return jarsFileUrl;
    }
    public void setJarsFileUrl(String jarsFileUrl) {
        this.jarsFileUrl = jarsFileUrl;
    }
    public String getTesseractBinFolder() {
        return tesseractBinFolder;
    }
    public void setTesseractBinFolder(String tesseractBinFolder) {
        this.tesseractBinFolder = tesseractBinFolder;
    }
    public String getTesseractDataFolder() {
        return tesseractDataFolder;
    }
    public void setTesseractDataFolder(String tesseractDataFolder) {
        this.tesseractDataFolder = tesseractDataFolder;
    }
    public boolean isTesseractCreateHOCR() {
        return tesseractCreateHOCR;
    }
    public void setTesseractCreateHOCR(boolean tesseractCreateHOCR) {
        this.tesseractCreateHOCR = tesseractCreateHOCR;
    }
    public boolean isOcrImagesFromPdfs() {
        return ocrImagesFromPdfs;
    }
    public void setOcrImagesFromPdfs(boolean ocrImagesFromPdfs) {
        this.ocrImagesFromPdfs = ocrImagesFromPdfs;
    }
    public int getDefaultPageSize() {
        return defaultPageSize;
    }
    public void setDefaultPageSize(int defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }
    public String getPdfToImageFormat() {
        return pdfToImageFormat;
    }
    public void setPdfToImageFormat(String pdfToImageFormat) {
        this.pdfToImageFormat = pdfToImageFormat;
    }
    public String getMetadataServiceDocUrl() {
        return metadataServiceDocUrl;
    }
    public void setMetadataServiceDocUrl(String metadataServiceDocUrl) {
        this.metadataServiceDocUrl = metadataServiceDocUrl;
    }
    public String getIframingAllowedHosts() {
        return iframingAllowedHosts;
    }
    public void setIframingAllowedHosts(String iframingAllowedHosts) {
        this.iframingAllowedHosts = iframingAllowedHosts;
    }
    public boolean isShowGithubLogin() {
        return showGithubLogin;
    }
    public void setShowGithubLogin(boolean showGithubLogin) {
        this.showGithubLogin = showGithubLogin;
    }
    public boolean isShowGoogleLogin() {
        return showGoogleLogin;
    }
    public void setShowGoogleLogin(boolean showGoogleLogin) {
        this.showGoogleLogin = showGoogleLogin;
    }
    public boolean isShowMitreidLogin() {
        return showMitreidLogin;
    }
    public void setShowMitreidLogin(boolean showMitreidLogin) {
        this.showMitreidLogin = showMitreidLogin;
    }
}
