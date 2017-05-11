package services

import javax.inject._
import scala.io
import scala.io.Codec

case class Country(
                    code: String,
                    name: String
                  ) {
  override def toString: String = s"Country(" +
    s"code:$code, " +
    s"name:$name)"
}

// Airport.iso_country -> Country.code
case class Airport(
                    id: String,
                    ident: String,
                    name: String,
                    iso_country: String
                  ) {
  override def toString: String = s"Airport(" +
    s"id:$id, " +
    s"ident:$ident, " +
    s"name:$name, " +
    s"iso_country:$iso_country)"
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

@Singleton
class SlowDatabase extends Database {
  val rawCountryHeaders :: rawCountries = io.Source.fromFile("public/data/countries.csv")("UTF-8").getLines.toList // assume there is always an header and one or more countries
  val countryHeaders = rawCountryHeaders.split(",", -1).map(_.replace("\"", ""))
  // "id","code","name","continent","wikipedia_link","keywords"
  val countries = rawCountries.map { line =>
    val fields = line.split(",", -1).map(_.replace("\"", ""))
    Country(
      code = fields(countryHeaders.indexOf("code")),
      name = fields(countryHeaders.indexOf("name"))
    )
  }

  val rawAirportHeaders :: rawAirports = io.Source.fromFile("public/data/airports.csv")("UTF-8").getLines.toList
  val airportHeaders = rawAirportHeaders.split(",", -1).map(_.replace("\"", ""))
  // "id","ident","type","name","latitude_deg","longitude_deg","elevation_ft","continent","iso_country","iso_region","municipality","scheduled_service","gps_code","iata_code","local_code","home_link","wikipedia_link","keywords"
  val airports = rawAirports.map { line =>
    val fields = line.split(",", -1).map(_.replace("\"", ""))
    Airport(
      id = fields(airportHeaders.indexOf("id")),
      ident = fields(airportHeaders.indexOf("ident")),
      name = fields(airportHeaders.indexOf("name")),
      iso_country = fields(airportHeaders.indexOf("iso_country"))
    )
  }

  val rawRunwayHeaders :: rawRunways = io.Source.fromFile("public/data/runways.csv")("UTF-8").getLines.toList
  val runwayHeaders = rawRunwayHeaders.split(",", -1).map(_.replace("\"", ""))
  // "id","airport_ref","airport_ident","length_ft","width_ft","surface","lighted","closed","le_ident","le_latitude_deg","le_longitude_deg","le_elevation_ft","le_heading_degT","le_displaced_threshold_ft","he_ident","he_latitude_deg","he_longitude_deg","he_elevation_ft","he_heading_degT","he_displaced_threshold_ft",
  val runways = rawRunways.map { line =>
    val fields = line.split(",", -1).map(_.replace("\"", ""))
    Runway(
      airport_ref = fields(runwayHeaders.indexOf("airport_ref")),
      length_ft = fields(runwayHeaders.indexOf("length_ft")),
      width_ft = fields(runwayHeaders.indexOf("width_ft")),
      surface = fields(runwayHeaders.indexOf("surface")),
      closed = fields(runwayHeaders.indexOf("closed")),
      le_ident = fields(runwayHeaders.indexOf("le_ident"))
    )
  }

  def queryFilterPredicate(query: String) = { c: Country =>
    if (query.length == 2) {
      c.code.toLowerCase == query.toLowerCase
    } else {
      c.name.toLowerCase.contains(query.toLowerCase)
    }
  }

  // html blank space
  val nbsp = "&nbsp;"

  override def getAirportInfo(query: String = ":)"): String =
    countries.filter(queryFilterPredicate(query)).map { country =>
      country + "<br>" + airports.filter(a => a.iso_country == country.code).map { airport =>
        nbsp * 4 + airport + "<br>" + runways.filter(r => r.airport_ref == airport.id).map { runway =>
          nbsp * 8 + runway + "<br>"
        }.mkString
      }.mkString
    }.mkString

  lazy val countryAirportCount = countries.map { country =>
    val count = airports.filter(a => a.iso_country == country.code).length
    (country, count)
  }

  /**
    * 10 countries with highest number of airports (with count) and countries with lowest number of airports.
    *
    * @return
    */
  override lazy val getTop10Airports: String = {
    countryAirportCount.sortWith(_._2 > _._2).take(10).map { t =>
      val (c, i) = t
      s"$c: Number of airports: $i"
    }.mkString("<br>")
  }

  /**
    * 10 countries with highest number of airports (with count) and countries with lowest number of airports.
    *
    * @return
    */
  override lazy val getBot10Airports: String = {
    countryAirportCount.sortWith(_._2 < _._2).take(10).map { t =>
      val (c, i) = t
      s"$c: Number of airports: $i"
    }.mkString("<br>")
  }

  /**
    * Type of runways (as indicated in "surface" column) per country
    *
    * @return
    */
  override lazy val getRunwayTypes: String = {
    countries.map { country =>
      val surfaces: Seq[String] = airports.filter(a => a.iso_country == country.code).flatMap { airport =>
        runways.filter(r => r.airport_ref == airport.id).map(_.surface)
      }
      (country, surfaces.toSet)
    }.map { t =>
      val (c, s) = t
      s"$c Surfaces:$s"
    }.mkString("<br>")
  }

  /**
    * top 10 most common runway identifications (indicated in "le_ident" column)
    *
    * @return
    */
  override lazy val getTop10Runways: String = {
    runways.groupBy(_.le_ident).mapValues(_.length).toList.sortWith(_._2 > _._2).take(10).map { t =>
      val (id, c) = t
      s"$c : $id"
    }.mkString("<br>")
  }

}
