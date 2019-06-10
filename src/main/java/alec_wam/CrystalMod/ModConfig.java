package alec_wam.CrystalMod;

import alec_wam.CrystalMod.events.EntityEventHandler.ItemDropType;
import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final General GENERAL = new General(BUILDER);
    public static final Blocks BLOCKS = new Blocks(BUILDER);
    public static final Entities ENTITIES = new Entities(BUILDER);
    public static final WorldGen WORLDGEN = new WorldGen(BUILDER);
    public static final ForgeConfigSpec spec = BUILDER.build();

    public static class General {
    	public final ForgeConfigSpec.ConfigValue<Boolean> Debug_Grinder_Recipes;
        
        public General(ForgeConfigSpec.Builder builder) {
            builder.push("General");
            Debug_Grinder_Recipes = builder
                    .comment("Print Grinder auto generated recipes in log [default:false]")
                    .translation("crystalmod.config.general.debug_grinder_recipes")
                    .define("debug_grinder_recipes", false);
            builder.pop();
        }
    }
    
    public static class Blocks {
        public final ForgeConfigSpec.ConfigValue<Integer> Shard_Block_Growth;
        
        public final ForgeConfigSpec.ConfigValue<Boolean> Crate_3D_Items;
        public final ForgeConfigSpec.ConfigValue<Boolean> Crate_LeaveItem;

        public final ForgeConfigSpec.ConfigValue<Integer> Pipe_Item_Default_Transfer;
        public final ForgeConfigSpec.ConfigValue<Integer> Pipe_Item_Speed_Count;

        public Blocks(ForgeConfigSpec.Builder builder) {
            builder.push("Blocks");
            Shard_Block_Growth = builder
                    .comment("1 / X rarity of growth happening for a Crystal Shard [default:8]")
                    .translation("crystalmod.config.blocks.shard_block_growth")
                    .defineInRange("shard_block_growth", 8, 1, Integer.MAX_VALUE);
            Crate_3D_Items = builder
                    .comment("Render Items on crates like Item Frames [default:false]")
                    .translation("crystalmod.config.blocks.crate_3d_items")
                    .define("crate_3d_items", false);
            Crate_LeaveItem = builder
                    .comment("Leave one item in crate when extracting [default:true]")
                    .translation("crystalmod.config.blocks.crate_leaveitem")
                    .define("crate_leaveitem", true);
           
            Pipe_Item_Default_Transfer = builder
                    .comment("Number of Items extracted each cycle [default:4]")
                    .translation("crystalmod.config.blocks.pipe_item_default_transfer")
                    .defineInRange("pipe_item_default_transfer", 4, 1, 64);
            Pipe_Item_Speed_Count = builder
                    .comment("Number of Pipe Speed upgrades needed for the fastest speed [default:8]")
                    .translation("crystalmod.config.blocks.pipe_item_speed_count")
                    .defineInRange("pipe_item_speed_count", 8, 1, 64);
            builder.pop();
        }
    }
    
    public static class Entities {
        public final ForgeConfigSpec.ConfigValue<Integer> Mob_Heads;
        public final ForgeConfigSpec.ConfigValue<Integer> Mob_Heads_Drop;
        public final ForgeConfigSpec.ConfigValue<Integer> Mob_Heads_Axe_Bonus;
        public final ForgeConfigSpec.ConfigValue<Integer> Wither_Heads_Axe_Bonus;
        
        public final ForgeConfigSpec.ConfigValue<Integer> Player_Heads;
        public final ForgeConfigSpec.ConfigValue<Integer> Player_Heads_Drop;
        public final ForgeConfigSpec.ConfigValue<Integer> Player_Heads_Axe_Bonus;

        public Entities(ForgeConfigSpec.Builder builder) {
            builder.push("Entities");
            Mob_Heads = builder
                    .comment("0 = Never, 1 = The Mob was killed by a player, 2 = Everytime a mob dies [default:1]")
                    .translation("crystalmod.config.entites.mob_heads")
                    .defineInRange("mob_heads", 1, 0, 2);
            Mob_Heads_Drop = builder
                    .comment("1 / X percent chance of dropping a head [default:30 (0.3%)]")
                    .translation("crystalmod.config.entites.mob_heads_drop")
                    .defineInRange("mob_heads_drop", 30, 1, Integer.MAX_VALUE);
            Mob_Heads_Axe_Bonus = builder
                    .comment("Axes divide the rarity of a head drop by X [default:2, 1 = no effect, Same as mob_heads_drop = 100% drop]")
                    .translation("crystalmod.config.entites.mob_heads_axe_bonus")
                    .defineInRange("mob_heads_axe_bonus", 2, 1, Integer.MAX_VALUE);            
            Wither_Heads_Axe_Bonus = builder
                    .comment("1 / X percent chance of dropping a bonus Wither Skeleton skull when using an axe (0 = never) [default:20 (Normal skull drop is 40)]")
                    .translation("crystalmod.config.entites.wither_heads_axe_bonus")
                    .defineInRange("wither_heads_axe_bonus", 20, 0, Integer.MAX_VALUE);
            
            Player_Heads = builder
                    .comment("0 = Never, 1 = The player was killed by another player, 2 = Everytime a player dies [default:2]")
                    .translation("crystalmod.config.entites.player_heads")
                    .defineInRange("player_heads", 2, 0, 2);
            Player_Heads_Drop = builder
                    .comment("1 / X percent chance of dropping a head [default:10 (10%)]")
                    .translation("crystalmod.config.entites.player_heads_drop")
                    .defineInRange("player_heads_drop", 10, 1, Integer.MAX_VALUE);
            Player_Heads_Axe_Bonus = builder
                    .comment("Axes divide the rarity of a head drop by X [default:2, 1 = no effect, Same as player_heads_drop = 100% drop]")
                    .translation("crystalmod.config.entites.player_heads_axe_bonus")
                    .defineInRange("player_heads_axe_bonus", 2, 1, Integer.MAX_VALUE);
            builder.pop();
        }
        
        public static ItemDropType getDropType(ForgeConfigSpec.ConfigValue<Integer> configValue){
        	return ItemDropType.values()[configValue.get()];
        }
    }
    
    public static class WorldGen {
        public final ForgeConfigSpec.ConfigValue<Integer> CrystalOre_Per_Chunk;

        public WorldGen(ForgeConfigSpec.Builder builder) {
            builder.push("WorldGen");
            CrystalOre_Per_Chunk = builder
                    .comment("Number of chances Crystal Ore has spawning in a chunk [default:2, 0 = never, 32 = max]")
                    .translation("crystalmod.config.worldgen.crystalore_per_chunk")
                    .defineInRange("crystalore_per_chunk", 2, 0, 32);
            builder.pop();
        }
    }
}
