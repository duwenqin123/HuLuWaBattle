import java.util.List;

/**
 * Created by Administrator on 2018/1/5.
 */
public class FangSiFormation implements Formation {

    @Override
    public void lineUp(int startX, int startY, Orientation orientation, List<? extends GameMap.Creature> creatures) {
        int size=creatures.size();
        if(size%4!=0)
            throw new RuntimeException("The size of FangSiFormation must be some times of 4!");
        int edgeSize=size/4;
        for (GameMap.Creature creature : creatures) {
            creature.setNeedToClear(true);
        }
        //int currentX=startX,currentY=startY;
        if(size>0)
            creatures.get(0).setPos(startX, startY);
        switch (orientation) {
            case LEFT:
                for (int i = 1; i <= edgeSize; i++) {
                    creatures.get(2 * i - 1).setPos(startX - i, startY - i);
                    creatures.get(2 * i).setPos(startX - i, startY + i);
                }
                for (int i = edgeSize+1; i < edgeSize*2; i++) {
                    creatures.get(2 * i - 1).setPos(startX - i, startY + i - 2*edgeSize);
                    creatures.get(2 * i ).setPos(startX - i, startY - (i - 2*edgeSize));
                }
                creatures.get(size-1).setPos(startX-2*edgeSize,startY);
                break;
            case RIGHT:
                for (int i = 1; i <= edgeSize; i++) {
                    creatures.get(2 * i - 1).setPos(startX + i, startY - i);
                    creatures.get(2 * i).setPos(startX + i, startY + i);
                }
                for (int i = edgeSize+1; i < edgeSize*2; i++) {
                    creatures.get(2 * i - 1).setPos(startX + i, startY + i - 2*edgeSize);
                    creatures.get(2 * i ).setPos(startX + i, startY - (i - 2*edgeSize));
                }
                creatures.get(size-1).setPos(startX+2*edgeSize,startY);
                break;
        }
    }

}
