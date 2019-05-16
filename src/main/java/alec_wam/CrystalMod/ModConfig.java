package alec_wam.CrystalMod;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final General GENERAL = new General(BUILDER);
    public static final Blocks BLOCKS = new Blocks(BUILDER);
    public static final ForgeConfigSpec spec = BUILDER.build();

    public static class General {

        public General(ForgeConfigSpec.Builder builder) {
            builder.push("General");
            builder.pop();
        }
    }
    
    public static class Blocks {
        public final ForgeConfigSpec.ConfigValue<Boolean> Crate_3D_Items;
        public final ForgeConfigSpec.ConfigValue<Boolean> Crate_LeaveItem;

        public final ForgeConfigSpec.ConfigValue<Integer> Pipe_Item_Default_Transfer;
        public final ForgeConfigSpec.ConfigValue<Integer> Pipe_Item_Speed_Count;

        public Blocks(ForgeConfigSpec.Builder builder) {
            builder.push("Blocks");
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
}
