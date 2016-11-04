package edu.asu.diging.gilesecosystem.web.aspects.access.openid.google;

public enum ValidationResult {
    AUDIENCE_MISMATCH,
    CLIENT_ID_MISMATCH,
    VALID,
    INVALID,
    EXPIRED,
    REVOKED
}
