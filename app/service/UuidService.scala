
package service

import com.google.inject.Singleton

import java.util.UUID

@Singleton
class UuidService {

  def uuid: String = UUID.randomUUID().toString

}
