-- create table deduc_voucher_mpng
CREATE TABLE deduc_voucher_mpng (
    id int8 NOT NULL,
    ph_id int8 NULL,
    vh_id int8 NULL,
    CONSTRAINT deduc_voucher_mpng_pkey PRIMARY KEY (id)
);


-- sequence for seq_deduc_voucher_mpng
CREATE SEQUENCE seq_deduc_voucher_mpng MINVALUE 1 MAXVALUE 999999999 INCREMENT BY 1 START WITH 1 ;


-- egcl_remittance definition

-- Drop table

-- DROP TABLE egcl_remittance;

CREATE TABLE egcl_remittance (
	id int8 NOT NULL,
	"version" int8 NOT NULL DEFAULT 1,
	referencenumber varchar(50) NOT NULL,
	referencedate timestamp NOT NULL,
	voucherheader int8 NULL,
	fund int8 NULL,
	"function" int8 NULL,
	remarks varchar(250) NULL,
	reasonfordelay varchar(250) NULL,
	status int8 NOT NULL,
	state int8 NULL,
	createdby int8 NOT NULL,
	createddate timestamp NULL,
	lastmodifiedby int8 NOT NULL,
	lastmodifieddate timestamp NULL,
	bankaccount int8 NULL,
	CONSTRAINT pk_egcl_remittance PRIMARY KEY (id),
	CONSTRAINT unq_remit_referencenumber UNIQUE (referencenumber)
);
CREATE INDEX idx_remit_function ON egcl_remittance USING btree (function);
CREATE INDEX idx_remit_state ON egcl_remittance USING btree (state);
CREATE INDEX idx_remit_status ON egcl_remittance USING btree (status);
CREATE INDEX idx_remit_voucher ON egcl_remittance USING btree (voucherheader);

CREATE TABLE egcl_remittance_instrument (
	id int8 NOT NULL,
	remittance int8 NULL,
	instrumentheader int8 NULL,
	reconciled bool NULL DEFAULT false,
	createdby int8 NOT NULL,
	createddate timestamp NULL,
	lastmodifiedby int8 NOT NULL,
	lastmodifieddate timestamp NULL,
	CONSTRAINT pk_remittance_instrument PRIMARY KEY (id)
);
CREATE INDEX idx_remit_instrument ON egcl_remittance_instrument USING btree (instrumentheader);
CREATE INDEX idx_remit_remittance ON egcl_remittance_instrument USING btree (remittance);

---------------------------------

-- eg_location definition

-- Drop table

-- DROP TABLE eg_location;

CREATE TABLE eg_location (
	id int8 NOT NULL,
	"name" varchar(50) NOT NULL,
	description varchar(100) NULL,
	active bool NULL,
	"version" numeric NULL DEFAULT 0,
	CONSTRAINT eg_location_pkey PRIMARY KEY (id)
);
-----
-- egcl_collectionheader definition

-- Drop table

-- DROP TABLE egcl_collectionheader;

CREATE TABLE egcl_collectionheader (
	id int8 NOT NULL,
	referencenumber varchar(50) NULL,
	referencedate timestamp NULL,
	receipttype bpchar(1) NOT NULL,
	receiptnumber varchar(50) NULL,
	receiptdate timestamp NOT NULL,
	referencedesc varchar(250) NULL,
	manualreceiptnumber varchar(50) NULL,
	manualreceiptdate timestamp NULL,
	ismodifiable bool NULL,
	servicedetails varchar(80) NOT NULL,
	collectiontype bpchar(1) NULL,
	"location" int8 NULL,
	isreconciled bool NULL,
	status int8 NOT NULL,
	reasonforcancellation varchar(250) NULL,
	paidby varchar(1024) NULL,
	reference_ch_id int8 NULL,
	overrideaccountheads bool NULL,
	partpaymentallowed bool NULL,
	displaymsg varchar(256) NULL,
	minimumamount float8 NULL,
	totalamount float8 NULL,
	collmodesnotallwd varchar(256) NULL,
	consumercode varchar(256) NULL,
	callbackforapportioning bool NULL,
	payeename varchar(256) NULL,
	payeeaddress varchar(1024) NULL,
	"version" int8 NOT NULL DEFAULT 1,
	createdby int8 NOT NULL,
	createddate timestamp NULL,
	lastmodifiedby int8 NOT NULL,
	lastmodifieddate timestamp NULL,
	"source" varchar(20) NULL,
	payeeemail varchar(254) NULL,
	consumertype varchar(100) NULL,
	CONSTRAINT pk_egcl_collectionheader PRIMARY KEY (id),
	CONSTRAINT unq_ch_receiptnumber UNIQUE (receiptnumber)
);
CREATE INDEX idx_collhd_consumercode ON egcl_collectionheader USING btree (consumercode);
CREATE INDEX idx_collhd_locid ON egcl_collectionheader USING btree (location);
CREATE INDEX idx_collhd_refchid ON egcl_collectionheader USING btree (reference_ch_id);
CREATE INDEX indx_collhd_createdby ON egcl_collectionheader USING btree (createdby);
CREATE INDEX indx_collhd_createddate ON egcl_collectionheader USING btree (receiptdate);
CREATE INDEX indx_collhd_mreceiptnumber ON egcl_collectionheader USING btree (manualreceiptnumber);
CREATE INDEX indx_collhd_refno ON egcl_collectionheader USING btree (referencenumber);
CREATE INDEX indx_collhd_service ON egcl_collectionheader USING btree (servicedetails);
CREATE INDEX indx_collhd_status ON egcl_collectionheader USING btree (status);


-- egcl_collectionheader foreign keys

ALTER TABLE egcl_collectionheader ADD CONSTRAINT fk_collhead_chid FOREIGN KEY (reference_ch_id) REFERENCES egcl_collectionheader(id);
ALTER TABLE egcl_collectionheader ADD CONSTRAINT fk_collhead_location FOREIGN KEY ("location") REFERENCES eg_location(id);
ALTER TABLE egcl_collectionheader ADD CONSTRAINT fk_collhead_status FOREIGN KEY (status) REFERENCES egw_status(id);
----
-- egcl_servicecategory definition

-- Drop table

-- DROP TABLE egcl_servicecategory;

CREATE TABLE egcl_servicecategory (
	id int8 NOT NULL,
	"name" varchar(256) NOT NULL,
	code varchar(50) NOT NULL,
	isactive bool NULL,
	"version" int8 NOT NULL DEFAULT 1,
	createdby int8 NOT NULL,
	createddate timestamp NULL,
	lastmodifiedby int8 NOT NULL,
	lastmodifieddate timestamp NULL,
	CONSTRAINT pk_egcl_servicecategory PRIMARY KEY (id)
);


-- egcl_servicedetails definition

-- Drop table

-- DROP TABLE egcl_servicedetails;

CREATE TABLE egcl_servicedetails (
	id int8 NOT NULL,
	"name" varchar(100) NOT NULL,
	serviceurl varchar(256) NULL,
	isenabled bool NULL,
	callbackurl varchar(256) NULL,
	servicetype bpchar(1) NULL,
	code varchar(12) NOT NULL,
	fund int8 NULL,
	fundsource int8 NULL,
	functionary int8 NULL,
	vouchercreation bool NULL,
	scheme int8 NULL,
	subscheme int8 NULL,
	servicecategory int8 NULL,
	isvoucherapproved bool NULL,
	vouchercutoffdate timestamp NULL,
	created_by int8 NOT NULL,
	created_date timestamp NOT NULL,
	modified_by int8 NOT NULL,
	modified_date timestamp NOT NULL,
	ordernumber int4 NULL,
	"function" int8 NULL,
	CONSTRAINT pk_egcl_servicedetails PRIMARY KEY (id),
	CONSTRAINT unq_servicedetailname UNIQUE (name)
);
CREATE INDEX idx_servicedetails_code ON egcl_servicedetails USING btree (code);

-- egcl_onlinepayments definition

-- Drop table

-- DROP TABLE egcl_onlinepayments;

CREATE TABLE egcl_onlinepayments (
	id int8 NOT NULL,
	collectionheader int8 NOT NULL,
	servicedetails int8 NOT NULL,
	transactionnumber varchar(50) NULL,
	transactionamount float8 NULL,
	transactiondate timestamp NULL,
	status int8 NULL,
	authorisation_statuscode varchar(50) NULL,
	remarks varchar(256) NULL,
	"version" int8 NOT NULL DEFAULT 1,
	createdby int8 NOT NULL,
	lastmodifiedby int8 NOT NULL,
	createddate timestamp NULL,
	lastmodifieddate timestamp NULL,
	CONSTRAINT pk_egcl_onlinepayments PRIMARY KEY (id)
);
CREATE INDEX idx_online_service ON egcl_onlinepayments USING btree (servicedetails);
CREATE INDEX idx_online_status ON egcl_onlinepayments USING btree (status);
CREATE INDEX idx_op_collheaderid ON egcl_onlinepayments USING btree (collectionheader);

--if required we need sequence for previous year
CREATE SEQUENCE sq_1_brv_202021 MINVALUE 1 MAXVALUE 999999999 INCREMENT BY 1 START WITH 1 ;
CREATE SEQUENCE sq_1_brv_202122 MINVALUE 1 MAXVALUE 999999999 INCREMENT BY 1 START WITH 1 ;
CREATE SEQUENCE sq_1_brv_202223 MINVALUE 1 MAXVALUE 999999999 INCREMENT BY 1 START WITH 1 ;
CREATE SEQUENCE sq_1_brv_202324 MINVALUE 1 MAXVALUE 999999999 INCREMENT BY 1 START WITH 1 ;

-- Drop table

-- DROP TABLE voucherdraftdetails;

CREATE TABLE voucherdraftdetails (
	vouchernumber varchar(30) NULL,
	glcodeiddetail int8 NULL,
	glcodedetail varchar NULL,
	issubledger varchar NULL,
	accounthead varchar NULL,
	debitamountdetail numeric NULL,
	id int8 NOT NULL,
	creditamountdetail numeric NULL,
	functiondetail varchar NULL,
	functioniddetail int8 NULL
);


-- DROP SEQUENCE seq_voucherdraftdetails;

CREATE SEQUENCE seq_voucherdraftdetails
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1;


------------------------

CREATE TABLE egcl_collectionmis (
	id int8 NOT NULL,
	fund int8 NOT NULL,
	fundsource int8 NULL,
	boundary int8 NULL,
	department varchar(80) NOT NULL,
	scheme int8 NULL,
	subscheme int8 NULL,
	collectionheader int8 NOT NULL,
	functionary int8 NULL,
	depositedbranch int8 NULL,
	CONSTRAINT pk_egcl_collectionmis PRIMARY KEY (id)
);
CREATE INDEX idx_collmis_boundaryid ON egcl_collectionmis USING btree (boundary);
CREATE INDEX indx_collmis_collheader ON egcl_collectionmis USING btree (collectionheader);
CREATE INDEX indx_collmis_dept ON egcl_collectionmis USING btree (department);

alter table eg_billdetails drop constraint fk_bd_gl;

ALTER TABLE scheme ALTER COLUMN "name" TYPE varchar(255) USING "name"::varchar;

ALTER TABLE bank ADD onlinelink varchar(100) NULL;

ALTER TABLE egf_contractor ADD adharnumber varchar(50) NULL;

ALTER TABLE egf_contractor ADD vigilance varchar(1024) NULL;

ALTER TABLE egf_contractor ADD blcklistfromdate timestamp NULL;

ALTER TABLE egf_contractor ADD blcklisttodate timestamp NULL;

ALTER TABLE egf_contractor ADD contractortype varchar(50) NULL;

ALTER TABLE egf_supplier ADD suppliertype varchar(50) NULL;

ALTER TABLE voucherheader ADD firstsignatory varchar(100) NULL;

ALTER TABLE voucherheader ADD secondsignatory varchar(100) NULL;

ALTER TABLE voucherheader ADD postauditprocessing varchar(1) NULL;

ALTER TABLE voucherheader ADD backdateentry varchar(10) NULL;

ALTER TABLE vouchermis ADD reciept_number varchar(255) NULL;

ALTER TABLE eg_billregister ADD billenddate timestamp NULL;

ALTER TABLE eg_billregister ADD sanctiondate timestamp NULL;

ALTER TABLE eg_billregister ADD sanctionnumber varchar(50) NULL;

ALTER TABLE eg_wf_states ADD owner_name varchar(200) NULL;

ALTER TABLE egf_documents DROP CONSTRAINT fk_objectid;

ALTER TABLE eg_remittance_detail ADD assign_number varchar(50) NULL;

ALTER TABLE tds ADD isreport varchar(1) NULL;



-- egcl_remittance foreign keys

ALTER TABLE egcl_remittance ADD CONSTRAINT fk_remit_function FOREIGN KEY ("function") REFERENCES "function"(id);
ALTER TABLE egcl_remittance ADD CONSTRAINT fk_remit_fund FOREIGN KEY (fund) REFERENCES fund(id);
ALTER TABLE egcl_remittance ADD CONSTRAINT fk_remit_state FOREIGN KEY (state) REFERENCES eg_wf_states(id);
ALTER TABLE egcl_remittance ADD CONSTRAINT fk_remit_status FOREIGN KEY (status) REFERENCES egw_status(id);
ALTER TABLE egcl_remittance ADD CONSTRAINT fk_rmtnc_bankaccount FOREIGN KEY (bankaccount) REFERENCES bankaccount(id);

-- egcl_remittance_instrument foreign keys

ALTER TABLE egcl_remittance_instrument ADD CONSTRAINT fk_remit_instrument FOREIGN KEY (instrumentheader) REFERENCES egf_instrumentheader(id);
ALTER TABLE egcl_remittance_instrument ADD CONSTRAINT fk_remit_remittance FOREIGN KEY (remittance) REFERENCES egcl_remittance(id);

-- egcl_servicedetails foreign keys

ALTER TABLE egcl_servicedetails ADD CONSTRAINT fk_function_servicedet FOREIGN KEY ("function") REFERENCES "function"(id);
ALTER TABLE egcl_servicedetails ADD CONSTRAINT fk_serdtls_fsource FOREIGN KEY (fundsource) REFERENCES fundsource(id);
ALTER TABLE egcl_servicedetails ADD CONSTRAINT fk_serdtls_functionary FOREIGN KEY (functionary) REFERENCES functionary(id);
ALTER TABLE egcl_servicedetails ADD CONSTRAINT fk_serdtls_fund FOREIGN KEY (fund) REFERENCES fund(id);
ALTER TABLE egcl_servicedetails ADD CONSTRAINT fk_serdtls_scheme FOREIGN KEY (scheme) REFERENCES scheme(id);
ALTER TABLE egcl_servicedetails ADD CONSTRAINT fk_serdtls_servicecat FOREIGN KEY (servicecategory) REFERENCES egcl_servicecategory(id);
ALTER TABLE egcl_servicedetails ADD CONSTRAINT fk_serdtls_subscheme FOREIGN KEY (subscheme) REFERENCES sub_scheme(id);

--------------------

-- egcl_onlinepayments foreign keys

ALTER TABLE egcl_onlinepayments ADD CONSTRAINT fk_onpay_collhead FOREIGN KEY (collectionheader) REFERENCES egcl_collectionheader(id);
ALTER TABLE egcl_onlinepayments ADD CONSTRAINT fk_onpay_service FOREIGN KEY (servicedetails) REFERENCES egcl_servicedetails(id);
ALTER TABLE egcl_onlinepayments ADD CONSTRAINT fk_onpay_status FOREIGN KEY (status) REFERENCES egw_status(id);

-----------------------------------------------
ALTER TABLE voucherheader ALTER COLUMN "name" TYPE varchar(1024) USING "name"::varchar;
ALTER TABLE eg_billdetails ALTER COLUMN billid DROP NOT NULL;
ALTER TABLE eg_billdetails ALTER COLUMN glcodeid DROP NOT NULL;
ALTER TABLE eg_billdetails ALTER COLUMN lastupdatedtime DROP NOT NULL;



ALTER TABLE egcl_collectionmis ADD CONSTRAINT fk_collmis_bankbranch FOREIGN KEY (depositedbranch) REFERENCES bankbranch(id);
ALTER TABLE egcl_collectionmis ADD CONSTRAINT fk_collmis_boundary FOREIGN KEY (boundary) REFERENCES eg_boundary(id);
ALTER TABLE egcl_collectionmis ADD CONSTRAINT fk_collmis_collhead FOREIGN KEY (collectionheader) REFERENCES egcl_collectionheader(id);
ALTER TABLE egcl_collectionmis ADD CONSTRAINT fk_collmis_fund FOREIGN KEY (fund) REFERENCES fund(id);
ALTER TABLE egcl_collectionmis ADD CONSTRAINT fk_collmis_fundsource FOREIGN KEY (fundsource) REFERENCES fundsource(id);
ALTER TABLE egcl_collectionmis ADD CONSTRAINT fk_collmis_funtionary FOREIGN KEY (functionary) REFERENCES functionary(id);
ALTER TABLE egcl_collectionmis ADD CONSTRAINT fk_collmis_scheme FOREIGN KEY (scheme) REFERENCES scheme(id);
ALTER TABLE egcl_collectionmis ADD CONSTRAINT fk_collmis_subscheme FOREIGN KEY (subscheme) REFERENCES sub_scheme(id);

ALTER TABLE voucherheader ADD fileno varchar(50) NULL;
ALTER TABLE paymentheader ADD fileno varchar(50) NULL;
ALTER TABLE paymentheader ADD paymentchequeno varchar(20) NULL;

ALTER TABLE eg_billregister ADD fileno varchar(50) NULL;

ALTER TABLE voucherheader ADD reasoncancel varchar NULL;
ALTER TABLE eg_billregister ADD reasoncancel varchar NULL;
--------------------
ALTER TABLE eg_wf_state_history ADD previousownerposition int8 NULL;

ALTER TABLE eg_billdetails ALTER COLUMN billid DROP NOT NULL;
ALTER TABLE eg_billdetails ALTER COLUMN glcodeid DROP NOT NULL;
ALTER TABLE eg_billdetails ALTER COLUMN lastupdatedtime DROP NOT NULL;

---------------------------
--Truncate default data
truncate table chartofaccounts cascade;
truncate table chartofaccountdetail cascade; 
truncate table egeis_employeetype  cascade;
truncate table bankentries  cascade;
truncate table tds  cascade;
truncate table eg_advancerequisitiondetails  cascade;
truncate table egf_instrumentaccountcodes  cascade;
truncate table egf_budgetgroup  cascade;
truncate table generalledgerdetail  cascade;
truncate table bankentries_mis  cascade;
truncate table eg_billpayeedetails  cascade;
truncate table eg_advancereqpayeedetails  cascade;
truncate table eg_remittance  cascade;
truncate table eg_remittance_gldtl  cascade;
truncate table egf_budgetdetail  cascade;
truncate table eg_remittance_gl  cascade;
truncate table egf_budget_reappropriation  cascade;
truncate table generalledger  cascade;
truncate table eg_billregistermis  cascade;
truncate table sub_scheme  cascade;
truncate table egeis_assignment  cascade;
truncate table eg_advancerequisitionmis  cascade;
truncate table eg_dept_do_mapping  cascade;
truncate table fundsource  cascade;
truncate table vouchermis  cascade;
truncate table egf_purchaseorder  cascade;
truncate table egf_workorder  cascade;
truncate table egcl_servicedetails  cascade;
truncate table egcl_collectionmis  cascade;
truncate table transactionsummary  cascade;
truncate table egcl_onlinepayments  cascade;
truncate table eg_billdetails  cascade;
truncate table egcl_remittance  cascade;
truncate table egcl_remittance_instrument cascade; 
truncate table voucherheader  cascade;
truncate table eg_surrendered_cheques  cascade;
truncate table contrajournalvoucher  cascade;
truncate table paymentheader  cascade;
truncate table miscbilldetail  cascade;
truncate table egf_fixeddeposit  cascade;
truncate table bankbranch  cascade;
truncate table egf_instrumentheader cascade; 
truncate table egf_contractor  cascade;
truncate table egf_supplier  cascade;
truncate table bankaccount  cascade;
truncate table egf_instrumentotherdetails  cascade;
truncate table bankreconciliation  cascade;
truncate table egf_account_cheques  cascade;
truncate table cheque_dept_mapping  cascade;
truncate table fiscalperiod  cascade;
truncate table egf_budget  cascade;
truncate table egf_budget_usage  cascade;
truncate table closedperiods  cascade;
--Truncate for ulb data
truncate table schedulemapping cascade;
truncate table chartofaccounts cascade;
truncate table eg_bill_subtype cascade;
truncate table eg_department cascade;
truncate table eg_designation cascade;
truncate table function cascade;
truncate table fund cascade;
truncate table scheme cascade;
truncate table bank cascade;
truncate table bankbranch cascade;
truncate table bankaccount cascade;
truncate table financialyear cascade;
truncate table fiscalperiod cascade;
truncate table egf_contractor cascade;
truncate table egf_supplier cascade;
truncate table accountdetailkey cascade;
truncate table chartofaccountdetail cascade;
truncate table tds cascade;
truncate table eg_city cascade; 
truncate table eg_citypreferences cascade;
truncate table eg_wf_matrix cascade;


ALTER sequence seq_chartofaccounts START WITH 1;
ALTER sequence seq_scheme START WITH 1;
ALTER sequence seq_fund START WITH 1;
ALTER sequence seq_schedulemapping START WITH 1;
ALTER sequence seq_eg_bill_subtype START WITH 1;
ALTER sequence seq_eg_designation START WITH 1;
ALTER sequence seq_eg_department START WITH 1;
ALTER sequence seq_function START WITH 1;
ALTER sequence seq_bank START WITH 1;
ALTER sequence seq_bankbranch START WITH 1;
ALTER sequence seq_bankaccount START WITH 1;
ALTER sequence seq_financialyear START WITH 1;
ALTER sequence seq_fiscalperiod START WITH 1;
ALTER sequence seq_egf_contractor START WITH 1;
ALTER sequence seq_egf_supplier START WITH 1;
ALTER sequence seq_accountdetailkey START WITH 1;
ALTER sequence seq_chartofaccountdetail START WITH 1;
ALTER sequence seq_tds START WITH 1;
ALTER sequence seq_eg_city START WITH 1;
ALTER sequence seq_eg_citypreferences START WITH 1;
ALTER sequence seq_eg_wf_matrix START WITH 1;


ALTER sequence seq_chartofaccounts RESTART WITH 1;
ALTER sequence seq_scheme RESTART WITH 1;
ALTER sequence seq_fund RESTART WITH 1;
ALTER sequence seq_schedulemapping RESTART WITH 1;
ALTER sequence seq_eg_bill_subtype RESTART WITH 1;
ALTER sequence seq_eg_designation RESTART WITH 1;
ALTER sequence seq_eg_department RESTART WITH 1;
ALTER sequence seq_function RESTART WITH 1;
ALTER sequence seq_bank RESTART WITH 1;
ALTER sequence seq_bankbranch RESTART WITH 1;
ALTER sequence seq_bankaccount RESTART WITH 1;
ALTER sequence seq_financialyear RESTART WITH 1;
ALTER sequence seq_fiscalperiod RESTART WITH 1;
ALTER sequence seq_egf_contractor RESTART WITH 1;
ALTER sequence seq_egf_supplier RESTART WITH 1;
ALTER sequence seq_accountdetailkey RESTART WITH 1;
ALTER sequence seq_chartofaccountdetail RESTART WITH 1;
ALTER sequence seq_tds RESTART WITH 1;
ALTER sequence seq_eg_city RESTART WITH 1;
ALTER sequence seq_eg_citypreferences RESTART WITH 1;
ALTER sequence seq_eg_wf_matrix RESTART WITH 1;

--Insert begin in Scedulemapping

INSERT INTO schedulemapping (id,reporttype,schedule,schedulename,repsubtype,createdby,createddate,lastmodifiedby,lastmodifieddate,isremission) VALUES
	 (nextval('seq_schedulemapping'),'BS','B-01','Municipal (General) Fund','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'BS','B-02','Earmarked Funds','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'BS','B-03','Reserves','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'BS','B-04','Grants , Contribution for specific Purposes','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'BS','B-05','Secured Loans','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'BS','B-06','Unsecured Loans','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'BS','B-07','Deposits Received','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'BS','B-08','Deposit works','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'BS','B-09','Other Liabilities','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'BS','B-10','Provisions','',1,'2021-04-01 00:00:00',NULL,NULL,NULL);
INSERT INTO schedulemapping (id,reporttype,schedule,schedulename,repsubtype,createdby,createddate,lastmodifiedby,lastmodifieddate,isremission) VALUES
	 (nextval('seq_schedulemapping'),'BS','B-11','Fixed Assets','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'BS','B-12','Accumulated Depreciation','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'BS','B-13','Capital Work in Progress','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'BS','B-14','Investments General Fund','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'BS','B-15','Investments Other Funds','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'BS','B-16','Stock in hand','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'BS','B-17','Sundry Debtors (Receivables)','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'BS','B-18','Accumulated Provisions against Debtors (Receivables)','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'BS','B-19','Prepaid Expenses','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'BS','B-20','Cash and Bank balance','',1,'2021-04-01 00:00:00',NULL,NULL,NULL);
INSERT INTO schedulemapping (id,reporttype,schedule,schedulename,repsubtype,createdby,createddate,lastmodifiedby,lastmodifieddate,isremission) VALUES
	 (nextval('seq_schedulemapping'),'BS','B-21','Loans, Advances and Deposits','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'BS','B-22','Accumulated Provisions against Loans, Advances and Deposits','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'BS','B-23','Other Assets','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'BS','B-24','Intangible Assets','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'BS','B-25','Miscellaneous Expenditure to be written off','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'IE','I-01','Tax Revenue','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'IE','I-02','Assigned Revenues and Compensations','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'IE','I-03','Rental Income from Municipal Properties','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'IE','I-04','Fees and User Charges','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'IE','I-05','Sale and Hire Charges','',1,'2021-04-01 00:00:00',NULL,NULL,NULL);
INSERT INTO schedulemapping (id,reporttype,schedule,schedulename,repsubtype,createdby,createddate,lastmodifiedby,lastmodifieddate,isremission) VALUES
	 (nextval('seq_schedulemapping'),'IE','I-06','Revenue Grants, Contribution and Subsidies','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'IE','I-07','Income from Investments','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'IE','I-08','Interest Earned','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'IE','I-09','Other Income','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'IE','I-10','Consolidated prior period income','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'IE','I-11','Transfer from Funds','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'IE','I-12','Transfer from Corporator Fund','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'IE','I-13','Transfer from Employee Fund','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'IE','I-14','Establishment Expenses','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'IE','I-15','Administrative Expenses','',1,'2021-04-01 00:00:00',NULL,NULL,NULL);
INSERT INTO schedulemapping (id,reporttype,schedule,schedulename,repsubtype,createdby,createddate,lastmodifiedby,lastmodifieddate,isremission) VALUES
	 (nextval('seq_schedulemapping'),'IE','I-16','Operations and Maintenance Expenses','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'IE','I-17','Interest and Finance Charges','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'IE','I-18','Programme Expenses','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'IE','I-19','Revenue Grants, Contribution and Subsidies','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'IE','I-20','Provisions and Write off','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'IE','I-21','Miscellaneous Expenses','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'IE','I-22','Depreciation','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'IE','I-23','Prior Period Expenses','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'IE','I-24','Transfer to Activity Funds','',1,'2021-04-01 00:00:00',NULL,NULL,NULL),
	 (nextval('seq_schedulemapping'),'IE','I-25','Transfer to Corporator Fund','',1,'2021-04-01 00:00:00',NULL,NULL,NULL);
INSERT INTO schedulemapping (id,reporttype,schedule,schedulename,repsubtype,createdby,createddate,lastmodifiedby,lastmodifieddate,isremission) VALUES
	 (nextval('seq_schedulemapping'),'IE','I-26','Transfer to Employee Fund','',1,'2021-04-01 00:00:00',NULL,NULL,NULL);
	 
--Insert begin in ChartOfAccounts

INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'1','INCOME','',false,NULL,NULL,' ','I',NULL,0,false,false,NULL,NULL,' ',NULL,' ','1',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2','EXPENSE','',false,NULL,NULL,' ','E',NULL,0,false,false,NULL,NULL,' ',NULL,' ','2',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3','LIABILITIES','',false,NULL,NULL,' ','L',NULL,0,false,false,NULL,NULL,' ',NULL,' ','3',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4','ASSETS','',false,NULL,NULL,' ','A',NULL,0,false,false,NULL,NULL,' ',NULL,' ','4',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'110','Tax Revenue','',false,1,NULL,'A','I',NULL,1,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'120','Assigned Revenues and Compensations','',false,1,NULL,'A','I',NULL,1,false,false,27,NULL,' ',NULL,' ','120',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'130','Rental Income from Municipal Properties','',false,1,NULL,'A','I',NULL,1,false,false,28,NULL,' ',NULL,' ','130',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'140','Fees and User Charges','',false,1,NULL,'A','I',NULL,1,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'150','Sale and Hire Charges','',false,1,NULL,'A','I',NULL,1,false,false,30,NULL,' ',NULL,' ','150',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'160','Revenue Grants, Contribution and Subsidies','',false,1,NULL,'A','I',NULL,1,false,false,31,NULL,' ',NULL,' ','160',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'170','Income from Investments','',false,1,NULL,'A','I',NULL,1,false,false,32,NULL,' ',NULL,' ','170',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'171','Interest Earned','',false,1,NULL,'A','I',NULL,1,false,false,33,NULL,' ',NULL,' ','171',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'180','Other Income','',false,1,NULL,'A','I',NULL,1,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'185','Consolidated prior period income','',false,1,NULL,'A','I',NULL,1,false,false,35,NULL,' ',NULL,' ','185',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'190','Transfer from Funds','',false,1,NULL,'A','I',NULL,1,false,false,36,NULL,' ',NULL,' ','190',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'191','Transfer from Corporator Fund','',false,1,NULL,'A','I',NULL,1,false,false,37,NULL,' ',NULL,' ','191',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'192','Transfer from Employee Fund','',false,1,NULL,'A','I',NULL,1,false,false,38,NULL,' ',NULL,' ','192',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'210','Establishment Expenses','',false,2,NULL,'A','E',NULL,1,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'220','Administrative Expenses','',false,2,NULL,'A','E',NULL,1,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'230','Operations and Maintenance','',false,2,NULL,'A','E',NULL,1,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'240','Interest and Finance Charges','',false,2,NULL,'A','E',NULL,1,false,false,42,NULL,' ',NULL,' ','240',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'250','Programme Expenses','',false,2,NULL,'A','E',NULL,1,false,false,43,NULL,' ',NULL,' ','250',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'260','Revenue Grants, Contribution and Subsidies','',false,2,NULL,'A','E',NULL,1,false,false,44,NULL,' ',NULL,' ','260',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'270','Provisions and Write off','',false,2,NULL,'A','E',NULL,1,false,false,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'271','Miscellaneous Expenses','',false,2,NULL,'A','E',NULL,1,false,false,46,NULL,' ',NULL,' ','271',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'272','Depreciation','',false,2,NULL,'A','E',NULL,1,false,false,47,NULL,' ',NULL,' ','272',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'285','Prior Period Expenses','',false,2,NULL,'A','E',NULL,1,false,false,48,NULL,' ',NULL,' ','285',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'290','Transfer to Activity Funds','',false,2,NULL,'A','E',NULL,1,false,false,49,NULL,' ',NULL,' ','290',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'291','Transfer to Corporator Fund','',false,2,NULL,'A','E',NULL,1,false,false,50,NULL,' ',NULL,' ','291',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'292','Transfer to Employee Fund','',false,2,NULL,'A','E',NULL,1,false,false,51,NULL,' ',NULL,' ','292',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'310','Municipal (General) Fund','',false,3,NULL,'A','L',NULL,1,false,false,1,NULL,' ',NULL,' ','310',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'311','Earmarked Funds','',false,3,NULL,'A','L',NULL,1,false,false,2,NULL,' ',NULL,' ','311',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'312','Reserves','',false,3,NULL,'A','L',NULL,1,false,false,3,NULL,' ',NULL,' ','312',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'320','Grants , Contribution for specific Purposes','',false,3,NULL,'A','L',NULL,1,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'330','Secured Loans','',false,3,NULL,'A','L',NULL,1,false,false,5,NULL,' ',NULL,' ','330',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'331','Unsecured Loans','',false,3,NULL,'A','L',NULL,1,false,false,6,NULL,' ',NULL,' ','331',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'340','Deposits Received','',false,3,NULL,'A','L',NULL,1,false,false,7,NULL,' ',NULL,' ','340',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'341','Deposit works','',false,3,NULL,'A','L',NULL,1,false,false,8,NULL,' ',NULL,' ','341',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'350','Other Liabilities','',false,3,NULL,'A','L',NULL,1,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'360','Provisions','',false,3,NULL,'A','L',NULL,1,false,false,10,NULL,' ',NULL,' ','360',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'410','Fixed Assets','',false,4,NULL,'A','A',NULL,1,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'411','Accumulated Depreciation','',false,4,NULL,'A','A',NULL,1,false,false,12,NULL,' ',NULL,' ','411',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'412','Capital Work in - progress','',false,4,NULL,'A','A',NULL,1,false,false,13,NULL,' ',NULL,' ','412',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'420','Investments General Fund','',false,4,NULL,'A','A',NULL,1,false,false,14,NULL,' ',NULL,' ','420',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'421','Investments Other Funds','',false,4,NULL,'A','A',NULL,1,false,false,15,NULL,' ',NULL,' ','421',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'430','Stock in hand','',false,4,NULL,'A','A',NULL,1,false,false,16,NULL,' ',NULL,' ','430',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'431','Sundry Debtors (Receivables)','',false,4,NULL,'A','A',NULL,1,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'432','Accumulated Provisions against Debtors (Receivables)','',false,4,NULL,'A','A',NULL,1,false,false,18,NULL,' ',NULL,' ','432',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'440','Pre-paid Expenses','',false,4,NULL,'A','A',NULL,1,false,false,19,NULL,' ',NULL,' ','440',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'450','Cash and Bank balance','',false,4,NULL,'A','A',NULL,1,false,false,20,NULL,' ',NULL,' ','450',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'460','Loans, Advances and Deposits','',false,4,NULL,'A','A',NULL,1,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'461','Accumulated Provisions against Loans, Advances and Deposits','',false,4,NULL,'A','A',NULL,1,false,false,22,NULL,' ',NULL,' ','461',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'470','Other Assets','',false,4,NULL,'A','A',NULL,1,false,false,23,NULL,' ',NULL,' ','470',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'471','Intangible Assets','',false,4,NULL,'A','A',NULL,1,false,false,24,NULL,' ',NULL,' ','471',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'480','Miscellaneous Expenditure to be written off','',false,4,NULL,'A','A',NULL,1,false,false,25,NULL,' ',NULL,' ','480',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'11001','Consolidated Property Tax','',false,5,NULL,'A','I',NULL,2,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'11002','Consolidated Water Tax','',false,5,NULL,'A','I',NULL,2,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'11003','Consolidated Sewerage/Drainage Tax','',false,5,NULL,'A','I',NULL,2,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'11004','Consolidated Conservancy/Latrine Tax','',false,5,NULL,'A','I',NULL,2,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'11005','Consolidated Lighting Tax','',false,5,NULL,'A','I',NULL,2,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'11006','Consolidated Education Tax','',false,5,NULL,'A','I',NULL,2,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'11007','Consolidated Vehicle Tax','',false,5,NULL,'A','I',NULL,2,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'11008','Consolidated Tax on Animals','',false,5,NULL,'A','I',NULL,2,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'11009','Consolidated Electricity Tax','',false,5,NULL,'A','I',NULL,2,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'11010','Consolidated Professional Tax','',false,5,NULL,'A','I',NULL,2,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'11011','Consolidated Advertisement Tax','',false,5,NULL,'A','I',NULL,2,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'11012','Consolidated Pilgrimage Tax','',false,5,NULL,'A','I',NULL,2,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'11013','Consolidated Export Tax','',false,5,NULL,'A','I',NULL,2,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'11051','Consolidated Octroi and Toll','',false,5,NULL,'A','I',NULL,2,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'11080','Consolidated Others Taxes','',false,5,NULL,'A','I',NULL,2,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'12010','Consolidated Taxes and Duties collected by other Governments.','',false,6,NULL,'A','I',NULL,2,false,false,27,NULL,' ',NULL,' ','120',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'12020','Consolidated Compensation in lieu of Taxes and Duties','',false,6,NULL,'A','I',NULL,2,false,false,27,NULL,' ',NULL,' ','120',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'13010','Consolidated Rent from Civic Amenities','',false,7,NULL,'A','I',NULL,2,false,false,28,NULL,' ',NULL,' ','130',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'13020','Consolidated Rent from Office Buildings','',false,7,NULL,'A','I',NULL,2,false,false,28,NULL,' ',NULL,' ','130',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'13040','Consolidated Rent from lease of lands','',false,7,NULL,'A','I',NULL,2,false,false,28,NULL,' ',NULL,' ','130',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'14010','Consolidated Empanelment and Registration Charges','',false,8,NULL,'A','I',NULL,2,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'14011','Consolidated Licensing Fees','',false,8,NULL,'A','I',NULL,2,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'14012','Consolidated Fees for Grant of Permit','',false,8,NULL,'A','I',NULL,2,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'14013','Consolidated Fees for Certificate or Extract','',false,8,NULL,'A','I',NULL,2,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'14014','Consolidated Development Charges','',false,8,NULL,'A','I',NULL,2,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'14015','Consolidated Regularization Fees','',false,8,NULL,'A','I',NULL,2,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'14020','Consolidated Penalties and Fines','',false,8,NULL,'A','I',NULL,2,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'14040','Consolidated Other Fees','',false,8,NULL,'A','I',NULL,2,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'14050','Consolidated User Charges','',false,8,NULL,'A','I',NULL,2,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'14060','Consolidated Entry Fees','',false,8,NULL,'A','I',NULL,2,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'14070','Consolidated Service / Administrative Charges','',false,8,NULL,'A','I',NULL,2,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'14080','Consolidated Other Charges','',false,8,NULL,'A','I',NULL,2,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'15010','Consolidated Sale of Products','',false,9,NULL,'A','I',NULL,2,false,false,30,NULL,' ',NULL,' ','150',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'15011','Consolidated Sale of Forms and Publications','',false,9,NULL,'A','I',NULL,2,false,false,30,NULL,' ',NULL,' ','150',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'15012','Consolidated Sale of stores and scrap','',false,9,NULL,'A','I',NULL,2,false,false,30,NULL,' ',NULL,' ','150',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'15030','Consolidated Sale of Others','',false,9,NULL,'A','I',NULL,2,false,false,30,NULL,' ',NULL,' ','150',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'15040','Consolidated Hire Charges for Vehicles','',false,9,NULL,'A','I',NULL,2,false,false,30,NULL,' ',NULL,' ','150',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'15041','Consolidated Hire Charges on Equipments','',false,9,NULL,'A','I',NULL,2,false,false,30,NULL,' ',NULL,' ','150',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'16010','Consolidated Revenue Grant','',false,10,NULL,'A','I',NULL,2,false,false,31,NULL,' ',NULL,' ','160',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'16020','Consolidated Re-imbursement of expenses','',false,10,NULL,'A','I',NULL,2,false,false,31,NULL,' ',NULL,' ','160',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'16030','Consolidated Contribution towards schemes','',false,10,NULL,'A','I',NULL,2,false,false,31,NULL,' ',NULL,' ','160',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'17010','Consolidated Interest','',false,11,NULL,'A','I',NULL,2,false,false,32,NULL,' ',NULL,' ','170',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'17020','Consolidated Dividend','',false,11,NULL,'A','I',NULL,2,false,false,32,NULL,' ',NULL,' ','170',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'17030','Consolidated Income from projects taken up on commercial basis','',false,11,NULL,'A','I',NULL,2,false,false,32,NULL,' ',NULL,' ','170',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'17040','Consolidated Profit in Sale of Investments','',false,11,NULL,'A','I',NULL,2,false,false,32,NULL,' ',NULL,' ','170',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'17080','Consolidated Others','',false,11,NULL,'A','I',NULL,2,false,false,32,NULL,' ',NULL,' ','170',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'17110','Consolidated Interest from Bank Accounts','',false,12,NULL,'A','I',NULL,2,false,false,33,NULL,' ',NULL,' ','171',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'17120','Consolidated Interest on Loans and advances to Employees','',false,12,NULL,'A','I',NULL,2,false,false,33,NULL,' ',NULL,' ','171',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'17130','Consolidated Interest on loans to others','',false,12,NULL,'A','I',NULL,2,false,false,33,NULL,' ',NULL,' ','171',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'17180','Consolidated Other Interest','',false,12,NULL,'A','I',NULL,2,false,false,33,NULL,' ',NULL,' ','171',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'18010','Consolidated Deposits Forfeited','',false,13,NULL,'A','I',NULL,2,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'18011','Consolidated Lapsed Deposits','',false,13,NULL,'A','I',NULL,2,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'18020','Consolidated Insurance Claim Recovery','',false,13,NULL,'A','I',NULL,2,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'18030','Consolidated Profit on Disposal of Fixed asses','',false,13,NULL,'A','I',NULL,2,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'18040','Consolidated Recovery from Employees','',false,13,NULL,'A','I',NULL,2,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'18050','Consolidated Unclaimed Refund Payable/ Liabilities Written Back','',false,13,NULL,'A','I',NULL,2,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'18060','Consolidated Excess Provisions written back','',false,13,NULL,'A','I',NULL,2,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'18080','Consolidated Miscellaneous Income','',false,13,NULL,'A','I',NULL,2,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'18510','Consolidated prior period income - Property and Other Taxes','',false,14,NULL,'A','I',NULL,2,false,false,35,NULL,' ',NULL,' ','185',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'18520','Consolidated prior period income - Other Revenues','',false,14,NULL,'A','I',NULL,2,false,false,35,NULL,' ',NULL,' ','185',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'18530','Consolidated prior period income - Others','',false,14,NULL,'A','I',NULL,2,false,false,35,NULL,' ',NULL,' ','185',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'19010','Consolidated Transfer from general account','',false,15,NULL,'A','I',NULL,2,false,false,36,NULL,' ',NULL,' ','190',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'19020','Consolidated Transfer from water supply, sewerage and draina','',false,15,NULL,'A','I',NULL,2,false,false,36,NULL,' ',NULL,' ','190',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'19030','Consolidated Transfer from road development and maintenance','',false,15,NULL,'A','I',NULL,2,false,false,36,NULL,' ',NULL,' ','190',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'19040','Consolidated Transfer from bustee services account','',false,15,NULL,'A','I',NULL,2,false,false,36,NULL,' ',NULL,' ','190',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'19050','Consolidated Transfer from commercial projects account','',false,15,NULL,'A','I',NULL,2,false,false,36,NULL,' ',NULL,' ','190',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'19060','Consolidated Transfer from solid waste management account','',false,15,NULL,'A','I',NULL,2,false,false,36,NULL,' ',NULL,' ','190',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'19070','Consolidated Transfer from environment management account','',false,15,NULL,'A','I',NULL,2,false,false,36,NULL,' ',NULL,' ','190',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'19110','Consolidated transfer from Corporator Fund - Ward/Zone Devel','',false,16,NULL,'A','I',NULL,2,false,false,37,NULL,' ',NULL,' ','191',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'19210','Consolidated Transfer from Pension Fund','',false,17,NULL,'A','I',NULL,2,false,false,38,NULL,' ',NULL,' ','192',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'19220','Consolidated Transfer from Gratuity and Leave Salary Fund','',false,17,NULL,'A','I',NULL,2,false,false,38,NULL,' ',NULL,' ','192',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'19230','Consolidated Transfer from Provident Fund','',false,17,NULL,'A','I',NULL,2,false,false,38,NULL,' ',NULL,' ','192',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'21010','Consolidated Salaries, Wages and Bonus','',false,18,NULL,'A','E',NULL,2,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'21020','Consolidated Benefits and Allowances','',false,18,NULL,'A','E',NULL,2,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'21030','Consolidated Pension','',false,18,NULL,'A','E',NULL,2,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'21040','Consolidated Other Terminal and Retirement Benefits','',false,18,NULL,'A','E',NULL,2,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'22010','Consolidated Rent, Rates and Taxes','',false,19,NULL,'A','E',NULL,2,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'22011','Consolidated Office maintenance','',false,19,NULL,'A','E',NULL,2,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'22012','Consolidated Communication Expenses','',false,19,NULL,'A','E',NULL,2,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'22020','Consolidated Books and Periodicals','',false,19,NULL,'A','E',NULL,2,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'22021','Consolidated Printing and Stationery','',false,19,NULL,'A','E',NULL,2,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'22030','Consolidated Travelling and Conveyance','',false,19,NULL,'A','E',NULL,2,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'22040','Consolidated Insurance','',false,19,NULL,'A','E',NULL,2,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'22050','Consolidated Audit Fees','',false,19,NULL,'A','E',NULL,2,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'22051','Consolidated Legal Expenses','',false,19,NULL,'A','E',NULL,2,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'22052','Consolidated Professional and other Fees','',false,19,NULL,'A','E',NULL,2,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'22060','Consolidated Advertisement and Publicity','',false,19,NULL,'A','E',NULL,2,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'22061','Consolidated Membership and subscriptions','',false,19,NULL,'A','E',NULL,2,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'22080','Consolidated Other Administrative Expenses','',false,19,NULL,'A','E',NULL,2,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'23010','Consolidated Power and Fuel','',false,20,NULL,'A','E',NULL,2,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'23020','Consolidated Bulk Purchases','',false,20,NULL,'A','E',NULL,2,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'23030','Consolidated Consumption of Stores','',false,20,NULL,'A','E',NULL,2,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'23040','Consolidated Hire Charges','',false,20,NULL,'A','E',NULL,2,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'23050','Consolidated Repairs and maintenance Infrastructure Assets','',false,20,NULL,'A','E',NULL,2,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'23051','Consolidated Repairs and maintenance Civic Amenities','',false,20,NULL,'A','E',NULL,2,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'23052','Consolidated Repairs and maintenance Buildings','',false,20,NULL,'A','E',NULL,2,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'23053','Consolidated Repairs and maintenance Vehicles','',false,20,NULL,'A','E',NULL,2,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'23059','Consolidated Repairs and Maintenance - Others','',false,20,NULL,'A','E',NULL,2,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'23080','Consolidated Other operating and maintenance expenses','',false,20,NULL,'A','E',NULL,2,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'24010','Consolidated Interest on Loans from Central Government','',false,21,NULL,'A','E',NULL,2,false,false,42,NULL,' ',NULL,' ','240',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'24020','Consolidated Interest on Loans from State Government','',false,21,NULL,'A','E',NULL,2,false,false,42,NULL,' ',NULL,' ','240',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'24030','Consolidated Interest on Loans from Government Bodies and Associations','',false,21,NULL,'A','E',NULL,2,false,false,42,NULL,' ',NULL,' ','240',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'24040','Consolidated Interest on Loans from International Agencies','',false,21,NULL,'A','E',NULL,2,false,false,42,NULL,' ',NULL,' ','240',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'24050','Consolidated Interest on Loans from Banks and Other Financial Institutions','',false,21,NULL,'A','E',NULL,2,false,false,42,NULL,' ',NULL,' ','240',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'24060','Consolidated Other Interest','',false,21,NULL,'A','E',NULL,2,false,false,42,NULL,' ',NULL,' ','240',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'24070','Consolidated Bank Charges','',false,21,NULL,'A','E',NULL,2,false,false,42,NULL,' ',NULL,' ','240',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'24080','Consolidated Other Finance Expenses','',false,21,NULL,'A','E',NULL,2,false,false,42,NULL,' ',NULL,' ','240',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'25010','Consolidated Election Expenses','',false,22,NULL,'A','E',NULL,2,false,false,43,NULL,' ',NULL,' ','250',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'25020','Consolidated Own Programme','',false,22,NULL,'A','E',NULL,2,false,false,43,NULL,' ',NULL,' ','250',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'25030','Consolidated Share in programme of others','',false,22,NULL,'A','E',NULL,2,false,false,43,NULL,' ',NULL,' ','250',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'26010','Consolidated Grants','',false,23,NULL,'A','E',NULL,2,false,false,44,NULL,' ',NULL,' ','260',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'26020','Consolidated Contributions','',false,23,NULL,'A','E',NULL,2,false,false,44,NULL,' ',NULL,' ','260',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'26030','Consolidated Subsidies','',false,23,NULL,'A','E',NULL,2,false,false,44,NULL,' ',NULL,' ','260',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'27010','Consolidated Provisions for Doubtful Receivables','',false,24,NULL,'A','E',NULL,2,false,false,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'27020','Consolidated Provision for other Assets','',false,24,NULL,'A','E',NULL,2,false,false,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'27030','Consolidated Revenues written off','',false,24,NULL,'A','E',NULL,2,false,false,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'27040','Consolidated Assets written Off','',false,24,NULL,'A','E',NULL,2,false,false,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'27050','Consolidated Miscellaneous Expense written Off','',false,24,NULL,'A','E',NULL,2,false,false,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'27090','Consolidated Tax Remission and Refunds','',false,24,NULL,'A','E',NULL,2,false,false,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'27091','Consolidated Fees Remission and Refund','',false,24,NULL,'A','E',NULL,2,false,false,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'27110','Consolidated Loss on disposal of Assets','',false,25,NULL,'A','E',NULL,2,false,false,46,NULL,' ',NULL,' ','271',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'27120','Consolidated Loss on disposal of Investments','',false,25,NULL,'A','E',NULL,2,false,false,46,NULL,' ',NULL,' ','271',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'27180','Consolidated Other Miscellaneous Expenses','',false,25,NULL,'A','E',NULL,2,false,false,46,NULL,' ',NULL,' ','271',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'27220','Consolidated Buildings','',false,26,NULL,'A','E',NULL,2,false,false,47,NULL,' ',NULL,' ','272',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'27230','Consolidated Roads and Bridges','',false,26,NULL,'A','E',NULL,2,false,false,47,NULL,' ',NULL,' ','272',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'27231','Consolidated Sewerage and Drainage','',false,26,NULL,'A','E',NULL,2,false,false,47,NULL,' ',NULL,' ','272',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'27232','Consolidated Waterways','',false,26,NULL,'A','E',NULL,2,false,false,47,NULL,' ',NULL,' ','272',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'27233','Consolidated Public Lighting','',false,26,NULL,'A','E',NULL,2,false,false,47,NULL,' ',NULL,' ','272',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'27240','Consolidated Plant and machinery','',false,26,NULL,'A','E',NULL,2,false,false,47,NULL,' ',NULL,' ','272',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'27250','Consolidated Vehicles','',false,26,NULL,'A','E',NULL,2,false,false,47,NULL,' ',NULL,' ','272',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'27260','Consolidated Office and Other Equipments','',false,26,NULL,'A','E',NULL,2,false,false,47,NULL,' ',NULL,' ','272',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'27270','Consolidated Furniture, Fixtures, Fittings and Electrical Appliances','',false,26,NULL,'A','E',NULL,2,false,false,47,NULL,' ',NULL,' ','272',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'27280','Consolidated Other Fixed Assets','',false,26,NULL,'A','E',NULL,2,false,false,47,NULL,' ',NULL,' ','272',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'28550','Refund of Taxes','',false,27,NULL,'A','E',NULL,2,false,false,48,NULL,' ',NULL,' ','285',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'28560','Refund of Other Revenues','',false,27,NULL,'A','E',NULL,2,false,false,48,NULL,' ',NULL,' ','285',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'28580','Other Expenses','',false,27,NULL,'A','E',NULL,2,false,false,48,NULL,' ',NULL,' ','285',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'29010','Transfer to general account','',false,28,NULL,'A','E',NULL,2,false,false,49,NULL,' ',NULL,' ','290',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'42040','Consolidated Preference Shares','',false,44,NULL,'A','A',NULL,2,false,false,14,NULL,' ',NULL,' ','420',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'29020','Transfer to water supply, sewerage and drainage account','',false,28,NULL,'A','E',NULL,2,false,false,49,NULL,' ',NULL,' ','290',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'29030','Transfer to road development and maintenance account','',false,28,NULL,'A','E',NULL,2,false,false,49,NULL,' ',NULL,' ','290',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'29040','Transfer to bustee services account','',false,28,NULL,'A','E',NULL,2,false,false,49,NULL,' ',NULL,' ','290',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'29050','Transfer to commercial projects account','',false,28,NULL,'A','E',NULL,2,false,false,49,NULL,' ',NULL,' ','290',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'29060','Transfer to solid waste management account','',false,28,NULL,'A','E',NULL,2,false,false,49,NULL,' ',NULL,' ','290',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'29070','Transfer to environment management account','',false,28,NULL,'A','E',NULL,2,false,false,49,NULL,' ',NULL,' ','290',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'29110','Consolidated Transfer to corporator fund','',false,29,NULL,'A','E',NULL,2,false,false,50,NULL,' ',NULL,' ','291',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'29210','Consolidated Transfer to pension fund','',false,30,NULL,'A','E',NULL,2,false,false,51,NULL,' ',NULL,' ','292',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'29220','Consolidated Transfer to gratuity and leave salary fund','',false,30,NULL,'A','E',NULL,2,false,false,51,NULL,' ',NULL,' ','292',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'29230','Consolidated Transfer to Provident fund','',false,30,NULL,'A','E',NULL,2,false,false,51,NULL,' ',NULL,' ','292',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'31010','Consolidated Municipal Fund','',false,31,NULL,'A','L',NULL,2,false,false,1,NULL,' ',NULL,' ','310',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'31090','Consolidated Excess of Income and Expenditure','',false,31,NULL,'A','L',NULL,2,false,false,1,NULL,' ',NULL,' ','310',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'31110','Consolidated Special Funds (Specify each Fund name)','',false,32,NULL,'A','L',NULL,2,false,false,2,NULL,' ',NULL,' ','311',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'31150','Consolidated Sinking Funds (Specify each Fund Name)','',false,32,NULL,'A','L',NULL,2,false,false,2,NULL,' ',NULL,' ','311',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'31170','Consolidated Trust or Agency Funds (Specify each Fund Name)','',false,32,NULL,'A','L',NULL,2,false,false,2,NULL,' ',NULL,' ','311',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'31210','Consolidated Capital Contribution','',false,33,NULL,'A','L',NULL,2,false,false,3,NULL,' ',NULL,' ','312',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'31211','Consolidated Capital Reserve','',false,33,NULL,'A','L',NULL,2,false,false,3,NULL,' ',NULL,' ','312',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'31220','Consolidated Borrowing Redemption reserve (if no sinking fund is Created)','',false,33,NULL,'A','L',NULL,2,false,false,3,NULL,' ',NULL,' ','312',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'31230','Consolidated Special Funds (Utilised)','',false,33,NULL,'A','L',NULL,2,false,false,3,NULL,' ',NULL,' ','312',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'31240','Consolidated Statutory Reserve','',false,33,NULL,'A','L',NULL,2,false,false,3,NULL,' ',NULL,' ','312',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'31250','Consolidated General Reserve','',false,33,NULL,'A','L',NULL,2,false,false,3,NULL,' ',NULL,' ','312',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'31260','Consolidated Revaluation Reserve','',false,33,NULL,'A','L',NULL,2,false,false,3,NULL,' ',NULL,' ','312',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'32010','Consolidated Grants from Central Government','',false,34,NULL,'A','L',NULL,2,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'32020','Consolidated Grants from State Government','',false,34,NULL,'A','L',NULL,2,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'32030','Consolidated Grants from Other Government Agencies','',false,34,NULL,'A','L',NULL,2,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'32040','Consolidated Grants from Financial Institutions','',false,34,NULL,'A','L',NULL,2,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'32050','Consolidated Grants from Welfare Bodies','',false,34,NULL,'A','L',NULL,2,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'32060','Consolidated Grants from International Organizations','',false,34,NULL,'A','L',NULL,2,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'32080','Consolidated Grants from Others','',false,34,NULL,'A','L',NULL,2,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'33010','Consolidated Loans from Central Government','',false,35,NULL,'A','L',NULL,2,false,false,5,NULL,' ',NULL,' ','330',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'33020','Consolidated Loans from State Government','',false,35,NULL,'A','L',NULL,2,false,false,5,NULL,' ',NULL,' ','330',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'33030','Consolidated Loans from Government Bodies and Association','',false,35,NULL,'A','L',NULL,2,false,false,5,NULL,' ',NULL,' ','330',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'33040','Consolidated Loans from International Agencies','',false,35,NULL,'A','L',NULL,2,false,false,5,NULL,' ',NULL,' ','330',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'33050','Consolidated Loans from Banks and Other Financial Institutions','',false,35,NULL,'A','L',NULL,2,false,false,5,NULL,' ',NULL,' ','330',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'33060','Consolidated Other Term Loans','',false,35,NULL,'A','L',NULL,2,false,false,5,NULL,' ',NULL,' ','330',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'33070','Consolidated Bonds and Debentures','',false,35,NULL,'A','L',NULL,2,false,false,5,NULL,' ',NULL,' ','330',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'33080','Consolidated Other Loans','',false,35,NULL,'A','L',NULL,2,false,false,5,NULL,' ',NULL,' ','330',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'33110','Consolidated Loans from Central Government','',false,36,NULL,'A','L',NULL,2,false,false,6,NULL,' ',NULL,' ','331',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'33120','Consolidated Loans from State Government','',false,36,NULL,'A','L',NULL,2,false,false,6,NULL,' ',NULL,' ','331',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'33130','Consolidated Loans from Government Bodies and Association','',false,36,NULL,'A','L',NULL,2,false,false,6,NULL,' ',NULL,' ','331',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'33140','Consolidated Loans from International Agencies','',false,36,NULL,'A','L',NULL,2,false,false,6,NULL,' ',NULL,' ','331',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'33150','Consolidated Loans from Banks and Other Financial Institutions','',false,36,NULL,'A','L',NULL,2,false,false,6,NULL,' ',NULL,' ','331',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'33160','Consolidated Other Term Loans','',false,36,NULL,'A','L',NULL,2,false,false,6,NULL,' ',NULL,' ','331',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'33170','Consolidated Bonds and Debentures','',false,36,NULL,'A','L',NULL,2,false,false,6,NULL,' ',NULL,' ','331',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'33180','Consolidated Other Loans','',false,36,NULL,'A','L',NULL,2,false,false,6,NULL,' ',NULL,' ','331',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'34010','Consolidated From Contractors / Suppliers','',false,37,NULL,'A','L',NULL,2,false,false,7,NULL,' ',NULL,' ','340',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'34020','Consolidated Deposits Revenues','',false,37,NULL,'A','L',NULL,2,false,false,7,NULL,' ',NULL,' ','340',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'34030','Consolidated From Staff','',false,37,NULL,'A','L',NULL,2,false,false,7,NULL,' ',NULL,' ','340',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'34080','Consolidated From Others','',false,37,NULL,'A','L',NULL,2,false,false,7,NULL,' ',NULL,' ','340',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'34110','Consolidated Civil works','',false,38,NULL,'A','L',NULL,2,false,false,8,NULL,' ',NULL,' ','341',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'34120','Consolidated Electrical works','',false,38,NULL,'A','L',NULL,2,false,false,8,NULL,' ',NULL,' ','341',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'34180','Consolidated Others','',false,38,NULL,'A','L',NULL,2,false,false,8,NULL,' ',NULL,' ','341',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'35010','Consolidated Creditors','',false,39,NULL,'A','L',NULL,2,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'35011','Consolidated Employee Liabilities','',false,39,NULL,'A','L',NULL,2,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'35012','Consolidated Interest Accrued and due','',false,39,NULL,'A','L',NULL,2,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'35020','Consolidated Recoveries Payable','',false,39,NULL,'A','L',NULL,2,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'35030','Consolidated Government Dues payable','',false,39,NULL,'A','L',NULL,2,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'35040','Consolidated Refunds payable','',false,39,NULL,'A','L',NULL,2,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'35041','Consolidated Advance Collection of Revenues','',false,39,NULL,'A','L',NULL,2,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'35080','Consolidated Others','',false,39,NULL,'A','L',NULL,2,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'35090','Consolidated Sale Proceeds','',false,39,NULL,'A','L',NULL,2,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'36010','Consolidated Provisions for Expenses','',false,40,NULL,'A','L',NULL,2,false,false,10,NULL,' ',NULL,' ','360',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'36020','Consolidated Provision for Interest','',false,40,NULL,'A','L',NULL,2,false,false,10,NULL,' ',NULL,' ','360',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'36030','Consolidated Provision for Other Assets','',false,40,NULL,'A','L',NULL,2,false,false,10,NULL,' ',NULL,' ','360',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'41010','Consolidated Land','',false,41,NULL,'A','A',NULL,2,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'41020','Consolidated Building','',false,41,NULL,'A','A',NULL,2,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'41030','Consolidated Roads and Bridges','',false,41,NULL,'A','A',NULL,2,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'41031','Consolidated Sewerage and Drainage','',false,41,NULL,'A','A',NULL,2,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'41032','Consolidated Waterways','',false,41,NULL,'A','A',NULL,2,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'41033','Consolidated Public Lighting','',false,41,NULL,'A','A',NULL,2,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'41040','Consolidated Plant and Machinery','',false,41,NULL,'A','A',NULL,2,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'41050','Consolidated Vehicles','',false,41,NULL,'A','A',NULL,2,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'41060','Consolidated Office and Other Equipments','',false,41,NULL,'A','A',NULL,2,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'41070','Consolidated Furniture, Fixtures, Fittings and Electrical Appliances','',false,41,NULL,'A','A',NULL,2,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'41080','Consolidated Other Fixed Assets','',false,41,NULL,'A','A',NULL,2,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'41090','Consolidated Assets under Disposal','',false,41,NULL,'A','A',NULL,2,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'41120','Consolidated Buildings','',false,42,NULL,'A','A',NULL,2,false,false,12,NULL,' ',NULL,' ','411',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'41130','Consolidated Roads and Bridges','',false,42,NULL,'A','A',NULL,2,false,false,12,NULL,' ',NULL,' ','411',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'41131','Consolidated Sewerage and Drainage','',false,42,NULL,'A','A',NULL,2,false,false,12,NULL,' ',NULL,' ','411',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'41132','Consolidated Waterways','',false,42,NULL,'A','A',NULL,2,false,false,12,NULL,' ',NULL,' ','411',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'41133','Consolidated Public Lighting','',false,42,NULL,'A','A',NULL,2,false,false,12,NULL,' ',NULL,' ','411',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'41140','Consolidated Plant and Machinery','',false,42,NULL,'A','A',NULL,2,false,false,12,NULL,' ',NULL,' ','411',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'41150','Consolidated Vehicles','',false,42,NULL,'A','A',NULL,2,false,false,12,NULL,' ',NULL,' ','411',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'41160','Consolidated Office and Other Equipments','',false,42,NULL,'A','A',NULL,2,false,false,12,NULL,' ',NULL,' ','411',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'41170','Consolidated Furniture, Fixtures, Fittings and Electrical Appliances','',false,42,NULL,'A','A',NULL,2,false,false,12,NULL,' ',NULL,' ','411',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'41180','Consolidated Other Fixed Assets','',false,42,NULL,'A','A',NULL,2,false,false,12,NULL,' ',NULL,' ','411',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'41210','Consolidated Specific Grants','',false,43,NULL,'A','A',NULL,2,false,false,13,NULL,' ',NULL,' ','412',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'41220','Consolidated Special funds','',false,43,NULL,'A','A',NULL,2,false,false,13,NULL,' ',NULL,' ','412',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'41230','Consolidated Specific Schemes','',false,43,NULL,'A','A',NULL,2,false,false,13,NULL,' ',NULL,' ','412',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'41240','Consolidated Others','',false,43,NULL,'A','A',NULL,2,false,false,13,NULL,' ',NULL,' ','412',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'42010','Consolidated Central Government Securities','',false,44,NULL,'A','A',NULL,2,false,false,14,NULL,' ',NULL,' ','420',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'42020','Consolidated State Government Securities','',false,44,NULL,'A','A',NULL,2,false,false,14,NULL,' ',NULL,' ','420',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'42030','Consolidated Debentures and Bonds','',false,44,NULL,'A','A',NULL,2,false,false,14,NULL,' ',NULL,' ','420',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'42050','Consolidated Equity Shares','',false,44,NULL,'A','A',NULL,2,false,false,14,NULL,' ',NULL,' ','420',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'42060','Consolidated Units of Mutual Funds','',false,44,NULL,'A','A',NULL,2,false,false,14,NULL,' ',NULL,' ','420',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'42080','Consolidated Other Investments','',false,44,NULL,'A','A',NULL,2,false,false,14,NULL,' ',NULL,' ','420',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'42090','Consolidated Accumulated Provision','',false,44,NULL,'A','A',NULL,2,false,false,14,NULL,' ',NULL,' ','420',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'42110','Consolidated Central Government Securities','',false,45,NULL,'A','A',NULL,2,false,false,15,NULL,' ',NULL,' ','421',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'42120','Consolidated State Government Securities','',false,45,NULL,'A','A',NULL,2,false,false,15,NULL,' ',NULL,' ','421',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'42130','Consolidated Debentures and Bonds','',false,45,NULL,'A','A',NULL,2,false,false,15,NULL,' ',NULL,' ','421',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'42140','Consolidated Preference Shares','',false,45,NULL,'A','A',NULL,2,false,false,15,NULL,' ',NULL,' ','421',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'42150','Consolidated Equity Shares','',false,45,NULL,'A','A',NULL,2,false,false,15,NULL,' ',NULL,' ','421',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'42160','Consolidated Units of Mutual Funds','',false,45,NULL,'A','A',NULL,2,false,false,15,NULL,' ',NULL,' ','421',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'42180','Consolidated Other Investments','',false,45,NULL,'A','A',NULL,2,false,false,15,NULL,' ',NULL,' ','421',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'42190','Consolidated Accumulated Provision','',false,45,NULL,'A','A',NULL,2,false,false,15,NULL,' ',NULL,' ','421',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'43010','Consolidated Stock-in-Hand','',false,46,NULL,'A','A',NULL,2,false,false,16,NULL,' ',NULL,' ','430',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'43020','Consolidated Loose Tools','',false,46,NULL,'A','A',NULL,2,false,false,16,NULL,' ',NULL,' ','430',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'43080','Consolidated Other stores','',false,46,NULL,'A','A',NULL,2,false,false,16,NULL,' ',NULL,' ','430',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'43110','Consolidated Receivables for Property Taxes','',false,47,NULL,'A','A',NULL,2,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'43111','Consolidated Receivables for Conservancy/Latrine Tax','',false,47,NULL,'A','A',NULL,2,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'43112','Consolidated Receivables for Light Tax','',false,47,NULL,'A','A',NULL,2,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'43113','Consolidated Receivables for Water Tax','',false,47,NULL,'A','A',NULL,2,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'43114','Consolidated Receivables for Sewerage/Drainage Tax','',false,47,NULL,'A','A',NULL,2,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'43119','Consolidated Receivable for Other Taxes','',false,47,NULL,'A','A',NULL,2,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'43130','Consolidated Receivable for Fees and User Charges','',false,47,NULL,'A','A',NULL,2,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'43140','Consolidated Receivable from other sources','',false,47,NULL,'A','A',NULL,2,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'43150','Consolidated Receivable from Government','',false,47,NULL,'A','A',NULL,2,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'43180','Consolidated Receivables control accounts','',false,47,NULL,'A','A',NULL,2,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'43210','Consolidated Provision for outstanding Property Taxes','',false,48,NULL,'A','A',NULL,2,false,false,18,NULL,' ',NULL,' ','432',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'43211','Consolidated Provision for Outstanding Water Taxes','',false,48,NULL,'A','A',NULL,2,false,false,18,NULL,' ',NULL,' ','432',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'43212','Consolidated Provision for outstanding Other Taxes','',false,48,NULL,'A','A',NULL,2,false,false,18,NULL,' ',NULL,' ','432',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'43230','Consolidated Provision for outstanding Fees and User Charges','',false,48,NULL,'A','A',NULL,2,false,false,18,NULL,' ',NULL,' ','432',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'43240','Consolidated Provision for outstanding other receivable','',false,48,NULL,'A','A',NULL,2,false,false,18,NULL,' ',NULL,' ','432',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'44010','Consolidated prepaid Establishment','',false,49,NULL,'A','A',NULL,2,false,false,19,NULL,' ',NULL,' ','440',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'44020','Consolidated prepaid Administration','',false,49,NULL,'A','A',NULL,2,false,false,19,NULL,' ',NULL,' ','440',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'44030','Consolidated prepaid Operations and Maintenance','',false,49,NULL,'A','A',NULL,2,false,false,19,NULL,' ',NULL,' ','440',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'45010','Consolidated Cash','',false,50,NULL,'A','A',NULL,2,false,false,20,NULL,' ',NULL,' ','450',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'45020','Consolidated Bank Balances - Municipal Fund (all places)','',false,50,NULL,'A','A',NULL,2,false,false,20,NULL,' ',NULL,' ','450',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'45040','Consolidated Bank Balances - Special Fund (all places)','',false,50,NULL,'A','A',NULL,2,false,false,20,NULL,' ',NULL,' ','450',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'45060','Consolidated Bank Balances - Grant Fund (all places)','',false,50,NULL,'A','A',NULL,2,false,false,20,NULL,' ',NULL,' ','450',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'46010','Consolidated Loans and advances to Employees','',false,51,NULL,'A','A',NULL,2,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'46020','Consolidated Employee Provident Fund Loans','',false,51,NULL,'A','A',NULL,2,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'46030','Consolidated Loans to Others','',false,51,NULL,'A','A',NULL,2,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'46040','Consolidated Advance to Suppliers and Contractors','',false,51,NULL,'A','A',NULL,2,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'46050','Consolidated Advance to Others','',false,51,NULL,'A','A',NULL,2,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'46060','Consolidated Deposits with external Agencies','',false,51,NULL,'A','A',NULL,2,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'46080','Consolidated Other current Assets','',false,51,NULL,'A','A',NULL,2,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'46110','Consolidated Accumulated Provisions on Loans to Others','',false,52,NULL,'A','A',NULL,2,false,false,22,NULL,' ',NULL,' ','461',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'46120','Consolidated Accumulated Provisions on Advances','',false,52,NULL,'A','A',NULL,2,false,false,22,NULL,' ',NULL,' ','461',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'46130','Consolidated Accumulated Provisions onDeposits','',false,52,NULL,'A','A',NULL,2,false,false,22,NULL,' ',NULL,' ','461',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'47010','Consolidated Deposit Works Expenditure','',false,53,NULL,'A','A',NULL,2,false,false,23,NULL,' ',NULL,' ','470',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'47030','Consolidated Interest Control Receivable','',false,53,NULL,'A','A',NULL,2,false,false,23,NULL,' ',NULL,' ','470',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'47040','Clearing Accounts','',false,53,NULL,'A','A',NULL,2,false,false,23,NULL,' ',NULL,' ','470',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'47050','Statutory Dues Receivable','',false,53,NULL,'A','A',NULL,2,false,false,23,NULL,' ',NULL,' ','470',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'47120','Other Intangible Assets','',false,54,NULL,'A','A',NULL,2,false,false,24,NULL,' ',NULL,' ','471',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'48010','Consolidated Loan Issue Expenses','',false,55,NULL,'A','A',NULL,2,false,false,25,NULL,' ',NULL,' ','480',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'48020','Consolidated Discount on Issue of loans','',false,55,NULL,'A','A',NULL,2,false,false,25,NULL,' ',NULL,' ','480',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'48030','Consolidated Others','',false,55,NULL,'A','A',NULL,2,false,false,25,NULL,' ',NULL,' ','480',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1100101','Property Tax on Building','',true,56,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1100102','Property Tax on Land','',true,56,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1100103','Service Charges in lieu of Property Tax','',true,56,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1100104','Property Tax on Other Properties','',true,56,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1100105','Consolidated property tax on land and building','',true,56,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1100106','Town Development Cess','',true,56,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1100107','Education Cess','',true,56,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1100108','Interest/Surcharge on Property Tax/Holding Tax','',true,56,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1100109','Searching Fees','',true,56,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'1100201','Metered Water - Domestic Supply','',true,57,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1100202','Metered Water - Commercial Supply','',true,57,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1100203','Bulk Supply of filtered water','',true,57,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1100204','Bulk Supply of unfiltered water','',true,57,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1100205','Water Tax','',true,57,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1100301','Sewerage/Drainage Tax','',true,58,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1100401','Conservancy/Latrine Tax','',true,59,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1100501','Lighting Tax','',true,60,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1100601','Education Tax','',true,61,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1100701','Vehicle Tax','',true,62,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'1100801','Animal Tax','',true,63,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1100901','Electricity Tax','',true,64,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1101001','Professional Tax','',true,65,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1101101','Advertisement Tax - Land Hoardings','',true,66,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1101102','Advertisement Tax - Bus Shelters','',true,66,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1101103','Advertisement Tax - Neon Stands and Shops','',true,66,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1101104','Advertisement Tax - Hoardings on Private Land','',true,66,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1101105','Advertisement Tax - On Public Toilets','',true,66,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1101106','Advertisement Tax-Traffic Signals, Police Booth/Umbrella','',true,66,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1101107','Advertisement Tax - Footpath, Railing and Posts','',true,66,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'1101108','Advertisement Tax - On Railings of Tree Guards','',true,66,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1101109','Advertisement Tax - On Others','',true,66,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1101110','Advertisement Tax - Govt land','',true,66,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1101201','Pilgrimage Tax','',true,67,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1101301','Export Tax','',true,68,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1105101','Octroi and Toll Tax','',true,69,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1108001','Development Tax','',true,70,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1108002','Toll Tax','',true,70,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1108003','Entertainment Tax','',true,70,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1108004','Tax on Carriage and Carts','',true,70,NULL,'A','I',NULL,4,false,false,26,NULL,' ',NULL,' ','110',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'1201001','Compensation in lieu of Entertainment Tax/Public Resort','',true,71,NULL,'A','I',NULL,4,false,false,27,NULL,' ',NULL,' ','120',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1201002','Compensation in lieu of Duty on transfer of Properties','',true,71,NULL,'A','I',NULL,4,false,false,27,NULL,' ',NULL,' ','120',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1201003','Compensation in lieu of Penalty imposed by Courts','',true,71,NULL,'A','I',NULL,4,false,false,27,NULL,' ',NULL,' ','120',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1201004','Compensation in lieu of Mining Royalty','',true,71,NULL,'A','I',NULL,4,false,false,27,NULL,' ',NULL,' ','120',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1201005','Compensation in lieu of Professional Tax','',true,71,NULL,'A','I',NULL,4,false,false,27,NULL,' ',NULL,' ','120',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1202001','Compensation in lieu of Octroi','',true,72,NULL,'A','I',NULL,4,false,false,27,NULL,' ',NULL,' ','120',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1202002','Compensation in lieu of Pilgrim Tax','',true,72,NULL,'A','I',NULL,4,false,false,27,NULL,' ',NULL,' ','120',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1202003','Compensation in lieu of other taxes and duties','',true,72,NULL,'A','I',NULL,4,false,false,27,NULL,' ',NULL,' ','120',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'12030','Consolidated Compensations in lieu of Concessions','',true,6,NULL,'A','I',NULL,2,false,false,27,NULL,' ',NULL,' ','120',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1203001','Compensation in lieu of concessions','',true,389,NULL,'A','I',NULL,4,false,false,27,NULL,' ',NULL,' ','120',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'1301001','Rent from Markets','',true,73,NULL,'A','I',NULL,4,false,false,28,NULL,' ',NULL,' ','130',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1301002','Rent from Shopping Complexes','',true,73,NULL,'A','I',NULL,4,false,false,28,NULL,' ',NULL,' ','130',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1301003','Rent from Community Halls','',true,73,NULL,'A','I',NULL,4,false,false,28,NULL,' ',NULL,' ','130',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1301004','Rent from Stadium','',true,73,NULL,'A','I',NULL,4,false,false,28,NULL,' ',NULL,' ','130',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1301005','Rent from Yatri Niwas','',true,73,NULL,'A','I',NULL,4,false,false,28,NULL,' ',NULL,' ','130',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1301006','Rent from Kalyan Mandap','',true,73,NULL,'A','I',NULL,4,false,false,28,NULL,' ',NULL,' ','130',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1301007','Rent from Town Hall','',true,73,NULL,'A','I',NULL,4,false,false,28,NULL,' ',NULL,' ','130',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1301008','Rent from Other Properties','',true,73,NULL,'A','I',NULL,4,false,false,28,NULL,' ',NULL,' ','130',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1302001','Rent from Office Building','',true,74,NULL,'A','I',NULL,4,false,false,28,NULL,' ',NULL,' ','130',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'13030','Consolidated Rent from Guest Houses','',true,7,NULL,'A','I',NULL,2,false,false,28,NULL,' ',NULL,' ','130',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'1303001','Rent from Working Women Hostel','',true,400,NULL,'A','I',NULL,4,false,false,28,NULL,' ',NULL,' ','130',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1303002','Rent from Guest House','',true,400,NULL,'A','I',NULL,4,false,false,28,NULL,' ',NULL,' ','130',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1304001','Rent from lease of land','',true,75,NULL,'A','I',NULL,4,false,false,28,NULL,' ',NULL,' ','130',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'13080','Consolidated Other rents','',true,7,NULL,'A','I',NULL,4,false,false,28,NULL,' ',NULL,' ','130',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1308001','Lease Rentals - Others','',true,404,NULL,'A','I',NULL,4,false,false,28,NULL,' ',NULL,' ','130',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1308002','Rent from Opolfed/Omfed/Ground Rent','',true,404,NULL,'A','I',NULL,4,false,false,28,NULL,' ',NULL,' ','130',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401001','Fees from empanelment of Contractors','',true,76,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401002','Colony empanelment and inspection fees.','',true,76,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401101','Trade license fees','',true,77,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401102','License fees','',true,77,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'1401103','License fees from Dangerous/Offensive Trade','',true,77,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401104','Licensing fees from hawkers (u/s 307)','',true,77,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401105','Washer men license fees','',true,77,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401106','Shop licensing fees','',true,77,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401107','Fees from Casual Vendors','',true,77,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401108','Licensing fees (Staff Quarters)','',true,77,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401109','Fees for Projections/erections','',true,77,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401110','Plumbing licensing fees','',true,77,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401111','Ghat and Boat licensing fees','',true,77,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401112','Licensing fees from pounding houses','',true,77,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'1401113','Licensing fees from slaughter houses','',true,77,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401114','Licensing fees from butchers and traders of meat','',true,77,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401115','Licensing fees from trading of meat and flesh','',true,77,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401116','Licensing Fees from Bar','',true,77,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401117','Fees from leasing of ponds','',true,77,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401118','Rickshaw licensing fees','',true,77,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401119','Income from providers of telephony services','',true,77,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401120','Fees from Daily/Weekly Market','',true,77,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401121','Roadside Sale','',true,77,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401122','License fee on Row','',true,77,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'1401123','Licensing Fee on Poles','',true,77,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401201','Fees from sanction of building plans','',true,78,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401202','Compounding Fees','',true,78,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401203','','',true,78,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401301','Fees from copies of plan','',true,79,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401302','Birth and Death Registration fees','',true,79,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401401','Development Charges','',true,80,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401402','Betterment Charges','',true,80,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401403','Demolition Charges','',true,80,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401501','Regularisation Fees - Encroachment','',true,81,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'1401502','Regularisation Fees - Agreement','',true,81,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1401503','Regularisation Fees - Building Construction','',true,81,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1402001','Penalty - Property Tax','',true,82,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1402002','Penalty - Water tax','',true,82,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1402003','Penalty - Rents','',true,82,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1402004','Penalty - Trade License','',true,82,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1402005','Penalty - Others','',true,82,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1404001','Advertisement fees','',true,83,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1404002','Cattle pounding fees','',true,83,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1404003','Education fees','',true,83,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'1404004','Sports fees','',true,83,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1404005','Property transfer charges/Mutation Fees','',true,83,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1404006','Notice Fee/Scrutiny Fee','',true,83,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1404007','Warrant fees','',true,83,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1404008','Water and Drain connection charges','',true,83,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1404009','Electricity supply fees','',true,83,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1404010','Delay fees','',true,83,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1404011','Application fees','',true,83,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1404012','Miscellaneous fees','',true,83,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1404013','Marriage Registration Fees','',true,83,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'1404014','RTI Application Fees','',true,83,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1404015','Sponsorship fees','',true,83,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1404016','Fire Fighting Fee','',true,83,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1404017','Retention Fee','',true,83,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1405001','Litter and debris collection charges','',true,84,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1405002','Septic tank cleaning charges','',true,84,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1405003','Ambulance charges','',true,84,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1405004','Examination Charges (Tests, xrays, ultra sound)','',true,84,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1405005','Funeral Van (Hearse) charges','',true,84,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1405006','Sewerage cleaning charges','',true,84,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'1405007','Pay and Use toilets','',true,84,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1405008','Parking fees (On contract)','',true,84,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1405009','Water supply','',true,84,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1405010','Charges for supply of water by tankers','',true,84,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1405011','Rent of water meter','',true,84,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1405012','Sale of water for industrial / commercial use','',true,84,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1405013','Crematorium Fees','',true,84,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1405014','Parking Fees from Bus/Car/Taxi/Auto/Rickshaw/Cycle Stand','',true,84,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1405015','User Fees','',true,84,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1405016','User Fees - NOC','',true,84,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'1405017','Express Cleaning','',true,84,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1405018','Income from Shoe Stand','',true,84,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1405019','Income from Temporary Shed/Platforms','',true,84,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1405020','Library Fees','',true,84,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1405021','Shelter for Urban Homeless(SUH) Fees','',true,84,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1406001','User charges from swimming pool','',true,85,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1406002','Entry Fees of Rajendra Park','',true,85,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1406003','Entry Fee from Parks','',true,85,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1407001','Charges as a percentage on deposit works','',true,86,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1407002','Recovery charges for damages to roads','',true,86,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'1407003','Stacking charges','',true,86,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1407004','Service charges','',true,86,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1407005','Overhead Charges (OHC)','',true,86,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1408001','Other Fees and Charges','',true,87,NULL,'A','I',NULL,4,false,false,29,NULL,' ',NULL,' ','140',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1501001','Sale of garbage and rubbish','',true,88,NULL,'A','I',NULL,4,false,false,30,NULL,' ',NULL,' ','150',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1501002','Sale of trees','',true,88,NULL,'A','I',NULL,4,false,false,30,NULL,' ',NULL,' ','150',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1501003','Sale of fruits','',true,88,NULL,'A','I',NULL,4,false,false,30,NULL,' ',NULL,' ','150',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1501004','Sale of grass','',true,88,NULL,'A','I',NULL,4,false,false,30,NULL,' ',NULL,' ','150',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1501005','Sale of nursery plants','',true,88,NULL,'A','I',NULL,4,false,false,30,NULL,' ',NULL,' ','150',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1501006','Sale of flowers','',true,88,NULL,'A','I',NULL,4,false,false,30,NULL,' ',NULL,' ','150',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'1501007','Sale of Water by Water-Tankers','',true,88,NULL,'A','I',NULL,4,false,false,30,NULL,' ',NULL,' ','150',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1501008','Sale of PDS Items','',true,88,NULL,'A','I',NULL,4,false,false,30,NULL,' ',NULL,' ','150',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1501101','Sale of tender papers','',true,89,NULL,'A','I',NULL,4,false,false,30,NULL,' ',NULL,' ','150',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1501102','Sale of ration card and other forms','',true,89,NULL,'A','I',NULL,4,false,false,30,NULL,' ',NULL,' ','150',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1501201','Sale of Stores and Scrap - Obsolete Stores','',true,90,NULL,'A','I',NULL,4,false,false,30,NULL,' ',NULL,' ','150',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1501202','Sale of Stores and Scrap - Obsolete Assets','',true,90,NULL,'A','I',NULL,4,false,false,30,NULL,' ',NULL,' ','150',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1501203','Sale of Bitumen/Drums/Empty Gunny Bags','',true,90,NULL,'A','I',NULL,4,false,false,30,NULL,' ',NULL,' ','150',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1503001','Sale of old newspapers','',true,91,NULL,'A','I',NULL,4,false,false,30,NULL,' ',NULL,' ','150',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1504001','Hire Charges for Vehicles','',true,92,NULL,'A','I',NULL,4,false,false,30,NULL,' ',NULL,' ','150',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1504101','Hire charges on road rollers','',true,93,NULL,'A','I',NULL,4,false,false,30,NULL,' ',NULL,' ','150',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'1504102','Hire charges on Tools and Equipments (Excavator, Tipper, etc.)','',true,93,NULL,'A','I',NULL,4,false,false,30,NULL,' ',NULL,' ','150',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1601001','Revenue Grant from State Government','',true,94,NULL,'A','I',NULL,4,false,false,31,NULL,' ',NULL,' ','160',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1601002','Revenue Grant from Central Government','',true,94,NULL,'A','I',NULL,4,false,false,31,NULL,' ',NULL,' ','160',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1601003','Revenue Grant from Others','',true,94,NULL,'A','I',NULL,4,false,false,31,NULL,' ',NULL,' ','160',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1602001','Reimbursement of Expense by State Govt.','',true,95,NULL,'A','I',NULL,4,false,false,31,NULL,' ',NULL,' ','160',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1602002','Reimbursement of Expense by Central Govt.','',true,95,NULL,'A','I',NULL,4,false,false,31,NULL,' ',NULL,' ','160',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1602003','Reimbursement of Expense by Others','',true,95,NULL,'A','I',NULL,4,false,false,31,NULL,' ',NULL,' ','160',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1602004','Reimbursement of CENSUS related expenditure','',true,95,NULL,'A','I',NULL,4,false,false,31,NULL,' ',NULL,' ','160',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1603001','Contribution towards Schemes from State Govt.','',true,96,NULL,'A','I',NULL,4,false,false,31,NULL,' ',NULL,' ','160',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1603002','Contribution towards Schemes from Central Govt.','',true,96,NULL,'A','I',NULL,4,false,false,31,NULL,' ',NULL,' ','160',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'1603003','Contribution towards Schemes from Others','',true,96,NULL,'A','I',NULL,4,false,false,31,NULL,' ',NULL,' ','160',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1701001','Interest on Fixed Deposit','',true,97,NULL,'A','I',NULL,4,false,false,32,NULL,' ',NULL,' ','170',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1701002','Interest on Government Securities','',true,97,NULL,'A','I',NULL,4,false,false,32,NULL,' ',NULL,' ','170',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1701003','Interest on Post Office Savings','',true,97,NULL,'A','I',NULL,4,false,false,32,NULL,' ',NULL,' ','170',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1702001','Dividend Income','',true,98,NULL,'A','I',NULL,4,false,false,32,NULL,' ',NULL,' ','170',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1703001','Income from Commercial Projects','',true,99,NULL,'A','I',NULL,4,false,false,32,NULL,' ',NULL,' ','170',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1704001','Profit on sale of Investments - Municipal Funds','',true,100,NULL,'A','I',NULL,4,false,false,32,NULL,' ',NULL,' ','170',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1708001','Gain from Exchange Fluctuation','',true,101,NULL,'A','I',NULL,4,false,false,32,NULL,' ',NULL,' ','170',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1711001','Interest from Bank Accounts','',true,102,NULL,'A','I',NULL,4,false,false,33,NULL,' ',NULL,' ','171',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1712001','Interest on Loans to Employees - House Building Loans','',true,103,NULL,'A','I',NULL,4,false,false,33,NULL,' ',NULL,' ','171',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'1712002','Interest on Loans to Employees - Vehicle Loans','',true,103,NULL,'A','I',NULL,4,false,false,33,NULL,' ',NULL,' ','171',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1712003','Interest on Loans to Employees - Computer Loans','',true,103,NULL,'A','I',NULL,4,false,false,33,NULL,' ',NULL,' ','171',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1712004','Interest on Loans to Employees - Food/Grain Loans','',true,103,NULL,'A','I',NULL,4,false,false,33,NULL,' ',NULL,' ','171',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1712005','Interest on Loans to Employees - Other Loans','',true,103,NULL,'A','I',NULL,4,false,false,33,NULL,' ',NULL,' ','171',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1713001','Interest on loans to others','',true,104,NULL,'A','I',NULL,4,false,false,33,NULL,' ',NULL,' ','171',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1718001','Interest from other receivables','',true,105,NULL,'A','I',NULL,4,false,false,33,NULL,' ',NULL,' ','171',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1718002','Interest on Hire Purchase','',true,105,NULL,'A','I',NULL,4,false,false,33,NULL,' ',NULL,' ','171',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1801001','Deposits Forfeited','',true,106,NULL,'A','I',NULL,4,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1801101','Deposits Lapsed - Contractors and Suppliers','',true,107,NULL,'A','I',NULL,4,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1801102','Deposits Lapsed - Rent','',true,107,NULL,'A','I',NULL,4,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'1801103','Deposits Lapsed - Others','',true,107,NULL,'A','I',NULL,4,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1802001','Insurance Claim Recovery','',true,108,NULL,'A','I',NULL,4,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1803001','Profit on disposal of fixed assets','',true,109,123,'A','I',NULL,4,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1804001','Recovery from Employees','',true,110,NULL,'A','I',NULL,4,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1804002','Recovery From Employees - Vehicle Usage','',true,110,NULL,'A','I',NULL,4,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1804003','Recovery From Employees - Quarter Rent','',true,110,NULL,'A','I',NULL,4,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1805001','Liabilities Written Back - Lapsed / Stale Cheque','',true,111,NULL,'A','I',NULL,4,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1806001','Provision Written Back - Property Tax','',true,112,NULL,'A','I',NULL,4,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1806002','Provision Written Back - Water Supply','',true,112,NULL,'A','I',NULL,4,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1806003','Provision Written Back - Advertisement Tax','',true,112,NULL,'A','I',NULL,4,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'1806004','Provision Written Back - Rent','',true,112,NULL,'A','I',NULL,4,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1808001','Penalty on Contractors','',true,113,NULL,'A','I',NULL,4,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1808002','Hospital Income - Pathology','',true,113,NULL,'A','I',NULL,4,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1808003','Hospital Income - Doctors/Cabin/Nursing Home Fees','',true,113,NULL,'A','I',NULL,4,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1808004','Hospital Income - Others','',true,113,NULL,'A','I',NULL,4,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1808005','Audit Recovery','',true,113,NULL,'A','I',NULL,4,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1808006','Income from Town Bus Service','',true,113,NULL,'A','I',NULL,4,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1808007','Donation Received','',true,113,NULL,'A','I',NULL,4,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1808008','Recovery-Others','',true,113,NULL,'A','I',NULL,4,false,false,34,NULL,' ',NULL,' ','180',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1851001','Property and Other Taxes','',true,114,NULL,'A','I',NULL,4,false,false,35,NULL,' ',NULL,' ','185',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'1852001','Prior Period Income - Water Supply','',true,115,NULL,'A','I',NULL,4,false,false,35,NULL,' ',NULL,' ','185',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1853001','Prior Period Income - Others','',true,116,NULL,'A','I',NULL,4,false,false,35,NULL,' ',NULL,' ','185',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1901001','Transfer from general account','',true,117,NULL,'A','I',NULL,4,false,false,36,NULL,' ',NULL,' ','190',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1902001','Transfer from water supply, sewerage and drainage account','',true,118,NULL,'A','I',NULL,4,false,false,36,NULL,' ',NULL,' ','190',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1903001','Transfer from road development and maintenance account','',true,119,NULL,'A','I',NULL,4,false,false,36,NULL,' ',NULL,' ','190',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1904001','Transfer from bustee services account','',true,120,NULL,'A','I',NULL,4,false,false,36,NULL,' ',NULL,' ','190',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1905001','Transfer from commercial projects account','',true,121,NULL,'A','I',NULL,4,false,false,36,NULL,' ',NULL,' ','190',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1906001','Transfer from solid waste management account','',true,122,NULL,'A','I',NULL,4,false,false,36,NULL,' ',NULL,' ','190',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1907001','Transfer from environment management account','',true,123,NULL,'A','I',NULL,4,false,false,36,NULL,' ',NULL,' ','190',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1911001','Transfer from Corporator Fund','',true,124,NULL,'A','I',NULL,4,false,false,37,NULL,' ',NULL,' ','191',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'1921001','Transfer from Pension Fund','',true,125,NULL,'A','I',NULL,4,false,false,38,NULL,' ',NULL,' ','192',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1922001','Transfer from Gratuity and Leave Salary Fund','',true,126,NULL,'A','I',NULL,4,false,false,38,NULL,' ',NULL,' ','192',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'1923001','Transfer from Provident Fund','',true,127,NULL,'A','I',NULL,4,false,false,38,NULL,' ',NULL,' ','192',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2101001','Salaries and Allowances - Officers','',true,128,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2101002','Salaries and Allowances - Staff','',true,128,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2101003','Wages','',true,128,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2101004','Bonus and Ex-Gratia','',true,128,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2101005','Revised Pay Arrear','',true,128,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2101006','Wages-Outsource Employees','',true,128,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2102001','Remuneration and Fees - Corporators, Mayor and Mayor-in-Council, etc.','',true,129,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'2102002','Remuneration and Fees - Officers and Staff','',true,129,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2102003','Leave Travel Concession','',true,129,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2102004','Medical Allowance','',true,129,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2102005','Uniform Allowance','',true,129,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2102006','Compensation to Staff','',true,129,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2102007','Staff welfare expenses','',true,129,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2102008','Staff training expenses','',true,129,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2102009','House Rent Allowance','',true,129,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2102010','Assured Career Progression (ACP)','',true,129,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2102011','Leave Salary','',true,129,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'2103001','Pension / Family Pension','',true,130,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2103002','Contribution for deficit in Pension Fund','',true,130,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2103003','Pension Fund Contribution','',true,130,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2103004','Pension Fund Employer''s Contribution - NPS','',true,130,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2104001','Death cum Retirement Benefit','',true,131,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2104002','Retirement Gratuity','',true,131,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2104003','Provident Fund Contribution','',true,131,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2104004','Contribution for deficit in Provident Fund','',true,131,NULL,'A','E',NULL,4,false,false,39,NULL,' ',NULL,' ','210',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2201001','Rent - Office Building','',true,132,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2201002','Rent - Others','',true,132,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'2201003','Rates and Taxes','',true,132,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2201004','Road Tax - RTO','',true,132,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2201005','Rent-Quarters','',true,132,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2201101','Electricity charges - Official Premises','',true,133,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2201102','Security expenses - Official Premises','',true,133,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2201201','Telephone expenses','',true,134,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2201202','Fax expenses','',true,134,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2201203','Postage and Courier expenses','',true,134,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2201204','Internet and Broadband Charges','',true,134,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2201205','DTH Service Expenses','',true,134,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'2202001','Magazines','',true,135,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2202002','Newspapers','',true,135,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2202101','Printing expenses','',true,136,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2202102','Stationery','',true,136,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2202103','Computer stationery and consumables','',true,136,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2203001','Traveling and Vehicle expenses','',true,137,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2203002','Fuel, Petrol and Diesel - Travel','',true,137,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2203003','Hire and Conveyance expenses','',true,137,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2204001','Insurance Charges','',true,138,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2205001','Audit Fees','',true,139,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'2205101','Legal Fees','',true,140,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2205102','Tax Revenue Recovery Expense','',true,140,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2205103','Cost of suits / compromises','',true,140,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2205201','Architects'' fee','',true,141,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2205202','Technical fees','',true,141,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2205203','Consultancy fees','',true,141,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2206001','Guest entertainment expenses','',true,142,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2206002','Advertisement expenses','',true,142,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2206003','Exhibition expenses','',true,142,NULL,'A','E',NULL,4,false,false,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2206101','Membership and Subscription Fees','',true,143,NULL,'A','E',NULL,4,false,true,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'2208001','Expenses for Meeting of ULBs','',true,144,NULL,'A','E',NULL,4,false,true,40,NULL,' ',NULL,' ','220',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2301001','Electricity Charges - Operation and Maintenance','',true,145,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2301002','Diesel/Petrol/Mobil - Operation and Maintenance','',true,145,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2302001','Bulk Water Purchase Expenses','',true,146,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2302002','Bulk Electricity Purchase Expenses','',true,146,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2302003','Purchase of PDS Items','',true,146,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2303001','Consumption of Stores','',true,147,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2303002','Consumption of General Stores','',true,147,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2303003','Consumption of Electrical Stores','',true,147,NULL,'A','E',NULL,4,false,true,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2303004','Consumption of Hospital Stores','',true,147,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'2303005','Consumption of Phynile, Bleaching & Other Sanitation goods','',true,147,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2304001','Hire Charges of machineries','',true,148,NULL,'A','E',NULL,4,false,true,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305001','Repair and Maintenance - Roads and Bridges','',true,149,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305002','Repair and Maintenance - Flyovers','',true,149,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305003','Repair and Maintenance - Water Supply and Drains','',true,149,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305004','Repair and Maintenance - Street Lighting System','',true,149,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305005','Repair and Maintenance - Storm Water Drains','',true,149,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305006','Repair and Maintenance - Traffic Signals','',true,149,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305101','Repair and Maintenance - Parks, Nurseries and Gardens','',true,150,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305102','Repair and Maintenance - Lakes and Ponds','',true,150,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'2305103','Repair and Maintenance - Playgrounds and Stadium','',true,150,NULL,'A','E',NULL,4,false,true,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305104','Repair and Maintenance - Swimming Pool','',true,150,NULL,'A','E',NULL,4,false,true,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305105','Repair and Maintenance - Parking Lots','',true,150,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305106','Repair and Maintenance - Markets and Complexes','',true,150,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305107','Repair and Maintenance - Public Toilets','',true,150,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305108','Repair and Maintenance - Street Lights','',true,150,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305109','Repair and Maintenance - Play materials','',true,150,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305110','Repair and Maintenance - Fire Tender Engines','',true,150,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305201','Repair and Maintenance - Office Buildings','',true,151,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305202','Repair and Maintenance - Residential Buildings','',true,151,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'2305203','Repair and Maintenance - Other Buildings','',true,151,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305301','Repair and Maintenance - Vehicles','',true,152,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305901','Repair and Maintenance - Furniture and Fixture','',true,153,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305902','Repair and Maintenance - Electrical Appliances','',true,153,NULL,'A','E',NULL,4,false,true,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305903','Repair and Maintenance - Office Equipments','',true,153,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305904','Repair and Maintenance - Survey and Drawing Equipments','',true,153,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305905','Repair and Maintenance - Other fixed assets','',true,153,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305906','Repair & Maintenance - Plant and Machinery','',true,153,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305907','Repair & Maintenance - Others','',true,153,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2305908','Annual Maintenance Charges','',true,153,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'2308001','Water Purification charges','',true,154,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2308002','Testing and Inspection charges','',true,154,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2308003','Garbage and Clearance expenses','',true,154,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2308004','Cleaning by private agencies','',true,154,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2308005','Water Tankers - Operation and Maintenance','',true,154,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2308006','Night shelter maintenance Expenses','',true,154,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2308007','Announcement Expenses','',true,154,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2308008','Expenditure on Jalachatra (heatwave)','',true,154,NULL,'A','E',NULL,4,false,false,41,NULL,' ',NULL,' ','230',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2401001','Interest on Loans from Central Government','',true,155,NULL,'A','E',NULL,4,false,false,42,NULL,' ',NULL,' ','240',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2402001','Interest on Loans from State Government','',true,156,NULL,'A','E',NULL,4,false,true,42,NULL,' ',NULL,' ','240',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'2403001','Interest on Loans from Government Bodies and Associations','',true,157,NULL,'A','E',NULL,4,false,false,42,NULL,' ',NULL,' ','240',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2404001','Interest on Loans from International Agencies','',true,158,NULL,'A','E',NULL,4,false,false,42,NULL,' ',NULL,' ','240',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2405001','Interest on Loans from Banks and Other Financial Institutions','',true,159,NULL,'A','E',NULL,4,false,false,42,NULL,' ',NULL,' ','240',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2406001','Other Interest','',true,160,NULL,'A','E',NULL,4,false,false,42,NULL,' ',NULL,' ','240',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2407001','Bank Charges','',true,161,NULL,'A','E',NULL,4,false,true,42,NULL,' ',NULL,' ','240',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2408001','Discount/Rebate on Early / Prompt Payments','',true,162,NULL,'A','E',NULL,4,false,false,42,NULL,' ',NULL,' ','240',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2408002','Other Finance Expenses','',true,162,NULL,'A','E',NULL,4,false,false,42,NULL,' ',NULL,' ','240',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2501001','Election Expense','',true,163,NULL,'A','E',NULL,4,false,false,43,NULL,' ',NULL,' ','250',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2501002','Honorarium for Census Work/Census Expenditure','',true,163,NULL,'A','E',NULL,4,false,false,43,NULL,' ',NULL,' ','250',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2502001','Training Programme Expense','',true,164,NULL,'A','E',NULL,4,false,false,43,NULL,' ',NULL,' ','250',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'2502002','Puja and Celebration Expense','',true,164,NULL,'A','E',NULL,4,false,false,43,NULL,' ',NULL,' ','250',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2502003','Awareness Program Expense','',true,164,NULL,'A','E',NULL,4,false,false,43,NULL,' ',NULL,' ','250',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2502004','NFSA Expenditure','',true,164,NULL,'A','E',NULL,4,false,true,43,NULL,' ',NULL,' ','250',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2503001','Share in Programme of Others','',true,165,NULL,'A','E',NULL,4,false,false,43,NULL,' ',NULL,' ','250',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2603001','Waiver of License Fee/Penalty/Property Tax','',true,168,NULL,'A','E',NULL,4,false,false,44,NULL,' ',NULL,' ','260',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2701001','Provisions for Doubtful Receivables - Property and Other taxes','',true,169,NULL,'A','E',NULL,4,false,false,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2701002','Provisions-Doubtful Receivables FeesandUserCharges-WaterSupply','',true,169,NULL,'A','E',NULL,4,false,false,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2701003','Provisions for Doubtful Receivables - Rent','',true,169,NULL,'A','E',NULL,4,false,true,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2702001','Provision for other assets - Store items','',true,170,NULL,'A','E',NULL,4,false,false,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2702002','Provision for other assets - Fixed assets','',true,170,NULL,'A','E',NULL,4,false,false,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'2702003','Provision for other assets - Investments','',true,170,NULL,'A','E',NULL,4,false,false,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2703001','Revenues written off - Property Tax','',true,171,NULL,'A','E',NULL,4,false,false,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2703002','Revenues written off - Assigned Revenues','',true,171,NULL,'A','E',NULL,4,false,true,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2703003','Revenues written off - Grants','',true,171,NULL,'A','E',NULL,4,false,true,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2703004','Revenues written off - Other Income','',true,171,NULL,'A','E',NULL,4,false,false,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2704001','Assets written off - Store items','',true,172,NULL,'A','E',NULL,4,false,true,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2704002','Assets written off - Fixed assets','',true,172,NULL,'A','E',NULL,4,false,false,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2704003','Assets written off - Loss in exchange fluctuation','',true,172,NULL,'A','E',NULL,4,false,false,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2705001','Misc. Exp. Written Off - Debentures and Bond issue expenses','',true,173,NULL,'A','E',NULL,4,false,false,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2709001','Tax Remission and Refunds - Property Tax','',true,174,NULL,'A','E',NULL,4,false,false,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'2709002','Tax Remission and Refunds - Other Tax','',true,174,NULL,'A','E',NULL,4,false,false,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2709101','Fees Remission and Refunds - Water Supply','',true,175,NULL,'A','E',NULL,4,false,true,45,NULL,' ',NULL,' ','270',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2711001','Loss on disposal of assets','',true,176,124,'A','E',NULL,4,false,false,46,NULL,' ',NULL,' ','271',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2712001','Loss on disposal of Investments','',true,177,NULL,'A','E',NULL,4,false,false,46,NULL,' ',NULL,' ','271',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2718001','Miscellaneous Expenses','',true,178,NULL,'A','E',NULL,4,false,false,46,NULL,' ',NULL,' ','271',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2718002','Hospital Expense - Diet/Food','',true,178,NULL,'A','E',NULL,4,false,false,46,NULL,' ',NULL,' ','271',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2718003','Hospital Expense - Medicine & Consumables','',true,178,NULL,'A','E',NULL,4,false,false,46,NULL,' ',NULL,' ','271',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2718004','Hospital Expense - Others','',true,178,NULL,'A','E',NULL,4,false,false,46,NULL,' ',NULL,' ','271',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2718005','Obsequies - Cremation Ceremony Expense','',true,178,NULL,'A','E',NULL,4,false,false,46,NULL,' ',NULL,' ','271',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2718006','Relief Expense','',true,178,NULL,'A','E',NULL,4,false,false,46,NULL,' ',NULL,' ','271',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'2718007','Plantation Expenditure','',true,178,NULL,'A','E',NULL,4,false,false,46,NULL,' ',NULL,' ','271',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2718008','Rehabilitation of Slum Dwellers','',true,178,NULL,'A','E',NULL,4,false,false,46,NULL,' ',NULL,' ','271',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2718009','Expenses towards Smart City','',true,178,NULL,'A','E',NULL,4,false,false,46,NULL,' ',NULL,' ','271',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2718010','IHHL(Individual House Hold Latrine) Expenses','',true,178,NULL,'A','E',NULL,4,false,false,46,NULL,' ',NULL,' ','271',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2718011','Expenses towards Covid-19','',true,178,NULL,'A','E',NULL,4,false,false,46,NULL,' ',NULL,' ','271',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2722001','Depreciation - Building','',true,179,NULL,'A','E',NULL,4,false,false,47,NULL,' ',NULL,' ','272',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2723001','Depreciation - Roads and Bridges','',true,180,NULL,'A','E',NULL,4,false,false,47,NULL,' ',NULL,' ','272',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2723101','Depreciation - Sewerage and Drainage','',true,181,NULL,'A','E',NULL,4,false,false,47,NULL,' ',NULL,' ','272',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2723201','Depreciation - Waterways','',true,182,NULL,'A','E',NULL,4,false,false,47,NULL,' ',NULL,' ','272',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2723301','Depreciation - Public Lighting','',true,183,NULL,'A','E',NULL,4,false,false,47,NULL,' ',NULL,' ','272',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'2724001','Depreciation - Plant and Machinery','',true,184,NULL,'A','E',NULL,4,false,false,47,NULL,' ',NULL,' ','272',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2725001','Depreciation - Vehicles','',true,185,NULL,'A','E',NULL,4,false,false,47,NULL,' ',NULL,' ','272',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2726001','Depreciation - Office and Other Equipments','',true,186,NULL,'A','E',NULL,4,false,false,47,NULL,' ',NULL,' ','272',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2727001','Depreciation - Furniture, Fixtures, Fittings and Electrical','',true,187,NULL,'A','E',NULL,4,false,false,47,NULL,' ',NULL,' ','272',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2728001','Depreciation - Other Fixed Assets','',true,188,NULL,'A','E',NULL,4,false,false,47,NULL,' ',NULL,' ','272',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2855001','Prior Period Expenses - Refund of Taxes - Property and Other','',true,189,NULL,'A','E',NULL,4,false,false,48,NULL,' ',NULL,' ','285',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2856001','PriorPeriodExpense-Refund of OtherRevenues-WaterSupplyCharge','',true,190,NULL,'A','E',NULL,4,false,true,48,NULL,' ',NULL,' ','285',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2856002','Prior Period ExpensesRefund of Other Revenues - Rent','',true,190,NULL,'A','E',NULL,4,false,false,48,NULL,' ',NULL,' ','285',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2858001','Prior Period Expenses - Other','',true,191,NULL,'A','E',NULL,4,false,true,48,NULL,' ',NULL,' ','285',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2901001','Transfer to general account','',true,192,NULL,'A','E',NULL,4,false,false,49,NULL,' ',NULL,' ','290',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'2902001','Transfer to water supply, sewerage and drainage account','',true,193,NULL,'A','E',NULL,4,false,false,49,NULL,' ',NULL,' ','290',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2903001','Transfer to road development and maintenance account','',true,194,NULL,'A','E',NULL,4,false,false,49,NULL,' ',NULL,' ','290',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2904001','Transfer to bustee services account','',true,195,NULL,'A','E',NULL,4,false,false,49,NULL,' ',NULL,' ','290',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2905001','Transfer to commercial projects account','',true,196,NULL,'A','E',NULL,4,false,false,49,NULL,' ',NULL,' ','290',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2906001','Transfer to solid waste management account','',true,197,NULL,'A','E',NULL,4,false,false,49,NULL,' ',NULL,' ','290',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2907001','Transfer to environment management account','',true,198,NULL,'A','E',NULL,4,false,false,49,NULL,' ',NULL,' ','290',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2911001','Transfer to corporator fund','',true,199,NULL,'A','E',NULL,4,false,false,50,NULL,' ',NULL,' ','291',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2921001','Transfer to pension fund','',true,200,NULL,'A','E',NULL,4,false,false,51,NULL,' ',NULL,' ','292',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2922001','Transfer to gratuity and leave salary fund','',true,201,NULL,'A','E',NULL,4,false,false,51,NULL,' ',NULL,' ','292',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'2923001','Transfer to Provident fund','',true,202,NULL,'A','E',NULL,4,false,false,51,NULL,' ',NULL,' ','292',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3101001','Municipal (General) Fund','',true,203,NULL,'A','L',NULL,4,false,false,1,NULL,' ',NULL,' ','310',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3101002','Adjustments to Opening Balance Sheet','',true,203,NULL,'A','L',NULL,4,false,false,1,NULL,' ',NULL,' ','310',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3109001','Excess of Income over Expenditure','',true,204,7,'A','L',NULL,4,false,false,1,NULL,' ',NULL,' ','310',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3121001','Capital Contribution','',true,208,NULL,'A','L',NULL,4,false,true,3,NULL,' ',NULL,' ','312',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3201001','Grants from Central Govt','',true,215,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3201002','13th Finance Commission Grant','',true,215,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3201003','Grant for Development of Bindusagar Lake','',true,215,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3201004','12th Finance Commission Grant (Roads & Bridges)','',true,215,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3201005','Grant - Social Economic Caste Sensus (SECC)','',true,215,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3201006','BRGF - Central Grant','',true,215,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3201007','IHSDP - Central Grant','',true,215,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3201008','IGNOAP - Central Grant','',true,215,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3201009','IGNWP - Central Grant','',true,215,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3201010','IGNDP - Central Grant','',true,215,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3201011','UIDSSMT - Central Grant','',true,215,NULL,'A','L',NULL,4,false,true,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3201012','JNNURM - BSUP - Housing','',true,215,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3201013','JNNURM - BSUP - Infra','',true,215,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3201014','JNNURM - Pipe Water Supply','',true,215,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3201015','General Performance Grant','',true,215,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3201016','Grant for Swachh Bharat Mission','',true,215,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3201017','14th Finance Grant','',true,215,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3201018','Grant for Smart City Mission','',true,215,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3201019','Grant for AMRUT','',true,215,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3201020','15th Finance Commission Grant','',true,215,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202001','Grants from Central Finance Commission','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202002','Grants from State Finance Commission','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202003','Grants for Road Development','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202004','National Slum Development Programme (NSDP)','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202005','MPLAD/MLA funds','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202006','Grants for Drinking Water programme','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3202007','Basic Minimum Programme','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202008','VAMBAY','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202009','SJSRY','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202010','National Family Benefit Scheme (NFBS)','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202011','State Insurance Scheme','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202012','Mid-Day Meal Program','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202013','Remuneration to Teachers','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202014','Relief for Unemployed','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202015','Other Grants','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202016','Grant for Renovation of Dying Water Bodies','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3202017','Grant for Development of Park','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202018','Grant for Accounting Reforms','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202019','Election Fund Grant','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202020','Grants for Construction of Boundary Wall','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202021','Grant for Dev. And Beautification of Old Town','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202022','DP- Aids','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202023','Grant for Hospital (CMR Fund)','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202024','Old Age Pension Grant','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202025','Grant - Storm Water Drainage Project','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202026','IHSDP - State Grant','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3202027','Kalyan Mandap - State Grant','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202028','Motor Vehicle - State Grant','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202029','Road & Bridge - State Grant','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202030','Special Development Funds (C.C Road)- State Grant','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202031','Biju KBK - State Grant','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202032','MBPY - State Grant','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202033','Pension/Family Pension - State Grant','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202034','Devolution of Fund - State Grant','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202035','Harischandra Sahayata - State Grant','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202036','Urban Asset Creation - State Grant','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3202037','Integrated Low Cost Sanitation Work (ILCS) - State Grant','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202038','Special Problem Fund - State Grant','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202039','Car Festival Grant/Local Festival Grant - State Grant','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202040','Grants for Construction of Public Toilets - State Grant','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202041','Grants for Solid Waste Management - State Grant','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202042','Grants for Maintenance of Non-Residential Buildings - State','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202043','Performace Based Incentives for Providing Basic Urban Servic','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202044','Animal Birth Control - State Grant','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202045','13th FC - Roads & Bridges - State Grant','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202046','Development of Night Shelter/Community Amenities - State Gra','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3202047','Chief Minister''s Relief Fund','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202048','Odisha State Disaster Management Fund','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202049','Grant for Slaughter House','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202050','Grant received from Sewerage board (OWSSB fund)','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202051','Grant for City Development Plans','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202052','Compensation for Sitting fees, honorarium, TA & DA','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202053','Grant for Smart City Mission-State Grant','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202054','OULM-SEP(individual)','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202055','OULM-SEP(Group)','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202056','OULM-EST&P (training)','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3202057','OULM-Revolving Fund','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202058','OULM-Interest on Bank Account','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202059','Grant for Aahar','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202060','4th State Finance Commission-Creation of Capital Asset','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202061','4th State Finance Commission-Maintenance of Capital','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202062','Grant for Urban Infrastracture Initiative(UNNATI)','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202063','Compensation for Arrear Pension and Basic Services','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202064','Grant for Mini Stadium/Playground','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202065','BIJU YUVA VAHINI (BYV)','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202066','Nirman Shramik Pension Yojana','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3202067','Kalakar Sahayata Yojana','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202068','Critical Gap Fund','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202069','Grant in aid for Mobility Infrastructure','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202070','Debt Service coverage of OUIDF Loan Grant','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202071','Funds for Novel Corona Virus (COVID-19)','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202072','UNNATI (UWEI)','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3202073','JAGA Mission','',true,216,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3203001','Grant for Ornamental Street Light(OMC)','',true,217,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3203002','Grant for Street Light','',true,217,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3203003','WODC Grant','',true,217,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3203004','NALCO Grant','',true,217,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3203005','RLTAP Grant','',true,217,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3203006','Special Development Programme','',true,217,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3203007','District Innovation Fund','',true,217,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3203008','Pre-Matric Scholarship Grant','',true,217,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3203009','National Urban Health Mission(NUHM) Grant','',true,217,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3203010','Grant from Odisha Urban Infrastructure Development Fund(OUID','',true,217,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3203011','CSR Development Fund from Paradip Port Trust','',true,217,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3203012','Grant from Fishery Engineering Division � Fish Market','',true,217,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3205001','Grant from Bill & Melinda Gates Foundation','',true,219,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3206001','Grant From JICA','',true,220,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208001','JnNURM - BSUP - Housing - Bharatpur','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208002','JnNURM - BSUP - Housing - Dumduma','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208003','JnNURM - BSUP - Housing - Nayapalli','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208004','JnNURM - BSUP - Infra - Bharatpur','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208005','JnNURM - BSUP - Infra - Dumduma','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208006','JnNURM - BSUP - Infra - Nayapalli','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208007','JnNURM - BSUP - Interest on Bank Deposit','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208008','JnNURM - City Bus','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208009','JnNURM - City Bus - Interest on Bank Deposit','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3208010','SJSRY - USEP - Subsidy on Loan','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208011','SJSRY - UWSP - Revolving Fund','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208012','SJSRY - UWSP - Subsidy on Loan','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208013','SJSRY - Step Up - Training Programme','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208014','SJSRY - UWEP - Wages for Infra Dev','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208015','SJSRY - UCDN - Community Development','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208016','SJSRY - Infrastructure Support','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208017','SJSRY - Interest on Bank Deposit','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208018','NRHM - Grant','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208019','NRHM - Interest on Bank Deposit','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3208020','Super Cyclone Fund','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208021','Balika Samrudhi Yojana','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208022','Rajiv Awas Yojana','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208023','JnNURM - Project Implementation Unit (PIU)','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208024','JnNURM - PIU - Interest on Bank Deposit','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208025','Special Relief Commission (SRC) Grant','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208026','SRC - Interest on Bank Deposit','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208027','CDPO/Anganbadi Building Construction Grant','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208028','CDPO/Anganbadi - Interest on Bank Deposit','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208029','JnNURM - National Mission Mode Project (NMMP)','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3208030','JnNURM - Low Cost Sanitation Work','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208031','NULM - SM & ID','',true,221,NULL,'A','L',NULL,4,false,true,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208032','NULM - SEP(I) & (G)','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208033','NULM - EST & P','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208034','NULM - CB & T','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208035','NULM - SUH','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208036','NULM - SUSV','',true,221,NULL,'A','L',NULL,4,false,true,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208037','NULM - Interest on Bank Deposits','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208038','JnNURM - Challenge Fund','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208039','NULM-City Livelihood Center','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3208040','Median Plantation','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3208041','Installation of Plastic Waste Management','',true,221,NULL,'A','L',NULL,4,false,false,4,NULL,' ',NULL,' ','320',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3302001','Secured Loan From State Govt','',true,223,NULL,'A','L',NULL,4,false,false,5,NULL,' ',NULL,' ','330',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3305001','DRI Loan- (PNB RPRC)','',true,226,NULL,'A','L',NULL,4,false,false,5,NULL,' ',NULL,' ','330',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3311001','Unsecured Loan from Central Govt','',true,230,NULL,'A','L',NULL,4,false,false,6,NULL,' ',NULL,' ','331',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3312001','Unsecured Loan From State Govt','',true,231,NULL,'A','L',NULL,4,false,false,6,NULL,' ',NULL,' ','331',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3313001','Unsecured Loan from Government Bodies & Association','',true,232,NULL,'A','L',NULL,4,false,false,6,NULL,' ',NULL,' ','331',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3315001','Unsecured Loan from Banks & Other Financial Institutions','',true,234,NULL,'A','L',NULL,4,false,false,6,NULL,' ',NULL,' ','331',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3318001','Unsecured Other Loans','',true,237,NULL,'A','L',NULL,4,false,false,6,NULL,' ',NULL,' ','331',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3401001','Earnest Deposit - Municipal Fund','',true,238,NULL,'A','L',NULL,4,false,false,7,NULL,' ',NULL,' ','340',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3401002','Security Deposit - Municipal Fund','',true,238,NULL,'A','L',NULL,4,false,false,7,NULL,' ',NULL,' ','340',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3401003','Earnest Deposit - Special Contribution','',true,238,NULL,'A','L',NULL,4,false,false,7,NULL,' ',NULL,' ','340',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3401004','Security Deposit - Special Contribution','',true,238,NULL,'A','L',NULL,4,false,false,7,NULL,' ',NULL,' ','340',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3401005','Earnest Deposit - Special Fund','',true,238,NULL,'A','L',NULL,4,false,false,7,NULL,' ',NULL,' ','340',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3401006','Security Deposit - Special Fund','',true,238,NULL,'A','L',NULL,4,false,false,7,NULL,' ',NULL,' ','340',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3401007','Deposits Withheld - Contractors','',true,238,NULL,'A','L',NULL,4,false,false,7,NULL,' ',NULL,' ','340',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3401008','Additional Performance Security','',true,238,NULL,'A','L',NULL,4,false,false,7,NULL,' ',NULL,' ','340',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3401009','Initial Security Deposit','',true,238,NULL,'A','L',NULL,4,false,false,7,NULL,' ',NULL,' ','340',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3401010','Market Security Deposit','',true,238,NULL,'A','L',NULL,4,false,false,7,NULL,' ',NULL,' ','340',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3402001','Water Deposits','',true,239,NULL,'A','L',NULL,4,false,false,7,NULL,' ',NULL,' ','340',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3402002','Rent Deposits','',true,239,NULL,'A','L',NULL,4,false,false,7,NULL,' ',NULL,' ','340',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3402003','Caution Money - Kalyan Mandap and Meeting Halls','',true,239,NULL,'A','L',NULL,4,false,false,7,NULL,' ',NULL,' ','340',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3408001','Deposit Received from Scheme Beneficiary','',true,241,NULL,'A','L',NULL,4,false,false,7,NULL,' ',NULL,' ','340',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3411001','Deposit Works - MPLAD Fund','',true,242,61,'A','L',NULL,4,false,false,8,NULL,' ',NULL,' ','341',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3418001','Deposit Works - Others','',true,244,61,'A','L',NULL,4,false,false,8,NULL,' ',NULL,' ','341',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3501001','Suppliers Control Account','',true,245,27,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3501002','Contractors Control Account','',true,245,26,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3501003','Expenses Payable','',true,245,28,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3501004','Payables against special funds','',true,245,28,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3501005','Payables against specific grants','',true,245,28,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3501006','Payables against deposit works','',true,245,28,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3501007','Contractors Advance Control Account','',true,245,105,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3501008','Contractors Advance Control Account - Specific Contribution','',true,245,105,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3501009','Contractors Advance Control Account - Special Funds','',true,245,105,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3501010','Payables against Scheme Expenses','',true,245,28,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3501101','Salary Payable (staff and officers)','',true,246,31,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3501102','Wages Payable (labourers)','',true,246,36,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3501103','Unpaid salaries','',true,246,31,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3501104','Provident Fund Payable','',true,246,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3501105','Pension Liabilities','',true,246,34,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3501106','Welfare Funds Liability','',true,246,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3501107','Leave Salary payable','',true,246,31,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3501108','Revise Pay Arrear Payable','',true,246,31,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3501109','Pension Fund Contribution Payable','',true,246,34,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3501110','Pension Fund Contribution Payable and NPS','',true,246,34,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502001','Provident Fund Deductions','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502002','Insurance Premium Deductions','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502003','Deduction for other Organization/Societies','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502004','Service Tax Deductions/Recovery','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502005','Profession Tax Deduction','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3502006','TDS - Employees','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502007','Deduction for Works Contract Tax','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502008','Trade Tax Deduction','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502009','TDS - Contractors','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502010','TDS - Special Contribution','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502011','Works Contract Tax - Special Contribution','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502012','TDS - Special Funds','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502013','Works Contract Tax - Special Fund','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502014','TDS - Scheme Expenses','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502015','Other Deductions','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3502016','Recovery Payable - ORHDC','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502017','Recovery Payable - PGB','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502018','Recovery Payable - OCSB','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502019','Recovery Payable - SBI','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502020','Recovery Payable - KGB','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502021','Recovery Payable - Water Supply','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502022','Recovery Payable - MC BMPur','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502023','Construction Cess Payable','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502024','Royalty Payable','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502025','Provident Fund Deductions - Contractors','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3502026','GIS Recovery','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502027','Recovery Payable-OSFDC Loan','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502028','Recovery Payable-Vijaya Bank','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502029','Recovery Payable-Indian Bank','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502030','Recovery Payable-Quarter Rent','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502031','Recovery Payable - Sales Tax','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502032','Recovery Payable - CPF','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502033','Recovery Payable - LIC Premium','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502034','Recovery Payable - GPF','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502035','Recovery Payable - EPF','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3502036','Recovery Payable - Society Loan','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502037','Recovery Payable - UCO Bank','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502038','VAT - 4%','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502039','VAT - 16%','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502040','Recovery Payable - Union/Sangha Fees','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502041','Recovery Payable - Housing Loan','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502042','VAT - 5%','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502043','Recovery Payable - Allahabad Bank','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502044','Recovery Payable - Andhra Bank','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502045','Recovery payable - Urban Co.Operative Bank','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3502046','Recovery Payable - Cuttack Gramya Bank','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502047','Recovery Payable - Union Bank of India','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502048','Recovery Payable - Bank Loans','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502049','TDS - Professional','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502050','Solid Waste Management Fund','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502051','GST Payable','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502052','TCS from Auctioner','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502053','TDS under GST','',true,248,NULL,'A','L',NULL,4,false,true,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502054','Output CGST','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502055','Output SGST','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3502056','Output IGST','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3502057','Redcross Payable','',true,248,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3503001','Education Cess payable','',true,249,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3503002','Court Attachment fees payable','',true,249,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3503003','City Infrastructure Development Fund (CIDF)','',true,249,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3503004','Land Right Certificate Fees','',true,249,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3504001','Refunds Payable - Property and Other Taxes','',true,250,28,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3504002','Refunds Payable - Water supply related','',true,250,28,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3504003','Refunds Payable - Rent','',true,250,28,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3504004','RefundsPayable-Excess receipt against saleofattached propert','',true,250,28,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3504005','Refunds Payable - Other Income','',true,250,28,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3504006','Refunds Payable - Refunds of contribution liability','',true,250,28,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3504007','Refunds Payable - Deposit Civil Works','',true,250,28,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3504101','Advance Receipts - Property and Other Taxes','',true,251,28,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3504102','Advance Receipts - Water supply','',true,251,28,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3504103','Advance Receipts - Rent','',true,251,28,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3504104','Advance Receipts - License Fees','',true,251,28,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3504105','Advance Receipts - Advertisement Fees','',true,251,28,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3504106','Advance Receipts - Other Revenues','',true,251,28,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3508001','Stale Cheques','',true,252,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'3508002','Compensation Payable','',true,252,31,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3508003','Hire Purchase Payable','',true,252,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3508004','Interest Payable Control Account - Hire Purchase','',true,252,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3509001','Sale Proceeds - Assets','',true,253,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3509002','Sale Proceeds - Investments','',true,253,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3509003','Sale Proceeds - Stores','',true,253,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3509004','Sale Proceeds - Attached properties','',true,253,NULL,'A','L',NULL,4,false,false,9,NULL,' ',NULL,' ','350',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3601001','Provision for Expense','',true,254,NULL,'A','L',NULL,4,false,false,10,NULL,' ',NULL,' ','360',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3603001','Provision for Stores','',true,256,NULL,'A','L',NULL,4,false,false,10,NULL,' ',NULL,' ','360',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'3603002','Provision for Investments','',true,256,NULL,'A','L',NULL,4,false,false,10,NULL,' ',NULL,' ','360',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'4101001','Land','',true,257,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4101002','Grounds','',true,257,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4101003','Parks and Gardens','',true,257,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4101004','Stadiums','',true,257,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4101005','Artificial Fountain','',true,257,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4102001','Office Buildings','',true,258,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4102002','Community Building','',true,258,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4102003','Market Building','',true,258,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4102004','Hospital Building','',true,258,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4102005','Boundary/Compound Walls','',true,258,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'4102006','Slaughter House','',true,258,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4102007','Kalyan Mandap','',true,258,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4102008','Public/Community/Hybrid Toilet','',true,258,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4102009','Bus Stand','',true,258,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4102010','Other Buildings','',true,258,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4102011','Night Shelter & Yatri Nivas','',true,258,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4102012','Vending Zone','',true,258,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4102013','Sports Arena','',true,258,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4103001','Concrete Roads','',true,259,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4103002','Metalled Roads (Bitumen)','',true,259,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'4103003','Other Roads','',true,259,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4103004','Bridges and Flyovers','',true,259,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4103005','Culverts','',true,259,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4103101','Underground Drains','',true,260,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4103102','Open Drains','',true,260,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4103201','Bore well','',true,261,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4103202','Open Wells','',true,261,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4103203','Water Reservoirs','',true,261,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4103204','Water Ways','',true,261,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4103205','Lakes & Ponds','',true,261,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'4103206','Stand Post','',true,261,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4103207','Water ATM','',true,261,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4103301','Lamp posts','',true,262,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4103302','Transformer','',true,262,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4103303','Public Lighting System','',true,262,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4104001','Pump Sets','',true,263,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4104002','Fogging Machine (Mosquito Control)','',true,263,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4104003','Plant and Machinery','',true,263,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4104004','DG Sets','',true,263,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4104005','Inverter','',true,263,NULL,'A','A',NULL,4,false,true,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'4104006','MCCs & MRFs (SWM Unit)','',true,263,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4104007','Solar System','',true,263,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4105001','Motor Car','',true,264,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4105002','Jeep','',true,264,NULL,'A','A',NULL,4,false,true,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4105003','Bus','',true,264,NULL,'A','A',NULL,4,false,true,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4105004','Trucks','',true,264,NULL,'A','A',NULL,4,false,true,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4105005','Tankers','',true,264,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4105006','Cranes','',true,264,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4105007','Ambulances','',true,264,NULL,'A','A',NULL,4,false,true,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4105008','Fire Tenders','',true,264,NULL,'A','A',NULL,4,false,true,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'4105009','Vehicles','',true,264,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4105010','Cow catcher vehicle','',true,264,NULL,'A','A',NULL,4,false,true,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4106001','Air Conditioners','',true,265,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4106002','Computers','',true,265,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4106003','Faxes','',true,265,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4106004','Photo-copiers','',true,265,NULL,'A','A',NULL,4,false,true,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4106005','Refrigerators','',true,265,NULL,'A','A',NULL,4,false,true,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4106006','Water Coolers','',true,265,NULL,'A','A',NULL,4,false,true,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4106007','EPABX System','',true,265,NULL,'A','A',NULL,4,false,true,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4106008','Office & Other Equipments','',true,265,NULL,'A','A',NULL,4,false,true,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'4106009','LAN/WAN','',true,265,NULL,'A','A',NULL,4,false,true,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4106010','CCTV Camera','',true,265,NULL,'A','A',NULL,4,false,true,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4107001','Chairs','',true,266,NULL,'A','A',NULL,4,false,true,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4107002','Tables','',true,266,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4107003','Almirah','',true,266,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4107004','Cupboards','',true,266,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4107005','Fans','',true,266,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4107006','Electrical Fittings','',true,266,NULL,'A','A',NULL,4,false,true,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4107007','Furniture and Fixtures','',true,266,NULL,'A','A',NULL,4,false,true,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4108001','Crematorium','',true,267,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'4108002','Other Fixed Assets','',true,267,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4108003','Wheelbarrow','',true,267,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4108004','Gymnasium Equipment','',true,267,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4108005','Temporary Shed','',true,267,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4108006','Dustbins','',true,267,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4108007','Mobile Toilet','',true,267,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4108008','Tricycle/ Rikshaw','',true,267,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4109001','Assets Under Disposal','',true,268,NULL,'A','A',NULL,4,false,false,11,NULL,' ',NULL,' ','410',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4112001','Accumulated Depreciation - Buildings','',true,269,NULL,'A','A',NULL,4,false,true,12,NULL,' ',NULL,' ','411',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4113001','Accumulated Depreciation - Roads and Bridges','',true,270,NULL,'A','A',NULL,4,false,false,12,NULL,' ',NULL,' ','411',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'4113101','Accumulated Depreciation - Sewerage and Drainage','',true,271,NULL,'A','A',NULL,4,false,false,12,NULL,' ',NULL,' ','411',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4113201','Accumulated Depreciation - Waterways','',true,272,NULL,'A','A',NULL,4,false,false,12,NULL,' ',NULL,' ','411',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4113301','Accumulated Depreciation - Public Lighting','',true,273,NULL,'A','A',NULL,4,false,false,12,NULL,' ',NULL,' ','411',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4114001','Accumulated Depreciation - Plant and Machinery','',true,274,NULL,'A','A',NULL,4,false,false,12,NULL,' ',NULL,' ','411',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4115001','Accumulated Depreciation - Vehicles','',true,275,NULL,'A','A',NULL,4,false,false,12,NULL,' ',NULL,' ','411',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4116001','Accumulated Depreciation - Office and Other Equipments','',true,276,NULL,'A','A',NULL,4,false,false,12,NULL,' ',NULL,' ','411',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4117001','AccumulatedDepreciation-Furniture,Fixture,Fittings and Elect','',true,277,NULL,'A','A',NULL,4,false,false,12,NULL,' ',NULL,' ','411',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4118001','Accumulated Depreciation - Other Fixed Assets','',true,278,NULL,'A','A',NULL,4,false,false,12,NULL,' ',NULL,' ','411',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4124001','CWIP - Buildings','',true,282,16,'A','A',NULL,4,false,false,13,NULL,' ',NULL,' ','412',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4124002','CWIP - Roads & Bridges','',true,282,16,'A','A',NULL,4,false,false,13,NULL,' ',NULL,' ','412',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'4124003','CWIP - Sewerage & Drainage','',true,282,16,'A','A',NULL,4,false,false,13,NULL,' ',NULL,' ','412',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4124004','CWIP - Others','',true,282,16,'A','A',NULL,4,false,false,13,NULL,' ',NULL,' ','412',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4205001','Equity Shares of BPTSL','',true,287,NULL,'A','A',NULL,4,false,false,14,NULL,' ',NULL,' ','420',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4208001','Other Investments','',true,289,NULL,'A','A',NULL,4,false,false,14,NULL,' ',NULL,' ','420',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4301001','Stock-in-Hand','',true,299,NULL,'A','A',NULL,4,false,false,16,NULL,' ',NULL,' ','430',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4301002','AAY/BPL/APL - Rice/Wheat/Sugar/Kerosene','',true,299,NULL,'A','A',NULL,4,false,false,16,NULL,' ',NULL,' ','430',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4301003','Tube Well Spares','',true,299,NULL,'A','A',NULL,4,false,false,16,NULL,' ',NULL,' ','430',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4311001','Property Tax Receivable - Current Year','',true,302,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4311002','Property Tax Receivable - Year 1','',true,302,NULL,'A','A',NULL,4,false,true,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4311003','Property Tax Receivable - Year 2','',true,302,NULL,'A','A',NULL,4,false,true,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'4311004','Property Tax Receivable - Year 3','',true,302,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4311005','Property Tax Receivable - Year 4','',true,302,NULL,'A','A',NULL,4,false,true,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4311006','Property Tax Receivable - Year 5','',true,302,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4311007','Property Tax Receivable - Others','',true,302,NULL,'A','A',NULL,4,false,true,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4311101','Conservancy/Latrine tax Receivable-Current Year','',true,303,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4311102','Conservancy/Latrine tax Receivable- Year-1','',true,303,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4311201','Light Tax Receivable- Current Year','',true,304,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4311202','Light Tax Receivable- Year-1','',true,304,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4311301','Water Tax Receivable- Current Year','',true,305,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4311302','Water Tax Receivable- Year-1','',true,305,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'4311401','Sewerage/Drainage Tax Receivable- Current Year','',true,306,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4311402','Sewerage/Drainage Tax Receivable- Year-1','',true,306,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4311901','Other Tax Receivable - Current year','',true,307,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4311902','Other Tax Receivable - Year 1','',true,307,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4311903','Other Tax Receivable - Year 2','',true,307,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4311904','Other Tax Receivable - Year 3','',true,307,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4311905','Other Tax Receivable - Others','',true,307,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4313001','Water Supply Receivable - Current Year','',true,308,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4313002','Water Supply Receivable - Year 1','',true,308,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4313003','Water Supply Receivable - Year 2','',true,308,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'4313004','Water Supply Receivable - Year 3','',true,308,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4313005','Water Supply Receivable - Others','',true,308,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4313006','License Fees Receivable - Current Year','',true,308,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4313007','License Fees Receivable - Year 1','',true,308,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4313008','License Fees Receivable - Year 2','',true,308,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4313009','License Fees Receivable - Year 3','',true,308,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4313010','License Fees Receivable - Others','',true,308,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4313011','Advertisement Fees Receivable - Current Year','',true,308,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4313012','Advertisement Fees Receivable - Year 1','',true,308,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4313013','Advertisement Fees Receivable - Year 2','',true,308,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'4313014','Advertisement Fees Receivable - Year 3','',true,308,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4313015','Advertisement Fees Receivable - Others','',true,308,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4313016','Market Toll Receivable','',true,308,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4314001','Rent Receivable - Current Year','',true,309,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4314002','Rent Receivable - Year 1','',true,309,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4314003','Rent Receivable - Year 2','',true,309,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4314004','Rent Receivable - Year 3','',true,309,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4314005','Rent Receivable - Others','',true,309,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4314006','Lease Rentals','',true,309,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4314007','Interest Earned','',true,309,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'4314008','Interest Accrued but not due - Municipal Fund','',true,309,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4314009','Interest Accrued but not due - Specific Contribution','',true,309,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4314010','Interest Accrued but not due - Special Funds','',true,309,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4314011','Interest from Employees','',true,309,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4315001','Receivable from Governmet - Grants','',true,310,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4315002','Receivable from Government - Assigned Revenue','',true,310,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4318001','Receivables Control Account - Property Tax','',true,311,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4318002','Receivables Control Account - Water Supply','',true,311,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4318003','Receivables Control Account - Rent','',true,311,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4318004','Receivables Control Account - License Fees','',true,311,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'4318005','Receivables Control Account - Advertisement Fees','',true,311,NULL,'A','A',NULL,4,false,false,17,NULL,' ',NULL,' ','431',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4321001','Accumulated Provision for outstanding Property Taxes','',true,312,NULL,'A','A',NULL,4,false,false,18,NULL,' ',NULL,' ','432',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4321101','Accumulated Provision for Outstanding Water Taxes','',true,313,NULL,'A','A',NULL,4,false,false,18,NULL,' ',NULL,' ','432',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4321201','Accumulated Provision for outstanding Other Taxes','',true,314,NULL,'A','A',NULL,4,false,false,18,NULL,' ',NULL,' ','432',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4323001','Accumulated Provision for outstanding Fees and User Charges','',true,315,NULL,'A','A',NULL,4,false,false,18,NULL,' ',NULL,' ','432',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4324001','Accumulated Provision for outstanding other receivable -Rent','',true,316,NULL,'A','A',NULL,4,false,false,18,NULL,' ',NULL,' ','432',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4324002','AccumulatedProvision for outstanding other receivable-Licens','',true,316,NULL,'A','A',NULL,4,false,false,18,NULL,' ',NULL,' ','432',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4324003','Accumulated Provision for outstanding other receivable - Adv','',true,316,NULL,'A','A',NULL,4,false,false,18,NULL,' ',NULL,' ','432',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4401001','Prepaid Expense - Establishment','',true,317,NULL,'A','A',NULL,4,false,false,19,NULL,' ',NULL,' ','440',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4402001','Prepaid Expense - Administration','',true,318,NULL,'A','A',NULL,4,false,false,19,NULL,' ',NULL,' ','440',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'4403001','Prepaid Expense - Operations and Maintenance','',true,319,NULL,'A','A',NULL,4,false,false,19,NULL,' ',NULL,' ','440',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4501001','Cash in hand (head office)','',true,320,4,'A','A',NULL,4,false,false,20,NULL,' ',NULL,' ','450',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4501002','Cash in hand (Zone Offices)','',true,320,4,'A','A',NULL,4,false,false,20,NULL,' ',NULL,' ','450',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4501003','Cash in hand - Hospital','',true,320,4,'A','A',NULL,4,false,false,20,NULL,' ',NULL,' ','450',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4501004','Cash in Hand - Pension Section','',true,320,4,'A','A',NULL,4,false,false,20,NULL,' ',NULL,' ','450',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4601001','Loans and advances to Employees - House Building Advance','',true,324,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4601002','Loans and advances to Employees - Vehicle Purchase Advance','',true,324,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4601003','Loans and advances to Employees - Computer Purchase Advance','',true,324,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4601004','Loans and advances to Employees - Festival Advance','',true,324,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4601005','Loans and advances to Employees - Food/ration Advance','',true,324,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'4601006','Loans and advances to Employees - Miscellaneous Advances','',true,324,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4601007','Loans and advances to Employees - Medical Advance','',true,324,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4601008','Loans and advances to Employees - Travel Advance','',true,324,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4601009','Loans and advances to Employees - Central Pay Advance','',true,324,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4601010','Advance to JE','',true,324,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4601011','Advance to Store Keeper','',true,324,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4601012','Advance to Sanitary Inspector','',true,324,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4601013','DA Advance','',true,324,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4601014','Loans and advances to Employees - Salary Advance','',true,324,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4602001','Loans and advance to Employees-Employee Provident Fund Loans','',true,325,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'4602002','GPF Advance','',true,325,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4603001','Loans and Advance to Others','',true,326,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4604001','Advance to Suppliers and Contractors - Public Works/Assets','',true,327,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4604002','Advance to Suppliers and Contractors-Stores/Materials supply','',true,327,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4604003','Advance to SuppliersandContractors-MaterialAdvances to contrac','',true,327,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4604004','Advance to Suppliers and Contractors - Specific Grants','',true,327,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4604005','Advance to Suppliers and Contractors - Special Funds','',true,327,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4604006','Advance to Suppliers and Contractors - Others','',true,327,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4605001','Advance to Others - Permanent Advances','',true,328,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4605002','Advance to Others - Advance against Grants','',true,328,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'4605003','Advance to Others - Advance against Schemes','',true,328,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4605004','Advance to E.E. PH Div','',true,328,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4605005','Advance for DPR Preparation','',true,328,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4605006','Advance for Health Camp','',true,328,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4606001','Electricity Deposits','',true,329,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4606002','Telephone Deposits','',true,329,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4606003','Other Deposits','',true,329,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4606004','Internet Deposits','',true,329,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4608001','Interest Receivable on Loans and Advances','',true,330,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4608002','Hire Purchase Installments','',true,330,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'4608003','Scheme Expenses','',true,330,NULL,'A','A',NULL,4,false,false,21,NULL,' ',NULL,' ','460',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4611001','Accumulated Provisions on Loans to Others','',true,331,NULL,'A','A',NULL,4,false,false,22,NULL,' ',NULL,' ','461',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4612001','Accumulated Provisions on Advances to Others','',true,332,NULL,'A','A',NULL,4,false,false,22,NULL,' ',NULL,' ','461',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4613001','Accumulated Provisions on Deposits with Others','',true,333,NULL,'A','A',NULL,4,false,false,22,NULL,' ',NULL,' ','461',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4701001','Deposit - Works Expenditure - Civil','',true,334,NULL,'A','A',NULL,4,false,false,23,NULL,' ',NULL,' ','470',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4701002','Deposit - Works Expenditure - Electrical','',true,334,NULL,'A','A',NULL,4,false,false,23,NULL,' ',NULL,' ','470',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4701003','Deposit - Works Expenditure - Others','',true,334,NULL,'A','A',NULL,4,false,false,23,NULL,' ',NULL,' ','470',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4703001','Interest Receivable - Hire Purchase','',true,335,NULL,'A','A',NULL,4,false,false,23,NULL,' ',NULL,' ','470',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4704001','Bank Clearing Account','',true,336,NULL,'A','A',NULL,4,false,false,23,NULL,' ',NULL,' ','470',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4705001','TDS Receivable','',true,337,NULL,'A','A',NULL,4,false,false,23,NULL,' ',NULL,' ','470',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_chartofaccounts'),'4705002','GST Receivable','',true,337,NULL,'A','A',NULL,4,false,false,23,NULL,' ',NULL,' ','470',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4705003','Input CGST','',true,337,NULL,'A','A',NULL,4,false,false,23,NULL,' ',NULL,' ','470',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4705004','Input SGST','',true,337,NULL,'A','A',NULL,4,false,false,23,NULL,' ',NULL,' ','470',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4705005','Input IGST','',true,337,NULL,'A','A',NULL,4,false,false,23,NULL,' ',NULL,' ','470',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0),
	 (nextval('seq_chartofaccounts'),'4712001','Computer Software','',true,338,NULL,'A','A',NULL,4,false,false,24,NULL,' ',NULL,' ','471',NULL,'','2021-04-01 00:00:00',1,now(),NULL,0);
	 
--Insert begin in eg_bill_subtype 

INSERT INTO eg_bill_subtype (id,"name",expenditure_type,"version") VALUES
	 (nextval('seq_eg_bill_subtype'),'Works Civil','Expense',0),
	 (nextval('seq_eg_bill_subtype'),'Works Electrical','Expense',0),
	 (nextval('seq_eg_bill_subtype'),'Works Mechanical','Expense',0),
	 (nextval('seq_eg_bill_subtype'),'Stores','Expense',0),
	 (nextval('seq_eg_bill_subtype'),' Telephone/Internet Bill','Expense',0),
	 (nextval('seq_eg_bill_subtype'),'Celebration/Programme expenses','Expense',0),
	 (nextval('seq_eg_bill_subtype'),' Electricity','Expense',0),
	 (nextval('seq_eg_bill_subtype'),' Advance Adjustment','Expense',0),
	 (nextval('seq_eg_bill_subtype'),' Pension/Gratuity','Expense',0),
	 (nextval('seq_eg_bill_subtype'),' Office Contingency','Expense',0);
INSERT INTO eg_bill_subtype (id,"name",expenditure_type,"version") VALUES
	 (nextval('seq_eg_bill_subtype'),' Refund of Deposits (SD/ISD/EMD etc)','Expense',0),
	 (nextval('seq_eg_bill_subtype'),' Petrol/Diesel','Expense',0),
	 (nextval('seq_eg_bill_subtype'),'Consultancy Fee','Expense',0),
	 (nextval('seq_eg_bill_subtype'),' Advertisement bill','Expense',0),
	 (nextval('seq_eg_bill_subtype'),' Refund of booking amount(Kalyan Mandap, Community Centre etc)','Expense',0),
	 (nextval('seq_eg_bill_subtype'),' Refund of property tax amount','Expense',0),
	 (nextval('seq_eg_bill_subtype'),' Salary bill (Regular employees)','Expense',0),
	 (nextval('seq_eg_bill_subtype'),' Salary bill (DLR/NMR/CLR/work charge)','Expense',0),
	 (nextval('seq_eg_bill_subtype'),' Salary bill (Contractual employee)','Expense',0),
	 (nextval('seq_eg_bill_subtype'),' Wages bill (Outsource)','Expense',0);
INSERT INTO eg_bill_subtype (id,"name",expenditure_type,"version") VALUES
	 (nextval('seq_eg_bill_subtype'),'Pension Bill','Expense',0),
	 (nextval('seq_eg_bill_subtype'),' TA bill','Expense',0),
	 (nextval('seq_eg_bill_subtype'),' LTC bill','Expense',0),
	 (nextval('seq_eg_bill_subtype'),'Advance  bill','Expense',0),
	 (nextval('seq_eg_bill_subtype'),' Adjustment of Advance','Expense',0),
	 (nextval('seq_eg_bill_subtype'),' Medical Reimbursement  bill','Expense',0),
	 (nextval('seq_eg_bill_subtype'),' Gratuity  bill','Expense',0),
	 (nextval('seq_eg_bill_subtype'),' Leave encashment  bill','Expense',0),
	 (nextval('seq_eg_bill_subtype'),' Recovery','Expense',0),
	 (nextval('seq_eg_bill_subtype'),' Others','Expense',0);
INSERT INTO eg_bill_subtype (id,"name",expenditure_type,"version") VALUES
	 (nextval('seq_eg_bill_subtype'),'Hire Charges Bill','Expense',0),
	 (nextval('seq_eg_bill_subtype'),'Insurance Charges Bill','Expense',0),
	 (nextval('seq_eg_bill_subtype'),'Audit Fee Bill','Expense',0),
	 (nextval('seq_eg_bill_subtype'),'Legal Fee','Expense',0),
	 (nextval('seq_eg_bill_subtype'),'Election/Census Bill','Expense',0),
	 (nextval('seq_eg_bill_subtype'),'Rehabilitation to/Relief of Slum Dwellers','Expense',0),
	 (nextval('seq_eg_bill_subtype'),'Programme Expenses','Expense',0),
	 (nextval('seq_eg_bill_subtype'),'Trainning Expenses','Expense',0),
	 (nextval('seq_eg_bill_subtype'),'Sanitation Bill','Expense',0),
	 (nextval('seq_eg_bill_subtype'),'Annual Maintenance Charges (AMC)','Expense',0);
INSERT INTO eg_bill_subtype (id,"name",expenditure_type,"version") VALUES
	 (nextval('seq_eg_bill_subtype'),'Security Expenses','Expense',0);

--Insert begin in eg_department 

INSERT INTO eg_department (id,"name",createddate,code,createdby,lastmodifiedby,lastmodifieddate,"version") VALUES
	 (nextval('seq_eg_department'),'Accounts Branch','2021-04-01 00:00:00','DEPT_25',NULL,NULL,'2021-04-01 00:00:00',0),
	 (nextval('seq_eg_department'),'Audit Branch','2021-04-01 00:00:00','DEPT_52',NULL,NULL,'2021-04-01 00:00:00',0),
	 (nextval('seq_eg_department'),'Engineering','2021-04-01 00:00:00','DEPT_53',NULL,NULL,'2021-04-01 00:00:00',0),
	 (nextval('seq_eg_department'),'Establishment','2021-04-01 00:00:00','DEPT_54',NULL,NULL,'2021-04-01 00:00:00',0),
	 (nextval('seq_eg_department'),'Disaster Management & Emergency','2021-04-01 00:00:00','DEPT_55',NULL,NULL,'2021-04-01 00:00:00',0),
	 (nextval('seq_eg_department'),'Health & Sanitation','2021-04-01 00:00:00','DEPT_3',NULL,NULL,'2021-04-01 00:00:00',0),
	 (nextval('seq_eg_department'),'Revenue','2021-04-01 00:00:00','DEPT_56',NULL,NULL,'2021-04-01 00:00:00',0),
	 (nextval('seq_eg_department'),'Corporation/Council','2021-04-01 00:00:00','DEPT_57',NULL,NULL,'2021-04-01 00:00:00',0),
	 (nextval('seq_eg_department'),'Election/Census','2021-04-01 00:00:00','DEPT_58',NULL,NULL,'2021-04-01 00:00:00',0),
	 (nextval('seq_eg_department'),'Forest & Environment','2021-04-01 00:00:00','DEPT_59',NULL,NULL,'2021-04-01 00:00:00',0);
INSERT INTO eg_department (id,"name",createddate,code,createdby,lastmodifiedby,lastmodifieddate,"version") VALUES
	 (nextval('seq_eg_department'),'PMU','2021-04-01 00:00:00','DEPT_60',NULL,NULL,'2021-04-01 00:00:00',0),
	 (nextval('seq_eg_department'),'Transport','2021-04-01 00:00:00','DEPT_61',NULL,NULL,'2021-04-01 00:00:00',0),
	 (nextval('seq_eg_department'),'Land ','2021-04-01 00:00:00','DEPT_62',NULL,NULL,'2021-04-01 00:00:00',0),
	 (nextval('seq_eg_department'),'Municipal Planning','2021-04-01 00:00:00','DEPT_63',NULL,NULL,'2021-04-01 00:00:00',0),
	 (nextval('seq_eg_department'),'Social Welfare','2021-04-01 00:00:00','DEPT_64',NULL,NULL,'2021-04-01 00:00:00',0),
	 (nextval('seq_eg_department'),'Vending zone','2021-04-01 00:00:00','DEPT_65',NULL,NULL,'2021-04-01 00:00:00',0),
	 (nextval('seq_eg_department'),'Enforcement','2021-04-01 00:00:00','DEPT_66',NULL,NULL,'2021-04-01 00:00:00',0),
	 (nextval('seq_eg_department'),'Slum Development & Housing','2021-04-01 00:00:00','DEPT_67',NULL,NULL,'2021-04-01 00:00:00',0),
	 (nextval('seq_eg_department'),'Legal','2021-04-01 00:00:00','DEPT_68',NULL,NULL,'2021-04-01 00:00:00',0),
	 (nextval('seq_eg_department'),'ULB Asset','2021-04-01 00:00:00','DEPT_69',NULL,NULL,'2021-04-01 00:00:00',0);
INSERT INTO eg_department (id,"name",createddate,code,createdby,lastmodifiedby,lastmodifieddate,"version") VALUES
	 (nextval('seq_eg_department'),'Stores','2021-04-01 00:00:00','DEPT_70',NULL,NULL,'2021-04-01 00:00:00',0);
INSERT INTO eg_department (id,"name",createddate,code,createdby,lastmodifiedby,lastmodifieddate,"version") VALUES
	 (nextval('seq_eg_department'),'Health','2021-04-01 00:00:00','DEPT_71',NULL,NULL,'2021-04-01 00:00:00',0);
INSERT INTO eg_department (id,"name",createddate,code,createdby,lastmodifiedby,lastmodifieddate,"version") VALUES
	 (nextval('seq_eg_department'),'Sanitation','2021-04-01 00:00:00','DEPT_72',NULL,NULL,'2021-04-01 00:00:00',0);
INSERT INTO eg_department (id,"name",createddate,code,createdby,lastmodifiedby,lastmodifieddate,"version") VALUES
	 (nextval('seq_eg_department'),'Trade License','2021-04-01 00:00:00','DEPT_73',NULL,NULL,'2021-04-01 00:00:00',0);
INSERT INTO eg_department (id,"name",createddate,code,createdby,lastmodifiedby,lastmodifieddate,"version") VALUES
	 (nextval('seq_eg_department'),'Electrical','2021-04-01 00:00:00','DEPT_74',NULL,NULL,'2021-04-01 00:00:00',0);
INSERT INTO eg_department (id,"name",createddate,code,createdby,lastmodifiedby,lastmodifieddate,"version") VALUES
	 (nextval('seq_eg_department'),'Housing','2021-04-01 00:00:00','DEPT_75',NULL,NULL,'2021-04-01 00:00:00',0);
INSERT INTO eg_department (id,"name",createddate,code,createdby,lastmodifiedby,lastmodifieddate,"version") VALUES
	 (nextval('seq_eg_department'),'Marriage','2021-04-01 00:00:00','DEPT_76',NULL,NULL,'2021-04-01 00:00:00',0);
INSERT INTO eg_department (id,"name",createddate,code,createdby,lastmodifiedby,lastmodifieddate,"version") VALUES
	 (nextval('seq_eg_department'),'Grievance','2021-04-01 00:00:00','DEPT_77',NULL,NULL,'2021-04-01 00:00:00',0);
INSERT INTO eg_department (id,"name",createddate,code,createdby,lastmodifiedby,lastmodifieddate,"version") VALUES
	 (nextval('seq_eg_department'),'Holding Tax','2021-04-01 00:00:00','DEPT_78',NULL,NULL,'2021-04-01 00:00:00',0);
INSERT INTO eg_department (id,"name",createddate,code,createdby,lastmodifiedby,lastmodifieddate,"version") VALUES
	 (nextval('seq_eg_department'),'PHEO','2021-04-01 00:00:00','DEPT_79',NULL,NULL,'2021-04-01 00:00:00',0);
INSERT INTO eg_department (id,"name",createddate,code,createdby,lastmodifiedby,lastmodifieddate,"version") VALUES
	 (nextval('seq_eg_department'),'WATCO','2021-04-01 00:00:00','DEPT_80',NULL,NULL,'2021-04-01 00:00:00',0);	 

--Insert begin in eg_designation 	 
	 
INSERT INTO eg_designation (id,"name",description,chartofaccounts,"version",createddate,lastmodifieddate,createdby,lastmodifiedby,code) VALUES
	 (nextval('seq_eg_designation'),'Accounatnt','Accounatnt',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_58'),
	 (nextval('seq_eg_designation'),'Accounts/Finance Officer','Accounts/Finance Officer',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_207'),
	 (nextval('seq_eg_designation'),'Additional Commissioner','Additional Commissioner',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_208'),
	 (nextval('seq_eg_designation'),'Administrator','Administrator',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_209'),
	 (nextval('seq_eg_designation'),'Assistant Engineer','Assistant Engineer',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_03'),
	 (nextval('seq_eg_designation'),'Assistant Executive Engineer','Assistant Executive Engineer',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_213'),
	 (nextval('seq_eg_designation'),'Auditor','Auditor',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_212'),
	 (nextval('seq_eg_designation'),'Bill Clerk/DEO','Bill Clerk/DEO',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_211'),
	 (nextval('seq_eg_designation'),'Cashier','Cashier',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_214'),
	 (nextval('seq_eg_designation'),'Chairman','Chairman',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_215');
INSERT INTO eg_designation (id,"name",description,chartofaccounts,"version",createddate,lastmodifieddate,createdby,lastmodifiedby,code) VALUES
	 (nextval('seq_eg_designation'),'Chief Auditor','Chief Auditor',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_216'),
	 (nextval('seq_eg_designation'),'City Engineer','City Engineer',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_217'),
	 (nextval('seq_eg_designation'),'City Health officer','City Health officer',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_13'),
	 (nextval('seq_eg_designation'),'Commissioner','Commissioner',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_218'),
	 (nextval('seq_eg_designation'),'Deputy Commissioner','Deputy Commissioner',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_219'),
	 (nextval('seq_eg_designation'),'Environment Officer','Environment Officer',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_220'),
	 (nextval('seq_eg_designation'),'Executive Engineer','Executive Engineer',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_222'),
	 (nextval('seq_eg_designation'),'Executive Officer','Executive Officer',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_210'),
	 (nextval('seq_eg_designation'),'Head Assistant','Head Assistant',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_221'),
	 (nextval('seq_eg_designation'),'Junior Assistant','Junior Assistant',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_22');
INSERT INTO eg_designation (id,"name",description,chartofaccounts,"version",createddate,lastmodifieddate,createdby,lastmodifiedby,code) VALUES
	 (nextval('seq_eg_designation'),'Junior Engineer','Junior Engineer',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_04'),
	 (nextval('seq_eg_designation'),'License Inspector','License Inspector',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_223'),
	 (nextval('seq_eg_designation'),'Mayor','Mayor',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_224'),
	 (nextval('seq_eg_designation'),'Senior Assistant','Senior Assistant',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_225'),
	 (nextval('seq_eg_designation'),'Tax Collector','Tax Collector',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_226'),
	 (nextval('seq_eg_designation'),'Municipal Engineer','Municipal Engineer',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_227'),
	 (nextval('seq_eg_designation'),'Nodal Officer','Nodal Officer',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_228'),
	 (nextval('seq_eg_designation'),'ULB Officer','ULB Officer',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_229'),
	 (nextval('seq_eg_designation'),'Collector','Collector',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_230'),
	 (nextval('seq_eg_designation'),'District Magistrate','District Magistrate',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_231'),
	 (nextval('seq_eg_designation'),'H&UDD Official','H&UDD Official',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_232'),
	 (nextval('seq_eg_designation'),'PHEO Official','PHEO Official',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_233'),
	 (nextval('seq_eg_designation'),'Project Director','Project Director',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_234'),
	 (nextval('seq_eg_designation'),'WATCO Official','WATCO Official',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,'DESIG_235');

--Insert begin in function
	 
INSERT INTO function (id,code,"name","type",llevel,parentid,isactive,isnotleaf,parentcode,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_function'),'202101','Civil Works','',0,NULL,true,false,'',NULL,NULL,NULL,NULL,0),
	 (nextval('seq_function'),'202401','Elctrical Works','',0,NULL,true,false,'',NULL,NULL,NULL,NULL,0),
	 (nextval('seq_function'),'404101','Mechanical works','',0,NULL,true,false,'',NULL,NULL,NULL,NULL,0),
	 (nextval('seq_function'),'300','Finance Accounts Audit','',0,NULL,true,false,'',NULL,NULL,NULL,NULL,0),
	 (nextval('seq_function'),'303207','Sanitation','',0,NULL,true,false,'',NULL,NULL,NULL,NULL,0),
	 (nextval('seq_function'),'505800','Municipal Planning','',0,NULL,true,false,'',NULL,NULL,NULL,NULL,0),
	 (nextval('seq_function'),'201','Information technology','',0,NULL,true,false,'',NULL,NULL,NULL,NULL,0),
	 (nextval('seq_function'),'2','Administrative','',0,NULL,true,false,'',NULL,NULL,NULL,NULL,0),
	 (nextval('seq_function'),'44010','Establishment','',0,NULL,true,false,'',NULL,NULL,NULL,NULL,0),
	 (nextval('seq_function'),'25010','Census & Election','',0,NULL,true,false,'',NULL,NULL,NULL,NULL,0);
INSERT INTO function (id,code,"name","type",llevel,parentid,isactive,isnotleaf,parentcode,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextval('seq_function'),'90','Revenue','',0,NULL,true,false,'',NULL,NULL,NULL,NULL,0),
	 (nextval('seq_function'),'303700','Birth & Death','',0,NULL,true,false,'',NULL,NULL,NULL,NULL,0),
	 (nextval('seq_function'),'303300','Social Welfare','',0,NULL,true,false,'',NULL,NULL,NULL,NULL,0),
	 (nextval('seq_function'),'6061','Urban Housing','',0,NULL,true,false,'',NULL,NULL,NULL,NULL,0),
	 (nextval('seq_function'),'204','Legal','',0,NULL,true,false,'',NULL,NULL,NULL,NULL,0),
	 (nextval('seq_function'),'101400','Enforcement','',0,NULL,true,false,'',NULL,NULL,NULL,NULL,0);

--Insert begin in fund

INSERT INTO fund (id,code,"name",llevel,parentid,isactive,isnotleaf,identifier,purpose_id,transactioncreditamount,createddate,lastmodifieddate,lastmodifiedby,createdby,"version") VALUES
	 (nextval('seq_fund'),'01','General Fund',0,NULL,true,false,'1',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,0),
	 (nextval('seq_fund'),'02','Urban Poor Welfare Fund',0,NULL,true,false,'2',NULL,NULL,'2021-04-01 00:00:00',NULL,NULL,NULL,0);

--Insert begin in scheme
INSERT INTO scheme (id,code,"name",validfrom,validto,isactive,description,fundid,sectorid,aaes,fieldid,createddate,lastmodifieddate,createdby,lastmodifiedby) VALUES
	 (nextval('seq_scheme'),'3201001','Grants from Central Govt','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grants from Central Govt',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3201002','13th Finance Commission Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'13th Finance Commission Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3201003','Grant for Development of Bindusagar Lake','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant for Development of Bindusagar Lake',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3201004','12th Finance Commission Grant - Roads & Bridges','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'12th Finance Commission Grant - Roads & Bridges',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3201005','Grant - Social Economic Caste Sensus - SECC','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant - Social Economic Caste Sensus - SECC',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3201006','BRGF - Central Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'BRGF - Central Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3201007','IHSDP - Central Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'IHSDP - Central Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3201008','IGNOAP - Central Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'IGNOAP - Central Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3201009','IGNWP - Central Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'IGNWP - Central Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3201010','IGNDP - Central Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'IGNDP - Central Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO scheme (id,code,"name",validfrom,validto,isactive,description,fundid,sectorid,aaes,fieldid,createddate,lastmodifieddate,createdby,lastmodifiedby) VALUES
	 (nextval('seq_scheme'),'3201011','UIDSSMT - Central Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'UIDSSMT - Central Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3201012','JNNURM - BSUP - Housing','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'JNNURM - BSUP - Housing',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3201013','JNNURM - BSUP - Infra','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'JNNURM - BSUP - Infra',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3201014','JNNURM - Pipe Water Supply','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'JNNURM - Pipe Water Supply',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3201015','General Performance Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'General Performance Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3201016','Grant for Swachh Bharat Mission','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant for Swachh Bharat Mission',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3201017','14th Finance Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'14th Finance Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3201018','Grant for Smart City Mission','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant for Smart City Mission',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3201019','Grant for AMRUT','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant for AMRUT',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3201020','15th Finance Commission Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'15th Finance Commission Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO scheme (id,code,"name",validfrom,validto,isactive,description,fundid,sectorid,aaes,fieldid,createddate,lastmodifieddate,createdby,lastmodifiedby) VALUES
	 (nextval('seq_scheme'),'3202000','Consolidated Grants from State Government','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Consolidated Grants from State Government',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202001','Grants from Central Finance Commission','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grants from Central Finance Commission',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202002','Grants from State Finance Commission','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grants from State Finance Commission',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202003','Grants for Road Development','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grants for Road Development',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202004','National Slum Development Programme - NSDP','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'National Slum Development Programme - NSDP',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202005','MPLAD/MLA funds','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'MPLAD/MLA funds',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202006','Grants for Drinking Water programme','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grants for Drinking Water programme',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202007','Basic Minimum Programme','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Basic Minimum Programme',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202008','VAMBAY','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'VAMBAY',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202009','SJSRY','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'SJSRY',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO scheme (id,code,"name",validfrom,validto,isactive,description,fundid,sectorid,aaes,fieldid,createddate,lastmodifieddate,createdby,lastmodifiedby) VALUES
	 (nextval('seq_scheme'),'3202010','National Family Benefit Scheme - NFBS','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'National Family Benefit Scheme - NFBS',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202011','State Insurance Scheme','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'State Insurance Scheme',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202012','Mid-Day Meal Program','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Mid-Day Meal Program',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202013','Remuneration to Teachers','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Remuneration to Teachers',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202014','Relief for Unemployed','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Relief for Unemployed',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202015','Other Grants','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Other Grants',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202016','Grant for Renovation of Dying Water Bodies','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant for Renovation of Dying Water Bodies',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202017','Grant for Development of Park','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant for Development of Park',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202018','Grant for Accounting Reforms','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant for Accounting Reforms',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202019','Election Fund Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Election Fund Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO scheme (id,code,"name",validfrom,validto,isactive,description,fundid,sectorid,aaes,fieldid,createddate,lastmodifieddate,createdby,lastmodifiedby) VALUES
	 (nextval('seq_scheme'),'3202020','Grants for Construction of Boundary Wall','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grants for Construction of Boundary Wall',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202021','Grant for Dev. And Beautification of Old Town','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant for Dev. And Beautification of Old Town',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202022','DP- Aids','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'DP- Aids',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202023','Grant for Hospital - CMR Fund','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant for Hospital - CMR Fund',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202024','Old Age Pension Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Old Age Pension Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202025','Grant - Storm Water Drainage Project','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant - Storm Water Drainage Project',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202026','IHSDP - State Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'IHSDP - State Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202027','Kalyan Mandap - State Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Kalyan Mandap - State Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202028','Motor Vehicle - State Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Motor Vehicle - State Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202029','Road & Bridge - State Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Road & Bridge - State Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO scheme (id,code,"name",validfrom,validto,isactive,description,fundid,sectorid,aaes,fieldid,createddate,lastmodifieddate,createdby,lastmodifiedby) VALUES
	 (nextval('seq_scheme'),'3202030','Special Development Funds - C.C Road- State Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Special Development Funds - C.C Road- State Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202031','Biju KBK - State Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Biju KBK - State Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202032','MBPY - State Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'MBPY - State Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202033','Pension/Family Pension - State Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Pension/Family Pension - State Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202034','Devolution of Fund - State Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Devolution of Fund - State Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202035','Harischandra Sahayata - State Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Harischandra Sahayata - State Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202036','Urban Asset Creation - State Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Urban Asset Creation - State Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202037','Integrated Low Cost Sanitation Work - ILCS - State Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Integrated Low Cost Sanitation Work - ILCS - State Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202038','Special Problem Fund - State Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Special Problem Fund - State Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202039','Car Festival Grant/Local Festival Grant - State Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Car Festival Grant/Local Festival Grant - State Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO scheme (id,code,"name",validfrom,validto,isactive,description,fundid,sectorid,aaes,fieldid,createddate,lastmodifieddate,createdby,lastmodifiedby) VALUES
	 (nextval('seq_scheme'),'3202040','Grants for Construction of Public Toilets - State Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grants for Construction of Public Toilets - State Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202041','Grants for Solid Waste Management - State Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grants for Solid Waste Management - State Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202042','Grants for Maintenance of Non-Residential Buildings - State','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grants for Maintenance of Non-Residential Buildings - State',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202043','Performace Based Incentives for Providing Basic Urban Servic','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Performace Based Incentives for Providing Basic Urban Servic',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202044','Animal Birth Control - State Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Animal Birth Control - State Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202045','13th FC - Roads & Bridges - State Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'13th FC - Roads & Bridges - State Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202046','Development of Night Shelter/Community Amenities - State Gra','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Development of Night Shelter/Community Amenities - State Gra',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202047','Chief Minister''s Relief Fund','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Chief Minister''s Relief Fund',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202048','Odisha State Disaster Management Fund','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Odisha State Disaster Management Fund',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202049','Grant for Slaughter House','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant for Slaughter House',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO scheme (id,code,"name",validfrom,validto,isactive,description,fundid,sectorid,aaes,fieldid,createddate,lastmodifieddate,createdby,lastmodifiedby) VALUES
	 (nextval('seq_scheme'),'3202050','Grant received from Sewerage board - OWSSB fund','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant received from Sewerage board - OWSSB fund',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202051','Grant for City Development Plans','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant for City Development Plans',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202052','Compensation for Sitting fees, honorarium, TA & DA','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Compensation for Sitting fees, honorarium, TA & DA',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202053','Grant for Smart City Mission-State Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant for Smart City Mission-State Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202054','OULM-SEP- individual','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'OULM-SEP- individual',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202055','OULM-SEP- Group','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'OULM-SEP- Group',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202056','OULM-EST&P - training','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'OULM-EST&P - training',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202057','OULM-Revolving Fund','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'OULM-Revolving Fund',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202058','OULM-Interest on Bank Account','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'OULM-Interest on Bank Account',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202059','Grant for Aahar','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant for Aahar',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO scheme (id,code,"name",validfrom,validto,isactive,description,fundid,sectorid,aaes,fieldid,createddate,lastmodifieddate,createdby,lastmodifiedby) VALUES
	 (nextval('seq_scheme'),'3202060','4th State Finance Commission-Creation of Capital Asset','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'4th State Finance Commission-Creation of Capital Asset',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202061','4th State Finance Commission-Maintenance of Capital','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'4th State Finance Commission-Maintenance of Capital',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202062','Grant for Urban Infrastracture Initiative- UNNATI','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant for Urban Infrastracture Initiative- UNNATI',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202063','Compensation for Arrear Pension and Basic Services','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Compensation for Arrear Pension and Basic Services',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202064','Grant for Mini Stadium/Playground','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant for Mini Stadium/Playground',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202065','BIJU YUVA VAHINI - BYV','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'BIJU YUVA VAHINI - BYV',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202066','Nirman Shramik Pension Yojana','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Nirman Shramik Pension Yojana',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202067','Kalakar Sahayata Yojana','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Kalakar Sahayata Yojana',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202068','Critical Gap Fund','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Critical Gap Fund',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202069','Grant in aid for Mobility Infrastructure','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant in aid for Mobility Infrastructure',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO scheme (id,code,"name",validfrom,validto,isactive,description,fundid,sectorid,aaes,fieldid,createddate,lastmodifieddate,createdby,lastmodifiedby) VALUES
	 (nextval('seq_scheme'),'3202070','Debt Service coverage of OUIDF Loan Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Debt Service coverage of OUIDF Loan Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202071','Funds for Novel Corona Virus - COVID-19','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Funds for Novel Corona Virus - COVID-19',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202072','UNNATI - UWEI','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'UNNATI - UWEI',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3202073','JAGA Mission','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'JAGA Mission',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3203000','Consolidated Grants from Other Government Agencies','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Consolidated Grants from Other Government Agencies',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3203001','Grant for Ornamental Street Light- OMC','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant for Ornamental Street Light- OMC',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3203002','Grant for Street Light','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant for Street Light',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3203003','WODC Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'WODC Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3203004','NALCO Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'NALCO Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3203005','RLTAP Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'RLTAP Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO scheme (id,code,"name",validfrom,validto,isactive,description,fundid,sectorid,aaes,fieldid,createddate,lastmodifieddate,createdby,lastmodifiedby) VALUES
	 (nextval('seq_scheme'),'3203006','Special Development Programme','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Special Development Programme',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3203007','District Innovation Fund','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'District Innovation Fund',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3203008','Pre-Matric Scholarship Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Pre-Matric Scholarship Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3203009','National Urban Health Mission- NUHM Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'National Urban Health Mission- NUHM Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3203010','Grant from Odisha Urban Infrastructure Development Fund- OUID','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant from Odisha Urban Infrastructure Development Fund- OUID',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3203011','CSR Development Fund from Paradip Port Trust','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'CSR Development Fund from Paradip Port Trust',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3203012','Grant from Fishery Engineering Division � Fish Market','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant from Fishery Engineering Division � Fish Market',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3204000','Consolidated Grants from Financial Institutions','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Consolidated Grants from Financial Institutions',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3205000','Consolidated Grants from Welfare Bodies','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Consolidated Grants from Welfare Bodies',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3205001','Grant from Bill & Melinda Gates Foundation','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant from Bill & Melinda Gates Foundation',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO scheme (id,code,"name",validfrom,validto,isactive,description,fundid,sectorid,aaes,fieldid,createddate,lastmodifieddate,createdby,lastmodifiedby) VALUES
	 (nextval('seq_scheme'),'3206000','Consolidated Grants from International Organizations','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Consolidated Grants from International Organizations',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3206001','Grant From JICA','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Grant From JICA',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208000','Consolidated Grants from Others','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Consolidated Grants from Others',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208001','JnNURM - BSUP - Housing - Bharatpur','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'JnNURM - BSUP - Housing - Bharatpur',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208002','JnNURM - BSUP - Housing - Dumduma','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'JnNURM - BSUP - Housing - Dumduma',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208003','JnNURM - BSUP - Housing - Nayapalli','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'JnNURM - BSUP - Housing - Nayapalli',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208004','JnNURM - BSUP - Infra - Bharatpur','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'JnNURM - BSUP - Infra - Bharatpur',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208005','JnNURM - BSUP - Infra - Dumduma','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'JnNURM - BSUP - Infra - Dumduma',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208006','JnNURM - BSUP - Infra - Nayapalli','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'JnNURM - BSUP - Infra - Nayapalli',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208007','JnNURM - BSUP - Interest on Bank Deposit','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'JnNURM - BSUP - Interest on Bank Deposit',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO scheme (id,code,"name",validfrom,validto,isactive,description,fundid,sectorid,aaes,fieldid,createddate,lastmodifieddate,createdby,lastmodifiedby) VALUES
	 (nextval('seq_scheme'),'3208008','JnNURM - City Bus','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'JnNURM - City Bus',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208009','JnNURM - City Bus - Interest on Bank Deposit','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'JnNURM - City Bus - Interest on Bank Deposit',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208010','SJSRY - USEP - Subsidy on Loan','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'SJSRY - USEP - Subsidy on Loan',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208011','SJSRY - UWSP - Revolving Fund','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'SJSRY - UWSP - Revolving Fund',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208012','SJSRY - UWSP - Subsidy on Loan','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'SJSRY - UWSP - Subsidy on Loan',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208013','SJSRY - Step Up - Training Programme','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'SJSRY - Step Up - Training Programme',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208014','SJSRY - UWEP - Wages for Infra Dev','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'SJSRY - UWEP - Wages for Infra Dev',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208015','SJSRY - UCDN - Community Development','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'SJSRY - UCDN - Community Development',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208016','SJSRY - Infrastructure Support','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'SJSRY - Infrastructure Support',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208017','SJSRY - Interest on Bank Deposit','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'SJSRY - Interest on Bank Deposit',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO scheme (id,code,"name",validfrom,validto,isactive,description,fundid,sectorid,aaes,fieldid,createddate,lastmodifieddate,createdby,lastmodifiedby) VALUES
	 (nextval('seq_scheme'),'3208018','NRHM - Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'NRHM - Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208019','NRHM - Interest on Bank Deposit','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'NRHM - Interest on Bank Deposit',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208020','Super Cyclone Fund','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Super Cyclone Fund',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208021','Balika Samrudhi Yojana','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Balika Samrudhi Yojana',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208022','Rajiv Awas Yojana','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Rajiv Awas Yojana',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208023','JnNURM - Project Implementation Unit - PIU','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'JnNURM - Project Implementation Unit - PIU',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208024','JnNURM - PIU - Interest on Bank Deposit','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'JnNURM - PIU - Interest on Bank Deposit',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208025','Special Relief Commission - SRC Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Special Relief Commission - SRC Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208026','SRC - Interest on Bank Deposit','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'SRC - Interest on Bank Deposit',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208027','CDPO/Anganbadi Building Construction Grant','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'CDPO/Anganbadi Building Construction Grant',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO scheme (id,code,"name",validfrom,validto,isactive,description,fundid,sectorid,aaes,fieldid,createddate,lastmodifieddate,createdby,lastmodifiedby) VALUES
	 (nextval('seq_scheme'),'3208028','CDPO/Anganbadi - Interest on Bank Deposit','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'CDPO/Anganbadi - Interest on Bank Deposit',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208029','JnNURM - National Mission Mode Project - NMMP','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'JnNURM - National Mission Mode Project - NMMP',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208030','JnNURM - Low Cost Sanitation Work','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'JnNURM - Low Cost Sanitation Work',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208031','NULM - SM & ID','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'NULM - SM & ID',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208032','NULM - SEP- I & - G','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'NULM - SEP- I & - G',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208033','NULM - EST & P','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'NULM - EST & P',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208034','NULM - CB & T','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'NULM - CB & T',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208035','NULM - SUH','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'NULM - SUH',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208036','NULM - SUSV','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'NULM - SUSV',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208037','NULM - Interest on Bank Deposits','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'NULM - Interest on Bank Deposits',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO scheme (id,code,"name",validfrom,validto,isactive,description,fundid,sectorid,aaes,fieldid,createddate,lastmodifieddate,createdby,lastmodifiedby) VALUES
	 (nextval('seq_scheme'),'3208038','JnNURM - Challenge Fund','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'JnNURM - Challenge Fund',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208039','NULM-City Livelihood Center','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'NULM-City Livelihood Center',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208040','Median Plantation','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Median Plantation',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	 (nextval('seq_scheme'),'3208041','Installation of Plastic Waste Management','2021-01-04 00:00:00','2025-07-03 00:00:00',NULL,'Installation of Plastic Waste Management',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL);

--update scheme active status	 

update scheme set isactive=true;	
-- insert into financialyear

INSERT INTO financialyear (id,financialyear,startingdate,endingdate,isactive,isactiveforposting,isclosed,transferclosingbalance,createddate,lastmodifiedby,lastmodifieddate,"version",createdby) VALUES
	 (nextVal('seq_financialyear'),'2011-12','2011-04-01 00:00:00','2012-03-31 00:00:00',true,false,false,false,'2021-04-27 00:00:00',1,'2021-04-27 00:00:00',0,1),
	 (nextVal('seq_financialyear'),'2012-13','2012-04-01 00:00:00','2013-03-31 00:00:00',true,false,false,false,'2021-04-27 00:00:00',1,'2021-04-27 00:00:00',0,1),
	 (nextVal('seq_financialyear'),'2013-14','2013-04-01 00:00:00','2014-03-31 00:00:00',true,false,false,false,'2021-04-27 00:00:00',1,'2021-04-27 00:00:00',0,1),
	 (nextVal('seq_financialyear'),'2014-15','2014-04-01 00:00:00','2015-03-31 00:00:00',true,false,false,false,'2021-04-27 00:00:00',1,'2021-04-27 00:00:00',0,1),
	 (nextVal('seq_financialyear'),'2015-16','2015-04-01 00:00:00','2016-03-31 00:00:00',true,false,false,false,'2021-04-27 00:00:00',1,'2021-04-27 00:00:00',0,1),
	 (nextVal('seq_financialyear'),'2016-17','2016-04-01 00:00:00','2017-03-31 00:00:00',true,false,false,false,'2012-03-30 00:00:00',1,'2012-03-30 00:00:00',0,1),
	 (nextVal('seq_financialyear'),'2017-18','2017-04-01 00:00:00','2018-03-31 00:00:00',true,false,false,false,'2021-04-27 00:00:00',1,'2021-04-27 00:00:00',0,1),
	 (nextVal('seq_financialyear'),'2018-19','2018-04-01 00:00:00','2019-03-31 00:00:00',true,false,false,false,'2021-04-27 00:00:00',1,'2021-04-27 00:00:00',0,1),
	 (nextVal('seq_financialyear'),'2019-20','2019-04-01 00:00:00','2020-03-31 00:00:00',true,false,false,false,'2021-04-27 00:00:00',1,'2021-04-27 00:00:00',0,1),
	 (nextVal('seq_financialyear'),'2020-21','2020-04-01 00:00:00','2021-03-31 00:00:00',true,false,false,false,'2021-04-27 00:00:00',1,'2021-04-27 00:00:00',0,1);
INSERT INTO financialyear (id,financialyear,startingdate,endingdate,isactive,isactiveforposting,isclosed,transferclosingbalance,createddate,lastmodifiedby,lastmodifieddate,"version",createdby) VALUES
	 (nextVal('seq_financialyear'),'2021-22','2021-04-01 00:00:00','2022-03-31 00:00:00',true,false,false,false,'2021-04-01 20:55:30.801',NULL,'2021-04-01 20:55:30.801',0,NULL);

INSERT INTO fiscalperiod (id,"type","name",startingdate,endingdate,parentid,financialyearid,moduleid,createddate,lastmodifiedby,lastmodifieddate,"version",createdby,isactive,isactiveforposting,isclosed) VALUES
	 (nextVal('seq_fiscalperiod'),NULL,'2011-12','2011-04-01 00:00:00','2012-03-31 00:00:00',NULL,(select id from financialyear where financialyear='2011-12'),NULL,now(),1,now(),0,1,true,false,NULL),
	 (nextVal('seq_fiscalperiod'),NULL,'2012-13','2012-04-01 00:00:00','2013-03-31 00:00:00',NULL,(select id from financialyear where financialyear='2012-13'),NULL,now(),1,now(),0,1,true,false,false),
	 (nextVal('seq_fiscalperiod'),NULL,'2013-14','2013-04-01 00:00:00','2014-03-31 00:00:00',NULL,(select id from financialyear where financialyear='2013-14'),NULL,now(),1,now(),0,1,true,false,false),
	 (nextVal('seq_fiscalperiod'),NULL,'2014-15','2014-04-01 00:00:00','2015-03-31 00:00:00',NULL,(select id from financialyear where financialyear='2014-15'),NULL,now(),1,now(),0,1,true,false,false),
	 (nextVal('seq_fiscalperiod'),NULL,'2015-16','2015-04-01 00:00:00','2016-03-31 00:00:00',NULL,(select id from financialyear where financialyear='2015-16'),NULL,now(),1,now(),0,1,true,false,false),
	 (nextVal('seq_fiscalperiod'),NULL,'2016-17','2016-04-01 00:00:00','2017-03-31 00:00:00',NULL,(select id from financialyear where financialyear='2016-17'),NULL,now(),1,now(),0,1,true,false,false),
	 (nextVal('seq_fiscalperiod'),NULL,'2017-18','2017-04-01 00:00:00','2018-03-31 00:00:00',NULL,(select id from financialyear where financialyear='2017-18'),NULL,now(),1,now(),0,1,true,false,false),
	 (nextVal('seq_fiscalperiod'),NULL,'2018-19','2018-04-01 00:00:00','2019-03-31 00:00:00',NULL,(select id from financialyear where financialyear='2018-19'),NULL,now(),1,now(),0,1,true,false,false),
	 (nextVal('seq_fiscalperiod'),NULL,'2019-20','2019-04-01 00:00:00','2020-03-31 00:00:00',NULL,(select id from financialyear where financialyear='2019-20'),NULL,now(),1,now(),0,1,true,false,false),
	 (nextVal('seq_fiscalperiod'),NULL,'2020-21','2020-04-01 00:00:00','2021-03-31 00:00:00',NULL,(select id from financialyear where financialyear='2020-21'),NULL,now(),1,now(),0,1,true,false,false);
INSERT INTO fiscalperiod (id,"type","name",startingdate,endingdate,parentid,financialyearid,moduleid,createddate,lastmodifiedby,lastmodifieddate,"version",createdby,isactive,isactiveforposting,isclosed) VALUES
	 (nextVal('seq_fiscalperiod'),NULL,'2021-22','2021-04-01 00:00:00','2022-03-31 00:00:00',NULL,(select id from financialyear where financialyear='2021-22'),NULL,now(),1,now(),0,1,true,false,false);

-- for subledger required data
INSERT INTO chartofaccountdetail (id,glcodeid,detailtypeid,createdby,createddate,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextVal('seq_chartofaccountdetail'),(select id from chartofaccounts where glcode='3501002'),12,NULL,'2021-03-04 00:48:28.273','2021-03-04 00:48:28.273',NULL,0);
INSERT INTO chartofaccountdetail (id,glcodeid,detailtypeid,createdby,createddate,lastmodifieddate,lastmodifiedby,"version") VALUES
	 (nextVal('seq_chartofaccountdetail'),(select id from chartofaccounts where glcode='3501001'),11,NULL,'2021-03-04 00:48:28.273','2021-03-04 00:48:28.273',NULL,0);



----Delete moduletype='EXPENSEBILL' data ONLY egw_status

delete from egw_status where moduletype='EXPENSEBILL';

--moduletype='EXPENSEBILL' insert into egw_status

 INSERT INTO egw_status (id,moduletype,description,lastmodifieddate,code,order_id) VALUES 
 (nextVal('seq_egw_status'),'EXPENSEBILL','Voucher Created',now(),'Voucher Created',3)
,(nextVal('seq_egw_status'),'EXPENSEBILL','Bill Payment Approved',now(),'Bill Payment Approved',7)
,(nextVal('seq_egw_status'),'EXPENSEBILL','Voucher Approved',now(),'Voucher Approved',8)
,(nextVal('seq_egw_status'),'EXPENSEBILL','Pending for Cancellation',now(),'Pending for Cancellation',9)
,(nextVal('seq_egw_status'),'EXPENSEBILL','Bill Approved',now(),'Approved',5)
,(nextVal('seq_egw_status'),'EXPENSEBILL','Bill Rejected','2021-03-04 13:53:50.615','Rejected',4)
,(nextVal('seq_egw_status'),'EXPENSEBILL','Bill Cancelled',now(),'Cancelled',2)
,(nextVal('seq_egw_status'),'EXPENSEBILL','Bill Payment Created',now(),'Bill Payment Created',10)
;
INSERT INTO egw_status (id,moduletype,description,lastmodifieddate,code,order_id) VALUES 
(nextVal('seq_egw_status'),'EXPENSEBILL','Bill Created',now(),'Created',1)
;

--insert into chartofaccounts 0

INSERT INTO chartofaccounts (id,glcode,"name",description,isactiveforposting,parentid,purposeid,operation,"type","class",classification,functionreqd,budgetcheckreq,scheduleid,receiptscheduleid,receiptoperation,paymentscheduleid,paymentoperation,majorcode,fiescheduleid,fieoperation,createddate,createdby,lastmodifieddate,lastmodifiedby,"version") VALUES
(0,'00000','N/A',NULL,true,NULL,NULL,NULL,'L',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2021-04-22 13:00:05.163',NULL,'2021-04-22 13:00:05.163',NULL,0);
--insert eg_module
INSERT INTO eg_module
(id, "name", enabled, contextroot, parentmodule, displayname, ordernumber, rootmodule)
VALUES(nextVal('seq_eg_module'), 'Collection', true, 'collection', NULL, 'Collection', 5, NULL);

--Insert into appconfig
INSERT INTO eg_appconfig (id,key_name,description,"version",createdby,lastmodifiedby,createddate,lastmodifieddate,"module") VALUES
(nextVal('seq_eg_appconfig'),'PEXNO_GENERATION_AUTO','PEXNO_GENERATION_AUTO',0,1,1,now(),now(),(select id from eg_module where name='EGF'));

INSERT INTO eg_appconfig (id,key_name,description,"version",createdby,lastmodifiedby,createddate,lastmodifieddate,"module") VALUES 
(nextVal('seq_eg_appconfig'),'Reason For RTGS Surrendaring','Reason For RTGS Surrendaring',NULL,NULL,NULL,NULL,NULL,302)
;
INSERT INTO eg_appconfig (id,key_name,description,"version",createdby,lastmodifiedby,createddate,lastmodifieddate,"module") VALUES 
(nextVal('seq_eg_appconfig'),'receipt_sub_divison','receipt_sub_divison',0,NULL,NULL,NULL,NULL,302)
;

INSERT INTO eg_appconfig
(id,key_name, description, "version", createdby, lastmodifiedby, createddate, lastmodifieddate, "module")
VALUES(nextVal('seq_eg_appconfig'),'COLLECTION_BANKREMITTANCE_DEPTCODE', 'Collection Remittance department code', 0, NULL, NULL, NULL, NULL, (SELECT id from eg_module where name='Collection'));

--Insert into appconfig_values
INSERT INTO eg_appconfig_values (id,key_id,effective_from,value,createddate,lastmodifieddate,createdby,lastmodifiedby,"version") VALUES
(nextVal('seq_eg_appconfig_values'),(select id from eg_appconfig where key_name='PEXNO_GENERATION_AUTO'),now(),'Y',now(),now(),1,1,0);


INSERT INTO eg_appconfig_values (id,key_id,effective_from,value,createddate,lastmodifieddate,createdby,lastmodifiedby,"version") VALUES 
(nextVal('seq_eg_appconfig_values'),(select id from eg_appconfig where key_name='Reason For RTGS Surrendaring'),now(),'RTGS to be scrapped.',NULL,NULL,NULL,NULL,NULL)
,(nextVal('seq_eg_appconfig_values'),(select id from eg_appconfig where key_name='Reason For RTGS Surrendaring'),now(),'Damaged RTGS.',NULL,NULL,NULL,NULL,NULL)
,(nextVal('seq_eg_appconfig_values'),(select id from eg_appconfig where key_name='Reason For RTGS Surrendaring'),now(),'Surrender : RTGS to be cancelled.',NULL,NULL,NULL,NULL,NULL)
;

INSERT INTO eg_appconfig_values
(id, key_id, effective_from, value, createddate, lastmodifieddate, createdby, lastmodifiedby, "version")
VALUES(nextVal('seq_eg_appconfig_values'), (SELECT id from eg_appconfig where key_name='COLLECTION_BANKREMITTANCE_DEPTCODE'), now(), '390', NULL, NULL, NULL, NULL, 0);

INSERT INTO eg_appconfig_values
(id, key_id, effective_from, value, createddate, lastmodifieddate, createdby, lastmodifiedby, "version")
VALUES(nextVal('seq_eg_appconfig_values'), (SELECT id from eg_appconfig where key_name='DEFAULTTXNMISATTRRIBUTES'), now(), 'fileno|N', NULL, NULL, NULL, NULL, NULL);

INSERT INTO eg_appconfig_values
(id, key_id, effective_from, value, createddate, lastmodifieddate, createdby, lastmodifiedby, "version")
VALUES(nextVal('seq_eg_appconfig_values'), (SELECT id from eg_appconfig where key_name='DEFAULTTXNMISATTRRIBUTES'), now(), 'backdateentry|N', NULL, NULL, NULL, NULL, NULL);

INSERT INTO eg_appconfig_values
(id, key_id, effective_from, value, createddate, lastmodifieddate, createdby, lastmodifiedby, "version")
VALUES(nextVal('seq_eg_appconfig_values'), (SELECT id from eg_appconfig where key_name='DEFAULTTXNMISATTRRIBUTES'), now(), 'narration|N', NULL, NULL, NULL, NULL, NULL);

--copy the matrix for payment req and others
CREATE OR REPLACE VIEW bill_party
AS SELECT eb3.billnumber,
    a.detailname
   FROM eg_billpayeedetails eb,
    accountdetailkey a,
    eg_billdetails eb2,
    eg_billregister eb3
  WHERE eb3.id = eb2.billid AND eb.billdetailid = eb2.id AND eb.accountdetailkeyid = a.detailkey AND eb.creditamount <> 0::numeric;

------views----
-- bill_party source

CREATE OR REPLACE VIEW bill_party
AS SELECT eb3.billnumber,
    a.detailname
   FROM eg_billpayeedetails eb,
    accountdetailkey a,
    eg_billdetails eb2,
    eg_billregister eb3
  WHERE eb3.id = eb2.billid AND eb.billdetailid = eb2.id AND eb.accountdetailkeyid = a.detailkey AND eb.creditamount <> 0::numeric;


-- contractor_active_view source

CREATE OR REPLACE VIEW contractor_active_view
AS SELECT es.id,
    es.code,
    es.name,
    es.correspondenceaddress,
    es.paymentaddress,
    es.contactperson,
    es.email,
    es.narration,
    es.pannumber,
    es.tinnumber,
    es.bank,
    es.ifsccode,
    es.bankaccount,
    es.registrationnumber,
    es.status,
    es.createdby,
    es.lastmodifiedby,
    es.createddate,
    es.lastmodifieddate,
    es.version,
    es.mobilenumber,
    es.epfnumber,
    es.esinumber,
    es.gstregisteredstate,
    es.vigilance,
    es.blcklistfromdate,
    es.blcklisttodate,
    es.adharnumber
   FROM egf_contractor es
  WHERE es.status = (( SELECT es2.id
           FROM egw_status es2
          WHERE es2.moduletype::text = 'Contractor'::text AND es2.code::text = 'Active'::text));


-- daybook_detail_party source

CREATE OR REPLACE VIEW daybook_detail_party
AS SELECT v.id,
    a.detailname
   FROM voucherheader v,
    generalledger g,
    generalledgerdetail g2,
    accountdetailkey a
  WHERE v.id = g.voucherheaderid AND g.id = g2.generalledgerid AND g2.detailtypeid = a.detailtypeid AND g2.detailkeyid = a.detailkey;


-- recovery_report_party source

CREATE OR REPLACE VIEW recovery_report_party
AS SELECT concat(acc.detailkey, '-', acc.detailtypeid) AS comp,
    acc.detailname
   FROM accountdetailkey acc;


-- supplier_active_view source

CREATE OR REPLACE VIEW supplier_active_view
AS SELECT es.id,
    es.code,
    es.name,
    es.correspondenceaddress,
    es.paymentaddress,
    es.contactperson,
    es.email,
    es.narration,
    es.pannumber,
    es.tinnumber,
    es.mobilenumber,
    es.bank,
    es.ifsccode,
    es.bankaccount,
    es.registrationnumber,
    es.status,
    es.createdby,
    es.lastmodifiedby,
    es.createddate,
    es.lastmodifieddate,
    es.version,
    es.epfnumber,
    es.esinumber,
    es.gstregisteredstate
   FROM egf_supplier es
  WHERE es.status = (( SELECT es2.id
           FROM egw_status es2
          WHERE es2.moduletype::text = 'Supplier'::text AND es2.code::text = 'Active'::text));


-- voucher_detail_bpvmapping source

CREATE OR REPLACE VIEW voucher_detail_bpvmapping
AS SELECT v.id,
    v.vouchernumber,
    v.voucherdate
   FROM voucherheader v
  WHERE v.type::text = 'Payment'::text;


-- voucher_detail_instrument source

CREATE OR REPLACE VIEW voucher_detail_instrument
AS SELECT ei.id,
    ei.transactionnumber,
    ei.transactiondate,
    ei2.voucherheaderid,
    b.accountnumber
   FROM egf_instrumentheader ei,
    egf_instrumentvoucher ei2,
    bankaccount b
  WHERE ei.id = ei2.instrumentheaderid AND ei.bankaccountid = b.id AND (ei.id_status = ANY (ARRAY[2::bigint, 4::bigint]));


-- voucher_detail_ledger source

CREATE OR REPLACE VIEW voucher_detail_ledger
AS SELECT egb.id,
    egb.glcode,
    egb.debitamount,
    egb.creditamount,
    egb.voucherheaderid
   FROM generalledger egb;


-- voucher_detail_main source

CREATE OR REPLACE VIEW voucher_detail_main
AS SELECT v.id AS voucherid,
    v.voucherdate,
    v.name,
    v.vouchernumber,
    v.status,
    f.name AS head,
    ed.name AS department,
    f2.id AS fund,
    ( SELECT s.name
           FROM scheme s
          WHERE s.id = v2.schemeid) AS scheme,
    v.description
   FROM voucherheader v,
    vouchermis v2,
    function f,
    eg_department ed,
    fund f2
  WHERE v.id = v2.voucherheaderid AND v.fundid = f2.id AND v2.functionid = f.id AND v2.departmentcode::text = ed.code::text AND ((v.name::text = ANY (ARRAY['Remittance Payment'::character varying::text, 'Direct Bank Payment'::character varying::text])) OR v.type::text = 'Journal Voucher'::text);


-- voucher_detail_misc source

CREATE OR REPLACE VIEW voucher_detail_misc
AS SELECT m.payvhid,
    m.paidamount
   FROM miscbilldetail m
  WHERE m.billvhid IS NULL;


-- voucher_detail_miscbill source

CREATE OR REPLACE VIEW voucher_detail_miscbill
AS SELECT m.id,
    m.billvhid,
    m.payvhid,
    m.paidamount
   FROM miscbilldetail m,
    voucherheader v2
  WHERE m.billvhid = v2.id AND m.billvhid IS NOT NULL AND v2.status <> 4;


-- voucher_detail_party source

CREATE OR REPLACE VIEW voucher_detail_party
AS SELECT v.id,
    a.detailname
   FROM voucherheader v,
    generalledger g,
    generalledgerdetail g2,
    accountdetailkey a
  WHERE v.id = g.voucherheaderid AND g.id = g2.generalledgerid AND g2.detailkeyid = a.detailkey AND ((v.name::text = ANY (ARRAY['Remittance Payment'::character varying::text, 'Direct Bank Payment'::character varying::text])) OR v.type::text = 'Journal Voucher'::text);
  
  --tds data
  INSERT INTO tds (id,"type",ispaid,glcodeid,isactive,lastmodifieddate,createddate,lastmodifiedby,rate,effectivefrom,createdby,remitted,bsrcode,description,partytypeid,bankid,caplimit,isearning,recoveryname,calculationtype,"section",recovery_mode,remittance_mode,ifsccode,accountnumber,"version",isreport) VALUES 
(nextVal('seq_tds'),'3502009',NULL,(select id from chartofaccounts where glcode='3502009'),true,now(),now(),NULL,NULL,NULL,315,'IT Department',NULL,NULL,NULL,NULL,NULL,NULL,'TDS-Contractor',NULL,NULL,'M',NULL,NULL,NULL,1,NULL)
,(nextVal('seq_tds'),'3502006',NULL,(select id from chartofaccounts where glcode='3502006'),true,now(),now(),NULL,NULL,NULL,315,'IT Department',NULL,NULL,NULL,NULL,NULL,NULL,'TDS - Employees',NULL,NULL,'M',NULL,NULL,NULL,1,NULL)
,(nextVal('seq_tds'),'3502005',NULL,(select id from chartofaccounts where glcode='3502005'),true,now(),now(),NULL,NULL,NULL,315,'Commercial Tax Department',NULL,NULL,NULL,NULL,NULL,NULL,'Profession Tax Deduction',NULL,NULL,'M',NULL,NULL,NULL,0,NULL)
,(nextVal('seq_tds'),'3502023',NULL,(select id from chartofaccounts where glcode='3502023'),true,now(),now(),NULL,NULL,NULL,315,'Odisha Building & other Construction Worker''s welfare Board',NULL,NULL,NULL,NULL,NULL,NULL,'Construction Cess Payable',NULL,NULL,'M',NULL,NULL,NULL,0,NULL)
,(nextVal('seq_tds'),'3502024',NULL,(select id from chartofaccounts where glcode='3502024'),true,now(),now(),NULL,NULL,NULL,315,'Royalty Payable',NULL,NULL,NULL,NULL,NULL,NULL,'Royalty Payable',NULL,NULL,'M',NULL,NULL,NULL,0,NULL)
,(nextVal('seq_tds'),'3502026',NULL,(select id from chartofaccounts where glcode='3502026'),true,now(),now(),NULL,NULL,NULL,315,'Treasury',NULL,NULL,NULL,NULL,NULL,NULL,'GIS Recovery',NULL,NULL,'M',NULL,NULL,NULL,0,NULL)
,(nextVal('seq_tds'),'3502030',NULL,(select id from chartofaccounts where glcode='3502030'),true,now(),now(),NULL,NULL,NULL,315,'GA & PG rent',NULL,NULL,NULL,NULL,NULL,NULL,'Recovery Payable-Quarter Rent',NULL,NULL,'M',NULL,NULL,NULL,0,NULL)
,(nextVal('seq_tds'),'3502032',NULL,(select id from chartofaccounts where glcode='3502032'),true,now(),now(),NULL,NULL,NULL,315,'Individual Bank Account of Employees',NULL,NULL,NULL,NULL,NULL,NULL,'Recovery Payable - CPF',NULL,NULL,'M',NULL,NULL,NULL,0,NULL)
,(nextVal('seq_tds'),'3502033',NULL,(select id from chartofaccounts where glcode='3502033'),true,now(),now(),NULL,NULL,NULL,315,'LIC office',NULL,NULL,NULL,NULL,NULL,NULL,'Recovery Payable - LIC Premium',NULL,NULL,'M',NULL,NULL,NULL,0,NULL)
,(nextVal('seq_tds'),'3502034',NULL,(select id from chartofaccounts where glcode='3502034'),true,now(),now(),NULL,NULL,NULL,315,'Treasury',NULL,NULL,NULL,NULL,NULL,NULL,'Recovery Payable - GPF',NULL,NULL,'M',NULL,NULL,NULL,0,NULL)
;
INSERT INTO tds (id,"type",ispaid,glcodeid,isactive,lastmodifieddate,createddate,lastmodifiedby,rate,effectivefrom,createdby,remitted,bsrcode,description,partytypeid,bankid,caplimit,isearning,recoveryname,calculationtype,"section",recovery_mode,remittance_mode,ifsccode,accountnumber,"version",isreport) VALUES 
 (nextVal('seq_tds'),'3502035',NULL,(select id from chartofaccounts where glcode='3502035'),true,now(),now(),NULL,NULL,NULL,315,'PF Office',NULL,NULL,NULL,NULL,NULL,NULL,'Recovery Payable - EPF',NULL,NULL,'M',NULL,NULL,NULL,0,NULL)
,(nextVal('seq_tds'),'3502048',NULL,(select id from chartofaccounts where glcode='3502048'),true,now(),now(),NULL,NULL,NULL,315,'Respective Bank Account',NULL,NULL,NULL,NULL,NULL,NULL,'Recovery Payable - Bank Loans',NULL,NULL,'M',NULL,NULL,NULL,0,NULL)
,(nextVal('seq_tds'),'3502025',NULL,(select id from chartofaccounts where glcode='3502025'),true,now(),now(),NULL,NULL,NULL,315,'PF Office',NULL,NULL,NULL,NULL,NULL,NULL,'Provident Fund Deductions - Contractors',NULL,NULL,'M',NULL,NULL,NULL,1,NULL)
,(nextVal('seq_tds'),'3502049',NULL,(select id from chartofaccounts where glcode='3502049'),true,now(),now(),NULL,NULL,NULL,315,'IT Department',NULL,NULL,NULL,NULL,NULL,NULL,'TDS - Professional',NULL,NULL,'M',NULL,NULL,NULL,0,NULL)
,(nextVal('seq_tds'),'3502051',NULL,(select id from chartofaccounts where glcode='3502051'),true,now(),now(),NULL,NULL,NULL,315,'Commercial Tax Department',NULL,NULL,NULL,NULL,NULL,NULL,'GST Payable',NULL,NULL,'M',NULL,NULL,NULL,0,NULL)
,(nextVal('seq_tds'),'3502052',NULL,(select id from chartofaccounts where glcode='3502052'),true,now(),now(),NULL,NULL,NULL,315,'IT Department',NULL,NULL,NULL,NULL,NULL,NULL,'TCS from Auctioner',NULL,NULL,'M',NULL,NULL,NULL,0,NULL)
,(nextVal('seq_tds'),'3502053',NULL,(select id from chartofaccounts where glcode='3502053'),true,now(),now(),NULL,NULL,NULL,315,'Commercial Tax Department',NULL,NULL,NULL,NULL,NULL,NULL,'TDS under GST',NULL,NULL,'M',NULL,NULL,NULL,0,NULL)
,(nextVal('seq_tds'),'3502054',NULL,(select id from chartofaccounts where glcode='3502054'),true,now(),now(),NULL,NULL,NULL,315,'Commercial Tax Department',NULL,NULL,NULL,NULL,NULL,NULL,'Output CGST',NULL,NULL,'M',NULL,NULL,NULL,0,NULL)
,(nextVal('seq_tds'),'3502055',NULL,(select id from chartofaccounts where glcode='3502055'),true,now(),now(),NULL,NULL,NULL,315,'Commercial Tax Department',NULL,NULL,NULL,NULL,NULL,NULL,'Output SGST',NULL,NULL,'M',NULL,NULL,NULL,0,NULL)
,(nextVal('seq_tds'),'3502056',NULL,(select id from chartofaccounts where glcode='3502056'),true,now(),now(),NULL,NULL,NULL,315,'Commercial Tax Department',NULL,NULL,NULL,NULL,NULL,NULL,'Output IGST',NULL,NULL,'M',NULL,NULL,NULL,0,NULL)
;
