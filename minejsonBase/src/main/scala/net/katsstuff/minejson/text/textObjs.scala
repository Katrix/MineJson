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
package net.katsstuff.minejson.text

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer

import net.katsstuff.minejson.text.serializer.{FormattingCodeSerializer, JsonTextSerializer, PlainTextSerializer}

//Partially taken from Sponge's Text
sealed trait Text {
  def format: TextFormat
  def format_=(format: TextFormat): Text = copyBase(format = format)

  def onShiftClick: Option[ShiftClickAction]
  def onShiftClick_=(action: Option[ShiftClickAction]): Text = copyBase(onShiftClick = action)
  def onShiftClick_=(action: ShiftClickAction):         Text = this.onShiftClick = Some(action)

  def onClick: Option[ClickAction]
  def onClick_=(action: Option[ClickAction]): Text = copyBase(onClick = action)
  def onClick_=(action: ClickAction):         Text = this.onClick = Some(action)

  def onHover: Option[HoverAction]
  def onHover_=(action: Option[HoverAction]): Text = copyBase(onHover = action)
  def onHover_=(action: HoverAction):         Text = this.onHover = Some(action)

  def copyBase(
      format: TextFormat = format,
      onShiftClick: Option[ShiftClickAction] = onShiftClick,
      onClick: Option[ClickAction] = onClick,
      onHover: Option[HoverAction] = onHover,
      children: Seq[Text] = children
  ): Text

  def children: Seq[Text]
  def children_=(children: Seq[Text]): Text = copyBase(children = children)

  def append(texts: Text*): Text = children = children ++ texts

  def toPlain:     String = PlainTextSerializer.serialize(this)
  def toJson:      String = JsonTextSerializer.serialize(this)
  def toCharCoded: String = FormattingCodeSerializer.serialize(this)

  def trim: Text = {
    val trimmedChildren = children.filter(_ != Text.Empty).map(_.trim)
    this.children = trimmedChildren
  }

  /**
    * Tries to merge two [[Text]]s together while preserving styling.
    * Both texts can't have children for this to work.
    *
    * @param other The text to combine with.
    * @return Some if the combination was successful, None otherwise
    */
  def merge(other: Text): Option[Text] = None

  /**
    * Tries to create a new [[Text]] where the children have been merged and
    * optimized.
    */
  def optimize: Text = {

    @tailrec
    def inner(acc: Seq[Text], current: Text, next: Text, rest: Seq[Text]): Seq[Text] = {

      current.merge(next) match {
        case Some(merged) =>
          if (rest.isEmpty) acc :+ merged else inner(acc, merged.optimize, rest.head.optimize, rest.tail)
        case None =>
          if (rest.isEmpty) acc :+ current :+ next else inner(acc :+ current, next, rest.head.optimize, rest.tail)
      }
    }

    //We merge with the parent without the children to ensure we don't get duplicated text
    //It also helps uncover Texts that serve no other purpose that having children
    children match {
      case Seq(head, next, rest @ _*) =>
        val newChildren = inner(Nil, head.optimize, next.optimize, rest)

        //We try to merge the first child into the parent.
        //If it fails, we set the children to the new children.
        //If it succeeds we set the children as the new children minus the head, and then optimize.
        copyBase(children = Nil)
          .merge(newChildren.head)
          .fold(copyBase(children = newChildren)) { merged =>
            merged.copyBase(children = merged.children ++ newChildren.tail).optimize
          }

      case Seq(single) => this.copyBase(children = Nil).merge(single.optimize).fold(this)(_.optimize)
      case Seq()       => this
    }
  }
}

object Text {

  final val Empty:   Text = LiteralText("")
  final val NewLine: Text = LiteralText("\n")

  def apply(string: String): LiteralText = LiteralText(string)
  def apply(anys: AnyRef*): Text =
    if (anys.length == 1 && anys.head.isInstanceOf[Text]) anys.head.asInstanceOf[Text]
    else {

      //This will most likely be a tight spot so we want as much speed as possible
      val builder       = new ArrayBuffer[Text]()
      var changedFormat = false

      var format = TextFormat.None
      var onClick:      Option[ClickAction]      = None
      var onShiftClick: Option[ShiftClickAction] = None
      var onHover:      Option[HoverAction]      = None
      val iterator = anys.iterator

      while (iterator.hasNext) {
        iterator.next() match {
          case newFormat: TextFormat =>
            changedFormat = true
            format = newFormat
          case style: CompositeTextStyle =>
            changedFormat = true
            format = format.copy(style = format.style.combine(style))
          case color: TextColor =>
            changedFormat = true
            format = format.copy(color = color)
          case style: TextStyle =>
            changedFormat = true
            format = format.copy(style = format.style.combine(CompositeTextStyle(style)))
          case newOnClick: ClickAction =>
            onClick = Some(newOnClick)
          case newOnHover: HoverAction =>
            onHover = Some(newOnHover)
          case newOnShiftClick: ShiftClickAction =>
            onShiftClick = Some(newOnShiftClick)
          case child: Text if child == Text.Empty => //Do nothing
          case child: Text =>
            builder.lastOption.flatMap(last => last.merge(child)) match {
              case Some(combined) => builder(builder.size - 1) = combined
              case None =>
                val childFormat       = format.combine(child.format)
                val childOnClick      = if (child.onClick.isEmpty) onClick else child.onClick
                val childOnHover      = if (child.onHover.isEmpty) onHover else child.onHover
                val childOnShiftClick = if (child.onShiftClick.isEmpty) onShiftClick else child.onShiftClick

                val newChild = child.copyBase(
                  format = childFormat,
                  onClick = childOnClick,
                  onHover = childOnHover,
                  onShiftClick = childOnShiftClick
                )

                builder.append(newChild)
            }
          case asText =>
            val child = asText match {
              case Translation(key, args @ _*) =>
                TranslateText(
                  key = key,
                  args = args,
                  format = format,
                  onClick = onClick,
                  onHover = onHover,
                  onShiftClick = onShiftClick
                )
              case Score(name, objective, value) =>
                ScoreText(
                  name = name,
                  objective = objective,
                  value = value,
                  format = format,
                  onClick = onClick,
                  onHover = onHover,
                  onShiftClick = onShiftClick
                )
              case Selector(selector) =>
                SelectorText(
                  selector = selector,
                  format = format,
                  onClick = onClick,
                  onHover = onHover,
                  onShiftClick = onShiftClick
                )
              case Keybind(key) =>
                KeybindText(
                  key = key,
                  format = format,
                  onClick = onClick,
                  onHover = onHover,
                  onShiftClick = onShiftClick
                )
              case simple =>
                LiteralText(
                  content = String.valueOf(simple),
                  format = format,
                  onClick = onClick,
                  onHover = onHover,
                  onShiftClick = onShiftClick
                )
            }

            changedFormat = false
            builder.lastOption.flatMap(last => last.merge(child)) match {
              case Some(combined) => builder(builder.size - 1) = combined
              case None           => builder.append(child)
            }
        }
      }

      if (changedFormat) {
        val child =
          Text.Empty.copyBase(onClick = onClick, onHover = onHover, onShiftClick = onShiftClick, format = format)
        builder.append(child)
      }

      if (builder.size == 1) builder.head
      else Text.Empty.copyBase(children = Seq(builder: _*))
    }
}

final case class LiteralText(
    content: String,
    format: TextFormat = TextFormat.None,
    onShiftClick: Option[ShiftClickAction] = None,
    onClick: Option[ClickAction] = None,
    onHover: Option[HoverAction] = None,
    children: Seq[Text] = Seq()
) extends Text {
  override def copyBase(
      format: TextFormat = format,
      onShiftClick: Option[ShiftClickAction] = onShiftClick,
      onClick: Option[ClickAction] = onClick,
      onHover: Option[HoverAction] = onHover,
      children: Seq[Text] = children
  ): Text =
    copy(format = format, onShiftClick = onShiftClick, onClick = onClick, onHover = onHover, children = children)

  override def merge(other: Text): Option[Text] = {
    if (this == Text.Empty) Some(other)
    else if (other == Text.Empty) Some(this)
    else {
      other match {
        case LiteralText(otherContent, `format`, `onShiftClick`, `onClick`, `onHover`, otherChildren) =>
          if (children.isEmpty) {
            Some(copy(content + otherContent, children = otherChildren))
          } else if (otherChildren.isEmpty) {
            Some(copy(children = children :+ LiteralText(otherContent)))
          } else None
        case _ => None
      }
    }
  }
}

case class Translation(key: String, args: AnyRef*)

final case class TranslateText(
    key: String,
    args: Seq[AnyRef] = Nil,
    format: TextFormat = TextFormat.None,
    onShiftClick: Option[ShiftClickAction] = None,
    onClick: Option[ClickAction] = None,
    onHover: Option[HoverAction] = None,
    children: Seq[Text] = Seq()
) extends Text {
  override def copyBase(
      format: TextFormat = format,
      onShiftClick: Option[ShiftClickAction] = onShiftClick,
      onClick: Option[ClickAction] = onClick,
      onHover: Option[HoverAction] = onHover,
      children: Seq[Text] = children
  ): Text =
    copy(format = format, onShiftClick = onShiftClick, onClick = onClick, onHover = onHover, children = children)

  override def merge(other: Text): Option[Text] = None
}

case class Score(name: String, objective: String, value: Option[String] = None)

final case class ScoreText(
    name: String,
    objective: String,
    value: Option[String] = None,
    format: TextFormat = TextFormat.None,
    onShiftClick: Option[ShiftClickAction] = None,
    onClick: Option[ClickAction] = None,
    onHover: Option[HoverAction] = None,
    children: Seq[Text] = Seq()
) extends Text {
  override def copyBase(
      format: TextFormat = format,
      onShiftClick: Option[ShiftClickAction] = onShiftClick,
      onClick: Option[ClickAction] = onClick,
      onHover: Option[HoverAction] = onHover,
      children: Seq[Text] = children
  ): Text =
    copy(format = format, onShiftClick = onShiftClick, onClick = onClick, onHover = onHover, children = children)

  override def merge(other: Text): Option[Text] = None
}

case class Selector(selector: String)

final case class SelectorText(
    selector: String,
    format: TextFormat = TextFormat.None,
    onShiftClick: Option[ShiftClickAction] = None,
    onClick: Option[ClickAction] = None,
    onHover: Option[HoverAction] = None,
    children: Seq[Text] = Seq()
) extends Text {
  override def copyBase(
      format: TextFormat = format,
      onShiftClick: Option[ShiftClickAction] = onShiftClick,
      onClick: Option[ClickAction] = onClick,
      onHover: Option[HoverAction] = onHover,
      children: Seq[Text] = children
  ): Text =
    copy(format = format, onShiftClick = onShiftClick, onClick = onClick, onHover = onHover, children = children)

  override def merge(other: Text): Option[Text] = None
}

case class Keybind(key: String)

final case class KeybindText(
    key: String,
    format: TextFormat = TextFormat.None,
    onShiftClick: Option[ShiftClickAction] = None,
    onClick: Option[ClickAction] = None,
    onHover: Option[HoverAction] = None,
    children: Seq[Text] = Seq()
) extends Text {
  override def copyBase(
      format: TextFormat = format,
      onShiftClick: Option[ShiftClickAction] = onShiftClick,
      onClick: Option[ClickAction] = onClick,
      onHover: Option[HoverAction] = onHover,
      children: Seq[Text] = children
  ): Text =
    copy(format = format, onShiftClick = onShiftClick, onClick = onClick, onHover = onHover, children = children)

  override def merge(other: Text): Option[Text] = None
}
