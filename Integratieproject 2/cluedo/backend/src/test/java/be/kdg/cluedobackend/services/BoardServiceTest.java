package be.kdg.cluedobackend.services;

import be.kdg.cluedobackend.model.cards.types.CharacterType;
import be.kdg.cluedobackend.model.cards.types.RoomType;
import be.kdg.cluedobackend.model.gameboard.Room;
import be.kdg.cluedobackend.model.gameboard.Tile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.function.Predicate;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class BoardServiceTest {
    @Autowired
    private BoardService boardService;

    @Test
    public void isUserInRoomWithPassageTest() throws Exception{
        boardService.moveCharacter(CharacterType.RED, 1,1,0);

        Room result = boardService.getPassage(CharacterType.RED, 0);

        Assert.assertNotNull(result);
    }

    @Test
    public void isUserInNormalRoom() throws Exception{
        boardService.moveCharacter(CharacterType.RED, 9,18,0);

        Room result = boardService.getPassage(CharacterType.RED, 0);

        Assert.assertNull(result);
    }

    @Test
    public void getPossibleMovesTest() throws Exception {
        boardService.moveCharacter(CharacterType.RED, 17,1,0);
        Set<Tile> results = boardService.getPossibleMoves(CharacterType.PURPLE,5, 0);

        long result = results
                .stream()
                .filter(t -> t.getXCoord() == 6 && t.getYCoord() == 6)
                .count();

        Assert.assertEquals(1, result);
    }

    @Test
    public void moveCharacterTest() throws Exception {
        Tile t = boardService.moveCharacter(CharacterType.RED, 9,4, 0);

        Assert.assertEquals(9, t.getXCoord());
        Assert.assertEquals(4, t.getYCoord());
    }

    @Test
    public void impossibleMoveCharacterTest() throws Exception{
        Tile t = boardService.moveCharacter(CharacterType.RED, 6,6,0);

        Set<Tile> possibles = boardService.getPossibleMoves(CharacterType.PURPLE, 5, 0);

        Assert.assertFalse(possibles.contains(t));
    }

    @Test
    public void possibleMoveOtherGamesTest() throws Exception{
        boardService.setupBoard(-1);
        Tile t = boardService.moveCharacter(CharacterType.RED, 6,6,0);
        Set<Tile> possibles = boardService.getPossibleMoves(CharacterType.PURPLE, 5, -1);
        Predicate<Tile> condition = ti -> t.getXCoord() == ti.getXCoord() && t.getYCoord() == ti.getYCoord();
        long result = possibles.stream().filter(condition).count();
        Assert.assertEquals(1, result);
    }

    @Test
    public void possibleMoveBlockedUserTest() throws Exception{
        boardService.moveCharacter(CharacterType.RED, 8,23,0);
        Set<Tile> possibles = boardService.getPossibleMoves(CharacterType.GREEN, 5, 0);
        assert(possibles.size() == 2);
    }

    @Test
    public void passageSuccessTest() throws Exception{
        boardService.moveCharacter(CharacterType.RED, 1,1,0);

        Room destination = boardService.getPassage(CharacterType.RED, 0);

        Assert.assertEquals(RoomType.KITCHEN, destination.getRoomType());
    }

    @Test
    public void passageFromTileTest() throws Exception {
        boardService.moveCharacter(CharacterType.RED, 6,6,0);

        Room destination = boardService.getPassage(CharacterType.RED, 0);

        Assert.assertNull(destination);
    }

    @Test
    public void passageRoomWithoutPassageTest() throws Exception {
        boardService.moveCharacter(CharacterType.RED, 9,18,0);

        Room destination = boardService.getPassage(CharacterType.RED, 0);

        Assert.assertNull(destination);
    }

    @Test
    public void passageUsageSuccessTest() throws Exception {
        boardService.moveCharacter(CharacterType.RED, 1,1,0);

        Room destination = boardService.takePassage(CharacterType.RED, 0);

        Assert.assertEquals(RoomType.KITCHEN, destination.getRoomType());
    }
}
