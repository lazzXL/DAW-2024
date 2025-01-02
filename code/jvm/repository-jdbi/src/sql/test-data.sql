INSERT INTO new.users (name, email, password)
VALUES
('Alice', 'alice@example.com', 'password123'),
('Bob', 'bob@example.com', 'securepassword'),
('Charlie', 'charlie@example.com', 'mypassword');

INSERT INTO new.channels (name, description, admin_id, visibility)
VALUES
('General', 'A general-purpose channel', 1, 'PUBLIC'),
('Private Club', 'For exclusive members only', 2, 'PRIVATE');

INSERT INTO new.channel_invitations (code, channel_id, permission)
VALUES
('b2342ec8-0a8f-4e8f-8c55-8cf7bca9143a', 1, 'READ_WRITE'),
('e914a36d-5f2a-442b-a704-0db1a2ef2f4a', 2, 'READ_ONLY');

INSERT INTO new.register_invitations (code)
VALUES
('bfc6a5dc-6e9b-4048-b6b2-8f5d5c3e9292'),
('9efabeec-7ebc-47c4-a4e8-c4e9b4c476db');

INSERT INTO new.participants (user_id, channel_id, permission, is_active)
VALUES
(1, 1, 'READ_WRITE', TRUE),
(2, 1, 'READ_ONLY', TRUE),
(3, 2, 'READ_WRITE', FALSE);

INSERT INTO new.messages (content, date_sent, sender_id)
VALUES
('Hello, world!', '2025-01-01 10:00:00', 1),
('Welcome to the channel!', '2025-01-01 10:05:00', 2),
('This is a private message.', '2025-01-01 10:10:00', 3);

INSERT INTO new.tokens (token_validation, user_id, created_at, last_used_at)
VALUES
('de305d54-75b4-431b-adb2-eb6b9e546013', 1, 1704063600, 1704067200),
('ba7dc25f-7c89-4d77-82a4-e6e9d8d1c4db', 2, 1704063600, 1704065400);
