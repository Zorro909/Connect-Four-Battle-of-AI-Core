package de.Zorro.VierGewinnt.Main;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import JavaUtils.ClassLoader.UtilClass;
import JavaUtils.ClassLoader.UtilClassBuilder;
import de.Zorro.VierGewinnt.API.AI;
import de.Zorro.VierGewinnt.Board.Board;
import jline.console.ConsoleReader;

public class VierGewinnt {

    private static boolean settable = false;
    private static Board b;
    private static AI ai1;
    private static AI ai2;
    private static int turn = 0;
    private static String aiName1;
    private static String aiName2;
    private static ConsoleReader cr;

    private static int lastWinner = -1;
    private static boolean log = false;
    private static boolean statisticsMode = false;
    private static int playingGames = 1;
    private static int playedGames = 0;

    private static int player1Wins = 0, player2Wins = 0;
    private static ArrayList<Double> times = new ArrayList<Double>();
    private static ArrayList<Double> turnCounts = new ArrayList<Double>();

    public static void main(String[] args) throws IOException {
        cr = new ConsoleReader();
        cr.println("Starting Connect-Four Engine...");
        if (args.length < 2) {
            cr.clearScreen();
            cr.println("Not enough arguments. Usage: java -jar VierGewinnt.jar Path/To/Java/AI.java Path/To/Python/AI.py [-a|-log] [-export {source} {target}]");
            cr.println(" -a [amount]: How many Games should be played through. Similiar to statistics.py in original CoRe");
            cr.println(" -log: Logs every Game to a File, which can then be exported to a html.");
            cr.println(" -export [Source] [Target]: Exports a Logged Game (Source) to a html (Target).");
            cr.println("PS: File Paths currently cannot have Spaces in them!");
            return;
        } else if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("-export")) {
                File s = new File(args[1]);
                File t = new File(args[2]);

                if (!s.exists()) {
                    cr.println("File " + s.getAbsolutePath() + " cannot be found!");
                    return;
                }
                BufferedReader br = new BufferedReader(new FileReader(s));
                String v = br.readLine();
                aiName1 = v.split(" ")[0];
                aiName2 = v.split(" ")[2];
                String ints = br.readLine();
                createBoard();
                int player = 0;
                String g = "";
                for(int i = 0;i<ints.length();i++){
                    settable = true;
                    g = b.setNew(player, Integer.valueOf(String.valueOf((char)ints.codePointAt(i))));
                    player = 1 - player;
                }
                finish(g, t);
                return;
            }
            for (int i = 2; i < args.length; i++) {
                if (args[i].equalsIgnoreCase("-a")) {
                    statisticsMode = true;
                    playingGames = Integer.valueOf(args[i + 1]);
                    i++;
                    cr.println("Playing " + playingGames + " Games in Statistics Mode.");
                } else if (args[i].equalsIgnoreCase("-log")) {
                    log = true;
                    cr.println("Enabled Logging.");
                }
            }
        }
        // overrideDefaultSystemOut();
        if (statisticsMode) {
            aiName1 = new File(args[0]).getName().split("\\.")[0];
            aiName2 = new File(args[1]).getName().split("\\.")[0];
            while (playingGames > playedGames) {
                printStatistics();
                long time = System.currentTimeMillis();
                playGame(args[0], args[1], false);
                ai1.shutdown();
                ai2.shutdown();
                time = System.currentTimeMillis() - time;
                times.add((double) (time / 1000));
                turnCounts.add((double) turn);
                turn = 0;
                if (lastWinner == 1) {
                    player1Wins++;
                } else if (lastWinner == 2) {
                    player2Wins++;
                }

                playedGames++;
            }
            printStatistics();
        } else {
            playGame(args[0], args[1], true);
        }
    }

    private static void printStatistics() throws IOException {
        cr.clearScreen();
        cr.print(aiName1 + " vs " + aiName2 + " | Played " + playedGames + " Games of "
                        + playingGames);
        cr.println();
        cr.print("Player 1 Wins: " + player1Wins + " | Player 2 Wins: " + player2Wins + " | Ties: "
                        + (player1Wins + player2Wins - playedGames));
        cr.println();
        double avgTime = 0;
        double avgturnCount = 0;
        if (times.size() > 0) {
            for (double time : times) {
                avgTime += time;
            }
            avgTime = avgTime / times.size();
            for (double turnc : turnCounts) {
                avgturnCount += turnc;
            }
        }
        avgturnCount = avgturnCount / turnCounts.size();
        cr.print("Average Roundtime: " + avgTime + " | Average Turncount: " + avgturnCount);
        cr.println();
        cr.flush();
    }

    private static void playGame(String ai1, String ai2, boolean autoFinish) {
        createBoard();

        loadAIs(ai1, ai2);

        turn = 0;
        int c = 0;
        turn++;
        while (makeTurn(c, autoFinish)) {
            try {
                cr.println("Player " + (c + 1) + " finished Turn " + turn);
                cr.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (c == 0) c = 1;
            else if (c == 1) c = 0;
            turn++;
        }
    }

    private static boolean makeTurn(int c, boolean autoFinish) {
        if (c == 0) {
            int x = -1;
            try {
                x = ai1.turn(b, "X");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Security.shutdownThreads();
            settable = true;
            String r = b.setNew(c, x);
            if (r.equalsIgnoreCase("e")) {
                try {
                    cr.println("Error occured while spawning new ball for player " + c
                                    + " in round " + turn);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                try {
                    lastWinner = 0;
                    if (log) log(b);
                    if (autoFinish) finish("l");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (autoFinish) System.exit(0);
            }
            if (!r.equalsIgnoreCase("g")) {
                try {
                    lastWinner = (r.equalsIgnoreCase("X") ? 1 : (r.equalsIgnoreCase("O") ? 2 : 0));
                    if (log) log(b);
                    if (autoFinish) finish(r);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return false;
            }
            return true;
        } else if (c == 1) {
            int x = -1;
            try {
                x = ai1.turn(b, "X");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Security.shutdownThreads();
            settable = true;
            String r = b.setNew(c, x);
            if (r.equalsIgnoreCase("e")) {
                try {
                    cr.println("Error occured while spawning new ball for player " + c
                                    + " in round " + turn);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                try {
                    lastWinner = 0;
                    if (log) log(b);
                    if (autoFinish) finish("l");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (autoFinish) System.exit(0);
            }
            if (!r.equalsIgnoreCase("g")) {
                try {
                    lastWinner = (r.equalsIgnoreCase("X") ? 1 : 2);
                    if (log) log(b);
                    if (autoFinish) finish(r);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return false;
            }
            return true;
        }
        return false;
    }

    private static void log(Board b2) {
        File f = new File("logs" + File.separator +
                        new Date().getTime()
                                        + ".save");
        try {
            f.getParentFile().mkdirs();
            f.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(f));
            pw.println(aiName1 + " vs " + aiName2);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (Integer i : b2.getPreviousTurns()) {
            pw.print(i);
        }
        pw.flush();
        pw.close();
    }
    
    private static void finish(String r) throws IOException {
        File f = new File("log.html");
        finish(r,f);
    }

    private static void finish(String r, File f) throws IOException {
        if (ai1 != null) {
            ai1.shutdown();
            ai2.shutdown();
        }
        if (log) { return; }
        cr.println("Game Ended");
        Board bN = new Board(b.getWidth(), b.getHeight(), false);
        if (f.exists()) f.delete();
        f.createNewFile();
        PrintWriter pw = new PrintWriter(new FileWriter(f));
        pw.print("<!DOCTYPE html><title>" + aiName1 + " / " + aiName2
                        + "</title><style>body { font-family: monospace; background-color: rgb(50,50,50); color: rgb(150,150,150); }h1,h2,h3,.a { font-size: 32px; }a { color: rgb(200,200,200); }button { background-color: rgb(50,50,50); color: rgb(150,150,150); font-weight: bold;font-size: 24px; }table {background: rgb(100,100,100);}table, th, td {border: 1px solid black;border-collapse: collapse;}td, .X, .O { width: 64px; height: 64px; }.X {background: linear-gradient(to bottom right, grey, white);border-radius: 50%; }.O {background: linear-gradient(to bottom right, black, grey);border-radius: 50%; }</style><h1>Vier Gewinnt</h1><h2>"
                        + aiName1 + " / " + aiName2 + "</h2><h3>" + b.getPreviousTurns().size()
                        + " Turns</h3><hr/><button onclick=\"var styles = 'table{margin-bottom:100vh;}';var tag = document.createElement('style');tag.appendChild(document.createTextNode(styles));document.head.appendChild(tag);         location.href = '#1';\">Simulate</button>        <button onclick=\"var turn = prompt('Turn [1-64]', '1'); location.href = '#' + turn;\">Goto</button><hr/>");
        int player = 1;
        LinkedList<Integer> t = b.getPreviousTurns();
        for (int i = 0; i < t.size(); i++) {

            pw.print("<div class='a'>");
            pw.print("<p>Player " + player + " (" + (player == 1 ? aiName1 : aiName2)
                            + ") moved to " + (t.get(i) + 1) + "</p>");
            pw.print("<a id='" + (i + 1) + "' href='#" + (i) + "'>back</a> <a href='#" + (i + 2)
                            + "'>next</a>");
            pw.print("<table>");
            bN.setNew(player - 1, t.get(i));
            for (int y = b.getHeight() - 1; y >= 0; y--) {
                pw.print("<tr>");
                for (int x = 0; x < b.getWidth(); x++) {
                    pw.print("<td>");
                    if (bN.getCell(x, y).equalsIgnoreCase("X")) {
                        pw.print("<div class='X'></div>");
                    } else if (bN.getCell(x, y).equalsIgnoreCase("O")) {
                        pw.print("<div class='O'></div>");
                    }
                    pw.print("</td>");
                }
                pw.print("</tr>");
            }
            pw.print("</table></div>");
            if (player == 1) player = 2;
            else if (player == 2) player = 1;
        }
        pw.print("<div class='a'>");
        if (r.equalsIgnoreCase("l")) {
            pw.print("<p>It's a Tie</p>");
        } else if (r.equalsIgnoreCase("X")) {
            pw.print("<p>Player 1 (" + aiName1 + ") won!</p>");
        } else if (r.equalsIgnoreCase("O")) {
            pw.print("<p>Player 2 (" + aiName2 + ") won!</p>");
        }
        pw.print("</div></body></html>");
        pw.flush();
        pw.close();
        Desktop.getDesktop().browse(f.toURI());
    }

    public static void createBoard() {
        b = new Board(8, 8, true);
    }

    public static boolean isNewBlockSettable() {
        return settable;
    }

    public static void disableSettableBlock() {
        settable = false;
    }

    private static void loadAIs(String string, String string2) {
        File ai1 = new File(string);
        File ai2 = new File(string2);

        VierGewinnt.ai1 = loadAI(ai1);
        aiName1 = ai1.getName().split("\\.")[0];
        VierGewinnt.ai2 = loadAI(ai2);
        aiName2 = ai2.getName().split("\\.")[0];
    }

    private static AI loadAI(File ai) {
        if (!ai.exists()) {
            try {
                cr.println("ERROR: AI File " + ai.getName() + " does not exist!");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.exit(0);
        }
        int index = ai.getName().indexOf(".");
        int i = 0;
        while (index >= 0 && i >= 0) {
            i = ai.getName().indexOf(".", index + 1);
            if (i != -1) {
                index = i;
            }
        }
        if (index == -1) {
            try {
                cr.println("ERROR: AI File " + ai.getName()
                                + " has no file ending! Can't find AI type! (Java,JS,Python)");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.exit(0);
        }
        switch (ai.getName().substring(index + 1)) {
        case "js":
            return loadJsAI(ai);
        case "py":
            return loadPyAI(ai);
        case "java":
            return loadJavaSrcAI(ai);
        case "class":
            return loadJavaClassAI(ai);
        case "jar":
            return loadJavaJarAI(ai);
        default:

            try {
                cr.println("ERROR: Could not find type of AI file with file ending: "
                                + ai.getName().substring(index));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.exit(0);
            return null;
        }
    }

    private static AI loadPyAI(File ai) {
        Process p = null;
        try {
            p = new ProcessBuilder("python",
                            "." + File.separator + "python" + File.separator + "handler.py")
                                            .redirectErrorStream(true).start();
        } catch (Exception e2) {
            e2.printStackTrace();
            try {
                cr.println(
                                "ERROR: Is Python installed on your system? And accessible with python?");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.exit(0);
        }
        final PrintWriter pw = new PrintWriter(new OutputStreamWriter(p.getOutputStream()));
        final BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        pw.println(ai.getAbsolutePath());
        pw.println(b.getWidth());
        pw.println(b.getHeight());
        pw.flush();
        final Process p2 = p;
        return new AI() {
            Process p = p2;

            @Override
            public void shutdown() {
                p.destroy();
            }

            @Override
            public int turn(Board b, String symbol) {
                pw.println(turn);
                for (int x = 0; x < b.getWidth(); x++) {
                    for (int y = 0; y < b.getHeight(); y++) {
                        pw.println(b.getCell(x, y));

                    }
                }
                pw.println(symbol);
                pw.flush();
                String line = "";
                try {

                    while ((line = br.readLine()) != null) {
                        if (!line.startsWith("pos_x_result_handler=")) {
                            cr.println(line);
                        } else {
                            int i = Integer.valueOf(line.split("=")[1]);
                            return i;
                        }
                    }
                } catch (NumberFormatException | IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return 0;
            }

        };
    }

    private static AI loadJsAI(File ai) {
        final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            engine.eval(new FileReader(ai));
            return new AI() {

                @Override
                public int turn(Board b, String symbol) {
                    Invocable i = (Invocable) engine;
                    try {
                        Object o = i.invokeFunction("turn", b, symbol);
                        if (o instanceof Integer) {
                            return ((Integer) o).intValue();
                        } else {
                            cr.println("ERROR: No Integer was returned by JS Script: "
                                            + o.toString());
                            return 0;
                        }
                    } catch (NoSuchMethodException | ScriptException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return 0;
                }

                @Override
                public void shutdown() {

                }

            };
        } catch (FileNotFoundException | ScriptException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            try {
                cr.println(
                                "ERROR: While initiating JS Script (JS syntax right? Function turn(board,symbol)?)");
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return null;
    }

    private static AI loadJavaSrcAI(File ai) {
        File c = null;
        try {
            c = new File(ai.getPath() + File.separator + ai.getName().replace(".java", ".class"));
            if (c.exists()) c.delete();
            if (new File("libs").exists()) {
                new ProcessBuilder("javac", "-cp",
                                "." + File.separator + "VierGewinnt.jar;." + File.separator + "libs"
                                                + File.separator + "*",
                                ai.getAbsolutePath()).redirectOutput(Redirect.INHERIT)
                                                .redirectErrorStream(true).start().waitFor();
            } else {
                new ProcessBuilder("javac", "-cp", "." + File.separator + "VierGewinnt.jar",
                                ai.getAbsolutePath()).redirectOutput(Redirect.INHERIT)
                                                .redirectErrorStream(true).start().waitFor();
            }
            c = new File(ai.getParentFile().getAbsolutePath() + File.separator
                            + ai.getName().replace(".java", ".class"));
            if (!c.exists()) {
                cr.println("ERROR: Compiling your Source Code File didn't work! ("
                                + ai.getName() + ")");
                System.exit(0);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return loadJavaClassAI(c);
    }

    private static AI loadJavaJarAI(File ai) {
        try {
            UtilClassBuilder<AI> cb = new UtilClassBuilder<>(ai.getParentFile());
            UtilClass<AI> a = cb.newUtilJarClass(ai, ai.getName().split("\\.")[0]);
            return a.initialise();
        } catch (Exception e) {
            try {
                cr.println("ERROR: AI " + ai.getName()
                                + " fails to load! (Wrong Name Scheme? Example: MainClassName.jar)");
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            System.exit(0);
        }
        return null;
    }

    private static AI loadJavaClassAI(File ai) {
        try {
            UtilClassBuilder<AI> cb = new UtilClassBuilder<>(ai.getParentFile());
            UtilClass<AI> a = cb.newUtilClass(ai.getName().split("\\.")[0]);
            return a.initialise();
        } catch (Exception e) {
            try {
                cr.println("ERROR: AI " + ai.getName() + " fails to load! (Corrupted File?)");
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            System.exit(0);
        }
        return null;
    }
}
