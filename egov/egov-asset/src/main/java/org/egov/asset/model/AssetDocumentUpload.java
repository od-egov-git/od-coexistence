package org.egov.asset.model;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.egov.infra.filestore.entity.FileStoreMapper;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.SafeHtml;

@Entity
@Table(name = "ASSET_DOCUMENTS")
@SequenceGenerator(name = AssetDocumentUpload.SEQ_ASSET_DOCUMENTS, sequenceName = AssetDocumentUpload.SEQ_ASSET_DOCUMENTS, allocationSize = 1)
public class AssetDocumentUpload implements Serializable {

    public static final String SEQ_ASSET_DOCUMENTS = "SEQ_ASSET_DOCUMENTS";

    @Id
    @GeneratedValue(generator = SEQ_ASSET_DOCUMENTS, strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "objectid")
    private Long objectId;

    @NotNull
    @SafeHtml
    @Length(max = 128)
    private String objectType;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "filestoreid")
    private FileStoreMapper fileStore;

    @Column(name = "uploadeddate")
    private Date uploadedDate;

    private transient ByteArrayInputStream inputStream;

    private transient String fileName;

    private transient String contentType;

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(final String objectType) {
        this.objectType = objectType;
    }

    public FileStoreMapper getFileStore() {
        return fileStore;
    }

    public void setFileStore(final FileStoreMapper fileStore) {
        this.fileStore = fileStore;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(final Long objectId) {
        this.objectId = objectId;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public ByteArrayInputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(ByteArrayInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Date getUploadedDate() {
        return uploadedDate;
    }

    public void setUploadedDate(Date uploadedDate) {
        this.uploadedDate = uploadedDate;
    }
    
    public Date getCreatedDate(){
        return fileStore.getCreatedDate();
    }
    
}