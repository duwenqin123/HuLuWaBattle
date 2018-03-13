import java.util.List;

/**
 * Created by Administrator on 2018/1/3.
 */
public interface Formation {
    void lineUp(int startX, int startY,Orientation orientation,
                List<? extends GameMap.Creature> creatures);
}
