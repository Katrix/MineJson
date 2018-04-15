/*
 * This file is part of MineJson, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018 Katrix
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.katsstuff.minejson.loottable

import io.circe._
import io.circe.syntax._
import net.katsstuff.minejson.RangeOrSingle

trait LootCondition {
  def toJson: Json
}

sealed trait Entity {
  def name: String
}
object Entity {
  case object This         extends Entity { def name: String = "this"          }
  case object Killer       extends Entity { def name: String = "killer"        }
  case object KillerPlayer extends Entity { def name: String = "killer_player" }

  implicit val encoder: Encoder[Entity] = _.name.asJson
}

case class EntityProperties(entity: Option[Entity] = None, properties: Option[EntityPropertiesData] = None)
    extends LootCondition {
  override def toJson: Json = Encoder[EntityProperties].apply(this)
}
object EntityProperties {
  implicit val encoder: Encoder[EntityProperties] = (a: EntityProperties) =>
    Json.obj(
      "condition" := "entity_properties",
      "entity" := a.entity,
      "properties" := Json.obj("on_fire" := a.properties.flatMap(_.onFire))
  )
}
case class EntityPropertiesData(onFire: Option[Boolean] = None)

case class EntityScores(entity: Option[Entity], scores: Map[String, RangeOrSingle] = Map.empty) extends LootCondition {
  override def toJson: Json = Encoder[EntityScores].apply(this)
}
object EntityScores {
  implicit val encoder: Encoder[EntityScores] = (a: EntityScores) =>
    Json.obj("condition" := "entity_scores", "entity" := a.entity, "scores" := a.scores)
}

case class KilledByPlayer(inverse: Option[Boolean] = None) extends LootCondition {
  override def toJson: Json = Encoder[KilledByPlayer].apply(this)
}
object KilledByPlayer {
  implicit val encoder: Encoder[KilledByPlayer] = (a: KilledByPlayer) =>
    Json.obj("condition" := "killed_by_player", "inverse" := a.inverse)
}

case class RandomChance(chance: Double) extends LootCondition {
  override def toJson: Json = Encoder[RandomChance].apply(this)
}
object RandomChance {
  implicit val encoder: Encoder[RandomChance] = (a: RandomChance) =>
    Json.obj("condition" := "random_chance", "chance" := a.chance)
}

case class RandomChanceWithLooting(chance: Double, lootingMultiplier: Double) extends LootCondition {
  override def toJson: Json = Encoder[RandomChanceWithLooting].apply(this)
}
object RandomChanceWithLooting {
  implicit val encoder: Encoder[RandomChanceWithLooting] = (a: RandomChanceWithLooting) =>
    Json.obj(
      "condition" := "random_chance_with_looting",
      "chance" := a.chance,
      "looting_multiplier" := a.lootingMultiplier
  )
}
