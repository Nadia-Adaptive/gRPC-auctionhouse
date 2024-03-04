CREATE TABLE IF not exists auctions
(
    auction_id   SERIAL      NOT NULL PRIMARY KEY,
    owner_id int NOT NULL,
    quantity int NOT NULL,
    min_price numeric NOT NULL,
    total_quantity_sold int,
    product varchar(50),
    total_revenue varchar(50),
    status varchar(50) CHECK(status in ('OPEN', 'CLOSED')),
    closed_at timestamp with time zone,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);
