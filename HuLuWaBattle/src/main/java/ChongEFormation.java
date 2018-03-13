import java.util.List;

/**
 * Created by Administrator on 2018/1/4.
 */
public class ChongEFormation implements Formation {
    public void lineUp(int startX, int startY,Orientation orientation,
                       List<? extends GameMap.Creature> creatures){
        int currentX = startX, currentY = startY;
        for (GameMap.Creature creature : creatures) {
            creature.setNeedToClear(true);
        }
        switch(orientation) {
            case LEFT:
                for (int i = 1; i <= creatures.size(); i++) {
                    if(i%2!=0) {
                        creatures.get(i - 1).setPos(startX-(i/2)*2, startY );
                    }
                    else {
                        creatures.get(i - 1).setPos(startX -i +1, startY -1);
                    }
                }
                break;
            case RIGHT:
                for (int i = 1; i <= creatures.size(); i++) {
                    if(i%2!=0) {
                        creatures.get(i - 1).setPos(startX+(i/2)*2, startY );
                    }
                    else {
                        creatures.get(i - 1).setPos(startX + i-1, startY + 1);
                    }
                }
                break;
            default:
                break;
        }
    }

}
