CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE vouchers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    discount_percent INT NOT NULL,
    quantity INT NOT NULL,
    expired_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_vouchers_code UNIQUE (code),
    CONSTRAINT chk_vouchers_discount_percent CHECK (discount_percent BETWEEN 1 AND 100),
    CONSTRAINT chk_vouchers_quantity CHECK (quantity >= 0),
    CONSTRAINT chk_vouchers_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE TABLE voucher_usages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    voucher_id BIGINT NOT NULL,
    used_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_voucher_usages_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_voucher_usages_voucher FOREIGN KEY (voucher_id) REFERENCES vouchers (id)
);

CREATE INDEX idx_voucher_usages_user_id ON voucher_usages (user_id);
CREATE INDEX idx_voucher_usages_voucher_id ON voucher_usages (voucher_id);
