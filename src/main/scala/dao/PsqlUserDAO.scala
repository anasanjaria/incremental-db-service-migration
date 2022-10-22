package dao

import scala.concurrent.Future

/**
 * Postgres based implementation for User DAO.
 * */
class PsqlUserDAO extends ProductDAO {

  override def storeProduct(userId: Int, productName: String): Future[Int] = ???
}
