package com.game.func;

import com.game.archive.Archive;
import com.game.client.Config;
import com.game.entity.Entities;
import com.game.entity.Entity;
import com.game.entity.func.Direction;
import com.game.entity.func.Move;
import com.game.entity.object.GameObject;
import com.game.entity.object.bomb.Bomb;
import com.game.item.Item;
import com.game.item.equipment.Equipable;
import com.game.server.GameRoll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import static com.game.entity.func.BodyPart.FOOT;

public class Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(Command.class);
    //']'为命令终止字符
    public static final char COMMAND_END = ']';
    public static final char SPACE = ' ';

    public void scanMove(Entity entity, String id) {
        if(!Config.isHeadlessMode()){return;}
        Scanner scanMove = Config.INPUT_SCANNER;
        System.out.println("请输入方向");
        String direction = scanMove.nextLine();
        try {
            switch (direction) {
                case ("forward"):
                    GameRoll.entityMove(entity, Direction.Directions.FORWARD, id, FOOT);
                    break;
                case ("backward"):
                    GameRoll.entityMove(entity, Direction.Directions.BACKWARD, id, FOOT);
                    break;
                case ("left"):
                    GameRoll.entityMove(entity, Direction.Directions.LEFT, id, FOOT);
                    break;
                case ("right"):
                    GameRoll.entityMove(entity, Direction.Directions.RIGHT, id, FOOT);
                    break;
                case ("up"):
                    GameRoll.entityMove(entity, Direction.Directions.UP, id, FOOT);
                    break;
                case ("down"):
                    GameRoll.entityMove(entity, Direction.Directions.DOWN, id, FOOT);
                    break;
                case null, default:
                    GameRoll.entityMove(entity, null, id, null);
                    break;
            }
        } catch (Exception e) {
            LOGGER.warn("位置不合法", e);
        }
    }

    public Entity chooseEntity() {
        if(!Config.isHeadlessMode()){return null;}
        Scanner chooser = Config.INPUT_SCANNER;
        String entity = chooser.nextLine();
        if ("exit".equals(entity)) {
            System.out.println("进程已退出");
            System.exit(0);
        }
        Entity chosen = null;
        try {
            for (Entity e : GameRoll.getEntities()) {
                if (entity.equals(e.getId())) {
                    chosen = e;
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.warn("该实体不存在");
            return null;
        }
        return chosen;
    }

    public boolean autoFightCommand(){
        if(!Config.isHeadlessMode()){return false;}
        Scanner scanner = Config.INPUT_SCANNER;
        String answer = scanner.nextLine();
        return "是".equals(answer);
    }

    public void showEntityArchive(Archive archive,String name){
        if(GameRoll.getEntities().isEmpty()){
            LOGGER.warn("暂无实体，请先生成实体！");
            return;
        }
        for (Entity entity : GameRoll.getEntities()){
            if (name.equals(entity.getId())){
                archive.showEntityArchive(entity.getId(),entity.getHealth());
            }
        }
    }

    public void commandError(String msg){
        LOGGER.error("命令执行失败：{}",msg);
    }

    public void handlePreCommand(String command){
        StringBuilder sb = new StringBuilder();
        ArrayList<String> commands = new ArrayList<>();
        for (int charPoint : command.chars().toArray()){
            for (char ch : Character.toChars(charPoint)){
                if(ch == SPACE){
                    commands.add(sb.toString());
                    sb = new StringBuilder();
                }
                else if (ch == COMMAND_END) {
                    commands.add(sb.toString());
                }
                else {
                    sb.append(ch);
                }
            }
        }

        String baseCommand = "";
        CopyOnWriteArrayList<String> args = new CopyOnWriteArrayList<>();
        for (String cmd : commands) {
            if(cmd.equals(commands.getFirst())){
                baseCommand = cmd;
            }
            else{
                args.add(cmd);
            }
        }

        switch (baseCommand){
            case "exit" -> System.exit(0);
            case "show_archive" -> showEntityArchive(GameRoll.getInstance().getArchive(), args.getFirst());
            case "equip" -> GameRoll.getEntities().forEach(entity -> {
                String equipTarget = args.get(0);
                String equipment = args.get(1);
                Stream<Entity> entityStream = GameRoll.getEntities().stream();
                Stream<Item> itemStream = GameRoll.getItems().stream();
                Optional<Entity> target = entityStream.filter(e -> e.getId() != null)
                        .filter(e -> e.getId().equals(equipTarget))
                        .findAny();
                target.orElseThrow(() -> new CommandException("实体不存在",this,equipTarget,Double.NaN));
                itemStream.filter(i -> i.name != null)
                        .filter(i -> i.name.equals(equipment))
                        .findAny()
                        .orElseThrow(() -> new CommandException("装备不存在",this));
                if(entity.getId().equals(equipTarget)){
                    GameRoll.getItems().forEach(item -> {
                        if(item.name != null) {
                            if (item.name.equals(equipment) && item instanceof Equipable) {
                                ((Equipable) item).equip(entity);
                            }
                        }
                    });
                }
            });
            case "place" -> {
                String objectId = args.get(0);
                float x = Float.parseFloat(args.get(1));
                float y = Float.parseFloat(args.get(2));
                float z = Float.parseFloat(args.get(3));
                GameObject.placeObject(objectId,x,y,z);
                LOGGER.info("在{},{},{}放置了{}",x,y,z,objectId);
            }
            case "explode" -> {
                float x = Float.parseFloat(args.get(0));
                float y = Float.parseFloat(args.get(1));
                float z = Float.parseFloat(args.get(2));
                for(GameObject object : GameRoll.getObjects()){
                    Move move = object.getMove();
                    if(object instanceof Bomb bomb && move.getX() == x && move.getY() == y && move.getZ() == z){
                        bomb.explode();
                    }
                }
            }
            case "test" -> {
                String testId = args.get(0);
                switch (testId) {
                    case "add_object_to_entity_list" -> {
                        String objectId = args.get(1);
                        for (GameObject object : GameRoll.getObjects()) {
                            if (object.getId().equals(objectId)) {
                                GameRoll.getEntities().add(object);
                                break;
                            }
                        }
                    }
                    case "second_time_add_to_place" -> {
                        for (Entity entity : GameRoll.getEntities()) {
                            String entityId = args.get(1);
                            if (entityId.equals(entity.getId())) {
                                entity.getPlace().addEntity(entity);
                                break;
                            } else if (args.get(1).equals("other")) {
                                entityId = args.get(2);
                                if (entityId.equals(entity.getId())) {
                                    String placeId = args.get(3);
                                    switch (placeId) {
                                        case "grass_land" -> Entities.grassLand.addEntity(entity);
                                        case "sea" -> Entities.sea.addEntity(entity);
                                        case "in_lava" -> Entities.inLava.addEntity(entity);
                                        default -> LOGGER.warn("未知场地：{}", placeId);
                                    }
                                }
                            }
                        }
                    }
                    case "create_item" -> {
                        String itemType = args.get(1);
                        String itemName = args.get(2);
                        boolean shouldAdd = Boolean.parseBoolean(args.get(3));
                        Item item = Item.create(Item.ItemType.valueOf(itemType),itemName);
                        if(shouldAdd){
                            GameRoll.getItems().add(item);
                        }
                        else{
                            LOGGER.warn("{}未被添加至物品列表",itemName);
                        }
                    }
                    default -> LOGGER.warn("未知测试项：{}",testId);
                }
            }
            default -> LOGGER.warn("未知命令：{} {}",baseCommand,args);
        }
    }
}
