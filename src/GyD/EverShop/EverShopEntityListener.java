package GyD.EverShop;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

public class EverShopEntityListener extends EntityListener {
	private final EverShop plugin;

	public EverShopEntityListener(EverShop instance) {
		plugin = instance;
	}
	

	@Override
	public void onEntityDamage(EntityDamageEvent e) {
		
		EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent)e;
		
		//if(isPlayer(sub.getEntity()) && isPlayer(sub.getDamager()))
        //{
			// get player
		Entity attacker = sub.getDamager();
	    	
	    	/*
	    	if (isBank(e.getBlock()))
	    	{
	    		if( !player.getName().equalsIgnoreCase("GyD") )
	    		{
	    			player.sendMessage("§cPas touche tit con!");
	    			e.setCancelled(true);
	    		}
	    	}
	    	else
	    	{
	    		if (isShop(e.getBlock()))
	        	{
	    			//get sign
	    	    	Sign sign = (Sign) e.getBlock().getState();
	    	    	
	    	    	// get sign owner
	    	    	String owner = sign.getLines()[3].substring(2);
	    	    	
	        		if( !player.getName().equalsIgnoreCase(owner) && !player.getName().equalsIgnoreCase("GyD") )
	        		{
	        			player.sendMessage("§cC'est le Shop de §f"+owner);
	        			e.setCancelled(true);
	        		}
	        	}
	    	}*/
        //}
	}
}
