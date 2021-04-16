ALTER EVENT cars.decouple_expired_reservations_from_car
ON SCHEDULE 
	EVERY 1 MINUTE
COMMENT 'Decouple all reservations from cars that are no longer valid'
DO
	UPDATE car
	INNER JOIN reservation ON car.reservation_id=reservation.reservation_id
	SET car.reservation_id = NULL
	WHERE reservation.valid_until < utc_timestamp()