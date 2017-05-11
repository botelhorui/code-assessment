package controllers

import javax.inject._

import play.api._
import play.api.mvc._
import services.Database

/**
  * Created by Rui on 09-May-17.
  */
@Singleton
class ReportsController @Inject() (database: Database) extends Controller {

  /**
    * Create an Action to render an HTML page with a welcome message.
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index = Action {
    //@(message: String, top10Airports: String, bot10Airports: String, runwayTypes: String, top10Runways: String)
    Ok(views.html.reports(
      message = "Report",
      top10Airports = database.getTop10Airports,
      bot10Airports = database.getBot10Airports,
      runwayTypes = database.getRunwayTypes,
      top10Runways = database.getTop10Runways
    ))
  }

}
