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
INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Frühstück & Netzwerken', 'München', 90, 40, 'Kaffee, Brötchen und Austausch.', NOW() + 1, 0.0, 2, 2);
INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Käseverkostung am See', 'Konstanz', 120, 25, 'Regionale Sorten und Weinprobe.', NOW() + 2, 18.50, 2, null);
INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Yoga im Grünen', 'Regensburg', 60, 30, 'Sanftes Flow-Training im Park.', NOW() + 3, 0.0, 2, null);
INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Bücherbörse für Studierende', 'Nürnberg', 180, 80, 'Tausch und Verkauf gebrauchter Bücher.', NOW() + 4, 0.0, 2, null);
INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Kreativ-Workshop: Öl & Acryl', 'Augsburg', 150, 20, 'Malen mit Öl- und Acrylfarben.', NOW() + 5, 25.00, 2, null);
INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Konzertabend: Düster & Laut', 'Würzburg', 200, 120, 'Lokale Bands mit kräftigem Sound.', NOW() + 6, 12.00, 2, null);
INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Stadtführung Altstadt', 'Bamberg', 90, 35, 'Geschichte, Gassen und gute Laune.', NOW() + 7, 8.00, 2, null);
INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Sonnenuntergang am Fluss', 'Passau', 60, 50, 'Gemeinsamer Spaziergang mit Ausblick.', NOW() + 8, 0.0, 2, null);
INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Spieleabend mit Würfeln', 'Ingolstadt', 180, 40, 'Brettspiele, Würfelspiele, gute Stimmung.', NOW() + 9, 5.00, 2, null);
INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Fotowalk für Anfänger', 'Landshut', 120, 25, 'Grundlagen und Tipps unterwegs.', NOW() + 10, 0.0, 2, null);
INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Kaffee & Kuchen: Omas Rezepte', 'Erlangen', 90, 30, 'Hausgemachte Klassiker zum Probieren.', NOW() + 11, 6.50, 2, null);
INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Open-Air-Kino', 'Fürth', 150, 100, 'Sommerfilm im Freien.', NOW() + 12, 9.00, 2, null);
INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Laufgruppe: 5km', 'Rosenheim', 60, 60, 'Gemeinsam laufen, Tempo 6:00.', NOW() + 13, 0.0, 2, null);
INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Radltour an der Donau', 'Deggendorf', 180, 40, 'Entspannte Tour mit Pausen.', NOW() + 14, 0.0, 2, null);
INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Kochkurs: Süß & Herzhaft', 'Weiden', 160, 18, 'Kochen mit regionalen Zutaten.', NOW() + 15, 30.00, 2, null);
INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Science Slam', 'Bayreuth', 120, 150, 'Forschung kurz und witzig.', NOW() + 16, 10.00, 2, null);
INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Poetry Night', 'Hof', 90, 70, 'Gedichte und Texte von lokalen Autor:innen.', NOW() + 17, 0.0, 2, null);
INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Töpfern für Einsteiger', 'Cham', 140, 16, 'Ton, Drehscheibe und Geduld.', NOW() + 18, 22.00, 2, null);
INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Klettertreff', 'Amberg', 120, 20, 'Bouldern für alle Levels.', NOW() + 19, 7.00, 2, null);
INSERT INTO events (name, location, duration_minutes, max_participants, description, event_time, costs, organizer_id, chatroom_id)
VALUES ('Winterwanderung mit Hüttenstopp', 'Garmisch-Partenkirchen', 240, 30, 'Schnee, Aussicht und heiße Getränke.', NOW() + 20, 0.0, 2, null);
UPDATE chat_rooms SET event_id = 1 WHERE id = 2;

-- Add payments
INSERT INTO payments (user_id, event_id, amount, status, timestamp, paypal_transaction_id) VALUES (1, 1, 99.99, 'COMPLETED', NOW()-1, 'PAYPAL-1234');
INSERT INTO payments (user_id, event_id, amount, status, timestamp, paypal_transaction_id) VALUES (1, 1, 49.95, 'COMPLETED', NOW()-2, 'PAYPAL-4321');
INSERT INTO payments (user_id, event_id, amount, status, timestamp, paypal_transaction_id) VALUES (1, 1, 10, 'FAILED', NOW()-3, 'PAYPAL-0000');
