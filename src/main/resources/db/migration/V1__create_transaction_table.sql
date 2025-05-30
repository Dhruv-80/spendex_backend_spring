-- Create transaction table
CREATE TABLE transaction (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    bank VARCHAR(255),
    amount DOUBLE PRECISION,
    date DATE,
    status VARCHAR(50),
    category VARCHAR(100),
    transaction_type VARCHAR(10)
); 