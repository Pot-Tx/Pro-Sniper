package net.pottx.config;

import com.google.gson.annotations.SerializedName;

public class ConfigData {
    public Enable enable = new Enable();

    public static class Enable {
        @SerializedName("enable_pro_skeletons")
        public boolean enableSkeleton = true;

        @SerializedName("enable_pro_drowneds")
        public boolean enableDrowned = true;

        @SerializedName("enable_pro_pillagers")
        public boolean enablePillager = true;

        @SerializedName("enable_pro_ghasts")
        public boolean enableGhast = true;

        @SerializedName("enable_pro_snow_golems")
        public boolean enableSnowGolem = true;

        @SerializedName("enable_pro_witches")
        public boolean enableWitch = true;
    }

    public Chance chance = new Chance();

    public static class Chance {
        @SerializedName("skeletons_pro_chance_under_each_difficulty")
        public double[] skeletonChance = {0.0, 0.25, 0.5, 0.75};

        @SerializedName("drowneds_pro_chance_under_each_difficulty")
        public double[] drownedChance = {0.0, 0.25, 0.5, 0.75};

        @SerializedName("pillagers_pro_chance_under_each_difficulty")
        public double[] pillagerChance = {0.0, 0.25, 0.5, 0.75};

        @SerializedName("ghasts_pro_chance_under_each_difficulty")
        public double[] ghastChance = {0.0, 0.25, 0.5, 0.75};

        @SerializedName("snow_golems_pro_chance_under_each_difficulty")
        public double[] snowGolemChance = {0.75, 0.5, 0.25, 0.0};

        @SerializedName("witchs_pro_chance_under_each_difficulty")
        public double[] witchChance = {0.0, 0.25, 0.5, 0.75};
    }
}
