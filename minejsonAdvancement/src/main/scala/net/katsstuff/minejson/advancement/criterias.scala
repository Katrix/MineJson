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

import io.circe._
import io.circe.syntax._
import net.katsstuff.minejson.recipe.RecipeOrResourceId
import net.katsstuff.minejson.{RangeOrSingle, ResourceId}

trait Criteria {
  def toJson: Json
}

case class BredAnimals(child: Option[Entity] = None, parent: Option[Entity] = None, partner: Option[Entity] = None)
    extends Criteria {
  override def toJson: Json = Encoder[BredAnimals].apply(this)
}
object BredAnimals {
  implicit val encoder: Encoder[BredAnimals] = (a: BredAnimals) =>
    Json.obj(
      "trigger"    := "minecraft:bred_animals",
      "conditions" := Json.obj("child" := a.child, "parent" := a.parent, "partner" := a.parent)
  )
}

case class BrewedPotion(potion: Option[ResourceId] = None) extends Criteria {
  override def toJson: Json = Encoder[BrewedPotion].apply(this)
}
object BrewedPotion {
  implicit val encoder: Encoder[BrewedPotion] = (a: BrewedPotion) =>
    Json.obj("trigger" := "minecraft:brewed_potion", "conditions" := Json.obj("potion" := a.potion))
}

case class ChangedDimension(from: Option[Dimension] = None, to: Option[Dimension] = None) extends Criteria {
  override def toJson: Json = Encoder[ChangedDimension].apply(this)
}
object ChangedDimension {
  implicit val encoder: Encoder[ChangedDimension] = (a: ChangedDimension) =>
    Json.obj("trigger" := "minecraft:changed_dimension", "conditions" := Json.obj("from" := a.from, "to" := a.to))
}

case class ChanneledLightning(victims: Seq[Entity]) extends Criteria {
  override def toJson: Json = Encoder[ChanneledLightning].apply(this)
}
object ChanneledLightning {
  implicit val encoder: Encoder[ChanneledLightning] = (a: ChanneledLightning) =>
    Json.obj("trigger" := "minecraft:channeled_lightning", "conditions" := Json.obj("victims" := a.victims))
}

case class ConstructBeacon(level: Option[RangeOrSingle] = None) extends Criteria {
  override def toJson: Json = Encoder[ConstructBeacon].apply(this)
}
object ConstructBeacon {
  implicit val encoder: Encoder[ConstructBeacon] = (a: ConstructBeacon) =>
    Json.obj("trigger" := "minecraft:construct_beacon", "conditions" := Json.obj("level" := a.level))
}

case class ConsumeItem(item: Option[Item] = None) extends Criteria {
  override def toJson: Json = Encoder[ConsumeItem].apply(this)
}
object ConsumeItem {
  implicit val encoder: Encoder[ConsumeItem] = (a: ConsumeItem) =>
    Json.obj("trigger" := "minecraft:consume_item", "conditions" := Json.obj("item" := a.item))
}

case class CuredZombieVillager(villager: Option[Entity] = None, zombie: Option[Entity] = None) extends Criteria {
  override def toJson: Json = Encoder[CuredZombieVillager].apply(this)
}
object CuredZombieVillager {
  implicit val encoder: Encoder[CuredZombieVillager] = (a: CuredZombieVillager) =>
    Json.obj(
      "trigger"    := "minecraft:cured_zombie_villager",
      "conditions" := Json.obj("villager" := a.villager, "zombie" := a.zombie)
  )
}

case class EffectsChanged(effects: Seq[Effect] = Nil) extends Criteria {
  override def toJson: Json = Encoder[EffectsChanged].apply(this)
}
object EffectsChanged {
  implicit val encoder: Encoder[EffectsChanged] = (a: EffectsChanged) =>
    Json.obj(
      "trigger"    := "minecraft:effects_changed",
      "conditions" := Json.obj("effects" := a.effects.map(e => e.name.toString := e).toMap)
  )
}

case class EnchantedItem(item: Option[Item] = None, levels: Option[RangeOrSingle] = None) extends Criteria {
  override def toJson: Json = Encoder[EnchantedItem].apply(this)
}
object EnchantedItem {
  implicit val encoder: Encoder[EnchantedItem] = (a: EnchantedItem) =>
    Json.obj("trigger" := "minecraft:enchanted_item", "conditions" := Json.obj("item" := a.item, "levels" := a.levels))
}

case class EnterBlock(block: Option[ResourceId] = None, state: Map[String, String] = Map.empty) extends Criteria {
  override def toJson: Json = Encoder[EnterBlock].apply(this)
}
object EnterBlock {
  implicit val encoder: Encoder[EnterBlock] = (a: EnterBlock) =>
    Json.obj("trigger" := "minecraft:enter_block", "conditions" := Json.obj("block" := a.block, "state" := a.state))
}

case class EntityHurtPlayer(damage: Option[Damage] = None) extends Criteria {
  override def toJson: Json = Encoder[EntityHurtPlayer].apply(this)
}
object EntityHurtPlayer {
  implicit val encoder: Encoder[EntityHurtPlayer] = (a: EntityHurtPlayer) =>
    Json.obj("trigger" := "minecraft:entity_hurt_player", "conditions" := Json.obj("damage" := a.damage))
}

case class EntityKilledPlayer(entity: Option[Entity] = None, killingBlow: Option[DamageType] = None) extends Criteria {
  override def toJson: Json = Encoder[EntityKilledPlayer].apply(this)
}
object EntityKilledPlayer {
  implicit val encoder: Encoder[EntityKilledPlayer] = (a: EntityKilledPlayer) =>
    Json.obj(
      "trigger"    := "minecraft:entity_killed_player",
      "conditions" := Json.obj("entity" := a.entity, "killing_blow" := a.killingBlow)
  )
}

case class FilledBucket(item: Option[Item] = None) extends Criteria {
  override def toJson: Json = Encoder[FilledBucket].apply(this)
}
object FilledBucket {
  implicit val encoder: Encoder[FilledBucket] = (a: FilledBucket) =>
    Json.obj("trigger" := "minecraft:filled_bucket", "conditions" := Json.obj("item" := a.item))
}

case class FishingRodHooked(entity: Option[Entity] = None, item: Option[Item] = None, rod: Option[Item] = None)
    extends Criteria {
  override def toJson: Json = Encoder[FishingRodHooked].apply(this)
}
object FishingRodHooked {
  implicit val encoder: Encoder[FishingRodHooked] = (a: FishingRodHooked) =>
    Json.obj(
      "trigger"    := "minecraft:fishing_rod_hooked",
      "conditions" := Json.obj("entity" := a.entity, "item" := a.item, "rod" := a.rod)
  )
}

case object Impossible extends Criteria {
  override def toJson: Json = Json.obj("trigger" := "minecraft:impossible")
}

case class InventoryChanged(items: Seq[Item] = Nil, slots: Option[Slots] = None) extends Criteria {
  override def toJson: Json = Encoder[InventoryChanged].apply(this)
}
object InventoryChanged {
  implicit val encoder: Encoder[InventoryChanged] = (a: InventoryChanged) =>
    Json.obj(
      "trigger"    := "minecraft:inventory_changed",
      "conditions" := Json.obj("items" := a.items, "slots" := a.slots)
  )
}
case class Slots(
    empty: Option[RangeOrSingle] = None,
    full: Option[RangeOrSingle] = None,
    occupied: Option[RangeOrSingle] = None
)
object Slots {
  implicit val encoder: Encoder[Slots] = (a: Slots) =>
    Json.obj("empty" := a.empty, "full" := a.full, "occupied" := a.occupied)
}

case class ItemDurabilityChanged(
    delta: Option[RangeOrSingle] = None,
    durability: Option[RangeOrSingle] = None,
    item: Option[Item] = None
) extends Criteria {
  override def toJson: Json = Encoder[ItemDurabilityChanged].apply(this)
}
object ItemDurabilityChanged {
  implicit val encoder: Encoder[ItemDurabilityChanged] = (a: ItemDurabilityChanged) =>
    Json.obj(
      "trigger" := "minecraft:item_durability_changed",
      "conditions" := Json
        .obj("delta" := a.delta, "durability" := a.durability, "item" := a.item)
  )
}

case class Levitation(distance: Option[Distance] = None, duration: Option[RangeOrSingle] = None) extends Criteria {
  override def toJson: Json = Encoder[Levitation].apply(this)
}
object Levitation {
  implicit val encoder: Encoder[Levitation] = (a: Levitation) =>
    Json.obj(
      "trigger"    := "minecraft:levitation",
      "conditions" := Json.obj("distance" := a.distance, "duration" := a.duration)
  )
}

case class LocationCheck(location: Option[Location] = None) extends Criteria {
  override def toJson: Json = Encoder[LocationCheck].apply(this)
}
object LocationCheck {
  implicit val encoder: Encoder[LocationCheck] = (a: LocationCheck) =>
    Json.obj("trigger" := "minecraft:location", "conditions" := a.location)
}

case class NetherTravel(distance: Option[Distance] = None) extends Criteria {
  override def toJson: Json = Encoder[NetherTravel].apply(this)
}
object NetherTravel {
  implicit val encoder: Encoder[NetherTravel] = (a: NetherTravel) =>
    Json.obj("trigger" := "minecraft:nether_travel", "conditions" := Json.obj("distance" := a.distance))
}

case class PlacedBlock(
    block: Option[ResourceId] = None,
    item: Option[Item] = None,
    location: Option[Location] = None,
    state: Map[String, String] = Map.empty
) extends Criteria {
  override def toJson: Json = Encoder[PlacedBlock].apply(this)
}
object PlacedBlock {
  implicit val encoder: Encoder[PlacedBlock] = (a: PlacedBlock) =>
    Json.obj(
      "trigger"    := "minecraft:placed_block",
      "conditions" := Json.obj("block" := a.block, "item" := a.item, "location" := a.location, "state" := a.state)
  )
}

case class PlayerHurtEntity(damage: Option[Damage] = None, entity: Option[Entity] = None) extends Criteria {
  override def toJson: Json = Encoder[PlayerHurtEntity].apply(this)
}
object PlayerHurtEntity {
  implicit val encoder: Encoder[PlayerHurtEntity] = (a: PlayerHurtEntity) =>
    Json.obj(
      "trigger"    := "minecraft:player_hurt_entity",
      "conditions" := Json.obj("damage" := a.damage, "entity" := a.entity)
  )
}

case class PlayerKilledEntity(entity: Option[Entity] = None, killingBlow: Option[DamageType] = None) extends Criteria {
  override def toJson: Json = Encoder[PlayerKilledEntity].apply(this)
}
object PlayerKilledEntity {
  implicit val encoder: Encoder[PlayerKilledEntity] = (a: PlayerKilledEntity) =>
    Json.obj(
      "trigger"    := "minecraft:player_killed_entity",
      "conditions" := Json.obj("entity" := a.entity, "killing_blow" := a.killingBlow)
  )
}

case class RecipeUnlocked(recipe: Option[RecipeOrResourceId] = None) extends Criteria {
  override def toJson: Json = Encoder[RecipeUnlocked].apply(this)
}
object RecipeUnlocked {
  implicit val encoder: Encoder[RecipeUnlocked] = (a: RecipeUnlocked) =>
    Json.obj("trigger" := "minecraft:recipe_unlocked", "conditions" := Json.obj("recipe" := a.recipe.map(_.id)))
}

case class SleptInBed(location: Option[Location] = None) extends Criteria {
  override def toJson: Json = Encoder[SleptInBed].apply(this)
}
object SleptInBed {
  implicit val encoder: Encoder[SleptInBed] = (a: SleptInBed) =>
    Json.obj("trigger" := "minecraft:slept_in_bed", "conditions" := a.location)
}

case class SummonedEntity(entity: Option[Entity] = None) extends Criteria {
  override def toJson: Json = Encoder[SummonedEntity].apply(this)
}
object SummonedEntity {
  implicit val encoder: Encoder[SummonedEntity] = (a: SummonedEntity) =>
    Json.obj("trigger" := "minecraft:summoned_entity", "conditions" := Json.obj("entity" := a.entity))
}

case class TameAnimal(entity: Option[Entity] = None) extends Criteria {
  override def toJson: Json = Encoder[TameAnimal].apply(this)
}
object TameAnimal {
  implicit val encoder: Encoder[TameAnimal] = (a: TameAnimal) =>
    Json.obj("trigger" := "minecraft:tame_animal", "conditions" := Json.obj("entity" := a.entity))
}

case object Tick extends Criteria {
  override def toJson: Json = Json.obj("trigger" := "minecraft:tick")
}

case class UsedEnderEye(distance: Option[RangeOrSingle] = None) extends Criteria {
  override def toJson: Json = Encoder[UsedEnderEye].apply(this)
}
object UsedEnderEye {
  implicit val encoder: Encoder[UsedEnderEye] = (a: UsedEnderEye) =>
    Json.obj("trigger" := "minecraft:used_ender_eye", "conditions" := Json.obj("distance" := a.distance))
}

case class UsedTotem(item: Option[Item] = None) extends Criteria {
  override def toJson: Json = Encoder[UsedTotem].apply(this)
}
object UsedTotem {
  implicit val encoder: Encoder[UsedTotem] = (a: UsedTotem) =>
    Json.obj("trigger" := "minecraft:used_totem", "conditions" := Json.obj("item" := a.item))
}

case class VillagerTrade(item: Option[Item] = None, villager: Option[Entity] = None) extends Criteria {
  override def toJson: Json = Encoder[VillagerTrade].apply(this)
}
object VillagerTrade {
  implicit val encoder: Encoder[VillagerTrade] = (a: VillagerTrade) =>
    Json.obj(
      "trigger"    := "minecraft:villager_trade",
      "conditions" := Json.obj("item" := a.item, "villager" := a.villager)
  )
}
