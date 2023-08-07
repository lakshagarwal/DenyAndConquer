# DenyAndConquer

"Deny and Conquer” is a multiplayer grid-based game where players compete to claim cells on a shared grid. The primary objective is to paint and claim as many cells as possible while preventing other players from doing the same. A player successfully claims a cell by painting more than 50% of it. The game concludes when all cells are claimed, and the player with the most cells claimed is declared the winner.

Design Overview:
1. Client-Server Architecture: The game is structured as a client-server application. One player acts as the server (host) while others join as clients. All participants, including the host, can play the game.
2. Server Role: 
    - The server plays a central role in managing the game's state. 
    - It keeps track of which cells are locked, claimed, and the scores of each player. 
    - It also broadcasts messages to synchronize game state across all clients.
    - The server listens for incoming client connections on a specific port and handles each client in a separate thread.
3. Client Role:
    - Clients are responsible for player interactions.
    - They provide a GUI allowing players to lock, claim, or unlock cells by painting them.
    - They send messages to the server based on player actions and update their GUI based on messages received from the server.
    - On starting the game, players can choose to either host (acting as the server) or join as a client.

4. Communication: All players, including the server, communicate by sending messages over TCP sockets. This ensures reliable, ordered delivery of messages, which is crucial for the game's consistency.

Application-Layer Messaging Scheme:
1. Connection and Initialization:
    - `YOUR_COLOR(COLOR)`: Upon a client's connection, the server sends this message to assign a unique color to the client.
2. Cell Operations:
    - `LOCK(CELL_NUMBER)`: A client sends this message to the server to indicate the intent to start painting a cell.
    - `UNLOCK(CELL_NUMBER)`: If a client releases a cell without claiming it, this message is sent to the server.
    - `CLAIM(CELL_NUMBER)`: On successfully painting and claiming a cell, a client sends this message to the server.
    - `LOCKED(CELL_NUMBER)`: The server broadcasts this message to inform all clients that a particular cell is currently under paint.
    - `UNLOCKED(CELL_NUMBER)`: This broadcasted message from the server informs clients that a cell has been released without being claimed.
    - `CLAIMED(CELL_NUMBER, COLOR)`: A server-broadcasted message notifying all clients of a cell's successful claim and the color (player) that claimed it.
3. Game Control Messages:
    - `GAME_OVER(RESULT)`: Once all cells are claimed, the server sends this message to all clients, indicating the game's end and its outcome.
4. Message Format:
    - Messages typically start with a keyword indicating the message's purpose, followed by relevant data enclosed in parentheses. This consistent format ensures easy parsing and interpretation of messages on both client and server sides.
