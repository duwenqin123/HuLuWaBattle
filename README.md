# 主要设计思路
### Creature类
游戏所有生物继承自Creature类。包括HuLuWas（葫芦娃群体）,GrandPa（老爷爷）,Scorpion（蝎子精）,Snake（蛇精）,Goblin（小妖怪）。其中，考虑到葫芦娃总是作为群体出现，名字又不同，每个单独实现为一个类太不方便，不如封装在一起。老爷爷、蝎子精和蛇精因为只有一个，故实现为单例模式。本来Creature是考虑实现为抽象类，后来发现Creature类的所有方法的实现都可以共用于所有子类，且不同子类可以通过名字来区分，同时也方便创建七个葫芦娃，故最后还是实现成了具体类。

### Formation接口
实现了五种阵型，分别为ChangSheFormation,FangSiFormation,YanXingFormation,HeYiFormation,ChongEFormation。由于Formation只有一个方法（即列阵），且在不同阵型中实现不同，故实现为接口，由于一个类可以实现多个接口，而只能继承单个抽象类，故接口更为灵活每种具体阵型根据阵型的特点实现，实现时需要有起始位置（即队首生物的位置），方向（处于左边准备向右还是相反），加上存有准备列阵的生物的列表。

### 图形界面
由于Oracle公司不再更新Swing，而新出的JavaFX书写更为便捷，故采用JavaFX实现。其中，生物的移动按照要求用多线程实现，界面背景用绿色的矩形填充在GridPane里，游戏结束后输出的结束字符串则采用正常的动画实现。另外，游戏界面上方有帮助信息,以及显示游戏当前状态，及用来改变阵型的组合框。当按下键L时，还会跳出带有当前目录下的形式为“record*.txt"的文件列表组合框供选择回放，整体界面如下所示。 Alt text

### 游戏状态
游戏利用enum设置多种状态,初始为BEGIN状态,并显示出游戏界面和两大对战群体的对峙状态。当按下SPACE键后，进入状态

    enum State{
        RUN,STOP,SUSPEND,BEGIN,REPLAY
    }
RUN,启动所有生物进程（不停地改变坐标），同时随时检测是否相遇，是否到达边界，并不断刷新界面。RUN状态下，按下SPACE可暂停游戏。REPLAY则用于回放时的状态。Run状态时，可以通过上面组合框随时改变阵型，或者上方向键来改变，用来增加游戏趣味。当有一方生物全死绝时，进入STOP状态，同时根据胜负显示闪烁的“You Won!”或“Game Over”。

### 记录文件
当游戏进入RUN状态时，开始记录游戏信息。采用一个线程来记录，每次记录一行数据，该行数据首先是葫芦娃阵营的现存人数，及每个人的名字和当前坐标。然后是敌方阵营的相似信息。回放状态时，每次读取一行数据并显示，每隔500ms左右刷新一次界面。

### 战斗处理
每个生物带有一个战斗力指数，当两个敌对的生物处于x,y坐标都小于等于1的状态时，战斗开始。处理的算法是，以两者战斗力的平方的比值设定双方的生存概率百分比，其和为1，保证其中一个生物死亡。死亡时，更新该生物的存活状态，并从其所在列表中删除。

### 存在的问题
游戏会有一定的卡顿现象，且运行越久越容易卡顿
初始状态下，按组合框选择阵型后，无法进入状态，原因是界面无法再识别键盘输入。很奇怪
