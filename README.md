# Laboratorium 14

Wykład [GUI AWT+Swing, około strony 35](http://pszwed.kis.agh.edu.pl/wyklady_java/w11-java-gui.pdf#page=35) zawiera przykład animacji pojedynczej kulki obijającej się od ścian. Zrealizujemy animację wielu kulek...

Projekt będzie składał się z dwóch plików

- `BouncingBallsFrame`
- `BouncingBallsPanel`

## BouncingBallsFrame

Kod jest praktycznie napisany. Proszę go przeanalizować.

Okno zawiera dwa panele: górny z przyciskami oraz dolny pokazujący animację.

- Co to jest `BorderLayout`
- Jak rozkładają się komponenty
- Co to jest `ActionListener`
- Jakie metody będą wołane po naciśnięciu przycisku?

```java
public class BouncingBallsFrame extends JFrame {
 
    BouncingBallsPanel bbPanel = new BouncingBallsPanel();
 
    BouncingBallsFrame() {
        super("Bouncing balls");
        buildGui();
    }
 
    void buildGui() {
        JPanel root = new JPanel();
        root.setLayout(new BorderLayout());
        JPanel northPanel = new JPanel();
 
        JButton start = new JButton("Start");
        start.addActionListener(p->bbPanel.onStart());
        northPanel.add(start);
 
        JButton stop = new JButton("Stop");
        stop.addActionListener(p->bbPanel.onStop());
        stop.setEnabled(false);
        northPanel.add(stop);
 
        start.addActionListener(p->{
            start.setEnabled(false);
            stop.setEnabled(true);
        });
        stop.addActionListener(p->{
            stop.setEnabled(false);
            start.setEnabled(true);
        });
 
        JButton plus = new JButton("Plus");
        plus.addActionListener(p->bbPanel.onPlus());
        northPanel.add(plus);
 
        JButton minus = new JButton("Minus");
        minus.addActionListener(p->bbPanel.onMinus());
        northPanel.add(minus);
        root.add(northPanel, BorderLayout.NORTH);
 
        root.add(bbPanel, BorderLayout.CENTER);
        setContentPane(root);
    }

    public static void main(String[] args) {
        BouncingBallsFrame frame = new BouncingBallsFrame();
 
        frame.setSize(700, 700);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setVisible(true);
 
    }
}
```

## BouncingBallsPanel

Panel przechowuje informacje o położeniu i prędkości (wektorach prędkości) kulek. Na początku panel powinien wylosować położenia i prędkości kilkunastu kulek i dodać je do listy.

Panel uruchamia wątek realizujący animację.

- Na początku (dopóki nie zostanie wywołana metoda `onStart()` wątek powinien być zawieszony. Na końcu [wykładu o wątkach](http://pszwed.kis.agh.edu.pl/wyklady_java/w10-java-threads.pdf) jest przykład, jak zawieszać wątki.
- Po wywołaniu metody `onStart()` wątek powinien być uaktywniony
- Wywołanie metody `onPlus()` powinno dodać kulkę z losowo wybranymi parametrami
- Wywołanie metody `onMinus()` powinno usunąć jedną z kulek

Szkielet kodu

```java
public class BouncingBallsPanel extends JPanel {
 
    static class Ball {
        int x;
        int y;
        double vx;
        double vy;
        Color color;
    }
 
    List<Ball> balls = new ArrayList<>();
 
    class AnimationThread extends Thread{
        public void run() {
            for (;;) {
                // przesuń kulki
                // wykonaj odbicia od ściany
                // wywołaj repaint
                // uśpij
            }
        }
    }
 
    BouncingBallsPanel() {
        setBorder(BorderFactory.createStrokeBorder(new BasicStroke(3.0f)));
    }
 
    void onStart() {
        System.out.println("Start or resume animation thread");
    }
 
    void onStop() {
        System.out.println("Suspend animation thread");
    }
 
    void onPlus() {
        System.out.println("Add a ball");
    }
 
    void onMinus() {
        System.out.println("Remove a ball");
    }
}
```

### Podwójne buforowanie

Spróbuj zaimplementować opisaną na wykładzie technikę podwójnego buforowania i porównaj płynność animacji...

### Zderzenia kulek

Zaimplementuj kod, który realizuje zderzenia kulek. Zapewne będą to zderzenia sprężyste z zachowniem energii i pędu. Na pewno znajdziesz wzory w sieci...
