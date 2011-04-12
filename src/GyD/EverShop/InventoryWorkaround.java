package GyD.EverShop;

// imported from:
// package com.earth2me.essentials;

import java.util.HashMap;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryWorkaround
{
  public static int first(Inventory ci, ItemStack item, boolean forceDurability, boolean forceAmount)
  {
    return next(ci, item, 0, forceDurability, forceAmount);
  }

  public static int next(Inventory ci, ItemStack item, int start, boolean forceDurability, boolean forceAmount) {
    ItemStack[] inventory = ci.getContents();
    for (int i = start; i < inventory.length; i++) {
      ItemStack cItem = inventory[i];
      if (cItem == null) {
        continue;
      }
      if ((item.getTypeId() == cItem.getTypeId()) && ((!forceAmount) || (item.getAmount() == cItem.getAmount())) && ((!forceDurability) || (cItem.getDurability() == item.getDurability()))) {
        return i;
      }
    }
    return -1;
  }

  public static HashMap<Integer, ItemStack> removeItem(Inventory ci, boolean forceDurability, ItemStack[] items) {
    HashMap leftover = new HashMap();

    for (int i = 0; i < items.length; i++) {
      ItemStack item = items[i];
      if (item == null) {
        continue;
      }
      int toDelete = item.getAmount();

      while (toDelete > 0)
      {
        int first = first(ci, item, forceDurability, false);

        if (first == -1) {
          item.setAmount(toDelete);
          leftover.put(Integer.valueOf(i), item);
          break;
        }
        ItemStack itemStack = ci.getItem(first);
        int amount = itemStack.getAmount();

        if (amount <= toDelete) {
          toDelete -= amount;

          ci.clear(first);
        }
        else {
          itemStack.setAmount(amount - toDelete);
          ci.setItem(first, itemStack);
          toDelete = 0;
        }
      }
    }

    return leftover;
  }

  public static boolean containsItem(Inventory ci, boolean forceDurability, ItemStack[] items) {
    HashMap leftover = new HashMap();

    ItemStack[] combined = new ItemStack[items.length];
    for (int i = 0; i < items.length; i++) {
      if (items[i] == null) {
        continue;
      }
      for (int j = 0; j < combined.length; j++) {
        if (combined[j] == null) {
          combined[j] = new ItemStack(items[i].getType(), items[i].getAmount(), items[i].getDurability());
          break;
        }
        if ((combined[j].getTypeId() == items[i].getTypeId()) && ((!forceDurability) || (combined[j].getDurability() == items[i].getDurability()))) {
          combined[j].setAmount(combined[j].getAmount() + items[i].getAmount());
          break;
        }
      }
    }

    for (int i = 0; i < combined.length; i++) {
      ItemStack item = combined[i];
      if (item == null) {
        continue;
      }
      int mustHave = item.getAmount();
      int position = 0;

      while (mustHave > 0)
      {
        int slot = next(ci, item, position, forceDurability, false);

        if (slot == -1) {
          leftover.put(Integer.valueOf(i), item);
          break;
        }
        ItemStack itemStack = ci.getItem(slot);
        int amount = itemStack.getAmount();

        if (amount <= mustHave)
          mustHave -= amount;
        else {
          mustHave = 0;
        }
        position = slot + 1;
      }
    }

    return leftover.isEmpty();
  }
}