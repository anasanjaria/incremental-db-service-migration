package dao

import utils.IncrementalRollout

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.control.NonFatal

class UserDAOProxy(
 private[dao] val mongodbUserDAO: MongodbUserDAO,
 private[dao] val psqlUserDAO: PsqlUserDAO,
 private[dao] val rollout: IncrementalRollout,
 private implicit val executionContext: ExecutionContext
) extends ProductDAO {

  override def storeProduct(userId: Int, productName: String): Future[Int] = {
    store[(Int, String), Int](
      (mongodbUserDAO.storeProduct _).tupled,
      (psqlUserDAO.storeProduct _).tupled,
      (userId, productName),
      userId
    )
  }

  /**
   * Store data in databases.
   *
   * @param mongodbF a method represents monogdb implementation
   * @param psqlF a method represents postgres implementation
   * @param params arguments necessary for a given method.
   * @param userId User id
   * @return a `Future` holding a result
   * */
  private def store[A, B](mongodbF: A => Future[B], psqlF: A => Future[B], params: A, userId: Int): Future[B] = {
    val mongodbResult = mongodbF(params)

    if(rollout.isAllowed(userId)) {
      val psqlResult = psqlF(params).recover {
        case NonFatal(t) =>
          // For the sake of simplicity, I used print statement.
          // Usually, we use logging framework here.
          println("Encountered failure during writing in psql")
          t.printStackTrace()
      }

      for {
        _ <- psqlResult
        r <- mongodbResult
      } yield {
        r
      }
    } else {
      mongodbResult
    }
  }

}
