-- Authorities
INSERT INTO authorities (description) VALUES ('BENUTZER');
INSERT INTO authorities (description) VALUES ('ORGANISATOR');
INSERT INTO authorities (description) VALUES ('ADMIN');

-- Test users for each role
INSERT INTO users (email, username, password, active) VALUES ('user@gmail.com', 'user', '$2a$12$dFswRWOmIRet32bS91WWMOYXT1eyHI8FUezDks/bq67LlC6tGIzUu', 1);
INSERT INTO users (email, username, password, active) VALUES ('organizer@gmail.com', 'organizer', '$2a$12$Xu5tpzRwH/kMc591xjxVJOwr1jCKMl57Aq0MrEau2PvyNhn.3XIre', 1);
INSERT INTO users (email, username, password, active) VALUES ('admin@gmail.com', 'admin', '$2a$12$8MRcgh3lTl2xjHFc5X1MvO8XH2MHjTBsPQW1Sx.N4CvE1eMlEp7Mq', 1);
INSERT INTO users (email, username, password, active) VALUES ('user2@gmail.com', 'user2', '$2b$12$Z6Gf/tv5QL0gTRjUWYtOVe4XUEOrs9O3.HWpvt9hUbVOxbmkzf/AW', 1);

-- Add user roles
INSERT INTO user_authorities(user_id, authority_id) VALUES (1, 1);
INSERT INTO user_authorities(user_id, authority_id) VALUES (2, 2);
INSERT INTO user_authorities(user_id, authority_id) VALUES (3, 3);
INSERT INTO user_authorities(user_id, authority_id) VALUES (4, 1);

-- Add friendships
INSERT INTO friendships (requestor_id, addressee_id, status, created_at, accepted_at) VALUES (1, 2, 'ACCEPTED', NOW() - 20, NOW() - 10);
INSERT INTO friendships (requestor_id, addressee_id, status, created_at, accepted_at) VALUES (1, 2, 'PENDING', NOW() - 5, null); 
INSERT INTO friendships (requestor_id, addressee_id, status, created_at, accepted_at) VALUES (2, 1, 'PENDING', NOW(), null);  
INSERT INTO friendships (requestor_id, addressee_id, status, created_at, accepted_at) VALUES (1, 4, 'ACCEPTED', NOW() - 2, NOW() - 1);

-- Add chat rooms
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('GROUP', 'Mein erster Chatroom', NOW(), null, 1);

-- Add chat memberships
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (1, 1, NOW(), 'CHATADMIN');
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (1, 2, NOW(), 'MEMBER');

-- Add chat messages
INSERT INTO chat_messages (chatroom_id, sender_id, message, sent_at) VALUES (1, 1, 'Hallo. Wie geht es dir?', NOW());
INSERT INTO chat_messages (chatroom_id, sender_id, message, sent_at) VALUES (1, 2, 'Servus. Mir geht es gut. Dir?', NOW());

-- Add events and their chatrooms
INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Frühstück & Netzwerken', 'München', 90, 40, 'Kaffee, Brötchen und Austausch.', NOW() + 1, 0.0, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW() + 1, 1, 2);
UPDATE events SET chatroom_id = 2 WHERE id = 1;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (2, 2, NOW() + 1, 'CHATADMIN');

INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Käseverkostung am See', 'Konstanz', 120, 25, 'Regionale Sorten und Weinprobe.', NOW() + 2, 18.50, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW() + 2, 2, 2);
UPDATE events SET chatroom_id = 3 WHERE id = 2;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (3, 2, NOW() + 2, 'CHATADMIN');

INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Yoga im Grünen', 'Regensburg', 60, 30, 'Sanftes Flow-Training im Park.', NOW() + 3, 0.0, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW() + 3, 3, 2);
UPDATE events SET chatroom_id = 4 WHERE id = 3;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (4, 2, NOW() + 3, 'CHATADMIN');

INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Bücherbörse für Studierende', 'Nürnberg', 180, 80, 'Tausch und Verkauf gebrauchter Bücher.', NOW() + 4, 0.0, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW() + 4, 4, 2);
UPDATE events SET chatroom_id = 5 WHERE id = 4;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (5, 2, NOW() + 4, 'CHATADMIN');

INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Kreativ-Workshop: Öl & Acryl', 'Augsburg', 150, 20, 'Malen mit Öl- und Acrylfarben.', NOW() + 5, 25.00, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW() + 5, 5, 2);
UPDATE events SET chatroom_id = 6 WHERE id = 5;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (6, 2, NOW() + 5, 'CHATADMIN');

INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Konzertabend: Düster & Laut', 'Würzburg', 200, 120, 'Lokale Bands mit kräftigem Sound.', NOW() + 6, 12.00, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW() + 6, 6, 2);
UPDATE events SET chatroom_id = 7 WHERE id = 6;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (7, 2, NOW() + 6, 'CHATADMIN');

INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Stadtführung Altstadt', 'Bamberg', 90, 35, 'Geschichte, Gassen und gute Laune.', NOW() + 7, 8.00, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW() + 7, 7, 2);
UPDATE events SET chatroom_id = 8 WHERE id = 7;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (8, 2, NOW() + 7, 'CHATADMIN');

INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Sonnenuntergang am Fluss', 'Passau', 60, 50, 'Gemeinsamer Spaziergang mit Ausblick.', NOW() + 8, 0.0, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW() + 8, 8, 2);
UPDATE events SET chatroom_id = 9 WHERE id = 8;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (9, 2, NOW() + 8, 'CHATADMIN');

INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Spieleabend mit Würfeln', 'Ingolstadt', 180, 40, 'Brettspiele, Würfelspiele, gute Stimmung.', NOW() + 9, 5.00, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW() + 9, 9, 2);
UPDATE events SET chatroom_id = 10 WHERE id = 9;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (10, 2, NOW() + 9, 'CHATADMIN');

INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Fotowalk für Anfänger', 'Landshut', 120, 25, 'Grundlagen und Tipps unterwegs.', NOW() + 10, 0.0, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW() + 10, 10, 2);
UPDATE events SET chatroom_id = 11 WHERE id = 10;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (11, 2, NOW() + 10, 'CHATADMIN');

INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Kaffee & Kuchen: Omas Rezepte', 'Erlangen', 90, 30, 'Hausgemachte Klassiker zum Probieren.', NOW() + 11, 6.50, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW() + 11, 11, 2);
UPDATE events SET chatroom_id = 12 WHERE id = 11;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (12, 2, NOW() + 11, 'CHATADMIN');

INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Open-Air-Kino', 'Fürth', 150, 100, 'Sommerfilm im Freien.', NOW() + 12, 9.00, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW() + 12, 12, 2);
UPDATE events SET chatroom_id = 13 WHERE id = 12;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (13, 2, NOW() + 12, 'CHATADMIN');

INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Laufgruppe: 5km', 'Rosenheim', 60, 60, 'Gemeinsam laufen, Tempo 6:00.', NOW() + 13, 0.0, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW() + 13, 13, 2);
UPDATE events SET chatroom_id = 14 WHERE id = 13;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (14, 2, NOW() + 13, 'CHATADMIN');

INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Radltour an der Donau', 'Deggendorf', 180, 40, 'Entspannte Tour mit Pausen.', NOW() + 14, 0.0, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW() + 14, 14, 2);
UPDATE events SET chatroom_id = 15 WHERE id = 14;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (15, 2, NOW() + 14, 'CHATADMIN');

INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Kochkurs: Süß & Herzhaft', 'Weiden', 160, 18, 'Kochen mit regionalen Zutaten.', NOW() + 15, 30.00, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW() + 15, 15, 2);
UPDATE events SET chatroom_id = 16 WHERE id = 15;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (16, 2, NOW() + 15, 'CHATADMIN');

INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Science Slam', 'Bayreuth', 120, 150, 'Forschung kurz und witzig.', NOW() + 16, 10.00, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW() + 16, 16, 2);
UPDATE events SET chatroom_id = 17 WHERE id = 16;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (17, 2, NOW() + 16, 'CHATADMIN');

INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Poetry Night', 'Hof', 90, 70, 'Gedichte und Texte von lokalen Autor:innen.', NOW() + 17, 0.0, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW() + 17, 17, 2);
UPDATE events SET chatroom_id = 18 WHERE id = 17;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (18, 2, NOW() + 17, 'CHATADMIN');

INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Töpfern für Einsteiger', 'Cham', 140, 16, 'Ton, Drehscheibe und Geduld.', NOW() + 18, 22.00, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW() + 18, 18, 2);
UPDATE events SET chatroom_id = 19 WHERE id = 18;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (19, 2, NOW() + 18, 'CHATADMIN');

INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Klettertreff', 'Amberg', 120, 20, 'Bouldern für alle Levels.', NOW() + 19, 7.00, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW() + 19, 19, 2);
UPDATE events SET chatroom_id = 20 WHERE id = 19;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (20, 2, NOW() + 19, 'CHATADMIN');

INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Winterwanderung mit Hüttenstopp', 'Garmisch-Partenkirchen', 240, 30, 'Schnee, Aussicht und heiße Getränke.', NOW() + 20, 0.0, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW() + 20, 20, 2);
UPDATE events SET chatroom_id = 21 WHERE id = 20;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (21, 2, NOW() + 20, 'CHATADMIN');

-- Add event participant organizer as creator of event
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (1, 2, TRUE, NOW());
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (2, 2, TRUE, NOW());
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (3, 2, TRUE, NOW());
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (4, 2, TRUE, NOW());
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (5, 2, TRUE, NOW());
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (6, 2, TRUE, NOW());
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (7, 2, TRUE, NOW());
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (8, 2, TRUE, NOW());
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (9, 2, TRUE, NOW());
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (10, 2, TRUE, NOW());
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (11, 2, TRUE, NOW());
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (12, 2, TRUE, NOW());
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (13, 2, TRUE, NOW());
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (14, 2, TRUE, NOW());
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (15, 2, TRUE, NOW());
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (16, 2, TRUE, NOW());
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (17, 2, TRUE, NOW());
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (18, 2, TRUE, NOW());
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (19, 2, TRUE, NOW());
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (20, 2, TRUE, NOW());

-- Add payments
INSERT INTO payments (user_id, event_id, amount, status, timestamp, paypal_transaction_id) VALUES (1, 1, 99.99, 'COMPLETED', NOW()-1, 'PAYPAL-1234');
INSERT INTO payments (user_id, event_id, amount, status, timestamp, paypal_transaction_id) VALUES (1, 1, 49.95, 'COMPLETED', NOW()-2, 'PAYPAL-4321');
INSERT INTO payments (user_id, event_id, amount, status, timestamp, paypal_transaction_id) VALUES (1, 1, 10, 'FAILED', NOW()-3, 'PAYPAL-0000');
