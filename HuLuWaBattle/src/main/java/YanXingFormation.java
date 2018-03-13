import java.util.List;

/**
 * Created by Administrator on 2018/1/3.
 */
public class YanXingFormation implements Formation {
    @Override
    public void lineUp(int startX, int startY, Orientation orientation, List<? extends GameMap.Creature> creatures) {
        int currentX = startX, currentY = startY;
        for (GameMap.Creature creature : creatures) {
            creature.setNeedToClear(true);
        }
        switch(orientation) {
            case LEFT:
                for (GameMap.Creature creature : creatures) {
                    creature.setPos(currentX--,currentY--);
                }
                break;
            case RIGHT:
                for (GameMap.Creature creature : creatures) {
                    creature.setPos(currentX++,currentY--);
                }
                break;
            default:
                break;
        }

    }

}
