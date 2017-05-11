import org.scalatestplus.play._

import scala.collection.mutable
import services.{Country, Database, MyDatabase}

/**
  * Created by Rui on 09-May-17.
  */
class DatabaseSpec extends PlaySpec with OneAppPerSuite{
  "Database component" should {
    "show all countries read from file" in {
      val db = new MyDatabase
      assert(db.countries.contains(Country(code="PT",name="Portugal"))
    }
  }
}
