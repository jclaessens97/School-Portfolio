USE VeloSA
SELECT geography::STGeomFromText('POINT (51.21990425067991 4.4160927863778445)', 4326).STDistance(v.Point.MakeValid().STUnion(v.Point.STStartPoint()).STAsText()) as distance
FROM Vehicles v JOIN BikeLots bl on v.BikeLotId = bl.BikeLotId where v.Point IS NOT NULL
and bl.BikeTypeId = 4;

