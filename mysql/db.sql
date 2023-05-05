DROP TABLE IF EXISTS `cbpayment`;
CREATE TABLE `cbpayment` (
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK2ac5ds3hn2h4fhheblpaoy0rx` FOREIGN KEY (`id`) REFERENCES `payment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `cheque_payment`;
CREATE TABLE `cheque_payment` (
  `person_who_received_payment` varchar(255) DEFAULT NULL,
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK2s4atsov28uiwcbwu7qqnoii0` FOREIGN KEY (`id`) REFERENCES `payment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `direct_payment`;
CREATE TABLE `direct_payment` (
  `person_who_received_payment` varchar(255) DEFAULT NULL,
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKlnh819mv7w044jg01ednl63ex` FOREIGN KEY (`id`) REFERENCES `payment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `direct_payment` (`person_who_received_payment`, `id`) VALUES
('angular',	1),
(NULL,	2),
(NULL,	52),
('angular',	202),
('angular',	252),
('angular',	302),
('Domain-Driven Design',	352),
('angular',	402);

DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
  `id` bigint NOT NULL,
  `amount` decimal(38,2) DEFAULT NULL,
  `date_time_payment` datetime(6) DEFAULT NULL,
  `payment_state` varchar(255) DEFAULT NULL,
  `person_who_refund_payment` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `payment` (`id`, `amount`, `date_time_payment`, `payment_state`, `person_who_refund_payment`, `type`) VALUES
(1,	10.00,	'2023-05-03 00:42:30.360277',	'ACCEPTED',	NULL,	'direct'),
(2,	20.00,	NULL,	'PENDING',	NULL,	'direct'),
(52,	10.00,	NULL,	'PENDING',	NULL,	'direct'),
(202,	0.00,	'2023-05-03 22:03:05.579329',	'ACCEPTED',	NULL,	'direct'),
(252,	10.00,	'2023-05-03 21:37:27.578018',	'ACCEPTED',	NULL,	'direct'),
(302,	0.00,	'2023-05-03 03:24:28.523458',	'ACCEPTED',	NULL,	'direct'),
(352,	10.00,	'2023-05-03 22:03:39.697791',	'ACCEPTED',	NULL,	'direct'),
(402,	10.00,	'2023-05-03 21:26:30.654286',	'ACCEPTED',	NULL,	'direct');

DROP TABLE IF EXISTS `payment_seq`;
CREATE TABLE `payment_seq` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `payment_seq` (`next_val`) VALUES
(501);

DROP TABLE IF EXISTS `reservation`;
CREATE TABLE `reservation` (
  `id` varchar(255) NOT NULL,
  `comments` varchar(255) DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `nbr_adult` int NOT NULL,
  `nbr_kid` int NOT NULL,
  `nbr_meal` int NOT NULL,
  `nbr_teen` int NOT NULL,
  `state` smallint DEFAULT NULL,
  `tel` varchar(255) DEFAULT NULL,
  `payment_id` bigint DEFAULT NULL,
  `qr_code_base64` blob,
  PRIMARY KEY (`id`),
  KEY `FK8g1s9tyunsjdv96dyiobv51bb` (`payment_id`),
  CONSTRAINT `FK8g1s9tyunsjdv96dyiobv51bb` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `reservation` (`id`, `comments`, `create_date`, `email`, `name`, `nbr_adult`, `nbr_kid`, `nbr_meal`, `nbr_teen`, `state`, `tel`, `payment_id`, `qr_code_base64`) VALUES
('SA828J26E',	NULL,	'2023-05-03',	'sebastien.burckhardt@gmail.com',	'Sébastien Burckhardt',	0,	0,	0,	0,	1,	'638937416',	302,	'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAACCklEQVR4Xu2YO5KDQAxERW1AyBHmKBwNjsZROMKGDihr1a3Ba8b7C7eqR4lnpIcDldQSmP/F3q31fGkda6xjjXWssY419v+xzWDFfThs8m254zo7vbYoYjyWbRngicAx1tsZk8PMIhCnY/R99sFv025LZNGKMrYZAjavCBSWlTbmEb9N2WcT0ieM4Rh5u9ujkMLz2lkyGDKFFA1VY/KHXlPETlvtjRKMYVSeA2oY8jbGFLL72w2pymGEGxpMEVsiYc7xM55lFRKMztqf600HM+4p0BhH+kKCDUqMh2h6WBVdBqKzUEEWPxzSihhlNjLFecyEMd7mTQerqrKmczVmca17rSDmzrc+wxoX4hLaa1xXDq5xgljtLIME4x0w68m5tfBf9LAat5jOJZW4YGtBPSliLCTWTO4pP3WWBrZiHhsCSJ8ljSxeG1AHc2hv1BPGTwRQT1FWn4u+HLbSsy1+mc4vnSWD5XT2wNK+6SwhbBnQWYbpHIv+I28Q5BmAIHa2VAbqnpJDWhKrqhKYU4nd9/BEPWV1qWHVNkgNNtkzjpsktiFH/NLIIQ1X3Wsv01kIW3As9SXH8JXgHE3nQ2oYRLdwXanam8kcEVXGhryF8KCl7nBqY9zYjBXEh64bvhKGI7CczvVdB8JTENfDDFYifZzOzgqKQsIX+1kR+9U61ljHGutYYx1rTAj7ACQEaAHw21FHAAAAAElFTkSuQmCC'),
('SA83S5H9A',	NULL,	'2023-05-03',	'sebastien.burckhardt@gmail.com',	'Sébastien Burckhardt',	1,	0,	0,	0,	1,	'638937416',	402,	'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAACCklEQVR4Xu2YO5KDQAxERW1AyBHmKBwNjsZROMKGDihr1a3Ba8b7C7eqR4lnpIcDldQSmP/F3q31fGkda6xjjXWssY419v+xzWDFfThs8m254zo7vbYoYjyWbRngicAx1tsZk8PMIhCnY/R99sFv025LZNGKMrYZAjavCBSWlTbmEb9N2WcT0ieM4Rh5u9ujkMLz2lkyGDKFFA1VY/KHXlPETlvtjRKMYVSeA2oY8jbGFLL72w2pymGEGxpMEVsiYc7xM55lFRKMztqf600HM+4p0BhH+kKCDUqMh2h6WBVdBqKzUEEWPxzSihhlNjLFecyEMd7mTQerqrKmczVmca17rSDmzrc+wxoX4hLaa1xXDq5xgljtLIME4x0w68m5tfBf9LAat5jOJZW4YGtBPSliLCTWTO4pP3WWBrZiHhsCSJ8ljSxeG1AHc2hv1BPGTwRQT1FWn4u+HLbSsy1+mc4vnSWD5XT2wNK+6SwhbBnQWYbpHIv+I28Q5BmAIHa2VAbqnpJDWhKrqhKYU4nd9/BEPWV1qWHVNkgNNtkzjpsktiFH/NLIIQ1X3Wsv01kIW3As9SXH8JXgHE3nQ2oYRLdwXanam8kcEVXGhryF8KCl7nBqY9zYjBXEh64bvhKGI7CczvVdB8JTENfDDFYifZzOzgqKQsIX+1kR+9U61ljHGutYYx1rTAj7ACQEaAHw21FHAAAAAElFTkSuQmCC'),
('SA8B0MDPP',	NULL,	'2023-05-03',	'sebastien.burckhardt@gmail.com',	'Sébastien Burckhardt',	1,	0,	0,	0,	1,	'638937416',	252,	'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAACCklEQVR4Xu2YO5KDQAxERW1AyBHmKBwNjsZROMKGDihr1a3Ba8b7C7eqR4lnpIcDldQSmP/F3q31fGkda6xjjXWssY419v+xzWDFfThs8m254zo7vbYoYjyWbRngicAx1tsZk8PMIhCnY/R99sFv025LZNGKMrYZAjavCBSWlTbmEb9N2WcT0ieM4Rh5u9ujkMLz2lkyGDKFFA1VY/KHXlPETlvtjRKMYVSeA2oY8jbGFLL72w2pymGEGxpMEVsiYc7xM55lFRKMztqf600HM+4p0BhH+kKCDUqMh2h6WBVdBqKzUEEWPxzSihhlNjLFecyEMd7mTQerqrKmczVmca17rSDmzrc+wxoX4hLaa1xXDq5xgljtLIME4x0w68m5tfBf9LAat5jOJZW4YGtBPSliLCTWTO4pP3WWBrZiHhsCSJ8ljSxeG1AHc2hv1BPGTwRQT1FWn4u+HLbSsy1+mc4vnSWD5XT2wNK+6SwhbBnQWYbpHIv+I28Q5BmAIHa2VAbqnpJDWhKrqhKYU4nd9/BEPWV1qWHVNkgNNtkzjpsktiFH/NLIIQ1X3Wsv01kIW3As9SXH8JXgHE3nQ2oYRLdwXanam8kcEVXGhryF8KCl7nBqY9zYjBXEh64bvhKGI7CczvVdB8JTENfDDFYifZzOzgqKQsIX+1kR+9U61ljHGutYYx1rTAj7ACQEaAHw21FHAAAAAElFTkSuQmCC'),
('SA8GW89UD',	NULL,	'2023-05-03',	'sebastien.burckhardt@gmail.com',	'Sébastien Burckhardt',	0,	0,	0,	0,	1,	'638937416',	202,	'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAACCklEQVR4Xu2YO5KDQAxERW1AyBHmKBwNjsZROMKGDihr1a3Ba8b7C7eqR4lnpIcDldQSmP/F3q31fGkda6xjjXWssY419v+xzWDFfThs8m254zo7vbYoYjyWbRngicAx1tsZk8PMIhCnY/R99sFv025LZNGKMrYZAjavCBSWlTbmEb9N2WcT0ieM4Rh5u9ujkMLz2lkyGDKFFA1VY/KHXlPETlvtjRKMYVSeA2oY8jbGFLL72w2pymGEGxpMEVsiYc7xM55lFRKMztqf600HM+4p0BhH+kKCDUqMh2h6WBVdBqKzUEEWPxzSihhlNjLFecyEMd7mTQerqrKmczVmca17rSDmzrc+wxoX4hLaa1xXDq5xgljtLIME4x0w68m5tfBf9LAat5jOJZW4YGtBPSliLCTWTO4pP3WWBrZiHhsCSJ8ljSxeG1AHc2hv1BPGTwRQT1FWn4u+HLbSsy1+mc4vnSWD5XT2wNK+6SwhbBnQWYbpHIv+I28Q5BmAIHa2VAbqnpJDWhKrqhKYU4nd9/BEPWV1qWHVNkgNNtkzjpsktiFH/NLIIQ1X3Wsv01kIW3As9SXH8JXgHE3nQ2oYRLdwXanam8kcEVXGhryF8KCl7nBqY9zYjBXEh64bvhKGI7CczvVdB8JTENfDDFYifZzOzgqKQsIX+1kR+9U61ljHGutYYx1rTAj7ACQEaAHw21FHAAAAAElFTkSuQmCC'),
('SA8HR9WQY',	NULL,	'2023-05-02',	'sebastien.burckhardt@gmail.com',	'Sébastien Burckhardt',	1,	0,	0,	0,	0,	'638937416',	1,	NULL),
('SA8UK0OPA',	NULL,	'2023-05-03',	'sebastien.burckhardt@gmail.com',	'Sébastien Burckhardt',	1,	0,	0,	0,	1,	'638937416',	352,	'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAACCklEQVR4Xu2YO5KDQAxERW1AyBHmKBwNjsZROMKGDihr1a3Ba8b7C7eqR4lnpIcDldQSmP/F3q31fGkda6xjjXWssY419v+xzWDFfThs8m254zo7vbYoYjyWbRngicAx1tsZk8PMIhCnY/R99sFv025LZNGKMrYZAjavCBSWlTbmEb9N2WcT0ieM4Rh5u9ujkMLz2lkyGDKFFA1VY/KHXlPETlvtjRKMYVSeA2oY8jbGFLL72w2pymGEGxpMEVsiYc7xM55lFRKMztqf600HM+4p0BhH+kKCDUqMh2h6WBVdBqKzUEEWPxzSihhlNjLFecyEMd7mTQerqrKmczVmca17rSDmzrc+wxoX4hLaa1xXDq5xgljtLIME4x0w68m5tfBf9LAat5jOJZW4YGtBPSliLCTWTO4pP3WWBrZiHhsCSJ8ljSxeG1AHc2hv1BPGTwRQT1FWn4u+HLbSsy1+mc4vnSWD5XT2wNK+6SwhbBnQWYbpHIv+I28Q5BmAIHa2VAbqnpJDWhKrqhKYU4nd9/BEPWV1qWHVNkgNNtkzjpsktiFH/NLIIQ1X3Wsv01kIW3As9SXH8JXgHE3nQ2oYRLdwXanam8kcEVXGhryF8KCl7nBqY9zYjBXEh64bvhKGI7CczvVdB8JTENfDDFYifZzOzgqKQsIX+1kR+9U61ljHGutYYx1rTAj7ACQEaAHw21FHAAAAAElFTkSuQmCC'),
('SA8WZHD8Y',	NULL,	'2023-05-03',	'sebastien.burckhardt@gmail.com',	'Sébastien Burckhardt',	1,	0,	0,	0,	2,	'638937416',	52,	NULL),
('SA8YMA0DV',	NULL,	'2023-05-02',	'sebastien.burckhardt@gmail.com',	'Sébastien Burckhardt',	2,	0,	0,	0,	2,	'638937416',	2,	NULL);
