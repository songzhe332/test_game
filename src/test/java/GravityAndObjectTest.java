import com.game.entity.Cow;
import com.game.entity.Entity;
import com.game.entity.func.Direction;
import com.game.entity.func.Move;
import com.game.entity.object.GameObject;
import com.game.server.GameRoll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 针对 c3bb85b「增加物体并优化移动」这套新机制的测试。
 *
 * 重力循环本身写死在 GameRoll.run() 的匿名 Runnable 里，无法单独调用，
 * 所以这里直接复刻它的那行 setY(getY() - 0.5f) 来验证数值行为；
 * 判定部分（getEntityOn / isOnObject）是纯方法，直接测真实实现。
 */
public class GravityAndObjectTest {

    /** 每轮把实体表和物体表清空，避免测试互相污染。 */
    @SuppressWarnings("unchecked")
    private static <T> void clearStatic(String name) {
        try {
            Field f = GameRoll.class.getDeclaredField(name);
            f.setAccessible(true);
            ((List<T>) f.get(null)).clear();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void reset() {
        clearStatic("ENTITY_LIST");
        clearStatic("OBJECT_LIST");
    }

    /** 造一头站在指定坐标的牛，并登记进实体表。 */
    private Cow cowAt(String id, float x, float y, float z) {
        Cow cow = new Cow(100, id);
        cow.getMove().setX(x);
        cow.getMove().setY(y);
        cow.getMove().setZ(z);
        GameRoll.getEntities().add(cow);
        return cow;
    }

    /** 造一个放在指定坐标的方块。 */
    private GameObject objectAt(String id, float x, float y, float z) {
        GameObject.placeObject(id, x, y, z);
        return GameRoll.getObjects().get(GameRoll.getObjects().size() - 1);
    }

    // ------------------------------------------------------------------
    // 一、判定逻辑
    // ------------------------------------------------------------------

    @Test
    @DisplayName("站在物体正上方(Y+1)的实体被识别")
    void entityDirectlyOnObjectIsDetected() {
        GameObject board = objectAt("board", 0, -1, 0);
        Cow onTop = cowAt("onTop", 0, 0, 0);   // 0 == -1 + 1

        assertTrue(onTop.isOnObject());
        assertEquals(List.of(onTop), board.getEntityOn());
    }

    @Test
    @DisplayName("X/Z 差一格就不算站在上面")
    void horizontalMismatchIsNotOnObject() {
        objectAt("board", 0, -1, 0);
        Cow offX = cowAt("offX", 1, 0, 0);
        Cow offZ = cowAt("offZ", 0, 0, 1);

        assertFalse(offX.isOnObject());
        assertFalse(offZ.isOnObject());
    }

    @Test
    @DisplayName("同一物体可以托住多个实体")
    void oneObjectSupportsMultipleEntities() {
        GameObject board = objectAt("board", 0, -1, 0);
        Cow a = cowAt("a", 0, 0, 0);
        Cow b = cowAt("b", 0, 0, 0);

        assertEquals(2, board.getEntityOn().size());
        assertTrue(a.isOnObject());
        assertTrue(b.isOnObject());
    }

    @Test
    @DisplayName("没有物体时所有实体都不算被托住")
    void noObjectMeansNothingIsSupported() {
        Cow cow = cowAt("lonely", 0, 0, 0);
        assertTrue(GameRoll.getObjects().isEmpty());
        assertFalse(cow.isOnObject());
    }

    @Test
    @DisplayName("悬浮在半空(Y=0.5)的实体不被托住")
    void halfStepAboveIsNotSupported() {
        objectAt("board", 0, -1, 0);
        Cow cow = cowAt("floating", 0, 0.5f, 0);
        assertFalse(cow.isOnObject());
    }

    @Test
    @DisplayName("同一坐标可以无限叠放物体，且无法区分/删除")
    void multipleObjectsCanStackOnSameCoordinate() {
        GameObject a = objectAt("board", 0, -1, 0);
        GameObject b = objectAt("board", 0, -1, 0);   // 同 id、同坐标
        GameObject c = objectAt("totallyDifferentId", 0, -1, 0); // 不同 id、同坐标

        assertEquals(3, GameRoll.getObjects().size(), "三个物体全被登记，无人拦截");

        Cow cow = cowAt("victim", 0, 0, 0);
        // 每块板子都认为自己托着这头牛——同一实体被重复计数 3 次
        assertEquals(List.of(cow), a.getEntityOn());
        assertEquals(List.of(cow), b.getEntityOn());
        assertEquals(List.of(cow), c.getEntityOn());
        // 但 isOnObject 是布尔短路，叠 1 块和叠 100 块效果完全一样
        assertTrue(cow.isOnObject());
    }

    // ------------------------------------------------------------------
    // 一半、护甲系统（2026-11-24 未提交的新改动）
    // ------------------------------------------------------------------

    @Test
    @DisplayName("护甲减免伤害：c - armor 正确")
    void armorReducesDamage() {
        Cow attacker = new Cow(100, "attacker");
        Cow target = new Cow(100, "target");
        target.setArmor(8.0);
        double before = target.getHealth();

        attacker.attack(target, 20.0);   // 20 - 8 = 12 伤害

        assertEquals(before - 12.0, target.getHealth());
    }

    @Test
    @DisplayName("护甲 > 伤害时完全格挡，不再出现负伤害加血")
    void armorAboveDamageBlocksAllDamage() {
        Cow attacker = new Cow(100, "attacker");
        Cow target = new Cow(100, "target");
        target.setArmor(50.0);
        double before = target.getHealth();

        attacker.attack(target, 20.0);   // 护甲 50 >= 伤害 20 → 实际伤害钳为 0

        assertEquals(before, target.getHealth(), "护甲高于伤害时应完全格挡，不得回血");
    }

    @Test
    @DisplayName("护甲恰好等于伤害：零伤害")
    void armorEqualsDamageIsZero() {
        Cow attacker = new Cow(100, "attacker");
        Cow target = new Cow(100, "target");
        target.setArmor(20.0);
        double before = target.getHealth();

        attacker.attack(target, 20.0);

        assertEquals(before, target.getHealth());
    }

    @Test
    @DisplayName("setArmor 拒绝负数，但装备可无限叠加护甲")
    void armorCannotBeNegativeButCanStackInfinitely() {
        Cow target = new Cow(100, "target");
        target.setArmor(-999);
        assertEquals(0.0, target.getArmor());

        com.game.item.equipment.Armor plate = new com.game.item.equipment.Armor("plate",5.0);
        for (int i = 0; i < 100; i++) {
            plate.equip(target);   // 每次 +5，同一件穿 100 次
        }
        assertEquals(500.0, target.getArmor(), "命令绕过 equipped 标记，护甲可无限刷");
    }

    // ------------------------------------------------------------------
    // 二、重力数值行为（复刻 GameRoll.run() 里的那行）
    // ------------------------------------------------------------------

    @Test
    @DisplayName("从 Y=1 落两次 0.5 正好到 0——你那句是对的")
    void twoHalfStepsLandExactlyOnTop() {
        objectAt("board", 0, -1, 0);   // 顶面 Y = 0
        Cow cow = cowAt("faller", 0, 1, 0);
        assertFalse(cow.isOnObject(), "Y=1 时不在板子上");

        fallOnce(cow);                  // 1.0 -> 0.5
        assertEquals(0.5f, cow.getMove().getY());
        assertFalse(cow.isOnObject(), "Y=0.5 仍不被托住");

        fallOnce(cow);                  // 0.5 -> 0.0
        assertEquals(0.0f, cow.getMove().getY());
        assertTrue(cow.isOnObject(), "Y=0 命中物体顶面，重力应停止");
    }

    @Test
    @DisplayName("浮点网格对齐时下落精确，不会漂")
    void alignedGridFallsExactly() {
        Cow cow = cowAt("faller", 0, 0, 0);
        for (int i = 1; i <= 20; i++) {
            fallOnce(cow);
            assertEquals(-0.5f * i, cow.getMove().getY(), "第" + i + "次下落应精确");
        }
    }

    @Test
    @DisplayName("X/Z 非整数坐标导致实体永远踩不上板子")
    void nonIntegerXZMeansNeverSupported() {
        objectAt("halfBoard", 0.5f, -1, 0);  // 板子在 x=0.5
        Cow cow = cowAt("integerGrid", 0, 0, 0); // 实体永远在 x=0

        assertFalse(cow.isOnObject());
        for (int i = 0; i < 10; i++) {
            fallOnce(cow);
            assertFalse(cow.isOnObject(), "无限下落也踩不上 x=0.5 的板子");
        }
        assertEquals(-5.0f, cow.getMove().getY());
    }

    @Test
    @DisplayName("float 在 2^23 处 -0.5 会被舍入掉，重力卡死")
    void gravityStallsAtTwoPow23() {
        Cow cow = cowAt("deep", 0, 0, 0);
        float edge = 8388608.0f;              // 2^23
        cow.getMove().setY(-edge);

        float before = cow.getMove().getY();
        fallOnce(cow);
        assertEquals(before, cow.getMove().getY(),
                "-2^23 处 ULP=1，-0.5 经 round-to-nearest-even 舍入回原值");

        // 再试 -2^24，ULP=2，-0.5 更是完全无效
        cow.getMove().setY(-16777216.0f);
        float before2 = cow.getMove().getY();
        fallOnce(cow);
        assertEquals(before2, cow.getMove().getY());
    }

    @Test
    @DisplayName("NaN / Infinity 坐标的板子永远接不住实体")
    void nanAndInfinityCoordinatesNeverMatch() {
        GameObject nanBoard = objectAt("nanBoard", Float.NaN, -1, 0);
        GameObject infBoard = objectAt("infBoard", Float.POSITIVE_INFINITY, -1, 0);
        Cow cow = cowAt("victim", 0, 0, 0);

        assertTrue(nanBoard.getEntityOn().isEmpty());
        assertTrue(infBoard.getEntityOn().isEmpty());
        assertFalse(cow.isOnObject());
    }

    @Test
    @DisplayName("Infinity 加一仍是 Infinity；Float.MAX_VALUE 加一却被吞掉")
    void infinityPlusOneIsStillInfinity() {
        assertEquals(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY + 1.0f);
        // 注意：Float.MAX_VALUE 在 double 里加 1 不会溢出成 Infinity，
        // 而是被精度吞掉、原样不变——这正是"大数加小数等于没加"的实证。
        assertEquals((double) Float.MAX_VALUE, (double) Float.MAX_VALUE + 1.0);
    }

    // ------------------------------------------------------------------
    // 三、Move 本身
    // ------------------------------------------------------------------

    @Test
    @DisplayName("移动是原地修改而非每次新建对象")
    void moveMutatesInPlace() {
        Cow cow = new Cow(100, "mover");
        Move original = cow.getMove();

        cow.move(Direction.Directions.RIGHT, "mover");
        assertSame(original, cow.getMove(), "move() 后应是同一个 Move 对象");
        assertEquals(1.0f, cow.getMove().getX());

        cow.move(Direction.Directions.UP, "mover");
        cow.move(Direction.Directions.FORWARD, "mover");
        assertSame(original, cow.getMove());
        assertEquals(1.0f, cow.getMove().getY());
        assertEquals(1.0f, cow.getMove().getZ());
    }

    /** 复刻 GameRoll.run() 中重力逻辑的“不在物体上就 -0.5”那一步。 */
    private static void fallOnce(Entity entity) {
        if (!entity.isOnObject() && entity.getMove() != null) {
            Move move = entity.getMove();
            move.setY(move.getY() - 0.5f);
        }
    }
}
