package dao

import scala.concurrent.Future

/**
 * Mongodb based implementation for User DAO.
 * */
class MongodbUserDAO extends ProductDAO {

  override def storeProduct(userId: Int, productName: String): Future[Int] = ???
}
