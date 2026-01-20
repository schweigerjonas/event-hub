-- Authorities
INSERT INTO authorities (description) VALUES ('BENUTZER');
INSERT INTO authorities (description) VALUES ('ORGANISATOR');
INSERT INTO authorities (description) VALUES ('ADMIN');

-- Test users for each role
INSERT INTO users (email, username, password, active, is_using2fa, secret) VALUES ('user@gmail.com', 'user', '$2a$12$dFswRWOmIRet32bS91WWMOYXT1eyHI8FUezDks/bq67LlC6tGIzUu', 1, false, 'OVZWK4TTNF4HIZLFNZRWQYLSOM======');
INSERT INTO users (email, username, password, active, is_using2fa, secret) VALUES ('organizer@gmail.com', 'organizer', '$2a$12$Xu5tpzRwH/kMc591xjxVJOwr1jCKMl57Aq0MrEau2PvyNhn.3XIre', 1, false, 'N5ZGOYLONF5GK4TTNF4HIZLFNY======');
INSERT INTO users (email, username, password, active, is_using2fa, secret) VALUES ('admin@gmail.com', 'admin', '$2a$12$8MRcgh3lTl2xjHFc5X1MvO8XH2MHjTBsPQW1Sx.N4CvE1eMlEp7Mq', 1, false, 'MFSG22LOONUXQ5DFMVXGG2DBOI======');
INSERT INTO users (email, username, password, active, is_using2fa, secret) VALUES ('user2@gmail.com', 'user2', '$2b$12$Z6Gf/tv5QL0gTRjUWYtOVe4XUEOrs9O3.HWpvt9hUbVOxbmkzf/AW', 1, false, 'OVZWK4RSONUXQ5DFMVXGG2DBOI======');
INSERT INTO users (email, username, password, active, is_using2fa, secret) VALUES ('user3@gmail.com', 'user3', '$2a$12$dFswRWOmIRet32bS91WWMOYXT1eyHI8FUezDks/bq67LlC6tGIzUu', 1, false, 'OVZWK4RSONUXQ5DFMVXGG2DBOI======');
INSERT INTO users (email, username, password, active, is_using2fa, secret) VALUES ('max.mustermann@mail.com', 'maxmuster', '$2a$12$dFswRWOmIRet32bS91WWMOYXT1eyHI8FUezDks/bq67LlC6tGIzUu', 1, false, 'OVZWK4RSONUXQ5DFMVXGG2DBOI======');
INSERT INTO users (email, username, password, active, is_using2fa, secret) VALUES ('julia.schmidt@mail.com', 'schmidtjulia', '$2a$12$dFswRWOmIRet32bS91WWMOYXT1eyHI8FUezDks/bq67LlC6tGIzUu', 1, false, 'OVZWK4RSONUXQ5DFMVXGG2DBOI======');
INSERT INTO users (email, username, password, active, is_using2fa, secret) VALUES ('lukas.weber@mail.com', 'weberluk', '$2a$12$dFswRWOmIRet32bS91WWMOYXT1eyHI8FUezDks/bq67LlC6tGIzUu', 1, false, 'OVZWK4RSONUXQ5DFMVXGG2DBOI======');
INSERT INTO users (email, username, password, active, is_using2fa, secret) VALUES ('sarah.fischer@mail.com', 'sarahfischer', '$2a$12$dFswRWOmIRet32bS91WWMOYXT1eyHI8FUezDks/bq67LlC6tGIzUu', 1, false, 'OVZWK4RSONUXQ5DFMVXGG2DBOI======');
INSERT INTO users (email, username, password, active, is_using2fa, secret) VALUES ('thomas.mayer@mail.com', 'mayerthommy', '$2a$12$dFswRWOmIRet32bS91WWMOYXT1eyHI8FUezDks/bq67LlC6tGIzUu', 1, false, 'OVZWK4RSONUXQ5DFMVXGG2DBOI======');

-- Add user roles
INSERT INTO user_authorities(user_id, authority_id) VALUES (1, 1);
INSERT INTO user_authorities(user_id, authority_id) VALUES (2, 2);
INSERT INTO user_authorities(user_id, authority_id) VALUES (3, 3);
INSERT INTO user_authorities(user_id, authority_id) VALUES (4, 1);
INSERT INTO user_authorities(user_id, authority_id) VALUES (5, 1);
INSERT INTO user_authorities(user_id, authority_id) VALUES (6, 1);
INSERT INTO user_authorities(user_id, authority_id) VALUES (7, 1);
INSERT INTO user_authorities(user_id, authority_id) VALUES (8, 1);
INSERT INTO user_authorities(user_id, authority_id) VALUES (9, 1);
INSERT INTO user_authorities(user_id, authority_id) VALUES (10, 1);

-- Add notifications
INSERT INTO notifications (recipient_id, type, message, link, is_read, created_at) VALUES (1, 2, 'organizer hat dich zum Event "Frühstück & Netzwerken" eingeladen.', '/invitations', FALSE, NOW());
INSERT INTO notifications (recipient_id, type, message, link, is_read, created_at) VALUES (1, 1, 'organizer hat deine Freundschaftsanfrage angenommen.', '/friends/all', FALSE, NOW());
INSERT INTO notifications (recipient_id, type, message, link, is_read, created_at) VALUES (1, 3, 'Das Event "Spieleabend für Strategen" wurde leider abgesagt.', '/events', FALSE, NOW()-20);
INSERT INTO notifications (recipient_id, type, message, link, is_read, created_at) VALUES (5, 0, 'user hat dir eine Freundschaftsanfrage gesendet.', '/friends/all', FALSE, NOW()-10);

-- Add activities
INSERT INTO activities (actor_id, event_id, type, message, link, timestamp) VALUES (2, 2, 0, 'organizer hat ein Event erstellt: Käseverkostung am See', 'events/2', NOW() - 12);
INSERT INTO activities (actor_id, event_id, type, message, link, timestamp) VALUES (2, 3, 0, 'organizer hat ein Event erstellt: Yoga im Grünen', 'events/3', NOW() - 8);
INSERT INTO activities (actor_id, event_id, type, message, link, timestamp) VALUES (2, 99, 3, 'organizer hat sein Event abgesagt: Spieleabend für Strategen', '', NOW() - 4);
INSERT INTO activities (actor_id, event_id, type, message, link, timestamp) VALUES (1, 1, 4, 'user hat das Event "Frühstück & Netzwerken" mit 5 Sternen bewertet', 'events/1', NOW() - 2);

-- Add friendships
INSERT INTO friendships (requestor_id, addressee_id, status, created_at, accepted_at) VALUES (1, 2, 'ACCEPTED', NOW() - 20, NOW() - 10);
INSERT INTO friendships (requestor_id, addressee_id, status, created_at, accepted_at) VALUES (1, 5, 'PENDING', NOW() - 5, null); 
INSERT INTO friendships (requestor_id, addressee_id, status, created_at, accepted_at) VALUES (3, 1, 'PENDING', NOW(), null);  
INSERT INTO friendships (requestor_id, addressee_id, status, created_at, accepted_at) VALUES (1, 4, 'ACCEPTED', NOW() - 2, NOW() - 1);

-- Add chat rooms
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('GROUP', 'Mein erster Chatroom', NOW() - 15, null, 1);

-- Add chat memberships
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (1, 1, NOW() - 15, 'CHATADMIN');
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (1, 5, NOW() - 15, 'MEMBER');

-- Add chat messages
INSERT INTO chat_messages (chatroom_id, sender_id, message, sent_at, is_deleted) VALUES (1, 1, 'Hallo. Wie geht es dir?', DATEADD('MINUTE', -60, CURRENT_TIMESTAMP), FALSE);
INSERT INTO chat_messages (chatroom_id, sender_id, message, sent_at, is_deleted) VALUES (1, 5, 'Servus. Mir geht es gut. Dir?', DATEADD('MINUTE', -59, CURRENT_TIMESTAMP), FALSE);
INSERT INTO chat_messages (chatroom_id, sender_id, message, sent_at, is_deleted) VALUES (1, 1, 'Mir geht es auch gut. Mir is aber langweilig. Hast du Lust, dass wir uns für ein Event einschreiben?', DATEADD('MINUTE', -58, CURRENT_TIMESTAMP), FALSE);
INSERT INTO chat_messages (chatroom_id, sender_id, message, sent_at, is_deleted) VALUES (1, 5, 'Ja, gerne. An welches Event hättest du denn gedacht?', DATEADD('MINUTE', -57, CURRENT_TIMESTAMP), FALSE);
INSERT INTO chat_messages (chatroom_id, sender_id, message, sent_at, is_deleted) VALUES (1, 1, 'asdfgjweuirgwe', DATEADD('MINUTE', -56, CURRENT_TIMESTAMP), TRUE);
INSERT INTO chat_messages (chatroom_id, sender_id, message, sent_at, is_deleted) VALUES (1, 1, 'Sorry. Ich meine natürlich Frühstücken und Netzwerken.', DATEADD('MINUTE', -55, CURRENT_TIMESTAMP), FALSE);
INSERT INTO chat_messages (chatroom_id, sender_id, message, sent_at, is_deleted) VALUES (1, 5, 'Super. Dann tragen wir uns doch gleich da ein.', DATEADD('MINUTE', -54, CURRENT_TIMESTAMP), FALSE);
INSERT INTO chat_messages (chatroom_id, sender_id, message, sent_at, is_deleted) VALUES (1, 1, 'Ich trag mich auch gleich ein, nicht dass das Event bald voll ist...', DATEADD('MINUTE', -53, CURRENT_TIMESTAMP), FALSE);
INSERT INTO chat_messages (chatroom_id, sender_id, message, sent_at, is_deleted) VALUES (1, 5, 'Hast du noch Lust, auch an einem anderen Event teilzunehmen?', DATEADD('MINUTE', -52, CURRENT_TIMESTAMP), FALSE);
INSERT INTO chat_messages (chatroom_id, sender_id, message, sent_at, is_deleted) VALUES (1, 1, 'Lust hätte ich, aber leider keine Zeit, weil ich momentan viel zu tun hab :(', DATEADD('MINUTE', -51, CURRENT_TIMESTAMP), FALSE);
INSERT INTO chat_messages (chatroom_id, sender_id, message, sent_at, is_deleted) VALUES (1, 5, 'Schade, aber wir sehen uns ja dann bei Frühstücken und Netzwerken.', DATEADD('MINUTE', -50, CURRENT_TIMESTAMP), FALSE);
INSERT INTO chat_messages (chatroom_id, sender_id, message, sent_at, is_deleted) VALUES (1, 1, 'Bis dann!', DATEADD('MINUTE', -49, CURRENT_TIMESTAMP), FALSE);

-- Add events and their chatrooms
INSERT INTO events (name, location, latitude, longitude, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Frühstück & Netzwerken', 'München', 48.1351, 11.582, 90, 40, 'Kaffee, Brötchen und Austausch.', NOW() + 1, 0.0, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW(), 1, 2);
UPDATE events SET chatroom_id = 2 WHERE id = 1;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (2, 2, NOW(), 'CHATADMIN');

INSERT INTO events (name, location, latitude, longitude, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Käseverkostung am See', 'Konstanz', 47.6779, 9.1732, 120, 25, 'Regionale Sorten und Weinprobe.', NOW() + 2, 18.50, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW(), 2, 2);
UPDATE events SET chatroom_id = 3 WHERE id = 2;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (3, 2, NOW(), 'CHATADMIN');

INSERT INTO events (name, location, latitude, longitude, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Yoga im Grünen', 'Bismarckplatz 1 Regensburg', 49.0134, 12.1016, 60, 30, 'Sanftes Flow-Training im Park.', NOW() + 3, 0.0, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW(), 3, 2);
UPDATE events SET chatroom_id = 4 WHERE id = 3;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (4, 2, NOW(), 'CHATADMIN');

INSERT INTO events (name, location, latitude, longitude, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Bücherbörse für Studierende', 'Hauptmarkt 18 Nürnberg', 49.4521, 11.0767, 180, 80, 'Tausch und Verkauf gebrauchter Bücher.', NOW() + 4, 0.0, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW(), 4, 2);
UPDATE events SET chatroom_id = 5 WHERE id = 4;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (5, 2, NOW(), 'CHATADMIN');

INSERT INTO events (name, location, latitude, longitude, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Comedy Night: Open Mic', 'Maximilianstraße 38 Augsburg', 48.3705, 10.8978, 150, 20, 'Stand-up und Impro-Acts am Abend.', NOW() + 5, 25.00, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW(), 5, 2);
UPDATE events SET chatroom_id = 6 WHERE id = 5;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (6, 2, NOW(), 'CHATADMIN');

INSERT INTO events (name, location, latitude, longitude, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Live-Konzert: Indie & Rock', 'Theaterstraße 1 Würzburg', 49.7913, 9.9534, 200, 120, 'Lokale Bands mit kraftvollem Sound.', NOW() + 6, 12.00, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW(), 6, 2);
UPDATE events SET chatroom_id = 7 WHERE id = 6;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (7, 2, NOW(), 'CHATADMIN');

INSERT INTO events (name, location, latitude, longitude, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Stadtführung Altstadt', 'Grüner Markt 14 Bamberg', 49.8988, 10.9028, 90, 35, 'Geschichte, Gassen und gute Laune.', NOW() + 7, 8.00, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW(), 7, 2);
UPDATE events SET chatroom_id = 8 WHERE id = 7;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (8, 2, NOW(), 'CHATADMIN');

INSERT INTO events (name, location, latitude, longitude, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Sonnenuntergang am Fluss', 'Domplatz 1 Passau', 48.5667, 13.4319, 60, 50, 'Gemeinsamer Spaziergang mit Ausblick.', NOW() + 8, 0.0, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW(), 8, 2);
UPDATE events SET chatroom_id = 9 WHERE id = 8;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (9, 2, NOW(), 'CHATADMIN');

INSERT INTO events (name, location, latitude, longitude, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Stand-up Comedy: Open Mic', 'Theresienstraße 25 Ingolstadt', 48.7651, 11.4237, 180, 40, 'Kurze Sets, neue Talente, gute Stimmung.', NOW() + 9, 5.00, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW(), 9, 2);
UPDATE events SET chatroom_id = 10 WHERE id = 9;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (10, 2, NOW(), 'CHATADMIN');

INSERT INTO events (name, location, latitude, longitude, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Fotowalk für Anfänger', 'Altstadt 79 Landshut', 48.5442, 12.1469, 120, 25, 'Grundlagen und Tipps unterwegs.', NOW() + 10, 0.0, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW(), 10, 2);
UPDATE events SET chatroom_id = 11 WHERE id = 10;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (11, 2, NOW(), 'CHATADMIN');

INSERT INTO events (name, location, latitude, longitude, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Comedy & Drinks', 'Hauptstraße 23 Erlangen', 49.5897, 11.0119, 90, 30, 'Stand-up am Abend mit Getränken.', NOW() + 11, 6.50, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW(), 11, 2);
UPDATE events SET chatroom_id = 12 WHERE id = 11;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (12, 2, NOW(), 'CHATADMIN');

INSERT INTO events (name, location, latitude, longitude, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Open-Air-Kino', 'Gustavstraße 12 Fürth', 49.4771, 10.9887, 150, 100, 'Sommerfilm im Freien.', NOW() + 12, 9.00, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW(), 12, 2);
UPDATE events SET chatroom_id = 13 WHERE id = 12;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (13, 2, NOW(), 'CHATADMIN');

INSERT INTO events (name, location, latitude, longitude, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Laufgruppe: 5km', 'Max-Josefs-Platz 20 Rosenheim', 47.8564, 12.1229, 60, 60, 'Gemeinsam laufen, Tempo 6:00.', NOW() + 13, 0.0, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW(), 13, 2);
UPDATE events SET chatroom_id = 14 WHERE id = 13;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (14, 2, NOW(), 'CHATADMIN');

INSERT INTO events (name, location, latitude, longitude, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Radltour an der Donau', 'Luitpoldplatz 1 Deggendorf', 48.8408, 12.9607, 180, 40, 'Entspannte Tour mit Pausen.', NOW() + 14, 0.0, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW(), 14, 2);
UPDATE events SET chatroom_id = 15 WHERE id = 14;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (15, 2, NOW(), 'CHATADMIN');

INSERT INTO events (name, location, latitude, longitude, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Kochkurs: Süß & Herzhaft', 'Oberer Markt 1 Weiden', 49.6764, 12.1568, 160, 18, 'Kochen mit regionalen Zutaten.', NOW() + 15, 30.00, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW(), 15, 2);
UPDATE events SET chatroom_id = 16 WHERE id = 15;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (16, 2, NOW(), 'CHATADMIN');

INSERT INTO events (name, location, latitude, longitude, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Science Slam', 'Opernstraße 14 Bayreuth', 49.9456, 11.5713, 120, 150, 'Forschung kurz und witzig.', NOW() + 16, 10.00, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW(), 16, 2);
UPDATE events SET chatroom_id = 17 WHERE id = 16;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (17, 2, NOW(), 'CHATADMIN');

INSERT INTO events (name, location, latitude, longitude, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Poetry Night', 'Altstadt 30 Hof', 50.312, 11.9126, 90, 70, 'Gedichte und Texte von lokalen Autor:innen.', NOW() + 17, 0.0, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW(), 17, 2);
UPDATE events SET chatroom_id = 18 WHERE id = 17;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (18, 2, NOW(), 'CHATADMIN');

INSERT INTO events (name, location, latitude, longitude, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Töpfern für Einsteiger', 'Rodinger Straße 12 Cham', 49.2256, 12.655, 140, 16, 'Ton, Drehscheibe und Geduld.', NOW() + 18, 22.00, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW(), 18, 2);
UPDATE events SET chatroom_id = 19 WHERE id = 18;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (19, 2, NOW(), 'CHATADMIN');

INSERT INTO events (name, location, latitude, longitude, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Klettertreff', 'Marktplatz 1 Amberg', 49.441, 11.8628, 120, 20, 'Bouldern für alle Levels.', NOW() + 19, 7.00, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW(), 19, 2);
UPDATE events SET chatroom_id = 20 WHERE id = 19;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (20, 2, NOW(), 'CHATADMIN');

INSERT INTO events (name, location, latitude, longitude, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Winterwanderung mit Hüttenstopp', 'Olympiastraße 27 Garmisch-Partenkirchen', 47.492, 11.0955, 240, 30, 'Schnee, Aussicht und heiße Getränke.', NOW() + 20, 0.0, 2, null);
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('EVENT', null, NOW(), 20, 2);
UPDATE events SET chatroom_id = 21 WHERE id = 20;
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (21, 2, NOW(), 'CHATADMIN');

-- Add another chatroom, after event creation because of ID autoincrement
INSERT INTO chat_rooms (type, name, created_at, event_id, owner_id) VALUES ('GROUP', 'Ein anderer Chatroom', NOW() - 15, null, 4);

INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (22, 1, NOW() - 14, 'MEMBER');
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (22, 4, NOW() - 14, 'CHATADMIN');

-- Add event participant organizer as creator of event
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (1, 2, TRUE, NOW());
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (1, 1, FALSE, DATEADD('MINUTE', -53, CURRENT_TIMESTAMP));
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (2, 1, DATEADD('MINUTE', -53, CURRENT_TIMESTAMP), 'MEMBER');
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (1, 5, FALSE, DATEADD('MINUTE', -53, CURRENT_TIMESTAMP));
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (2, 5, DATEADD('MINUTE', -53, CURRENT_TIMESTAMP), 'MEMBER');
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (2, 2, TRUE, NOW());
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (2, 1, FALSE, NOW());
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (3, 1, NOW(), 'MEMBER');
INSERT INTO event_participants (event_id, user_id, organizer, joined_at) VALUES (2, 4, FALSE, NOW());
INSERT INTO chat_memberships (chatroom_id, user_id, joined_at, role) VALUES (3, 4, NOW(), 'MEMBER');
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

-- Add ratings
INSERT INTO ratings (event_id, user_id, stars, comment, created_at) VALUES (1, 1, 5, 'Super organisiert, hat mir sehr gefallen.', NOW() - 1);
INSERT INTO ratings (event_id, user_id, stars, comment, created_at) VALUES (2, 4, 4, 'Gute Stimmung, leckere Käseauswahl.', NOW() - 2);
INSERT INTO ratings (event_id, user_id, stars, comment, created_at) VALUES (3, 1, 3, 'Netter Kurs, aber etwas voll.', NOW() - 3);
INSERT INTO ratings (event_id, user_id, stars, comment, created_at) VALUES (4, 3, 5, 'Sehr informativ und gut geführt.', NOW() - 4);
INSERT INTO ratings (event_id, user_id, stars, comment, created_at) VALUES (5, 4, 2, 'Für mich war es zu kurz.', NOW() - 5);
INSERT INTO ratings (event_id, user_id, stars, comment, created_at) VALUES (6, 1, 4, 'Laut, aber toller Abend!', NOW() - 6);

-- Add event invitations
INSERT INTO event_invitations (event_id, inviter_id, invitee_id, status, created_at, responded_at) VALUES (1, 2, 1, 'PENDING', NOW() - 1, null);
INSERT INTO event_invitations (event_id, inviter_id, invitee_id, status, created_at, responded_at) VALUES (2, 1, 4, 'ACCEPTED', NOW() - 3, NOW() - 2);
INSERT INTO event_invitations (event_id, inviter_id, invitee_id, status, created_at, responded_at) VALUES (3, 1, 3, 'DECLINED', NOW() - 4, NOW() - 3);
INSERT INTO event_invitations (event_id, inviter_id, invitee_id, status, created_at, responded_at) VALUES (4, 2, 4, 'PENDING', NOW() - 1, null);

-- Add payments
INSERT INTO payments (user_id, event_id, amount, status, timestamp, paypal_transaction_id) VALUES (1, null, 99.99, 'COMPLETED', NOW()-1, 'PAYPAL-1234');
INSERT INTO payments (user_id, event_id, amount, status, timestamp, paypal_transaction_id) VALUES (1, null, 49.95, 'COMPLETED', NOW()-2, 'PAYPAL-4321');
INSERT INTO payments (user_id, event_id, amount, status, timestamp, paypal_transaction_id) VALUES (1, null, 10, 'FAILED', NOW()-3, 'PAYPAL-0000');
INSERT INTO payments (user_id, event_id, amount, status, timestamp, paypal_transaction_id) VALUES (1, null, 99.99, 'COMPLETED', NOW()-1, 'PAYPAL-1234');
INSERT INTO payments (user_id, event_id, amount, status, timestamp, paypal_transaction_id) VALUES (1, null, 49.95, 'COMPLETED', NOW()-2, 'PAYPAL-4321');
INSERT INTO payments (user_id, event_id, amount, status, timestamp, paypal_transaction_id) VALUES (1, null, 10, 'FAILED', NOW()-3, 'PAYPAL-0000');
INSERT INTO payments (user_id, event_id, amount, status, timestamp, paypal_transaction_id) VALUES (1, null, 99.99, 'COMPLETED', NOW()-1, 'PAYPAL-1234');
INSERT INTO payments (user_id, event_id, amount, status, timestamp, paypal_transaction_id) VALUES (1, null, 49.95, 'COMPLETED', NOW()-2, 'PAYPAL-4321');
INSERT INTO payments (user_id, event_id, amount, status, timestamp, paypal_transaction_id) VALUES (1, null, 10, 'FAILED', NOW()-3, 'PAYPAL-0000');
INSERT INTO payments (user_id, event_id, amount, status, timestamp, paypal_transaction_id) VALUES (1, null, 99.99, 'COMPLETED', NOW()-1, 'PAYPAL-1234');
INSERT INTO payments (user_id, event_id, amount, status, timestamp, paypal_transaction_id) VALUES (1, null, 49.95, 'COMPLETED', NOW()-2, 'PAYPAL-4321');
INSERT INTO payments (user_id, event_id, amount, status, timestamp, paypal_transaction_id) VALUES (1, null, 10, 'FAILED', NOW()-3, 'PAYPAL-0000');
INSERT INTO payments (user_id, event_id, amount, status, timestamp, paypal_transaction_id) VALUES (1, 2, 18.50, 'COMPLETED', NOW(), 'PAYPAL-1234');
INSERT INTO payments (user_id, event_id, amount, status, timestamp, paypal_transaction_id) VALUES (4, 2, 18.50, 'COMPLETED', NOW(), 'PAYPAL-1234');
