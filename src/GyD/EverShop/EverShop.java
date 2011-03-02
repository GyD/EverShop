package GyD.EverShop;

import com.nijiko.coelho.iConomy.*;
import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.event.server.PluginEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

/**
 * EverShop for Bukkit
 *
 * @author GyD
 */
public class EverShop extends JavaPlugin {
    private final EverShopBlockListener blockListener = new EverShopBlockListener(this);
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    
    public static final Logger log = Logger.getLogger("Minecraft");
    
    /*
     * On ajoute iConomy
     * basé sur: http://pastie.org/1604147
     */
    public static iConomy iConomy;
    private Listener Listener = new Listener();

    private class Listener extends ServerListener {

        public Listener() { }

        @Override
        public void onPluginEnabled(PluginEvent event) {
            if(event.getPlugin().getDescription().getName().equals("iConomy")) {
                EverShop.iConomy = (iConomy)event.getPlugin();
                EverShop.log.info("[MyPlugin] Attached to iConomy.");
            }
        }
    }


    public void onEnable() {
        // TODO: Place any custom enable code here including the registration of any events

        // Register our events
        PluginManager pm = getServer().getPluginManager();
       

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }
    public void onDisable() {
        // TODO: Place any custom disable code here

        // NOTE: All registered events are automatically unregistered when a plugin is disabled

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        System.out.println("Goodbye world!");
    }
    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
}

