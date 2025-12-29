This is a tool base mod only ment to be added to a mod pack when you want to change the selling_bin.json file. 
Most commonly used in BCG+ And Mayview

=================================================
Instructions: 
=================================================
Download the mod packaged mod from releases. 
Extract and you will have 2 files. Both have seperate locations but are easy to find. SellingGen.jar will go into mod folder. 
And override.py will go into config/sellingbin folder. 

If you use prism launcher your file path will look something like this. (EXAMPLE: PrismLauncher/instances/Mayview Extended/minecraft/config/sellingbin/)
you should see a file aready there it will be Selling_bin.json. In order to use the override.py you need to first run the game with the mod in your folder and launch any world, It works on client worlds, and server worlds. then type the command /sellingbin regen
If it was succesful you will see. "SellingBin file regenerated!" In green in your chat. 


Afterwards now in /config/sellingbin/  You will see 3 files, selling_bin.json, sellingbin_generated.json, and override.py. Now all you need to do is run override.py and it will merge both files into one and will give you sellingbin_merged.json. simply rename this file selling_bin.json or copy and paste the contents into it. 
afterwards Simply remove the mod sellinggen.jar or disable it and play like usual. 
