package services

import javax.inject._
import scala.io
import scala.io.Codec

case class Country(code: String, name: String){
  override def toString: String = s"Country(code:$code, name:$name)"
}

// Airport.iso_country -> Country.code
case class Airport(id: String, ident: String, name: String, iso_country: String){
  override def toString: String = s"Airport(id:$id, ident:$ident, name:$name, iso_country:$iso_country)"
}

// Runway.airport_ref = Airport.id
case class Runway(airport_ref: String, length_ft: String, width_ft: String, surface: String, closed: String){
  override def toString: String = s"Runway(airport_ref:$airport_ref, length_ft:$length_ft, width_ft:$width_ft, surface:$surface, closed:$closed)"
}

trait Database {
  def getAirportInfo(query: String = ""): String
}

/**
  * Created by Rui on 09-May-17.
  */
@Singleton
class MyDatabase extends Database {
  val rawCountryHeaders :: rawCountries = io.Source.fromFile("public/data/countries.csv")("UTF-8").getLines.toList // assume there is always an header and one or more countries
  val countryHeaders = rawCountryHeaders.split(",").map(_.replace("\"", ""))
  // "id","code","name","continent","wikipedia_link","keywords"
  val countries = rawCountries.map { line =>
    val fields = line.split(",").map(_.replace("\"", ""))
    Country(
      code = fields(countryHeaders.indexOf("code")),
      name = fields(countryHeaders.indexOf("name"))
    )
  }

  val rawAirportHeaders :: rawAirports = io.Source.fromFile("public/data/airports.csv")("UTF-8").getLines.toList
  val airportHeaders = rawAirportHeaders.split(",").map(_.replace("\"", ""))
  // "id","ident","type","name","latitude_deg","longitude_deg","elevation_ft","continent","iso_country","iso_region","municipality","scheduled_service","gps_code","iata_code","local_code","home_link","wikipedia_link","keywords"
  val airports = rawAirports.map { line =>
    val fields = line.split(",").map(_.replace("\"", ""))
    Airport(
      id = fields(airportHeaders.indexOf("id")),
      ident = fields(airportHeaders.indexOf("ident")),
      name = fields(airportHeaders.indexOf("name")),
      iso_country = fields(airportHeaders.indexOf("iso_country"))
    )
  }

  val rawRunwayHeaders :: rawRunways = io.Source.fromFile("public/data/runways.csv")("UTF-8").getLines.toList
  val runwayHeaders = rawRunwayHeaders.split(",").map(_.replace("\"", ""))
  // "id","airport_ref","airport_ident","length_ft","width_ft","surface","lighted","closed","le_ident","le_latitude_deg","le_longitude_deg","le_elevation_ft","le_heading_degT","le_displaced_threshold_ft","he_ident","he_latitude_deg","he_longitude_deg","he_elevation_ft","he_heading_degT","he_displaced_threshold_ft",
  val runways = rawRunways.map { line =>
    val fields = line.split(",").map(_.replace("\"", ""))
    Runway(
      airport_ref = fields(runwayHeaders.indexOf("airport_ref")),
      length_ft = fields(runwayHeaders.indexOf("length_ft")),
      width_ft = fields(runwayHeaders.indexOf("width_ft")),
      surface = fields(runwayHeaders.indexOf("surface")),
      closed = fields(runwayHeaders.indexOf("closed"))
    )
  }

  // html blank space
  val nbsp = "&nbsp;"
  override def getAirportInfo(query: String = ":)"): String =
    countries.filter(c => c.code == query || c.name == query).map { country =>
      country + "<br>" + airports.filter(a => a.iso_country == country.code).map { airport =>
        nbsp*4 + airport + "<br>" + runways.filter(r => r.airport_ref == airport.id).map { runway =>
          nbsp*8 + runway + "<br>"
        }.mkString
      }.mkString
    }.mkString
}
