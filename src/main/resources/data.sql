INSERT INTO client VALUES ('user1@mail.com', '$2a$10$8pzbe3xdTq5qr.ILAuwY6OrUCO/4TOwE2FMeXfDxtHf8roz42j1hC', 'USER'); -- Password
INSERT INTO client VALUES ('user2@mail.com', '$2a$10$XCv/x5eGwaO.TanPpvkrbO2fuKY4HU1tcYacF8WaM2/e8vUZSiqz6', 'USER');
INSERT INTO client VALUES ('user3@mail.com', '$2a$10$XCv/x5eGwaO.TanPpvkrbO2fuKY4HU1tcYacF8WaM2/e8vUZSiqz6', 'USER');
INSERT INTO client VALUES ('admin@mail.com', '$2a$10$sxL2RBX9jQVBAcBMDAvZBexo8mHXMEp7KB1DpZNc5wx3rvPJBD/uW', 'ADMIN');

INSERT INTO frage (titel, question, ablaufdatum) VALUES ('POS', 'Ist HTML eine Programmiersprache?', '2023-05-21');
INSERT INTO frage (titel, question, ablaufdatum) VALUES ('DBI', 'REDIS ist eine Key-Value Store?', '2023-05-21');
INSERT INTO frage (titel, question, ablaufdatum) VALUES ('JavaScript', 'JavaScript wird compiliert?', '2023-05-21');
INSERT INTO frage (titel, question, ablaufdatum) VALUES ('CSS', 'In CSS spielt vererbung eine wichtige Rolle?', '2023-05-21');
INSERT INTO frage (titel, question, ablaufdatum) VALUES ('Ablaufdatum', 'Diese Frage ist schon seid langem abgelaufen?', '2003-05-21');

INSERT INTO antwort ( client_Antwort, client, frage) VALUES ( 1, 'user1@mail.com', 5);
INSERT INTO antwort ( client_Antwort, client, frage) VALUES ( 2, 'user2@mail.com', 5);
INSERT INTO antwort ( client_Antwort, client, frage) VALUES ( 2, 'user1@mail.com', 1);
