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
package net.katsstuff.minejson.text.serializer

import scala.util.Try

import io.circe._
import io.circe.syntax._
import io.circe.parser
import cats.syntax.either._
import net.katsstuff.minejson.text._
import net.katsstuff.typenbt.{Mojangson, NBTCompound}

object JsonTextSerializer extends TextSerializer {

  implicit val textEncoder: Encoder[Text] = (text: Text) => {
    val common = commonJsonWrite(text)

    val extra = text match {
      case text: LiteralText => Seq("text" := text.content)
      case text: TranslateText =>
        Seq(
          "translate" := text.key,
          "with"      := text.args.map(_.toString)
        )
      case text: ScoreText =>
        Seq(
          "score" := Json.obj(
            "name"      := text.name,
            "objective" := text.objective,
            "value"     := text.value
          )
        )
      case text: SelectorText => Seq("selector" := text.selector)
      case text: KeybindText  => Seq("keybind"  := text.key)
      case text: NBTText =>
        Seq(
          "nbt"       := text.nbtPath,
          "interpret" := text.interpret,
          "block"     := text.block,
          "entity"    := text.entity,
          "storage"   := text.storage
        )
    }

    Json.obj(extra ++ common: _*)
  }

  implicit val textDecoder: Decoder[Text] = (c: HCursor) =>
    c.get[String]("text")
      .flatMap(text => commonJsonRead(Text(text), c))
      .orElse {
        for {
          translate <- c.get[String]("translate")
          args      <- c.getOrElse[Seq[String]]("with")(Nil)
          res       <- commonJsonRead(TranslateText(translate, args), c)
        } yield res
      }
      .orElse {
        val score = c.downField("score")
        for {
          name      <- score.get[String]("name")
          objective <- score.get[String]("objective")
          value     <- score.get[Option[String]]("value")
          res       <- commonJsonRead(ScoreText(name, objective, value), c)
        } yield res
      }
      .orElse(c.get[String]("selector").flatMap(selector => commonJsonRead(SelectorText(selector), c)))
      .orElse(c.get[String]("keybind").flatMap(key => commonJsonRead(KeybindText(key), c)))

  implicit val colorEncoder: Encoder[TextColor] = {
    case TextColor.NoColor     => Json.Null
    case TextColor.Black       => "black".asJson
    case TextColor.DarkBlue    => "dark_blue".asJson
    case TextColor.DarkGreen   => "dark_green".asJson
    case TextColor.DarkAqua    => "dark_aqua".asJson
    case TextColor.DarkRed     => "dark_red".asJson
    case TextColor.DarkPurple  => "dark_purple".asJson
    case TextColor.Gold        => "gold".asJson
    case TextColor.Gray        => "gray".asJson
    case TextColor.DarkGray    => "dark_gray".asJson
    case TextColor.Blue        => "blue".asJson
    case TextColor.Green       => "green".asJson
    case TextColor.Aqua        => "aqua".asJson
    case TextColor.Red         => "red".asJson
    case TextColor.LightPurple => "light_purple".asJson
    case TextColor.Yellow      => "yellow".asJson
    case TextColor.White       => "white".asJson
    case TextColor.Reset       => "reset".asJson
    case TextColor.Hex(color)  => s"#$color".asJson
  }

  private val hexPattern = """#(\d{6})""".r

  implicit val colorDecoder: Decoder[TextColor] = (c: HCursor) => {
    if (c.value.isNull) Right(TextColor.NoColor)
    else {
      c.as[String].flatMap {
        case "black"           => Right(TextColor.Black)
        case "dark_blue"       => Right(TextColor.DarkBlue)
        case "dark_green"      => Right(TextColor.DarkGreen)
        case "dark_aqua"       => Right(TextColor.DarkAqua)
        case "dark_red"        => Right(TextColor.DarkRed)
        case "dark_purple"     => Right(TextColor.DarkPurple)
        case "gold"            => Right(TextColor.Gold)
        case "gray"            => Right(TextColor.Gray)
        case "dark_gray"       => Right(TextColor.DarkGray)
        case "blue"            => Right(TextColor.Blue)
        case "green"           => Right(TextColor.Green)
        case "aqua"            => Right(TextColor.Aqua)
        case "red"             => Right(TextColor.Red)
        case "light_purple"    => Right(TextColor.LightPurple)
        case "yellow"          => Right(TextColor.Yellow)
        case "white"           => Right(TextColor.White)
        case "reset"           => Right(TextColor.Reset)
        case hexPattern(color) => Right(TextColor.Hex(color))
        case other             => Left(DecodingFailure(s"$other is not a valid color", c.history))
      }
    }
  }

  implicit val shiftClickActionEncoder: Encoder[InsertionText] = {
    case InsertionText(insert) => insert.asJson
  }

  implicit val clickActionEncoder: Encoder[ClickAction] = {
    case ClickAction.OpenUrl(url) =>
      Json.obj(
        "action" := "open_url",
        "value"  := url
      )
    case ClickAction.OpenFile(file) =>
      Json.obj(
        "action" := "open_file",
        "value"  := file
      )
    case ClickAction.RunCommand(command) =>
      Json.obj(
        "action" := "run_command",
        "value"  := command
      )
    case ClickAction.SuggestCommand(command) =>
      Json.obj(
        "action" := "suggest_command",
        "value"  := command
      )
    case ClickAction.ChangePage(page) =>
      Json.obj(
        "action" := "change_page",
        "value"  := page
      )
    case ClickAction.CopyToClipboard(value) =>
      Json.obj(
        "action" := "copy_to_clipboard",
        "value"  := value
      )
  }

  implicit val clickActionDecoder: Decoder[ClickAction] = (c: HCursor) => {
    c.get[String]("value").flatMap { value =>
      c.get[String]("action").flatMap {
        case "open_url"          => Right(ClickAction.OpenUrl(value))
        case "open_file"         => Right(ClickAction.OpenFile(value))
        case "run_command"       => Right(ClickAction.RunCommand(value))
        case "suggest_command"   => Right(ClickAction.SuggestCommand(value))
        case "change_page"       => Right(ClickAction.ChangePage(value))
        case "copy_to_clipboard" => Right(ClickAction.CopyToClipboard(value))
        case other               => Left(DecodingFailure(s"$other is not a valid click action", c.downField("action").history))
      }
    }
  }

  implicit val hoverActionEncoder: Encoder[HoverText] = {
    case HoverText.ShowText(text) =>
      Json.obj(
        "action" := "show_text",
        "value"  := text
      )
    case HoverText.ShowItem(nbt) =>
      Json.obj(
        "action" := "show_item",
        "value"  := Mojangson.serialize(nbt)
      )
    case HoverText.ShowEntity(nbt) =>
      Json.obj(
        "action" := "show_entity",
        "value"  := Mojangson.serialize(nbt)
      )
  }

  implicit val hoverActionDecoder: Decoder[HoverText] = (c: HCursor) => {
    def nbtValue =
      c.get[String]("value")
        .flatMap { s =>
          Either
            .catchNonFatal(Mojangson.deserialize(s).get)
            .leftMap(e => DecodingFailure(e.getMessage, c.downField("value").history))
            .map(_.value)
        }
        .flatMap {
          case compound: NBTCompound => Right(compound)
          case other =>
            Left(
              DecodingFailure(
                s"Found invalid json type for hover action ${other.nbtType.id}",
                c.downField("value").history
              )
            )
        }

    c.get[String]("action").flatMap {
      case "show_text"   => c.get[Text]("value").map(HoverText.ShowText)
      case "show_item"   => nbtValue.map(HoverText.ShowItem)
      case "show_entity" => nbtValue.map(HoverText.ShowItem)
      case other         => Left(DecodingFailure(s"$other is not a valid hover action", c.downField("action").history))
    }
  }

  private def commonJsonWrite(text: Text): Seq[(String, Json)] = {
    val format = text.format

    val extra         = if (text.children.nonEmpty) Seq("extra" := text.children) else Nil
    val color         = format.color
    val bold          = format.style.styles.get(TextStyle.Bold)
    val underlined    = format.style.styles.get(TextStyle.Underlined)
    val italic        = format.style.styles.get(TextStyle.Italic)
    val strikeThrough = format.style.styles.get(TextStyle.StrikeThrough)
    val obfuscated    = format.style.styles.get(TextStyle.Obfuscated)
    val insertion     = text.insertionText
    val clickAction   = text.clickAction
    val hoverAction   = text.hoverText
    extra ++ Seq(
      "color"         := color,
      "bold"          := bold,
      "underlined"    := underlined,
      "italic"        := italic,
      "strikethrough" := strikeThrough,
      "obfuscated"    := obfuscated,
      "insertion"     := insertion,
      "clickEvent"    := clickAction,
      "hoverEvent"    := hoverAction
    )
  }

  def commonJsonRead(text: Text, c: HCursor): Either[DecodingFailure, Text] =
    for {
      color         <- c.getOrElse[TextColor]("color")(TextColor.NoColor)
      bold          <- c.get[Option[Boolean]]("bold").map(TextStyle.Bold -> _)
      underlined    <- c.get[Option[Boolean]]("underlinded").map(TextStyle.Underlined -> _)
      italic        <- c.get[Option[Boolean]]("italic").map(TextStyle.Italic -> _)
      strikeThrough <- c.get[Option[Boolean]]("strikethrough").map(TextStyle.StrikeThrough -> _)
      obfuscated    <- c.get[Option[Boolean]]("obfuscated").map(TextStyle.Obfuscated -> _)
      insertion     <- c.get[Option[String]]("insertion")
      clickEvent    <- c.get[Option[ClickAction]]("clickEvent")
      hoverEvent    <- c.get[Option[HoverText]]("hoverAction")
      children      <- c.getOrElse[Seq[Text]]("extra")(Nil)

    } yield {
      val compositeTextStyle = CompositeTextStyle.fromOptions(Seq(bold, underlined, italic, strikeThrough, obfuscated))
      text.copyBase(
        format = TextFormat(color, compositeTextStyle),
        insertionText = insertion,
        clickAction = clickEvent,
        hoverText = hoverEvent,
        children = children
      )
    }

  def serialize(text: Text): String = text.asJson.noSpaces.replace("\\n", "\n") //TODO: Fix this in a better way

  override def deserialize(text: String): Try[Text] = parser.parse(text).flatMap(_.as[Text]).toTry

  def deserializeThrow(text: String): Text = deserialize(text).get
}
