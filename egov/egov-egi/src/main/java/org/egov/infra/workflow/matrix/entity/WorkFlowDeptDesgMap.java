/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2018  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */

package org.egov.infra.workflow.matrix.entity;

import org.egov.infra.persistence.entity.AbstractPersistable;
import org.hibernate.validator.constraints.SafeHtml;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import static org.egov.infra.workflow.matrix.entity.WorkFlowDeptDesgMap.SEQ_WF_DEPT_DESG_MAP;

@Entity
@Table(name = "EG_WF_DEPT_DESG_MAP")
@SequenceGenerator(name = SEQ_WF_DEPT_DESG_MAP, sequenceName = SEQ_WF_DEPT_DESG_MAP, allocationSize = 1)
public class WorkFlowDeptDesgMap extends AbstractPersistable<Long> implements Cloneable {

    public static final String SEQ_WF_DEPT_DESG_MAP = "SEQ_EG_WF_DEPT_DESG_MAP";
    private static final long serialVersionUID = 4954386159285858993L;
    @Id
    @GeneratedValue(generator = SEQ_WF_DEPT_DESG_MAP, strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @SafeHtml
    private String objectType;

    @SafeHtml
    private String currentState;

    @SafeHtml
    private String nextDepartment;
    
    @SafeHtml
    private String nextDesignation;
    
    @SafeHtml
    private String additionalRule;

    public WorkFlowDeptDesgMap(String objectType, String currentState, String nextDepartment,
			String nextDesignation, String additionalRule) {
		super();
		this.objectType = objectType;
		this.currentState = currentState;
		this.nextDepartment = nextDepartment;
		this.nextDesignation = nextDesignation;
		this.additionalRule= additionalRule;
	}

	public WorkFlowDeptDesgMap() {

    }

    @Override
    public WorkFlowDeptDesgMap clone() {
        return new WorkFlowDeptDesgMap(objectType, currentState, nextDepartment, nextDesignation, additionalRule);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(final String objectType) {
        this.objectType = objectType;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(final String currentState) {
        this.currentState = currentState;
    }

    public String getNextDepartment() {
		return nextDepartment;
	}

	public void setNextDepartment(String nextDepartment) {
		this.nextDepartment = nextDepartment;
	}
	
    public String getNextDesignation() {
        return nextDesignation;
    }

    public void setNextDesignation(final String nextDesignation) {
        this.nextDesignation = nextDesignation;
    }

	public String getAdditionalRule() {
		return additionalRule;
	}

	public void setAdditionalRule(String additionalRule) {
		this.additionalRule = additionalRule;
	}

}