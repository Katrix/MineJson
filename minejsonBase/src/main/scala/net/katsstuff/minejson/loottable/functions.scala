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

import java.util.UUID

import scala.language.implicitConversions

import io.circe._
import io.circe.syntax._
import net.katsstuff.minejson.{DoubleRangeOrSingle, RangeOrSingle, ResourceId}
import net.katsstuff.typenbt.{Mojangson, NBTCompound}

trait LootFunction {
  def toJson: Json
}

case class EnchantRandomly(enchantments: Seq[ResourceId] = Nil) extends LootFunction {
  override def toJson: Json = Encoder[EnchantRandomly].apply(this)
}
object EnchantRandomly {
  implicit val encoder: Encoder[EnchantRandomly] = (a: EnchantRandomly) =>
    Json.obj(
      (if (a.enchantments.isEmpty) Nil else Seq("enchantments" := a.enchantments)) ++ Seq(
        "function" := "enchant_randomly".asJson
      ): _*
  )
}

case class EnchantWithLevels(treasure: Option[Boolean], levels: RangeOrSingle) extends LootFunction {
  override def toJson: Json = Encoder[EnchantWithLevels].apply(this)
}
object EnchantWithLevels {
  implicit val encoder: Encoder[EnchantWithLevels] = (a: EnchantWithLevels) =>
    Json.obj("function" := "enchant_with_levels ", "treasure" := a.treasure, "levels" := a.levels)
}

case class ExplorationMap(
    destination: Option[String] = None,
    decoration: Option[String] = None,
    zoom: Option[Int] = None,
    searchRadius: Option[Int] = None,
    skipExistingChunks: Option[Boolean] = None
) extends LootFunction {
  override def toJson: Json = Encoder[ExplorationMap].apply(this)
}
object ExplorationMap {
  implicit val encoder: Encoder[ExplorationMap] = (a: ExplorationMap) =>
    Json.obj(
      "destination" := a.destination,
      "decoration" := a.decoration,
      "zoom" := a.zoom,
      "search_radius" := a.searchRadius,
      "skip_existing_chunks" := a.skipExistingChunks,
  )
}

case object FurnaceSmelt extends LootFunction {
  override def toJson: Json = Json.obj("function" := "furnace_smelt")
}

case class LootingEnchant(count: Option[DoubleRangeOrSingle] = None, limit: Option[Int] = None) extends LootFunction {
  override def toJson: Json = Encoder[LootingEnchant].apply(this)
}
object LootFunction {
  implicit val encoder: Encoder[LootingEnchant] = (a: LootingEnchant) =>
    Json.obj("function" := "looting_enchant", "count" := a.count, "limit" := a.limit)
}

case class SetAttributes(modifiers: Seq[LootModifier] = Nil) extends LootFunction {
  override def toJson: Json = Encoder[SetAttributes].apply(this)
}
object SetAttributes {
  implicit val encoder: Encoder[SetAttributes] = (a: SetAttributes) =>
    Json.obj(
      "function" := "set_attributes",
      "modifiers" := a.modifiers.map { modifier =>
        Json.obj(
          "name" := modifier.name,
          "attribute" := modifier.attribute,
          "amount" := modifier.amount,
          "id" := modifier.id,
          "slot" := modifier.slot
        )
      }.asJson
  )
}
case class LootModifier(
    name: String,
    attribute: String,
    operation: AttributeOperation,
    amount: RangeOrSingle,
    id: Option[UUID] = None,
    slot: AttributeSlotsListOrSingle
)
sealed trait AttributeOperation {
  def name: String
}
object AttributeOperation {
  case object Addition      extends AttributeOperation { def name: String = "addition"       }
  case object MultiplyBase  extends AttributeOperation { def name: String = "multiply_base"  }
  case object MultiplyTotal extends AttributeOperation { def name: String = "multiply_total" }

  implicit val encoder: Encoder[AttributeOperation] = _.name.asJson
}

sealed trait AttributeSlotsListOrSingle
object AttributeSlotsListOrSingle {
  implicit def fromSingle(attributeSlot: AttributeSlot): AttributeSlotsListOrSingle =
    AttributeSlotsListOrSingleAsSingle(attributeSlot)
  implicit def fromList(attributeSlots: Seq[AttributeSlot]): AttributeSlotsListOrSingle =
    AttributeSlotsListOrSingleAsList(attributeSlots)

  implicit val encoder: Encoder[AttributeSlotsListOrSingle] = {
    case AttributeSlotsListOrSingleAsSingle(slot) => slot.asJson
    case AttributeSlotsListOrSingleAsList(slots)  => slots.asJson
  }
}
case class AttributeSlotsListOrSingleAsSingle(slot: AttributeSlot)     extends AttributeSlotsListOrSingle
case class AttributeSlotsListOrSingleAsList(slots: Seq[AttributeSlot]) extends AttributeSlotsListOrSingle

sealed trait AttributeSlot {
  def name: String
}
object AttributeSlot {
  case object MainHand extends AttributeSlot { override def name: String = "mainhand" }
  case object OffHand  extends AttributeSlot { override def name: String = "offhand"  }
  case object Feet     extends AttributeSlot { override def name: String = "feet"     }
  case object Legs     extends AttributeSlot { override def name: String = "legs"     }
  case object Chest    extends AttributeSlot { override def name: String = "chest"    }
  case object Head     extends AttributeSlot { override def name: String = "head"     }

  implicit val encoder: Encoder[AttributeSlot] = _.name.asJson
}

case class SetCount(count: RangeOrSingle) extends LootFunction {
  override def toJson: Json = Encoder[SetCount].apply(this)
}
object SetCount {
  implicit val encoder: Encoder[SetCount] = (a: SetCount) => Json.obj("function" := "set_count", "count" := a.count)
}

case class SetDamage(damage: DoubleRangeOrSingle) extends LootFunction {
  override def toJson: Json = Encoder[SetDamage].apply(this)
}
object SetDamage {
  implicit val encoder: Encoder[SetDamage] = (a: SetDamage) =>
    Json.obj("function" := "set_damage", "damage" := a.damage)
}

case class SetData(data: RangeOrSingle) extends LootFunction {
  override def toJson: Json = Encoder[SetData].apply(this)
}
object SetData {
  implicit val encoder: Encoder[SetData] = (a: SetData) => Json.obj("function" := "set_data", "data" := a.data)
}

case class SetNbt(tag: NBTCompound) extends LootFunction {
  override def toJson: Json = Encoder[SetNbt].apply(this)
}
object SetNbt {
  implicit val encoder: Encoder[SetNbt] = (a: SetNbt) =>
    Json.obj("function" := "set_nbt", "tag" := Mojangson.toMojangson(a.tag))
}
