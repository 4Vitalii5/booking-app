INSERT INTO bookings (id, user_id, accommodation_id, check_in_date, check_out_date, status) VALUES
(1, 1, 1, CURRENT_DATE, CURRENT_DATE + INTERVAL '10 days', 'PENDING'),
(2, 2, 1, CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE - INTERVAL '15 days', 'CONFIRMED'),
(3, 2, 1, CURRENT_DATE - INTERVAL '21 days', CURRENT_DATE - INTERVAL '25 days', 'PENDING');
