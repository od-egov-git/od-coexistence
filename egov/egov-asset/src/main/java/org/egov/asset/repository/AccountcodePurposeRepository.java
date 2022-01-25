package org.egov.asset.repository;

import org.egov.asset.model.AccountcodePurpose;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountcodePurposeRepository extends JpaRepository<AccountcodePurpose, Long> {
public AccountcodePurpose findByName(String name);
}
