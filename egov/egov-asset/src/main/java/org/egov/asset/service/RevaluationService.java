package org.egov.asset.service;

import org.egov.asset.model.AssetMaster;
import org.egov.asset.model.AssetRevaluation;
import org.egov.asset.repository.RevaluationRepository;
import org.egov.infra.script.service.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RevaluationService {

	@Autowired
	private RevaluationRepository revaluationRepository;

	private final ScriptService scriptExecutionService;

	@Autowired
	public RevaluationService(final RevaluationRepository revaluationRepository,
			final ScriptService scriptExecutionService) {
		this.revaluationRepository = revaluationRepository;
		this.scriptExecutionService = scriptExecutionService;
	}

	public AssetRevaluation create(final AssetRevaluation assetRevaluation) {

		return revaluationRepository.save(assetRevaluation);
	}

	public String createVoucher(AssetRevaluation savedAssetRevaluation) {
		// TODO Auto-generated method stub
		return null;
	}

}
