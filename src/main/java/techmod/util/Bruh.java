package techmod.util;

import necesse.entity.mobs.HumanTexture;
import necesse.entity.mobs.HumanTextureFull;
import necesse.entity.mobs.MobTexture;
import necesse.gfx.gameTexture.GameTexture;

import static necesse.gfx.gameTexture.GameTexture.fromFile;

public class Bruh {

    public static HumanTextureFull humanTextureFullfromString(String path) {
        return new HumanTextureFull(GameTexture.fromFile(path + "head",null), GameTexture.fromFile(path + "hair",null), GameTexture.fromFile(path + "backhair",null),GameTexture.fromFile( path + "body",null),GameTexture.fromFile( path + "arms_left",null),GameTexture.fromFile( path + "arms_right",null), GameTexture.fromFile(path + "feet",null));
    }
}
