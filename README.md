# TicTacToeBedrock

## Description
A minecraft nukkit plugin for playing "tic tac toe" against other players.
Games can be both *two-dimensional* and *three-dimensional*.
You can customize how many fields a player needs to mark in a row in order to win.

## Keywords

**Main player**: The player who started the game.

**Opponent player**: The player who was invited to the game.

**Field**: A single field in a game of tic-tac-toe that is either marked by one of the two players or neutral. 

## Usage

### Requesting a game

Use the `/tictactoe` command and provide an opponent player (and *optionally* the game's size and *winRequiredAmount*) to start a new game.

Usage: `/tictactoe <opponent: Player> [sizeX = 3] [sizeY = 1] [sizeZ = 3] [winRequiredAmount = 3]`


The smallest possible game has a size of `(2, 1, 2)`.

`winRequiredAmount` is the amount of fields that have to be marked by one player (in a row or diagonally) for that player to win. This number must not be larger than the biggest dimension of the game and defaults to exactly that.

After this command has been executed, the opponent player reveives a chat invitation message containing the game's size and *winRequiredAmount*. Using the command `/tictactoeaccept` and providing the main player's name, they can join the game.

### Starting the game

After the opponent player accepted the game, the plugin places the game into the world (in front of where the main player was when first executing the `/tictactoe` command).

### Playing the game

The opponent player begins. Taking turns, both players can mark one field at a time by clicking the neutral (white) blocks. The main player has the color **red** and the opponent player has the color **light blue**. The in-world Minecraft blocks are colored according to their state.

Markings that are *"in the air"*, meaning that there are still neutral fields below them, will fall until they *"hit"* a non-neutral block or the bottom of the game.

### Winning the game

As soon as one player marked `winRequiredAmount` fields in a row or diagonally, the game stops, shows the fields that are in a row by marking them yellow and tells both players whether they won or whether they lost. In case of a tie, no player wins and a tie-message appears.

The player who lost (or, in case of a tie, both players) can immediately request a return match using the command `/tictactoe requestReturnMatch`.

### Cancelling the game

During a game, both players can cancel it anytime by executing `/tictactoe cancel`.

When a game is cancelled, it is removed from the world and both players receive a chat message stating that the game is over.

Incase the plugin destroyed part of the world when placing the game, all blocks are restored after the game is finished (mostly).

## Installation

Download the latest **JAR**-file from the releases of this repository (or build the plugin yourself using **maven**) and move it to the `plugins` directory of your Minecraft-server (which has to be a nukkit server, download [here](https://ci.opencollab.dev/job/NukkitX/job/Nukkit/job/master/)). If your server is not hosted locally, you might need to use the **FTP** to transfer the file.

## Configuration

This plugin uses the default config, located in `plugins/Tic-Tac-Toe/config.yml`.

The configuration contains one key, `max_dimension_size`, with an integer value (default is `15`) specifying how large the games on the server are allowed to be at max.

This can be used to prevent server crashing.

## Localization

The plugin is available in *English* and *German*. If the server's language is set to `deu`, *German* is used, otherwise *English* is used.

## State

Games are playable, but the plugin is not yet tested.

## Sounds

This plugin also makes use of ingame sounds.
