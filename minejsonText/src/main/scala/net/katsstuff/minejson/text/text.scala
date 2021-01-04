package net.katsstuff.minejson

import scala.annotation.tailrec

package object text {

  //Actions

  val ClickAction: action.ClickAction.type = action.ClickAction
  type ClickAction = action.ClickAction

  val HoverText: action.HoverText.type = action.HoverText
  type HoverText = action.HoverText

  val InsertionText: action.InsertionText.type = action.InsertionText
  type InsertionText = action.InsertionText

  //Format

  val TextFormat: format.TextFormat.type = format.TextFormat
  type TextFormat = format.TextFormat

  val CompositeTextStyle: format.CompositeTextStyle.type = format.CompositeTextStyle
  type CompositeTextStyle = format.CompositeTextStyle

  //Styles

  val TextStyle: format.TextStyle.type = format.TextStyle
  type TextStyle = format.TextStyle

  val Bold: format.TextStyle.Bold.type = format.TextStyle.Bold
  type Bold = format.TextStyle.Bold.type

  val Underlined: format.TextStyle.Underlined.type = format.TextStyle.Underlined
  type Underlined = format.TextStyle.Underlined.type

  val Italic: format.TextStyle.Italic.type = format.TextStyle.Italic
  type Italic = format.TextStyle.Italic.type

  val StrikeThrough: format.TextStyle.StrikeThrough.type = format.TextStyle.StrikeThrough
  type StrikeThrough = format.TextStyle.StrikeThrough.type

  val Obfuscated: format.TextStyle.Obfuscated.type = format.TextStyle.Obfuscated
  type Obfuscated = format.TextStyle.Obfuscated.type

  val TextFont: format.TextFont.type = format.TextFont
  type TextFont = format.TextFont

  //Colors

  val TextColor: format.TextColor.type = format.TextColor
  type TextColor = format.TextColor

  val NoColor: format.TextColor.NoColor.type = format.TextColor.NoColor
  type NoColor = format.TextColor.NoColor.type

  val Black: format.TextColor.Black.type = format.TextColor.Black
  type Black = format.TextColor.Black.type

  val DarkBlue: format.TextColor.DarkBlue.type = format.TextColor.DarkBlue
  type DarkBlue = format.TextColor.DarkBlue.type

  val DarkGreen: format.TextColor.DarkGreen.type = format.TextColor.DarkGreen
  type DarkGreen = format.TextColor.DarkGreen.type

  val DarkAqua: format.TextColor.DarkAqua.type = format.TextColor.DarkAqua
  type DarkAqua = format.TextColor.DarkAqua.type

  val DarkRed: format.TextColor.DarkRed.type = format.TextColor.DarkRed
  type DarkRed = format.TextColor.DarkRed.type

  val DarkPurple: format.TextColor.DarkPurple.type = format.TextColor.DarkPurple
  type DarkPurple = format.TextColor.DarkPurple.type

  val Gold: format.TextColor.Gold.type = format.TextColor.Gold
  type Gold = format.TextColor.Gold.type

  val Gray: format.TextColor.Gray.type = format.TextColor.Gray
  type Gray = format.TextColor.Gray.type

  val DarkGray: format.TextColor.DarkGray.type = format.TextColor.DarkGray
  type DarkGray = format.TextColor.DarkGray.type

  val Blue: format.TextColor.Blue.type = format.TextColor.Blue
  type Blue = format.TextColor.Blue.type

  val Green: format.TextColor.Green.type = format.TextColor.Green
  type Green = format.TextColor.Green.type

  val Aqua: format.TextColor.Aqua.type = format.TextColor.Aqua
  type Aqua = format.TextColor.Aqua.type

  val Red: format.TextColor.Red.type = format.TextColor.Red
  type Red = format.TextColor.Red.type

  val LightPurple: format.TextColor.LightPurple.type = format.TextColor.LightPurple
  type LightPurple = format.TextColor.LightPurple.type

  val Yellow: format.TextColor.Yellow.type = format.TextColor.Yellow
  type Yellow = format.TextColor.Yellow.type

  val White: format.TextColor.White.type = format.TextColor.White
  type White = format.TextColor.White.type

  val Reset: format.TextColor.Reset.type = format.TextColor.Reset
  type Reset = format.TextColor.Reset.type

  val Hex: format.TextColor.Hex.type = format.TextColor.Hex
  type Hex = format.TextColor.Hex

  implicit class TextSyntax(private val sc: StringContext) extends AnyVal {

    /**
      * Create a [[Text]] representation of this string.
      * Really just a nicer way of saying [[Text#of(anyRef: AnyRef*]]
      */
    def t(args: Any*): Text = {
      sc.checkLengths(args)

      @tailrec
      def inner(partsLeft: Seq[String], argsLeft: Seq[Any], res: Seq[AnyRef]): Seq[AnyRef] =
        if (argsLeft == Nil) res
        else {
          inner(partsLeft.tail, argsLeft.tail, (res :+ argsLeft.head.asInstanceOf[AnyRef]) :+ partsLeft.head)
        }

      Text(inner(sc.parts.tail, args, Seq(sc.parts.head)): _*)
    }
  }
}
