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

-- Add friendships
INSERT INTO friendships (requestor_id, addressee_id, status, created_at, accepted_at) VALUES (1, 2, 'ACCEPTED', NOW() - 20, NOW() - 10);
INSERT INTO friendships (requestor_id, addressee_id, status, created_at, accepted_at) VALUES (1, 2, 'PENDING', NOW() - 5, null); 
INSERT INTO friendships (requestor_id, addressee_id, status, created_at, accepted_at) VALUES (2, 1, 'PENDING', NOW(), null);  

-- Add chat rooms
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('GROUP', 'Mein erster Chatroom', NOW(), null, 1);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW(), null, 1);

-- Add chat memberships
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (1, 1, NOW(), 'CHATADMIN');
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (1, 2, NOW(), 'MEMBER');

-- Add chat messages
INSERT INTO chat_messages (chatroom_id, sender_id, message, sent_at) VALUES (1, 1, 'Hallo. Wie geht es dir?', NOW());
INSERT INTO chat_messages (chatroom_id, sender_id, message, sent_at) VALUES (1, 2, 'Servus. Mir geht es gut. Dir?', NOW());

-- Add events
INSERT INTO events (name, event_time, costs, chatroom_id) VALUES ('Testevent', NOW() + 7, 99.99, 2);
UPDATE chat_rooms SET event_id = 1 WHERE id = 2;

-- Add payments
INSERT INTO payments (user_id, event_id, amount, status, timestamp, paypal_transaction_id) VALUES (1, 1, 99.99, 'COMPLETED', NOW()-1, 'PAYPAL-1234');
INSERT INTO payments (user_id, event_id, amount, status, timestamp, paypal_transaction_id) VALUES (1, 1, 49.95, 'COMPLETED', NOW()-2, 'PAYPAL-4321');
INSERT INTO payments (user_id, event_id, amount, status, timestamp, paypal_transaction_id) VALUES (1, 1, 10, 'FAILED', NOW()-3, 'PAYPAL-0000');