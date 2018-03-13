import org.junit.*;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2018/1/7.
 */
public class GameMapTest {
    @Test
    public void testRecordProcess() throws Exception {
        GameMap.huLuWaList = new ArrayList<>();
        GameMap.huLuWaList.add(new GameMap.Creature(17, 15, "DaWa"));

        GameMap.enemyList = new ArrayList<>();
        GameMap.enemyList.add(new GameMap.Creature(9, 13, "Goblin"));

        GameMap.recordProcess();

        try {
            String fileName="HuLuWaBattle/test1.txt";
            Scanner fileInput = new Scanner(new File(fileName));
            Scanner fileInput1 = new Scanner(GameMap.file);
            String line=fileInput.nextLine().trim();
            String line1=fileInput1.nextLine().trim();
            assertEquals(line,line1);

        }catch (IOException ex){
            System.out.println(ex);
        }
        //assertEquals();
    }

    @Test
    public void TestRemoveDeadCreature() throws Exception {
        GameMap.HuLuWas huLuWas = new GameMap.HuLuWas();
        GameMap.huLuWaList = huLuWas.getHuLuWas();
        List<GameMap.Creature> testList = new ArrayList<>(GameMap.huLuWaList);
        GameMap.huLuWaList.get(0).setIsDeath(true);
        GameMap.huLuWaList.get(3).setIsDeath(true);
        testList.remove(0);
        testList.remove(2);

        GameMap.enemyList = new LinkedList<>(Arrays.asList(GameMap.Scorpion.getUniqueScorpion(),
                GameMap.Snake.getUniqueSnake()));
        for (int i = 0; i < 6; i++) {
            GameMap.enemyList.add(new GameMap.Goblin());
        }
        List<GameMap.Creature> testList1 = new ArrayList<>(GameMap.enemyList);
        GameMap.enemyList.get(0).setIsDeath(true);
        GameMap.enemyList.get(3).setIsDeath(true);
        testList1.remove(0);
        testList1.remove(2);

        GameMap.removeDeadCreature();
        assertEquals(testList,GameMap.huLuWaList);
        assertEquals(testList1, GameMap.enemyList);
    }

    /*@Test
    public void TestReadRecord() throws Exception {

        String fileName = "C:/Users/Administrator/workspace/HuLuWaBattle/test.txt";
        List<List<GameMap.Creature>> list1 = new ArrayList<>();
        list1.add(new ArrayList<>());
        list1.add(new ArrayList<>());
        list1.get(0).add(new GameMap.Creature(17, 15, "DaWa"));
        list1.get(1).add(new GameMap.Creature(17, 15, "ErWa"));

        List<List<GameMap.Creature>> list2 = new ArrayList<>();
        list2.add(new ArrayList<>());
        list2.add(new ArrayList<>());
        list2.get(0).add(new GameMap.Creature(9, 13, "Goblin"));
        list2.get(1).add(new GameMap.Creature(9, 13, "Goblin"));

        List<List<GameMap.Creature>> testList1 = new ArrayList<>(), testList2 = new ArrayList<>();
        GameMap.readRecord(fileName, testList1, testList2);

        for (int i = 0; i < list1.size(); i++) {
            for (int j = 0; j < list1.get(i).size(); j++) {
                assertEquals(list1.get(i).get(j).getCreatureName(),
                        testList1.get(i).get(j).getCreatureName());
                assertEquals(list1.get(i).get(j).getX(),
                        testList1.get(i).get(j).getX());
                assertEquals(list1.get(i).get(j).getY(),
                        testList1.get(i).get(j).getY());
            }
        }
        *//*assertEquals(list1, testList1);
        assertEquals(list2,testList2);*//*
    }


    @Test
    public void TestGetNameOfRecordFiles() throws Exception {
        GameMap.directoryName="C:/Users/Administrator/workspace/HuLuWaBattle";
        List<String> list = new ArrayList<>();
        //for (int i = 0; i <= GameMap.fileCount; i++) {
            list.add("record" + 8 + ".txt");
        //}
        assertEquals(list,GameMap.getNameOfRecordFiles());
    }*/
}
