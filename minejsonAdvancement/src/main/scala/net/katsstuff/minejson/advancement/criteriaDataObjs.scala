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
import net.katsstuff.minejson.{RangeOrSingle, ResourceId}
import net.katsstuff.typenbt.{Mojangson, NBTCompound}

case class Item(
    item: Option[ResourceId] = None,
    data: Option[Int] = None,
    count: Option[RangeOrSingle] = None,
    durability: Option[RangeOrSingle] = None,
    enhancements: Seq[Enhancement] = Nil,
    nbt: Option[NBTCompound] = None,
    potion: Option[ResourceId] = None,
    tag: Option[ResourceId] = None
)
object Item {
  implicit val ender: Encoder[Item] = (a: Item) =>
    Json.obj(
      "count"      := a.count,
      "data"       := a.data,
      "durability" := a.durability,
      "enhancements" := a.enhancements.map { enhancement =>
        Json.obj("enhancement" := enhancement.enchantment, "levels" := enhancement.levels)
      },
      "item"   := a.item,
      "nbt"    := a.nbt.map(Mojangson.serialize),
      "potion" := a.potion
  )
}

case class Enhancement(enchantment: Option[ResourceId] = None, levels: Option[RangeOrSingle] = None)

case class Entity(
    distance: Option[Distance] = None,
    effects: Seq[Effect] = Nil,
    location: Option[Location] = None,
    nbt: Option[NBTCompound] = None,
    tpe: Option[ResourceId] = None
)
object Entity {
  implicit val encoder: Encoder[Entity] = (a: Entity) => {
    Json.obj(
      "distance" := a.distance,
      "effects"  := a.effects.map(e => e.name.toString := e).toMap,
      "location" := a.location,
      "nbt"      := a.nbt.map(Mojangson.serialize),
      "type"     := a.tpe
    )
  }
}

case class Effect(name: ResourceId, amplifier: Option[RangeOrSingle] = None, duration: Option[RangeOrSingle] = None)
object Effect {
  implicit val encoder: Encoder[Effect] = (a: Effect) => Json.obj("amplifier" := a.amplifier, "duration" := a.duration)
}

case class Distance(
    absolute: Option[RangeOrSingle] = None,
    horizontal: Option[RangeOrSingle] = None,
    x: Option[RangeOrSingle] = None,
    y: Option[RangeOrSingle] = None,
    z: Option[RangeOrSingle] = None,
)
object Distance {
  implicit val encoder: Encoder[Distance] = (a: Distance) =>
    Json.obj(
      "absolute"   := a.absolute,
      "horizontal" := a.horizontal,
      "x"          := a.x,
      "y"          := a.y,
      "z"          := a.z
  )
}

trait Biome {
  def name: String
}
object Biome {
  case object Beaches                      extends Biome { def name: String = "beaches"                          }
  case object BirchForest                  extends Biome { def name: String = "birch_forest"                     }
  case object BirchForestHills             extends Biome { def name: String = "birch_forest_hills"               }
  case object ColdBeach                    extends Biome { def name: String = "cold_beach"                       }
  case object DeepOcean                    extends Biome { def name: String = "deep_ocean"                       }
  case object Desert                       extends Biome { def name: String = "desert"                           }
  case object DesertHills                  extends Biome { def name: String = "desert_hills"                     }
  case object ExtremeHills                 extends Biome { def name: String = "extreme_hills"                    }
  case object ExtremeHillsWithTrees        extends Biome { def name: String = "extreme_hills_with_trees"         }
  case object Forest                       extends Biome { def name: String = "forest"                           }
  case object ForestHills                  extends Biome { def name: String = "forest_hills"                     }
  case object FrozenOcean                  extends Biome { def name: String = "frozen_ocean"                     }
  case object FrozenRiver                  extends Biome { def name: String = "frozen_river"                     }
  case object Hell                         extends Biome { def name: String = "hell"                             }
  case object IceFlats                     extends Biome { def name: String = "ice_flats"                        }
  case object IceMountains                 extends Biome { def name: String = "ice_mountains"                    }
  case object Jungle                       extends Biome { def name: String = "jungle"                           }
  case object JungleEdge                   extends Biome { def name: String = "jungle_edge"                      }
  case object JungleHills                  extends Biome { def name: String = "jungle_hills"                     }
  case object Mesa                         extends Biome { def name: String = "mesa"                             }
  case object MesaClearRock                extends Biome { def name: String = "mesa_clear_rock"                  }
  case object MesaRock                     extends Biome { def name: String = "mesa_rock"                        }
  case object MushroomIsland               extends Biome { def name: String = "mushroom_island"                  }
  case object MushroomIslandShore          extends Biome { def name: String = "mushroom_island_shore"            }
  case object MutatedBirchForest           extends Biome { def name: String = "mutated_birch_forest"             }
  case object MutatedBirchForestHills      extends Biome { def name: String = "mutated_birch_forest_hills"       }
  case object MutatedDesert                extends Biome { def name: String = "mutated_desert"                   }
  case object MutatedExtremeHills          extends Biome { def name: String = "mutated_extreme_hills"            }
  case object MutatedExtremeHillsWithTrees extends Biome { def name: String = "mutated_extreme_hills_with_trees" }
  case object MutatedForest                extends Biome { def name: String = "mutated_forest"                   }
  case object MutatedIceFlats              extends Biome { def name: String = "mutated_ice_flats"                }
  case object MutatedJungle                extends Biome { def name: String = "mutated_jungle"                   }
  case object MutatedJungleEdge            extends Biome { def name: String = "mutated_jungle_edge"              }
  case object MutatedMesa                  extends Biome { def name: String = "mutated_mesa"                     }
  case object MutatedMesaClearRock         extends Biome { def name: String = "mutated_mesa_clear_rock"          }
  case object MutatedMesaRock              extends Biome { def name: String = "mutated_mesa_rock"                }
  case object MutatedPlains                extends Biome { def name: String = "mutated_plains"                   }
  case object MutatedRedwoodTaiga          extends Biome { def name: String = "mutated_redwood_taiga"            }
  case object MutatedRedwoodTaigaHills     extends Biome { def name: String = "mutated_redwood_taiga_hills"      }
  case object MutatedRoofedForest          extends Biome { def name: String = "mutated_roofed_forest"            }
  case object MutatedSavanna               extends Biome { def name: String = "mutated_savanna"                  }
  case object MutatedSavannaRock           extends Biome { def name: String = "mutated_savanna_rock"             }
  case object MutatedSwampland             extends Biome { def name: String = "mutated_swampland"                }
  case object MutatedTaiga                 extends Biome { def name: String = "mutated_taiga"                    }
  case object MutatedTaigaCold             extends Biome { def name: String = "mutated_taiga_cold"               }
  case object Ocean                        extends Biome { def name: String = "ocean"                            }
  case object Plains                       extends Biome { def name: String = "plains"                           }
  case object RedwoodTaiga                 extends Biome { def name: String = "redwood_taiga"                    }
  case object RedwoodTaigaHills            extends Biome { def name: String = "redwood_taiga_hills"              }
  case object River                        extends Biome { def name: String = "river"                            }
  case object RoofedForest                 extends Biome { def name: String = "roofed_forest"                    }
  case object Savanna                      extends Biome { def name: String = "savanna"                          }
  case object SavannaRock                  extends Biome { def name: String = "savanna_rock"                     }
  case object Sky                          extends Biome { def name: String = "sky"                              }
  case object SmallerExtremeHills          extends Biome { def name: String = "smaller_extreme_hills"            }
  case object StoneBeach                   extends Biome { def name: String = "stone_beach"                      }
  case object Swampland                    extends Biome { def name: String = "swampland"                        }
  case object Taiga                        extends Biome { def name: String = "taiga"                            }
  case object TaigaCold                    extends Biome { def name: String = "taiga_cold"                       }
  case object TaigaColdHills               extends Biome { def name: String = "taiga_cold_hills"                 }
  case object TaigaHills                   extends Biome { def name: String = "taiga_hills"                      }
  case object Void                         extends Biome { def name: String = "void"                             }

  implicit val encoder: Encoder[Biome] = _.name.asJson
}

trait Dimension {
  def name: String
}
object Dimension {
  case object Overworld extends Dimension { def name = "overworld"  }
  case object TheEnd    extends Dimension { def name = "the_end"    }
  case object TheNether extends Dimension { def name = "the_nether" }

  implicit val encoder: Encoder[Dimension] = _.name.asJson
}

trait Feature {
  def name: String
}
object Feature {
  case object EndCity    extends Feature { def name = "EndCity"    }
  case object Fortress   extends Feature { def name = "Fortress"   }
  case object Mansion    extends Feature { def name = "Mansion"    }
  case object Mineshaft  extends Feature { def name = "Mineshaft"  }
  case object Monument   extends Feature { def name = "Monument"   }
  case object Stronghold extends Feature { def name = "Stronghold" }
  case object Temple     extends Feature { def name = "Temple"     }
  case object Village    extends Feature { def name = "Village"    }

  implicit val encoder: Encoder[Feature] = _.name.asJson
}

case class Location(
    biome: Option[Biome] = None,
    dimension: Option[Dimension] = None,
    feature: Option[Feature] = None,
    position: Option[Position] = None
)
object Location {
  implicit val encoder: Encoder[Location] = (a: Location) =>
    Json.obj(
      "biome"     := a.biome,
      "dimension" := a.dimension,
      "feature"   := a.feature,
      "position"  := a.position
  )
}

case class Position(x: Option[RangeOrSingle] = None, y: Option[RangeOrSingle] = None, z: Option[RangeOrSingle] = None)
object Position {
  implicit val encoder: Encoder[Position] = (a: Position) => Json.obj("x" := a.x, "y" := a.y, "z" := a.z)
}

case class Damage(
    blocked: Option[Boolean] = None,
    dealt: Option[RangeOrSingle] = None,
    directEntity: Option[Entity] = None,
    sourceEntity: Option[Entity] = None,
    taken: Option[RangeOrSingle] = None,
    tpe: Option[DamageType] = None
)
object Damage {
  implicit val encoder: Encoder[Damage] = (a: Damage) =>
    Json.obj(
      "blocked"       := a.blocked,
      "dealt"         := a.dealt,
      "direct_entity" := a.directEntity,
      "source_entity" := a.sourceEntity,
      "taken"         := a.taken,
      "type"          := a.tpe
  )
}

case class DamageType(
    bypassesArmor: Option[Boolean] = None,
    bypassesInvulnerability: Option[Boolean] = None,
    bypassesMagic: Option[Boolean] = None,
    directEntity: Option[Entity] = None,
    isExplosion: Option[Boolean] = None,
    isFire: Option[Boolean] = None,
    isMagic: Option[Boolean] = None,
    isProjectile: Option[Boolean] = None,
    sourceEntity: Option[Entity] = None,
)
object DamageType {
  implicit val encoder: Encoder[DamageType] = (a: DamageType) =>
    Json.obj(
      "bypasses_armor"           := a.bypassesArmor,
      "bypasses_invulnerability" := a.bypassesInvulnerability,
      "direct_entity"            := a.directEntity,
      "is_explosion"             := a.isExplosion,
      "is_fire"                  := a.isFire,
      "is_magic"                 := a.isMagic,
      "is_projectile"            := a.isProjectile,
      "source_entity"            := a.sourceEntity
  )
}
