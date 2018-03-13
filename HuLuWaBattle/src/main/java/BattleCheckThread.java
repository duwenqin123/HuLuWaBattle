import java.util.List;

/**
 * Created by Administrator on 2018/1/4.
 */
public class BattleCheckThread implements Runnable {
    private List<GameMap.Creature> list1;
    private List<GameMap.Creature> list2;

    public BattleCheckThread(List<GameMap.Creature> list1,
                             List<GameMap.Creature> list2) {
        this.list1=list1;
        this.list2=list2;
    }
    @Override
    public void run() {

        while(true) {
            try {
                for (GameMap.Creature creature1 : list1) {
                    for (GameMap.Creature creature2 : list2) {
                        if (Math.abs(creature1.getX() - creature2.getX()) <= 1 &&
                                Math.abs(creature1.getY() - creature2.getY()) <= 1)
                            GameMap.battle(creature1, creature2);
                    }
                }
                Thread.sleep(500);
            }catch (InterruptedException ex){
                System.out.println(ex);
            }
        }
    }
}
