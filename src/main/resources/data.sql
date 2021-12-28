INSERT INTO customer (customerId, firstName, lastName, pan) VALUES
  ('CUST0001', 'Harry', 'Potter', 'SDFHK2233E'),
  ('CUST0002', 'Steve', 'Rogers', 'FGSDD4455O'),
  ('CUST0003', 'Samuel', 'Sonny', 'FSDSG7788F');
  
  
  
INSERT INTO BANKACCOUNT (accountNumber, customer, balance, accountType, accountStatus) VALUES
  ('ACC00001', 'CUST0001', 4477.0, 'SAVINGS_ACCOUNT', 'ACTIVE'),
  ('ACC00002', 'CUST0002', 88000.0, 'SAVINGS_ACCOUNT', 'ACTIVE'),
  ('ACC00003', 'CUST0003', 567.88, 'SAVINGS_ACCOUNT', 'ACTIVE'),
  ('ACC00004', 'CUST0001', 346790.67, 'SAVINGS_ACCOUNT', 'ACTIVE');