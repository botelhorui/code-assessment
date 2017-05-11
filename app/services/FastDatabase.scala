package services

case class Country(
                    code: String,
                    name: String,
                    var airports: List[Airport] = List()
                  ) {
  override def toString: String = s"Country(" +
    s"code:$code, " +
    s"name:$name" +
    s"airports:${airports.length}" +
    s")"
}

// Airport.iso_country -> Country.code
case class Airport(
                    id: String,
                    ident: String,
                    name: String,
                    iso_country: String,
                    var runways: List[Runway] = List()
                  ) {
  override def toString: String = s"Airport(" +
    s"id:$id, " +
    s"ident:$ident, " +
    s"name:$name, " +
    s"iso_country:$iso_country, " +
    s"runways:${runways.length}" +
    s")"
}

// Runway.airport_ref = Airport.id
case class Runway(
                   airport_ref: String,
                   length_ft: String,
                   width_ft: String,
                   surface: String,
                   closed: String,
                   le_ident: String
                 ) {
  override def toString: String = s"Runway(" +
    s"airport_ref:$airport_ref, " +
    s"length_ft:$length_ft, " +
    s"width_ft:$width_ft, " +
    s"surface:$surface, " +
    s"closed:$closed, " +
    s"le_ident:$le_ident)"
}


class FastDatabase extends  Database{
  override def getAirportInfo(query: String): String = ???

  override def getTop10Airports(): String = ???

  override def getBot10Airports(): String = ???

  override def getRunwayTypes(): String = ???

  override def getTop10Runways(): String = ???
}
