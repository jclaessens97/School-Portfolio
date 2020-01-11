use VeloSA

Begin Transaction
DECLARE @RowsToRemove TABLE(vehicle INT, ride INT)
INSERT INTO @RowsToRemove
select potentieelOngeregistreerdeLock.VehicleId, potentieelOngeregistreerdeLock.RideId from
	Rides potentieelOngeregistreerdeLock
	INNER JOIN
		(SELECT v.LockId
		FROM Vehicles v
		WHERE LockId IS NOT NULL
		GROUP BY LockId
		HAVING COUNT(VehicleId) > 1) dubbeleLocks
	ON ( dubbeleLocks.LockId = potentieelOngeregistreerdeLock.EndLockId)
	WHERE SubscriptionId is null
	and NOT EXISTS
		(select * from Rides er
		where potentieelOngeregistreerdeLock.VehicleId = er.VehicleId
		and er.StartTime > potentieelOngeregistreerdeLock.StartTime)

update Vehicles set LockId = null
where vehicleId in (Select vehicle from @RowsToRemove)

update Rides set EndLockId = null
where RideId in (Select ride from @RowsToRemove)


rollback