CREATE TABLE IF NOT EXISTS book_outbox
(
    id         bigserial primary key not null,
    data       jsonb                 not null,
    data_op    text                  not null,
    created_at timestamptz default now()
);

-- Change text to jsonb if needed using:
-- alter table book_outbox alter column data type jsonb using cast(data as jsonb);

