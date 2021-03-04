package org.egov.egf.masters.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.egov.commons.Accountdetailkey;
import org.egov.commons.service.AccountDetailKeyService;
import org.egov.commons.service.AccountdetailtypeService;
import org.egov.commons.service.EntityTypeService;
import org.egov.egf.masters.repository.OtherpartyRepository;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.model.masters.OtherParty;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OtherPartyService implements EntityTypeService {

    @Autowired
    private OtherpartyRepository otherpartyRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AccountDetailKeyService accountDetailKeyService;

    @Autowired
    private AccountdetailtypeService accountdetailtypeService;

    public Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    public OtherParty getById(final Long id) {
        return otherpartyRepository.findOne(id);
    }

    @Transactional
    public OtherParty create(OtherParty otherParty) {

        setAuditDetails(otherParty);
        otherParty = otherpartyRepository.save(otherParty);
        //saveAccountDetailKey(otherParty);
        return otherParty;
    }

    @Transactional
    public void saveAccountDetailKey(OtherParty otherparty) {

        Accountdetailkey accountdetailkey = new Accountdetailkey();
        accountdetailkey.setDetailkey(otherparty.getId().intValue());
        accountdetailkey.setDetailname(otherparty.getName());
        accountdetailkey.setAccountdetailtype(accountdetailtypeService.findByName(otherparty.getClass().getSimpleName()));
        accountdetailkey.setGroupid(1);
        accountDetailKeyService.create(accountdetailkey);
    }

    @Transactional
    public OtherParty update(final OtherParty otherparty) {
        setAuditDetails(otherparty);
        return otherpartyRepository.save(otherparty);
    }

    private void setAuditDetails(OtherParty otherparty) {
        if (otherparty.getId() == null) {
            otherparty.setCreatedDate(new Date());
            otherparty.setCreatedBy(ApplicationThreadLocals.getUserId());
        }
        otherparty.setLastModifiedDate(new Date());
        otherparty.setLastModifiedBy(ApplicationThreadLocals.getUserId());
    }

    public List<OtherParty> search(final OtherParty otherparty) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<OtherParty> createQuery = cb.createQuery(OtherParty.class);
        final Root<OtherParty> otherpartys = createQuery.from(OtherParty.class);
        createQuery.select(otherpartys);
        final Metamodel m = entityManager.getMetamodel();
        final EntityType<OtherParty> OtherParty_ = m.entity(OtherParty.class);

        final List<Predicate> predicates = new ArrayList<Predicate>();
        if (otherparty.getName() != null) {
            final String name = "%" + otherparty.getName().toLowerCase() + "%";
            predicates.add(cb.isNotNull(otherpartys.get("name")));
            predicates.add(cb.like(
                    cb.lower(otherpartys.get(OtherParty_.getDeclaredSingularAttribute("name", String.class))), name));
        }
        if (otherparty.getCode() != null) {
            final String code = "%" + otherparty.getCode().toLowerCase() + "%";
            predicates.add(cb.isNotNull(otherpartys.get("code")));
            predicates.add(cb.like(
                    cb.lower(otherpartys.get(OtherParty_.getDeclaredSingularAttribute("code", String.class))), code));
        }

        createQuery.where(predicates.toArray(new Predicate[] {}));
        final TypedQuery<OtherParty> query = entityManager.createQuery(createQuery);
        return query.getResultList();

    }

    public List<OtherParty> getAllActivePartys() {
        return otherpartyRepository.findByStatus();
    }
    @Override
    public List<? extends org.egov.commons.utils.EntityType> getAllActiveEntities(Integer accountDetailTypeId) {
        // TODO Auto-generated method stub
        
        return otherpartyRepository.findByStatus();
    }

    @Override
    public List<? extends org.egov.commons.utils.EntityType> filterActiveEntities(String filterKey, int maxRecords,
            Integer accountDetailTypeId) {
        return otherpartyRepository.findByNameLikeIgnoreCaseOrCodeLikeIgnoreCase(filterKey + "%", filterKey + "%");
    }

    @Override
    public List getAssetCodesForProjectCode(Integer accountdetailkey) throws ValidationException {
        // TODO Auto-generated method stub
       
        
        return null;
    }

    @Override
    public List<? extends org.egov.commons.utils.EntityType> validateEntityForRTGS(List<Long> idsList)
            throws ValidationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<? extends org.egov.commons.utils.EntityType> getEntitiesById(List<Long> idsList) throws ValidationException {
        // TODO Auto-generated method stub
        return null;
    }

}
