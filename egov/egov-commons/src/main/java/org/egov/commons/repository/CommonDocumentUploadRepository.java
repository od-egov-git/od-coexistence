package org.egov.commons.repository;


import org.egov.commons.DocumentUploads;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CommonDocumentUploadRepository extends JpaRepository<DocumentUploads, Long> {

    List<DocumentUploads> findByObjectId(Long objectId);

    List<DocumentUploads> findByObjectIdAndObjectType(Long objectId, String objectType);

    List<DocumentUploads> findByObjectType(String objectType);

    @Query("from DocumentUpload where uploadedDate <= :uploadedDate and objectType=:objectType and fileStore.fileName like %:fileName%")
    List<DocumentUploads> findByUploadedDateObjectTypeAndFileName(@Param("uploadedDate") Date uploadedDate, @Param("objectType") String objectType, @Param("fileName") String fileName);

    @Query("from DocumentUpload where uploadedDate <=:uploadedDate and objectId =:objectId  and fileStore.fileName like %:fileName%")
    List<DocumentUploads> findByUploadedDateObjectIdAndFileName(@Param("uploadedDate") Date uploadedDate, @Param("objectId") Long objectId, @Param("fileName") String fileName);

    @Query("from DocumentUpload where fileStore.fileStoreId = :fileStore")
    DocumentUploads findByFileStore(@Param("fileStore") String fileStore);
    
    @Query("from DocumentUpload where fileStore.fileName like %:fileName% and objectType=:objectType")
    List<DocumentUploads> findByFileNameAndObjectType(@Param("fileName") String fileName, @Param("objectType") String objectType);
    
    @Query("from DocumentUpload where fileStore.fileName like %:fileName% and objectId =:objectId")
    List<DocumentUploads> findByFileNameAndObjectId(@Param("fileName") String fileName, @Param("objectId") Long objectId);

}
