CREATE EXTENSION IF NOT EXISTS citext;

create table users(
                      username citext not null primary key,
                      password varchar(500) not null,
                      enabled boolean not null
);

create table authorities (
                             username citext not null,
                             authority citext not null,
                             constraint fk_authorities_users foreign key(username) references users(username)
);

create unique index ix_auth_username on authorities (username,authority);

-- Group Schema
-- Groups table
CREATE TABLE groups (
                        id SERIAL PRIMARY KEY, -- Use SERIAL for auto-incrementing ID
                        group_name CITEXT NOT NULL UNIQUE -- CITEXT for case-insensitive names
);

-- Group Authorities table
CREATE TABLE group_authorities (
                                   group_id BIGINT NOT NULL,
                                   authority VARCHAR(50) NOT NULL,
                                   CONSTRAINT fk_group_authorities_group FOREIGN KEY (group_id) REFERENCES groups(id)
);

-- Group Members table
CREATE TABLE group_members (
                               id SERIAL PRIMARY KEY,
                               username VARCHAR(50) NOT NULL,
                               group_id BIGINT NOT NULL,
                               CONSTRAINT fk_group_members_group FOREIGN KEY (group_id) REFERENCES groups(id)
);
