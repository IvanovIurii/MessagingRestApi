CREATE TABLE messages (
  id UUID DEFAULT gen_random_uuid(),
  sender_id UUID NOT NULL,
  recipient_id UUID NOT NULL,
  message VARCHAR NOT NULL,
  timestamp TIMESTAMP default CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_sender FOREIGN KEY(sender_id) REFERENCES users(id),
  CONSTRAINT fk_recipient FOREIGN KEY(recipient_id) REFERENCES users(id)
);