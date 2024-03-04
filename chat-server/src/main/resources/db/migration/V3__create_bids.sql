CREATE TABLE IF not exists bids
(
    id   SERIAL      NOT NULL PRIMARY KEY,
    bidderId int NOT NULL,
    auctionId int NOT NULL,
    quantity int NOT NULL,
    offerPrice numeric NOT NULL,
    quantityFilled int,
    status varchar(50) CHECK(status in ('PENDING', 'FILLED', 'PARTIAL_FILL', 'UNFILLED')),
    createdAt timestamp not null
);
