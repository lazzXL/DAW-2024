-- Populate dbo.users table
INSERT INTO dbo.users (name, email, token, password)
VALUES
    ('Alice Johnson', 'alice@example.com', 'token123', 'password1'),
    ('Bob Smith', 'bob@example.com', 'token124', 'password2'),
    ('Charlie Brown', 'charlie@example.com', 'token125', 'password3'),
    ('Dana White', 'dana@example.com', 'token126', 'password4');

-- Populate dbo.channels table
INSERT INTO dbo.channels (name, description, admin_id, visibility)
VALUES
    ('General', 'General discussion channel', 1, 'PUBLIC'),
    ('Private Talks', 'Private discussion among admins', 2, 'PRIVATE'),
    ('Tech Support', 'Channel for tech support inquiries', 3, 'PUBLIC'),
    ('Team Updates', 'Updates for the project team', 1, 'PRIVATE');

-- Populate dbo.invitations table
INSERT INTO dbo.invitations (code, channel_id, permission)
VALUES
    ('invitation123', 1, 'READ-WRITE'),
    ('invitation124', 2, 'READ-ONLY'),
    ('invitation125', 3, 'READ-WRITE'),
    ('invitation126', 4, 'READ-ONLY');

-- Populate dbo.participants table
INSERT INTO dbo.participants (user_id, channel_id, permission)
VALUES
    (1, 1, 'READ-WRITE'), -- Alice in General channel
    (2, 2, 'READ-WRITE'), -- Bob in Private Talks
    (3, 3, 'READ-WRITE'), -- Charlie in Tech Support
    (4, 4, 'READ-ONLY'),  -- Dana in Team Updates
    (1, 4, 'READ-WRITE'), -- Alice in Team Updates
    (3, 1, 'READ-ONLY');  -- Charlie in General channel

-- Populate dbo.messages table
INSERT INTO dbo.messages (content, date_sent, sender_id)
VALUES
    ('Hello everyone!', '2024-10-12 10:00:00', 1), -- Alice in General channel
    ('This is a private message', '2024-10-12 11:00:00', 2), -- Bob in Private Talks
    ('Can someone help with a tech issue?', '2024-10-12 12:00:00', 3), -- Charlie in Tech Support
    ('Project update: We are on track', '2024-10-12 13:00:00', 4); -- Dana in Team Updates