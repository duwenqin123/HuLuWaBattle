import java.util.List;

/**
 * Created by Administrator on 2018/1/3.
 */
public class HeYiFormation implements Formation {
    @Override
    public void lineUp(int startX, int startY, Orientation orientation,
                       List<? extends GameMap.Creature> creatures) {
        int size=creatures.size();
        int halfSize=(size-1)/2;
        for (GameMap.Creature creature : creatures) {
            creature.setNeedToClear(true);
        }
        //int currentX=startX,currentY=startY;
        if(size>0)
            creatures.get(0).setPos(startX, startY);
        switch (orientation) {
            case LEFT:
                for (int i = 1; i <= halfSize; i++) {
                    creatures.get(2 * i - 1).setPos(startX - i, startY - i);
                    creatures.get(2 * i).setPos(startX - i, startY + i);
                }
                if (size % 2 == 0) {
                    creatures.get(size - 1).setPos(startX - halfSize - 1,
                            startY - halfSize - 1);
                }
                break;
            case RIGHT:
                for (int i = 1; i <= halfSize; i++) {
                    creatures.get(2 * i - 1).setPos(startX + i, startY - i);
                    creatures.get(2 * i).setPos(startX + i, startY + i);
                }
                if (size % 2 == 0) {
                    creatures.get(size - 1).setPos(startX + halfSize + 1,
                            startY - halfSize - 1);
                }
                break;
        }
    }

}
