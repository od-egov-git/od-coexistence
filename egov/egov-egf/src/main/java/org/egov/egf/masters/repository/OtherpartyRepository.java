package org.egov.egf.masters.repository;

import java.util.List;

import org.egov.model.masters.OtherParty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OtherpartyRepository extends JpaRepository<OtherParty, Long> {

    public List<OtherParty> findByNameLikeIgnoreCaseOrCodeLikeIgnoreCase(String name, String code);
    
    @Query("from OtherParty where status.code='Active'")
    public List<OtherParty> findByStatus();

}
