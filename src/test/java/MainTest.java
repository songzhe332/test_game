import ai.FoodChimera;
import com.game.archive.Archive;
import com.game.entity.Entities;
import com.game.func.Command;
import com.game.item.Item;
import com.game.server.GameRoll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MainTest {
    public static void main(String[] args) {
        //测试chimera
        GameRoll gameRoll = new GameRoll();
        FoodChimera chimera = new FoodChimera("chimera", Item.ItemType.ARMOR);
        GameRoll.getItems().add(chimera);
        gameRoll.fight();
    }

    @Test
    public void testArchive() {
        Archive archive = new Archive();
        archive.showEntityArchive("cow", 20);
    }

    @Test
    public void testCommand(){
        //new GameRoll();
        new Entities().summon();
        Assertions.assertDoesNotThrow(() -> new Command().handlePreCommand("show_archive man]"),"无法在未创建GameRoll实例时使用show_archive");
    }
}
