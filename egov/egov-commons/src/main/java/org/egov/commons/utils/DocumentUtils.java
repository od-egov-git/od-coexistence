package org.egov.commons.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.common.contstants.CommonConstants;
import org.egov.commons.DocumentUploads;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.filestore.service.FileStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DocumentUtils {
	   @Autowired
	    private FileStoreService fileStoreService;
	 public List<DocumentUploads> getDocumentDetails(final List<DocumentUploads> files, final Object object,
	            final String objectType) {
	        final List<DocumentUploads> documentDetailsList = new ArrayList<>();

	        Long id;
	        Method method;
	        try {
	            method = object.getClass().getMethod("getId", null);
	            id = (Long) method.invoke(object, null);
	        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
	                | InvocationTargetException e) {
	            throw new ApplicationRuntimeException("error.expense.bill.document.error", e);
	        }

	        for (DocumentUploads doc : files) {
	            final DocumentUploads documentDetails = new DocumentUploads();
	            documentDetails.setObjectId(id);
	            documentDetails.setObjectType(objectType);
	            documentDetails.setFileStore(fileStoreService.store(doc.getInputStream(), doc.getFileName(),
	                    doc.getContentType(), CommonConstants.FILESTORE_MODULECODE));
	            documentDetailsList.add(documentDetails);

	        }
	        return documentDetailsList;
	    }

}
