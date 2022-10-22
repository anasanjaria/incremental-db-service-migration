package utils

/**
 * Rolling out a feature incrementally.
 * */
class IncrementalRollout {

  /**
   * Whether a given user is allowed to use a feature.
   *
   * @param userId User id.
   * @return Boolean true if a user is allowed false otherwise.
   * */
  def isAllowed(userId: Int): Boolean = ???
}
