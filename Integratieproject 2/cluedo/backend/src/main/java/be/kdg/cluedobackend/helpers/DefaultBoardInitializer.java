package be.kdg.cluedobackend.helpers;

import be.kdg.cluedobackend.model.cards.types.CharacterType;
import be.kdg.cluedobackend.model.cards.types.RoomType;
import be.kdg.cluedobackend.model.gameboard.GameBoard;
import be.kdg.cluedobackend.model.gameboard.Room;

import java.util.ArrayList;
import java.util.List;

public final class DefaultBoardInitializer {
    private static List<Integer> makeRange(int low, int up){
        return makeRange(low, up, new ArrayList<>());
    }

    private static List<Integer> makeRange(int low, int up, List<Integer> except){
        //at end
        if (low == up) return List.of(up);

        //when part of exception
        if (except.contains(low)) return makeRange(low+1, up, except);

        //else
        List<Integer> total = new ArrayList<>(List.of(low));
        total.addAll(makeRange(low+1, up, except));
        return total;
    }

    public static GameBoard initialiseDefault() {
        GameBoard gameBoard = new GameBoard();
        gameBoard.setName("Default");
        //row 1
        gameBoard.createTile(8,1);

        //row 2-4
        makeRange(8, 17, makeRange(10,15)).forEach(column ->
                makeRange(2,4).forEach(row -> gameBoard.createTile(column, row)));

        //row 5-6
        makeRange(2, 17, makeRange(10,15)).forEach(column ->
                makeRange(5,6).forEach(row -> gameBoard.createTile(column, row)));

        //row 7
        makeRange(7, 23, makeRange(10,15)).forEach(column ->
                makeRange(7,7).forEach(row -> gameBoard.createTile(column, row)));

        //row 8
        makeRange(8, 23).forEach(column ->
                makeRange(8,8).forEach(row -> gameBoard.createTile(column, row)));

        //row 9
        makeRange(8, 23, makeRange(10,14)).forEach(column ->
                makeRange(9,9).forEach(row -> gameBoard.createTile(column, row)));

        //row 10
        makeRange(8, 16, makeRange(10,14)).forEach(column ->
                makeRange(10,10).forEach(row -> gameBoard.createTile(column, row)));

        //row 11+13-15
        makeRange(7, 16, makeRange(10,14)).forEach(column ->
                makeRange(11,15, makeRange(12,12)).forEach(row -> gameBoard.createTile(column, row)));

        //row 12
        makeRange(2, 16, makeRange(10,14)).forEach(column ->
                makeRange(12,12).forEach(row -> gameBoard.createTile(column, row)));

        //row 16
        makeRange(7, 19).forEach(column ->
                makeRange(16,16).forEach(row -> gameBoard.createTile(column, row)));

        //row 17
        makeRange(7, 23).forEach(column ->
                makeRange(17,17).forEach(row -> gameBoard.createTile(column, row)));

        //row 18
        makeRange(2, 24, makeRange(9,16)).forEach(column ->
                makeRange(18,18).forEach(row -> gameBoard.createTile(column, row)));

        //row 19
        makeRange(2, 18, makeRange(9,16)).forEach(column ->
                makeRange(19,19).forEach(row -> gameBoard.createTile(column, row)));

        //row 20
        makeRange(6, 18, makeRange(9,16)).forEach(column ->
                makeRange(20,20).forEach(row -> gameBoard.createTile(column, row)));

        //row 21-23
        makeRange(7, 18, makeRange(9,16)).forEach(column ->
                makeRange(21,23).forEach(row -> gameBoard.createTile(column, row)));

        //row 24
        makeRange(8, 17, makeRange(11,14)).forEach(column ->
                makeRange(24,24).forEach(row -> gameBoard.createTile(column, row)));

        //SpawnTiles
        gameBoard.createSpawnTile(1,6, CharacterType.PURPLE);
        gameBoard.createSpawnTile(1,19, CharacterType.BLUE);
        gameBoard.createSpawnTile(10,25, CharacterType.GREEN);
        gameBoard.createSpawnTile(15,25, CharacterType.WHITE);
        gameBoard.createSpawnTile(17,1,CharacterType.RED);
        gameBoard.createSpawnTile(24,8,CharacterType.YELLOW);

        initialiseRooms(gameBoard);
        return gameBoard;
    }

    public static void initialiseRooms(GameBoard gameBoard){
        //STUDY
        Room study = gameBoard.createRoom(1, 1, 7, 4, RoomType.STUDY);
        gameBoard.createDoor(7,4, false, study);

        //LIBRARY
        Room library = gameBoard.createRoom(1,7,7,5,RoomType.LIBRARY);
        gameBoard.createDoor(7,9, true, library);
        gameBoard.createDoor(4,11, false, library);

        //BILLIARD_ROOM
        Room billiard = gameBoard.createRoom(1,13,6,5,RoomType.BILLIARDROOM);
        gameBoard.createDoor(2,13, false, billiard);
        gameBoard.createDoor(6,16, true, billiard);

        //CONSERVATORY
        Room conservatory = gameBoard.createRoom(1,20,6,5,RoomType.CONSERVATORY);
        gameBoard.createDoor(5,20, true, conservatory);

        //HALL
        Room hall = gameBoard.createRoom(10,1,6,7,RoomType.HALL);
        gameBoard.createDoor(10,5, true, hall);
        gameBoard.createDoor(12,7, false, hall);
        gameBoard.createDoor(13,7, false, hall);

        //BALL_ROOM
        Room ballroom = gameBoard.createRoom(9,18,8,7,RoomType.BALLROOM);
        gameBoard.createDoor(9,20, true, ballroom);
        gameBoard.createDoor(10,18, false, ballroom);
        gameBoard.createDoor(15,18, false, ballroom);
        gameBoard.createDoor(16,20, true, ballroom);

        //LOUNGE
        Room lounge = gameBoard.createRoom(18,1,7,6, RoomType.LOUNGE);
        gameBoard.createDoor(18,6, false, lounge);

        //DINING_ROOM
        Room dining = gameBoard.createRoom(17,10,8,7, RoomType.DININGROOM);
        gameBoard.createDoor(17,13, true, dining);
        gameBoard.createDoor(18,10, false, dining);

        //KITCHEN
        Room kitchen = gameBoard.createRoom(19,19,6,6,RoomType.KITCHEN);
        gameBoard.createDoor(20,19, false, kitchen);

        //SECRET PASSAGE
        gameBoard.linkPassage(kitchen, study);
        gameBoard.linkPassage(lounge, conservatory);
    }
}
