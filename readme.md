# Serverselector

Serverselector is free and opensource server selector and server starter plugin for paper based servers and crafty
panel control panel. Serverselector registers `/join` command that opens gui inventory. Within the gui end users can
choose which server to join. If the server is not on for some reason it will be started automatically.

> ### **Hint:** This plugin works well with my other plugin `servercloser` (TODO)

## Installation

1. Download the latest release from the releases page in the GitHub.
2. Put the downloaded jar to the `plugins` folder in your server.
3. Run the server once and stop it to generate the configuration. Alternatively you can copy the config yourself
   to `plugins/serverselector/config.toml`.

<details>
  <summary>Default configuration</summary>

   ```toml
    inventorySize = 9
inventoryName = "Select a server"
panelUrl = "https://example.com"
pingTime = 200
username = "admin"
password = "crafty"

[[server]]
name = "<bold>Example server 1</bold>"
slot = 2
material = "DIRT"
lore = ["Join now", "Line 2"]
serverId = 2
serverName = "server_1"

[[server]]
name = "<bold>Example server 2</bold>"
slot = 6
material = "DIAMOND"
lore = ["Join now", "Line 2"]
serverId = 3
serverName = "server_2"

[messages]
notAPlayer = "<red>You can only run this as a player</red>"
noSuchServer = "<red>No server with ID</red>"
sendingToServer = "Sending to server"
startingServer = "Starting the server"
waitingForServer = "Waiting for the server to start"
serverStartError = "<red>Could not start server</red>"
   ```

</details>

## Configuration

### PanelUrl

PanelUrl refers to the url of your crafty panel. It should have the protocol I.E. `http://` or `https://`, but not a
trailing slash.

- Ok: `https://example.com`, `http://example.com`, `http://localhost:8443`, `https://localhost`.
- Not ok: `example.com`, `localhost`, `https://example.com/`

## Ping time

Ping time is how often the plugin checks if the server has started. It is measured in game tics so the default value
is `20*5=100` so 5 seconds.

### Username and password

The username and password should be set to be the credentials for the user that can control the servers you want
Serverselector to be able to start. Only `Access` permission node should be enough.

### Server

Servers can be added with the `[[server]]` header. Item names and the lore supports MiniMessage
formatting. [documentation](https://docs.advntr.dev/minimessage/format.html)

- **Name:** The name of the server that will be displayed to the user.
- **Slot:** The inventory slot that the server item should be.
- **Material:** The item that the server should be represented
  as.  [list of accepted values](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html)
- **Lore:** The description of the item.
- **Server ID:** The id of the server in crafty controller. Can be found in the url of the
  panel `https://localhost:8443/panel/server_detail?id=4` would be id 4.
- **Server name:** Name of the server configured in the bungeecord/velocity/waterfall configuration.

## Messages

Everything under the `[messages]` header are considered as messages, they can be used to localize the plugin to your
language or customize the messages. Messages supports MiniMessage
formatting. [documentation](https://docs.advntr.dev/minimessage/format.html)

## Roadmap
I will only fix issues related to this plugin. No new features are planned!
