/**
 * Created by Administrator on 2017/11/12.
 */

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GameMap extends Application {
    enum State{
        RUN,STOP,SUSPEND,BEGIN,REPLAY
    }

    private static GridPane gridPane = new GridPane();
    private static BorderPane borderPane = new BorderPane();
    private static StackPane stackPane = new StackPane();
    private static HBox hBox = new HBox();
    private static HBox topPane = new HBox();
    private static BorderPane borderPane1 = new BorderPane();
    private static VBox vBox = new VBox();
    private static HBox hBox1 = new HBox();
    private static HBox hBox2 = new HBox();
    private static String stateString="Initial";
    static Text text = new Text(20, 10, "");
    static String gameOverString = "Game Over";
    static String winString = "You Won!";
    final static int BASE=100;
    final static int N=30;
    final static int M=30;
    final static int SLEEPTIME=300;
    public static List<Creature> huLuWaList;
    public static List<Creature> enemyList;
    private static Random random = new Random(47);
    static Lock lock = new ReentrantLock();
    static State state=State.BEGIN;
    static int fileCount=9;
    static String directoryName = "HuLuWaBattle";
    //private static Timeline animation;
    static Formation huLuWaFormation=new ChangSheFormation();
    static Formation enermyFormation = new HeYiFormation();
    static List<String> formationNameList = new ArrayList<>(Arrays.asList(
            "ChangShe", "HeYi", "ChongEr", "YangXing", "FangSi"
    ));
    private static ExecutorService executor= Executors.newCachedThreadPool();
    static File file = new File(directoryName+"/record" + fileCount + ".txt");
//    boolean targetIsHuLuWa=true;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        final int LEN=100;
        stackPane.getChildren().add(gridPane);
        borderPane.setCenter(stackPane);
        borderPane.setTop(topPane);
//        borderPane.getChildren().addAll(topPane, gridPane);
        topPane.getChildren().addAll(vBox,hBox1,hBox2,hBox);
        topPane.setAlignment(Pos.CENTER);
        topPane.setStyle("-fx-border-color: green;");
        vBox.getChildren().addAll(new Label("Key function: "), new Label("   L: Replay"),
                new Label("   Space: Run/Suspend"),new Label("   UP: ChangeFormation"), new Label("   E: Stop"),
                new Label("State: " + stateString));

        //设置结束后输出的动画
        text.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.ITALIC, 50));
        text.xProperty().bind(stackPane.widthProperty().divide(2).subtract(10));
        text.yProperty().bind(stackPane.heightProperty().divide(2).subtract(10));
        stackPane.getChildren().add(text);
        Timeline animation = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            if(state==State.STOP) {
                if (text.getText().length() != 0) {
                    text.setText("");
                } else {
                    if (huLuWaList.isEmpty()) {
                        text.setText(gameOverString);
                    } else {
                        text.setText(winString);
                    }
                }
            }
        }));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();

        //设置提供改变阵型的组合框
        hBox1.setPadding(new Insets(15));
        hBox1.getChildren().add(new Label("Select a formation for HuLuWas: "));
        ComboBox<String> cbo1 = new ComboBox<>();
        cbo1.setPrefWidth(150);
        cbo1.setPrefHeight(30);
        hBox1.getChildren().add(cbo1);
        cbo1.getItems().addAll(formationNameList);
        cbo1.setValue(formationNameList.get(0));
        /*new Thread(
                new ChangeFormationCboTask(cbo1, true)
        ).start();*/
        executor.execute(new ChangeFormationCboTask(cbo1, true));

        hBox2.setPadding(new Insets(15));
        hBox2.getChildren().add(new Label("Select a formation for enemys: "));
        ComboBox<String> cbo2 = new ComboBox<>();
        cbo2.setPrefWidth(150);
        cbo2.setPrefHeight(30);
        hBox2.getChildren().add(cbo2);
        cbo2.getItems().addAll(formationNameList);
        cbo2.setValue(formationNameList.get(1));
        /*new Thread(
                new ChangeFormationCboTask(cbo2, false)
        ).start();*/
        executor.execute(new ChangeFormationCboTask(cbo2, false));

        HuLuWas huLuWas = new HuLuWas();
        huLuWaList = huLuWas.getHuLuWas();
        huLuWaList.add(GrandPa.getUniqueGrandPa());
        huLuWaFormation.lineUp(N / 2 - 3, M / 2, Orientation.LEFT, huLuWaList);

        //Goblin[] goblins = new Goblin[6];
        enemyList = new LinkedList<>(Arrays.asList(Scorpion.getUniqueScorpion(),
                Snake.getUniqueSnake()));
        for (int i = 0; i <6; i++) {
            enemyList.add(new Goblin());
        }
        enermyFormation.lineUp(N / 2 + 3, M / 2, Orientation.RIGHT, enemyList);
//        putToMap(enemyList);

        drawMap();
//        animation = new Timeline(new KeyFrame(Duration.millis(100), e -> drawMap()));
//        animation.setCycleCount(Timeline.INDEFINITE);
//        animation.play();

        /*new Thread(new SetStateLabelTask()
        ).start();*/
        executor.execute(new SetStateLabelTask());

        stackPane.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case SPACE:
//                    goblin1.start();
                    switch (state) {
                        case BEGIN:
                            startGame();
                            break;
                        case RUN:
                            state = State.SUSPEND;
                            break;
                        case SUSPEND:
                            state = State.RUN;
                            break;
                        default:
                    }
                    break;
                case L:
                    hBox.setPadding(new Insets(15));
                    hBox.getChildren().add(new Label("Select a file: "));

                    ComboBox<String> cbo = new ComboBox<>();
                    cbo.setPrefWidth(150);
                    cbo.setPrefHeight(30);
                    hBox.getChildren().add(cbo);

                    List<String> fileNameList=getNameOfRecordFiles();

                    cbo.getItems().addAll(fileNameList);
                    //cbo.setValue(fileNameList.get(0));
                    cbo.setOnAction(v1 -> {
                        if(state==State.STOP){
                            state=State.REPLAY;
                            startGame();
                        }
                        state = State.REPLAY;
                        String fileName = directoryName+"/" + cbo.getValue();
                        List<List<Creature>> list1 = new ArrayList<>();
                        List<List<Creature>> list2 = new ArrayList<>();
                        readRecord(fileName, list1, list2);
                        int[] index = {0};
                        new Thread(() -> {
                            try {
                                while (state != State.STOP) {
                                    //lock.lock();
                                    if (index[0] >= list1.size()) {
                                        state=State.STOP;
                                        //drawMap();
                                        Platform.runLater(() -> {
                                                    drawMap(list1.get(list1.size()-1),list2.get(list2.size()-1));
                                                }
                                        );
                                        break;
                                    }
                                    Platform.runLater(() -> {
                                                drawMap(list1.get(index[0]-1),list2.get(index[0]-1));
                                            }
                                    );
                                    index[0]++;
                                    //lock.unlock();
                                    Thread.sleep(SLEEPTIME);
                                }
                            } catch (InterruptedException ex) {
                                System.out.println(ex);
                            }
                        }).start();
                        topPane.getChildren().remove(hBox);
                        //state = State.STOP;
                    });
                    break;
                case UP:
                    if(state==State.RUN || state==State.BEGIN) {
                        int rand = random.nextInt(formationNameList.size());
                        changeFormation(rand, huLuWaList, huLuWaFormation);
                    }
                    break;
                case E:
                    state = State.STOP;
                default:
                    break;
            }
        });

        Scene scene = new Scene(borderPane,870,870);
        primaryStage.setTitle("GameMap");
        primaryStage.setScene(scene);
        primaryStage.show();
//        gridPane.requestFocus();
        stackPane.requestFocus();
    }

    static void startGame() {
        state = State.RUN;
        if(file.exists()){
            file.delete();
            file=new File("HuLuWaGame/record" + fileCount + ".txt");
        }
        creaturesThreadStart();

        /*new Thread(new CheckTimeToChangeOrientationTask(huLuWaList, Orientation.LEFT)).start();
        new Thread(new CheckTimeToChangeOrientationTask(enemyList, Orientation.RIGHT)).start();

        new Thread(new BattleTask()).start();

        new Thread(new DrawMapAndRecordTask()).start();*/
        executor.execute(new CheckTimeToChangeOrientationTask(huLuWaList, Orientation.LEFT));
        executor.execute(new CheckTimeToChangeOrientationTask(enemyList, Orientation.RIGHT));
        executor.execute(new BattleTask());
        executor.execute(new DrawMapAndRecordTask());

    }
    static void cboSetOnAction(ComboBox<String> cbo,List<Creature> creatures,Formation formation){
                cbo.setOnAction(v-> {
                    String formationName = cbo.getValue();
                    changeFormation(formationNameList.indexOf(formationName),creatures,formation);
                    Platform.runLater(()->{
                        drawMap();
                    });
                });
    }

    static void creaturesThreadStart() {
        for (Creature creature : huLuWaList) {
            creature.start();
        }
        for (Creature creature : enemyList) {
            creature.start();
        }
    }

    static List<String> getNameOfRecordFiles() {
        File fileForDirectory = new File(directoryName);
        File[] files = fileForDirectory.listFiles();
        List<String> fileNameList = new ArrayList<>();
        //Pattern p = Pattern.compile("record\\d+\\.txt");
        for (File file1 : files) {
            if (file1.getName().trim().matches("record\\d+\\.txt")) {
                fileNameList.add(file1.getName());
            }
        }
        return fileNameList;
    }

    static void removeDeadCreature(){
        Iterator<Creature> iterator1 = huLuWaList.iterator();
        Iterator<Creature> iterator2 = enemyList.iterator();
        while (iterator1.hasNext()) {
            Creature creature = iterator1.next();
            if (creature.getIsDeath() == true)
                iterator1.remove();
        }
        while (iterator2.hasNext()) {
            Creature creature = iterator2.next();
            if (creature.getIsDeath() == true)
                iterator2.remove();
        }
    }

    static void drawMap(){
        setGameBackground();
        putToMap(huLuWaList);
        putToMap(enemyList);
    }

    static void drawMap(List<Creature> creatures1, List<Creature> creatures2) {
        setGameBackground();
        putToMap(creatures1);
        putToMap(creatures2);
    }

    static void readRecord(String fileName,List<List<Creature>> list1,
                                   List<List<Creature>> list2) {
        File file = new File(fileName);
        try {
            try (
                    Scanner fileInput = new Scanner(file);
            ) {
                int lineNumber=0;
                while(fileInput.hasNext()) {
                    Scanner input = new Scanner(fileInput.nextLine());
                    int list1Size = input.nextInt();
                    list1.add(new ArrayList<>());
                    for (int i = 0; i < list1Size; i++) {
                        String name=input.next();
                        int x = input.nextInt();
                        int y=input.nextInt();
                        list1.get(lineNumber).add(new Creature(x, y, name));
                    }
                    int list2Size = input.nextInt();
                    list2.add(new ArrayList<>());
                    for (int i = 0; i < list2Size; i++) {
                        String name=input.next();
                        int x = input.nextInt();
                        int y=input.nextInt();
                        list2.get(lineNumber).add(new Creature(x, y, name));
                    }
                    lineNumber++;
                }
            }
        }catch (IOException ex){
            System.out.println(ex);
        }
    }

    static void recordProcess() {
       // File file = new File("HuLuWaGame/record" + fileCount++ + ".txt");
        try {
            PrintWriter output = new PrintWriter(new FileWriter(file, true));
            output.print(huLuWaList.size() + " ");
            lock.lock();
            for (Creature creature : huLuWaList) {
                output.print(creature.getCreatureName() + " " + creature.getX() + " " + creature.getY()+" ");
            }
            output.print(enemyList.size() + " ");
            for (Creature creature : enemyList) {
                output.print(creature.getCreatureName() + " " + creature.getX() + " " + creature.getY()+" ");
            }
            output.println();
            output.close();
            lock.unlock();
        }catch (Exception ex){
            System.out.println(ex);
        }
    }
    private static void setGameBackground() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                Cell cell=new Cell();
                gridPane.add(cell,i,j);
            }
        }
    }
    static boolean isTimeToChangeDirection(List<Creature> creatures,
                                                      Orientation orientation) {
        for (Creature creature : creatures) {
            switch (orientation){
                case LEFT:
                    if (creature.getX() >= N - 1) {
                        return true;
                    }
                    break;
                case RIGHT:
                    if (creature.getX() <= 0) {
                        return true;
                    }
                    break;
            }
        }
        return false;
    }
    public static void battle() {
        for (Creature creature1 : huLuWaList) {
            for (Creature creature2 : enemyList) {
                if (creature1.isDeath == false && creature2.isDeath == false && Math.abs(creature1.getX() - creature2.getX()) <= 1 &&
                        Math.abs(creature1.getY() - creature2.getY()) <= 1) {
                    GameMap.battle(creature1, creature2);
                }
            }
        }
    }
    public static void battle(Creature a, Creature b) {
        int power1=a.getCombatEffectiveness();
        int power2=b.getCombatEffectiveness();
        int powerSquare1=power1*power1;
        int powerSquare2=power2*power2;
        int powerSquareSum=powerSquare1+powerSquare2;
        boolean[] booleanArray = new boolean[powerSquareSum];
        Arrays.fill(booleanArray, 0, powerSquare1, true);
        int rand = random.nextInt(powerSquareSum);
        a.setIsDeath(!booleanArray[rand]);
        b.setIsDeath(booleanArray[rand]);
        if(a.getIsDeath()==true){
            System.out.println(a + " is death!");
        }
        if(b.getIsDeath()==true){
            System.out.println(b + " is death!");
        }
    }

    private static void changeFormation(int index,List<Creature> creatures,Formation formation) {
        //cbo.setValue(fileNameList.get(0));
        switch(index){
            case 0:
                formation=new ChangSheFormation();
                break;
            case 1:
                formation=new HeYiFormation();
                break;
            case 2:
                formation=new ChongEFormation();
                break;
            case 3:
                formation=new YanXingFormation();
                break;
            case 4:
                formation=new FangSiFormation();
                break;
            default:
        }
        int startX=creatures.get(0).getX();
        int startY=creatures.get(0).getY();
        Orientation orientation=creatures.get(0).getOrientation();

        formation.lineUp(startX, startY, orientation, creatures);
//        drawMap();
    }
    private static void changeFormation(int startX, int startY, Orientation orientation,
                                        Formation formation,
                                        List<? extends GameMap.Creature> creatures) {
        formation.lineUp(startX, startY, orientation, creatures);
    }

    public static void putToMap(List<? extends Creature> list) {
        for (Creature creature : list) {
            if(!creature.isDeath) {
                ImageView imageView = new ImageView( creature.getCreatureName() + ".png");
                imageView.fitWidthProperty().bind(gridPane.widthProperty().divide(M));
                imageView.fitHeightProperty().bind(gridPane.heightProperty().divide(N));
                gridPane.add(imageView, creature.getX(), creature.getY());
            }
        }
    }


    static class DrawMapAndRecordTask implements Runnable{
        @Override
        public void run() {
            try {
                while (true) {
                    lock.lock();
                    removeDeadCreature();
                    Platform.runLater(() -> {
                                drawMap();
                            }
                    );
                    recordProcess();
                    if(enemyList.isEmpty() || huLuWaList.isEmpty()){
                        state=State.STOP;
                        break;
                    }
                    lock.unlock();
                    Thread.sleep(SLEEPTIME);
                }
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
            /*finally {
                 lock.unlock();
            }*/
        }
    }
    static class BattleTask implements Runnable{
        @Override
        public void run() {
            try {
                while (state!=State.STOP) {
                    if (state == State.RUN) {
                        lock.lock();
                        battle();
                        lock.unlock();
                    }
                    Thread.sleep(SLEEPTIME);
                }
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        }
    }

    static class SetStateLabelTask implements Runnable{
        @Override
        public void run() {
            try {
                while (true) {
                    switch (state) {
                        case BEGIN:
                            stateString = "Initial";
                            break;
                        case RUN:
                            stateString = "Running";
                            break;
                        case SUSPEND:
                            stateString = "Suspend";
                            break;
                        case STOP:
                            stateString = "Stop";
                            break;
                        case REPLAY:
                            stateString = "Replaying";
                        default:
                    }
                    Platform.runLater(() -> {
                        vBox.getChildren().remove(vBox.getChildren().size() - 1);
                        vBox.getChildren().add(new Label("State :" + stateString));
                    });
                    Thread.sleep(SLEEPTIME);
                }

            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        }
    }

    static class ChangeFormationCboTask implements Runnable{
        private ComboBox<String> cbo;
        boolean targetIsHuluwas;

        public ChangeFormationCboTask(ComboBox<String> cbo, boolean targetIsHuluwas){
            this.cbo=cbo;
            this.targetIsHuluwas=targetIsHuluwas;
        }
        @Override
        public void run() {
            try{
                while(true){
//                    targetIsHuluwas ? cboSetOnAction(cbo, huLuWaList, huLuWaFormation) : cboSetOnAction(cbo, enemyList,enermyFormation);
                    if(targetIsHuluwas) {
                        cboSetOnAction(cbo, huLuWaList, huLuWaFormation);
                    }
                    else
                        cboSetOnAction(cbo, enemyList,enermyFormation);
                    Thread.sleep(SLEEPTIME);
                }
            }catch (InterruptedException ex){
                System.out.println(ex);
            }
        }
    }
    static class Creature extends Thread{
        private boolean needToClear=false;
        private int combatEffectiveness=0;
        private boolean isDeath=false;
        private int x,y;
        private Orientation orientation;
        private String creatureName;

        public Creature(){
            this(0, 0, "",Orientation.LEFT,1);
        }

        public Creature(int x, int y, String name) {
            this(x, y, name, Orientation.LEFT,1);
        }
        public Creature(int x, int y,String name,Orientation orientation,
                        int combatEffectiveness) {
            this.x=x;
            this.y=y;
            //this.name=name;
            creatureName=name;
            this.orientation=orientation;
            this.combatEffectiveness=combatEffectiveness;
        }

        public void reverseOrientation() {
            switch (orientation) {
                case LEFT:
                    orientation=Orientation.RIGHT;
                    break;
                case RIGHT:
                    orientation=Orientation.LEFT;
                    break;
                default:

            }
        }
        public void run() {
                    try {
                        while (state!= GameMap.State.STOP) {
                            if(state== GameMap.State.RUN) {
                                switch (orientation) {
                                    case LEFT:
                                        if (x < N - 1) {
                                            x += 1;

                                        } else {
                                            orientation = Orientation.RIGHT;
                                        }
                                        break;
                                    case RIGHT:
                                        if (x > 0) {
                                            x -= 1;

                                        } else {
                                            orientation = Orientation.LEFT;
                                        }
                                        break;
                                }
                            }
                            Thread.sleep(SLEEPTIME);
                        }
                    }catch (InterruptedException ex){
                        System.out.println(ex);
                    }
            }

        public boolean getIsDeath() {
            return isDeath;
        }
        public void setIsDeath(boolean isDeath) {
            this.isDeath=isDeath;
        }

        public int getCombatEffectiveness() {
            return combatEffectiveness;
        }
        public void setCombatEffectiveness(int combatEffectiveness) {
            this.combatEffectiveness = combatEffectiveness;
        }

        public Orientation getOrientation() {
            return orientation;
        }

        public boolean getNeedToClear() {
            return needToClear;
        }

        public void setNeedToClear(boolean b){
            needToClear=b;
        }
        public void setOrientation(Orientation orientation) {
            this.orientation = orientation;
        }

        public String getCreatureName() {
            return creatureName;
        }

        public void moveTo(int endX, int endY){
            setPos(endX, endY);
        }

        public void swap(Creature creature){
            int tempX=getX();
            int tempY = getY();
            setPos(creature.getX(), creature.getY());
            creature.setPos(tempX, tempY);
        };

        public String toString(){
            return creatureName;
        }

        public int getX() {
            return x;
        }
        public int getY() {
            return y;
        }
        public void setX(int x) {
            this.x = x;
        }
        public void setY(int y) {
            this.y = y;
        }
        public void setPos(int x, int y) {
            setX(x);
            setY(y);
        }
    }
    static class HuLuWas extends Creature {
        public static final List<String> nameList= Arrays.asList("DaWa","ErWa","SanWa","SiWa","WuWa","LiuWa","QiWa");
        private List<Creature> huLuWas = new LinkedList<>();
        public HuLuWas(){
            for(int i = 0; i < nameList.size(); i++) {
                huLuWas.add(new Creature(0, 0, nameList.get(i),Orientation.LEFT,7));
            }
        }

        public List<Creature> getHuLuWas() {
            return huLuWas;
        }
    }
    static class Goblin extends Creature {

        private static int count=1;
        private final int id=count++;
        public Goblin() {
            this(0, 0);
        }
        public Goblin(int x,int y) {
            super(x, y, "Goblin",Orientation.RIGHT,3);
        }

        public Goblin(int x, int y, Orientation orientation) {
            super(x, y, "Goblin", orientation, 4);
        }

        public String toString() {
            return getCreatureName()+id;
        }
    }
    static class GrandPa extends Creature {

        private static GrandPa uniqueGrandPa;
        private GrandPa(){
            this(0, 0);
        }
        private GrandPa(int x,int y)
        {
            super(x, y, "GrandPa",Orientation.LEFT,2);
        }
        private GrandPa(int x,int y,Orientation orientation){
            super(x, y, "GrandPa",orientation,1);
        }


        public static GrandPa getUniqueGrandPa(int x,int y){
            return getUniqueGrandPa(x, y, Orientation.LEFT);
        }

        public static GrandPa getUniqueGrandPa(int x, int y, Orientation orientation) {
            if(uniqueGrandPa==null)
                uniqueGrandPa=new GrandPa(x,y,orientation);
            return uniqueGrandPa;
        }
        public static GrandPa getUniqueGrandPa(){
            return getUniqueGrandPa(0, 0);
        }
    }
    static class Scorpion extends Creature{
        private static Scorpion uniqueScorpion;
        private Scorpion()
        {
            this(0, 0,Orientation.RIGHT);
        }

        private Scorpion(int x, int y) {
            this(x, y, Orientation.RIGHT);
        }
        private Scorpion(int x,int y,Orientation orientation)
        {
            super(x, y, "Scorpion",orientation,10);
        }

        public static Scorpion getUniqueScorpion(){
            return getUniqueScorpion(0, 0);
        }
        public static Scorpion getUniqueScorpion(int x,int y){
            return getUniqueScorpion(x, y, Orientation.RIGHT);
        }
        public static Scorpion getUniqueScorpion(int x,int y,Orientation orientation){
            if(uniqueScorpion==null)
                uniqueScorpion=new Scorpion(x,y,orientation);
            return uniqueScorpion;
        }
    }
    static class Snake extends Creature {
        private static Snake uniqueSnake;
        private Snake(){
            this(0, 0);
        }
        private Snake(int x,int y)
        {
            super(x, y, "Snake",Orientation.RIGHT,10);
        }
        private Snake(int x, int y, Orientation orientation)
        {
            super(x, y, "Snake",orientation,8);
        }

        public static Snake getUniqueSnake(int x,int y){
            if(uniqueSnake==null)
                uniqueSnake=new Snake(x,y);
            return uniqueSnake;
        }
        public static Snake getUniqueSnake(){
            return getUniqueSnake(0, 0);
        }
        public static Snake getUniqueSnake(int x,int y,Orientation orientation)
        {
            return getUniqueSnake(x,y,orientation);
        }
    }

    static class Cell extends Pane {
        public Cell() {
            setStyle("-fx-border-color: green;");
            setPrefSize(1000, 1000);
            Rectangle rectangle = new Rectangle(0,0,0,0);
            rectangle.setFill(Color.GREEN);
            rectangle.setStroke(Color.BLACK);
            rectangle.widthProperty().bind(widthProperty());
            rectangle.heightProperty().bind(heightProperty());
            getChildren().add(rectangle);
        }
    }
}
