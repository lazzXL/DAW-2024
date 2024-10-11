-- Create the schema dbo
CREATE SCHEMA IF NOT EXISTS dbo;

-- Create table for users in the dbo schema
CREATE TABLE dbo.users
(
    id    SERIAL PRIMARY KEY,
    name  VARCHAR(255)        NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    token VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Create table for channels in the dbo schema
CREATE TABLE dbo.channels
(
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(255) UNIQUE NOT NULL,
    description     TEXT,
    admin_id        INT NOT NULL,
    visibility      VARCHAR(10)  NOT NULL CHECK (visibility IN ('PUBLIC', 'PRIVATE')),
    FOREIGN KEY (admin) REFERENCES dbo.users (id)
);

-- Create table for time slots in the dbo schema
CREATE TABLE dbo.messages
(
    id                  SERIAL PRIMARY KEY,
    content             TEXT,
    date_sent           TIMESTAMP   NOT NULL,
    sender_id           INT         NOT NULL,
    FOREIGN KEY (sender_id) REFERENCES dbo.participants (id)
);

-- Create table for invitations in the dbo schema
CREATE TABLE dbo.invitations
(
    id              SERIAL PRIMARY KEY,
    code            VARCHAR(255) UNIQUE NOT NULL,
    channel_id      INT NOT NULL,
    permission      VARCHAR(10) NOT NULL CHECK (permission IN ('READ-ONLY', 'READ-WRITE')),
    FOREIGN KEY (channel_id) REFERENCES dbo.channels (id)
);

-- Create table for participants in the dbo schema
CREATE TABLE dbo.participants
(
    id          SERIAL PRIMARY KEY,
    user_id     INT NOT NULL,
    channel_id  INT NOT NULL,
    permission  VARCHAR(10) NOT NULL CHECK (permission IN ('READ-ONLY', 'READ-WRITE')),
    FOREIGN KEY (user_id) REFERENCES dbo.users (id),
    FOREIGN KEY (channel_id) REFERENCES dbo.channels (id)
);
