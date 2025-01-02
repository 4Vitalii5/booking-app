INSERT INTO payments (id, status, booking_id, session_url, session_id, amount_to_pay) VALUES
(1, 'PENDING', 1, 'http://example.com/session', 'sessionId', 230.00),
(2, 'PENDING', 2, 'http://example.com/another-session', 'anotherSessionId', 200.00),
(3, 'EXPIRED', 3, 'http://example.com/another-session', 'anotherSessionId123', 200.00);
