ALTER TABLE beds
DROP CONSTRAINT chk_beds_monthly_rent;

ALTER TABLE beds
ADD CONSTRAINT chk_beds_monthly_rent
CHECK (monthly_rent BETWEEN 1000 AND 100000);