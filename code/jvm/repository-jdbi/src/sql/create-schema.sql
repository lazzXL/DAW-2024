-- Create the schema new
CREATE SCHEMA IF NOT EXISTS new;

-- Create table for users in the new schema
CREATE TABLE new.users
(
    id    SERIAL PRIMARY KEY,
    name  VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Create table for channels in the new schema
CREATE TABLE new.channels
(
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(255) UNIQUE NOT NULL,
    description     TEXT,
    admin_id        INT NOT NULL,
    visibility      VARCHAR(10)  NOT NULL CHECK (visibility IN ('PUBLIC', 'PRIVATE')),
    FOREIGN KEY (admin_id) REFERENCES new.users (id)
);

-- Create table for channel invitations in the new schema
CREATE TABLE new.channel_invitations
(
    id              SERIAL PRIMARY KEY,
    code            VARCHAR(255) UNIQUE NOT NULL,
    channel_id      INT NOT NULL,
    permission      VARCHAR(10) NOT NULL CHECK (permission IN ('READ_ONLY', 'READ_WRITE')),
    FOREIGN KEY (channel_id) REFERENCES new.channels (id)
);

-- Create table for register invitations in the new schema
CREATE TABLE new.register_invitations
(
    id              SERIAL PRIMARY KEY,
    code            VARCHAR(255) UNIQUE NOT NULL
);

-- Create table for participants in the new schema
CREATE TABLE new.participants
(
    id          SERIAL PRIMARY KEY,
    user_id     INT NOT NULL,
    channel_id  INT NOT NULL,
    permission  VARCHAR(10) NOT NULL CHECK (permission IN ('READ_ONLY', 'READ_WRITE')),
    is_active   BOOLEAN NOT NULL,
    FOREIGN KEY (user_id) REFERENCES new.users (id),
    FOREIGN KEY (channel_id) REFERENCES new.channels (id)
);

-- Create table for time slots in the new schema
CREATE TABLE new.messages
(
    id                  SERIAL PRIMARY KEY,
    content             TEXT,
    date_sent           TIMESTAMP   NOT NULL,
    sender_id           INT         NOT NULL,
    FOREIGN KEY (sender_id) REFERENCES new.participants (id)
);

create table new.tokens
(
    token_validation VARCHAR(256) primary key,
    user_id          int references new.users (id),
    created_at       bigint not null,
    last_used_at     bigint not null
);

ALTER TABLE new.participants
ADD CONSTRAINT unique_user_channel UNIQUE (user_id, channel_id);

CREATE OR REPLACE FUNCTION activate_participant()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM new.participants
               WHERE user_id = NEW.user_id AND channel_id = NEW.channel_id) THEN
        UPDATE new.participants
        SET is_active = TRUE
        WHERE user_id = NEW.user_id AND channel_id = NEW.channel_id;

        RETURN NULL;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_activate_participant
BEFORE INSERT ON new.participants
FOR EACH ROW
EXECUTE FUNCTION activate_participant();
