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
package net.katsstuff.minejson.recipe

import scala.language.implicitConversions

import io.circe._
import io.circe.syntax._
import net.katsstuff.minejson.ResourceId
import net.katsstuff.typenbt.{Mojangson, NBTCompound}

trait Recipe extends RecipeOrResourceId {
  def fileName:   ResourceId
  def tpe:        RecipeType
  def group:      Option[ResourceId]
  def result:     RecipeResultItemOrResourceId
  def conditions: Seq[RecipeCondition]
  def toJson:     Json

  override def id: ResourceId = fileName
}

sealed trait RecipeOrResourceId {
  def id: ResourceId
}
object RecipeOrResourceId {
  implicit def mkId(id: ResourceId): RecipeOrResourceId = RecipeOrResourceIdAsResourceId(id)
}
case class RecipeOrResourceIdAsResourceId(id: ResourceId) extends RecipeOrResourceId

case class RawRecipe(
    fileName: ResourceId,
    tpe: RecipeType,
    group: Option[ResourceId] = None,
    pattern: List[String] = Nil,
    key: Map[Char, RecipeIngredient] = Map.empty,
    ingredient: Option[RecipeIngredient] = None,
    ingredients: Seq[RecipeIngredient] = Nil,
    result: RecipeResultItemOrResourceId,
    experience: Option[Double] = None,
    cookingTime: Option[Int] = None,
    conditions: Seq[RecipeCondition] = Nil
) extends Recipe {
  override def id:     ResourceId = fileName
  override def toJson: Json       = Encoder[RawRecipe].apply(this)
}
object RawRecipe {
  implicit val encoder: Encoder[RawRecipe] = (a: RawRecipe) =>
    Json.obj(
      "type" := a.tpe,
      "group" := a.group,
      "pattern" := a.pattern,
      "key" := a.key.map(t => t._1.toString -> t._2.toJson),
      "ingredient" := a.ingredient.map(_.toJson),
      "ingredients" := a.ingredients.map(_.toJson),
      "result" := a.result,
      "experience" := a.experience,
      "cookingtime" := a.cookingTime,
      "conditions" := a.conditions.map(_.toJson)
  )
}

case class ShapedRecipe(
    fileName: ResourceId,
    group: Option[ResourceId],
    pattern: List[String],
    key: Map[Char, RecipeIngredient],
    result: RecipeResultItemOrResourceId,
    conditions: Seq[RecipeCondition] = Nil
) extends Recipe {
  override def tpe:    RecipeType = RecipeType.CraftingShaped
  override def toJson: Json       = Encoder[ShapedRecipe].apply(this)
}
object ShapedRecipe {
  implicit val encoder: Encoder[ShapedRecipe] = (a: ShapedRecipe) =>
    Json.obj(
      "type" := a.tpe,
      "group" := a.group,
      "pattern" := a.pattern,
      "key" := a.key.map(t => t._1.toString -> t._2.toJson),
      "result" := a.result,
      "conditions" := a.conditions.map(_.toJson)
  )
}

case class ShapelessRecipe(
    fileName: ResourceId,
    group: Option[ResourceId],
    ingredients: Seq[RecipeIngredient],
    result: RecipeResultItemOrResourceId,
    conditions: Seq[RecipeCondition] = Nil
) extends Recipe {
  override def tpe:    RecipeType = RecipeType.CraftingShapeless
  override def toJson: Json       = Encoder[ShapelessRecipe].apply(this)
}
object ShapelessRecipe {
  implicit val encoder: Encoder[ShapelessRecipe] = (a: ShapelessRecipe) =>
    Json.obj(
      "type" := a.tpe,
      "group" := a.group,
      "ingredients" := a.ingredients.map(_.toJson),
      "result" := a.result,
      "conditions" := a.conditions.map(_.toJson)
  )
}

case class SmeltingRecipe(
    fileName: ResourceId,
    group: Option[ResourceId] = None,
    ingredient: Option[RecipeIngredient] = None,
    result: RecipeResultItemOrResourceId,
    experience: Option[Double] = None,
    cookingTime: Option[Int] = None,
    conditions: Seq[RecipeCondition] = Nil
) extends Recipe {
  override def tpe:    RecipeType = RecipeType.Smelting
  override def toJson: Json       = Encoder[SmeltingRecipe].apply(this)
}
object SmeltingRecipe {
  implicit val encoder: Encoder[SmeltingRecipe] = (a: SmeltingRecipe) =>
    Json.obj(
      "type" := a.tpe,
      "group" := a.group,
      "ingredient" := a.ingredient.map(_.toJson),
      "result" := a.result,
      "experience" := a.experience,
      "cookingtime" := a.cookingTime,
      "conditions" := a.conditions.map(_.toJson)
  )
}

case class OreShapedRecipe(
    fileName: ResourceId,
    group: Option[ResourceId],
    pattern: List[String],
    key: Map[Char, RecipeIngredient],
    result: RecipeResultItemOrResourceId,
    mirror: Boolean = true,
    conditions: Seq[RecipeCondition] = Nil
) extends Recipe {
  override def tpe:    RecipeType = RecipeType.OreShaped
  override def toJson: Json       = Encoder[OreShapedRecipe].apply(this)
}
object OreShapedRecipe {
  implicit val encoder: Encoder[OreShapedRecipe] = (a: OreShapedRecipe) =>
    Json.obj(
      "type" := a.tpe,
      "group" := a.group,
      "pattern" := a.pattern,
      "key" := a.key.map(t => t._1.toString -> t._2.toJson),
      "result" := a.result,
      "mirror" := a.mirror,
      "conditions" := a.conditions.map(_.toJson)
  )
}

case class OreShapelessRecipe(
    fileName: ResourceId,
    group: Option[ResourceId],
    ingredients: Seq[RecipeIngredient],
    result: RecipeResultItemOrResourceId,
    conditions: Seq[RecipeCondition] = Nil
) extends Recipe {
  override def tpe:    RecipeType = RecipeType.OreShapeless
  override def toJson: Json       = Encoder[OreShapelessRecipe].apply(this)
}
object OreShapelessRecipe {
  implicit val encoder: Encoder[OreShapelessRecipe] = (a: OreShapelessRecipe) =>
    Json.obj(
      "type" := a.tpe,
      "group" := a.group,
      "ingredients" := a.ingredients.map(_.toJson),
      "result" := a.result,
      "conditions" := a.conditions.map(_.toJson)
  )
}

trait RecipeType {
  def name: ResourceId
}
object RecipeType {
  object CraftingShaped          extends RecipeType { def name: ResourceId = "crafting_shaped"           }
  object CraftingShapeless       extends RecipeType { def name: ResourceId = "crafting_shapeless"        }
  object CraftingSpecialArmorDye extends RecipeType { def name: ResourceId = "crafting_special_armordye" }
  object CraftingSpecialBannerAddPattern extends RecipeType {
    def name: ResourceId = "crafting_special_banneraddpattern"
  }
  object CraftingSpecialBannerDuplicate extends RecipeType { def name: ResourceId = "crafting_special_bannerduplicate" }
  object CraftingSpecialBookCloning     extends RecipeType { def name: ResourceId = "crafting_special_bookcloning"     }
  object CraftingSpecialFireworkRocket  extends RecipeType { def name: ResourceId = "crafting_special_firework_rocket" }
  object CraftingSpecialFireworkStar    extends RecipeType { def name: ResourceId = "crafting_special_firework_star"   }
  object CraftingSpecialFireworkStarFade extends RecipeType {
    def name: ResourceId = "crafting_special_firework_star_fade"
  }
  object CraftingSpecialMapCloning   extends RecipeType { def name: ResourceId = "crafting_special_mapcloning"   }
  object CraftingSpecialMapExtending extends RecipeType { def name: ResourceId = "crafting_special_mapextending" }
  object CraftingSpecialRepairItem   extends RecipeType { def name: ResourceId = "crafting_special_repairitem"   }
  object CraftingSpecialShieldDecoration extends RecipeType {
    def name: ResourceId = "crafting_special_shielddecoration"
  }
  object CraftingSpecialShulkerBoxColoring extends RecipeType {
    def name: ResourceId = "crafting_special_shulkerboxcoloring"
  }
  object CraftingSpecialTippedArrow extends RecipeType { def name: ResourceId = "crafting_special_tippedarrow" }

  object Smelting extends RecipeType { def name: ResourceId = "smelting" }

  object OreShaped    extends RecipeType { def name: ResourceId = "forge:ore_shaped"    }
  object OreShapeless extends RecipeType { def name: ResourceId = "forge:ore_shapeless" }

  implicit val encoder: Encoder[RecipeType] = _.name.asJson
}

trait RecipeIngredient {
  def toJson: Json
}

case class ItemRecipeIngredient(item: ResourceId, data: Option[Int] = None, tag: Option[String] = None)
    extends RecipeIngredient {
  override def toJson: Json = Encoder[ItemRecipeIngredient].apply(this)
}
object ItemRecipeIngredient {
  implicit val encoder: Encoder[ItemRecipeIngredient] = (a: ItemRecipeIngredient) =>
    Json.obj("item" := a.item, "data" := a.data, "tag" := a.tag)
}
case object EmptyIngredient extends RecipeIngredient {
  override def toJson: Json = Json.obj("type" := "minecraft:empty")
}
case class ItemNbtRecipeIngredient(
    item: ResourceId,
    data: Option[Int] = None,
    nbt: Option[NBTCompound] = None,
    tag: Option[String] = None
) extends RecipeIngredient {
  override def toJson: Json = Encoder[ItemNbtRecipeIngredient].apply(this)
}
object ItemNbtRecipeIngredient {
  implicit val encoder: Encoder[ItemNbtRecipeIngredient] = (a: ItemNbtRecipeIngredient) =>
    Json.obj(
      "type" := "minecraft:item_nbt",
      "item" := a.item,
      "data" := a.data,
      "nbt" := a.nbt.map(Mojangson.serialize),
      "tag" := a.tag
  )
}
case class OreRecipeIngredient(ore: String) extends RecipeIngredient {
  override def toJson: Json = Json.obj("type" := "forge:ore_dict", "ore" := ore)
}

case class RecipeResult(item: ResourceId, data: Option[Int] = None, tag: Option[String] = None)
object RecipeResult {
  implicit val encoder: Encoder[RecipeResult] = (a: RecipeResult) =>
    Json.obj("item" := a.item, "data" := a.data, "tag" := a.tag)
}

sealed trait RecipeResultItemOrResourceId
object RecipeResultItemOrResourceId {
  implicit def fromId(id: ResourceId): RecipeResultItemOrResourceId = RecipeResultItemOrResourceIdAsResouceId(id)
  implicit def fromResult(result: RecipeResult): RecipeResultItemOrResourceId =
    RecipeResultItemOrResourceIdAsRecipeResult(result)

  implicit val encoder: Encoder[RecipeResultItemOrResourceId] = {
    case RecipeResultItemOrResourceIdAsResouceId(id)        => id.asJson
    case RecipeResultItemOrResourceIdAsRecipeResult(result) => result.asJson
  }
}

case class RecipeResultItemOrResourceIdAsResouceId(id: ResourceId)         extends RecipeResultItemOrResourceId
case class RecipeResultItemOrResourceIdAsRecipeResult(value: RecipeResult) extends RecipeResultItemOrResourceId

trait RecipeCondition {
  def toJson: Json
}

case class ModLoadedCondition(modid: String) extends RecipeCondition {
  override def toJson: Json = Json.obj("type" := "forge:mod_loaded", "modid" := modid)
}
case class ItemExistsCondition(item: ResourceId) extends RecipeCondition {
  override def toJson: Json = Json.obj("type" := "forge:item_exists", "item" := item)
}
case class NotCondition(value: RecipeCondition) extends RecipeCondition {
  override def toJson: Json = Json.obj("type" := "forge:not", "value" := value.toJson)
}
case class OrCondition(values: Seq[RecipeCondition]) extends RecipeCondition {
  override def toJson: Json = Json.obj("type" := "forge:or", "values" := values.map(_.toJson))
}
case class AndCondition(values: Seq[RecipeCondition]) extends RecipeCondition {
  override def toJson: Json = Json.obj("type" := "forge:and", "values" := values.map(_.toJson))
}
case object FalseCondition extends RecipeCondition {
  override def toJson: Json = Json.obj("type" := "forge:false")
}
