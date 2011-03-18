package GyD.EverShop;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.ContainerBlock;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRightClickEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;
import org.bukkit.util.config.Configuration;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.system.Account;
import com.nijiko.coelho.iConomy.system.Bank;

/**
 * EverShop block listener
 * @author GyD
 */
public class EverShopBlockListener extends BlockListener {
    private final EverShop plugin;
    //public Configuration EverShopConfig = new Configuration(new File("plugins/EverShop/config.yml"));

    public EverShopBlockListener(final EverShop plugin) {
        this.plugin = plugin;
    }
    
    public void onBlockDamage(BlockDamageEvent e)
    {
		// get player
    	Player player = e.getPlayer();
    	
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
    	}
    }
    
    public void onBlockRightClick(BlockRightClickEvent e)
    {
		// get player
    	Player player = e.getPlayer();
    	
    	if (isBank(e.getBlock()))
    	{
    		// how many iConomy coin for a Slime?
    		int ratio = 1;
    		
    		int number = player.getItemInHand().getAmount();
    		Bank bank = iConomy.getBank();
    		Account account = bank.getAccount(player.getName());
    		double balance = account.getBalance();
    		
    		if( number == -1 ||  number == 0 )
    		{
    			number = 1;
    		}

    		double price = number*ratio;
    		
    		ItemStack prix = new ItemStack(341);
	    	prix.setAmount(number);
    		
    		if( player.getItemInHand().getType().equals(Material.SLIME_BALL) )
    		{
    			account.add(price);
    	    	InventoryWorkaround.removeItem((CraftInventory)player.getInventory(), true, new ItemStack[] { prix });
    			player.sendMessage("§eDépot de §a"+price+" §e"+bank.getCurrency());
    			player.sendMessage("§eSolde: §a"+account.getBalance());
    		}
    		else
    		{
    			if( balance >= price )
    			{
	    	    	if( !player.getInventory().addItem(new ItemStack[] { prix }).isEmpty() )
	    	    	{
	    	    		player.sendMessage("§cPas de place dans vos poches");
	    	    	}
	    	    	else
	    	    	{
	    	    		account.subtract(price);
	    	    		player.sendMessage("§eRetrait de §a"+price+" §e"+bank.getCurrency());
	        			player.sendMessage("§eSolde: §a"+account.getBalance());
	    	    	}
    			}
    			else
    			{
    	    		player.sendMessage("§cPas assez d'argent (§a"+balance+")§c pour un retrait de §a"+price+" §c"+bank.getCurrency());
    			}
    		}

	    	player.updateInventory();
    	}
    	
    	if(isShop(e.getBlock())){
    		
	    	
	    	// get chest
    		ContainerBlock chest = (ContainerBlock)e.getBlock().getFace(BlockFace.valueOf("DOWN"), 1).getState();
	    	
	    	//get sign
	    	Sign sign = (Sign) e.getBlock().getState();
	    	
	    	boolean restore = false;
	    	
	    	String l1 = sign.getLines()[1];
	    	String l2 = sign.getLines()[2];
	    	
	    	// get price
	    	int price = Integer.parseInt(l2.substring(2, l2.length()-7));

	    	// get amount of items sold 
	    	int sellamount = Integer.parseInt(l1.substring(2, 4));
	    	
	    	String name = rename(l1.substring(9));
	    	
	    	String[] itemvals = name.split("[ :]+");
	    	
	    	short color = new Short("0");
	    	
	    	if( itemvals.length == 2 ){
	    		color = Short.valueOf(itemvals[1]);
	    	}
	    	
	    	ItemStack prix = new ItemStack(341);
	    	prix.setAmount(price);
	    	
	    	ItemStack recompense = new ItemStack(getItemId(itemvals[0]));
	    	recompense.setAmount(sellamount);
	    	recompense.setDurability(color);
	    	
	    	if (!InventoryWorkaround.containsItem((CraftInventory)chest.getInventory(), true, new ItemStack[] { recompense })) {
    			player.sendMessage("§cLe coffre est vide");
    			return ;
	    	}
	    	
	    	if (!InventoryWorkaround.containsItem((CraftInventory)player.getInventory(), true, new ItemStack[] { prix })) {
    			player.sendMessage("§cVous n'avez pas assez de Slime");
    			return ;
	    	}
	    	
	    	InventoryWorkaround.removeItem((CraftInventory)chest.getInventory(), true, new ItemStack[] { recompense });
	    	InventoryWorkaround.removeItem((CraftInventory)player.getInventory(), true, new ItemStack[] { prix });
	    	if( !chest.getInventory().addItem(new ItemStack[] { prix }).isEmpty() )
	    	{
	    		player.sendMessage("§cPas de place dans le coffre");
	    		restore = true;
	    	}
	    	
	    	
            if( !player.getInventory().addItem(new ItemStack[] { recompense }).isEmpty() )
	    	{
	    		player.sendMessage("§cPas de place dans votre inventaire");
	    		restore = true;
	    	}
            
            if( restore )
            {
	    		chest.getInventory().addItem(new ItemStack[] { recompense });
	    		player.getInventory().addItem(new ItemStack[] { prix });
            }
	    	
	    	
	    	
	    	player.updateInventory();
    	}
    }
	    	/*
	    	
	    	// get user slimes
	    	int userslimes = 0;
	    	// get shop amount
	    	int count = 0;
	    	
	    	int first = player.getInventory().first(Material.getMaterial(341));
    		
    		// check if the chest is empty or not!
    		if( first == -1 )
    		{
    			player.sendMessage("Vous n'avez pas de Slime");
    			return ;
    		}
	    	
	    	// count userslimes
	    	for (int i = player.getInventory().first(Material.getMaterial(341)); i < player.getInventory().getSize(); i++) {
	    		ItemStack current = player.getInventory().getItem(i);
	    		
	    		userslimes += current.getAmount();
	    	}

	    	// enouth slimes?
	    	if( userslimes >= price )
	    	{
	    		// check if shop have material
	    		first = chest.getInventory().first(Material.getMaterial(vals[0]));
	    		
	    		// check if the chest is empty or not!
	    		if( first == -1 )
	    		{
	    			player.sendMessage("Il n'y a plus rien a vendre dans ce coffre! (1)");
	    			return ;
	    		}
	    		
	    		// get material amount
	    		for (int i = chest.getInventory().first(Material.getMaterial(vals[0])); i < chest.getInventory().getSize(); i++) {
		    		ItemStack current = chest.getInventory().getItem(i);
		    		
		    		count += current.getAmount();
		    	}
	    		
	    		// check if the chest have enouth
	    		if( count < sellamount )
	    		{
	    			player.sendMessage("Il n'y a plus rien a vendre dans ce coffre!");
	    			return ;
	    		}
	    		
	    		// get the first item
	    		ItemStack item = chest.getInventory().getItem(first);
	    		
	    		if( item.getAmount() > sellamount )
	    		{
	    			int amount1 = item.getAmount();
	    			item.setAmount(price);
	    			player.getInventory().addItem(new ItemStack[] { item });
	    			item.setAmount(amount1 - price);
	    		}
	    		
	    		// check color
	    		if( item.getDurability() != color )
	    		{
	    			player.sendMessage("Il y a une erreur avec ce coffre");
	    			return ;
	    		}
	    		first = player.getInventory().first(Material.getMaterial(341));
	    		item = player.getInventory().getItem(first);
	    		
	    		// 
	    		if( item.getAmount() > price )
	    		{
	    			int amount1 = item.getAmount();
	    			item.setAmount(price);
	    			chest.getInventory().addItem(new ItemStack[] { item });
	    			item.setAmount(amount1 - price);
	    		}
	    		else
	    		{
	    			int left = price;
	    			for (int i = player.getInventory().first(Material.getMaterial(341)); i < player.getInventory().getSize(); i++) {
	    				if (left == 0) {
	                        break;
	                    }
	    				ItemStack current = player.getInventory().getItem(i);
	    				
	    				if( current.getAmount() > left )
	    				{
	                        int amount1 = current.getAmount();
	                        current.setAmount(left);
	                        chest.getInventory().addItem(new ItemStack[] { current });
	                        current.setAmount(amount1 - left);
	                        break;
	    				}
	    				left -= current.getAmount();
	                    chest.getInventory().addItem(new ItemStack[] { current });
	                    player.getInventory().clear(i);
	    			}
	    		}
	    	}
	    	else
	    	{
	    		player.sendMessage("Vous n'avez pas assez de slime");
	    	}
	    	
	    	//player.getInventory().addItem(new ItemStack[] { objet });
	    	
	    	//player.sendMessage(""+objet);
	    	CraftPlayer cPlayer = ((CraftPlayer)e.getPlayer());
	    	cPlayer.getHandle().l();
    	}
    }*/
    
    public void onBlockPlace(BlockPlaceEvent e)
    {
    	// get seller
    	Player seller = e.getPlayer();
    	// get block
    	Block block = e.getBlock();
    	// get block type
    	Material blockMat = block.getType();
    	// get block agains
    	Block blockAgainst = e.getBlockAgainst();
    	// get block agains type
    	Material blockAgainstMaterial = blockAgainst.getType();
    	
    	// please don't place block on signs!
    	if ((blockAgainstMaterial.equals(Material.SIGN_POST)) || (blockAgainstMaterial.equals(Material.WALL_SIGN))){
    		if( isShop(blockAgainst) )
    			e.setCancelled(true);
    	}
    	else
    	if (block.equals(Material.CHEST))
    	{
    		seller.sendMessage("En placant un panneau au dessus du coffre vous pouvez créer un magasin.");
    	}
    }
    
    
    public boolean isShop(Block block)
    {
    	if ((block.getType().equals(Material.SIGN_POST)) || (block.getType().equals(Material.WALL_SIGN)))
    	{
	    	// get the sign
	    	Sign sign = (Sign)block.getState();
	    	// get the sign content
	    	String[] text = sign.getLines();
	    	// get the underblock
	    	Block underblock = block.getFace(BlockFace.valueOf("DOWN"), 1);
	    	
	    	// is this a Shop ?
	    	if( underblock.getType() == Material.CHEST && text[0].equalsIgnoreCase("§a[shop]") ){
	        	// yes it is
	    		return true;
	    	}
    	}
    	
    	// no
    	return false;
    }
    
    public boolean isBank(Block block)
    {
    	if ((block.getType().equals(Material.SIGN_POST)) || (block.getType().equals(Material.WALL_SIGN)))
    	{
	    	// get the sign
	    	Sign sign = (Sign)block.getState();
	    	// get the sign content
	    	String[] text = sign.getLines();
	    	
	    	// is this a Bank ?
	    	if( text[0].equalsIgnoreCase("§a[bank]") ){
	        	// yes it is
	    		return true;
	    	}
    	}
    	
    	// no
    	return false;
    }

    public void onSignChange(SignChangeEvent e)
    {
    	// get underblock
    	Block underblock = e.getBlock().getFace(BlockFace.valueOf("DOWN"), 1);
    	
    	// get seller
    	Player seller = e.getPlayer();
    	
    	// get sign text
    	String[] text = e.getLines();
    	
    	// does the sign contain [Shop] ?
    	if (
    			text[0].equalsIgnoreCase("[Bank]")
    	) {
    		
    		if( seller.getName().toString().equalsIgnoreCase("GyD") )
    		{
    			e.setLine(0, "§a[Bank]");
    		}
    		return;
    	}
    	
    	// chest under?
    	if( underblock.getType() == Material.CHEST )
    	{
    		// get the chest
    		ContainerBlock chest = (ContainerBlock)underblock.getState();
    		
    		// does the sign contain [Shop] ?
	    	if (
	    			text[0].equalsIgnoreCase("[Shop]")
	    	) {
	    		// is the second line a int (price)
	    		if( isInt(text[1]) )
	    		{
	    			// what do you sell? (what's inside the chest)
		    		ItemStack item = null;
	                for (int i = 0; i < chest.getInventory().getSize(); i++) {
	                  if (chest.getInventory().getItem(i) == null) {
	                    continue;
	                  }
	                  if (chest.getInventory().getItem(i).getAmount() > 0) {
	                    item = chest.getInventory().getItem(i);
	                    break;
	                  }
	                }
		    		
		    		int prix = Integer.parseInt(text[1]);
	                
		    		String color = "";
	                
	                if( item.getType() == Material.WOOL ||  item.getType() == Material.INK_SACK )
	                {
	                	color = ":"+(byte)item.getDurability();
	                }
	                
	                String nbr = ""+item.getAmount();;
	                
	                // on convertit à deux chiffres
	                if( item.getAmount() < 10 )
	                {
	                	nbr = "0"+item.getAmount();
	                }
	                
	                e.setLine(0, "§a[Shop]");
		    		e.setLine(1, "§f"+ nbr + "§ax§f" + rename(item.getType() + color) );
		    		e.setLine(2, "§a"+ prix + "§fSlime");
		    		e.setLine(3, "§8" + seller.getName());
		    		//e.setLine(3, "§8" + "dante231");
		    	}
		    }
    	}
    }
    
    public String rename(String name)
    {
    	String[] vals = new String[] { "WOOL:1|WOOL:ORANGE", "WOOL:2|WOOL:MAGENTA", "INK_SACK:15|Bone_Meal" };
    	
    	String originalName = name;
    	
    	for(int i = 0; i < vals.length; i++ ){
    		String[] valeurs = vals[i].split("[|]");
    		
    		name = name.replace(valeurs[0], valeurs[1]);
    		if( name == originalName)
    		{
    			name = name.replace(valeurs[1], valeurs[0]);
    		}
    		
    		if( name != originalName )
    			return name;
    	}
    	
    	return name;
    }
    
    // is this string an int?
    public boolean isInt(String i)
    {
      try
      {
        Integer.parseInt(i);
        return true; } catch (NumberFormatException nfe) {
      }
      return false;
    }
    
    public int getItemId(String name) {
        Material[] mat = Material.values();
        for (Material m : mat) {
          if (m.name().toLowerCase().startsWith(name.toLowerCase())) {
            return m.getId();
          }
        }
        return -1;
      }
}
