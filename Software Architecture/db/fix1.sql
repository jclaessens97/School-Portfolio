  /* USE VeloSA for Software Engineering*/
  USE VeloSA

  /* Update ride records where X and Y where switched*/
  UPDATE Rides
  SET StartPoint =  geometry::Point(StartPoint.STY, StartPoint.STX, 4326),  EndPoint =  geometry::Point(EndPoint.STY, EndPoint.STX, 4326)
  where StartPoint.STX < 5
  /* Update ride records with wrong SRID*/
  UPDATE Rides
  SET StartPoint =  geometry::Point(StartPoint.STX, StartPoint.STY, 4326),  EndPoint =  geometry::Point(EndPoint.STX, EndPoint.STY, 4326)
  where StartPoint.STSrid = 0

  /* Update station locations where X and Y where switched*/
   UPDATE Stations
  SET GPSCoord =  geometry::Point(GPSCoord.STY, GPSCoord.STX, 4326)
  where GPSCoord.STX < 5

   /* Update Vehicle locations where X and Y where switched*/
   UPDATE Vehicles
  SET Point =  geometry::Point(Point.STY, Point.STX, 4326)
  where Point.STX < 5

    /* Update Vehicles records with wrong SRID*/
   UPDATE Vehicles
  SET Point =  geometry::Point(Point.STY, Point.STX, 4326)
   where Point.STX < 5