# Faster Copper Golem - Enhanced Edition

An enhanced fork of the original Faster Copper Golem mod with advanced sorting, filtering, and AI features.

## ✨ Features

### Original Features (PixelIndieDev)
- **2x Movement Speed** - Golems move faster
- **Expanded Search Radius** - 16 blocks horizontal, 8 blocks vertical
- **64 Item Capacity** - Carry full stacks
- **Animation Fix** - Smooth animations at high speeds

### New Features (Redtracx)
- **Smart Sorting** - Prevents item mixing in chests
- **Item Frame Filtering** - Designate chests for specific items
- **Tag-Based Sorting** - Groups items by tags (logs, planks, etc.)
- **Chest Commands** - `!Dump` (accepts all) and `!Ignore` (blocks golem)
- **GUI Pickup Filter** - Shift+Right-Click to open filter GUI
- **NBT Persistence** - Filter and memory survive restarts
- **Memory Pathfinding** - Golems remember and prioritize known chests
- **ModMenu Integration** - Full in-game configuration

## 📦 Installation

**Required on BOTH Client and Server!**

1. Install [Fabric Loader](https://fabricmc.net/)
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download this mod
4. Place JAR in `.minecraft/mods/` on **both** client and server

## ⚙️ Configuration

Access via **ModMenu** or edit `config/faster_copper_golem.json`:

```json
{
  "gollemInteractionTime": "Fastest",
  "gollemMaxStackSize": 64,
  "gollemSearchRadius": "Largest",
  "gollemMovingSpeed": "Fastest",
  "gollemAmountChestRemembered": "Extreme",
  "smartSorting": true,
  "frameSorting": true,
  "tagSorting": true,
  "nameSorting": true
}
```

**Enum Values:**
- `gollemInteractionTime`: Vanilla, Fast, Faster, Fastest
- `gollemSearchRadius`: Vanilla, Large, Larger, Largest, Extreme
- `gollemMovingSpeed`: Vanilla, Fast, Faster, Fastest
- `gollemAmountChestRemembered`: Vanilla, Many, More, Most, Extreme
- `gollemMaxStackSize`: 16-64 (integer)

All features can be toggled in-game via ModMenu!

## 🎮 Usage

### Pickup Filter
- Shift + Right-Click on Golem
- Place items you want Golem to IGNORE
- Items persist across restarts

### Chest Commands
- Rename chest to `!Dump` - Accepts any item
- Rename chest to `!Ignore` - Golem never uses it

### Item Frames
- Place item frame on chest
- Put item in frame
- Golem only places that item type

## 📜 License

GNU General Public License v3.0 only

## 👥 Authors

- **PixelIndieDev** - Original mod
- **Redtracx** - Enhancements & modifications

See [CONTRIBUTORS.md](CONTRIBUTORS.md) for detailed attribution.

## 🔗 Links

- **Original Mod**: [Modrinth](https://modrinth.com/mod/faster-copper-golem)
- **This Fork**: [GitHub](https://github.com/Redtracx/Faster-Copper-Golem)
- **Issues**: [GitHub Issues](https://github.com/Redtracx/Faster-Copper-Golem/issues)

## 🤝 Contributing

This is a fork under GPL-3.0-only. Contributions welcome via pull requests.

## 📝 Changelog

### Enhanced Edition v1.0.0 (2025)
- Added Smart Sorting
- Added Item Frame Filtering
- Added Tag-Based Sorting
- Added Chest Name Commands
- Added GUI Pickup Filter
- Added NBT Persistence
- Added Memory Pathfinding
- Integrated ModMenu Config GUI
- Updated copyright and attribution
