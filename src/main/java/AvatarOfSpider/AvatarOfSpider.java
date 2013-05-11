package AvatarOfSpider;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.Effect;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.effects.common.InvulnerabilityEffect;
import com.herocraftonline.heroes.characters.skill.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;
import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import org.bukkit.event.EventHandler;


/**
 * Created with IntelliJ IDEA.
 * User: nicolaslachance
 * Date: 2013-05-03
 * Time: 10:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class AvatarOfSpider extends ActiveSkill
{
    private Heroes plugin;
    private DisguiseCraft dCraft = (DisguiseCraft) plugin.getServer().getPluginManager().getPlugin("DisguiseCraft");

    public AvatarOfSpider(Heroes plugin)
    {
        super(plugin, "spiderform");
        this.plugin = plugin;
        setDescription("The might of Ungoliant, granting you extra damage");
        setUsage("/skill spiderform");
        setArgumentRange(0, 0);
        setIdentifiers(new String[] { "skill spiderform" });
        setTypes(new SkillType[] { SkillType.FORCE, SkillType.BUFF, SkillType.SILENCABLE, SkillType.COUNTER });

    }

    public ConfigurationSection getDefaultConfig()
    {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(SkillSetting.DURATION.node(), Integer.valueOf(10000));
        node.set("dmgBoost", Integer.valueOf(1));
        return node;
    }

    public SkillResult use(Hero hero, String[] args)
    {
        Player player = hero.getPlayer();


        broadcastExecuteText(hero);
        int duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 10000, false);

        if (dCraft.disguiseDB.containsKey(player.getName()))
        {
            Disguise disguise = dCraft.disguiseDB.get(player.getName()).clone();
            disguise.setType(DisguiseType.Spider).clearData();
            dCraft.changeDisguise(player, disguise);
        } else
        {
            Disguise disguise = new Disguise(dCraft.getNextAvailableID(), DisguiseType.Spider);
            dCraft.disguisePlayer(player, disguise);
        }

        hero.addEffect(new SpiderFormEffect(this, duration));

        return SkillResult.NORMAL;
    }

    public String getDescription(Hero hero)
    {
        int duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 1, false);
        return getDescription().replace("$1", duration / 1000 + "");
    }

    public class SpiderFormEffect extends ExpirableEffect
    {
        private final String applyText = "$1 has become a spider!";
        private final String expireText = "$1 is once again a human!";

        public SpiderFormEffect(Skill skill, long duration)
        {
            super(skill, "spiderform", duration);
            this.types.add(EffectType.BENEFICIAL);
        }

        public void removeFromHero(Hero hero)
        {
            super.removeFromHero(hero);
            dCraft.unDisguisePlayer(hero.getPlayer());
        }
    }
}
