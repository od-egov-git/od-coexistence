SELECT setval('"seq_eg_module"',(SELECT MAX(ID) FROM eg_module ));

-----------------START--------------------
INSERT INTO eg_module(id, name, enabled, contextroot, parentmodule, displayname, ordernumber) VALUES (nextval('SEQ_EG_MODULE'), 'Collection', true, 'collection', null, 'Collection', (select max(ordernumber)+1 from eg_module where parentmodule is null));
INSERT INTO eg_module(id, name, enabled, contextroot, parentmodule, displayname, ordernumber) VALUES (nextval('SEQ_EG_MODULE'),'Collection Transaction',true,null,(select id from eg_module where name='Collection'),'Transactions',1);
INSERT INTO eg_module(id, name, enabled, contextroot, parentmodule, displayname, ordernumber) VALUES (nextval('SEQ_EG_MODULE'),'Collection Reports',true,null,(select id from eg_module where name='Collection'),'Reports',2);
INSERT INTO eg_module(id, name, enabled, contextroot, parentmodule, displayname, ordernumber) VALUES (nextval('SEQ_EG_MODULE'),'Collection Master',true,null,(select id from eg_module where name='Collection'),'Master',3);
INSERT INTO eg_module(id, name, enabled, contextroot, parentmodule, displayname, ordernumber) VALUES (nextval('SEQ_EG_MODULE'),'Collection Challan',false,null,(select id from eg_module where name='Collection'),'Challan Services',null);
INSERT INTO eg_module(id, name, enabled, contextroot, parentmodule, displayname, ordernumber) VALUES (nextval('SEQ_EG_MODULE'),'COLLECTION-COMMON',false,'collection',(select id from eg_module where name='Collection'),'COLLECTION-COMMON',null);
INSERT INTO eg_module(id, name, enabled, contextroot, parentmodule, displayname, ordernumber) VALUES (nextval('SEQ_EG_MODULE'),'Billbased Services',true,null,(select id from eg_module where name='Collection Transaction'),'Bill Based Service',1);
INSERT INTO eg_module(id, name, enabled, contextroot, parentmodule, displayname, ordernumber) VALUES (nextval('SEQ_EG_MODULE'),'Receipt Services',true,null,(select id from eg_module where name='Collection Transaction'),'Receipt Services',2);
------------------END---------------------
-----------------START--------------------

------------------END---------------------
-----------------START--------------------
--DROP SEQUENCE seq_eg_role;

CREATE SEQUENCE seq_eg_role
    START WITH 17
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
    
--INSERT INTO eg_role(id, name, description, createddate, createdby, lastmodifiedby, lastmodifieddate, version) VALUES (nextval('seq_eg_role'),'Remitter','Remitter',to_timestamp('2015-08-15 11:04:23.846601','null'),1,1,to_timestamp('2015-08-15 11:04:23.846601','null'),null);
------------------END---------------------
-----------------START--------------------

------------------END---------------------
-------------------START-------------------

DROP SEQUENCE seq_eg_wf_types;

CREATE SEQUENCE seq_eg_wf_types
    START WITH 11
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
Insert into eg_wf_types (id,module,type,link,createdby,createddate,lastmodifiedby,lastmodifieddate,groupyn,typefqn,displayname,version) values (nextval('seq_eg_wf_types'),(select id from eg_module where name='Collection'),'ReceiptHeader','/collection/receipts/collectionsWorkflow-listWorkflow.action?inboxItemDetails=:ID',1,now(),1,now(), 'N', 'org.egov.collection.entity.ReceiptHeader', 'Collections Receipt Header', 0 );
-------------------END----------------------
-------------------START--------------------
Insert into EGW_STATUS (ID,MODULETYPE,DESCRIPTION,LASTMODIFIEDDATE,CODE,ORDER_ID) values (nextval('SEQ_EGW_STATUS'),'ReceiptHeader','To Be Submitted',to_date('22-11-09','DD-MM-RR'),'TO_BE_SUBMITTED',1);
Insert into EGW_STATUS (ID,MODULETYPE,DESCRIPTION,LASTMODIFIEDDATE,CODE,ORDER_ID) values (nextval('SEQ_EGW_STATUS'),'ReceiptHeader','Submitted',to_date('22-11-09','DD-MM-RR'),'SUBMITTED',2);
Insert into EGW_STATUS (ID,MODULETYPE,DESCRIPTION,LASTMODIFIEDDATE,CODE,ORDER_ID) values (nextval('SEQ_EGW_STATUS'),'ReceiptHeader','Approved',to_date('22-11-09','DD-MM-RR'),'APPROVED',3);
Insert into EGW_STATUS (ID,MODULETYPE,DESCRIPTION,LASTMODIFIEDDATE,CODE,ORDER_ID) values (nextval('SEQ_EGW_STATUS'),'ReceiptHeader','Cancelled',to_date('22-11-09','DD-MM-RR'),'CANCELLED',4);
Insert into EGW_STATUS (ID,MODULETYPE,DESCRIPTION,LASTMODIFIEDDATE,CODE,ORDER_ID) values (nextval('SEQ_EGW_STATUS'),'ReceiptHeader','Pending',to_date('23-01-10','DD-MM-RR'),'PENDING',5);
Insert into EGW_STATUS (ID,MODULETYPE,DESCRIPTION,LASTMODIFIEDDATE,CODE,ORDER_ID) values (nextval('SEQ_EGW_STATUS'),'OnlinePayment','Pending',to_date('28-03-10','DD-MM-RR'),'ONLINE_STATUS_PENDING',1);
Insert into EGW_STATUS (ID,MODULETYPE,DESCRIPTION,LASTMODIFIEDDATE,CODE,ORDER_ID) values (nextval('SEQ_EGW_STATUS'),'OnlinePayment','Success',to_date('28-03-10','DD-MM-RR'),'ONLINE_STATUS_SUCCESS',2);
Insert into EGW_STATUS (ID,MODULETYPE,DESCRIPTION,LASTMODIFIEDDATE,CODE,ORDER_ID) values (nextval('SEQ_EGW_STATUS'),'OnlinePayment','Failure',to_date('28-03-10','DD-MM-RR'),'ONLINE_STATUS_FAILURE',3);
Insert into EGW_STATUS (ID,MODULETYPE,DESCRIPTION,LASTMODIFIEDDATE,CODE,ORDER_ID) values (nextval('SEQ_EGW_STATUS'),'OnlinePayment','To Be Refunded',to_date('28-03-10','DD-MM-RR'),'TO_BE_REFUNDED',4);
Insert into EGW_STATUS (ID,MODULETYPE,DESCRIPTION,LASTMODIFIEDDATE,CODE,ORDER_ID) values (nextval('SEQ_EGW_STATUS'),'OnlinePayment','Refunded',to_date('14-07-10','DD-MM-RR'),'ONLINE_STATUS_REFUNDED',4);
Insert into EGW_STATUS (ID,MODULETYPE,DESCRIPTION,LASTMODIFIEDDATE,CODE,ORDER_ID) values (nextval('SEQ_EGW_STATUS'),'ReceiptHeader','Remitted',to_date('23-01-10','DD-MM-RR'),'REMITTED',6);
Insert into EGW_STATUS (ID,MODULETYPE,DESCRIPTION,LASTMODIFIEDDATE,CODE,ORDER_ID) values (nextval('SEQ_EGW_STATUS'),'ReceiptHeader','Instrument Bounced',to_date('23-01-10','DD-MM-RR'),'INSTR_BOUNCED',7);

--------------------END-----------------------
--------------------START---------------------
INSERT INTO EGF_INSTRUMENTACCOUNTCODES (ID, TYPEID, GLCODEID, CREATEDBY, LASTMODIFIEDBY, CREATEDDATE, LASTMODIFIEDDATE) VALUES (nextval('SEQ_EGF_INSTRUMENTACCOUNTCODES'),(SELECT ID FROM EGF_INSTRUMENTTYPE WHERE TYPE='cash'),(SELECT ID FROM CHARTOFACCOUNTS WHERE GLCODE='4501001'),(select id from eg_user where username='egovernments'),(select id from eg_user where username='egovernments'),current_timestamp,current_timestamp);
INSERT INTO EGF_INSTRUMENTACCOUNTCODES (ID, TYPEID, GLCODEID, CREATEDBY, LASTMODIFIEDBY, CREATEDDATE, LASTMODIFIEDDATE) VALUES (nextval('SEQ_EGF_INSTRUMENTACCOUNTCODES'),(SELECT ID FROM EGF_INSTRUMENTTYPE WHERE TYPE='cheque'),(SELECT ID FROM CHARTOFACCOUNTS WHERE GLCODE='4501051'),(select id from eg_user where username='egovernments'),(select id from eg_user where username='egovernments'),current_timestamp,current_timestamp);
INSERT INTO EGF_INSTRUMENTACCOUNTCODES (ID, TYPEID, GLCODEID, CREATEDBY, LASTMODIFIEDBY, CREATEDDATE, LASTMODIFIEDDATE) VALUES (nextval('SEQ_EGF_INSTRUMENTACCOUNTCODES'),(SELECT ID FROM EGF_INSTRUMENTTYPE WHERE TYPE='dd'),(SELECT ID FROM CHARTOFACCOUNTS WHERE GLCODE='4501051'),(select id from eg_user where username='egovernments'),(select id from eg_user where username='egovernments'),current_timestamp,current_timestamp);
INSERT INTO EGF_INSTRUMENTACCOUNTCODES (ID, TYPEID, GLCODEID, CREATEDBY, LASTMODIFIEDBY, CREATEDDATE, LASTMODIFIEDDATE) VALUES (nextval('SEQ_EGF_INSTRUMENTACCOUNTCODES'),(SELECT ID FROM EGF_INSTRUMENTTYPE WHERE TYPE='online'),(SELECT ID FROM CHARTOFACCOUNTS WHERE GLCODE='4501002'),(select id from eg_user where username='egovernments'),(select id from eg_user where username='egovernments'),current_timestamp,current_timestamp);
----------------------END----------------------

------------------START------------------
INSERT INTO eg_appconfig ( ID, KEY_NAME, DESCRIPTION, VERSION, MODULE ) VALUES (nextval('SEQ_EG_APPCONFIG'), 'COLLECTIONDEPARTMENTFORWORKFLOW', 'Department for Workflow',0, (select id from eg_module where name='Collection')); 
INSERT INTO eg_appconfig ( ID, KEY_NAME, DESCRIPTION, MODULE ) VALUES ( nextval('SEQ_EG_APPCONFIG'), 'MANUALRECEIPTINFOREQUIRED','Manual receipt information required',(select id from eg_module where name='Collection'));
Insert into eg_appconfig_values (ID,KEY_ID,EFFECTIVE_FROM,VALUE) values (nextval('seq_eg_appconfig_values'),(select id from eg_appconfig where KEY_NAME ='MANUALRECEIPTINFOREQUIRED'),to_date('23-09-09','DD-MM-RR'),'Y');
INSERT INTO eg_appconfig ( ID, KEY_NAME, DESCRIPTION, MODULE ) VALUES ( nextval('SEQ_EG_APPCONFIG'), 'BILLINGSERVICEPAYMENTGATEWAY','Get_Billing_Service_Payment_Gateway',(select id from eg_module where name='Collection'));
INSERT into eg_appconfig_values (ID,KEY_ID,EFFECTIVE_FROM,VALUE,VERSION) values (nextval('seq_eg_appconfig_values'),(select id from eg_appconfig where KEY_NAME ='BILLINGSERVICEPAYMENTGATEWAY'),to_date('23-09-09','DD-MM-RR'),'PT|AXIS',0);
INSERT INTO eg_appconfig_values ( ID, KEY_ID, EFFECTIVE_FROM, VALUE, VERSION ) VALUES (nextval('SEQ_EG_APPCONFIG_VALUES'),(SELECT id FROM EG_APPCONFIG WHERE KEY_NAME='COLLECTIONDEPARTMENTFORWORKFLOW' AND MODULE =(select id from eg_module where name='Collection')),current_date, 'Revenue',0);
INSERT INTO eg_appconfig ( ID, KEY_NAME, DESCRIPTION, VERSION, MODULE ) VALUES (nextval('SEQ_EG_APPCONFIG'), 'COLLECTIONROLEFORNONEMPLOYEE','roles for Collection workflow',0, (select id from eg_module where name='Collection')); 
INSERT INTO eg_appconfig_values ( ID, KEY_ID, EFFECTIVE_FROM, VALUE, VERSION ) VALUES (nextval('SEQ_EG_APPCONFIG_VALUES'),(SELECT id FROM EG_APPCONFIG WHERE KEY_NAME='COLLECTIONROLEFORNONEMPLOYEE'),current_date, 'CSC Operator',0);
INSERT INTO eg_appconfig ( ID, KEY_NAME, DESCRIPTION, VERSION, MODULE ) VALUES (nextval('SEQ_EG_APPCONFIG'), 'COLLECTIONDESIGNATIONFORCSCOPERATORASCLERK','Designation for Collection workflow',0, (select id from eg_module where name='Collection')); 
INSERT INTO eg_appconfig_values ( ID, KEY_ID, EFFECTIVE_FROM, VALUE, VERSION ) VALUES (nextval('SEQ_EG_APPCONFIG_VALUES'),(SELECT id FROM EG_APPCONFIG WHERE KEY_NAME='COLLECTIONDESIGNATIONFORCSCOPERATORASCLERK'),current_date, 'Revenue Clerk',0);
INSERT INTO eg_appconfig ( ID, KEY_NAME, DESCRIPTION, MODULE ) VALUES ( nextval('SEQ_EG_APPCONFIG'), 'CREATEVOUCHER_FOR_REMITTANCE','Create Voucher for Remittance',(select id from eg_module where name='Collection'));
INSERT into eg_appconfig_values (ID,KEY_ID,EFFECTIVE_FROM,VALUE) values (nextval('seq_eg_appconfig_values'),(select id from eg_appconfig where KEY_NAME ='CREATEVOUCHER_FOR_REMITTANCE'),current_date,'N');
INSERT INTO eg_appconfig ( ID, KEY_NAME, DESCRIPTION, MODULE ) VALUES ( nextval('SEQ_EG_APPCONFIG'), 'REMITTANCEVOUCHERTYPEFORCHEQUEDDCARD','Remittance Voucher Type for Cheque,DD and Card',(select id from eg_module where name='Collection'));
INSERT into eg_appconfig_values (ID,KEY_ID,EFFECTIVE_FROM,VALUE) values (nextval('seq_eg_appconfig_values'),(select id from eg_appconfig where KEY_NAME ='REMITTANCEVOUCHERTYPEFORCHEQUEDDCARD'),current_date,'Contra');
INSERT INTO eg_appconfig ( ID, KEY_NAME, DESCRIPTION, MODULE ) VALUES ( nextval('SEQ_EG_APPCONFIG'), 'USERECEIPTDATEFORCONTRA','Use Receipt Voucher Date for Contra Voucher',(select id from eg_module where name='Collection'));
INSERT into eg_appconfig_values (ID,KEY_ID,EFFECTIVE_FROM,VALUE) values (nextval('seq_eg_appconfig_values'),(select id from eg_appconfig where KEY_NAME ='USERECEIPTDATEFORCONTRA'),current_date,'N');

-------------------END---------------------