CREATE TABLE IF NOT EXISTS orders
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name  VARCHAR(255)   NOT NULL,
    customer_email VARCHAR(255)   NOT NULL,
    order_date     TIMESTAMP      NOT NULL,
    status         VARCHAR(50)    NOT NULL,
    total_amount   DECIMAL(19, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS order_items
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id     BIGINT         NOT NULL,
    product_name VARCHAR(255)   NOT NULL,
    quantity     INTEGER        NOT NULL,
    unit_price   DECIMAL(19, 2) NOT NULL,
    subtotal     DECIMAL(19, 2) NOT NULL,

    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
            REFERENCES orders (id)
            ON DELETE CASCADE
);

INSERT INTO orders (customer_name,
                    customer_email,
                    order_date,
                    status,
                    total_amount)
VALUES ('João Silva',
        'joao@email.com',
        CURRENT_TIMESTAMP,
        'PENDING',
        54.90),
       ('Maria Oliveira',
        'maria@email.com',
        CURRENT_TIMESTAMP,
        'CONFIRMED',
        99.80),
       ('Carlos Souza',
        'carlos@email.com',
        CURRENT_TIMESTAMP,
        'DELIVERED',
        48.87),
       ('Ana Lima',
        'ana@email.com',
        CURRENT_TIMESTAMP,
        'CANCELLED',
        54.90),
       ('Pedro Santos',
        'pedro@email.com',
        CURRENT_TIMESTAMP,
        'CONFIRMED',
        29.30);

INSERT INTO order_items (order_id,
                         product_name,
                         quantity,
                         unit_price,
                         subtotal)
VALUES (1, 'Dipirona 1g', 2, 12.50, 25.00),

       (1, 'Vitamina C', 1, 29.90, 29.90),

       (2, 'Protetor Solar FPS 60', 1, 54.90, 54.90),

       (2, 'Shampoo Anticaspa', 2, 22.45, 44.90),

       (3, 'Ibuprofeno 600mg', 1, 18.90, 18.90),

       (3, 'Álcool em Gel 70%', 3, 9.99, 29.97),

       (4, 'Curativo Adesivo', 2, 7.50, 15.00),

       (4, 'Termômetro Digital', 1, 39.90, 39.90),

       (5, 'Omeprazol 20mg', 1, 16.80, 16.80),

       (5, 'Máscara Cirúrgica', 5, 2.50, 12.50);