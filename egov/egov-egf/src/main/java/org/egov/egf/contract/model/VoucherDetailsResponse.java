package org.egov.egf.contract.model;

import java.util.Date;
import org.egov.commons.Fund;
import org.egov.commons.Vouchermis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VoucherDetailsResponse {
	
	private Long id;
    private String name;
    private String type;
    private String description;
    private Date effectiveDate;
    private String voucherNumber;
    private Date voucherDate;
    private Fund fund;
    private String fundName;
    private Integer fiscalPeriodId;
    private Integer status;
    private Long originalvcId;
    private Integer isConfirmed;
    private Long refvhId;
    private String cgvn;
    private Integer moduleId; 
    private Vouchermis vouchermis;
    private String scheme;
    private String subScheme;
    private String financeSource;
    private String deptName;    
    private String narration;
    private String banNumber;
}
