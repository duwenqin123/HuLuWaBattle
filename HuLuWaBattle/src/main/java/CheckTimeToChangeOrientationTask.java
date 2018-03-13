import com.sun.org.apache.xpath.internal.operations.Or;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2018/1/5.
 */
public class CheckTimeToChangeOrientationTask implements Runnable {
    private List<GameMap.Creature> creatures;
    private Orientation orientation;
    private Lock lock = new ReentrantLock();

    public CheckTimeToChangeOrientationTask(List<GameMap.Creature> creatures,
                                            Orientation orientation) {
        this.creatures=creatures;
        this.orientation=orientation;
    }
    /*public CheckTimeToChangeOrientationTask(Orientation orientation) {
        //this.creatures=creatures;
        this.orientation=orientation;
    }*/
    @Override
    public void run() {
        try {
            while (GameMap.state!= GameMap.State.STOP) {

                if (GameMap.state == GameMap.State.RUN) {
                    lock.lock();
                    if (GameMap.isTimeToChangeDirection(creatures, orientation)) {
                        for (GameMap.Creature creature : creatures) {
                            creature.reverseOrientation();
                        }
                        reverseOrientation();
                    }
                    lock.unlock();
                    Thread.sleep(GameMap.SLEEPTIME);
                }
            }
            }catch(InterruptedException ex){
                System.out.println(ex);
            }
    }
    private void reverseOrientation() {
        switch (orientation) {
            case LEFT:
                orientation = Orientation.RIGHT;
                break;
            case RIGHT:
                orientation = Orientation.LEFT;
                break;
            default:

        }
    }
}
