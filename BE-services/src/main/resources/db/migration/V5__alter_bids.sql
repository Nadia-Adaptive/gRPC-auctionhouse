ALTER TABLE IF EXISTS bids ADD COLUMN IF NOT EXISTS updated_at timestamp with time zone DEFAULT current_timestamp;

ALTER TABLE IF EXISTS bids RENAME COLUMN createdAt to created_at;
ALTER TABLE IF EXISTS bids RENAME COLUMN quantityFilled to quantity_filled;
ALTER TABLE IF EXISTS bids RENAME COLUMN id to bid_id;
ALTER TABLE IF EXISTS bids RENAME COLUMN auctionId to auction_id;
ALTER TABLE IF EXISTS bids RENAME COLUMN bidderId to bidder_id;
ALTER TABLE IF EXISTS bids RENAME COLUMN offerPrice to offer_price;