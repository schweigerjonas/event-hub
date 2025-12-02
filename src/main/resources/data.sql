-- Authorities
INSERT INTO authorities (description) VALUES ('BENUTZER');
INSERT INTO authorities (description) VALUES ('ORGANISATOR');
INSERT INTO authorities (description) VALUES ('ADMIN');

-- Test users for each role
INSERT INTO users (email, username, password, active) VALUES ('user@gmail.com', 'user', '$2a$12$dFswRWOmIRet32bS91WWMOYXT1eyHI8FUezDks/bq67LlC6tGIzUu', 1);
INSERT INTO users (email, username, password, active) VALUES ('organizer@gmail.com', 'organizer', '$2a$12$Xu5tpzRwH/kMc591xjxVJOwr1jCKMl57Aq0MrEau2PvyNhn.3XIre', 1);
INSERT INTO users (email, username, password, active) VALUES ('admin@gmail.com', 'admin', '$2a$12$8MRcgh3lTl2xjHFc5X1MvO8XH2MHjTBsPQW1Sx.N4CvE1eMlEp7Mq', 1);

-- Add user roles
INSERT INTO user_authorities(user_id, authority_id) VALUES (1, 1);
INSERT INTO user_authorities(user_id, authority_id) VALUES (2, 2);
INSERT INTO user_authorities(user_id, authority_id) VALUES (3, 3);