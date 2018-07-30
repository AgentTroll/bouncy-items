# bouncy-items

Demo solution for https://www.spigotmc.org/threads/wobbling-while-adding-items-in-inventory.325797/

This user pointed out a difference between giving the player an item programatically and picking them up. Thinking this was a matter of packet magic, I spent several hours experimenting with different inventory data options as well as using PlayOutPickupItem, but these didn't do anything.

On the brink of giving up, I ended up finding out that the bouncing items inventory effect is the norm. I was thinking that the no-bounce effect was the norm, and was trying to find a way to validate this observation until I looked at how the player inventory was implemented. It is the PlayOutSetSlot item that causes the bouncing effect to get cancelled, rather than a packet that causes the bouncing effect to happen.
