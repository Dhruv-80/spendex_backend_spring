-- Update existing records based on amount sign
UPDATE transaction 
SET transaction_type = CASE 
    WHEN amount < 0 THEN 'DEBIT'
    ELSE 'CREDIT'
END;

-- Make the column not null after updating existing records
ALTER TABLE transaction ALTER COLUMN transaction_type SET NOT NULL; 