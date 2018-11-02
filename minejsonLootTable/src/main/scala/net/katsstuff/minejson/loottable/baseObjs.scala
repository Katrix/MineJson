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

import scala.language.implicitConversions

import io.circe._
import io.circe.syntax._
import net.katsstuff.minejson.{RangeOrSingle, ResourceId}

case class LootTable(fileName: ResourceId, pools: Seq[LootPool] = Nil) extends LootTableOrResourceId {
  override def id: ResourceId = fileName
}
object LootTable {
  implicit val encoder: Encoder[LootTable] = (a: LootTable) => Json.obj("pools" := a.pools)
}

sealed trait LootTableOrResourceId {
  def id: ResourceId
}
object LootTableOrResourceId {
  implicit def mkId(id: ResourceId): LootTableOrResourceId = LootTableOrResourceIdAsResourceId(id)
}
case class LootTableOrResourceIdAsResourceId(id: ResourceId) extends LootTableOrResourceId

case class LootPool(
    conditions: Seq[LootCondition] = Nil,
    rolls: Option[RangeOrSingle] = None,
    bonusRolls: Option[RangeOrSingle] = None,
    entries: Seq[LootEntry] = Nil
)
object LootPool {
  implicit val encoder: Encoder[LootPool] = (a: LootPool) =>
    Json.obj(
      "conditions" := a.conditions.map(_.toJson),
      "rolls" := a.rolls,
      "bonus_rolls" := a.bonusRolls,
      "entries" := a.entries
  )
}

sealed trait LootTableType {
  def name: String
}
object LootTableType {
  case object Item      extends LootTableType { def name: String = "item"       }
  case object LootTable extends LootTableType { def name: String = "loot_table" }
  case object Empty     extends LootTableType { def name: String = "empty"      }

  implicit val encoder: Encoder[LootTableType] = _.name.asJson
}

case class LootEntry(
    tpe: LootTableType,
    name: LootTableOrResourceId,
    conditions: Seq[LootCondition] = Nil,
    functions: Seq[LootFunction] = Nil,
    weight: Option[Int] = None,
    quality: Option[Int] = None
)
object LootEntry {
  implicit val encoder: Encoder[LootEntry] = (a: LootEntry) =>
    Json.obj(
      "conditions" := a.conditions.map(_.toJson),
      "type" := a.tpe,
      "name" := a.name.id,
      "functions" := a.functions.map(_.toJson),
      "weight" := a.weight,
      "quality" := a.quality
  )
}
