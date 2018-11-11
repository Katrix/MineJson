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
package net.katsstuff.minejson.advancement

import scala.language.implicitConversions

import io.circe._
import io.circe.syntax._
import net.katsstuff.minejson._
import net.katsstuff.minejson.loottable.LootTableOrResourceId
import net.katsstuff.minejson.recipe.RecipeOrResourceId

case class Advancement(
    fileName: ResourceId,
    display: Display,
    parent: Option[AdvancementOrResourceId] = None,
    criteria: Map[String, Criteria],
    requirements: Seq[Seq[String]] = Nil,
    rewards: Option[Reward] = None
) extends AdvancementOrResourceId {
  override def id: ResourceId = fileName
}
object Advancement {

  implicit val encoder: Encoder[Advancement] = (a: Advancement) => {
    Json.obj(
      "display" := Json.obj(
        "icon"             := Json.obj("item" := a.display.icon.item, "data" := a.display.icon.data),
        "title"            := a.display.title,
        "frame"            := a.display.frame,
        "background"       := a.display.background,
        "description"      := a.display.description,
        "show_toast"       := a.display.showToast,
        "announce_to_chat" := a.display.announceToChat,
        "hidden"           := a.display.hidden
      ),
      "parent"       := a.parent.map(_.id),
      "criteria"     := a.criteria.mapValues(_.toJson),
      "requirements" := a.requirements,
      "rewards" := a.rewards.map { rewards =>
        Json.obj(
          "recipes"    := rewards.recipes.map(_.id),
          "loot"       := rewards.loot.map(_.id),
          "experience" := rewards.experience,
          "function"   := rewards.function
        )
      }
    )
  }
}

sealed trait AdvancementOrResourceId {
  def id: ResourceId
}
object AdvancementOrResourceId {
  implicit def mkId(id: ResourceId): AdvancementOrResourceId = AdvancementOrResourceIdAsResouceId(id)
}
case class AdvancementOrResourceIdAsResouceId(id: ResourceId) extends AdvancementOrResourceId

case class Display(
    icon: Icon,
    title: TextOrString,
    frame: Option[FrameType] = None,
    background: Option[ResourceId] = None,
    description: TextOrString,
    showToast: Option[Boolean] = None,
    announceToChat: Option[Boolean] = None,
    hidden: Option[Boolean] = None
)

case class Icon(item: ResourceId, data: Option[Int] = None)

sealed trait FrameType
object FrameType {
  case object Task      extends FrameType
  case object Goal      extends FrameType
  case object Challenge extends FrameType

  implicit val encoder: Encoder[FrameType] = {
    case Task      => Json.fromString("task")
    case Goal      => Json.fromString("goal")
    case Challenge => Json.fromString("challenge")
  }
}

case class Reward(
    recipes: Seq[RecipeOrResourceId] = Nil,
    loot: Seq[LootTableOrResourceId] = Nil,
    experience: Option[Int] = None,
    function: Option[String] = None
)
