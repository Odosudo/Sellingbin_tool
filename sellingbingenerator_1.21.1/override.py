import json

manual_path = "selling_bin.json"
generated_path = "sellingbin_generated.json"
output_path = "sellingbin_merged.json"

# Load manual file (object with settings + trades)
with open(manual_path, "r") as f:
    manual = json.load(f)

# Load generated file (list of trades only)
with open(generated_path, "r") as f:
    generated_list = json.load(f)

# Extract manual sections
manual_settings = manual.get("settings", {})
manual_trades = manual.get("trades", [])

# Convert lists → dicts keyed by input.filter
def to_dict(trade_list):
    out = {}
    for entry in trade_list:
        try:
            key = entry["input"]["filter"]
        except KeyError:
            print(f"⚠️ Skipping malformed entry: {entry}")
            continue
        out[key] = entry
    return out

manual_dict = to_dict(manual_trades)
generated_dict = to_dict(generated_list)

# Merge: manual overrides generated
merged_dict = generated_dict.copy()
merged_dict.update(manual_dict)

# Convert back to list
merged_trades = list(merged_dict.values())

# Build final structure
merged = {
    "settings": manual_settings,
    "trades": merged_trades
}

# Save output
with open(output_path, "w") as f:
    json.dump(merged, f, indent=4)

print("✅ Merge complete!")
print(f"Manual trades: {len(manual_dict)}")
print(f"Generated trades: {len(generated_dict)}")
print(f"Merged total: {len(merged_trades)}")
