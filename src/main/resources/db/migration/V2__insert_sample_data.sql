INSERT INTO users (full_name, email, phone)
VALUES
    ('Nguyen Van A', 'a@gmail.com', NULL),
    ('Tran Thi B', 'b@gmail.com', NULL);

INSERT INTO vouchers (code, discount_percent, quantity, expired_date, status)
VALUES
    ('SALE10', 10, 100, DATE_ADD(CURRENT_DATE, INTERVAL 90 DAY), 'ACTIVE'),
    ('SALE50', 50, 10, DATE_ADD(CURRENT_DATE, INTERVAL 90 DAY), 'ACTIVE');
