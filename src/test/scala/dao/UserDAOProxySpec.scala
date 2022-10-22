package dao

import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.FutureOutcome
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.FixtureAsyncWordSpec

import scala.concurrent.Future

class UserDAOProxySpec extends FixtureAsyncWordSpec with Matchers with AsyncMockFactory {

  private val productName: String = "product name"
  private val productId: Int = 1
  private val userId: Int = 10

  override def withFixture(test: OneArgAsyncTest): FutureOutcome = {
    withFixture(
      test.toNoArgAsyncTest(
        new UserDAOProxy(mock[MongodbUserDAO], mock[PsqlUserDAO], executionContext)
      )
    )
  }

  override type FixtureParam = UserDAOProxy

  "UserDAOProxy" should {
    "fail whole user saving" when {
      "it fails to store user in a mongodb" in { proxy =>
        (proxy.psqlUserDAO.storeProduct _).expects(userId, productName).returns(Future.successful(productId))
        (proxy.mongodbUserDAO.storeProduct _).expects(userId, productName).returns(Future.failed(new RuntimeException("Database failed to store a user")))

        recoverToSucceededIf[RuntimeException] {
          proxy.storeProduct(userId, productName)
        }
      }
    }
    "not fail whole user saving" when {
      "it fails to store user only in a postgres" in { proxy =>
        (proxy.psqlUserDAO.storeProduct _).expects(userId, productName).returns(Future.failed(new RuntimeException("Database failed to store a user")))
        (proxy.mongodbUserDAO.storeProduct _).expects(userId, productName).returns(Future.successful(productId))

        for {
          result <- proxy.storeProduct(userId, productName)
        } yield {
          result must be(productId)
        }

      }
    }
  }
}
