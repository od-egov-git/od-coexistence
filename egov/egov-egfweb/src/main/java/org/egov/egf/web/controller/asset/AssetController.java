package org.egov.egf.web.controller.asset;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.egov.asset.contract.AssetCurrentValueRequest;
import org.egov.asset.contract.AssetCurrentValueResponse;
import org.egov.asset.contract.AssetRequest;
import org.egov.asset.contract.AssetResponse;
import org.egov.asset.contract.DepreciationReportResponse;
import org.egov.asset.contract.DepreciationRequest;
import org.egov.asset.contract.DepreciationResponse;
import org.egov.asset.contract.DisposalRequest;
import org.egov.asset.contract.DisposalResponse;
import org.egov.asset.contract.RequestInfoWrapper;
import org.egov.asset.contract.RevaluationRequest;
import org.egov.asset.contract.RevaluationResponse;
import org.egov.asset.exception.ErrorResponse;
import org.egov.egf.web.model.Asset;
import org.egov.egf.web.model.AssetCriteria;
import org.egov.egf.web.model.Depreciation;
import org.egov.egf.web.model.DepreciationReportCriteria;
import org.egov.egf.web.model.Disposal;
import org.egov.egf.web.model.DisposalCriteria;
import org.egov.egf.web.model.Revaluation;
import org.egov.egf.web.model.RevaluationCriteria;
import org.egov.egf.web.model.enums.TransactionType;
import org.egov.egf.web.service.asset.AssetCommonService;
import org.egov.egf.web.service.asset.AssetService;
import org.egov.egf.web.service.asset.CurrentValueService;
import org.egov.egf.web.service.asset.DepreciationService;
import org.egov.egf.web.service.asset.DisposalService;
import org.egov.egf.web.service.asset.RevaluationService;
import org.egov.egf.web.service.asset.validator.AssetValidator;
import org.egov.asset.web.wrapperfactory.ResponseInfoFactory;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/assets")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @Autowired
    private AssetValidator assetValidator;

    @Autowired
    private RevaluationService revaluationService;

    @Autowired
    private CurrentValueService currentValueService;

    @Autowired
    private DisposalService disposalService;

    @Autowired
    private AssetCommonService assetCommonService;

    @Autowired
    private DepreciationService depreciationservice;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @PostMapping("_search")
    @ResponseBody
    public ResponseEntity<?> search(@RequestBody @Valid final RequestInfoWrapper requestInfoWrapper,
            @ModelAttribute @Valid final AssetCriteria assetCriteria, final BindingResult bindingResult) {
        log.debug("assetCriteria::" + assetCriteria + "requestInfoWrapper::" + requestInfoWrapper);

        if (bindingResult.hasErrors()) {
            final ErrorResponse errorResponse = assetCommonService.populateErrors(bindingResult);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

        }
        if (assetCriteria.getTransaction() != null)
            if (assetCriteria.getTransaction().toString().equals(TransactionType.DEPRECIATION.toString())) {
                final List<ErrorResponse> errorResponses = assetValidator.validateSearchAssetDepreciation(assetCriteria);
                if (!errorResponses.isEmpty())
                    return new ResponseEntity<>(errorResponses, HttpStatus.BAD_REQUEST);
            }

        if (assetCriteria.getGrossValue() != null && assetCriteria.getFromCapitalizedValue() != null
                && assetCriteria.getToCapitalizedValue() != null)
            throw new RuntimeException(
                    "Gross Value should not be present with from capitalized value and to capitalized value");

        final List<Asset> assets = assetService.getAssets(assetCriteria, requestInfoWrapper.getRequestInfo());
        return getAssetResponse(assets, requestInfoWrapper.getRequestInfo());
    }

    @PostMapping("_paginatedsearch")
    @ResponseBody
    public ResponseEntity<?> paginatedSearch(@ModelAttribute @Valid final AssetCriteria assetCriteria,
            @RequestBody @Valid final RequestInfoWrapper requestInfoWrapper,
            final BindingResult bindingResult) {
        final RequestInfo requestInfo = requestInfoWrapper.getRequestInfo();
        if (bindingResult.hasErrors()) {
            final ErrorResponse errorResponse = assetCommonService.populateErrors(bindingResult);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

        }

        // Call service
        Map<String, Object> assetMap = null;
        try {
            assetMap = assetService.getPaginatedAssets(assetCriteria, requestInfoWrapper.getRequestInfo());
        } catch (final Exception exception) {
            log.error("Error while processing request " + assetCriteria, exception);
            throw new RuntimeException("Error while processing request for assets");
        }

        return getPaginatedSuccessResponse(assetMap, requestInfo);
    }

    @PostMapping("_create")
    @ResponseBody
    public ResponseEntity<?> create(@RequestBody @Valid final AssetRequest assetRequest,
            final BindingResult bindingResult) {
        log.debug("create asset:" + assetRequest);
        if (bindingResult.hasErrors()) {
            final ErrorResponse errorResponse = assetCommonService.populateErrors(bindingResult);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        final List<ErrorResponse> errorResponses = assetValidator.validateAssetRequest(assetRequest);
        if (!errorResponses.isEmpty())
            return new ResponseEntity<List<ErrorResponse>>(errorResponses, HttpStatus.BAD_REQUEST);
        assetValidator.validateAsset(assetRequest);
        final Asset asset = assetService.createAsset(assetRequest);
        final List<Asset> assets = new ArrayList<>();
        assets.add(asset);

        return getAssetResponse(assets, assetRequest.getRequestInfo());
    }

    @PostMapping("_update")
    @ResponseBody
    public ResponseEntity<?> update(@RequestBody final AssetRequest assetRequest, final BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            final ErrorResponse errorResponse = assetCommonService.populateErrors(bindingResult);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        assetValidator.validateAssetForUpdate(assetRequest);
        final Asset asset = assetService.updateAsset(assetRequest);
        final List<Asset> assets = new ArrayList<>();
        assets.add(asset);
        return getAssetResponse(assets, assetRequest.getRequestInfo());
    }

    @PostMapping("revaluation/_create")
    @ResponseBody
    public ResponseEntity<?> revaluate(@RequestBody @Valid final RevaluationRequest revaluationRequest,
            final BindingResult bindingResult, @RequestHeader final HttpHeaders headers) {

        log.debug("create revaluate:" + revaluationRequest);
        if (bindingResult.hasErrors()) {
            final ErrorResponse errorResponse = assetCommonService.populateErrors(bindingResult);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        log.debug("Request Headers :: " + headers);
        assetValidator.validateRevaluation(revaluationRequest);

        final Revaluation revaluation = revaluationService.saveRevaluation(revaluationRequest, headers);
        final List<Revaluation> revaluations = new ArrayList<>();
        revaluations.add(revaluation);
        return getRevaluationResponse(revaluations, revaluationRequest.getRequestInfo());

    }

    @PostMapping("revaluation/_search")
    @ResponseBody
    public ResponseEntity<?> revaluateSearch(@RequestBody @Valid final RequestInfoWrapper requestInfoWrapper,
            @ModelAttribute @Valid final RevaluationCriteria revaluationCriteria, final BindingResult bindingResult) {

        log.debug("revaluateSearch revaluationCriteria:" + revaluationCriteria);
        if (bindingResult.hasErrors()) {
            final ErrorResponse errorResponse = assetCommonService.populateErrors(bindingResult);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        assetValidator.validateRevaluationCriteria(revaluationCriteria);

        final List<Revaluation> revaluations = revaluationService.search(revaluationCriteria,
                requestInfoWrapper.getRequestInfo());

        return getRevaluationResponse(revaluations, requestInfoWrapper.getRequestInfo());
    }

    @PostMapping("dispose/_create")
    @ResponseBody
    public ResponseEntity<?> dispose(@RequestBody @Valid final DisposalRequest disposalRequest,
            final BindingResult bindingResult, @RequestHeader final HttpHeaders headers) {

        log.debug("create dispose:" + disposalRequest);
        if (bindingResult.hasErrors()) {
            final ErrorResponse errorResponse = assetCommonService.populateErrors(bindingResult);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        log.debug("Request Headers :: " + headers);
        assetValidator.validateDisposal(disposalRequest);

        final Disposal disposal = disposalService.saveDisposal(disposalRequest, headers);
        final List<Disposal> disposals = new ArrayList<>();
        disposals.add(disposal);
        log.debug("dispose :" + disposals);
        return getDisposalResponse(disposals, disposalRequest.getRequestInfo());
    }

    @PostMapping("dispose/_search")
    @ResponseBody
    public ResponseEntity<?> disposalSearch(@RequestBody @Valid final RequestInfoWrapper requestInfoWrapper,
            @ModelAttribute @Valid final DisposalCriteria disposalCriteria, final BindingResult bindingResult) {

        log.debug("disposalSearch disposalCriteria:" + disposalCriteria);
        if (bindingResult.hasErrors()) {
            final ErrorResponse errorResponse = assetCommonService.populateErrors(bindingResult);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        assetValidator.validateDisposalCriteria(disposalCriteria);

        final List<Disposal> disposals = disposalService.search(disposalCriteria,
                requestInfoWrapper.getRequestInfo());
        return getDisposalResponse(disposals, requestInfoWrapper.getRequestInfo());
    }

    @PostMapping("currentvalue/_search")
    @ResponseBody
    public ResponseEntity<?> getAssetCurrentValue(
            @RequestParam(name = "assetIds", required = true) final Set<Long> assetIds,
            @RequestParam(name = "tenantId", required = true) final String tenantId,
            @RequestBody @Valid final RequestInfoWrapper requestInfoWrapper, final BindingResult bindingResult) {

        log.debug("getAssetCurrentValue assetId:" + assetIds + ",tenantId:" + tenantId);
        if (bindingResult.hasErrors()) {
            final ErrorResponse errorResponse = assetCommonService.populateErrors(bindingResult);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        final AssetCurrentValueResponse assetCurrentValueResponse = currentValueService.getCurrentValues(assetIds,
                tenantId, requestInfoWrapper.getRequestInfo());

        log.debug("getAssetCurrentValue assetCurrentValueResponse:" + assetCurrentValueResponse);
        return new ResponseEntity<>(assetCurrentValueResponse, HttpStatus.OK);
    }

    @PostMapping("currentvalue/_create")
    @ResponseBody
    public ResponseEntity<?> saveCurrentValue(
            @RequestBody @Valid final AssetCurrentValueRequest assetCurrentValueRequest,
            final BindingResult bindingResult) {
        log.debug("create assetcurrentvalue :" + assetCurrentValueRequest);
        if (bindingResult.hasErrors()) {
            final ErrorResponse errorResponse = assetCommonService.populateErrors(bindingResult);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        final AssetCurrentValueResponse assetCurrentValueResponse = currentValueService
                .createCurrentValue(assetCurrentValueRequest);
        return new ResponseEntity<>(assetCurrentValueResponse, HttpStatus.CREATED);
    }

    @PostMapping("depreciations/_create")
    @ResponseBody
    public ResponseEntity<?> saveDepreciation(@RequestBody @Valid final DepreciationRequest depreciationRequest,
            final BindingResult bindingResult, @RequestHeader final HttpHeaders headers) {
        log.debug("create depreciationRequest :" + depreciationRequest);
        if (bindingResult.hasErrors()) {
            final ErrorResponse errorResponse = assetCommonService.populateErrors(bindingResult);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        log.debug("Request Headers :: " + headers);
        final Depreciation depreciation = depreciationservice.saveDepreciateAsset(depreciationRequest,
                headers);
        log.debug("depreciations :" + depreciation);
        return getDepreciationResponse(depreciation, depreciationRequest.getRequestInfo());
    }

    @PostMapping("depreciations/_search")
    @ResponseBody
    public ResponseEntity<?> depreciationReport(@RequestBody @Valid final RequestInfoWrapper requestInfoWrapper,
            @ModelAttribute @Valid final DepreciationReportCriteria depreciationReportCriteria,
            final BindingResult bindingResult) {
        log.debug("depreciationReportCriteria :" + depreciationReportCriteria);
        if (bindingResult.hasErrors()) {
            final ErrorResponse errorResponse = assetCommonService.populateErrors(bindingResult);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        final DepreciationReportResponse depreciationResponse = depreciationservice.getDepreciationReport(
                requestInfoWrapper.getRequestInfo(),
                depreciationReportCriteria);
        return new ResponseEntity<>(depreciationResponse, HttpStatus.OK);
    }

    private ResponseEntity<?> getAssetResponse(final List<Asset> assetList,
            final RequestInfo requestInfo) {
        final AssetResponse assetRes = new AssetResponse();
        assetRes.setAssets(assetList);
        final ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestHeaders(requestInfo);
        responseInfo.setStatus(HttpStatus.OK.toString());
        assetRes.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestHeaders(requestInfo));
        return new ResponseEntity<AssetResponse>(assetRes, HttpStatus.OK);
    }

    private ResponseEntity<?> getRevaluationResponse(final List<Revaluation> revaluationList,
            final RequestInfo requestInfo) {
        final RevaluationResponse revaluationRes = new RevaluationResponse();
        revaluationRes.setRevaluations(revaluationList);
        final ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestHeaders(requestInfo);
        responseInfo.setStatus(HttpStatus.OK.toString());
        revaluationRes.setResposneInfo(responseInfoFactory.createResponseInfoFromRequestHeaders(requestInfo));
        return new ResponseEntity<RevaluationResponse>(revaluationRes, HttpStatus.OK);
    }

    public ResponseEntity<?> getDisposalResponse(final List<Disposal> disposals, final RequestInfo requestInfo) {
        final DisposalResponse disposalResponse = new DisposalResponse();
        disposalResponse.setDisposals(disposals);
        final ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestHeaders(requestInfo);
        responseInfo.setStatus(HttpStatus.OK.toString());
        disposalResponse.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestHeaders(requestInfo));
        return new ResponseEntity<DisposalResponse>(disposalResponse, HttpStatus.OK);
    }

    public ResponseEntity<?> getDepreciationResponse(final Depreciation depreciations, final RequestInfo requestInfo) {
        final DepreciationResponse depreciationRes = new DepreciationResponse();
        depreciationRes.setDepreciation(depreciations);
        final ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestHeaders(requestInfo);
        responseInfo.setStatus(HttpStatus.OK.toString());
        depreciationRes.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestHeaders(requestInfo));
        return new ResponseEntity<DepreciationResponse>(depreciationRes, HttpStatus.OK);
    }

    public ResponseEntity<?> getPaginatedSuccessResponse(final Map<String, Object> requestMap, final RequestInfo requestInfo) {
        final ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestHeaders(requestInfo);

        final Map<String, Object> response = new LinkedHashMap<>();
        response.put("ResponseInfo", responseInfo);
        requestMap.forEach(response::put);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}