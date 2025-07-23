CREATE TABLE IF NOT EXISTS purchase (
    id SERIAL PRIMARY KEY,
    data_criacao DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    tipo_pagamento VARCHAR(20) NOT NULL,
    valor DECIMAL(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS item_purchase (
    id SERIAL PRIMARY KEY,
    purchase_id INTEGER REFERENCES purchase(id) ON DELETE CASCADE,
    product_id INTEGER REFERENCES product(id),
    quantidade INTEGER NOT NULL,
    subvalor DECIMAL(10, 2) NOT NULL
);
