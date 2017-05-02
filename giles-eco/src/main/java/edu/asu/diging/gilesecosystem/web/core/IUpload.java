package edu.asu.diging.gilesecosystem.web.core;

import edu.asu.diging.gilesecosystem.util.store.IStorableObject;

/**
 * @deprecated
 *      Use {@link edu.asu.diging.gilesecosystem.web.domain.IUpload} instead. This
 *      interface is only kept for migration purposes.
 * @author jdamerow
 *
 */
@Deprecated
public interface IUpload extends IStorableObject {

    public abstract String getId();

    public abstract void setId(String id);

    public abstract String getUsername();

    public abstract void setUsername(String username);

    public abstract String getCreatedDate();

    public abstract void setCreatedDate(String createdDate);

    public abstract void setUploadProgressId(String uploadProgressId);

    public abstract String getUploadProgressId();

}