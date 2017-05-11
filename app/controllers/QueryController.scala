package controllers

import javax.inject._

import play.api.mvc._
import services.Database
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._


case class Query(query:String)

/**
  * Created by Rui on 09-May-17.
  */
@Singleton
class QueryController @Inject() (database: Database) extends Controller {

  /**
    * Create an Action to render an HTML page with a welcome message.
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */

  val queryForm: Form[Query] = Form(
    mapping(
      "query" -> nonEmptyText
    )(Query.apply)(Query.unapply)
  )

  def index = Action {
    Ok(views.html.form(queryForm))
  }

  def query = Action {implicit request =>
    queryForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.index(formWithErrors.toString)),
      query => Ok(views.html.query(message="Query", output=database.getAirportInfo(query.query)))
    )
  }

}
