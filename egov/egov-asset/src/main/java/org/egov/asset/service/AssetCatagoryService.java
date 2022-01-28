package org.egov.asset.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.egov.asset.model.AssetCatagory;
import org.egov.asset.model.AssetCatagoryType;
import org.egov.asset.model.CustomFieldDataType;
import org.egov.asset.model.CustomeFields;
import org.egov.asset.model.DepriciationMethod;
import org.egov.asset.model.ParentCatagory;
import org.egov.asset.model.UnitOfMeasurement;
import org.egov.asset.repository.AssetCatagoryRepository;
import org.egov.asset.repository.AssetCatagoryTypeRepository;
import org.egov.asset.repository.CustomFieldDataTypeRepository;
import org.egov.asset.repository.CustomeFieldsRepository;
import org.egov.asset.repository.DepriciationMethodRepository;
import org.egov.asset.repository.ParentCatagoryRepository;
import org.egov.asset.repository.UnitOfMeasurementRepository;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.repository.CChartOfAccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssetCatagoryService {

	private static final Logger LOGGER = Logger.getLogger(AssetCatagoryService.class);

	@Autowired
	private AssetCatagoryTypeRepository assetCatagoryTypeRepository;
	/*@Autowired
	private AccumulatedDepriciationCodeRepository accumulatedDepriciationCodeRepository;
	@Autowired
	private AssetAccountCodeRepository assetAccountCodeRepository;
	@Autowired
	private DepriciationExpenseAccountRepository depriciationExpenseAccountRepository;
	@Autowired
	private RevolutionReserveAccountCodeRepository revolutionReserveAccountCodeRepository;*/
	@Autowired
	private DepriciationMethodRepository depriciationMethodRepository;
	@Autowired
	private ParentCatagoryRepository parentCatagoryRepository;
	
	@Autowired
	private UnitOfMeasurementRepository unitOfMeasurementRepository;
	@Autowired
	private CustomFieldDataTypeRepository customFieldDataTypeRepository;
	@Autowired
	private AssetCatagoryRepository assetCatagoryRepository;
	@Autowired
	private CustomeFieldsRepository customeFieldsRepository;
	@Autowired
	private CChartOfAccountsRepository cChartOfAccountsRepository;

	public List<CustomFieldDataType> getCustomFieldDataType() {
		return customFieldDataTypeRepository.findAll();
	}

	public List<UnitOfMeasurement> getUnitOfMeasurement() {
		return unitOfMeasurementRepository.findAll();
	}

	/*public List<RevolutionReserveAccountCode> getRevolutionReserveAccountCode() {
		return revolutionReserveAccountCodeRepository.findAll();
	}

	public List<ParentCatagory> getParentCatagory() {
		return parentCatagoryRepository.findAll();
	}

	public List<DepriciationExpenseAccount> getDepriciationExpenseAccount() {
		return depriciationExpenseAccountRepository.findAll();
	}

	

	public List<AssetAccountCode> getAssetAccountCode() {
		return assetAccountCodeRepository.findAll();
	}

	public List<AccumulatedDepriciationCode> getAccumulatedDepriciationCode() {
		return accumulatedDepriciationCodeRepository.findAll();
	}*/
	public List<DepriciationMethod> getDepriciationMethod() {
		return depriciationMethodRepository.findAll();
	}
	public List<ParentCatagory> getParentCatagory() {
		return parentCatagoryRepository.findAll();
	}
	public List<AssetCatagoryType> getAssetCatagoryType() {

		List<AssetCatagoryType> assetCatagoryTypeList = assetCatagoryTypeRepository.findAll();
		/*
		 * List<String> assetCatagorytypes=new ArrayList<String>();
		 * assetCatagoryTypeList.forEach(assetCatagotytype->{
		 * assetCatagorytypes.add(assetCatagotytype.getDescription()); });
		 */

		return assetCatagoryTypeList;
	}

	public AssetCatagory saveAssetCatagory(Long id) {
		return assetCatagoryRepository.getOne(id);
	}

	@Transactional
	public AssetCatagory createAssetCatagory(AssetCatagory assetCatagory) {

		AssetCatagory findByName = assetCatagoryRepository.findByName(assetCatagory.getName());
		if (null != findByName) {
			assetCatagory.setErrorMessage("Asset Category " + assetCatagory.getName() + " already exist..!!");
			return assetCatagory;
		}

		//AssetCatagory assetCatagoryOrm = new AssetCatagory();
		if(null!=assetCatagory.getRevolutionReserveAccountCode().getId()) {
			CChartOfAccounts revResAcc = cChartOfAccountsRepository.findOne(assetCatagory.getRevolutionReserveAccountCode().getId());
			assetCatagory.setRevolutionReserveAccountCode(revResAcc);
		}
		if(null!=assetCatagory.getDepriciationExpenseAccount().getId()) {
			CChartOfAccounts depExpAcc = cChartOfAccountsRepository.findOne(assetCatagory.getDepriciationExpenseAccount().getId());
			assetCatagory.setDepriciationExpenseAccount(depExpAcc);
		}
		if(null!=assetCatagory.getAssetAccountCode().getId()) {
			CChartOfAccounts assAccCode = cChartOfAccountsRepository.findOne(assetCatagory.getAssetAccountCode().getId());
			assetCatagory.setAssetAccountCode(assAccCode);
		}
		if(null!=assetCatagory.getAccumulatedDepriciationCode().getId()) {
			CChartOfAccounts accDepCode = cChartOfAccountsRepository.findOne(assetCatagory.getAccumulatedDepriciationCode().getId());
			assetCatagory.setAccumulatedDepriciationCode(accDepCode);
		}
		
		if (null != assetCatagory.getCustomeFields() && assetCatagory.getCustomeFields().size() > 0) {
			List<CustomeFields> customeFields = assetCatagory.getCustomeFields();
			List<CustomeFields> customeFieldList = new ArrayList<CustomeFields>();
			for (CustomeFields customeField : customeFields) {
				Date date = new Date(); 
				DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");  
				String createDate = dateFormat.format(date); 
				customeField.setCreateDate(createDate);
				long userid = Long.parseLong(assetCatagory.getUserid());
				customeField.setCreatedBy(userid);
				CustomFieldDataType dataType = customFieldDataTypeRepository.findByDataTypes(customeField.getDataType());
				customeField.setCustomFieldDataType(dataType);
				customeFieldList.add(customeField);
			}
			assetCatagory.setCustomeFields(customeFieldList);
			
		}
		
		
		
		if (null != assetCatagory.getDepriciationRate()) {
			Double depRate = getDepreciationRate(assetCatagory.getDepriciationRate());
			assetCatagory.setDepriciationRate(depRate);
		}
		if (null != assetCatagory.getUserid()) {
			long userid = Long.parseLong(assetCatagory.getUserid());
			assetCatagory.setCreatedBy(userid);
		}
		String assetCode = getAssetCode();
		assetCatagory.setAssetCode(assetCode);
		Date date = new Date(); 
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");  
		String createDate = dateFormat.format(date);
		assetCatagory.setCreateDate(createDate);
		
		System.out.println("Before saving requet " + assetCatagory);
		
		
		
		return assetCatagoryRepository.save(assetCatagory);
	}
	public AssetCatagory updateAssetCategory(AssetCatagory assetCategory) {
		
		Long updatedBy=assetCategory.getUpdatedBy();
		List<CustomeFields> customFields=assetCategory.getCustomeFields();
		List<CustomeFields> customFieldOrm=new ArrayList<>();
		CustomeFields customField=assetCategory.getCustomeField();
		if(assetCategory.getCustomeFields().size()>0) {
			int size=assetCategory.getCustomeFields().size();
			for(int i=0;i<size;i++) {
				CustomeFields customeField = customFields.get(i);
				if(customeField.getId()==assetCategory.getCustomeField().getId()) {
				}else {
					CustomFieldDataType dataType=customFieldDataTypeRepository.findByDataTypes(customeField.getDataType());
					customeField.setCustomFieldDataType(dataType);
					customFieldOrm.add(customeField);
				}
				
			}
		}
		Date date = new Date(); 
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
		String updatedDate = dateFormat.format(date);
		assetCategory.setUpdateDate(updatedDate);
		if(null!=assetCategory.getCustomeField().getName()) {
		customField.setDataType(assetCategory.getCustomeField().getCustomFieldDataType().getDataTypes());
		
		customField.setUpdateDate(updatedDate);
		customField.setUpdatedBy(assetCategory.getUpdatedBy());
		customFieldOrm.add(customField);
		assetCategory.setCustomeFields(customFieldOrm);
		}
		AssetCatagory findOne = assetCatagoryRepository.findOne(assetCategory.getId());
		if(null!=assetCategory.getName()) {
			findOne.setName(assetCategory.getName());
//		}if(null!=assetCategory.getAccumulatedDepriciationCode().getDescription()) {
		}if(null!=assetCategory.getAccumulatedDepriciationCode().getId()) {
			CChartOfAccounts accDepCode = cChartOfAccountsRepository.findOne(assetCategory.getAccumulatedDepriciationCode().getId());
			findOne.setAccumulatedDepriciationCode(accDepCode);
			//findOne.setAccumulatedDepriciationCode(assetCategory.getAccumulatedDepriciationCode());
		}if(null!=assetCategory.getCreatedBy()) {
			findOne.setCreatedBy(assetCategory.getCreatedBy());
		}if(null!=assetCategory.getDepriciationRate()) {
			findOne.setDepriciationRate(assetCategory.getDepriciationRate());
		}if(null!=assetCategory.getUpdatedBy()) {
			findOne.setUpdatedBy(assetCategory.getUpdatedBy());
		}if(null!=assetCategory.getCreateDate()) {
			findOne.setCreateDate(assetCategory.getCreateDate());
		}if(null!=assetCategory.getUpdateDate()) {
			findOne.setUpdateDate(assetCategory.getUpdateDate());
		//}if(null!=assetCategory.getAssetAccountCode().getDescription()) {
		}if(null!=assetCategory.getAssetAccountCode().getId()) {
			CChartOfAccounts assetAccountCode = cChartOfAccountsRepository.findOne(assetCategory.getAssetAccountCode().getId());
			findOne.setAssetAccountCode(assetAccountCode);
			//findOne.setAssetAccountCode(assetCategory.getAssetAccountCode());
		}if(null!=assetCategory.getAssetCatagoryType().getDescription()) {
			findOne.setAssetCatagoryType(assetCategory.getAssetCatagoryType());
		}if(null!=assetCategory.getAssetCode()) {
			findOne.setAssetCode(assetCategory.getAssetCode());
		}if(null!=assetCategory.getUserid()) {
			findOne.setUserid(assetCategory.getUserid());
		}if(null!=assetCategory.getVersion()) {
			findOne.setVersion(assetCategory.getVersion());
		//}if(null!=assetCategory.getDepriciationExpenseAccount().getDescription()) {
		}if(null!=assetCategory.getDepriciationExpenseAccount().getId()) {
			CChartOfAccounts depExpAcc = cChartOfAccountsRepository.findOne(assetCategory.getDepriciationExpenseAccount().getId());
			findOne.setDepriciationExpenseAccount(depExpAcc);
			//findOne.setDepriciationExpenseAccount(assetCategory.getDepriciationExpenseAccount());
		}if(null!=assetCategory.getDepriciationMethod().getDescription()) {
			findOne.setDepriciationMethod(assetCategory.getDepriciationMethod());
		}if(null!=assetCategory.getParentCatagory().getDescription()) {
			findOne.setParentCatagory(assetCategory.getParentCatagory());
		//}if(null!=assetCategory.getRevolutionReserveAccountCode().getDescription()) {
		}if(null!=assetCategory.getRevolutionReserveAccountCode().getId()) {
			CChartOfAccounts revResAcc = cChartOfAccountsRepository.findOne(assetCategory.getRevolutionReserveAccountCode().getId());
			findOne.setRevolutionReserveAccountCode(revResAcc);
			//findOne.setRevolutionReserveAccountCode(assetCategory.getRevolutionReserveAccountCode());
		}if(null!=assetCategory.getUnitOfMeasurement().getDescription()) {
			findOne.setUnitOfMeasurement(assetCategory.getUnitOfMeasurement());
		}
		findOne.setLeaseAndAgreement(assetCategory.isLeaseAndAgreement());
		if(assetCategory.getCustomeFields().size()>0) {
			for(int i=0; i<assetCategory.getCustomeFields().size();i++) {
					for(int j=0; j<findOne.getCustomeFields().size();j++) {
						if(assetCategory.getCustomeFields().get(i).getId()==findOne.getCustomeFields().get(j).getId()) {
							
							if(null!=assetCategory.getCustomeFields().get(i).getColumns()) {
								findOne.getCustomeFields().get(j).setColumns(assetCategory.getCustomeFields().get(i).getColumns());
							}if(null!=assetCategory.getCustomeFields().get(i).getCreatedBy()) {
								findOne.getCustomeFields().get(j).setCreatedBy(assetCategory.getCustomeFields().get(i).getCreatedBy());
							}if(null!=assetCategory.getCustomeFields().get(i).getUpdatedBy()) {
								findOne.getCustomeFields().get(j).setUpdatedBy(assetCategory.getCustomeFields().get(i).getUpdatedBy());
								
							}if(null!=assetCategory.getCustomeFields().get(i).getCreateDate()) {
								findOne.getCustomeFields().get(j).setCreateDate(assetCategory.getCustomeFields().get(i).getCreateDate());
							}if(null!=assetCategory.getCustomeFields().get(i).getCustomFieldDataType().getDataTypes()) {
								findOne.getCustomeFields().get(j).getCustomFieldDataType().setDataTypes(assetCategory.getCustomeFields().get(i).getCustomFieldDataType().getDataTypes());
							}if(null!=assetCategory.getCustomeFields().get(i).getDataType()) {
								findOne.getCustomeFields().get(j).setDataType(assetCategory.getCustomeFields().get(i).getDataType());
							}if(null!=assetCategory.getCustomeFields().get(i).getName()) {
								findOne.getCustomeFields().get(j).setName(assetCategory.getCustomeFields().get(i).getName());
							}if(null!=assetCategory.getCustomeFields().get(i).getOrders()) {
								findOne.getCustomeFields().get(j).setOrders(assetCategory.getCustomeFields().get(i).getOrders());
							}if(null!=assetCategory.getCustomeFields().get(i).getUpdateDate()) {
								findOne.getCustomeFields().get(j).setUpdateDate(assetCategory.getCustomeFields().get(i).getUpdateDate());
							}if(null!=assetCategory.getCustomeFields().get(i).getVlaues()) {
								findOne.getCustomeFields().get(j).setVlaues(assetCategory.getCustomeFields().get(i).getVlaues());
							}
							findOne.getCustomeFields().get(j).setMandatory(assetCategory.getCustomeFields().get(i).isMandatory());
							findOne.getCustomeFields().get(j).setActive(assetCategory.getCustomeFields().get(i).isActive());
						}
					}
			}
				
		}
		AssetCatagory save=assetCatagoryRepository.save(findOne);
//		AssetCatagory save = assetCatagoryRepository.saveAndFlush(assetCategory);
		save.setErrorMessage("Record updated successfully..!!");
		
		return save;
	}
	public AssetCatagory getAssetCategory(Long id) {
		return assetCatagoryRepository.getOne(id);
	}
	public List<AssetCatagory> getAssetcategories(){
		return assetCatagoryRepository.findAll();
	}

	public List<AssetCatagory> findBynameorAssetCataType(String name,Long id){
		return assetCatagoryRepository.findBynameorAssetCataType(name, id);
	}
	public Double getDepreciationRate(final Double depreciationRate) {
		if (depreciationRate != null) {
			final Double deprRate = Math.round(depreciationRate * 100.0) / 100.0;
			LOGGER.debug("Depreciation Rate ::" + deprRate);
			return deprRate;
		} else
			return null;
	}

	public String getAssetCode() {
		Long seq = assetCatagoryRepository.getNextValMySequence();
		String codeFormat = "%03d";
		StringBuilder code = new StringBuilder(String.format(codeFormat, seq));
		String assetCode = code.toString();
		return assetCode;
	}
	public AssetCatagory updateCustomField(Long cusId,String acName) {
		
		
		CustomeFields custoField = customeFieldsRepository.findOne(cusId);
		AssetCatagory assetCategory = assetCatagoryRepository.findByName(acName);
		assetCategory.setCustomeField(custoField);
		System.out.println("updatable customefield "+assetCategory.getCustomeField());
		
		return assetCategory;
	}
public AssetCatagory updateAssetcategory(Long id) {
		AssetCatagory assetCategory = assetCatagoryRepository.findOne(id);
		return assetCategory;
	}

public AssetCatagory deleteCustomField(Long AssetCatId,Long cusId) {
	//customeFieldsRepository.findOne(id).get;
	//customeFieldsRepository.deleteById(id);
	//customeFieldsRepository.delete(cusId);
	CustomeFields customField = customeFieldsRepository.findOne(cusId);
	customField.setMandatory(false);
	customField.setActive(false);
	CustomeFields updatedCustomField = customeFieldsRepository.save(customField);
	AssetCatagory findOne = assetCatagoryRepository.findOne(AssetCatId);
	findOne.setErrorMessage("Custom Field "+updatedCustomField.getName()+" Deactivated sucessfully!!");
	
	return findOne;
}
		
}

