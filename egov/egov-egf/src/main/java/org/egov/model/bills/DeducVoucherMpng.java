package org.egov.model.bills;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "DEDUC_VOUCHER_MPNG")
@SequenceGenerator(name = DeducVoucherMpng.SEQ_DEDUC_VOUCHER_MPNG, sequenceName = DeducVoucherMpng.SEQ_DEDUC_VOUCHER_MPNG, allocationSize = 1)
public class DeducVoucherMpng {

	public static final String SEQ_DEDUC_VOUCHER_MPNG = "SEQ_DEDUC_VOUCHER_MPNG";

    @Id
    @GeneratedValue(generator = SEQ_DEDUC_VOUCHER_MPNG, strategy = GenerationType.SEQUENCE)
    private Long id;
    
    private Long ph_id;
    
    private Long vh_id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPh_id() {
		return ph_id;
	}

	public void setPh_id(Long ph_id) {
		this.ph_id = ph_id;
	}

	public Long getVh_id() {
		return vh_id;
	}

	public void setVh_id(Long vh_id) {
		this.vh_id = vh_id;
	}
}
