# ASCII Adventurer

ASCII Adventurer is a text-based adventure game where players explore various terrains, encounter enemies, and collect items. The game features different rooms, dungeons, and a village where players can interact with NPCs and purchase items.

## Table of Contents
- [Installation](#installation)
- [Gameplay](#gameplay)
- [Commands](#commands)
- [Contributing](#contributing)
- [License](#license)

## Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/cadenfinley/ASCII-Adventurer.git
    ```

2. Navigate to the project directory:
    ```sh
    cd ASCII-Adventurer
    ```

3. Compile and execute using maven:
    ```sh
    mvn compile exec:java
    ```


## Gameplay

In ASCII Adventurer, you start in a village and can explore various terrains, including forests, mountains, deserts, and oceans. Each terrain has unique, randomly generated dungeons with enemies and treasure. Your goal is to defeat enemies, collect items, and complete dungeons.

### Village

The village is the central hub where you can rest, buy items, and interact with NPCs. You can also warp to different areas using the portal if you have completed enough dungeons.

### Dungeons

Dungeons are challenging areas with enemies and bosses. Each dungeon has a unique layout and set of enemies. You must defeat the boss to complete the dungeon and unlock new areas.

### Inventory

Manage your inventory to keep track of weapons, armor, potions, and keys. Use items strategically to survive encounters and progress through the game.

## Commands

Here are some common commands you can use in the game:

- `north`, `south`, `east`, `west`: Move in the specified direction.
- `fight`: Engage in combat with enemies.
- `run`: Attempt to flee from combat.
- `use [item]`: Use an item from your inventory.
- `drop [item]`: Drop an item from your inventory.
- `inventory`: View your current inventory.
- `map`: View the map of the current dungeon.
- `leave`: Exit the current area or dungeon.

## Contributing

Contributions are welcome! If you would like to contribute to the project, please follow these steps:

1. Fork the repository.
2. Create a new branch:
    ```sh
    git checkout -b feature/your-feature-name
    ```
3. Make your changes and commit them:
    ```sh
    git commit -m "Add your commit message"
    ```
4. Push to the branch:
    ```sh
    git push origin feature/your-feature-name
    ```
5. Open a pull request.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
