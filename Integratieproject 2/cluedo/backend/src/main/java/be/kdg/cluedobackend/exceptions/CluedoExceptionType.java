package be.kdg.cluedobackend.exceptions;

import lombok.Getter;

public enum CluedoExceptionType {
    // Player & user
    USER_NOT_FOUND("User not found"),
    FRIEND_NOT_FOUND("Friend not found"),
    INVALID_ADD_FRIEND("This relationship already exists"),
    PLAYER_NOT_FOUND("Player not found"),
    PLAYER_NOT_IN_LOBBY("Player not in lobby"),
    PLAYER_NOT_HOST("Player not host"),
    PLAYER_USER_NOT_LINKED("Player not linked to user"),
    PLAYER_ALREADY_IN_LOBBY("Player already in lobby"),

    // Lobby
    CLUEDO_NOT_FOUND("Cluedo not found"),
    CHARACTERTYPE_TAKEN("Charactertype is already taken"),
    LOBBY_NAME_EMPTY("Lobbyname is empty"),
    LOBBY_TURN_DURATION_TOO_SHORT("Lobby turn duration must be greater than one"),
    LOBBY_MAX_PLAYERS_INVALID("Lobby max players must be between 3 and 6"),
    LOBBY_START_PLAYER_COUNT("Lobby must have more than 3 players to start"),
    LOBBY_FULL("Lobby is full"),
    GAME_ALREADY_STARTED("Game already started"),

    // Game
    GAME_NOT_ACTIVE("This game is not active"),
    CATEGORY_NOT_EXIST("Category does not exist"),
    SCENE_NOT_ACCUSATION("Scene is not an accusation"),
    SCENE_NOT_SUGGESTION("Scene is not a suggestion"),
    NOTEBOOK_NOT_FOUND("Notebook not found"),
    INVALID_SUGGESTION("This suggestionDto could not be validated"),
    INVALID_TURN("The given turn is invalid"),

    // Board
    BOARD_NOT_FOUND("Board not found"),

    // Cards
    CARD_NOT_FOUND("Card not found"),

    ;

    @Getter
    private final String message;

    CluedoExceptionType(String message) {
        this.message = message;
    }
}
