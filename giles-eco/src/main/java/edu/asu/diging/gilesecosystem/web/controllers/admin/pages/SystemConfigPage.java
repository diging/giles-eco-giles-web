package edu.asu.diging.gilesecosystem.web.controllers.admin.pages;

public class SystemConfigPage {

    private String registeredApps;
    private String digilibScalerUrl;
    private String gilesUrl;
    private String jarsUrl;
    private String jarsFileUrl;
    private String metadataServiceDocUrl;
    private int defaultPageSize;
    private String iframingAllowedHosts;
    private boolean showGithubLogin;
    private boolean showGoogleLogin;
    private boolean showMitreidLogin;
    private String gilesFilesTmpDir;
    private String nepomukAccessToken;
    
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
    public int getDefaultPageSize() {
        return defaultPageSize;
    }
    public void setDefaultPageSize(int defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
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
    public String getGilesFilesTmpDir() {
        return gilesFilesTmpDir;
    }
    public void setGilesFilesTmpDir(String gilesFilesTmpDir) {
        this.gilesFilesTmpDir = gilesFilesTmpDir;
    }
    public String getNepomukAccessToken() {
        return nepomukAccessToken;
    }
    public void setNepomukAccessToken(String nepomukAccessToken) {
        this.nepomukAccessToken = nepomukAccessToken;
    }
}
