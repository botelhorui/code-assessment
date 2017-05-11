package services

trait Database {
  def getAirportInfo(query: String = ""): String
  def getTop10Airports(): String
  def getBot10Airports(): String
  def getRunwayTypes(): String
  def getTop10Runways(): String
}


