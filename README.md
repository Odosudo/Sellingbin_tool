SellingGen
A lightweight trade‑generation helper for modpack developers

  
  
  
  

Versioning
SellingGen follows a simple versioning scheme:

Major — Breaking changes to the JSON format or merge behavior

Minor — New features or improvements

Patch — Bug fixes or small adjustments

Current recommended version:
v1.1.0 (stable for BCG+ and Mayview)

You can expand this later with a changelog or compatibility matrix if you want.

Overview
SellingGen is a tool‑only mod designed for modpack creators who need to regenerate or modify the selling_bin.json file used by SellingBin‑based economy systems. It is commonly used in BCG+ and Mayview packs.

This mod is not intended for normal gameplay — only for development workflows.

Installation
Download the packaged release and extract it. You will get two files:

SellingGen.jar — place this in your mods folder

override.py — place this in config/sellingbin/

Example Prism Launcher path:

Code
PrismLauncher/instances/Mayview Extended/minecraft/config/sellingbin/
You should already see a selling_bin.json file in this folder.

Generating the Files
Launch the game with SellingGen.jar installed.

Enter any world (single‑player or server).

Run:

Code
/sellingbin regen
If successful, you’ll see:

“SellingBin file regenerated!” (green text)

After this, the folder will contain:

selling_bin.json — your manual file

sellingbin_generated.json — the auto‑generated file

override.py — the merge script

Merging Your Changes
Run override.py using Python. It will:

Read both JSON files

Keep all your manual edits

Add any new generated trades

Output:

Code
sellingbin_merged.json
Rename this file to selling_bin.json or copy its contents into the existing file.

Final Step
Once your merged file is in place:

Remove SellingGen.jar  
or

Disable it in your launcher

Your pack will now use the updated selling_bin.json normally.
