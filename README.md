## Simple whitelist plugin for your offline-mode server. Two types of storage: **Yaml** or **MySql**

modrinth: https://modrinth.com/plugin/name-based-whitelist

###  How to start using plugin?
1. Disable Minecraft's whitelist in server.properties (white-list=false)
2. Put plugin in plugins folder
3. Start your server

### If you want to use MySql:
- Change storage-type to mysql in the config.
- Specify the data from the database in the config.
- Restart your server.

###  Commands:
/nbwl add <username> - add player in whitelist<br />
/nbwl remove <username> - remove player in whitelist<br />
/nbwl enable - Enable Name Based Whitelist<br />
/nbwl remove - Disable Name Based Whitelist<br />
/nbwl reload - Config Reload<br />

###  Permissions:
namebasedwhitelist.* - for all command<br />
namebasedwhitelist.modify - for add/remove command<br />
namebasedwhitelist.manage - for reload/enable/disable command<br />
